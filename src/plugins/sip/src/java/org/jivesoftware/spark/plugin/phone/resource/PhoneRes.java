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
package org.jivesoftware.spark.plugin.phone.resource;

import javax.swing.ImageIcon;

import org.jivesoftware.spark.util.log.Log;

import java.awt.Image;
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
        irb = (PropertyResourceBundle)ResourceBundle.getBundle("org/jivesoftware/spark/plugin/phone/resource/sip_spark_i18n");
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

    public static final Image getImage(String imageName) {
       try {
           final String iconURI = getString(imageName);
           final URL imageURL = cl.getResource(iconURI);
           return new ImageIcon(imageURL).getImage();
       }
       catch (Exception ex) {
           Log.error(imageName + " not found.");
       }
       return null;
   }
}