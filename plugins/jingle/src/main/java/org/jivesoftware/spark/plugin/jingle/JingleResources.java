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

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;

public enum JingleResources {
    ;
    static ClassLoader cl = JingleResources.class.getClassLoader();
    private static final PropertyResourceBundle prb;

    static {
        prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/jingle_i18n");
    }

    public static String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        } catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }

    public static String getString(String propertyName, Object... obj) {
        String str = prb.getString(propertyName);
        return MessageFormat.format(str, obj);
    }
}
