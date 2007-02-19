/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;
import org.jivesoftware.MainWindow;
import org.jivesoftware.MainWindowListener;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.ui.status.StatusItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

/**
 * Handles tray icon operations inside of Spark. Use to display incoming chat requests, incoming messages
 * and general notifications.
 */
public final class Notifications implements ActionListener, MainWindowListener {
    private ImageIcon availableIcon;
    private ImageIcon unavaliableIcon;
    private ImageIcon busyIcon;
    private TrayIcon trayIcon;
    private JPopupMenu notificationDialog;
    private WrappedLabel messageLabel = new WrappedLabel();


    private final JMenuItem openMenu = new JMenuItem(Res.getString("menuitem.open"));
    private final JMenuItem hideMenu = new JMenuItem(Res.getString("menuitem.hide"));
    private final JMenuItem exitMenu = new JMenuItem(Res.getString("menuitem.exit"));

    // Define DND MenuItems
    private final JMenu statusMenu = new JMenu(Res.getString("menuitem.status"));

    private boolean isDisposed;
    private ImageTitlePanel headerLabel = new ImageTitlePanel();

    private JFrame hideWindow = null;

    private long madeVisibleTime = 0;

    /**
     * Creates a new instance of notifications.
     */
    public Notifications() {
        // Tray only works in windows.
        if (Spark.isMac()) {
            return;
        }

        SystemTray tray = SystemTray.getDefaultSystemTray();
        setupNotificationDialog();

        availableIcon = SparkRes.getImageIcon(SparkRes.MAIN_IMAGE);
        unavaliableIcon = SparkRes.getImageIcon(SparkRes.MESSAGE_AWAY);
        busyIcon = SparkRes.getImageIcon(SparkRes.MESSAGE_DND);
        trayIcon = new TrayIcon(availableIcon);
        trayIcon.setToolTip("Spark Client"); // NORES

        JPopupMenu popupMenu = new JPopupMenu(Res.getString("title.tray.information"));

        // Add DND Menus
        addStatusMenuItems();

        // Add Open Menu
        openMenu.setFont(new Font("Dialog", Font.BOLD, 11));
        popupMenu.add(openMenu);

        // Add Hide Menu
        popupMenu.add(hideMenu);
        popupMenu.addSeparator();
        // Add Spark Home Menu

        popupMenu.add(statusMenu);

        // Add Listeners
        openMenu.addActionListener(this);
        hideMenu.addActionListener(this);


        Action logoutAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                SparkManager.getMainWindow().logout(false);
            }
        };

        logoutAction.putValue(Action.NAME, Res.getString("menuitem.logout.no.status"));

        if (Spark.isWindows()) {
            popupMenu.add(logoutAction);
        }
        // Add Exit Menu
        popupMenu.add(exitMenu);
        exitMenu.addActionListener(this);

        SparkManager.getMainWindow().addMainWindowListener(this);

        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addActionListener(this);
        tray.addTrayIcon(trayIcon);

        SparkManager.getSessionManager().addPresenceListener(new PresenceListener() {
            public void presenceChanged(Presence presence) {
                changePresence(presence);
            }
        });
    }

    /**
     * Change the presence of the tray.
     *
     * @param presence the new presence.
     */
    public void changePresence(Presence presence) {
        if (Spark.isMac()) {
            return;
        }

        if (presence.getMode() == Presence.Mode.available || presence.getMode() == Presence.Mode.chat) {
            trayIcon.setIcon(availableIcon);
        }
        else if (presence.getMode() == Presence.Mode.away || presence.getMode() == Presence.Mode.xa) {
            trayIcon.setIcon(unavaliableIcon);
        }
        else {
            trayIcon.setIcon(busyIcon);
        }

        // Get Status Text
        if (presence.isAvailable()) {
            String status = presence.getStatus();
            trayIcon.setToolTip("Spark Client\n" + status);
        }
    }

    /**
     * Displays an incoming message.
     *
     * @param from    who the message was from.
     * @param message the message body.
     */
    public void showMessageReceived(String from, String message) {
        try {
            if (isDisposed) {
                return;
            }
            if (Spark.isMac()) {
                return;
            }
            trayIcon.getLocationOnScreen();
            showNotificationDialog(Res.getString("message.new.message", from), message);
        }
        catch (Exception e) {
        }
    }

    /**
     * Displays an incoming message.
     *
     * @param title   the title to use.
     * @param message the message to display.
     */
    public void showMessage(String title, String message) {
        try {
            if (isDisposed) {
                return;
            }
            if (Spark.isMac()) {
                return;
            }
            showNotificationDialog(title, message);
        }
        catch (Exception e) {
        }
    }


    /**
     * Notify agent that there is a new incoming chat request.
     */
    public final void showIncomingRequest() {
        // Make sure we only show when minimized or not in focus
        if (SparkManager.getMainWindow().isInFocus()) {
            return;
        }

        showNotificationDialog("Incoming Request", "You have a new incoming chat request.");
    }

    /**
     * Stops the icon from flashing.
     */
    public void stopFlashing() {
        Workspace workspace = SparkManager.getWorkspace();

        StatusBar statusBox = workspace.getStatusBar();
        Presence presence = statusBox.getPresence();

        changePresence(presence);

    }

    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (!(o instanceof JMenuItem)) {
            MainWindow window = SparkManager.getMainWindow();
            if (window.isVisible() && window.getState() == Frame.NORMAL) {
                long now = System.currentTimeMillis();
                if (now - madeVisibleTime > 1000) {
                    window.setVisible(false);
                    return;
                }
            }

            madeVisibleTime = System.currentTimeMillis();
            showMainWindow();
            return;
        }

        final JMenuItem item = (JMenuItem)e.getSource();
        if (item == openMenu) {
            showMainWindow();
        }
        else if (item == hideMenu) {
            SparkManager.getMainWindow().setVisible(false);
            hideMenu.setEnabled(false);
        }
        else if (item == exitMenu) {
            isDisposed = true;
            SparkManager.getMainWindow().shutdown();
        }
        else {
            final String status = item.getText();

            // Change Status
            Workspace workspace = SparkManager.getWorkspace();
            StatusItem statusItem = workspace.getStatusBar().getStatusItem(status);
            if (statusItem != null) {
                SparkManager.getSessionManager().changePresence(statusItem.getPresence());
            }
        }
    }

    /**
     * Brings the <code>MainWindow</code> to the front.
     */
    private void showMainWindow() {
        if (hideWindow != null) {
            hideWindow.dispose();
        }

        Workspace workspace = SparkManager.getWorkspace();

        SparkManager.getMainWindow().setState(Frame.NORMAL);
        SparkManager.getMainWindow().setVisible(true);

        StatusBar statusBox = workspace.getStatusBar();
        Presence presence = statusBox.getPresence();
        if (presence.getMode() != Presence.Mode.available) {
            SparkManager.getSessionManager().changePresence(presence);
        }


        notificationDialog.setVisible(false);

        hideMenu.setEnabled(true);

        SparkManager.getMainWindow().toFront();
    }


    /**
     * Add all Registered DND's to MenuItems.
     */
    private void addStatusMenuItems() {
        Workspace workspace = SparkManager.getWorkspace();
        StatusBar statusBar = workspace.getStatusBar();

        Iterator iter = statusBar.getStatusList().iterator();
        while (iter.hasNext()) {
            StatusItem item = (StatusItem)iter.next();
            final JMenuItem menuItem = new JMenuItem(item.getText(), item.getIcon());
            menuItem.addActionListener(this);
            statusMenu.add(menuItem);
        }
    }

    public void shutdown() {
    }

    public void mainWindowActivated() {
    }

    public void mainWindowDeactivated() {
    }

    /**
     * Setup notification dialog.
     */
    private void setupNotificationDialog() {
        notificationDialog = new JPopupMenu();
        notificationDialog.setFocusable(false);
        notificationDialog.setLayout(new BorderLayout());

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Point newLoc = new Point((int)screenSize.getWidth() - 200, (int)screenSize.getHeight() - 150);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        // Add Header Label
        mainPanel.add(headerLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // Add Message Label
        final JScrollPane messageScroller = new JScrollPane(messageLabel);
        mainPanel.add(messageScroller, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        messageScroller.setBackground(Color.white);
        messageScroller.setForeground(Color.white);
        messageLabel.setBackground(Color.white);
        messageScroller.getViewport().setBackground(Color.white);

        // JButton okButton = new JButton("Ok");
        mainPanel.setPreferredSize(new Dimension(200, 150));

        headerLabel.setTitle("Spark"); // NORES
        headerLabel.setTitleFont(new Font("Dialog", Font.BOLD, 10));

        messageLabel.setFont(new Font("Dialog", Font.PLAIN, 11));

        notificationDialog.add(mainPanel, BorderLayout.CENTER);
        notificationDialog.pack();
        notificationDialog.setPreferredSize(new Dimension(200, 150));
        notificationDialog.setLocation(newLoc);
        messageLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                notificationDialog.setVisible(false);
                showMainWindow();
            }
        });

        mainPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                notificationDialog.setVisible(false);
                showMainWindow();
            }
        });
    }

    /**
     * Shows notification dialog for incoming messages.
     *
     * @param from who the body is from.
     * @param body the body of the body.
     */
    public void showNotificationDialog(String from, String body) {
        // Tray only works in windows and mac.
        if (!Spark.isWindows()) {

            return;
        }

        //trayIcon.displayMessage(from, body, TrayIcon.INFO_MESSAGE_TYPE);
    }

    public void showWindow(JPanel panel, int timeout) {

    }


}
