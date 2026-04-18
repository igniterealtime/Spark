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


package org.jivesoftware;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.ui.themes.ColorSettingManager;
import org.jivesoftware.spark.ui.themes.ColorSettings;
import org.jivesoftware.spark.ui.themes.LookAndFeelManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.jivesoftware.gui.LoginUIPanel;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.SystemUtils.*;


/**
 * In many cases, you will need to know the structure of the Spark installation, such as the directory structures, what
 * type of system Spark is running on, and also the arguments which were passed into Spark on startup. The <code>Spark</code>
 * class provides some simple static calls to retrieve this information.
 */
public final class Spark {
    private static File USER_SPARK_HOME;
    private static Map<String, String> ARGUMENTS;
    private static File BIN_DIRECTORY;
    private static File LOG_DIRECTORY;
    private static File PLUGIN_DIRECTORY;
    private static File RESOURCE_DIRECTORY;
    private static File SECURITY_DIRECTORY;
    private static File USER_DIRECTORY;
    private static File XTRA_DIRECTORY;


    /**
     * Private constructor that invokes the LoginDialog and
     * the Spark Main Application.
     */
    public Spark() {
    }

    private static synchronized File initializeDirectory(File directoryHome, String directoryName) {
        File targetDir = new File(directoryHome, directoryName).getAbsoluteFile();
        if (!targetDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            targetDir.mkdirs();
        }
        return targetDir;
    }

    private static File initializeDirectory(String directoryName) {
        return initializeDirectory(USER_SPARK_HOME , directoryName);
    }

    public static void initializeFolders(Properties sysProps) {
        // On Windows get the Spark home folder from the %APPDATA% environment variable i.e. "%SystemDrive%\%Username%\LoggedInUser\AppData\Roaming\Spark"
        String appDataFolder = System.getenv("APPDATA");
        if (appDataFolder == null || appDataFolder.isEmpty()) {
            appDataFolder = sysProps.getProperty("user.home");
        }
        USER_SPARK_HOME = new File(appDataFolder, getUserConf());
        BIN_DIRECTORY = initializeDirectory("bin");
        LOG_DIRECTORY = initializeDirectory("logs");
        PLUGIN_DIRECTORY = initializeDirectory("plugins");
        RESOURCE_DIRECTORY = initializeDirectory("resources");
        SECURITY_DIRECTORY = initializeDirectory("security");
        USER_DIRECTORY = initializeDirectory("user");
        XTRA_DIRECTORY = initializeDirectory("xtra");
        // Get installation folder e.g. /opt/Spark
        final String workingDirectory = sysProps.getProperty("appdir");
        if (workingDirectory == null) {
            System.out.println("Warning: no working directory set. This might cause updated data to be missed. Please set a system property 'appdir' to the location where Spark is installed to correct this.");
            // Terminate if required directories cannot be created
            if (!RESOURCE_DIRECTORY.exists() || !LOG_DIRECTORY.exists() || !USER_DIRECTORY.exists() || !PLUGIN_DIRECTORY.exists() || !XTRA_DIRECTORY.exists() || !SECURITY_DIRECTORY.exists()) {
                UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                JOptionPane.showMessageDialog(new JFrame(), "Unable to create directories necessary for runtime.", "Spark Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } else {
            // This is the Spark.exe or Spark.dmg installed executable.
            File workingDir = new File(workingDirectory);
            BIN_DIRECTORY = initializeDirectory(workingDir, "bin");
            PLUGIN_DIRECTORY = initializeDirectory(workingDir, "plugins");
            RESOURCE_DIRECTORY = initializeDirectory(workingDir, "resources");
            SECURITY_DIRECTORY = initializeDirectory(workingDir, "security");
            XTRA_DIRECTORY = initializeDirectory(workingDir, "xtra");
        }
    }

    /**
     * Configures environment; starts application; invokes login
     */
    public void startup() {
        Properties sysProps = System.getProperties();
        initializeFolders(sysProps);
        SparkCompatibility.transferConfig(USER_SPARK_HOME);

        // Set the default language set by the user
        loadLanguage();
        LookAndFeelManager.loadPreferredLookAndFeel();

        // Update Library Path
        String current = sysProps.getProperty("java.library.path");
        String classPath = sysProps.getProperty("java.class.path");
        String javaLibraryPath = current + File.pathSeparatorChar + classPath + File.pathSeparatorChar + RESOURCE_DIRECTORY.getAbsolutePath();

        // Update System Properties
        System.setProperty("java.library.path", javaLibraryPath);
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("file.encoding", "UTF-8");
        // macOS: Show main menu to the top of the window
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        installBaseUIProperties();
        if (Default.getBoolean(Default.CHANGE_COLORS_DISABLED)) {
            ColorSettingManager.restoreDefault();
        }

        try {
            EventQueue.invokeAndWait(() -> {
                final LoginUIPanel dialog = UIComponentRegistry.createLoginDialog();
                dialog.invoke();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Setup the look and feel of this application.
    static {
        com.install4j.api.launcher.StartupNotification.registerStartupListener(new SparkStartupListener());
    }

    public static boolean isWindows() {
        return IS_OS_WINDOWS;
    }

    public static boolean isMac() {
        return IS_OS_MAC;
    }

    public static boolean isLinux() {
        return IS_OS_LINUX;
    }

    /**
     * Returns the value associated with a passed in argument. Spark
     * accepts HTTP style attributes to allow for name-value pairing.
     * e.g. username=foo&password=pwd.
     * To retrieve the value of a username, you would do the following:
     * <pre>
     * String value = Spark.getArgumentValue("username");
     * </pre>
     *
     * @param argumentName the name of the argument to retrieve.
     * @return the value of the argument. If no argument was found, null will be returned.
     */
    public static String getArgumentValue(String argumentName) {
        return ARGUMENTS != null ? ARGUMENTS.get(argumentName) : null;
    }

    /**
     * Command line arguments
     */
    public static void setArguments(String[] args) {
        if (args.length == 0) {
            return;
        }
        Map<String, String> arguments = new HashMap<>(args.length);
        // check for old URL encoded style arguments e.g. username=juliet&server=capulet.lit&password=secret
        if (args.length == 1 && !args[0].startsWith("xmpp:") && !args[0].startsWith("--")) {
            // write to stderr because the log folder is not initialized yet
            System.err.println("Use of deprecated URL encoded argument. Instead use --username=LOGIN --server=DOMAIN --password=PASSWORD");
            String[] parts = args[0].split("&");
            for (String pair : parts) {
                int idx = pair.indexOf("=");
                if (idx == -1) {
                    continue;
                }
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                String val = URLDecoder.decode(value, UTF_8);
                arguments.put(key, val);
            }
        } else {
            // Convert agrs "--param1 --param2=val" to map[param1="", param2="val"]
            for (String arg : args) {
                if (arg.startsWith("--")) {
                    String pair = arg.substring(2);
                    int idx = pair.indexOf("=");
                    String value = "";
                    String key;
                    if (idx == -1) {
                        key = pair;
                    } else {
                        key = pair.substring(0, idx);
                        value = pair.substring(idx + 1);
                    }
                    arguments.put(key, value);
                }
            }
            // the last argument can be an XMPP URI
            String lastArg = args[args.length - 1].trim();
            if (lastArg.startsWith("xmpp:")) {
                arguments.put("uri", lastArg);
            }
        }
        ARGUMENTS = arguments;
    }

    /**
     * Returns the bin directory of the Spark install. The bin directory contains the startup scripts needed
     * to start Spark.
     *
     * @return the bin directory.
     */
    public static File getBinDirectory() {
        if (BIN_DIRECTORY == null) {
            BIN_DIRECTORY = initializeDirectory("bin");
        }
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
        if (RESOURCE_DIRECTORY == null) {
            RESOURCE_DIRECTORY = initializeDirectory("resources");
        }
        return RESOURCE_DIRECTORY;
    }

    /**
     * Returns the plugins directory of the Spark installation. THe plugins-dir contains all the third-party plugins.
     *
     * @return the plugins directory
     */
    public static File getPluginDirectory() {
        if (PLUGIN_DIRECTORY == null) {
            PLUGIN_DIRECTORY = initializeDirectory("plugins");
        }
        return PLUGIN_DIRECTORY;
    }

    /**
     * Returns the log directory. The log directory contains all debugging and error files for Spark.
     *
     * @return the log directory.
     */
    public static File getLogDirectory() {
        if (LOG_DIRECTORY == null) {
            LOG_DIRECTORY = initializeDirectory("logs");
        }
        return LOG_DIRECTORY;
    }

    public static File getXtraDirectory() {
        if (XTRA_DIRECTORY == null) {
            XTRA_DIRECTORY = initializeDirectory("xtra");
        }
        return XTRA_DIRECTORY;
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
        if (isMac()) {
            return Default.getString(Default.USER_DIRECTORY_MAC);
        }
        return Default.getString(Default.USER_DIRECTORY_WINDOWS);
    }


    /**
     * Returns the Spark directory for the current user (user.home). The user home is where all user specific
     * files are placed to run Spark within a multi-user system.
     *
     * @return the user home / Spark;
     */
    public static File getSparkUserHome() {
        return USER_SPARK_HOME;
    }

    public static boolean disableUpdatesOnCustom() {
        return Default.getBoolean(Default.DISABLE_UPDATES);
    }

    public static synchronized void setApplicationFont(Font f) {
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        for (Object ui_property : defaults.keySet()) {
            if (ui_property.toString().endsWith(".font")) {
                UIManager.put(ui_property, f);
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
        for (String property : colorsettings.getKeys()) {
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
            Locale userLocale = Locale.forLanguageTag(setLanguage.replace("_", "-"));
            Locale.setDefault(userLocale);
        }
    }
}
