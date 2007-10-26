/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.plugin.phone.resource;

import javax.swing.ImageIcon;

import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Use for Phone Res Internationalization.
 *
 * @author Derek DeMoro
 */
public class PhoneRes {
    private static PropertyResourceBundle prb;
    private static PropertyResourceBundle irb;

    private PhoneRes() {

    }

    static ClassLoader cl = PhoneRes.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle)ResourceBundle.getBundle("org/jivesoftware/spark/plugin/phone/resource/phone");
        irb = (PropertyResourceBundle)ResourceBundle.getBundle("org/jivesoftware/spark/plugin/phone/resource/spark_i18n");
    }

    public static final String getString(String propertyName) {
        return prb.getString(propertyName);
    }

    public static final String getIString(String propertyName) {
        return irb.getString(propertyName);
    }

    public static final ImageIcon getImageIcon(String imageName) {
        try {
            final String iconURI = getString(imageName);
            final URL imageURL = cl.getResource(iconURI);
            return new ImageIcon(imageURL);
        }
        catch (Exception ex) {
            System.out.println(imageName + " not found.");
        }
        return null;
    }

    public static final URL getURL(String propertyName) {
        return cl.getResource(getString(propertyName));
    }


}