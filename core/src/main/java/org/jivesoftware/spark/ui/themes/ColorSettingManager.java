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
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.util.log.Log;

public class ColorSettingManager {

    private static Map<String, String> colorSettings = new HashMap<>(0);

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
    private static File getSettingsFile() {
        File path = new File(Spark.getSparkUserHome());
        if (!path.exists()) {
            path.mkdirs();
        }
        File f = new File(path, "color.settings");
        return f;
    }

    /**
     * Save all settings
     */
    public static void saveColorSettings() {
        final Properties props = new Properties();
        props.putAll(colorSettings);
        try {
            props.store(Files.newOutputStream(getSettingsFile().toPath()), null);
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
        if (colorSettings.isEmpty()) {
            colorSettings = defaultColors;
        } else if (colorSettings.size() != defaultColors.size()) {
            // add missing settings from defaults
            defaultColors.forEach(colorSettings::putIfAbsent);
        }
        return new ColorSettings(colorSettings);
    }

    /**
     * loads all Properties into a HashMap from the File specified
     */
    private static void loadSettingsToMap(File file) {
        if (!file.exists()) {
            return;
        }
        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            Log.error("Error loading color settings.", e);
            return;
        }
        Enumeration<Object> enume = props.keys();
        while (enume.hasMoreElements()) {
            String propName = (String) enume.nextElement();
            colorSettings.put(propName, props.getProperty(propName));
        }
    }

    /**
     * Gets colors from default.properties
     */
    private static HashMap<String, String> getDefaultColors() {
        Matcher colorPatternMatcher = Pattern.compile("[0-9]*,[0-9]*,[0-9]*,[0-9]*").matcher("");
        HashMap<String, String> hashmap = new HashMap<>(40);
        Enumeration<String> enu = Default.getAllKeys();
        while (enu.hasMoreElements()) {
            String s = enu.nextElement();
            String propValue = Default.getString(s).replace(" ", "");
            colorPatternMatcher.reset(propValue);
            if (colorPatternMatcher.matches()) {
                hashmap.put(s, propValue);
            }
        }
        return hashmap;
    }

    /**
     * Restores the Default Settings
     */
    public static void restoreDefault() {
        colorSettings = getDefaultColors();
        saveColorSettings();
    }
}
