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

package org.jivesoftware.sparkimpl.settings;

import org.jivesoftware.resource.Default;

public class JiveInfo {

    private JiveInfo() {

    }
    
    public static String getName() {
    	return Default.getString(Default.APPLICATION_NAME);
    }

    public static String getVersion() {
        return Default.getString(Default.APPLICATION_VERSION);
    }

    /*
     * This should be used for an actual
     * build number, rather than duplicating
     * getVersion(); i propose to use the
     * SVN revision number of the build
     */
    public static String getBuildNumber() {
        return "12555";
    }

    public static String getOS() {
        return System.getProperty("os.name");
    }
}
