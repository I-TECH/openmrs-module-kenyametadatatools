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

package org.openmrs.module.kenyamflsync.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyamflsync.api.KenyaMflSyncService;
import org.openmrs.module.kenyamflsync.task.BaseTask;

/**
 * Default implementation of {@link org.openmrs.module.kenyamflsync.api.KenyaMflSyncService}
 */
public class KenyaMflSyncServiceImpl extends BaseOpenmrsService implements KenyaMflSyncService {

	protected final Log log = LogFactory.getLog(KenyaMflSyncServiceImpl.class);

	/**
	 * @see KenyaMflSyncService#executeTask(org.openmrs.module.kenyamflsync.task.BaseTask)
	 */
	@Override
	public void executeTask(BaseTask task) {
		task.execute();
	}
}