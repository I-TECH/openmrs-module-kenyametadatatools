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

import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.kenyamflsync.api.KenyaMflSyncService;

/**
 * Simple task engine which runs single task at a time
 */
public class TaskEngine {

	private static BaseSynchronizeTask activeTask;

	private static boolean busy = false;

	/**
	 * Starts the specified task if engine is not busy
	 * @param task the task to start
	 * @return true if task was started, else false if engine is busy
	 */
	public static synchronized boolean start(BaseSynchronizeTask task) {
		if (busy) {
			return false;
		}

		busy = true;
		activeTask = task;

		TaskRunner runner = new TaskRunner(task);
		Thread taskThread = new Thread(runner);
		taskThread.start();
		return true;
	}

	/**
	 * Gets whether task engine is busy with an existing task
	 * @return true if engine is busy
	 */
	public static synchronized boolean isBusy() {
		return busy;
	}

	/**
	 * Gets the output of the current task
	 * @return
	 */
	public static synchronized String getOutput() {
		return (activeTask != null) ? activeTask.getOutput() : null;
	}

	/**
	 * Runner for executing tasks in separate thread
	 */
	public static class TaskRunner implements Runnable {

		private BaseSynchronizeTask task;
		private UserContext userContext;

		public TaskRunner(BaseSynchronizeTask task) {
			this.task = task;
			this.userContext = Context.getUserContext();
		}

		@Override
		public void run() {
			try {
				Context.setUserContext(userContext);
				Context.getService(KenyaMflSyncService.class).executeTask(task);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			finally {
				busy = false;
			}
		}
	}
}