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
package org.jivesoftware.spark.ui.themes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.util.log.Log;

public class ColorSettingManager {

    private static HashMap<String, String> _propertyHashMap = new HashMap<>();

	public ColorSettingManager() {

    }

    /**
     * Returns the ColorSettingagent
     * 
     * @return ColorSettingagent
     */
    public static ColorSettings getColorSettings() {
	File settings = getSettingsFile();
		ColorSettings _colorsettings = loadSettings( settings );
	return _colorsettings;
    }

    public static boolean exists() {
	return getSettingsFile().exists();
    }

    /**
     * Returns the file or creates it
     * 
     * @return
     */
    public static File getSettingsFile() {
	File path = new File(Spark.getSparkUserHome());
	if (!path.exists()) {
	    path.mkdirs();
	}
	File f = new File(path, "color.settings");
	if (!f.exists())
	    try {
		f.createNewFile();
	    } catch (IOException e) {
		Log.error("Error saving settings.", e);
	    }
	return f;
    }

    /**
     * Save all settings
     */
    public static void saveColorSettings() {
	final Properties props = new Properties();

	for (String key : _propertyHashMap.keySet()) {
	    String value = _propertyHashMap.get(key);
	    props.setProperty(key, value);
	}
	try {
	    props.store(new FileOutputStream(getSettingsFile()),
		    "Storing Spark Color Settings");
	} catch (Exception e) {
	    Log.error("Error saving settings.", e);
	}
    }

    /**
     * load the Settingfile
     * 
     * @param file
     * @return
     */
    private static ColorSettings loadSettings(File file) {
	// load from file
	loadSettingsToMap(file);

	if (_propertyHashMap.size() == 0) {

	    Properties p = new Properties();
	    try {
		p.load(new FileInputStream(getSettingsFile()));
		initialLoad(p);
		loadSettingsToMap(file);

	    } catch (FileNotFoundException e) {
		Log.error("Error saving settings.", e);
	    } catch (IOException e) {
		Log.error("Error saving settings.", e);
	    }

	} else if (_propertyHashMap.size() != getDefaultColors().size()) {
	    compareSettings(_propertyHashMap, getDefaultColors());
	}

	ColorSettings settings = new ColorSettings(_propertyHashMap);

	return settings;

    }

    /**
     * Compares two Hashmaps, if defaultmap has keys that are not within mymap
     * 
     * @param mymap
     *            HashMap < String,String >
     * @param defaultmap
     *            HashMap < String,String >
     */
    private static void compareSettings(HashMap<String, String> mymap,
	    HashMap<String, String> defaultmap) {
	boolean changesmade = false;
	for (String key : defaultmap.keySet()) {
	    if (mymap.get(key) == null) // key doesnt exist in mymap
	    {
		mymap.put(key, defaultmap.get(key));
		changesmade = true;
	    }
	}

	if (changesmade) {
	    saveColorSettings();
	}

    }

    /**
     * loads all Properties into a HashMap from the File specified
     * 
     * @param file
     */
    private static void loadSettingsToMap(File file) {

	Properties props = new Properties();
	try {

	    props.load(new FileInputStream(file));

	    Enumeration<Object> enume = props.keys();
	    while (enume.hasMoreElements()) {
		String object = (String) enume.nextElement();
		_propertyHashMap.put(object, props.getProperty(object));
	    }

	} catch (FileNotFoundException e) {
	    Log.error("Error saving settings.", e);
	} catch (IOException e) {
	    Log.error("Error saving settings.", e);
	}

    }

    public static HashMap<String, String> getDefaultColors() {
	HashMap<String, String> hashmap = new HashMap<>();

	Enumeration<String> enu = Default.getAllKeys();
	while (enu.hasMoreElements()) {
	    String s = enu.nextElement();

	    if (Default.getString(s).replace(" ", "")
		    .matches("[0-9]*,[0-9]*,[0-9]*,[0-9]*")) {

		hashmap.put(s, Default.getString(s));
	    }

	}

	return hashmap;
    }

    /**
     * Used to set the Default values
     * 
     * @param props
     */
    private static void initialLoad(Properties props) {

	HashMap<String, String> map = getDefaultColors();
	for (String key : map.keySet()) {
	    props.setProperty(key, map.get(key));

	}

	try {
	    props.store(new FileOutputStream(getSettingsFile()),
		    "Storing Spark Color Settings");
	} catch (FileNotFoundException e) {
	    Log.error("Error saving settings.", e);
	} catch (IOException e) {
	    Log.error("Error saving settings.", e);
	}

    }

    /**
     * Restores the Default Settings
     */
    public static void restoreDefault() {	
	_propertyHashMap = getDefaultColors();
	saveColorSettings();
    }
}
