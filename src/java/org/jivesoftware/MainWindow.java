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
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.debugger.EnhancedDebuggerWindow;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.InputTextAreaDialog;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.sparkimpl.updater.CheckUpdates;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

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
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The <code>MainWindow</code> class acts as both the DockableHolder and the proxy
 * to the Workspace in Spark.
 *
 * @version 1.0, 03/12/14
 */
public final class MainWindow extends ChatFrame implements ActionListener {
    private final Set<MainWindowListener> listeners = new HashSet<MainWindowListener>();

    private final JMenu connectMenu = new JMenu();
    private final JMenu contactsMenu = new JMenu();
    private final JMenu actionsMenu = new JMenu();
    private final JMenu pluginsMenu = new JMenu();
    private final JMenu helpMenu = new JMenu();

    private JMenuItem preferenceMenuItem;

    private final JMenuItem menuAbout = new JMenuItem(SparkRes.getImageIcon(SparkRes.INFORMATION_IMAGE));
    private final JMenuItem helpMenuItem = new JMenuItem();

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
                MainWindow controller = new MainWindow(Default.getString(Default.APPLICATION_NAME), SparkRes.getImageIcon(SparkRes.MAIN_IMAGE));
                singleton = controller;
                return controller;
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

        // Add menubar
        this.setJMenuBar(mainWindowBar);
        this.getContentPane().add(topToolbar, BorderLayout.NORTH);

        setTitle(title + " - " + SparkManager.getSessionManager().getUsername());

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
        finally {
            // Close application.
            System.exit(1);
        }
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

        // Notify all MainWindowListeners
        try {
            // Set auto-login to false;
            SettingsManager.getLocalPreferences().setAutoLogin(false);

            fireWindowShutdown();
            setVisible(false);
        }
        finally {
            closeConnectionAndInvoke(status);
        }
    }

    /**
     * Closes the current connection and restarts Spark.
     *
     * @param reason the reason for logging out. This can be if user gave no reason.
     */
    private void closeConnectionAndInvoke(String reason) {
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

        try {
            String command = "";
            if (Spark.isWindows()) {
                String sparkExe = Spark.getBinDirectory().getParentFile().getCanonicalPath() + "\\Spark.exe";
                String starterExe = Spark.getBinDirectory().getParentFile().getCanonicalPath() + "\\starter.exe";

                command = starterExe + " \"" + sparkExe + "\"";
            }
            else if (Spark.isMac()) {
                command = "open -a Spark";
            }

            Runtime.getRuntime().exec(command);
        }
        catch (IOException e) {
            Log.error("Error starting Spark", e);
        }

        System.exit(1);
    }

    /**
     * Setup the Main Toolbar with File, Tools and Help.
     */
    private void buildMenu() {
        // setup file menu
        JMenuItem exitMenuItem = new JMenuItem();

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
        connectMenu.addSeparator();

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


        if (Spark.isWindows()) {
            connectMenu.add(logoutMenuItem);
            connectMenu.add(logoutWithStatus);
        }

        connectMenu.addSeparator();

        if (!Spark.isMac()) {
            connectMenu.add(exitMenuItem);
        }
        Action showTrafficAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                EnhancedDebuggerWindow window = EnhancedDebuggerWindow.getInstance();
                window.setVisible(true);
            }
        };
        showTrafficAction.putValue(Action.NAME, Res.getString("menuitem.show.traffic"));
        showTrafficAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.TRAFFIC_LIGHT_IMAGE));

        Action updateAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                checkForUpdates(true);
            }
        };

        updateAction.putValue(Action.NAME, Res.getString("menuitem.check.for.updates"));
        updateAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DOWNLOAD_16x16));

        // Add Error Dialog Viewer
        Action viewErrors = new AbstractAction() {
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

        viewErrors.putValue(Action.NAME, "View Logs");

        final Action viewHelpGuideAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    BrowserLauncher.openURL("http://www.igniterealtime.org/builds/spark/docs/spark_user_guide.pdf");
                }
                catch (IOException e) {
                    Log.error("Unable to load online help.", e);
                }
            }
        };

        viewHelpGuideAction.putValue(Action.NAME, Res.getString("menuitem.user.guide"));
        viewHelpGuideAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_QUESTION));

        // Build Help Menu
        helpMenu.add(viewHelpGuideAction);
        helpMenu.add(helpMenuItem);
        helpMenu.add(showTrafficAction);
        helpMenu.add(updateAction);
        helpMenu.addSeparator();
        helpMenu.add(viewErrors);


        helpMenu.add(menuAbout);

        // ResourceUtils - Adds mnemonics
        ResourceUtils.resButton(preferenceMenuItem, Res.getString("menuitem.preferences"));
        ResourceUtils.resButton(helpMenu, Res.getString("menuitem.help"));
        ResourceUtils.resButton(menuAbout, Res.getString("menuitem.about"));
        ResourceUtils.resButton(helpMenuItem, Res.getString("menuitem.online.help"));

        // Register shutdown with the exit menu.
        exitMenuItem.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                shutdown();
            }
        });

        helpMenuItem.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    BrowserLauncher.openURL("http://www.igniterealtime.org/forum/forum.jspa?forumID=49");
                }
                catch (Exception browserException) {
                    Log.error("Error launching browser:", browserException);
                }
            }
        });

        // Show About Box
        menuAbout.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showAboutBox();
            }
        });

        int delay = 15000; // 15 sec
        Date timeToRun = new Date(System.currentTimeMillis() + delay);
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                checkForUpdates(false);
            }
        }, timeToRun);

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
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the Spark error log.
     */
    private void showErrorLog() {
        final File logDir = new File(Spark.getLogDirectory(), "errors.log");

        // Read file and show
        final String errorLogs = URLFileSystem.getContents(logDir);

        final JFrame frame = new JFrame("Spark Logs");
        frame.setLayout(new BorderLayout());
        frame.setIconImage(SparkRes.getImageIcon(SparkRes.MAIN_IMAGE).getImage());

        final JTextPane pane = new JTextPane();
        pane.setBackground(Color.white);
        pane.setFont(new Font("Dialog", Font.PLAIN, 12));
        pane.setEditable(false);
        pane.setText(errorLogs);

        frame.add(new JScrollPane(pane), BorderLayout.CENTER);

        final JButton copyButton = new JButton("Copy To Clipboard");
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
