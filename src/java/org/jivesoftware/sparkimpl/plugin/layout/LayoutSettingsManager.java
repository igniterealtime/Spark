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

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Responsbile for the loading and persisting of LocalSettings.
 */
public class LayoutSettingsManager {
    private static LayoutSettings layoutSettings;

    private LayoutSettingsManager() {
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
            layoutSettings = load(settingsFile);
        }
        return layoutSettings;
    }

    /**
     * Persists the settings to the local file system.
     */
    public static void saveLayoutSettings() {
        final Properties props = new Properties();

        String mainWindowX = Integer.toString(layoutSettings.getMainWindowX());
        String mainWindowY = Integer.toString(layoutSettings.getMainWindowY());
        String mainWindowHeight = Integer.toString(layoutSettings.getMainWindowHeight());
        String mainWindowWidth = Integer.toString(layoutSettings.getMainWindowWidth());
        String chatFrameX = Integer.toString(layoutSettings.getChatFrameX());
        String chatFrameY = Integer.toString(layoutSettings.getChatFrameY());
        String chatFrameWidth = Integer.toString(layoutSettings.getChatFrameWidth());
        String chatFrameHeight = Integer.toString(layoutSettings.getChatFrameHeight());
        String splitDividerLocation = Integer.toString(layoutSettings.getSplitPaneDividerLocation());
        
        props.setProperty("mainWindowX", mainWindowX);
        props.setProperty("mainWindowY", mainWindowY);
        props.setProperty("mainWindowHeight", mainWindowHeight);
       	props.setProperty("mainWindowWidth", mainWindowWidth);
        
        props.setProperty("chatFrameX", chatFrameX);
        props.setProperty("chatFrameY", chatFrameY);
        props.setProperty("chatFrameWidth", chatFrameWidth);
        props.setProperty("chatFrameHeight", chatFrameHeight);
        
        props.setProperty("splitDividerLocation", splitDividerLocation);
        
        try {
            props.store(new FileOutputStream(getSettingsFile()), "Storing Spark Layout Settings");
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
        File file = new File(Spark.getUserSparkHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "layout.settings");
    }

    private static LayoutSettings load(File file) {
        final Properties props = new Properties();
        try {
            props.load(new FileInputStream(file));


            LayoutSettings settings = null;
            String mainWindowX = props.getProperty("mainWindowX");
            String mainWindowY = props.getProperty("mainWindowY");
            String mainWindowHeight = props.getProperty("mainWindowHeight");
            String mainWindowWidth = props.getProperty("mainWindowWidth");
            String chatFrameX = props.getProperty("chatFrameX");
            String chatFrameY = props.getProperty("chatFrameY");
            String chatFrameWidth = props.getProperty("chatFrameWidth");
            String chatFrameHeight = props.getProperty("chatFrameHeight");
            String splitDividerLocation = props.getProperty("splitDividerLocation");
            
            settings = new LayoutSettings();

            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int height = (int)screenSize.getHeight();
            int width = (int)screenSize.getWidth();

            int mainWindowXInt = Integer.parseInt(mainWindowX);
            int mainWindowYInt = Integer.parseInt(mainWindowY);
            int mainWindowHeightInt = Integer.parseInt(mainWindowHeight);
            int mainWindowWidthInt = Integer.parseInt(mainWindowWidth);

            if (mainWindowXInt + mainWindowWidthInt > width) {
                mainWindowXInt = (width - mainWindowWidthInt) / 2;
            }

            if (mainWindowYInt + mainWindowHeightInt > height) {
                mainWindowYInt = (height - mainWindowHeightInt) / 2;
            }


            int chatFrameXInt = Integer.parseInt(chatFrameX);
            int chatFrameYInt = Integer.parseInt(chatFrameY);
            int chatFrameWidthInt = Integer.parseInt(chatFrameWidth);
            int chatFrameHeightInt = Integer.parseInt(chatFrameHeight);
            int splitDividerLocationInt = splitDividerLocation == null ? -1 : Integer.parseInt(splitDividerLocation);
            
            if (chatFrameXInt + chatFrameWidthInt > width) {
                chatFrameXInt = (width - chatFrameWidthInt) / 2;
            }

            if (chatFrameYInt + chatFrameHeightInt > height) {
                chatFrameYInt = (height - chatFrameHeightInt) / 2;
            }


            settings.setMainWindowX(mainWindowXInt);
            settings.setMainWindowY(mainWindowYInt);
            settings.setMainWindowHeight(mainWindowHeightInt);
            settings.setMainWindowWidth(mainWindowWidthInt);

            settings.setChatFrameX(chatFrameXInt);
            settings.setChatFrameY(chatFrameYInt);
            settings.setChatFrameWidth(chatFrameWidthInt);
            settings.setChatFrameHeight(chatFrameHeightInt);
            settings.setSplitPaneDividerLocation(splitDividerLocationInt);
            return settings;
        }
        catch (Exception e) {
            Log.error(e);
            return new LayoutSettings();
        }
    }

}
