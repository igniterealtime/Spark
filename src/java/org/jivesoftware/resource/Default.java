/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.resource;

import org.jivesoftware.spark.PluginRes;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.ImageIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class Default {
    private static PropertyResourceBundle prb;

    private static Map<String,Object> customMap = new HashMap<String,Object>();

    private static Map<String,ImageIcon> cache = new HashMap<String,ImageIcon>();

    public static final String MAIN_IMAGE = "MAIN_IMAGE";
    public static final String APPLICATION_NAME = "APPLICATION_NAME";
    public static final String SHORT_NAME = "SHORT_NAME";
    public static final String APPLICATION_VERSION = "APPLICATION_VERSION";
    public static final String LOGIN_DIALOG_BACKGROUND_IMAGE = "LOGIN_DIALOG_BACKGROUND_IMAGE";
    public static final String HOST_NAME = "HOST_NAME";
    public static final String HOST_NAME_CHANGE_DISABLED = "HOST_NAME_CHANGE_DISABLED";
    public static final String SHOW_POWERED_BY = "SHOW_POWERED_BY";
    public static final String TOP_BOTTOM_BACKGROUND_IMAGE = "TOP_BOTTOM_BACKGROUND_IMAGE";
    public static final String BRANDED_IMAGE = "BRANDED_IMAGE";
    public static final String DISABLE_UPDATES = "DISABLE_UPDATES";
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
    public static final String PASSWORD_RESET_ENABLED = "PASSWORD_RESET_ENABLED";
    public static final String PASSWORD_RESET_URL = "PASSWORD_RESET_URL";
    public static final String ADD_CONTACT_DISABLED = "ADD_CONTACT_DISABLED";
    public static final String CHANGE_PASSWORD_DISABLED = "CHANGE_PASSWORD_DISABLED";
    public static final String TRAY_IMAGE = "TRAY_IMAGE";
    public static final String FRAME_IMAGE = "FRAME_IMAGE";
    public static final String LOOK_AND_FEEL_DISABLED = "LOOK_AND_FEEL_DISABLED";
    public static final String DEFAULT_LOOK_AND_FEEL = "DEFAULT_LOOK_AND_FEEL";
    public static final String DEFAULT_LOOK_AND_FEEL_MAC = "DEFAULT_LOOK_AND_FEEL_MAC";
    public static final String INSTALL_PLUGINS_DISABLED = "INSTALL_PLUGINS_DISABLED";
    public static final String DEINSTALL_PLUGINS_DISABLED = "DEINSTALL_PLUGINS_DISABLED";
    public static final String ADVANCED_DISABLED = "ADVANCED_DISABLED";
    public static final String SSO_DISABLED = "SSO_DISABLED";
    public static final String PROXY_DISABLED = "PROXY_DISABLED";
    public static final String PKI_DISABLED = "PKI_DISABLED";
    public static final String HELP_USER_GUIDE = "HELP_USER_GUIDE";
    public static final String BROADCAST_IN_CHATWINDOW = "BROADCAST_IN_CHATWINDOW";
    public static final String MENUBAR_TEXT = "MENUBAR_TEXT";
    public static final String FILE_TRANSFER_WARNING_SIZE = "FILE_TRANSFER_WARNING_SIZE";
    public static final String FILE_TRANSFER_MAXIMUM_SIZE = "FILE_TRANSFER_MAXIMUM_SIZE";
    public static final String TABS_PLACEMENT_TOP = "TABS_PLACEMENT_TOP";
    public static final String HIDE_PERSON_SEARCH_FIELD = "HIDE_PERSON_SEARCH_FIELD";
    public static final String USER_DIRECTORY_WINDOWS = "USER_DIRECTORY_WINDOWS";
    public static final String USER_DIRECTORY_LINUX = "USER_DIRECTORY_LINUX";
    public static final String USER_DIRECTORY_MAC = "USER_DIRECTORY_MAC";

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
        String pluginString = PluginRes.getDefaultRes(propertyName);
        return pluginString != null ? pluginString : prb.getString(propertyName);
    }

    public static boolean getBoolean(String propertyName) {
	return getString(propertyName).replace(" ","").equals("true");
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
            final URL imageURL = getURL(imageName);

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
    	URL pluginUrl = PluginRes.getDefaultURL(propertyName);
        return pluginUrl != null ? pluginUrl : cl.getResource(getString(propertyName));
    }


    public static URL getURLWithoutException(String propertyName) {
        // Otherwise, load and add to cache.
        try {
            return getURL(propertyName);
        }
        catch (Exception ex) {
            Log.debug(propertyName + " not found.");
        }
        return null;
    }

    /**
     * Returns a Collection of Plugins on the Blacklist<br>
     * Containing the Name and also if specified the entrypoint-class
     * @return Collection
     */
    public static Collection<String> getPluginBlacklist() {
	String pluginlist = getString("PLUGIN_BLACKLIST").replace(" ", "")
		.toLowerCase();
	StringTokenizer tokenizer = new StringTokenizer(pluginlist, ",");
	ArrayList<String> list = new ArrayList<String>();

	while (tokenizer.hasMoreTokens()) {
	    list.add(tokenizer.nextToken());
	}

	StringTokenizer clazztokenz = new StringTokenizer(
		getString("PLUGIN_BLACKLIST_CLASS").replace(" ", ""), ",");

	while (clazztokenz.hasMoreTokens()) {
	    list.add(clazztokenz.nextToken());
	}

	return list;

    }

    /**
     * Returns all Keys stored in the default.properties file
     * @return {@link Enumeration}<{@link String}>
     */
    public static Enumeration<String> getAllKeys()
    {
	return prb.getKeys();
    }

}