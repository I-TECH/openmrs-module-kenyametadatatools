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

package org.openmrs.module.kenyamflsync;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.kenyamflsync.task.TaskEngine;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class KenyaMflSyncActivator implements ModuleActivator, DaemonTokenAware {
	
	protected static Log log = LogFactory.getLog(KenyaMflSyncActivator.class);

	/**
	 * @see DaemonTokenAware#setDaemonToken(org.openmrs.module.DaemonToken)
	 */
	@Override
	public void setDaemonToken(DaemonToken token) {
		TaskEngine.setDaemonToken(token);
	}
		
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	@Override
	public void willRefreshContext() {
		log.info("Refreshing Kenya MFL Synchronization Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	@Override
	public void contextRefreshed() {
		log.info("Kenya MFL Synchronization Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	@Override
	public void willStart() {
		log.info("Starting Kenya MFL Synchronization Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	@Override
	public void started() {
		log.info("Kenya MFL Synchronization Module started");
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	@Override
	public void willStop() {
		log.info("Stopping Kenya MFL Synchronization Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	@Override
	public void stopped() {
		log.info("Kenya MFL Synchronization Module stopped");
	}
}