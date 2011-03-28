/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
    
    private static HashMap<String,String> _propertyHashMap = new HashMap<String, String>();

    private static ColorSettings _colorsettings;
    public ColorSettingManager() {

    }
    
    /**
     * Returns the ColorSettingagent
     *
     * @return ColorSettingagent
     */
    public static ColorSettings getColorSettings() {
	File settings = getSettingsFile();
	_colorsettings = load(settings);
	return _colorsettings;
    }


    public static boolean exists() {
        return getSettingsFile().exists();
    }
    
    /**
     * Returns the file or creates it
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

        for(String key : _propertyHashMap.keySet())
        {
            String value =_propertyHashMap.get(key);
            props.setProperty(key, value);
        }     
        try {
            props.store(new FileOutputStream(getSettingsFile()), "Storing Spark Color Settings");
        }
        catch (Exception e) {
            Log.error("Error saving settings.", e);
        }
    }
    
    /**
     * load the Settingfile
     * @param file
     * @return
     */
    private static ColorSettings load(File file) {

	loadMap(file);
	if (_propertyHashMap.size() == 0) {

	    Properties p = new Properties();
	    try {
		p.load(new FileInputStream(getSettingsFile()));
		initialLoad(p);
		loadMap(file);
		
	    } catch (FileNotFoundException e) {
		Log.error("Error saving settings.", e);
	    } catch (IOException e) {
		Log.error("Error saving settings.", e);
	    }

	}

	ColorSettings settings = new ColorSettings(_propertyHashMap);

	return settings;

    }
    
    
    /**
     * loads all Properties into a HashMap
     * @param file
     */
    private static void loadMap(File file) {
	

	Properties props = new Properties();
	try {
		
	    props.load(new FileInputStream(file));

	    Enumeration<Object> xx2 = props.keys();
		while (xx2.hasMoreElements()) {
		    String object = (String) xx2.nextElement();
		    _propertyHashMap.put(object, props.getProperty(object));
		}

	} catch (FileNotFoundException e) {
	    Log.error("Error saving settings.", e);
	} catch (IOException e) {
	    Log.error("Error saving settings.", e);
	}


    }
    
 /**
  * Used to set the Default values
  * @param props
  */
    private static void initialLoad(Properties props)
    {
	
//	if(Default.getBoolean("USE_COLORS_FROM_BELOW"))
//	{
	    props.setProperty("ChatInput.SelectedTextColor", Default.getString("ChatInput.SelectedTextColor"));
	    props.setProperty("ChatInput.SelectionColor", Default.getString("ChatInput.SelectionColor" ));
	    props.setProperty("ContactItemNickname.foreground", Default.getString("ContactItemNickname.foreground"));
	    props.setProperty("ContactItemDescription.foreground", Default.getString("ContactItemDescription.foreground"));
	    props.setProperty("ContactItem.background", Default.getString("ContactItem.background"));
	    props.setProperty("ContactItemOffline.color", Default.getString("ContactItemOffline.color"));
	    props.setProperty("Link.foreground", Default.getString("Link.foreground"));
	    props.setProperty("List.selectionBackground", Default.getString("List.selectionBackground"));
	    props.setProperty("List.selectionForeground", Default.getString("List.selectionForeground"));
	    props.setProperty("List.selectionBorder", Default.getString("List.selectionBorder"));
	    props.setProperty("List.foreground", Default.getString("List.foreground"));
	    props.setProperty("List.background", Default.getString("List.background"));  
	    props.setProperty("TextField.lightforeground", Default.getString("TextField.lightforeground"));
	    props.setProperty("TextField.foreground", Default.getString("TextField.foreground"));
	    props.setProperty("TextField.caretForeground", Default.getString("TextField.caretForeground"));
	    props.setProperty("TextPane.foreground", Default.getString("TextPane.foreground"));
	    props.setProperty("TextPane.background", Default.getString("TextPane.background"));
	    props.setProperty("TextPane.inactiveForeground", Default.getString("TextPane.inactiveForeground"));
	    props.setProperty("TextPane.caretForeground", Default.getString("TextPane.caretForeground"));  
	    props.setProperty("MenuItem.selectionBackground", Default.getString("MenuItem.selectionBackground"));
	    props.setProperty("MenuItem.selectionForeground", Default.getString("MenuItem.selectionForeground"));
	    props.setProperty("Table.foreground", Default.getString("Table.foreground"));
	    props.setProperty("Table.background", Default.getString("Table.background"));
	    props.setProperty("Address.foreground", Default.getString("Address.foreground"));
	    props.setProperty("User.foreground", Default.getString("User.foreground"));
	    props.setProperty("OtherUser.foreground", Default.getString("OtherUser.foreground"));
	    props.setProperty("Notification.foreground", Default.getString("Notification.foreground"));
	    props.setProperty("Error.foreground", Default.getString("Error.foreground"));
	    props.setProperty("History.foreground", Default.getString("History.foreground"));
	    props.setProperty("SparkTabbedPane.startColor", Default.getString("SparkTabbedPane.startColor"));
	    props.setProperty("SparkTabbedPane.endColor", Default.getString("SparkTabbedPane.endColor"));
	    props.setProperty("SparkTabbedPane.borderColor", Default.getString("SparkTabbedPane.borderColor"));
	    
	    
//	}
//	else{
//	    // These are initial reference Values
//	    // if you want your own modify the default.properties file
//	    // and enable USE_CUSTOM_COLORS
//        	props.setProperty("TextField.lightforeground", "128,128,128,255");
//        	props.setProperty("TextField.foreground", "0,0,0,255");
//        	props.setProperty("TextField.caretForeground", "0,0,0,255");
//        	props.setProperty("List.selectionBackground", "217,232,250,255");
//        	props.setProperty("List.selectionForeground", "0,0,0,255");
//        	props.setProperty("List.selectionBorder", "187,195,215,255");
//        	props.setProperty("List.foreground", "0,0,0,255");
//        	props.setProperty("List.background", "255,255,255,255");
//        	props.setProperty("MenuItem.selectionBackground", "217,232,250,255");
//        	props.setProperty("MenuItem.selectionForeground", "0,0,0,255");
//        	props.setProperty("TextPane.foreground", "0,0,0,255");
//        	props.setProperty("TextPane.background", "255,255,255,0,255");
//        	props.setProperty("TextPane.inactiveForeground", "255,255,255,255");
//        	props.setProperty("TextPane.caretForeground", "0,0,0,255");
//        	props.setProperty("ChatInput.SelectedTextColor", "255,255,255,255");
//        	props.setProperty("ChatInput.SelectionColor", "0,0,255,255");
//        	props.setProperty("ContactItemNickname.foreground", "0,0,0,255");
//        	props.setProperty("ContactItemDescription.foreground", "128,128,128,255");
//        	props.setProperty("ContactItem.background", "240,243,253,0");
//        	props.setProperty("ContactItemOffline.color", "128,128,128,255");
//        	props.setProperty("Table.foreground", "0,0,0,255");
//        	props.setProperty("Table.background", "255,255,255,255");
//        	props.setProperty("Link.foreground", "0,0,255,255");
//        	props.setProperty("Address.foreground", "212,160,0,255");
//        	props.setProperty("User.foreground", "0,0,255,255");
//        	props.setProperty("OtherUser.foreground", "255,0,0,255");
//        	props.setProperty("Notification.foreground", "0,128,0,255");
//        	props.setProperty("Error.foreground", "255,0,0,255");
//        	props.setProperty("Question.foreground", "255,0,0,255");
//        	props.setProperty("History.foreground", "64,64,64,255");
//        	props.setProperty("SparkTabbedPane.startColor", "236,236,236,255");
//        	props.setProperty("SparkTabbedPane.endColor", "236,236,236,255");
//        	props.setProperty("SparkTabbedPane.borderColor", "192,192,192,255");
//	}
	try {
	    props.store(new FileOutputStream(getSettingsFile()), "Storing Spark Color Settings");
	} catch (FileNotFoundException e) {
	    Log.error("Error saving settings.", e);
	} catch (IOException e) {
	    Log.error("Error saving settings.", e);
	}

    }
}
