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

import java.io.InputStream;
import java.net.URL;

public class Utils {

    public static String getSystemProperty(String property) {
        try {
            // console.logEntry();
            String retval = System.getProperty(property);
            if (retval == null) {

                return retval;
            }
            if (retval.trim().length() == 0) {
                return null;
            }
            return retval;
        }
        finally {
            // console.logExit();
        }
    }

    public static void setSystemProperty(String propertyName,
                                         String propertyValue) {
        System.setProperty(propertyName, propertyValue);
    }

    public static URL getResource(String name) {
        return Utils.class.getResource("resources/" + name);
    }

    public static InputStream getResourceAsStream(String name) {
        return Utils.class.getResourceAsStream("resources/" + name);
    }
}
