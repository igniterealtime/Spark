/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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
package org.jivesoftware.sparkimpl.plugin.layout;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

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
        String preferencesFrameX = Integer.toString(layoutSettings.getPreferencesFrameX());
        String preferencesFrameY = Integer.toString(layoutSettings.getPreferencesFrameY());
        String preferencesFrameWidth = Integer.toString(layoutSettings.getPreferencesFrameWidth());
        String preferencesFrameHeight = Integer.toString(layoutSettings.getPreferencesFrameHeight());
        
        props.setProperty("mainWindowX", mainWindowX);
        props.setProperty("mainWindowY", mainWindowY);
        props.setProperty("mainWindowHeight", mainWindowHeight);
        props.setProperty("mainWindowWidth", mainWindowWidth);

        props.setProperty("chatFrameX", chatFrameX);
        props.setProperty("chatFrameY", chatFrameY);
        props.setProperty("chatFrameWidth", chatFrameWidth);
        props.setProperty("chatFrameHeight", chatFrameHeight);

        props.setProperty("preferencesFrameX", preferencesFrameX);
        props.setProperty("preferencesFrameY", preferencesFrameY);
        props.setProperty("preferencesFrameWidth", preferencesFrameWidth);
        props.setProperty("preferencesFrameHeight", preferencesFrameHeight);
        
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
        File file = new File(Spark.getSparkUserHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "layout.settings");
    }

    private static LayoutSettings load(File file) {
        final Properties props = new Properties();
        try {
            props.load(new FileInputStream(file));


            LayoutSettings settings;
            String mainWindowX = props.getProperty("mainWindowX");
            String mainWindowY = props.getProperty("mainWindowY");
            String mainWindowHeight = props.getProperty("mainWindowHeight");
            String mainWindowWidth = props.getProperty("mainWindowWidth");
            String chatFrameX = props.getProperty("chatFrameX");
            String chatFrameY = props.getProperty("chatFrameY");
            String chatFrameWidth = props.getProperty("chatFrameWidth");
            String chatFrameHeight = props.getProperty("chatFrameHeight");
            String splitDividerLocation = props.getProperty("splitDividerLocation");
            String preferencesFrameX = props.getProperty("preferencesFrameX");
            String preferencesFrameY = props.getProperty("preferencesFrameY");
            String preferencesFrameWidth = props.getProperty("preferencesFrameWidth");
            String preferencesFrameHeight = props.getProperty("preferencesFrameHeight");
            
            settings = new LayoutSettings();

            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int height = (int)screenSize.getHeight();
            int width = (int)screenSize.getWidth();

            int mainWindowXInt = Integer.parseInt(mainWindowX);
            int mainWindowYInt = Integer.parseInt(mainWindowY);
            int mainWindowHeightInt = Integer.parseInt(mainWindowHeight);
            int mainWindowWidthInt = Integer.parseInt(mainWindowWidth);

            if (!isValidWindowPosition(mainWindowXInt, mainWindowYInt,
					mainWindowWidthInt, mainWindowHeightInt)) {
                mainWindowXInt = (width - mainWindowWidthInt) / 2;
                mainWindowYInt = (height - mainWindowHeightInt) / 2;
            }
            
            int chatFrameXInt = Integer.parseInt(chatFrameX);
            int chatFrameYInt = Integer.parseInt(chatFrameY);
            int chatFrameWidthInt = Integer.parseInt(chatFrameWidth);
            int chatFrameHeightInt = Integer.parseInt(chatFrameHeight);
            
            if (!isValidWindowPosition(chatFrameXInt, chatFrameYInt,
            		chatFrameWidthInt, chatFrameHeightInt)) {
            	chatFrameXInt = (width - chatFrameWidthInt) / 2;
            	chatFrameYInt = (height - chatFrameHeightInt) / 2;
            }
            
            int preferencesFrameXInt = preferencesFrameX == null ? -1 : Integer.parseInt(preferencesFrameX);
            int preferencesFrameYInt = preferencesFrameY == null ? -1 : Integer.parseInt(preferencesFrameY);
            int preferencesFrameWidthInt = preferencesFrameWidth == null ? -1 : Integer.parseInt(preferencesFrameWidth);
            int preferencesFrameHeightInt = preferencesFrameHeight == null ? -1 : Integer.parseInt(preferencesFrameHeight);
            
            if (!isValidWindowPosition(preferencesFrameXInt, preferencesFrameYInt,
            	preferencesFrameWidthInt, preferencesFrameHeightInt)) {
            	preferencesFrameXInt = (width - preferencesFrameWidthInt) / 2;
            	preferencesFrameYInt = (height - preferencesFrameHeightInt) / 2;
            }
            
            int splitDividerLocationInt = splitDividerLocation == null ? -1 : Integer.parseInt(splitDividerLocation);

            if (chatFrameHeightInt < 100) {
                chatFrameHeightInt = 100;
            }
            if (chatFrameWidthInt < 100) {
            	chatFrameWidthInt = 100;
            }
            if (preferencesFrameWidthInt < 600) {
            	preferencesFrameWidthInt = 600;
            }
            if (preferencesFrameHeightInt < 600) {
            	preferencesFrameHeightInt = 600;
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
            
            settings.setPreferencesFrameX(preferencesFrameXInt);
            settings.setPreferencesFrameY(preferencesFrameYInt);
            settings.setPreferencesFrameWidth(preferencesFrameWidthInt);
            settings.setPreferencesFrameHeight(preferencesFrameHeightInt);
            
            return settings;
        }
        catch (Exception e) {
            Log.error(e);
            return new LayoutSettings();
        }
    }
    
    protected static boolean isValidWindowPosition(int x, int y, int width, int height) {
    	Rectangle windowTitleBounds = new Rectangle(x,y,width,20);
        double windowTitleArea = windowTitleBounds.getWidth() * windowTitleBounds.getHeight();
        
        Rectangle[] screenBounds = GraphicUtils.getScreenBounds();
        for (int i = 0; i < screenBounds.length; i++) {
           	Rectangle screen = screenBounds[i].getBounds();
        	Rectangle intersection = screen.intersection(windowTitleBounds);
        	double visibleArea = intersection.getWidth() * intersection.getHeight();
        	
        	// if 25% of it is visible in the device, then it is good
        	if ((visibleArea/windowTitleArea) > 0.25)
        		return true;
        	
        }
        
        return false;
    }
    
    /**
     * converts a Rectangle to a String
     * @param r
     * @return
     */
    public static String rectangleToString(Rectangle r) {
	return r.x + "," + r.y + "," + r.width + "," + r.height;
    }

    /**
     * converts a String to a Rectangle
     * @param s
     * @return
     */
    public static Rectangle stringToRectangle(String s) {

	if(s == null)
	{
	    return new Rectangle(0,0,0,0);
	}
	
	if (!s.matches("[0-9]*,[0-9]*,[0-9]*,[0-9]*")) {
	    return new Rectangle(0,0,0,0);
	} else {
	    String[] arr = s.split(",");
	    

	    return new Rectangle(Integer.parseInt(arr[0]),
		    Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),
		    Integer.parseInt(arr[3]));
	}

    }

}
