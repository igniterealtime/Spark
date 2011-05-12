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

