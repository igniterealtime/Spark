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

import javax.swing.ImageIcon;

import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ConfigurationRes {
    private static PropertyResourceBundle prb;
    public static final String GLOBAL_ELEMENT_NAME = "GLOBAL_ELEMENT_NAME";
    public static final String DELETE_IMAGE = "DELETE_IMAGE";
    public static final String PERSONAL_NAMESPACE = "PERSONAL_NAMESPACE";
    public static final String HEADER_FILE = "HEADER_FILE";
    public static final String CHECK_IMAGE = "CHECK_IMAGE";

    public static final String SPELLING_PROPERTIES = "SPELLING_PROPERTIES";
    public static final String PERSONAL_ELEMENT_NAME = "PERSONAL_ELEMENT_NAME";
    static ClassLoader cl = ConfigurationRes.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle)ResourceBundle.getBundle("org/jivesoftware/resource/configuration");
    }

    public static String getString(String propertyName) {
        return prb.getString(propertyName);
    }

    public static ImageIcon getImageIcon(String imageName) {
        final String iconURI = getString(imageName);
        final URL imageURL = cl.getResource(iconURI);
        return new ImageIcon(imageURL);
    }

    public static URL getURL(String propertyName) {
        return cl.getResource(getString(propertyName));
    }
}