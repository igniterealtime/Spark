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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import org.jivesoftware.launcher.Startup;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.RawPacketSender;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.InputTextAreaDialog;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettings;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.sparkimpl.updater.CheckUpdates;

/**
 * The <code>MainWindow</code> class acts as both the DockableHolder and the proxy
 * to the Workspace in Spark.
 *
 * @version 1.0, 03/12/14
 */
public final class MainWindow extends ChatFrame implements ActionListener {
	private static final long serialVersionUID = -6062104959613603510L;

	private final Set<MainWindowListener> listeners = new HashSet<MainWindowListener>();

    private final JMenu connectMenu = new JMenu();
    private final JMenu contactsMenu = new JMenu();
    private final JMenu actionsMenu = new JMenu();
    private final JMenu pluginsMenu = new JMenu();
    private final JMenu helpMenu = new JMenu();

    private JMenuItem preferenceMenuItem;
    private JCheckBoxMenuItem alwaysOnTopItem;

    private final JMenuItem menuAbout = new JMenuItem(SparkRes.getImageIcon(SparkRes.INFORMATION_IMAGE));
    private final JMenuItem sparkforumItem = new JMenuItem();

    private final JMenuBar mainWindowBar = new JMenuBar();

    private boolean focused;

    private JToolBar topToolbar = new JToolBar();

    private JSplitPane splitPane;

    private static MainWindow singleton;
    private static final Object LOCK = new Object();

    /**
     * Returns the singleton instance of <CODE>MainWindow</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>MainWindow</CODE>
     */
    public static MainWindow getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.


        synchronized (LOCK) {
            if (null == singleton) {
            	MainWindow controller = new MainWindow(Default.getString(Default.APPLICATION_NAME), SparkManager.getApplicationImage());
            	singleton = controller;
            }
        }
        return singleton;
    }


    /**
     * Constructs the UI for the MainWindow. The MainWindow UI is the container for the
     * entire Spark application.
     *
     * @param title the title of the frame.
     * @param icon  the icon used in the frame.
     */
    private MainWindow(String title, ImageIcon icon) {


        // Initialize and dock the menus
        buildMenu();



        // Add Workspace Container
        getContentPane().setLayout(new BorderLayout());

        LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();
        if (settings.getMainWindowX() == 0 && settings.getMainWindowY() == 0) {
            // Use default settings.
            setSize(300, 500);
            GraphicUtils.centerWindowOnScreen(this);
        }
        else {
            setBounds(settings.getMainWindowX(), settings.getMainWindowY(), settings.getMainWindowWidth(), settings.getMainWindowHeight());
        }

        // Add menubar
        this.setJMenuBar(mainWindowBar);
        this.getContentPane().add(topToolbar, BorderLayout.NORTH);

        setTitle(title);
        setIconImage(icon.getImage());



        // Setup WindowListener to be the proxy to the actual window listener
        // which cannot normally be used outside of the Window component because
        // of protected access.
        addWindowListener(new WindowAdapter() {

            /**
             * This event fires when the window has become active.
             *
             * @param e WindowEvent is not used.
             */
            public void windowActivated(WindowEvent e) {
                fireWindowActivated();
            }

            /**
             * Invoked when a window is de-activated.
             */
            public void windowDeactivated(WindowEvent e) {
            }

            /**
             * This event fires whenever a user minimizes the window
             * from the toolbar.
             *
             * @param e WindowEvent is not used.
             */
            public void windowIconified(WindowEvent e) {
            }

            /**
             * This event fires when the application is closing.
             * This allows Plugins to do any persistence or other
             * work before exiting.
             *
             * @param e WindowEvent is never used.
             */
            public void windowClosing(WindowEvent e) {
                saveLayout();
                setVisible(false);
            }
        });

        this.addWindowFocusListener(new MainWindowFocusListener());
    }

    /**
     * Adds a MainWindow listener to {@link MainWindow}. The
     * listener will be called when either the MainWindow has been minimized, maximized,
     * or is shutting down.
     *
     * @param listener the <code>MainWindowListener</code> to register
     */
    public void addMainWindowListener(MainWindowListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified {@link MainWindowListener}.
     *
     * @param listener the <code>MainWindowListener</code> to remove.
     */
    public void removeMainWindowListener(MainWindowListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all {@link MainWindowListener}s that the <code>MainWindow</code>
     * has been activated.
     */
    private void fireWindowActivated() {
        for (MainWindowListener listener : listeners) {
            listener.mainWindowActivated();
        }

        if (Spark.isMac()) {
            setJMenuBar(mainWindowBar);
        }
    }

    /**
     * Notifies all {@link MainWindowListener}s that the <code>MainWindow</code>
     * is shutting down.
     */
    private void fireWindowShutdown() {
        for (MainWindowListener listener : listeners) {
            listener.shutdown();
        }
    }

    /**
     * Invokes the Preferences Dialog.
     *
     * @param e the ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(preferenceMenuItem)) {
            SparkManager.getPreferenceManager().showPreferences();
        }
    }

    /**
     * Prepares Spark for shutting down by first calling all {@link MainWindowListener}s and
     * setting the Agent to be offline.
     */
    public void shutdown() {
        final XMPPConnection con = SparkManager.getConnection();

        if (con.isConnected()) {
            // Send disconnect.
            con.disconnect();
        }

        // Notify all MainWindowListeners
        try {
            fireWindowShutdown();
        }
        catch (Exception ex) {
            Log.error(ex);
        }
        // Close application.
        if(!Default.getBoolean("DISABLE_EXIT"))
            System.exit(1);

    }

    /**
     * Prepares Spark for shutting down by first calling all {@link MainWindowListener}s and
     * setting the Agent to be offline.
     *
     * @param sendStatus true if Spark should send a presence with a status message.
     */
    public void logout(boolean sendStatus) {
        final XMPPConnection con = SparkManager.getConnection();
        String status = null;

        if (con.isConnected() && sendStatus) {
            final InputTextAreaDialog inputTextDialog = new InputTextAreaDialog();
            status = inputTextDialog.getInput(Res.getString("title.status.message"), Res.getString("message.current.status"),
                SparkRes.getImageIcon(SparkRes.USER1_MESSAGE_24x24), this);
        }

        if (status != null || !sendStatus)
        {
	        // Notify all MainWindowListeners
	        try {
	            // Set auto-login to false;
	            SettingsManager.getLocalPreferences().setAutoLogin(false);
	            SettingsManager.saveSettings();

	            fireWindowShutdown();
	            setVisible(false);
	        }
	        finally {
	            closeConnectionAndInvoke(status);
	        }
        }
    }

    /**
     * Closes the current connection and restarts Spark.
     *
     * @param reason the reason for logging out. This can be if user gave no reason.
     */
    public void closeConnectionAndInvoke(String reason) {
        final XMPPConnection con = SparkManager.getConnection();
        if (con.isConnected()) {
            if (reason != null) {
                Presence byePresence = new Presence(Presence.Type.unavailable, reason, -1, null);
                con.disconnect(byePresence);
            }
            else {
                con.disconnect();
            }
        }
        if (!restartApplicationWithScript()) {
            restartApplicationWithJava();
        }
    }
    
    private File getLibDirectory() throws IOException {
        File jarFile;
        try{
            jarFile = new File(Startup.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch(Exception e) {
            Log.error("Cannot get jar file containing the startup class", e);
            return null;
        }
        if ( !jarFile.getName().endsWith(".jar") ) {
            Log.error("The startup class is not packaged in a jar file");
            return null;
        }
        File libDir = jarFile.getParentFile();
        return libDir;
    }
    
    private String getClasspath() throws IOException {
        File libDir = getLibDirectory();
        String libPath = libDir.getCanonicalPath();
        String[] files = libDir.list();
        StringBuilder classpath = new StringBuilder();
        for (String file : files) {
            if (file.endsWith(".jar")) {
                classpath.append(libPath + File.separatorChar + file + File.pathSeparatorChar);
            }
        }
        return classpath.toString();
    }

    private String getCommandPath() throws IOException{        
        return getLibDirectory().getParentFile().getCanonicalPath();
    }
    
    public boolean restartApplicationWithScript() {
        String command = null;
        try {
            if (Spark.isWindows()) {
                String sparkExe = getCommandPath() + File.separator + Default.getString(Default.SHORT_NAME) + ".exe";
                if (!new File(sparkExe).exists()) {
                    Log.warning("Client EXE file does not exist");
                    return false;
                }
                String starterExe = getCommandPath() + File.separator + "starter.exe";
                if (!new File(starterExe).exists()) {
                    Log.warning("Starter EXE file does not exist");
                    return false;
                }
                command = starterExe + " \"" + sparkExe + "\""; 
            } else if (Spark.isLinux()) {
                command = getCommandPath() + File.separator + Default.getString(Default.SHORT_NAME);
                if (!new File(command).exists()) {
                    Log.warning("Client startup script does not exist");
                    return false;
                }
            } else if (Spark.isMac()) {
                command = "open -a " + Default.getString(Default.SHORT_NAME);
            }

            Runtime.getRuntime().exec(command);
            System.exit(0);
            return true;
        } catch (IOException e) {
            Log.error("Error trying to restart application with script", e);
            return false;
        }
    }
    
    public boolean restartApplicationWithJava() {
        String javaBin = System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java";
        try {
            String toExec[] = new String[] {
                    javaBin, "-cp", getClasspath(), "org.jivesoftware.launcher.Startup"};
            Runtime.getRuntime().exec(toExec);
        } catch (Exception e) {
            Log.error("Error trying to restart application with java", e);
            return false;
        }
        System.exit(0);
        return true;
    }

    /**
     * Setup the Main Toolbar with File, Tools and Help.
     */
    private void buildMenu() {

        // setup file menu
        final JMenuItem exitMenuItem = new JMenuItem();

        // Setup ResourceUtils
        ResourceUtils.resButton(connectMenu, "&" + Default.getString(Default.APPLICATION_NAME));
        ResourceUtils.resButton(contactsMenu, Res.getString("menuitem.contacts"));
        ResourceUtils.resButton(actionsMenu, Res.getString("menuitem.actions"));
        ResourceUtils.resButton(exitMenuItem, Res.getString("menuitem.exit"));
        ResourceUtils.resButton(pluginsMenu, Res.getString("menuitem.plugins"));

        exitMenuItem.setIcon(null);

        mainWindowBar.add(connectMenu);
        mainWindowBar.add(contactsMenu);
        mainWindowBar.add(actionsMenu);
        //mainWindowBar.add(pluginsMenu);
        mainWindowBar.add(helpMenu);


        preferenceMenuItem = new JMenuItem(SparkRes.getImageIcon(SparkRes.PREFERENCES_IMAGE));
        preferenceMenuItem.setText(Res.getString("title.spark.preferences"));
        preferenceMenuItem.addActionListener(this);
        connectMenu.add(preferenceMenuItem);


        alwaysOnTopItem = new JCheckBoxMenuItem();
        ResourceUtils.resButton(alwaysOnTopItem, Res.getString("menuitem.always.on.top"));
        alwaysOnTopItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                	if (alwaysOnTopItem.isSelected())
                	{
                		SettingsManager.getLocalPreferences().setMainWindowAlwaysOnTop(true);
                		MainWindow.getInstance().setAlwaysOnTop(true);
                	}
                	else
                	{
                		SettingsManager.getLocalPreferences().setMainWindowAlwaysOnTop(false);
                		MainWindow.getInstance().setAlwaysOnTop(false);
                	}
                }
        });

        if (SettingsManager.getLocalPreferences().isMainWindowAlwaysOnTop())
        {
        	alwaysOnTopItem.setSelected(true);
        	this.setAlwaysOnTop(true);
        }
        connectMenu.add(alwaysOnTopItem);

        if(!Default.getBoolean("DISABLE_EXIT"))
            connectMenu.addSeparator();

        //EventQueue.invokeLater(new Runnable() {
   	   //	public void run() {

        JMenuItem logoutMenuItem = new JMenuItem();
        ResourceUtils.resButton(logoutMenuItem, Res.getString("menuitem.logout.no.status"));
        logoutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout(false);
           }
        });

        JMenuItem logoutWithStatus = new JMenuItem();
        ResourceUtils.resButton(logoutWithStatus, Res.getString("menuitem.logout.with.status"));
        logoutWithStatus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout(true);
            }
        });

        if ((Spark.isWindows() || Spark.isLinux()) || Spark.isMac() && !Default.getBoolean("DISABLE_EXIT")) {
            connectMenu.add(logoutMenuItem);
            connectMenu.add(logoutWithStatus);
	    connectMenu.addSeparator();
	}

        if (!Default.getBoolean("DISABLE_EXIT")) {
            connectMenu.add(exitMenuItem);
        }

        JMenuItem updateMenu= new JMenuItem("", SparkRes.getImageIcon(SparkRes.DOWNLOAD_16x16));
        ResourceUtils.resButton(updateMenu, Res.getString("menuitem.check.for.updates"));
        updateMenu.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		checkForUpdates(true);

	    }
	});

        // Add Error Dialog Viewer
        final Action viewErrors = new AbstractAction() {
			private static final long serialVersionUID = -420926784631340112L;

			public void actionPerformed(ActionEvent e) {
                File logDir = new File(Spark.getLogDirectory(), "errors.log");
                if (!logDir.exists()) {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "No error logs found.", "Error Log", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    showErrorLog();
                }
            }
        };

        viewErrors.putValue(Action.NAME, Res.getString("menuitem.view.logs"));

        final Action viewHelpGuideAction = new AbstractAction() {

            	final String url = Default.getString(Default.HELP_USER_GUIDE);
			private static final long serialVersionUID = 2680369963282231348L;

			public void actionPerformed(ActionEvent actionEvent) {
                try {

                    BrowserLauncher.openURL(url);
                }
                catch (Exception e) {
                    Log.error("Unable to load online help.", e);
                }
            }
        };


	if (!Default.getBoolean("HELP_USER_GUIDE_DISABLED")) {
	    viewHelpGuideAction.putValue(Action.NAME,
		    Res.getString("menuitem.user.guide"));
	    viewHelpGuideAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.SMALL_QUESTION));
	    helpMenu.add(viewHelpGuideAction);
	}
	if (!Default.getBoolean("HELP_FORUM_DISABLED")) {
	    helpMenu.add(sparkforumItem);
	}

        // Build Help Menu
	if(!Default.getBoolean(Default.DISABLE_UPDATES)){
	    helpMenu.add(updateMenu);
	}
        helpMenu.addSeparator();
        helpMenu.add(viewErrors);
        helpMenu.add(menuAbout);

        // ResourceUtils - Adds mnemonics
        ResourceUtils.resButton(preferenceMenuItem, Res.getString("menuitem.preferences"));
        ResourceUtils.resButton(helpMenu, Res.getString("menuitem.help"));
        ResourceUtils.resButton(menuAbout, Res.getString("menuitem.about"));

	if (Default.getString("HELP_FORUM_TEXT").length() > 0) {
	    ResourceUtils.resButton(sparkforumItem, Default.getString("HELP_FORUM_TEXT"));
	} else {
	    ResourceUtils.resButton(sparkforumItem, Res.getString("menuitem.online.help"));
	}
        // Register shutdown with the exit menu.
	exitMenuItem.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = -2301236575241532698L;

	    public void actionPerformed(ActionEvent e) {
		shutdown();
	    }
	});

	sparkforumItem.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = -1423433460333010339L;

	    final String url = Default.getString("HELP_FORUM");

	    public void actionPerformed(ActionEvent e) {
		try {
		    BrowserLauncher.openURL(url);
		} catch (Exception browserException) {
		    Log.error("Error launching browser:", browserException);
		}
	    }
	});

        // Show About Box
	menuAbout.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = -7173666373051354502L;

	    public void actionPerformed(ActionEvent e) {
		showAboutBox();
	    }
	});

	if (!Default.getBoolean("DISABLE_UPDATES")) {
	    // Execute spark update checker after one minute.
	    final TimerTask task = new SwingTimerTask() {
		public void doRun() {
		    checkForUpdates(false);
		}
	    };

	    TaskEngine.getInstance().schedule(task, 60000);
	}

	if(SettingsManager.getLocalPreferences().isDebuggerEnabled())
	{
	    JMenuItem rawPackets = new JMenuItem(SparkRes.getImageIcon(SparkRes.TRAY_IMAGE));
	    rawPackets.setText("Send Packets");
	    rawPackets.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	    		new RawPacketSender();

	        }
	    });

	    connectMenu.add(rawPackets,2);
	}

    }

    /**
     * Returns the JMenuBar for the MainWindow. You would call this if you
     * wished to add or remove menu items to the main menubar. (File | Tools | Help)
     *
     * @return the Jive Talker Main Window MenuBar
     */
    public JMenuBar getMenu() {
        return mainWindowBar;
    }

    /**
     * Returns the Menu in the JMenuBar by it's name. For example:<p>
     * <pre>
     * JMenu toolsMenu = getMenuByName("Tools");
     * </pre>
     * </p>
     *
     * @param name the name of the Menu.
     * @return the JMenu item with the requested name.
     */
    public JMenu getMenuByName(String name) {
        for (int i = 0; i < getMenu().getMenuCount(); i++) {
            JMenu menu = getMenu().getMenu(i);
            if (menu.getText().equals(name)) {
                return menu;
            }
        }
        return null;
    }

    /**
     * Returns true if the Spark window is in focus.
     *
     * @return true if the Spark window is in focus.
     */
    public boolean isInFocus() {
        return focused;
    }

    private class MainWindowFocusListener implements WindowFocusListener {

        public void windowGainedFocus(WindowEvent e) {
            focused = true;
        }

        public void windowLostFocus(WindowEvent e) {
            focused = false;
        }
    }

    /**
     * Return the top toolbar in the Main Window to allow for customization.
     *
     * @return the MainWindows top toolbar.
     */
    public JToolBar getTopToolBar() {
        return topToolbar;
    }

    /**
     * Checks for the latest update on the server.
     *
     * @param forced true if you want to bypass the normal checking security.
     */
    private void checkForUpdates(final boolean forced) {
        final CheckUpdates updater = new CheckUpdates();
        try {
            final SwingWorker updateThread = new SwingWorker() {
                public Object construct() {
                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        Log.error(e);
                    }
                    return "ok";
                }

                public void finished() {
                    try {
                        updater.checkForUpdate(forced);
                    }
                    catch (Exception e) {
                        Log.error("There was an error while checking for a new update.", e);
                    }
                }
            };

            updateThread.start();

        }
        catch (Exception e) {
            Log.warning("Error updating.", e);
        }
    }

    /**
     * Displays the About Box for Spark.
     */
    private static void showAboutBox() {
        JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Default.getString(Default.APPLICATION_NAME) + " " + JiveInfo.getVersion(),
            Res.getString("title.about"), JOptionPane.INFORMATION_MESSAGE, SparkRes.getImageIcon(SparkRes.MAIN_IMAGE));
    }

    /**
     * Displays the Spark error log.
     */
    private void showErrorLog() {
        final File logDir = new File(Spark.getLogDirectory(), "errors.log");

        // Read file and show
        final String errorLogs = URLFileSystem.getContents(logDir);

        final JFrame frame = new JFrame(Res.getString("title.client.logs"));
        frame.setLayout(new BorderLayout());
        frame.setIconImage(SparkManager.getApplicationImage().getImage());

        final JTextPane pane = new JTextPane();
        pane.setBackground(Color.white);
        pane.setFont(new Font("Dialog", Font.PLAIN, 12));
        pane.setEditable(false);
        pane.setText(errorLogs);

        frame.add(new JScrollPane(pane), BorderLayout.CENTER);

        final JButton copyButton = new JButton(Res.getString("button.copy.to.clipboard"));
        frame.add(copyButton, BorderLayout.SOUTH);

        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SparkManager.setClipboard(errorLogs);
                copyButton.setEnabled(false);
            }
        });

        frame.pack();
        frame.setSize(600, 400);

        GraphicUtils.centerWindowOnScreen(frame);
        frame.setVisible(true);
    }

    /**
     * Saves the layout on closing of the main window.
     */
    public void saveLayout() {
        try {
            LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();
            settings.setMainWindowHeight(getHeight());
            settings.setMainWindowWidth(getWidth());
            settings.setMainWindowX(getX());
            settings.setMainWindowY(getY());
            LayoutSettingsManager.saveLayoutSettings();
        }
        catch (Exception e) {
            // Don't let this cause a real problem shutting down.
        }
    }

    /**
     * Return true if the MainWindow is docked.
     *
     * @return true if the window is docked.
     */
    public boolean isDocked() {
        LocalPreferences preferences = SettingsManager.getLocalPreferences();
        return preferences.isDockingEnabled();
    }

    /**
     * Returns the inner split pane.
     *
     * @return the split pane.
     */
    public JSplitPane getSplitPane() {
        // create the split pane only if required.
        if (splitPane == null) {
            splitPane = new JSplitPane();
        }
        return this.splitPane;
    }
}
