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

package org.openmrs.module.kenyametadatatools.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.kenyametadatatools.api.MetadataToolsService;
import org.openmrs.module.kenyametadatatools.mflsync.BaseMflSyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Simple task engine which runs single task at a time
 */
public class TaskEngine {

	protected final static Log log = LogFactory.getLog(TaskEngine.class);

	private static BaseTask activeTask;

	private static DaemonToken daemonToken;

	private static boolean busy = false;

	private static List<TaskMessage> messages = new ArrayList<TaskMessage>();

	private static int lastMessageId = 0;

	/**
	 * Starts the specified task if engine is not busy
	 * @param task the task to start
	 * @return true if task was started, else false if engine is busy
	 */
	public static synchronized boolean start(BaseMflSyncTask task) {
		if (busy) {
			return false;
		}

		busy = true;
		activeTask = task;
		messages.clear();

		TaskRunner runner = new TaskRunner(task);

		Daemon.runInDaemonThread(runner, daemonToken);

		return true;
	}

	/**
	 * Logs a message
	 * @param object the object
	 */
	public static void log(Object object) {
		log(object, false);
	}

	/**
	 * Logs a message
	 * @param object the object
	 */
	public static void logError(Object object) {
		log(object, true);
	}

	/**
	 * Logs a message or exception
	 * @param object the object
	 * @param error whether message is an error
	 */
	private static synchronized void log(Object object, boolean error) {
		messages.add(new TaskMessage(++lastMessageId, object.toString(), error));

		if (error) {
			log.error(object);
		}
		else {
			log.info(object);
		}
	}

	/**
	 * Gets whether task engine is busy with an existing task
	 * @return true if engine is busy
	 */
	public static synchronized boolean isBusy() {
		return busy;
	}

	/**
	 * Sets the daemon token
	 * @param token the token
	 */
	public static void setDaemonToken(DaemonToken token) {
		daemonToken = token;
	}

	/**
	 * Gets the messages logged by this task
	 * @return the messages
	 */
	public static synchronized List<TaskMessage> getMessages() {
		return getMessagesSince(null);
	}

	/**
	 * Gets the messages logged by this task after the given message id
	 * @return the messages since specified message id
	 */
	public static synchronized List<TaskMessage> getMessagesSince(Integer messageId) {
		if (messageId == null || messageId == 0) {
			return messages;
		}

		List<TaskMessage> since = new ArrayList<TaskMessage>();
		for (TaskMessage message : messages) {
			if (message.getId() > messageId) {
				since.add(message);
			}
		}
		return since;

	}

	/**
	 * Runner for executing tasks in separate thread
	 */
	private static class TaskRunner implements Runnable {

		private BaseTask task;

		public TaskRunner(BaseMflSyncTask task) {
			this.task = task;
		}

		@Override
		public void run() {
			try {
				Context.getService(MetadataToolsService.class).executeTask(task);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			finally {
				busy = false;
			}
		}
	}

	/**
	 * A timestamped message
	 */
	public static class TaskMessage {

		private int id;

		private Date timestamp;

		private String message;

		private boolean error;

		/**
		 * Constructs a task message
		 * @param id the message id
		 * @param message the message
		 * @param error whether message is error
		 */
		public TaskMessage(int id, String message, boolean error) {
			this.id = id;
			this.timestamp = new Date();
			this.message = message;
			this.error = error;
		}

		/**
		 * Gets the message id
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the message timestamp
		 * @return the timestamp
		 */
		public Date getTimestamp() {
			return timestamp;
		}

		/**
		 * Gets the message text
		 * @return the message text
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * Gets whether message is an error
		 * @return true if message is error
		 */
		public boolean isError() {
			return error;
		}
	}
}