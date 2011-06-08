package org.jivesoftware.spark.otrplug.util;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import org.jivesoftware.spark.util.log.Log;

public class OTRResources {

    private static PropertyResourceBundle prb;

    static ClassLoader cl = OTRResources.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/otrplugin_i18n");
    }

    public static final String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        } catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }
    
    
    public static ImageIcon getIcon(String fileName)
    {
        final ClassLoader cl = OTRResources.class.getClassLoader();
        ImageIcon icon = new ImageIcon(cl.getResource(fileName));
        return icon;
    }
    

    public static String getString(String propertyName, Object... obj) {
        String str = prb.getString(propertyName);
        if (str == null) {
            return propertyName;
        }


        return MessageFormat.format(str, obj);
    }

}
