/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaLookAndFeel;

/**
 * In many cases, you will need to know the structure of the Spark installation, such as the directory structures, what
 * type of system Spark is running on, and also the arguments which were passed into Spark on startup. The <code>Spark</code>
 * class provides some simple static calls to retrieve this information.
 *
 * @version 1.0, 11/17/2005
 */
public final class Spark {

    private static final String USER_SPARK_HOME = System.getenv("APPDATA") != null && !System.getenv("APPDATA").equals("") 
    													?  System.getenv("APPDATA") + "/" + getUserConf()  
    													:  System.getProperties().getProperty("user.home") + "/" + getUserConf();

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

    public void startup() {
        String current = System.getProperty("java.library.path");
        String classPath = System.getProperty("java.class.path");

        // Set UIManager properties for JTree
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        /** Update Library Path **/
        StringBuffer buf = new StringBuffer();
        buf.append(current);
        buf.append(";");
        
        RESOURCE_DIRECTORY = new File(USER_SPARK_HOME, "/resources").getAbsoluteFile();
        if(!RESOURCE_DIRECTORY.exists()){
        	
        	RESOURCE_DIRECTORY.mkdirs();
        }
        BIN_DIRECTORY = new File(USER_SPARK_HOME, "/bin").getAbsoluteFile();
        if(!BIN_DIRECTORY.exists()){
        	
        	BIN_DIRECTORY.mkdirs();
        }
        LOG_DIRECTORY = new File(USER_SPARK_HOME, "/logs").getAbsoluteFile();
        if (!LOG_DIRECTORY.exists()){
        	
        	LOG_DIRECTORY.mkdirs();
        }
        USER_DIRECTORY = new File(USER_SPARK_HOME, "/user").getAbsoluteFile();
        if(!USER_DIRECTORY.exists()){
        	
        	USER_DIRECTORY.mkdirs();
        }
        PLUGIN_DIRECTORY = new File(USER_SPARK_HOME, "/plugins").getAbsoluteFile();
        if(!PLUGIN_DIRECTORY.exists()){
        	
        	PLUGIN_DIRECTORY.mkdirs();
        }
        XTRA_DIRECTORY = new File(USER_SPARK_HOME, "/xtra").getAbsoluteFile();
        if(!XTRA_DIRECTORY.exists()){
        	
        	XTRA_DIRECTORY.mkdirs();
        	//TODO methode an richtige stelle setzen
        	//copyEmoticonFiles();

        }

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

            RESOURCE_DIRECTORY = new File(workingDir, "resources").getAbsoluteFile();
            BIN_DIRECTORY = new File(workingDir, "bin").getAbsoluteFile();
            File emoticons = new File(XTRA_DIRECTORY, "emoticons").getAbsoluteFile();
            if(!emoticons.exists()){
            	
            	//Copy emoticon files from install directory to the spark user home directory
            }
            
            LOG_DIRECTORY = new File(USER_SPARK_HOME, "/logs").getAbsoluteFile();
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

        final LocalPreferences preferences = SettingsManager.getLocalPreferences();
        boolean useSystemLookAndFeel = preferences.useSystemLookAndFeel();

        try {
            String classname = UIManager.getSystemLookAndFeelClassName();

            if (classname.indexOf("Windows") != -1) {
                try {
                    if (useSystemLookAndFeel) {
                        UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
                    }
                    else {
                        UIManager.setLookAndFeel(new SyntheticaBlueMoonLookAndFeel());
                        SyntheticaLookAndFeel.setFont("Dialog", 11);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (classname.indexOf("mac") != -1 || classname.indexOf("apple") != -1) {
                UIManager.setLookAndFeel(classname);
            }
            else {
                if (useSystemLookAndFeel) {
                    UIManager.setLookAndFeel(classname);
                }
                else {
                    UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.Plastic3DLookAndFeel());
                }
            }

            // Update install ui properties.
            installBaseUIProperties();
        }
        catch (Exception e) {
            Log.error(e);
        }


        buf.append(classPath);
        buf.append(";").append(RESOURCE_DIRECTORY.getAbsolutePath());

        // Update System Properties
        System.setProperty("java.library.path", buf.toString());


        System.setProperty("sun.java2d.noddraw", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Start Application
                new Spark();
            }
        });


        final LoginDialog dialog = new LoginDialog();
        dialog.invoke(new JFrame());
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
        return RESOURCE_DIRECTORY;
    }

    /**
     * Returns the log directory. The log directory contains all debugging and error files for Spark.
     *
     * @return the log directory.
     */
    public static File getLogDirectory() {
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
    private static String getUserConf() {
        if (isLinux()) {
            return ".Spark";
        }
        /*else if (isWindows()) {
            return "Spark";
        } (not needed really)*/

        return "Spark";
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

    public static boolean isCustomBuild() {
        return "true".equals(Default.getString("CUSTOM"));
    }

    public static void installBaseUIProperties() {
        UIManager.put("TextField.lightforeground", Color.gray);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("TextField.caretForeground", Color.black);
        UIManager.put("TextField.font", new Font("Dialog", Font.PLAIN, 11));

        UIManager.put("List.selectionBackground", new Color(217, 232, 250));
        UIManager.put("List.selectionForeground", Color.black);
        UIManager.put("List.selectionBorder", new Color(187, 195, 215));
        UIManager.put("List.foreground", Color.black);
        UIManager.put("List.background", Color.white);
        UIManager.put("MenuItem.selectionBackground", new Color(217, 232, 250));
        UIManager.put("MenuItem.selectionForeground", Color.black);
        UIManager.put("TextPane.foreground", Color.black);
        UIManager.put("TextPane.background", Color.white);
        UIManager.put("TextPane.inactiveForeground", Color.white);
        UIManager.put("TextPane.caretForeground", Color.black);
        UIManager.put("ChatInput.SelectedTextColor", Color.white);
        UIManager.put("ChatInput.SelectionColor", new Color(209, 223, 242));
        UIManager.put("ContactItemNickname.foreground", Color.black);
        UIManager.put("ContactItemDescription.foreground", Color.gray);
        UIManager.put("ContactItem.background", new Color(240, 243, 253));
        UIManager.put("ContactItem.border", BorderFactory.createLineBorder(Color.white));
        UIManager.put("ContactItemOffline.color", Color.gray);
        UIManager.put("Table.foreground", Color.black);
        UIManager.put("Table.background", Color.white);

        UIManager.put("Label.font", new Font("Dialog", Font.PLAIN, 11));

        // Chat Area Text Settings
        UIManager.put("Link.foreground", Color.blue);
        UIManager.put("Address.foreground", new Color(212, 160, 0));
        UIManager.put("User.foreground", Color.blue);
        UIManager.put("OtherUser.foreground", Color.red);
        UIManager.put("Notification.foreground", new Color(0, 128, 0));
        UIManager.put("Error.foreground", Color.red);
        UIManager.put("Question.foreground", Color.red);
        UIManager.put("History.foreground", Color.darkGray);

        UIManager.put("SparkTabbedPane.startColor", new Color(236, 236, 236));
        UIManager.put("SparkTabbedPane.endColor", new Color(236, 236, 236));
        UIManager.put("SparkTabbedPane.borderColor", Color.lightGray);
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
