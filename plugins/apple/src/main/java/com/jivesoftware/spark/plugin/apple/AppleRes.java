package com.jivesoftware.spark.plugin.apple;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;

import javax.swing.ImageIcon;


public class AppleRes {
    private static final PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/apple_i18n");
    private static final ClassLoader cl = AppleRes.class.getClassLoader();

    static String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        } catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }

    static String getString(String propertyName, Object... obj) {
        String str = prb.getString(propertyName);
        return MessageFormat.format(str, obj);
    }

    static ImageIcon getImageIcon(String icon) {
        return new ImageIcon(cl.getResource(icon));
    }

}
