/**
 * $Revision: 22540 $
 * $Date: 2005-10-10 08:44:25 -0700 (Mon, 10 Oct 2005) $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package com.jivesoftware.spark.plugin.apple;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.MainWindowListener;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.spark.Alerter;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.plugin.Plugin;
import org.jdesktop.jdic.systeminfo.SystemInfo;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;


/**
 * Plugins for handling Mac OS X specific functionality
 *
 * @author Andrew Wright
 */
public class ApplePlugin implements Plugin, Alerter {

    private AppleStatusMenu statusMenu;
    private AppleUtils appleUtils;
    private boolean unavailable;
    private int previousPriority;
    private boolean addedFrameListener;
    private long lastActive;

    private ChatFrame chatFrame;

    public void initialize() {
        if (Spark.isMac()) {
            appleUtils = new AppleUtils();
            SparkManager.getAlertManager().addAlert(this);

            handleIdle();

            // Remove the About Menu Item from the help menu
            MainWindow mainWindow = SparkManager.getMainWindow();

            JMenu helpMenu = mainWindow.getMenuByName("Help");
            Component[] menuComponents = helpMenu.getMenuComponents();
            Component prev = null;
            for (int i = 0; i < menuComponents.length; i++) {
                Component current = menuComponents[i];
                if (current instanceof JMenuItem) {
                    JMenuItem item = (JMenuItem) current;
                    if ("About".equals(item.getText())) {
                        helpMenu.remove(item);

                        // We want to remove the seperator
                        if (prev != null && (prev instanceof JSeparator)) {
                            helpMenu.remove(prev);
                        }
                    }
                }
                prev = current;
            }

            JMenu connectMenu = mainWindow.getMenuByName("Spark");
            connectMenu.setText("Connect");
            menuComponents = connectMenu.getMenuComponents();
            JSeparator lastSeperator = null;
            for (int i = 0; i < menuComponents.length; i++) {
                Component current = menuComponents[i];
                if (current instanceof JMenuItem) {
                    JMenuItem item = (JMenuItem) current;

                    if ("Preferences".equals(item.getText())) {
                        connectMenu.remove(item);
                    }
                    else if ("Log Out".equals(item.getText())) {
                        connectMenu.remove(item);
                    }


                }
                else if (current instanceof JSeparator) {
                    lastSeperator = (JSeparator) current;
                }
            }
            if (lastSeperator != null) {
                connectMenu.remove(lastSeperator);
            }

            // register an application listener to show the about box
            Application application = Application.getApplication();

            application.setEnabledPreferencesMenu(true);
            application.addPreferencesMenuItem();
            application.addApplicationListener(new ApplicationAdapter() {

                public void handlePreferences(ApplicationEvent applicationEvent) {
                    SparkManager.getPreferenceManager().showPreferences();
                }

                public void handleReOpenApplication(ApplicationEvent event) {
                    MainWindow mainWindow = SparkManager.getMainWindow();
                    if (!mainWindow.isVisible()) {
                        mainWindow.setState(Frame.NORMAL);
                        mainWindow.setVisible(true);
                    }
                }


                public void handleQuit(ApplicationEvent applicationEvent) {
                    System.exit(0);
                }

            });
            statusMenu = new AppleStatusMenu();
            statusMenu.display();
        }
    }

    public void shutdown() {
        if (Spark.isMac()) {
            SparkManager.getAlertManager().removeAlert(this);
        }
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
        // No need, since this is internal
    }

    public void flashWindow(Window window) {
        appleUtils.bounceDockIcon(false);
        statusMenu.showActiveIcon();
    }

    public void flashWindowStopWhenFocused(Window window) {
        appleUtils.bounceDockIcon(false);
        statusMenu.showActiveIcon();
    }

    public void stopFlashing(Window window) {
        appleUtils.resetDock();
        statusMenu.showBlackIcon();

    }

    public boolean handleNotification() {
        return Spark.isMac();
    }

    private void handleIdle() {
        SparkManager.getMainWindow().addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent componentEvent) {
                setActivity();
            }

            public void componentMoved(ComponentEvent componentEvent) {
                setActivity();
            }

            public void componentShown(ComponentEvent componentEvent) {
                setActivity();
            }

            public void componentHidden(ComponentEvent componentEvent) {
                setActivity();
            }
        });

        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                if (!addedFrameListener) {
                    chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
                    chatFrame.addComponentListener(new ComponentListener() {
                        public void componentResized(ComponentEvent componentEvent) {
                            setActivity();
                        }

                        public void componentMoved(ComponentEvent componentEvent) {
                            setActivity();
                        }

                        public void componentShown(ComponentEvent componentEvent) {
                            setActivity();
                        }

                        public void componentHidden(ComponentEvent componentEvent) {
                            setActivity();
                        }
                    });

                    addedFrameListener = true;

                }
            }
        });


        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                sparkIsIdle();
            }
        }, 10000, 10000);

        lastActive = System.currentTimeMillis();

    }

    public void setActivity() {
        lastActive = System.currentTimeMillis();
        setAvailableIfActive();
    }


    private void sparkIsIdle() {
        LocalPreferences localPref = SettingsManager.getLocalPreferences();
        if (!localPref.isIdleOn()) {
            return;
        }

        try {
            // Handle if spark is not connected to the server.
            if (SparkManager.getConnection() == null || !SparkManager.getConnection().isConnected()) {
                return;
            }

            // Change Status
            Workspace workspace = SparkManager.getWorkspace();
            Presence presence = workspace.getStatusBar().getPresence();
            long diff = System.currentTimeMillis() - lastActive;
            boolean idle = diff > 60000;
            if (workspace != null && presence.getMode() == Presence.Mode.available && idle) {
                unavailable = true;
                StatusItem away = workspace.getStatusBar().getStatusItem("Away");
                Presence p = away.getPresence();
                p.setStatus(Res.getString("message.away.idle"));

                previousPriority = presence.getPriority();

                p.setPriority(0);

                SparkManager.getSessionManager().changePresence(p);
            }
        }
        catch (Exception e) {
            Log.error("Error with IDLE status.", e);
        }
    }

    private void setAvailableIfActive() {
        if (!unavailable) {
            return;
        }
        // Handle if spark is not connected to the server.
        if (SparkManager.getConnection() == null || !SparkManager.getConnection().isConnected()) {
            return;
        }

        // Change Status
        Workspace workspace = SparkManager.getWorkspace();
        if (workspace != null) {
            Presence presence = workspace.getStatusBar().getStatusItem(Res.getString("available")).getPresence();
            if (previousPriority != -1) {
                presence.setPriority(previousPriority);
            }

            SparkManager.getSessionManager().changePresence(presence);
            unavailable = false;
        }
    }


}



