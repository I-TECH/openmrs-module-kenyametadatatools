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

package org.openmrs.module.kenyametadatatools.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyametadatatools.api.MetadataToolsService;
import org.openmrs.module.kenyametadatatools.task.BaseTask;

/**
 * Default implementation of {@link org.openmrs.module.kenyametadatatools.api.MetadataToolsService}
 */
public class MetadataToolsServiceImpl extends BaseOpenmrsService implements MetadataToolsService {

	protected final Log log = LogFactory.getLog(MetadataToolsServiceImpl.class);

	/**
	 * @see org.openmrs.module.kenyametadatatools.api.MetadataToolsService#executeTask(org.openmrs.module.kenyametadatatools.task.BaseTask)
	 */
	@Override
	public void executeTask(BaseTask task) {
		task.execute();
	}
}