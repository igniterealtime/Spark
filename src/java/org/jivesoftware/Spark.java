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


package org.jivesoftware;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.PluginManager;
import org.jivesoftware.spark.ui.themes.ColorSettingManager;
import org.jivesoftware.spark.ui.themes.ColorSettings;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;



/**
 * In many cases, you will need to know the structure of the Spark installation, such as the directory structures, what
 * type of system Spark is running on, and also the arguments which were passed into Spark on startup. The <code>Spark</code>
 * class provides some simple static calls to retrieve this information.
 *
 * @version 1.0, 11/17/2005
 */
public final class Spark {



    private static String USER_SPARK_HOME;



    public static String ARGUMENTS;

    private static File RESOURCE_DIRECTORY;
    private static File BIN_DIRECTORY;
    private static File LOG_DIRECTORY;
    private static File USER_DIRECTORY;
    private static File PLUGIN_DIRECTORY;
    private static File XTRA_DIRECTORY;


    /**
     * Private constructor that invokes the LoginDialog and
     * the Spark Main Application.
     */
    public Spark() {

    }

    private static synchronized File initializeDirectory(File directoryHome, String directoryName){
    	File targetDir = new File(directoryHome, directoryName).getAbsoluteFile();
        if(!targetDir.exists()){
        	targetDir.mkdirs();
        }
        return targetDir;
    }
    
    
    private static synchronized File initializeDirectory(String directoryName){
    	return initializeDirectory(new File(USER_SPARK_HOME), directoryName);
    }
    
    public void startup() {
	if (System.getenv("APPDATA") != null && !System.getenv("APPDATA").equals("")) {
	    USER_SPARK_HOME = System.getenv("APPDATA") + "/" + getUserConf();
	} else {
	    USER_SPARK_HOME = System.getProperties().getProperty("user.home") + "/" + getUserConf();
	}

        String current = System.getProperty("java.library.path");
        String classPath = System.getProperty("java.class.path");

        // Set UIManager properties for JTree
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        /** Update Library Path **/
        StringBuffer buf = new StringBuffer();
        buf.append(current);
        buf.append(";");

    	SparkCompatibility sparkCompat = new SparkCompatibility();
    	try {
    		// Absolute paths to a collection of files or directories to skip
			Collection<String> skipFiles = new HashSet<String>();
			skipFiles.add(new File(USER_SPARK_HOME, "plugins").getAbsolutePath());

    		sparkCompat.transferConfig(USER_SPARK_HOME, skipFiles);
    	} catch (IOException e) {
    		// Do nothing
    	}

    	
    	RESOURCE_DIRECTORY = initializeDirectory("resources");
    	BIN_DIRECTORY = initializeDirectory("bin");
    	LOG_DIRECTORY = initializeDirectory("logs");
    	USER_DIRECTORY = initializeDirectory("user");
    	PLUGIN_DIRECTORY = initializeDirectory("plugins");
    	XTRA_DIRECTORY = initializeDirectory("xtra");
    	// TODO implement copyEmoticonFiles();
        final String workingDirectory = System.getProperty("appdir");
        
        if (workingDirectory == null) {

            if (!RESOURCE_DIRECTORY.exists() || !LOG_DIRECTORY.exists() || !USER_DIRECTORY.exists() || !PLUGIN_DIRECTORY.exists() || !XTRA_DIRECTORY.exists()) {
                JOptionPane.showMessageDialog(new JFrame(), "Unable to create directories necessary for runtime.", "Spark Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        // This is the Spark.exe or Spark.dmg installed executable.

        else {
            // This is the installed executable.
            File workingDir = new File(workingDirectory);
            RESOURCE_DIRECTORY = initializeDirectory(workingDir, "resources");
            BIN_DIRECTORY = initializeDirectory(workingDir, "bin");
            File emoticons = new File(XTRA_DIRECTORY, "emoticons").getAbsoluteFile();
            if(!emoticons.exists()){

            	//Copy emoticon files from install directory to the spark user home directory
            }
            
            LOG_DIRECTORY = initializeDirectory("logs");
            LOG_DIRECTORY = new File(USER_SPARK_HOME, "logs").getAbsoluteFile();
            LOG_DIRECTORY.mkdirs();
            try {
                buf.append(RESOURCE_DIRECTORY.getCanonicalPath()).append(";");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Set default language set by the user.
        loadLanguage();

        /**
         * Loads the LookandFeel
         */
        loadLookAndFeel();


        buf.append(classPath);
        buf.append(";").append(RESOURCE_DIRECTORY.getAbsolutePath());

        // Update System Properties
        System.setProperty("java.library.path", buf.toString());


        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("file.encoding", "UTF-8");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Start Application
                new Spark();
            }
        });

        //load plugins before Workspace initialization to avoid any UI delays
        //during plugin rendering
        final PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.loadPlugins();

        installBaseUIProperties();

        if (Default.getBoolean("CHANGE_COLORS_DISABLED")) {
            ColorSettingManager.restoreDefault();
        }

        try {
	        EventQueue.invokeAndWait(new Runnable(){
	        	public void run() {
				final LoginDialog dialog = UIComponentRegistry.createLoginDialog();
	        		dialog.invoke(new JFrame());
	        	}
	        });
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
    }

    private String getLookandFeel(LocalPreferences preferences) {
	String result = "";

	String whereToLook = isMac() ? Default.DEFAULT_LOOK_AND_FEEL_MAC
		: Default.DEFAULT_LOOK_AND_FEEL;

	if (!Default.getBoolean(Default.LOOK_AND_FEEL_DISABLED)) {
	    result = preferences.getLookAndFeel();
	} else if (Default.getString(whereToLook).length() > 0) {
	    result = Default.getString(whereToLook);
	} else {
	    result = UIManager.getSystemLookAndFeelClassName();
	}

	return result;

    }

    /**
     * Handles the Loading of the Look And Feel
     */
    private void loadLookAndFeel() {
	final LocalPreferences preferences = SettingsManager.getLocalPreferences();
	final String laf = getLookandFeel(preferences);

	try {
	    if (laf.toLowerCase().contains("substance")) {
		EventQueue.invokeLater(new Runnable() {
		    public void run() {
			try {
			    if (Spark.isWindows()) {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
			    }
			    UIManager.setLookAndFeel(laf);
			} catch (Exception e) {
			    // dont care
			    e.printStackTrace();
			}
		    }
		});
	    } else {
		try {
		    if(Spark.isWindows()) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		    }
		    UIManager.setLookAndFeel(laf);


		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	} catch (Exception e) {
	    Log.error(e);
	}

	if (laf.contains("jtattoo")) {
	    Properties props = new Properties();
	    String menubar = Default.getString(Default.MENUBAR_TEXT) == null ? ""
		    : Default.getString(Default.MENUBAR_TEXT);
	    props.put("logoString", menubar);
	    try {
		Class<?> c = ClassLoader.getSystemClassLoader().loadClass(laf);
		Method m = c.getMethod("setCurrentTheme", Properties.class);
		m.invoke(c.newInstance(), props);
	    } catch (Exception e) {
		Log.error("Error Setting JTattoo ", e);
	    }
	}
    }


    // Setup the look and feel of this application.
    static {
        com.install4j.api.launcher.StartupNotification.registerStartupListener(new SparkStartupListener());
    }

    /**
     * Return if we are running on windows.
     *
     * @return true if we are running on windows, false otherwise.
     */
    public static boolean isWindows() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("windows");
    }

    /**
     * Returns true if Spark is running on vista.
     *
     * @return true if running on Vista.
     */
    public static boolean isVista() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("vista");
    }

    /**
     * Return if we are running on a mac.
     *
     * @return true if we are running on a mac, false otherwise.
     */
    public static boolean isMac() {
        String lcOSName = System.getProperty("os.name").toLowerCase();
        return lcOSName.indexOf("mac") != -1;
    }


    /**
     * Returns the value associated with a passed in argument. Spark
     * accepts HTTP style attributes to allow for name-value pairing.
     * ex. username=foo&password=pwd.
     * To retrieve the value of username, you would do the following:
     * <pre>
     * String value = Spark.getArgumentValue("username");
     * </pre>
     *
     * @param argumentName the name of the argument to retrieve.
     * @return the value of the argument. If no argument was found, null
     *         will be returned.
     */
    public static String getArgumentValue(String argumentName) {
        if (ARGUMENTS == null) {
            return null;
        }

        String arg = argumentName + "=";

        int index = ARGUMENTS.indexOf(arg);
        if (index == -1) {
            return null;
        }

        String value = ARGUMENTS.substring(index + arg.length());
        int index2 = value.indexOf("&");
        if (index2 != -1) {
            // Must be the last argument
            value = value.substring(0, index2);
        }


        return value;
    }

    public void setArgument(String arguments) {
        ARGUMENTS = arguments;
    }

    /**
     * Returns the bin directory of the Spark install. The bin directory contains the startup scripts needed
     * to start Spark.
     *
     * @return the bin directory.
     */
    public static File getBinDirectory() {
    	if (BIN_DIRECTORY == null ) BIN_DIRECTORY = initializeDirectory("bin");
    	return BIN_DIRECTORY;
    }

    /**
     * Returns the resource directory of the Spark install. The resource directory contains all native
     * libraries needed to run os specific operations, such as tray support. You may place other native
     * libraries within this directory if you wish to have them placed into the system.library.path.
     *
     * @return the resource directory.
     */
    public static File getResourceDirectory() {
    	if (RESOURCE_DIRECTORY == null ) RESOURCE_DIRECTORY = initializeDirectory("resources");
        return RESOURCE_DIRECTORY;
    }

    /**
     * Returns the log directory. The log directory contains all debugging and error files for Spark.
     *
     * @return the log directory.
     */
    public static File getLogDirectory() {
    	if (LOG_DIRECTORY == null )LOG_DIRECTORY = initializeDirectory("logs");
        return LOG_DIRECTORY;
    }

    /**
     * Return if we are running on Linux.
     *
     * @return true if we are running on Linux, false otherwise.
     */

    public static boolean isLinux() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("linux");
    }

    /**
     * Keep track of the users configuration directory.
     *
     * @return Directory name depending on Operating System.
     */
    public static String getUserConf() {
        if (isLinux()) {
            return Default.getString(Default.USER_DIRECTORY_LINUX);
        }
        else if(isMac())
        {
            return Default.getString(Default.USER_DIRECTORY_MAC);
        }
        else
            return Default.getString(Default.USER_DIRECTORY_WINDOWS);
    }


    /**
     * Returns the Spark directory for the current user (user.home). The user home is where all user specific
     * files are placed to run Spark within a multi-user system.
     *
     * @return the user home / Spark;
     */
    public static String getSparkUserHome() {
        return USER_SPARK_HOME;
    }

    /**
     * Return the base user home.
     *
     * @return the user home.
     */
    public static String getUserHome() {
        return System.getProperties().getProperty("user.home");
    }

    public static boolean disableUpdatesOnCustom() {
	return Default.getBoolean("DISABLE_UPDATES");
    }

	public static void setApplicationFont(Font f) {
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		synchronized(defaults) {
		    for (Object ui_property: defaults.keySet()) {
		        if (ui_property.toString().endsWith(".font")) {
		            UIManager.put(ui_property, f);
		        }
		    }
		}
	}

    /**
     * Sets Spark specific colors
     */
    public static void installBaseUIProperties() {
    	setApplicationFont(new Font("Dialog", Font.PLAIN, 11));
        UIManager.put("ContactItem.border", BorderFactory.createLineBorder(Color.white));
        //UIManager.put("TextField.font", new Font("Dialog", Font.PLAIN, 11));
        //UIManager.put("Label.font", new Font("Dialog", Font.PLAIN, 11));

        ColorSettings colorsettings = ColorSettingManager.getColorSettings();

        for(String property : colorsettings.getKeys())
        {
            Color c = colorsettings.getColorFromProperty(property);
            UIManager.put(property, c);
        }


    }

    /**
     * Loads the language set by the user. If no language is set, then the default implementation will be used.
     */
    private void loadLanguage() {
        final LocalPreferences preferences = SettingsManager.getLocalPreferences();
        final String setLanguage = preferences.getLanguage();
        if (ModelUtil.hasLength(setLanguage)) {
            Locale[] locales = Locale.getAvailableLocales();
            for (Locale locale : locales) {
                if (locale.toString().equals(setLanguage)) {
                    Locale.setDefault(locale);
                    break;
                }
            }
        }
    }
   /* public void copyEmoticonFiles() {
        // Current Plugin directory
        File newEmoticonDir = new File(Spark.getLogDirectory().getParentFile(), "xtra/emoticons").getAbsoluteFile();
        newEmoticonDir.mkdirs();
        //TODO emoticondirectory anpassen
        File EMOTICON_DIRECTORY = new File("D:/workspace/Spark 2.6 beta/src","xtra/emoticons");
        File[] files = EMOTICON_DIRECTORY.listFiles();


        for (File file : files) {
            if (file.isFile()) {

               // Copy over
               File newFile = new File(newEmoticonDir, file.getName());

            }
        }
    }*/
}
