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

public class SoundsRes {
    private static PropertyResourceBundle prb;
    public static final String INCOMING_USER = "INCOMING_USER";
    public static final String TRAY_SHOWING = "TRAY_SHOWING";
    public static final String OPENING = "OPENING";
    public static final String CLOSING = "CLOSING";


    static ClassLoader cl = SoundsRes.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle)ResourceBundle.getBundle("org/jivesoftware/resource/sounds");
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