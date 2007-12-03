/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
 * Creates and writes out messages to a a log file. This should be used for all error handling within
 * the Agent application.
 */
public class Log {
    private static java.util.logging.Logger ERROR_LOGGER;

    private static Logger WARNING_LOGGER;

    private Log() {
        // Do not allow initialization
    }

    static {
        if (!Spark.getLogDirectory().exists()) {
            Spark.getLogDirectory().mkdirs();
        }

        File ERROR_LOG_FILE = new File(Spark.getLogDirectory(), "errors.log");
        File WARNING_LOG_FILE = new File(Spark.getLogDirectory(), "warn.log");


        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler errorHandler = new FileHandler(ERROR_LOG_FILE.getCanonicalPath(), append);
            errorHandler.setFormatter(new SimpleFormatter());

            FileHandler warnHandler = new FileHandler(WARNING_LOG_FILE.getCanonicalPath(), append);
            warnHandler.setFormatter(new SimpleFormatter());

            // Add to the desired logger
            ERROR_LOGGER = java.util.logging.Logger.getAnonymousLogger();
            ERROR_LOGGER.addHandler(errorHandler);

            WARNING_LOGGER = Logger.getAnonymousLogger();
            WARNING_LOGGER.addHandler(warnHandler);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs all error messages to default error logger.
     *
     * @param message a message to append to log file.
     * @param ex      the exception being thrown.
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
     * @param ex      the exception.
     */
    public static void warning(String message, Throwable ex) {
        WARNING_LOGGER.log(Level.WARNING, message, ex);
    }

    public static void warning(String message) {
        WARNING_LOGGER.log(Level.WARNING, message);
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
     * Logs all messages to standard errout for debugging purposes.
     * To use, pass in the VM Parameters debug.mode=true.
     * <p/>
     * ex. (-Ddebug.mode=true)
     *
     * @param message the message to print out.
     */
    public static void debug(String message) {
        if (System.getProperty("debug.mode") != null) {
            ERROR_LOGGER.info(message);
        }
    }

}
