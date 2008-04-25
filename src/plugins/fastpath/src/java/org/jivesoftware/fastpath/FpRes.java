/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.fastpath;

import org.jivesoftware.spark.util.log.Log;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 *
 */
public class FpRes {
    private static PropertyResourceBundle prb;

    private FpRes() {

    }

    static ClassLoader cl = FpRes.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle)ResourceBundle.getBundle("i18n/fastpath_i18n");
    }

    public static final String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        }
        catch (Exception e) {
            Log.error(e);
            return propertyName;
        }

    }

    public static final String getString(String propertyName, Object... obj) {
        String str = prb.getString(propertyName);
        if (str == null) {
            return null;
        }


        return MessageFormat.format(str, obj);
    }

    public static PropertyResourceBundle getBundle() {
        return prb;
    }
}
