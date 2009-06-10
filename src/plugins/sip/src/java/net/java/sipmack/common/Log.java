/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package net.java.sipmack.common;

/**
 * Creates and writes out messages.
 */

public class Log {

    private static boolean debugger = false;

    static {

        if (System.getProperty("debugger") != null
                && System.getProperty("debugger").equals("true"))
            debugger = true;

    }

    public static void debug(String message) {
        if (debugger)
            System.out.println((message != null ? message : ""));
    }

    public static void debug(String method, String message) {
        if (debugger)
            System.out.println((method != null ? method : "") + " - "
                    + (message != null ? message : ""));
    }

    public static void error(String method, Exception e) {
        System.out.println((method != null ? method : "") + " - "
                + (e != null ? e.toString() : ""));
        e.printStackTrace();
    }

    public static void error(Exception e) {
        error("", e);
    }

    public static void error(String method, Error e) {
        System.out.println((method != null ? method : "") + " - "
                + (e != null ? e.toString() : ""));
    }

    public static void error(String method, Throwable e) {
        System.out.println((method != null ? method : "") + " - "
                + (e != null ? e.toString() : ""));
    }

}
