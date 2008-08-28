/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.settings;

public class JiveInfo {

    private JiveInfo() {

    }

    public static String getVersion() {
        return "2.6.0 Beta 2";
    }

    /*
     * This should be used for an actual
     * build number, rather than duplicating
     * getVersion(); i propose to use the
     * SVN revision number of the build 
     */
    public static String getBuildNumber() {
        return "10370"; //example number
    }

    public static String getOS() {
        return System.getProperty("os.name");
    }
}
