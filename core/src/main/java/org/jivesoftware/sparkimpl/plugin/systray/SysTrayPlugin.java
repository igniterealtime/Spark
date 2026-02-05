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
package org.jivesoftware.sparkimpl.plugin.systray;

import java.awt.MouseInfo;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.NativeHandler;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.status.CustomStatusItem;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;


/**
 * Handles tray icon operations inside of Spark.
 * Use to display incoming chat requests, incoming messages  and general notifications.
 */
public class SysTrayPlugin implements Plugin, NativeHandler, ChatStateListener {
    private final JPopupMenu popupMenu = new JPopupMenu();

    private final LocalPreferences pref = SettingsManager.getLocalPreferences();
    private ImageIcon availableIcon;
    private ImageIcon dndIcon;
    private ImageIcon awayIcon;
    private ImageIcon xawayIcon;
    private ImageIcon offlineIcon;
    private ImageIcon connectingIcon;
    private ImageIcon newMessageIcon;
    private ImageIcon typingIcon;
    private TrayIcon trayIcon;
    private boolean newMessage = false;
    private Presence presence;
    private final ChatMessageHandlerImpl chatMessageHandler = new ChatMessageHandlerImpl();

    @Override
    public boolean canShutDown() {
        return true;
    }

    @Override
    public void initialize() {
        if (!SystemTray.isSupported()) {
            Log.error("Tray don't supports on this platform.");
            return;
        }
        JMenuItem openMenu = new JMenuItem(Res.getString("menuitem.open"));
        JMenuItem minimizeMenu = new JMenuItem(Res.getString("menuitem.hide"));
        JMenuItem exitMenu = new JMenuItem(Res.getString("menuitem.exit"));
        JMenuItem logoutMenu = new JMenuItem(Res.getString("menuitem.logout.no.status"));

        SystemTray tray = SystemTray.getSystemTray();
        SparkManager.getNativeManager().addNativeHandler(this);
        ChatManager.getInstance().addChatMessageHandler(chatMessageHandler);
        //XEP-0085 support (replaces the obsolete XEP-0022)
        ChatStateManager.getInstance(SparkManager.getConnection()).addChatStateListener(this);

        if (Spark.isLinux()) {
            newMessageIcon = SparkRes.getImageIcon(SparkRes.MESSAGE_NEW_TRAY_LINUX);
            typingIcon = SparkRes.getImageIcon(SparkRes.TYPING_TRAY_LINUX);
        } else {
            newMessageIcon = SparkRes.getImageIcon(SparkRes.MESSAGE_NEW_TRAY);
            typingIcon = SparkRes.getImageIcon(SparkRes.TYPING_TRAY);
        }

        availableIcon = Default.getImageIcon(Default.TRAY_IMAGE);
        if (Spark.isLinux()) {
            if (availableIcon == null) {
                availableIcon = SparkRes.getImageIcon(SparkRes.TRAY_IMAGE_LINUX);
            }
            awayIcon = SparkRes.getImageIcon(SparkRes.TRAY_AWAY_LINUX);
            xawayIcon = SparkRes.getImageIcon(SparkRes.TRAY_XAWAY_LINUX);
            dndIcon = SparkRes.getImageIcon(SparkRes.TRAY_DND_LINUX);
            offlineIcon = SparkRes.getImageIcon(SparkRes.TRAY_OFFLINE_LINUX);
            connectingIcon = SparkRes.getImageIcon(SparkRes.TRAY_CONNECTING_LINUX);
        } else {
            if (availableIcon == null) {
                availableIcon = SparkRes.getImageIcon(SparkRes.TRAY_IMAGE);
            }
            awayIcon = SparkRes.getImageIcon(SparkRes.TRAY_AWAY);
            xawayIcon = SparkRes.getImageIcon(SparkRes.TRAY_XAWAY);
            dndIcon = SparkRes.getImageIcon(SparkRes.TRAY_DND);
            offlineIcon = SparkRes.getImageIcon(SparkRes.TRAY_OFFLINE);
            connectingIcon = SparkRes.getImageIcon(SparkRes.TRAY_CONNECTING);
        }

        popupMenu.add(openMenu);

        openMenu.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                SparkManager.getMainWindow().setVisible(true);
                SparkManager.getMainWindow().toFront();
            }

        });

        popupMenu.add(minimizeMenu);

        minimizeMenu.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                SparkManager.getMainWindow().setVisible(false);
            }
        });

        // See if we should disable the ability to change presence status
        if (!Default.getBoolean(Default.DISABLE_PRESENCE_STATUS_CHANGE) && Enterprise.containsFeature(Enterprise.PRESENCE_STATUS_FEATURE)) {
            popupMenu.addSeparator();
            addStatusMessages();
            popupMenu.addSeparator();
        }

        // Logout Menu
        if (Spark.isWindows()) {
            if (!Default.getBoolean(Default.DISABLE_EXIT) && Enterprise.containsFeature(Enterprise.LOGOUT_EXIT_FEATURE)) {
                if (!Default.getBoolean(Default.HIDE_SAVE_PASSWORD_AND_AUTO_LOGIN) && pref.getPswdAutologin()) {
                    logoutMenu.addActionListener(new AbstractAction() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SparkManager.getMainWindow().logout(false);
                        }
                    });
                    popupMenu.add(logoutMenu);
                }
            }
        }

        // Exit Menu
        exitMenu.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                SparkManager.getMainWindow().shutdown();
            }
        });

        if (!Default.getBoolean(Default.DISABLE_EXIT) && Enterprise.containsFeature(Enterprise.LOGOUT_EXIT_FEATURE))
            popupMenu.add(exitMenu);

        // If connection closed set offline tray image
        SparkManager.getConnection().addConnectionListener(new TrayConnectionListener());

        SparkManager.getSessionManager().addPresenceListener(
            presence -> {
                setIconByPresence(presence, availableIcon);
            });

        try {
            trayIcon = new TrayIcon(availableIcon.getImage(), JiveInfo.getName(), null);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new TrayMouseListener());

            tray.add(trayIcon);
        } catch (Exception e) {
            Log.error("Unable to render tray icon", e);
        }
    }

    public void addStatusMessages() {
        StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
        for (StatusItem statusItem : statusBar.getStatusList()) {
            final AbstractAction action = new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    SparkManager.getSessionManager().changePresence(statusItem.getPresence());
                    statusBar.setStatus(statusItem.getText());
                }
            };
            action.putValue(Action.NAME, statusItem.getText());
            action.putValue(Action.SMALL_ICON, statusItem.getIcon());

            boolean hasChildren = false;
            for (CustomStatusItem cItem : statusBar.getCustomStatusList()) {
                String type = cItem.getType();
                if (type.equals(statusItem.getText())) {
                    hasChildren = true;
                    break;
                }
            }

            if (!hasChildren) {
                JMenuItem status = new JMenuItem(action);
                popupMenu.add(status);
            } else {
                final JMenu status = new JMenu(action);
                popupMenu.add(status);

                status.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        action.actionPerformed(null);
                        popupMenu.setVisible(false);
                    }
                });

                for (CustomStatusItem customItem : statusBar.getCustomStatusList()) {
                    String type = customItem.getType();
                    if (type.equals(statusItem.getText())) {
                        AbstractAction customAction = new AbstractAction() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Presence oldPresence = statusItem.getPresence();
                                Presence presence = StanzaBuilder.buildPresence()
                                    .ofType(oldPresence.getType())
                                    .setStatus(customItem.getStatus())
                                    .setPriority(customItem.getPriority())
                                    .setMode(oldPresence.getMode())
                                    .build();
                                SparkManager.getSessionManager().changePresence(presence);

                                statusBar.setStatus(statusItem.getName() + " - " + customItem.getStatus());
                            }
                        };
                        customAction.putValue(Action.NAME, customItem.getStatus());
                        customAction.putValue(Action.SMALL_ICON, statusItem.getIcon());
                        JMenuItem menuItem = new JMenuItem(customAction);
                        status.add(menuItem);
                    }
                }

            }
        }
    }

    @Override
    public void shutdown() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            tray.remove(trayIcon);
        }
        ChatManager.getInstance().removeChatMessageHandler(chatMessageHandler);
    }

    @Override
    public void uninstall() {
        ChatManager.getInstance().removeChatMessageHandler(chatMessageHandler);
    }

    // Info on new Messages
    @Override
    public void flashWindow(Window window) {
        if (pref.isSystemTrayNotificationEnabled()) {
            trayIcon.setImage(newMessageIcon.getImage());
            if (window instanceof JFrame) {
                String counteredTitle = getCounteredTitle(((JFrame) window).getTitle(), chatMessageHandler.getUnreadMessages());
                ((JFrame) window).setTitle(counteredTitle);
            }
            newMessage = true;
        }
    }

    private String getCounteredTitle(String title, int counter) {
        String stringCounter = String.format("[%s] ", counter);
        String MESSAGE_COUNTER_REG_EXP = "\\[\\d+\\] ";
        String titleWithoutCount = title.replaceFirst(MESSAGE_COUNTER_REG_EXP, "");
        return counter > 0 ? stringCounter + titleWithoutCount : titleWithoutCount;
    }

    @Override
    public void flashWindowStopWhenFocused(Window window) {
        presence = Workspace.getInstance().getStatusBar().getPresence();
        setIconByPresence(presence, availableIcon);
        newMessage = false;
        chatMessageHandler.clearUnreadMessages();
    }

    @Override
    public boolean handleNotification() {
        return true;
    }

    @Override
    public void stopFlashing(Window window) {
        presence = Workspace.getInstance().getStatusBar().getPresence();
        setIconByPresence(presence, availableIcon);
        newMessage = false;
        chatMessageHandler.clearUnreadMessages();
    }

    @Override
    public void stateChanged(Chat chat, ChatState state, Message message) {
        presence = Workspace.getInstance().getStatusBar().getPresence();
        if (ChatState.composing.equals(state)) {
            changeSysTrayIcon();
        } else {
            if (!newMessage) {
                setIconByPresence(presence, newMessageIcon);
            }
        }
    }

    private void setIconByPresence(Presence presence, ImageIcon newTrayIcon) {
        if (presence.getMode() == Presence.Mode.available) {
            trayIcon.setImage(newTrayIcon.getImage());
        } else if (presence.getMode() == Presence.Mode.away) {
            trayIcon.setImage(awayIcon.getImage());
        } else if (presence.getMode() == Presence.Mode.xa) {
            trayIcon.setImage(xawayIcon.getImage());
        } else if (presence.getMode() == Presence.Mode.dnd) {
            trayIcon.setImage(dndIcon.getImage());
        } else {
            trayIcon.setImage(newTrayIcon.getImage());
        }
    }

    private void changeSysTrayIcon() {
        if (pref.isTypingNotificationShown()) {
            trayIcon.setImage(typingIcon.getImage());
        }
    }

    private class TrayMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent event) {
            // if we are using double click on tray icon
            if ((!pref.isUsingSingleTrayClick()
                && event.getButton() == MouseEvent.BUTTON1
                && event.getClickCount() % 2 == 0)
                ||
                // if we're using single click on tray icon
                (pref.isUsingSingleTrayClick()
                    && event.getButton() == MouseEvent.BUTTON1
                    && event.getClickCount() == 1)) {

                // bring the mainwindow to front
                if ((SparkManager.getMainWindow().isVisible())
                    && (SparkManager.getMainWindow().getState() == java.awt.Frame.NORMAL)) {
                    SparkManager.getMainWindow().setVisible(false);
                } else {
                    SparkManager.getMainWindow().setVisible(true);
                    SparkManager.getMainWindow().setState(java.awt.Frame.NORMAL);
                    SparkManager.getMainWindow().toFront();
                }

            } else if (event.getButton() == MouseEvent.BUTTON1) {
                SparkManager.getMainWindow().toFront();
                // SparkManager.getMainWindow().requestFocus();
            } else if (event.getButton() == MouseEvent.BUTTON3) {
                if (popupMenu.isVisible()) {
                    popupMenu.setVisible(false);
                } else {
                    double x = MouseInfo.getPointerInfo().getLocation().getX();
                    double y = MouseInfo.getPointerInfo().getLocation().getY();

                    if (Spark.isMac()) {
                        popupMenu.setLocation((int) x, (int) y);
                    } else {
                        popupMenu.setLocation(event.getX(), event.getY());
                    }

                    popupMenu.setInvoker(popupMenu);
                    popupMenu.setVisible(true);
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent event) {

        }

        @Override
        public void mouseExited(MouseEvent event) {

        }

        @Override
        public void mousePressed(MouseEvent event) {
            // on Mac I would want the window to show when I left-click the Icon
            if (Spark.isMac() && event.getButton() != MouseEvent.BUTTON3) {
                SparkManager.getMainWindow().setVisible(false);
                SparkManager.getMainWindow().setVisible(true);
                SparkManager.getMainWindow().requestFocusInWindow();
                SparkManager.getMainWindow().bringFrameIntoFocus();
                SparkManager.getMainWindow().toFront();
                SparkManager.getMainWindow().requestFocus();
            }
        }

        @Override
        public void mouseReleased(MouseEvent event) {

        }

    }

    private class TrayConnectionListener implements ConnectionListener {
        @Override
        public void connected(XMPPConnection xmppConnection) {
            trayIcon.setImage(availableIcon.getImage());
        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {
            trayIcon.setImage(availableIcon.getImage());
        }

        @Override
        public void connectionClosed() {
            trayIcon.setImage(offlineIcon.getImage());
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            trayIcon.setImage(offlineIcon.getImage());
        }
    }
}
