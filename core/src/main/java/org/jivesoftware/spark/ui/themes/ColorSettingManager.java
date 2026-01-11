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
package org.jivesoftware.spark.ui.themes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.util.log.Log;

public class ColorSettingManager {

    private static HashMap<String, String> _propertyHashMap = new HashMap<>();

    private ColorSettingManager() {
    }

    /**
     * Returns the ColorSettings
     */
    public static ColorSettings getColorSettings() {
        return loadSettings(getSettingsFile());
    }

    /**
     * Returns the file or creates it
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
        props.putAll(_propertyHashMap);
        try {
            props.store(new FileOutputStream(getSettingsFile()),
                "Storing Spark Color Settings");
        } catch (Exception e) {
            Log.error("Error saving settings.", e);
        }
    }

    /**
     * load the Settings file
     */
    private static ColorSettings loadSettings(File file) {
        loadSettingsToMap(file);
        Map<String, String> defaultColors = getDefaultColors();

        // Loads defaults if empty; reconciles against defaults otherwise
        if (_propertyHashMap.isEmpty()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(getSettingsFile()));
                initialLoad(p);
                loadSettingsToMap(file);
            } catch (IOException e) {
                Log.error("Error saving settings.", e);
            }
        } else if (_propertyHashMap.size() != defaultColors.size()) {
            // add missing settings from defaults
            defaultColors.forEach(_propertyHashMap::putIfAbsent);
            saveColorSettings();
        }
        return new ColorSettings(_propertyHashMap);
    }

    /**
     * loads all Properties into a HashMap from the File specified
     */
    private static void loadSettingsToMap(File file) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(file));
        } catch (IOException e) {
            Log.error("Error loading color settings.", e);
            return;
        }
        Enumeration<Object> enume = props.keys();
        while (enume.hasMoreElements()) {
            String propName = (String) enume.nextElement();
            _propertyHashMap.put(propName, props.getProperty(propName));
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
     */
    private static void initialLoad(Properties props) {
        HashMap<String, String> map = getDefaultColors();
        props.putAll(map);
        try {
            props.store(new FileOutputStream(getSettingsFile()), "Storing Spark Color Settings");
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
