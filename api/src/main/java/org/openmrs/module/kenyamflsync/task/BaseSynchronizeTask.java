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

package org.openmrs.module.kenyamflsync.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.scheduler.Task;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.TimerTask;

/**
 * Base class for synchronize tasks
 */
public abstract class BaseSynchronizeTask extends AbstractTask {

	protected  Log log = LogFactory.getLog(getClass());

	protected StringBuilder output = new StringBuilder();

	protected int createdCount = 0;

	protected int updatedCount = 0;

	/**
	 * Runs the import process
	 * @throws Exception if an error occurred
	 */
	protected abstract void doImport() throws Exception;

	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		try {
			doImport();

			log("Created " + createdCount + " new locations and updated " + updatedCount + " existing locations");
		}
		catch (Exception ex) {
			log("Import failed");
			log(ex.toString());
		}
	}

	/**
	 * Records a log message
	 * @param message the message
	 */
	protected void log(String message) {
		output.append(message);
		output.append("\n");

		log.info(message);
	}

	/**
	 * Gets the output of this task
	 * @return the output
	 */
	public String getOutput() {
		return output.toString();
	}

	/**
	 * Gets the count of newly created locations
	 * @return the count
	 */
	public int getCreatedCount() {
		return createdCount;
	}

	/**
	 * Gets the count of updated locations
	 * @return the count
	 */
	public int getUpdatedCount() {
		return updatedCount;
	}
}