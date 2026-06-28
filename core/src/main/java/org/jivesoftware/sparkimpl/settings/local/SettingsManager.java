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

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;


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
    private static void saveSettings() {
        if (localPreferences == null) {
            return;
        }
        Properties props = localPreferences.getProperties();
        if (props.isEmpty()) {
            // happens in tests, just silently leave
            return;
        }
        Log.debug("Saving settings...");
        try {
            props.store(Files.newOutputStream(getSettingsFile().toPath()), "Spark Settings");
        } catch (Exception e) {
            Log.error("Error saving settings.", e);
            return;
        }
        Log.debug("Settings saved");
    }

    /**
     * Returns the settings file.
     */
    private static File getSettingsFile() {
        return new File(Spark.getSparkUserHome(), "spark.properties");
    }

    private static LocalPreferences load() {
        final Properties props = new Properties();
        File settingsFile = getSettingsFile();
        if (settingsFile.exists()) {
            try {
                props.load(Files.newInputStream(settingsFile.toPath()));
            } catch (IOException e) {
                Log.error(e);
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

}
