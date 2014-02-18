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
