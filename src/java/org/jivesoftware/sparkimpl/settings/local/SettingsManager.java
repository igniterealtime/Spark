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

package org.jivesoftware.sparkimpl.settings.local;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.WinRegistry;
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

    private static List<PreferenceListener> listeners = new ArrayList<>();

    private static boolean fileExists = false;

    private SettingsManager() {
    }
    
    //should probably not be read in a separate call
    public static LocalPreferences getRelodLocalPreferences()
    {
    	getSettingsFile();
    	localPreferences = load();
    	return localPreferences;
    }

    /**
     * Returns the LocalPreferences for this user.
     *
     * @return the LocalPreferences for this user.
     */
    public static LocalPreferences getLocalPreferences() {
        if(localPreferences != null){
            return localPreferences;
        }

        if (!fileExists) {
            fileExists = exists();
        }

        if (!fileExists && localPreferences == null) {
            localPreferences = new LocalPreferences();
            saveSettings();
        }

        if (localPreferences == null) {
            // Do Initial Load from FileSystem.
            getSettingsFile();
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
        
        if (localPreferences.getStartOnStartup())
        {
        	try	{
        		if (Spark.isWindows())
        		{
        			String PROGDIR = Spark.getBinDirectory().getParent();
        			File file = new File(PROGDIR + "\\" + SparkRes.getString(SparkRes.EXECUTABLE_NAME));
        			if (file.exists())
        			{
		        		WinRegistry.createKey(
		        				WinRegistry.HKEY_CURRENT_USER, 
		        				"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run");
        				WinRegistry.writeStringValue(
        					WinRegistry.HKEY_CURRENT_USER, 
        					"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", 
        					SparkRes.getString(SparkRes.APP_NAME), 
        					file.getAbsolutePath());
        			}
        		}        	
        	} 
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        else
        {

    		if (Spark.isWindows())
    		{
            	try	{
            		String run = WinRegistry.readString(
            				WinRegistry.HKEY_CURRENT_USER, 
            				"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", 
            				SparkRes.getString(SparkRes.APP_NAME));
            		if (run != null)
            		{
	            		WinRegistry.deleteValue(
	            	          WinRegistry.HKEY_CURRENT_USER, 
	            	          "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", 
	            	          SparkRes.getString(SparkRes.APP_NAME));
            		}
            	}
            	catch (Exception e) {
            		Log.error("Can not delete registry entry",e);
            	}
    		}
        }
    }

    /**
     * Return true if the settings file exists.
     *
     * @return true if the settings file exists.('spark.properties')
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

        // Override with global settings file
        File globalSettingsFile = new File("spark.properties");
        if (globalSettingsFile.exists()) {
            try {
                props.load(new FileInputStream(globalSettingsFile));
            } catch (IOException e) {
                Log.error(e);
            }
        }

        return new LocalPreferences(props);
    }

    public static void addPreferenceListener(PreferenceListener listener) {
        listeners.add(listener);
    }

    public static void removePreferenceListener(PreferenceListener listener) {
        listeners.remove(listener);
    }

    public static void fireListeners()
    {
        for ( PreferenceListener listener : listeners )
        {
            try
            {
                listener.preferencesChanged( localPreferences );
            }
            catch ( Exception e )
            {
                Log.error( "A PreferenceListener (" + listener + ") threw an exception while processing a 'referencesChanged' event.", e );
            }
        }
    }
}
