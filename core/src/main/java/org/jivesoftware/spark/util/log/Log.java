/**
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Creates and writes out messages to a a log file. This should be used for all
 * error handling within the Agent application.
 */
public class Log {
	private static final Logger ERROR_LOGGER;
    private static final boolean debugEnabled = System.getProperty("debug.mode") != null;

    static {
		ERROR_LOGGER = java.util.logging.Logger.getAnonymousLogger();
		File ERROR_LOG_FILE = new File(Spark.getLogDirectory(), "errors.log");
		try {
			// Create an appending file handler
			boolean append = true;
			FileHandler errorHandler = new FileHandler(
					ERROR_LOG_FILE.getCanonicalPath(), 1000000, 10, append);
			errorHandler.setFormatter(new SimpleFormatter());
			// Add to the desired logger
			ERROR_LOGGER.addHandler(errorHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logs all error messages to default error logger.
	 *
	 * @param message
	 *            a message to append to log file.
	 * @param ex the exception being thrown.
	 */
	public static void error(String message, Throwable ex) {
        ERROR_LOGGER.log(Level.SEVERE, message, ex);
	}

	/**
	 * Logs all error messages to default error logger.
	 *
	 * @param ex the exception being thrown.
	 */
	public static void error(Throwable ex) {
        ERROR_LOGGER.log(Level.SEVERE, "", ex);
	}

	/**
	 * Log a warning message to the default logger.
	 *
	 * @param message the message to log.
	 * @param ex the exception.
	 */
	public static void warning(String message, Throwable ex) {
        ERROR_LOGGER.log(Level.WARNING, message, ex);
	}

	public static void warning(String message) {
        ERROR_LOGGER.log(Level.WARNING, message);
	}

	/**
	 * Logs all error messages to default error logger.
	 *
	 * @param message a message to append to log file.
	 */
	public static void error(String message) {
        ERROR_LOGGER.log(Level.SEVERE, message);
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
        if (debugEnabled) {
            System.err.println(message);
		}
	}

}
