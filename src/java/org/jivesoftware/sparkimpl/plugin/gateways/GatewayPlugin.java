/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.gateways;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.AIMTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.MSNTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportManager;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class GatewayPlugin implements Plugin {
    public static final String GATEWAY = "gateway";
    private Map<Transport, RolloverButton> uiMap = new HashMap<Transport, RolloverButton>();


    public void initialize() {
        SwingWorker thread = new SwingWorker() {
            public Object construct() {
                try {
                    populateTransports(SparkManager.getConnection());
                    for (final Transport transport : TransportManager.getTransports()) {
                        addTransport(transport);
                    }
                }
                catch (Exception e) {
                    Log.error(e);
                    return false;
                }

                return true;
            }

            public void finished() {
                Boolean b = (Boolean)get();
                if (!b) {
                    return;
                }
                SparkManager.getConnection().addPacketListener(new PacketListener() {
                    public void processPacket(Packet packet) {
                        Presence presence = (Presence)packet;
                        Transport transport = TransportManager.getTransport(packet.getFrom());
                        if (transport != null) {
                            boolean registered = presence != null && presence.getMode() != null;
                            if (presence.getType() == Presence.Type.UNAVAILABLE) {
                                registered = false;
                            }
                            RolloverButton button = uiMap.get(transport);
                            if (!registered) {
                                button.setIcon(transport.getInactiveIcon());
                            }
                            else {
                                button.setIcon(transport.getIcon());
                            }
                        }
                    }
                }, new PacketTypeFilter(Presence.class));

            }
        };

        thread.start();
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
    }

    public void populateTransports(XMPPConnection con) throws Exception {
        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(con);

        DiscoverItems discoItems = manager.discoverItems(con.getServiceName());

        DiscoverItems.Item item;
        DiscoverInfo info;
        DiscoverInfo.Identity identity;

        Iterator it = discoItems.getItems();
        while (it.hasNext()) {
            item = (Item)it.next();
            info = manager.discoverInfo(item.getEntityID());
            Iterator itx = info.getIdentities();
            while (itx.hasNext()) {
                identity = (Identity)itx.next();

                if (identity.getCategory().equalsIgnoreCase(GATEWAY)) {
                    if (item.getEntityID().startsWith("aim.")) {
                        AIMTransport aim = new AIMTransport(item.getEntityID());
                        TransportManager.addTransport(item.getEntityID(), aim);
                    }
                    else if (item.getEntityID().startsWith("msn.")) {
                        MSNTransport msn = new MSNTransport(item.getEntityID());
                        TransportManager.addTransport(item.getEntityID(), msn);
                    }
                }
            }
        }

    }

    private void addTransport(final Transport transport) {
        final StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
        final JPanel commandPanel = statusBar.getCommandPanel();


        final boolean isRegistered = TransportManager.isRegistered(SparkManager.getConnection(), transport);
        final RolloverButton button = new RolloverButton();
        if (!isRegistered) {
            button.setIcon(transport.getInactiveIcon());
        }
        else {
            button.setIcon(transport.getIcon());
        }

        button.setToolTipText(transport.getInstructions());

        commandPanel.add(button);

        button.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                boolean reg = TransportManager.isRegistered(SparkManager.getConnection(), transport);
                if (!reg) {
                    TransportManager.registerWithService(SparkManager.getConnection(), transport.getServiceName());

                    // Send Presence
                    Presence presence = statusBar.getPresence();
                    statusBar.changeAvailability(presence);
                }
                else {
                    int confirm = JOptionPane.showConfirmDialog(SparkManager.getMainWindow(), "Would you like to disable this active transport?", "Disable Transport", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            TransportManager.unregister(SparkManager.getConnection(), transport.getServiceName());
                        }
                        catch (XMPPException e1) {
                            e1.printStackTrace();
                        }
                    }

                }

            }
        });
        uiMap.put(transport, button);

        statusBar.invalidate();
        statusBar.validate();
        statusBar.repaint();
    }
}
