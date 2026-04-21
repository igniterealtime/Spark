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
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Creates and writes out messages to a log file.
 * This should be used for all error handling within the application.
 * By default, the logs are written to a file in SPARK_HOME/logs/errors.log.
 * You see it by clicking on Main Menu / Help / View logs.
 * If you run the Spark from the IDE then specify the VM option <code>-Ddebug.mode=true</code>.
 * Then the logs are written to stout.
 */
public class Log {
	private static final Logger rootLogger = Logger.getLogger("");
    private static final boolean debugEnabled = System.getProperty("debug.mode") != null;

    static {
		try {
            rootLogger.setLevel(debugEnabled ? Level.ALL : Level.INFO);
            // When debug is enabled, we want to log everything only to the log file
            File logFilePath = getLogFilePath();
            if (logFilePath != null) {
                // Create an appending file handler
                FileHandler fileHandler = new FileHandler(logFilePath.getCanonicalPath(), 1_000_000, 1, true);
                fileHandler.setFormatter(new SimpleFormatter());
                fileHandler.setLevel(Level.INFO); // log to a file only INFO messages
                // Remove the console logger
                Handler rootLoggerConsoleHandler = rootLogger.getHandlers()[0];
                rootLogger.removeHandler(rootLoggerConsoleHandler);
                rootLogger.addHandler(fileHandler);
            }
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
		}
	}

    public static File getLogFilePath() {
        return !debugEnabled ? new File(Spark.getLogDirectory(), "errors.log") : null;
    }

	/**
	 * Logs the error message to log.
	 *
	 * @param message a message to append to log.
	 * @param ex the exception being thrown.
	 */
	public static void error(String message, Throwable ex) {
        rootLogger.log(Level.SEVERE, message, ex);
	}

	/**
     * Logs the error message to log.
	 *
	 * @param ex the exception being thrown.
	 */
	public static void error(Throwable ex) {
        rootLogger.log(Level.SEVERE, "", ex);
	}

	/**
	 * Log the warning message to the log.
	 *
	 * @param message the message to log.
	 * @param ex the exception.
	 */
	public static void warning(String message, Throwable ex) {
        rootLogger.log(Level.WARNING, message, ex);
	}

	public static void warning(String message) {
        rootLogger.log(Level.WARNING, message);
	}

	/**
	 * Logs the error message to log.
	 *
	 * @param message a message to log.
	 */
	public static void error(String message) {
        rootLogger.log(Level.SEVERE, message);
	}

	/**
	 * Logs the debug message to standard error output.
     * To use, pass in the VM options <code>-Ddebug.mode=true</code>.
	 *
	 * @param message the message to print out.
	 */
	public static void debug(String message) {
        if (debugEnabled) {
            System.err.println(message);
		}
	}

}
