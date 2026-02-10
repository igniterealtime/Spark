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

package org.jivesoftware.sparkimpl.settings.local;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Responsible for the loading and persisting of LocalSettings.
 */
public class SettingsManager {
    private static LocalPreferences localPreferences;

    private static final CopyOnWriteArrayList<PreferenceListener> listeners = new CopyOnWriteArrayList<>();

    private SettingsManager() {
    }

    static {
        // always save settings on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(SettingsManager::saveSettings));
    }

    /**
     * Returns the LocalPreferences for this user.
     */
    public synchronized static LocalPreferences getLocalPreferences() {
        if (localPreferences != null) {
            return localPreferences;
        }
        // Do Initial Load from FileSystem.
        localPreferences = load();
        return localPreferences;
    }

    /**
     * Persists the settings to the local file system.
     */
    public static void saveSettings() {
        if (localPreferences == null) {
            return;
        }
        Log.debug("Saving settings...");
        final Properties props = localPreferences.getProperties();
        try {
            props.store(Files.newOutputStream(getSettingsFile().toPath()), "Spark Settings");
        } catch (Exception e) {
            Log.error("Error saving settings.", e);
        }
        setUpAutostart(localPreferences.getStartOnStartup());
        Log.debug("Settings saved");
    }

    /**
     * Returns the settings file.
     *
     * @return the settings file.
     */
    public static File getSettingsFile() {
        File file = new File(Spark.getSparkUserHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "spark.properties");
    }


    private static LocalPreferences load() {
        final Properties props = new Properties();
        File settingsFile = getSettingsFile();
        if (settingsFile.exists()) {
            try {
                props.load(Files.newInputStream(settingsFile.toPath()));
            } catch (IOException e) {
                Log.error(e);
                return new LocalPreferences();
            }
        }

        // Override with global settings file
        File globalSettingsFile = new File("spark.properties");
        if (globalSettingsFile.exists()) {
            try {
                props.load(Files.newInputStream(globalSettingsFile.toPath()));
            } catch (IOException e) {
                Log.error(e);
            }
        }

        return new LocalPreferences(props);
    }

    public static void addPreferenceListener(PreferenceListener listener) {
        listeners.addIfAbsent(listener);
    }

    public static void removePreferenceListener(PreferenceListener listener) {
        listeners.remove(listener);
    }

    public static void fireListeners() {
        for (PreferenceListener listener : listeners) {
            try {
                listener.preferencesChanged(localPreferences);
            } catch (Exception e) {
                Log.error("A PreferenceListener (" + listener + ") threw an exception while processing a 'referencesChanged' event.", e);
            }
        }
    }


    private static void setUpAutostart(boolean startOnStartup) {
        if (localPreferences.getStartOnStartup()) {
            if (Spark.isWindows()) {
                addToAutostartWindows();
            }
        } else {
            if (Spark.isWindows()) {
                removeFromAutostartWindows();
            }
        }
    }

    private static void addToAutostartWindows() {
        try {
            // Persists autostart via Windows registry, if executable exists
            String PROGDIR = Spark.getBinDirectory().getParent();
            File exeFile = new File(PROGDIR + "\\" + SparkRes.getString(SparkRes.EXECUTABLE_NAME));
            if (exeFile.exists()) {
                Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER,
                    "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run");
                Advapi32Util.registrySetStringValue(
                    WinReg.HKEY_CURRENT_USER,
                    "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                    SparkRes.getString(SparkRes.APP_NAME),
                    exeFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.error("Error enabling start on reboot", e);
        }
    }

    private static void removeFromAutostartWindows() {
        if (!Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER,
            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
            SparkRes.getString(SparkRes.APP_NAME))) {
            return;
        }
        try {
            Advapi32Util.registryDeleteValue(
                WinReg.HKEY_CURRENT_USER,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                SparkRes.getString(SparkRes.APP_NAME));
        } catch (Exception e) {
            Log.error("Can not delete registry entry", e);
        }
    }
}
