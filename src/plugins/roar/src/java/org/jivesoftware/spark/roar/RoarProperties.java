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
import org.jivesoftware.spark.roar.displaytype.BottomRight;
import org.jivesoftware.spark.roar.displaytype.RoarDisplayType;
import org.jivesoftware.spark.roar.displaytype.SparkToasterHandler;
import org.jivesoftware.spark.roar.displaytype.TopRight;

/**
 * RoarProperties file stuff
 * 
 * @author wolf.posdorfer
 * 
 */
public class RoarProperties {
    private Properties props;
    private File configFile;

    public static final String BACKGROUNDCOLOR = "backgroundcolor";
    public static final String HEADERCOLOR = "headercolor";
    public static final String TEXTCOLOR = "textcolor";
    public static final String DURATION = "duration";
    public static final String ACTIVE = "active";
    public static final String AMOUNT = "amount";
    public static final String ROARDISPLAYTYPE = "roardisplaytype";
    
    private static final Object LOCK = new Object();  
    private static RoarProperties instance = null;
    
    /**
     * returns the Instance of this Properties file
     * @return
     */
    public static RoarProperties getInstance() {
	synchronized (LOCK) {
	    if (instance == null) {
		instance = new RoarProperties();
	    }
	    return instance;
	}
    }

    private RoarProperties() {
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
	    props.store(new FileOutputStream(getConfigFile()),
		    "Storing ROAR properties");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public boolean getShowingPopups() {
	return getBoolean(ACTIVE, false);
    }

    public void setShowingPopups(boolean popups) {
	setBoolean(ACTIVE, popups);
    }

    public Color getBackgroundColor() {
	return getColor(BACKGROUNDCOLOR, Color.BLACK);
    }

    public void setBackgroundColor(Color c) {
	setColor(BACKGROUNDCOLOR, c);
    }

    public Color getHeaderColor() {
	return getColor(HEADERCOLOR, Color.RED);
    }

    public void setHeaderColor(Color c) {
	setColor(HEADERCOLOR, c);
    }

    public Color getTextColor() {
	return getColor(TEXTCOLOR, Color.WHITE);
    }

    public void setTextColor(Color c) {
	setColor(TEXTCOLOR, c);
    }

    public int getDuration() {

	return getInt(DURATION) == 0 ? 3000 : getInt(DURATION);
    }

    public void setDuration(int dur) {
	setInt(DURATION, dur);
    }

    public int getMaximumPopups() {
	return getInt(AMOUNT);
    }

    public void setMaximumPopups(int amount) {
	setInt(AMOUNT, amount);
    }
    
    
    public void setDisplayType(String classstring)
    {
	props.setProperty(ROARDISPLAYTYPE, classstring);
    }
    
    public String getDisplayType() {

	return props.getProperty(ROARDISPLAYTYPE, TopRight.getName());

    }
    
    public RoarDisplayType getDisplayTypeClass() {

	if (getDisplayType().equals(TopRight.getName())) {
	    return new TopRight();
	}
	else if(getDisplayType().equals(SparkToasterHandler.getName()))
	{
	    return new SparkToasterHandler();
	}
	
	else {
	    return new BottomRight();
	}
	
    }

    // ===============================================================================
    // ===============================================================================
    // ===============================================================================
    private boolean getBoolean(String property, boolean defaultValue) {
	return Boolean.parseBoolean(props.getProperty(property,
		Boolean.toString(defaultValue)));
    }

    private void setBoolean(String property, boolean value) {
	props.setProperty(property, Boolean.toString(value));
    }

    private int getInt(String property) {
	return Integer.parseInt(props.getProperty(property, "0"));
    }

    private void setInt(String property, int integer) {
	props.setProperty(property, "" + integer);
    }

    private void setColor(String property, Color color) {
	props.setProperty(property, convertColor(color));
    }

    private Color getColor(String property, Color defaultcolor) {
	try {
	    return convertString(props.getProperty(property));
	} catch (Exception e) {
	    return defaultcolor;
	}

    }

    public String getProperty(String property) {
	return props.getProperty(property);
    }

    /**
     * Converts a {@link String} matching xxx,xxx,xxx to a {@link Color}<br>
     * where xxx is a number from 0 to 255
     * 
     * @param s
     * @return
     */
    public static Color convertString(String s) throws Exception {
	String[] arr = s.split(",");
	return new Color(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]),
		Integer.parseInt(arr[2]));

    }

    /**
     * Converts a {@link Color} to a {@link String} in this format:<br>
     * <b>xxx,xxx,xxx</b> <br>
     * where xxx is a number from 0 to 255
     * 
     * @param color
     * @return
     */
    public static String convertColor(Color color) {
	return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
    
 }
