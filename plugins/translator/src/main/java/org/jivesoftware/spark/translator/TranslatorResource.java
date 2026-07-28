package org.jivesoftware.spark.translator;

import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class TranslatorResource {
    private static final PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/translator_i18n");
    private static ClassLoader cl = TranslatorResource.class.getClassLoader();
    static ImageIcon ICON_TRANSLATOR = new ImageIcon(cl.getResource("translation/translator.png"));

    static String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        }
        catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }

    static String getString(String propertyName, Object... obj) {
        String str = prb.getString(propertyName);
        if (str == null) {
            return null;
        }
        return MessageFormat.format(str, obj);
    }

}
