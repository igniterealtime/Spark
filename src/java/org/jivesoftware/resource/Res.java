/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.resource;

import org.jivesoftware.spark.util.log.Log;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Use for Spark Internationalization.
 *
 * @author Derek DeMoro
 */
public class Res {
    private static PropertyResourceBundle prb;

    private Res() {

    }

    static ClassLoader cl = Res.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle)ResourceBundle.getBundle("i18n/spark_i18n");
    }

    public static String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        }
        catch (Exception e) {
            Log.error(e);
            return propertyName;
        }

    }

    public static String getString(String propertyName, Object... obj) {
        String str = prb.getString(propertyName);
        if (str == null) {
            return propertyName;
        }


        return MessageFormat.format(str, obj);
    }

    public static PropertyResourceBundle getBundle() {
        return prb;
    }
}
