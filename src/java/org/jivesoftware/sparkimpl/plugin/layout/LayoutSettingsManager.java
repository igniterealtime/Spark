/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.layout;

import com.thoughtworks.xstream.XStream;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Responsbile for the loading and persisting of LocalSettings.
 */
public class LayoutSettingsManager {
    private static LayoutSettings layoutSettings;
    private static XStream xstream = new XStream();

    private LayoutSettingsManager() {
    }

    static {
        xstream.alias("layout", LayoutSettings.class);
    }

    /**
     * Returns the LayoutSettings for this agent.
     *
     * @return the LayoutSettings for this agent.
     */
    public static LayoutSettings getLayoutSettings() {
        if (!exists() && layoutSettings == null) {
            layoutSettings = new LayoutSettings();
        }

        if (layoutSettings == null) {
            // Do Initial Load from FileSystem.
            File settingsFile = getSettingsFile();
            try {
                FileReader reader = new FileReader(settingsFile);
                layoutSettings = (LayoutSettings)xstream.fromXML(reader);
            }
            catch (Exception e) {
                xstream.alias("com.jivesoftware.settings.layout.LayoutSettings", LayoutSettings.class);
                try {
                    FileReader reader = new FileReader(settingsFile);
                    layoutSettings = (LayoutSettings)xstream.fromXML(reader);
                }
                catch (FileNotFoundException e1) {
                    Log.error("Error loading LayoutSettings.", e);
                    layoutSettings = new LayoutSettings();
                }
            }
            xstream.alias("layout", LayoutSettings.class);
        }
        return layoutSettings;
    }

    /**
     * Persists the settings to the local file system.
     */
    public static void saveLayoutSettings() {

        try {
            FileWriter writer = new FileWriter(getSettingsFile());
            xstream.toXML(layoutSettings, writer);
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
        return new File(file, "layout-settings.xml");
    }
}
