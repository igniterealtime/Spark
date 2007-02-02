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
            if (Spark.isWindows()) {
                command = file.getCanonicalPath();
            }
            else if (Spark.isMac()) {
                command = "open -a Spark";
            }

            Runtime.getRuntime().exec(command);
        }
        catch (IOException e) {
            Log.error("Error starting Spark", e);
        }

    }


}

