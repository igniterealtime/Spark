package org.jivesoftware.spark.translator;

import org.jivesoftware.spark.util.log.Log;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class TranslatorResource {
    private static final PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/translator_i18n");

    static ClassLoader cl = TranslatorResource.class.getClassLoader();

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
