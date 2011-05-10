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
package org.jivesoftware.spark.roar;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jivesoftware.Spark;

public class RoarProperties {
    private Properties props;
    private File configFile;
    
    public static final String BACKGROUNDCOLOR = "backgroundcolor";

    public RoarProperties() {
	this.props = new Properties();

	try {
	    props.load(new FileInputStream(getConfigFile()));
	} catch (IOException e) {
	    // Can't load ConfigFile
	}

    }

    private File getConfigFile() {
	if (configFile == null)
	    configFile = new File(Spark.getSparkUserHome(), "roar.properties");
	
	return configFile;
    }

    public void save() {
	try {
	    props.store(new FileOutputStream(getConfigFile()), "");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public boolean showingPopups() {
	return getBoolean("popups", true);
    }

    public void setShowingPopups(boolean popups) {
	setBoolean("popups", popups);
    }

    public Color getBackgroundColor() {
	return getColor(BACKGROUNDCOLOR);
    }

    public void setBackgroundColor(Color c) {
	setColor(BACKGROUNDCOLOR, c);
    }

    private boolean getBoolean(String property, boolean defaultValue) {
	return Boolean.parseBoolean(props.getProperty(property,
		Boolean.toString(defaultValue)));
    }

    private void setBoolean(String property, boolean value) {
	props.setProperty(property, Boolean.toString(value));
    }

    private void setColor(String property, Color color) {
	String c = color.getRed() + "," + color.getGreen() + ","
		+ color.getBlue();
	props.setProperty(property, c);
    }

    private Color getColor(String property) {
	try {
	    String c = props.getProperty(property);

	    String[] arr = c.split(",");
	    return new Color(Integer.parseInt(arr[0]),
		    Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
	} catch (Exception e) {
	    setColor(property,Color.BLACK);
	    save();
	    return Color.BLACK;
	}
    }
    
    public String getProperty(String property)
    {
	return props.getProperty(property);
    }

}
