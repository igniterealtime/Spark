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

package org.jivesoftware.sparkimpl.settings;


import org.jivesoftware.resource.SparkRes;

public class JiveInfo {

    private JiveInfo() {
    }
    
    public static String getName() {
        final String name = SparkRes.getString( "APP_NAME" );
        return name != null && !name.isBlank() ? name.trim() : "Spark";
    }

    public static String getVersion() {
        final String version = SparkRes.getString( "VERSION" );
        // When running from IDE the VERSION=${project.version} because it wasn't yet filtered by the Maven resources plugin.
        // avoid null and return at least some current version
        return version != null && !version.isBlank() && !version.equals("${project.version}") ? version.trim() : "3.1.0";
    }

    public static String getOS() {
        return System.getProperty("os.name");
    }
}
