/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware;

import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.IOException;

/**
 * Used to restart spark on a sleep thread to allow for XMPP URI Mapping.
 *
 * @author Derek DeMoro
 */
public class Restarter {

    /**
     * Is called on a restart of Spark. This is the format for restarting Spark in Log out.
     *
     * @param args the array of arguments.
     */
    public static void main(String args[]) {
        if (args.length == 0) {
            return;
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            return;
        }

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            String command = "";
            if (isWindows()) {
                command = file.getCanonicalPath();
            }
            if (isLinux()) {
                command = file.getCanonicalPath();
            }
            else if (isMac()) {
                command = "open -a Spark";
            }

            Runtime.getRuntime().exec(command);
        }
        catch (IOException e) {
            Log.error("Error starting Spark", e);
        }

    }
    
    /**
     * Return if we are running on windows.
     *
     * @return true if we are running on windows, false otherwise.
     */
    public static boolean isWindows() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("windows");
    }

    /**
     * Returns true if Spark is running on vista.
     *
     * @return true if running on Vista.
     */
    public static boolean isVista() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("vista");
    }

    /**
     * Return if we are running on a mac.
     *
     * @return true if we are running on a mac, false otherwise.
     */
    public static boolean isMac() {
        String lcOSName = System.getProperty("os.name").toLowerCase();
        return lcOSName.indexOf("mac") != -1;
    }
    
    /**
     * Return if we are running on Linux.
     *
     * @return true if we are running on Linux, false otherwise.
     */

    public static boolean isLinux() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("linux");
    }
    
}

