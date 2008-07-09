/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.resource;

import org.jivesoftware.spark.util.log.Log;

import javax.swing.ImageIcon;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Default {
    private static PropertyResourceBundle prb;

    private static Map<String,Object> customMap = new HashMap<String,Object>();

    private static Map<String,ImageIcon> cache = new HashMap<String,ImageIcon>();

    public static final String MAIN_IMAGE = "MAIN_IMAGE";
    public static final String APPLICATION_NAME = "APPLICATION_NAME";
    public static final String SHORT_NAME = "SHORT_NAME";
    public static final String LOGIN_DIALOG_BACKGROUND_IMAGE = "LOGIN_DIALOG_BACKGROUND_IMAGE";
    public static final String HOST_NAME = "HOST_NAME";
    public static final String SHOW_POWERED_BY = "SHOW_POWERED_BY";
    public static final String TOP_BOTTOM_BACKGROUND_IMAGE = "TOP_BOTTOM_BACKGROUND_IMAGE";
    public static final String BRANDED_IMAGE = "BRANDED_IMAGE";
    public static final String CUSTOM = "CUSTOM";
    public static final String SECONDARY_BACKGROUND_IMAGE = "SECONDARY_BACKGROUND_IMAGE";
    public static final String HOVER_TEXT_COLOR = "HOVER_TEXT_COLOR";
    public static final String TEXT_COLOR = "TEXT_COLOR";
    public static final String TAB_START_COLOR = "TAB_START_COLOR";
    public static final String TAB_END_COLOR = "TAB_END_COLOR";
    public static final String CONTACT_GROUP_START_COLOR = "CONTACT_GROUP_START_COLOR";
    public static final String CONTACT_GROUP_END_COLOR = "CONTACT_GROUP_END_COLOR";
    public static final String PROXY_HOST = "PROXY_HOST";
    public static final String PROXY_PORT = "PROXY_PORT";
    public static final String ACCOUNT_DISABLED = "ACCOUNT_DISABLED";
    public static final String CHANGE_PASSWORD_DISABLED = "CHANGE_PASSWORD_DISABLED";
    public static final String TRAY_IMAGE = "TRAY_IMAGE";
    public static final String FRAME_IMAGE = "FRAME_IMAGE";

    static ClassLoader cl = SparkRes.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle)ResourceBundle.getBundle("org/jivesoftware/resource/default");
    }

    public static void putCustomValue(String value, Object object) {
        customMap.put(value, object);
    }

    public static void removeCustomValue(String value) {
        customMap.remove(value);
    }

    public static void clearCustomValues() {
        customMap.clear();
    }

    public static String getString(String propertyName) {
        return prb.getString(propertyName);
    }

    public static ImageIcon getImageIcon(String imageName) {
        // Check custom map
        Object o = customMap.get(imageName);
        if (o != null && o instanceof ImageIcon) {
            return (ImageIcon)o;
        }

        // Otherwise check cache
        o = cache.get(imageName);
        if (o != null && o instanceof ImageIcon) {
            return (ImageIcon)o;
        }

        // Otherwise, load and add to cache.
        try {
            final String iconURI = getString(imageName);
            final URL imageURL = cl.getResource(iconURI);

            final ImageIcon icon = new ImageIcon(imageURL);
            cache.put(imageName, icon);
            return icon;
        }
        catch (Exception ex) {
            Log.debug(imageName + " not found.");
        }
        return null;
    }

    public static URL getURL(String propertyName) {
        return cl.getResource(getString(propertyName));
    }


    public static URL getURLWithoutException(String propertyName) {
        // Otherwise, load and add to cache.
        try {
            final String iconURI = getString(propertyName);
            return cl.getResource(iconURI);
        }
        catch (Exception ex) {
            Log.debug(propertyName + " not found.");
        }
        return null;
    }

}