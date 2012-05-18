/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.util.log;

import org.jivesoftware.Spark;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Creates and writes out messages to a a log file. This should be used for all
 * error handling within the Agent application.
 */
public class Log {
	private java.util.logging.Logger ERROR_LOGGER;
	private Logger WARNING_LOGGER;

	private volatile static Log singleton = null;

	private static Log getInstance() {
		if (singleton == null) {
			synchronized (Log.class) {
				if (singleton == null) {
					singleton = new Log();
				}
			}
		}
		return singleton;
	}

	private Log() {
		if (!Spark.getLogDirectory().exists()) {
			Spark.getLogDirectory().mkdirs();
		}
		ERROR_LOGGER = java.util.logging.Logger.getAnonymousLogger();
		WARNING_LOGGER = java.util.logging.Logger.getAnonymousLogger();

		File ERROR_LOG_FILE = new File(Spark.getLogDirectory(), "errors.log");
		File WARNING_LOG_FILE = new File(Spark.getLogDirectory(), "warn.log");

		try {
			// Create an appending file handler
			boolean append = true;
			FileHandler errorHandler = new FileHandler(
					ERROR_LOG_FILE.getCanonicalPath(), append);
			errorHandler.setFormatter(new SimpleFormatter());

			FileHandler warnHandler = new FileHandler(
					WARNING_LOG_FILE.getCanonicalPath(), append);
			warnHandler.setFormatter(new SimpleFormatter());

			// Add to the desired logger
			ERROR_LOGGER.addHandler(errorHandler);
			WARNING_LOGGER.addHandler(warnHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a handler to the error logger
	 * 
	 * @param h the handler to add
	 */
	public synchronized static void addErrorHandler(Handler h) {
		getInstance().ERROR_LOGGER.addHandler(h);
	}

	/**
	 * Adds a handler to the error logger
	 * 
	 * @param h the handler to add
	 */
	public synchronized static void addWarningHandler(Handler h) {
		getInstance().WARNING_LOGGER.addHandler(h);
	}

	/**
	 * Logs all error messages to default error logger.
	 * 
	 * @param message
	 *            a message to append to log file.
	 * @param ex the exception being thrown.
	 */
	public static void error(String message, Throwable ex) {
		getInstance().ERROR_LOGGER.log(Level.SEVERE, message, ex);
	}

	/**
	 * Logs all error messages to default error logger.
	 * 
	 * @param ex the exception being thrown.
	 */
	public static void error(Throwable ex) {
		getInstance().ERROR_LOGGER.log(Level.SEVERE, "", ex);
	}

	/**
	 * Log a warning message to the default logger.
	 * 
	 * @param message the message to log.
	 * @param ex the exception.
	 */
	public static void warning(String message, Throwable ex) {
		getInstance().WARNING_LOGGER.log(Level.WARNING, message, ex);
	}

	public static void warning(String message) {
		getInstance().WARNING_LOGGER.log(Level.WARNING, message);
	}

	/**
	 * Logs all error messages to default error logger.
	 * 
	 * @param message a message to append to log file.
	 */
	public static void error(String message) {
		getInstance().ERROR_LOGGER.log(Level.SEVERE, message);
	}

	/**
	 * Logs all messages to standard errout for debugging purposes. To use, pass
	 * in the VM Parameters debug.mode=true.
	 * <p/>
	 * ex. (-Ddebug.mode=true)
	 * 
	 * @param message the message to print out.
	 */
	public static void debug(String message) {
		if (System.getProperty("debug.mode") != null) {
			getInstance().ERROR_LOGGER.info(message);
		}
	}

}
