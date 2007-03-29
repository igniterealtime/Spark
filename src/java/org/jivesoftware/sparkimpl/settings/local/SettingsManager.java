/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.settings.local;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Responsbile for the loading and persisting of LocalSettings.
 */
public class SettingsManager {
    private static LocalPreferences localPreferences;

    private static List<PreferenceListener> listeners = new ArrayList<PreferenceListener>();

    private SettingsManager() {
    }

    /**
     * Returns the LocalPreferences for this agent.
     *
     * @return the LocalPreferences for this agent.
     */
    public static LocalPreferences getLocalPreferences() {

        if (!exists() && localPreferences == null) {
            localPreferences = new LocalPreferences();
        }

        if (localPreferences == null) {
            // Do Initial Load from FileSystem.
            File settingsFile = getSettingsFile();
            localPreferences = load();
        }


        return localPreferences;
    }

    /**
     * Persists the settings to the local file system.
     */
    public static void saveSettings() {
        final Properties props = localPreferences.getProperties();

        try {
            props.store(new FileOutputStream(getSettingsFile()), "Spark Settings");
        }
        catch (Exception e) {
            Log.error("Error saving settings.", e);
        }

        fireListeners(localPreferences);
    }

    /**
     * Return true if the settings file exists.
     *
     * @return true if the settings file exists.('settings.xml')
     */
    public static boolean exists() {
        return getSettingsFile().exists();
    }

    /**
     * Returns the settings file.
     *
     * @return the settings file.
     */
    public static File getSettingsFile() {
        File file = new File(Spark.getUserSparkHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "spark.properties");
    }


    private static LocalPreferences load() {
        final Properties props = new Properties();
        try {
            props.load(new FileInputStream(getSettingsFile()));
        }
        catch (IOException e) {
            Log.error(e);
            return new LocalPreferences();
        }

        return new LocalPreferences(props);
    }

    public static void addPreferenceListener(PreferenceListener listener) {
        listeners.add(listener);
    }

    public static void removePreferenceListener(PreferenceListener listener) {
        listeners.remove(listener);
    }

    private static void fireListeners(LocalPreferences pref) {
        for (PreferenceListener listener : listeners) {
            listener.preferencesChanged(pref);
        }
    }
}
