package org.jivesoftware.spark.otrplug.util;

/**
 * OTRResources needed to load icons and language files into OTR plugin
 * 
 * @author Bergunde Holger
 */
import java.net.URL;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import org.jivesoftware.spark.util.log.Log;

public class OTRResources {

    private static PropertyResourceBundle prb;

    static {
        prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/otrplugin_i18n");
    }

    /**
     * Returns a string from the language file
     */
    public static String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        } catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }

    /**
     * Returns an ImageIcon from OTR resources folder
     */
    public static ImageIcon getIcon(String fileName) {
        try {
            final ClassLoader cl = OTRResources.class.getClassLoader();
            final URL imageURL = cl.getResource(fileName);
            if (imageURL != null) {
                return new ImageIcon(imageURL);
            } else {
                Log.warning(fileName + " not found.");
            }
        }
        catch (Exception e) {
            Log.warning("Unable to load image " + fileName, e);
        }
        return null;
    }

    /**
     * Returns a string with wildcards
     */
    public static String getString(String propertyName, Object... obj) {
        try {
            String str = prb.getString(propertyName);
            return MessageFormat.format(str, obj);
        } catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }

}
