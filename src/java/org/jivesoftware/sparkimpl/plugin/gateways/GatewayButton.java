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
package org.jivesoftware.sparkimpl.plugin.gateways;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;

/**
 *
 */
public class GatewayButton extends JPanel implements GatewayItem {
	private static final long serialVersionUID = -2692869826501622612L;
	private final RolloverButton button = new RolloverButton();
    private Transport transport;
    private boolean signedIn;

    public GatewayButton(final Transport transport) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        this.transport = transport;

        final StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
        final JPanel commandPanel = SparkManager.getWorkspace().getCommandPanel();

        if (PresenceManager.isOnline(transport.getServiceName())) {
            button.setIcon(transport.getIcon());
        }
        else {
            button.setIcon(transport.getInactiveIcon());
        }
        button.setToolTipText(transport.getName());

        commandPanel.add(button);

        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                handlePopup(mouseEvent);
            }
        });
        commandPanel.updateUI();
        final Runnable registerThread = new Runnable() {
            public void run() {
                // Send directed presence if registered with this transport.
                final boolean isRegistered = TransportUtils.isRegistered(SparkManager.getConnection(), transport);
                if (isRegistered) {
                    // Check if auto login is set.
                    boolean autoJoin = TransportUtils.autoJoinService(transport.getServiceName());
                    if (autoJoin) {
                        Presence oldPresence = statusBar.getPresence();
                        Presence presence = new Presence(oldPresence.getType(), oldPresence.getStatus(), oldPresence.getPriority(), oldPresence.getMode());
                        presence.setTo(transport.getServiceName());
                        SparkManager.getConnection().sendPacket(presence);
                    }
                }
            }
        };

        TaskEngine.getInstance().submit(registerThread);
    }

    /**
     * Handles the display of a popup menu when a transport button is clicked.
     *
     * @param event the MouseEvent.
     */
    private void handlePopup(MouseEvent event) {
        final JPopupMenu popupMenu = new JPopupMenu();

        // Create action to sign off of transport.
        final JMenuItem signOutMenu = new JMenuItem(Res.getString("menuitem.sign.out"));
        signOutMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                final Presence offlinePresence = new Presence(Presence.Type.unavailable);
                offlinePresence.setTo(transport.getServiceName());

                SparkManager.getConnection().sendPacket(offlinePresence);
            }
        });

        // Create menu to sign in.
        final JMenuItem signInMenu = new JMenuItem(Res.getString("menuitem.sign.in"));
        signInMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                final Presence onlinePresence = new Presence(Presence.Type.available);
                onlinePresence.setTo(transport.getServiceName());
                SparkManager.getConnection().sendPacket(onlinePresence);
            }
        });

        // Create menu item to  toggle signing in at startup.
        final JCheckBoxMenuItem signInAtLoginMenu = new JCheckBoxMenuItem();
        signInAtLoginMenu.setText(Res.getString("menuitem.sign.in.at.login"));
        signInAtLoginMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                TransportUtils.setAutoJoin(transport.getServiceName(), signInAtLoginMenu.isSelected());
            }
        });

        final JMenuItem registerMenu = new JMenuItem(Res.getString("menuitem.enter.login.information"));
        registerMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                TransportRegistrationDialog registrationDialog = new TransportRegistrationDialog(transport.getServiceName());
                registrationDialog.invoke();
            }
        });

        // Create action to delete login information
        final JMenuItem unregisterMenu = new JMenuItem(Res.getString("menuitem.delete.login.information"));
        unregisterMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                int confirm = JOptionPane.showConfirmDialog(SparkManager.getMainWindow(), Res.getString("message.disable.transport", transport.getName()), Res.getString("title.disable.transport"), JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        TransportUtils.unregister(SparkManager.getConnection(), transport.getServiceName());
                    }
                    catch (XMPPException e1) {
                        Log.error(e1);
                    }
                }
            }
        });

        // If user is not registered with the gateway
        boolean reg = TransportUtils.isRegistered(SparkManager.getConnection(), transport);
        if (!reg) {
            popupMenu.add(registerMenu);
            popupMenu.addSeparator();
            signInMenu.setEnabled(false);
            popupMenu.add(signInMenu);
            signInAtLoginMenu.setEnabled(false);
            popupMenu.add(signInAtLoginMenu);
            popupMenu.show((Component)event.getSource(), event.getX(), event.getY());
            return;
        }

        if (signedIn) {
            popupMenu.add(signOutMenu);
        }
        else {
            popupMenu.add(signInMenu);
        }

        boolean autoJoin = TransportUtils.autoJoinService(transport.getServiceName());
        signInAtLoginMenu.setSelected(autoJoin);

        popupMenu.add(signInAtLoginMenu);
        popupMenu.addSeparator();
        popupMenu.add(unregisterMenu);
        popupMenu.show((Component)event.getSource(), event.getX(), event.getY());
    }

    public void signedIn(boolean signedIn) {
        if (!signedIn) {
            button.setIcon(transport.getInactiveIcon());
        }
        else {
            button.setIcon(transport.getIcon());
        }

        this.signedIn = signedIn;
    }

    public boolean isLoggedIn() {
        return signedIn;
    }


}
