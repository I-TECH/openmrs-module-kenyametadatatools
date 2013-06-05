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

package org.openmrs.module.kenyamflsync.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyamflsync.KenyaMflSyncConstants;
import org.openmrs.module.kenyamflsync.api.KenyaMflSyncService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Synchronization page controller
 */
@Controller
@RequestMapping("/module/kenyamflsync/synchronize")
public class SynchronizeController {
	
	protected final Log log = LogFactory.getLog(SynchronizeController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(ModelMap model) {
		model.addAttribute("spreadsheetUrl", KenyaMflSyncConstants.DEFAULT_SPREADSHEET_URL);
		return "/module/kenyamflsync/synchronize";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String startTask(ModelMap model, @RequestParam("spreadsheetUrl") String spreadsheetUrl) {

		try {
			URL url = new URL(spreadsheetUrl);

			Context.getService(KenyaMflSyncService.class).synchronizeWithSpreadSheet(url);
			return "redirect:synchronize.form";
		}
		catch (MalformedURLException e) {
			model.put("spreasheetUrlError", "Malformed URL");
		}

		return "/module/kenyamflsync/synchronize";
	}
}