package org.jivesoftware.spark.plugin.otr.util;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * OTRResources needed to load icons and language files into the OTR plugin
 *
 * @author Bergunde Holger
 */
public class OTRResources {

    private static final PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/otrplugin_i18n");
    private static ClassLoader cl = OTRResources.class.getClassLoader();
    public static final ImageIcon PLUGIN_ICON = getImageIcon("otr_pref.png");
    public static final ImageIcon ICON_OTR_ON = getImageIcon("otr_on.png");
    public static final ImageIcon ICON_OTR_OFF = getImageIcon("otr_off.png");

    /**
     * Returns a string from the language file
     */
    public static String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        } catch (Exception e) {
            return propertyName;
        }
    }

    /**
     * Returns a string with wildcards
     */
    public static String getString(String propertyName, Object... obj) {
        try {
            String str = prb.getString(propertyName);
            return MessageFormat.format(str, obj);
        } catch (Exception e) {
            return propertyName;
        }
    }

    private static ImageIcon getImageIcon(String icon) {
        return new ImageIcon(Objects.requireNonNull(cl.getResource(icon)));
    }
}
