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

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.debugger.EnhancedDebuggerWindow;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * In many cases, you will need to know the structure of the Spark installation, such as the directory structures, what
 * type of system Spark is running on, and also the arguments which were passed into Spark on startup. The <code>Spark</code>
 * class provides some simple static calls to retrieve this information.
 *
 * @version 1.0, 11/17/2005
 */
public final class Spark {

    private static final String USER_HOME = System.getProperties().getProperty("user.home");
    private static String argument;

    private static File RESOURCE_DIRECTORY;
    private static File BIN_DIRECTORY;
    private static File LOG_DIRECTORY;


    /**
     * Private constructor that invokes the LoginDialog and
     * the Spark Main Application.
     */
    private Spark() {
        final LoginDialog dialog = new LoginDialog();
        dialog.invoke(new JFrame());
    }

    /**
     * Invocation method.
     *
     * @param args - Will receive arguments from Java Web Start.
     */
    public static void main(final String[] args) {
        EnhancedDebuggerWindow.PERSISTED_DEBUGGER = true;
        EnhancedDebuggerWindow.MAX_TABLE_ROWS = 10;
        XMPPConnection.DEBUG_ENABLED = true;


        String current = System.getProperty("java.library.path");
        String classPath = System.getProperty("java.class.path");

        // Set UIManager properties for JTree
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        /** Update Library Path **/
        StringBuffer buf = new StringBuffer();
        buf.append(current);
        buf.append(";");


        final String workingDirectory = System.getProperty("appdir");
        if (workingDirectory == null) {
            RESOURCE_DIRECTORY = new File(USER_HOME, "/Spark/resources").getAbsoluteFile();
            BIN_DIRECTORY = new File(USER_HOME, "/Spark/bin").getAbsoluteFile();
            LOG_DIRECTORY = new File(USER_HOME, "/Spark/logs").getAbsoluteFile();
            RESOURCE_DIRECTORY.mkdirs();
            LOG_DIRECTORY.mkdirs();
            if (!RESOURCE_DIRECTORY.exists() || !LOG_DIRECTORY.exists()) {
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


            LOG_DIRECTORY = new File(USER_HOME, "/Spark/logs").getAbsoluteFile();
            LOG_DIRECTORY.mkdirs();
            try {
                buf.append(RESOURCE_DIRECTORY.getCanonicalPath()).append(";");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


        buf.append(classPath);

        // Update System Properties
        System.setProperty("java.library.path", buf.toString());


        System.setProperty("sun.java2d.noddraw", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Start Application
                new Spark();
            }
        });

        // Handle arguments
        if (args.length > 0) {
            argument = args[0];
        }
    }


    // Setup the look and feel of this application.
    static {
        try {
            String classname = UIManager.getSystemLookAndFeelClassName();

            if (classname.indexOf("Windows") != -1) {
                UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
            }
            else if (classname.indexOf("mac") != -1 || classname.indexOf("apple") != -1) {
                UIManager.setLookAndFeel(classname);
            }
            else {
                UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.Plastic3DLookAndFeel());
            }

        }
        catch (Exception e) {
            Log.error(e);
        }

        UIManager.put("Tree.openIcon", SparkRes.getImageIcon(SparkRes.FOLDER));
        UIManager.put("Tree.closedIcon", SparkRes.getImageIcon(SparkRes.FOLDER_CLOSED));
        UIManager.put("Button.showMnemonics", Boolean.TRUE);
        UIManager.put("CollapsiblePane.titleFont", new Font("Dialog", Font.BOLD, 11));
        UIManager.put("DockableFrameTitlePane.font", new Font("Dialog", Font.BOLD, 10));
        UIManager.put("DockableFrame.inactiveTitleForeground", Color.white);
        UIManager.put("DockableFrame.inactiveTitleBackground", new Color(180, 176, 160));
        UIManager.put("DockableFrame.activeTitleBackground", new Color(105, 132, 188));
        UIManager.put("DockableFrame.activeTitleForeground", Color.white);
        UIManager.put("CollapsiblePane.background", Color.white);
        UIManager.put("TextField.font", new Font("Dialog", Font.PLAIN, 11));
        if (isWindows()) {
            UIManager.put("DockableFrameTitlePane.titleBarComponent", true);
        }
        else {
            UIManager.put("DockableFrameTitlePane.titleBarComponent", false);
        }

        UIManager.put("SidePane.lineColor", Color.BLACK);
        UIManager.put("SidePane.foreground", Color.BLACK);


        Color menuBarColor = new Color(255, 255, 255);//235, 233, 237);
        UIManager.put("MenuBar.background", menuBarColor);
        UIManager.put("JideTabbedPane.tabInsets", new Insets(3, 10, 3, 10));
        UIManager.put("JideTabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        installBaseUIProperties();

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
        if (argument == null) {
            return null;
        }

        String arg = argumentName + "=";

        int index = argument.indexOf(arg);
        if (index == -1) {
            return null;
        }

        String value = argument.substring(index + arg.length());
        int index2 = value.indexOf("&");
        if (index2 != -1) {
            // Must be the last argument
            value = value.substring(0, index2);
        }


        return value;
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
     * Returns the User specific directory for this Spark instance. The user home is where all user specific
     * files are placed to run Spark within a multi-user system.
     *
     * @return the user home;
     */
    public static String getUserHome() {
        return USER_HOME;
    }

    public static boolean isCustomBuild() {
        return "true".equals(Default.getString("CUSTOM"));
    }

    public static void installBaseUIProperties() {
        UIManager.put("TextField.lightforeground", Color.BLACK);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("TextField.caretForeground", Color.black);

        UIManager.put("List.selectionBackground", new Color(217, 232, 250));
        UIManager.put("List.selectionForeground", Color.black);
        UIManager.put("List.selectionBorder", new Color(187, 195, 215));
        UIManager.put("List.foreground", Color.black);
        UIManager.put("List.background", Color.white);
        UIManager.put("TextPane.foreground", Color.black);
        UIManager.put("TextPane.background", Color.white);
        UIManager.put("TextPane.inactiveForeground", Color.white);
        UIManager.put("TextPane.caretForeground", Color.black);
        UIManager.put("ChatInput.SelectedTextColor", Color.white);
        UIManager.put("ChatInput.SelectionColor", new Color(209, 223, 242));
        UIManager.put("ContactItemNickname.foreground", Color.black);
        UIManager.put("ContactItemDescription.foreground", Color.gray);
        UIManager.put("ContactItem.background", Color.white);
        UIManager.put("ContactItem.border", BorderFactory.createLineBorder(Color.white));
        UIManager.put("ContactItemOffline.color", Color.gray);
        UIManager.put("Table.foreground", Color.black);
        UIManager.put("Table.background", Color.white);

        UIManager.put("Label.font", new Font("Dialog", Font.PLAIN, 11));

        // Chat Area Text Settings
        UIManager.put("Link.foreground", Color.blue);
        UIManager.put("User.foreground", Color.blue);
        UIManager.put("OtherUser.foreground", Color.red);
        UIManager.put("Notification.foreground", new Color(51, 153, 51));
        UIManager.put("Error.foreground", Color.red);
        UIManager.put("Question.foreground", Color.red);
    }
}