/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.plugin.jingle;

import javax.swing.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.net.URL;

import org.jivesoftware.spark.util.log.Log;

/**
 * Use for Phone Res Internationalization.
 *
 * @author Derek DeMoro
 */
public enum JinglePhoneRes {
    ;
    static ClassLoader cl = JinglePhoneRes.class.getClassLoader();
    private static PropertyResourceBundle prb;

    static {
        JinglePhoneRes.prb = (PropertyResourceBundle) ResourceBundle.getBundle("org/jivesoftware/spark/plugin/jingle/jingle");
    }

    public static String getString(String propertyName) {
        return JinglePhoneRes.prb.getString(propertyName);
    }

    public static ImageIcon getImageIcon(String imageName) {
        try {
            final String iconURI = JinglePhoneRes.getString(imageName);
            final URL imageURL = JinglePhoneRes.cl.getResource(iconURI);
            if (imageURL != null) {
                return new ImageIcon(imageURL);
            } else {
                Log.warning(imageName + " not found.");
            }
        } catch (Exception e) {
            Log.warning("Unable to load image " + imageName, e);
        }
        return null;
    }

    public static URL getURL(String propertyName) {
        return JinglePhoneRes.cl.getResource(JinglePhoneRes.getString(propertyName));
    }
}
