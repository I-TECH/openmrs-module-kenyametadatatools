/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyametadatatools.web.controller.mflsync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyametadatatools.mflsync.MflSyncOptions;
import org.openmrs.module.kenyametadatatools.mflsync.BaseMflSyncTask;
import org.openmrs.module.kenyametadatatools.mflsync.MflSyncFromRemoteSpreadsheetTask;
import org.openmrs.module.kenyametadatatools.task.TaskEngine;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Synchronization page controller
 */
@Controller
public class SynchronizeController {
	
	protected final Log log = LogFactory.getLog(SynchronizeController.class);

	/**
	 * Handles requests to show the synchronize form
	 * @param model the model
	 * @return the view name
	 */
	@RequestMapping(value = "/module/kenyametadatatools/mflsync/synchronize", method = RequestMethod.GET)
	public String showForm(@ModelAttribute("options") MflSyncOptions options, ModelMap model) {

		model.put("locationAttributeTypes", Context.getLocationService().getAllLocationAttributeTypes());

		return "/module/kenyametadatatools/mflsync/synchronize";
	}

	/**
	 * Handles requests to start a synchronization task
	 * @param options the synchronization options
	 * @param session the http session
	 * @return the view name
	 */
	@RequestMapping(value = "/module/kenyametadatatools/mflsync/synchronize", method = RequestMethod.POST)
	public String submit(@ModelAttribute("options") MflSyncOptions options, ModelMap model, HttpSession session) {

		try {
			if (!Context.hasPrivilege(PrivilegeConstants.MANAGE_LOCATIONS)) {
				session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Insufficient privileges");
			}
			else {
				URL url = new URL(options.getSpreadsheetUrl());
				LocationAttributeType attrType = options.getAttributeType();

				BaseMflSyncTask task = new MflSyncFromRemoteSpreadsheetTask(attrType, url);

				if (TaskEngine.start(task)) {
					return "redirect:synchronize.form";
				} else {
					session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Task already running");
				}
			}
		}
		catch (MalformedURLException ex) {
			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Invalid spreadsheet URL");
			log.error(ex);
		}

		return showForm(options, model);
	}

	/**
	 * Handles requests to get the current task status
	 * @param sinceMessageId the last message id fetched
	 * @param response the http response
	 * @throws Exception if an error occurs
	 */
	@RequestMapping(value = "/module/kenyametadatatools/mflsync/status", method = RequestMethod.GET)
	@ResponseBody
	public void status(@RequestParam(value = "sinceMessageId", required = false) Integer sinceMessageId, HttpServletResponse response) throws Exception {
		Map<String, Object> status = new HashMap<String, Object>();

		status.put("busy", TaskEngine.isBusy());
		status.put("messages", TaskEngine.getMessagesSince(sinceMessageId));

		ObjectMapper mapper = new ObjectMapper();
		response.setContentType("application/json");
		mapper.writeValue(response.getWriter(), status);
	}
}