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

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.AIMTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.ICQTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.MSNTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportManager;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.YahooTransport;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Handles Gateways/Transports in Spark.
 *
 * @author Derek DeMoro
 */
public class GatewayPlugin implements Plugin, ContactItemHandler {

    /**
     * Defined Static Variable for Gateways. *
     */
    public static final String GATEWAY = "gateway";

    private Map<Transport, RolloverButton> uiMap = new HashMap<Transport, RolloverButton>();


    public void initialize() {
        SwingWorker thread = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(10000);
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
                Boolean transportExists = (Boolean)get();
                if (!transportExists) {
                    return;
                }

                // Register presences.
                registerPresences();

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

    private void populateTransports(XMPPConnection con) throws Exception {
        ServiceDiscoveryManager discoveryManager = ServiceDiscoveryManager.getInstanceFor(con);

        DiscoverItems discoItems = SparkManager.getSessionManager().getDiscoveredItems();

        DiscoverItems.Item item;
        DiscoverInfo info;
        DiscoverInfo.Identity identity;

        Iterator items = discoItems.getItems();
        while (items.hasNext()) {
            item = (Item)items.next();
            try {
                info = discoveryManager.discoverInfo(item.getEntityID());
            }
            catch (XMPPException e) {
                Log.error(e);
                continue;
            }
            Iterator identities = info.getIdentities();
            while (identities.hasNext()) {
                identity = (Identity)identities.next();

                if (identity.getCategory().equalsIgnoreCase(GATEWAY)) {
                    if ("aim".equals(identity.getType())) {
                        AIMTransport aim = new AIMTransport(item.getEntityID());
                        TransportManager.addTransport(item.getEntityID(), aim);
                    }
                    else if ("msn".equals(identity.getType())) {
                        MSNTransport msn = new MSNTransport(item.getEntityID());
                        TransportManager.addTransport(item.getEntityID(), msn);
                    }
                    else if ("yahoo".equals(identity.getType())) {
                        YahooTransport yahoo = new YahooTransport(item.getEntityID());
                        TransportManager.addTransport(item.getEntityID(), yahoo);
                    }
                    else if ("icq".equals(identity.getType())) {
                        ICQTransport icq = new ICQTransport(item.getEntityID());
                        TransportManager.addTransport(item.getEntityID(), icq);
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
                    presence.setTo(transport.getServiceName());
                    SparkManager.getConnection().sendPacket(presence);
                }
                else {
                    int confirm = JOptionPane.showConfirmDialog(SparkManager.getMainWindow(), Res.getString("message.disable.transport"), Res.getString("title.disable.transport"), JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            TransportManager.unregister(SparkManager.getConnection(), transport.getServiceName());
                        }
                        catch (XMPPException e1) {
                            Log.error(e1);
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


    private void registerPresences() {
        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Presence presence = (Presence)packet;
                Transport transport = TransportManager.getTransport(packet.getFrom());
                if (transport != null) {
                    boolean registered = presence != null && presence.getMode() != null;
                    if (presence.getType() == Presence.Type.unavailable) {
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


        ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addContactItemHandler(this);

        // Iterate through Contacts and check for
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        for(ContactGroup contactGroup : contactList.getContactGroups()){
            for(ContactItem contactItem : contactGroup.getContactItems()){
                Presence presence = contactItem.getPresence();
                boolean handle = handlePresence(contactItem, presence);
                if(handle){
                    contactGroup.fireContactGroupUpdated();
                }
            }
        }



    }

    public boolean handlePresence(ContactItem item, Presence presence) {
        if (presence != null) {
            String domain = StringUtils.parseServer(presence.getFrom());
            Transport transport = TransportManager.getTransport(domain);
            if (transport != null) {
                if (presence.getType() == Presence.Type.available) {
                    item.setSideIcon(transport.getIcon());
                }
                else {
                    item.setSideIcon(transport.getInactiveIcon());
                }
                return false;
            }
        }

        return false;
    }

    public boolean handleDoubleClick(ContactItem item) {
        return false;
    }

    public Icon getIcon(Presence presence) {
        if (presence == null) {
            return null;
        }
        String domain = StringUtils.parseServer(presence.getFrom());
        Transport transport = TransportManager.getTransport(domain);
        if (transport != null) {
            if (presence.getType() == Presence.Type.available) {
                return transport.getIcon();
            }
            else {
                return transport.getInactiveIcon();
            }
        }
        return null;
    }

    public Icon getTabIcon(Presence presence) {
        return null;
    }
}
