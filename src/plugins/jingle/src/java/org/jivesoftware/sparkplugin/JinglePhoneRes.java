/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package org.jivesoftware.sparkplugin;

import javax.swing.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.net.URL;

/**
 * Use for Phone Res Internationalization.
 *
 * @author Derek DeMoro
 */
public class JinglePhoneRes {
    private static PropertyResourceBundle prb;

    private JinglePhoneRes() {

    }

    static ClassLoader cl = JinglePhoneRes.class.getClassLoader();

    static {
        JinglePhoneRes.prb = (PropertyResourceBundle) ResourceBundle.getBundle("org/jivesoftware/sparkplugin/jingle");
    }

    public static final String getString(String propertyName) {
        return JinglePhoneRes.prb.getString(propertyName);
    }

    public static final ImageIcon getImageIcon(String imageName) {
        try {
            final String iconURI = JinglePhoneRes.getString(imageName);
            final URL imageURL = JinglePhoneRes.cl.getResource(iconURI);
            return new ImageIcon(imageURL);
        }
        catch (Exception ex) {
            System.out.println(imageName + " not found.");
        }
        return null;
    }

    public static final URL getURL(String propertyName) {
        return JinglePhoneRes.cl.getResource(JinglePhoneRes.getString(propertyName));
    }


}
