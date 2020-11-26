/**
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
package org.jivesoftware.fastpath;

import org.jivesoftware.spark.util.log.Log;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 */
public class FpRes {
    private static final PropertyResourceBundle prb;

    private FpRes() {

    }

    static {
        prb = (PropertyResourceBundle)ResourceBundle.getBundle("i18n/fastpath_i18n");
    }

    public static String getString(String propertyName) {
        try {
            /* Revert to this code after Spark is moved to Java 11 or newer
            return prb.getString(propertyName);
            */
            return new String(prb.getString(propertyName).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            Log.error(e);
            return propertyName;
        }

    }

    public static String getString(String propertyName, Object... obj) {
        String str = prb.getString(propertyName);
        if (str == null) {
            return null;
        }


        return MessageFormat.format(str, obj);
    }

    public static PropertyResourceBundle getBundle() {
        return prb;
    }
}
