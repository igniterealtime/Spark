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
import java.util.logging.SimpleFormatter;

/**
 * Creates and writes out messages to a a log file. This should be used for all error handling within
 * the Agent application.
 */
public class Log {
    private static File LOG_FILE;
    private static java.util.logging.Logger LOGGER;

    private Log() {
        // Do not allow initialization
    }

    static {
        if (!Spark.getLogDirectory().exists()) {
            Spark.getLogDirectory().mkdirs();
        }

        LOG_FILE = new File(Spark.getLogDirectory(), "spark-error.log");


        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(LOG_FILE.getCanonicalPath(), append);
            handler.setFormatter(new SimpleFormatter());

            // Add to the desired logger
            LOGGER = java.util.logging.Logger.getAnonymousLogger();
            LOGGER.addHandler(handler);
        }
        catch (IOException e) {
            Log.error(e);
        }
    }

    /**
     * Logs all error messages to default error logger.
     *
     * @param message a message to append to log file.
     * @param ex      the exception being thrown.
     */
    public static void error(String message, Throwable ex) {
        LOGGER.log(Level.SEVERE, message, ex);
    }

    /**
     * Logs all error messages to default error logger.
     *
     * @param ex the exception being thrown.
     */
    public static void error(Throwable ex) {
        LOGGER.log(Level.SEVERE, "", ex);
    }

    /**
     * Log a warning message to the default logger.
     *
     * @param message the message to log.
     * @param ex      the exception.
     */
    public static void warning(String message, Throwable ex) {
        ex.printStackTrace();
    }

    public static void warning(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    /**
     * Logs all error messages to default error logger.
     *
     * @param message a message to append to log file.
     */
    public static void error(String message) {
        LOGGER.log(Level.SEVERE, message);
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
            LOGGER.info(message);
        }
    }

}
