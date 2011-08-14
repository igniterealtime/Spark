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

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Use for Spark Internationalization.
 *
 * @author Derek DeMoro
 */
public class Res {
    private static PropertyResourceBundle prb;

    private Res() {

    }

    static ClassLoader cl = Res.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle)ResourceBundle.getBundle("i18n/spark_i18n");
    }

    public static String getString(String propertyName) {
        try {
            String pluginString = PluginRes.getI18nRes(propertyName);
            return pluginString != null ? pluginString : prb.getString(propertyName);
        }
        catch (Exception e) {
            Log.error(e);
            return propertyName;
        }

    }

    public static String getString(String propertyName, Object... obj) {
        String pluginString = PluginRes.getI18nRes(propertyName);
        String str = pluginString != null ? pluginString : prb.getString(propertyName);
        if (str == null) {
            return propertyName;
        }


        return MessageFormat.format(str, obj);
    }

    public static PropertyResourceBundle getBundle() {
        return prb;
    }
}
