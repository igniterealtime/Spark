package org.jivesoftware.spark.plugin.ofmeet;

import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SparkMeetResource {
    private static final PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/sparkmeet_i18n");

    private static final ClassLoader cl = SparkMeetResource.class.getClassLoader();
    static final ImageIcon PLUGIN_ICON = getImageIcon("images/pade.png");

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

    private static ImageIcon getImageIcon(String icon) {
        return new ImageIcon(cl.getResource(icon));
    }
}
