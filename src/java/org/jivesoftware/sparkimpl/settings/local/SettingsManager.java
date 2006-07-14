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

import com.thoughtworks.xstream.XStream;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


/**
 * Responsbile for the loading and persisting of LocalSettings.
 */
public class SettingsManager {
    private static LocalPreferences localPreferences;
    private static XStream xstream = new XStream();

    private SettingsManager() {
    }

    static {
        xstream.alias("settings", LocalPreferences.class);
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
            FileReader reader = null;

            try {
                reader = new FileReader(settingsFile);
                localPreferences = (LocalPreferences)xstream.fromXML(reader);
            }
            catch (Exception e) {
                xstream.alias("com.jivesoftware.settings.local.LocalPreferences", LocalPreferences.class);
                try {
                    reader = new FileReader(settingsFile);
                    localPreferences = (LocalPreferences)xstream.fromXML(reader);
                }
                catch (Exception e1) {
                    Log.error("Error loading LocalPreferences.", e);
                    localPreferences = new LocalPreferences();
                }
            }
            xstream.alias("settings", LocalPreferences.class);
        }


        return localPreferences;
    }

    /**
     * Persists the settings to the local file system.
     */
    public static void saveSettings() {
        try {
            FileWriter writer = new FileWriter(getSettingsFile());
            xstream.toXML(localPreferences, writer);
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
        return new File(file, "settings.xml");
    }
}
