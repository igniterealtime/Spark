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
package com.jivesoftware.spark.plugin.apple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jivesoftware.Spark;

/**
 * Apple Plugin Properties
 * 
 * @author wolf.posdorfer
 * 
 */
public class AppleProperties {
    private Properties props;
    private File configFile;

    public static final String DOCKBOUNCE = "DOCKBOUNCE";
    public static final String REPEATDOCKBOUNCE = "PERMANENTDOCKBOUNCE";
    public static final String DOCKBADGE = "DOCKBADGE";

    public AppleProperties() {
	this.props = new Properties();

	try {
	    props.load(new FileInputStream(getConfigFile()));
	} catch (IOException e) {
	    // Can't load ConfigFile
	}

    }

    private File getConfigFile() {
	if (configFile == null)
	    configFile = new File(Spark.getSparkUserHome(), "apple.properties");

	return configFile;
    }

    public void save() {
	try {
	    props.store(new FileOutputStream(getConfigFile()), "Storing Apple/Growl properties");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public boolean getDockBadges() {
	return getBoolean(DOCKBADGE, true);
    }

    public boolean getDockBounce() {
	return getBoolean(DOCKBOUNCE, true);
    }

    public boolean getRepeatBounce() {
	return getBoolean(REPEATDOCKBOUNCE, false);
    }

    // ===============================================================================
    // ===============================================================================
    // ===============================================================================
    public boolean getBoolean(String property, boolean defaultValue) {
	return Boolean.parseBoolean(props.getProperty(property, Boolean.toString(defaultValue)));
    }

    public void setBoolean(String property, boolean value) {
	props.setProperty(property, Boolean.toString(value));
    }

    public int getInt(String property) {
	return Integer.parseInt(props.getProperty(property, "0"));
    }

    public void setInt(String property, int integer) {
	props.setProperty(property, "" + integer);
    }

    public String getProperty(String property) {
	return props.getProperty(property);
    }

}
