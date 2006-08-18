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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.debugger.EnhancedDebuggerWindow;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.InputTextAreaDialog;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.sparkimpl.updater.CheckUpdates;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

/**
 * The <code>MainWindow</code> class acts as both the DockableHolder and the proxy
 * to the Workspace in Spark.
 *
 * @version 1.0, 03/12/14
 */
public final class MainWindow extends JFrame implements ActionListener {
    private final Set<MainWindowListener> listeners = new HashSet<MainWindowListener>();

    private final JMenu connectMenu = new JMenu();
    private final JMenu contactsMenu = new JMenu();
    private final JMenu actionsMenu = new JMenu();
    private final JMenu pluginsMenu = new JMenu();
    private final JMenu helpMenu = new JMenu();

    private JMenuItem preferenceMenuItem;

    private final JMenuItem menuAbout = new JMenuItem(SparkRes.getImageIcon(SparkRes.INFORMATION_IMAGE));
    private final JMenuItem helpMenuItem = new JMenuItem(SparkRes.getImageIcon(SparkRes.SMALL_QUESTION));

    private final JMenuBar mainWindowBar = new JMenuBar();

    private boolean focused;

    private JToolBar topBar = new JToolBar();

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
        configureMenu();

        // Add Workspace Container
        getContentPane().setLayout(new BorderLayout());

        // Add menubar
        this.setJMenuBar(mainWindowBar);
        this.getContentPane().add(topBar, BorderLayout.NORTH);

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

                // Show confirmation about hiding.
                //   SparkManager.getNotificationsEngine().confirmHidingIfNecessary();
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
        // Notify all MainWindowListeners
        try {
            fireWindowShutdown();
        }
        finally {
            // Close application.
            System.exit(1);
        }
    }

    /**
     * Prepares Spark for shutting down by first calling all {@link MainWindowListener}s and
     * setting the Agent to be offline.
     */
    public void logout(boolean sendStatus) {
        final XMPPConnection con = SparkManager.getConnection();


        if (con.isConnected() && sendStatus) {
            final InputTextAreaDialog inputTextDialog = new InputTextAreaDialog();
            String status = inputTextDialog.getInput("Status Message", "Let others know your current status or activity.",
                SparkRes.getImageIcon(SparkRes.USER1_MESSAGE_24x24), this);

            if (status != null) {
                Presence presence = new Presence(Presence.Type.unavailable);
                presence.setStatus(status);
                con.sendPacket(presence);
            }
        }

        // Set auto-login to false
        SettingsManager.getLocalPreferences().setAutoLogin(false);

        // Notify all MainWindowListeners
        try {
            fireWindowShutdown();
            setVisible(false);
        }
        finally {

            final SwingWorker shutdownThread = new SwingWorker() {
                public Object construct() {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        Log.error(e);
                    }
                    return true;
                }

                public void finished() {
                    closeConnectionAndInvoke();
                }
            };

            shutdownThread.start();

        }
    }

    private void closeConnectionAndInvoke() {
        final XMPPConnection con = SparkManager.getConnection();
        if (con.isConnected()) {
            con.close();
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
    private void configureMenu() {
        // setup file menu
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        // Setup ResourceUtils
        ResourceUtils.resButton(connectMenu, "&" + Default.getString(Default.APPLICATION_NAME));
        ResourceUtils.resButton(contactsMenu, "Con&tacts");
        ResourceUtils.resButton(actionsMenu, "&Actions");
        ResourceUtils.resButton(exitMenuItem, "&Exit");
        ResourceUtils.resButton(pluginsMenu, "&Plugins");

        exitMenuItem.setIcon(null);

        mainWindowBar.add(connectMenu);
        mainWindowBar.add(contactsMenu);
        mainWindowBar.add(actionsMenu);
        //mainWindowBar.add(pluginsMenu);
        mainWindowBar.add(helpMenu);


        preferenceMenuItem = new JMenuItem(SparkRes.getImageIcon(SparkRes.PREFERENCES_IMAGE));
        preferenceMenuItem.setText("Spark Preferences");
        preferenceMenuItem.addActionListener(this);
        connectMenu.add(preferenceMenuItem);
        connectMenu.addSeparator();

        JMenuItem logoutMenuItem = new JMenuItem("Log Out");
        ResourceUtils.resButton(logoutMenuItem, "L&og Out");
        logoutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout(false);
            }
        });

        JMenuItem logoutWithStatus = new JMenuItem("Log Out");
        ResourceUtils.resButton(logoutWithStatus, "Log out with status");
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

        connectMenu.add(exitMenuItem);

        Action showTrafficAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                EnhancedDebuggerWindow window = EnhancedDebuggerWindow.getInstance();
                window.setVisible(true);
            }
        };
        showTrafficAction.putValue(Action.NAME, "Show Traffic Window");
        showTrafficAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.TRAFFIC_LIGHT_IMAGE));

        Action updateAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                checkUpdate(true);
            }
        };

        updateAction.putValue(Action.NAME, "Check For Updates");
        updateAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DOWNLOAD_16x16));

        // Build Help Menu
        helpMenu.setText("Help");
        //s helpMenu.add(helpMenuItem);
        helpMenu.add(showTrafficAction);
        helpMenu.add(updateAction);
        helpMenu.addSeparator();
        helpMenu.add(menuAbout);

        // ResourceUtils - Adds mnemonics
        ResourceUtils.resButton(preferenceMenuItem, "&Preferences");
        ResourceUtils.resButton(helpMenu, "&Help");
        ResourceUtils.resButton(menuAbout, "&About");
        ResourceUtils.resButton(helpMenuItem, "&Online Help");

        // Register shutdown with the exit menu.
        exitMenuItem.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                shutdown();
            }
        });

        helpMenuItem.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    BrowserLauncher.openURL("http://www.jivesoftware.org/community/kbcategory.jspa?categoryID=23");
                }
                catch (IOException browserException) {
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
                checkUpdate(false);
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
        return topBar;
    }

    /**
     * Checks for the latest update on the server.
     *
     * @param forced true if you want to bypass the normal checking security.
     */
    private void checkUpdate(final boolean forced) {
        final CheckUpdates updater = new CheckUpdates();
        try {
            SwingWorker stopWorker = new SwingWorker() {
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

            stopWorker.start();

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

}