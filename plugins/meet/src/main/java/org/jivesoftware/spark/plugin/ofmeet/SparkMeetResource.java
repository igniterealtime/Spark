package org.jivesoftware.spark.plugin.ofmeet;

import org.jivesoftware.resource.UTF8Control;
import org.jivesoftware.spark.util.log.Log;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SparkMeetResource {
    private static PropertyResourceBundle prb;

    static ClassLoader cl = SparkMeetResource.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/sparkmeet_i18n", new UTF8Control());
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
            return null;
        }
        return MessageFormat.format(str, obj);
    }
}
