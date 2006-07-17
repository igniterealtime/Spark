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
import java.util.Properties;


/**
 * Responsbile for the loading and persisting of LocalSettings.
 */
public class SettingsManager {
    private static LocalPreferences localPreferences;

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
            props.store(new FileOutputStream(getSettingsFile()), "Saving Spark Settings");
        }
        catch (Exception e) {
            Log.error("Error saving settings.", e);
        }
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
        File file = new File(Spark.getUserHome(), "Spark");
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "spark-settings.xml");
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
}
