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
package com.jivesoftware.spark.plugin.apple;

import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jivesoftware.Spark;

/**
 * Apple Plugin Properties
 *
 * @author Wolf Posdorfer
 */
public class AppleProperties {
    private final Properties props;
    private File configFile;

    private static final String DOCKBOUNCE = "DOCKBOUNCE";
    private static final String REPEATDOCKBOUNCE = "PERMANENTDOCKBOUNCE";
    private static final String DOCKBADGE = "DOCKBADGE";

    public AppleProperties() {
        props = new Properties();
        try {
            props.load(new FileInputStream(getConfigFile()));
        } catch (IOException e) {
           Log.error(e);
        }
    }

    private File getConfigFile() {
        if (configFile == null) {
            configFile = new File(Spark.getSparkUserHome(), "apple.properties");
        }
        return configFile;
    }

    public void save() {
        try {
            props.store(new FileOutputStream(getConfigFile()), "Storing Apple/Growl properties");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public boolean getDockBadges() {
        return getBoolean(DOCKBADGE, true);
    }

    public boolean getDockBounce() {
        return getBoolean(DOCKBOUNCE, true);
    }

    public boolean getRepeatBouncing() {
        return getBoolean(REPEATDOCKBOUNCE, false);
    }

    public void setDockBadges(boolean enabled) {
        getBoolean(DOCKBADGE, enabled);
    }

    public void setDockBounce(boolean enabled) {
        setBoolean(DOCKBOUNCE, enabled);
    }

    public void setRepeatBouncing(boolean enabled) {
        setBoolean(REPEATDOCKBOUNCE, enabled);
    }

    private boolean getBoolean(String property, boolean defaultValue) {
        return Boolean.parseBoolean(props.getProperty(property, Boolean.toString(defaultValue)));
    }

    private void setBoolean(String property, boolean value) {
        props.setProperty(property, Boolean.toString(value));
    }
}
