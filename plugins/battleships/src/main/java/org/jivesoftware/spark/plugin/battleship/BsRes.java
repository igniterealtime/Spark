package org.jivesoftware.spark.plugin.battleship;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;


public class BsRes {
    private static final PropertyResourceBundle prb;

    static ClassLoader cl = BsRes.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/battleships_i18n");
    }

    public static String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        } catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }

    public static String getString(String propertyName, Object... obj) {
        String str = prb.getString(propertyName);
        return MessageFormat.format(str, obj);
    }
}
