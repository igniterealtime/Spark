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
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.NativeHandler;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.plugin.Plugin;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;


/**
 * Plugins for handling Mac OS X specific functionality
 *
 * @author Andrew Wright
 */
public class ApplePlugin implements Plugin, NativeHandler {

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
            SparkManager.getNativeManager().addNativeHandler(this);

            handleIdle();

            // Remove the About Menu Item from the help menu
            MainWindow mainWindow = SparkManager.getMainWindow();

            JMenu helpMenu = mainWindow.getMenuByName("Help");
            Component[] menuComponents = helpMenu.getMenuComponents();
            Component prev = null;
            for (Component current : menuComponents) {
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
            for (Component current : menuComponents) {
                if (current instanceof JMenuItem) {
                    JMenuItem item = (JMenuItem) current;

                    if ("Preferences".equals(item.getText())) {
                        connectMenu.remove(item);
                    } else if ("Log Out".equals(item.getText())) {
                        connectMenu.remove(item);
                    }


                } else if (current instanceof JSeparator) {
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

                    if(SparkManager.getChatManager().getChatContainer().getTotalNumberOfUnreadMessages() > 0){
                        final ChatFrame frame = SparkManager.getChatManager().getChatContainer().getChatFrame();
                        frame.setState(Frame.NORMAL);
                        frame.setVisible(true);
                        frame.toFront();
                    }
                }


                public void handleQuit(ApplicationEvent applicationEvent) {
                    SparkManager.getMainWindow().shutdown();
                }

            });
            statusMenu = new AppleStatusMenu();
            statusMenu.display();
        }
    }

    public void shutdown() {
        if (Spark.isMac()) {
            SparkManager.getNativeManager().removeNativeHandler(this);
        }
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
        // No need, since this is internal
    }

    public void flashWindow(Window window) {
        appleUtils.bounceDockIcon(true);
        statusMenu.showActiveIcon();
    }

    public void flashWindowStopWhenFocused(Window window) {
        appleUtils.bounceDockIcon(true);
        try {
            statusMenu.showActiveIcon();
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    public void stopFlashing(Window window) {
        appleUtils.resetDock();
        try {
            statusMenu.showBlackIcon();
        }
        catch (Exception e) {
            Log.error(e);
        }

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

                setActivity();
            }


            public void chatRoomClosed(ChatRoom room) {
                setActivity();
            }
        });

        SparkManager.getSessionManager().addPresenceListener(new PresenceListener() {
            public void presenceChanged(Presence presence) {
                if(presence.isAvailable() && !presence.isAway()){
                    lastActive = System.currentTimeMillis();
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
            if (workspace != null) {
                Presence presence = workspace.getStatusBar().getPresence();
                long diff = System.currentTimeMillis() - lastActive;
                boolean idle = diff > 60000 * 60;
                if (presence.getMode() == Presence.Mode.available && idle) {
                    unavailable = true;
                    StatusItem away = workspace.getStatusBar().getStatusItem("Away");
                    Presence p = away.getPresence();
                    p.setStatus(Res.getString("message.away.idle"));

                    previousPriority = presence.getPriority();

                    p.setPriority(0);

                    SparkManager.getSessionManager().changePresence(p);
                }
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
            lastActive = System.currentTimeMillis();
        }
    }


    public boolean openFile(File file) {
        return false;
    }

    public boolean launchEmailClient(String to, String subject) {
        return false;
    }

    public boolean launchBrowser(String url) {
        try {
            BrowserLauncher.openURL(url);
        }
        catch (IOException e) {
            Log.error(e);
        }
        return true;
    }
}



