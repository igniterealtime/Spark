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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.AIMTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.ICQTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.MSNTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.YahooTransport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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

    private Map<Transport, GatewayButton> uiMap = new HashMap<Transport, GatewayButton>();


    public void initialize() {
        ProviderManager.getInstance().addIQProvider(Gateway.ELEMENT_NAME, Gateway.NAMESPACE, new Gateway.Provider());

        SwingWorker thread = new SwingWorker() {
            public Object construct() {
                try {
                    populateTransports(SparkManager.getConnection());
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

                if (TransportUtils.getTransports().size() > 0) {
                    final JPanel commandPanel = SparkManager.getWorkspace().getCommandPanel();
                    final JLabel dividerLabel = new JLabel(SparkRes.getImageIcon("DIVIDER_IMAGE"));
                    commandPanel.add(dividerLabel);
                }

                for (final Transport transport : TransportUtils.getTransports()) {
                    addTransport(transport);
                }

                // Register presences.
                registerPresenceListener();
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
                Log.debug("Unable to locate " + item);
                continue;
            }
            Iterator identities = info.getIdentities();
            while (identities.hasNext()) {
                identity = (Identity)identities.next();

                if (identity.getCategory().equalsIgnoreCase(GATEWAY)) {
                    if ("aim".equals(identity.getType())) {
                        AIMTransport aim = new AIMTransport(item.getEntityID());
                        TransportUtils.addTransport(item.getEntityID(), aim);
                    }
                    else if ("msn".equals(identity.getType())) {
                        MSNTransport msn = new MSNTransport(item.getEntityID());
                        TransportUtils.addTransport(item.getEntityID(), msn);
                    }
                    else if ("yahoo".equals(identity.getType())) {
                        YahooTransport yahoo = new YahooTransport(item.getEntityID());
                        TransportUtils.addTransport(item.getEntityID(), yahoo);
                    }
                    else if ("icq".equals(identity.getType())) {
                        ICQTransport icq = new ICQTransport(item.getEntityID());
                        TransportUtils.addTransport(item.getEntityID(), icq);
                    }
                }
            }
        }

    }

    private void addTransport(final Transport transport) {
        final GatewayButton button = new GatewayButton(transport);
        uiMap.put(transport, button);
    }

    private void registerPresenceListener() {
        PacketFilter orFilter = new OrFilter(new PacketTypeFilter(Presence.class), new PacketTypeFilter(Message.class));

        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                if (packet instanceof Presence) {
                    Presence presence = (Presence)packet;
                    Transport transport = TransportUtils.getTransport(packet.getFrom());
                    if (transport != null) {
                        boolean registered = true;
                        if (presence.getType() == Presence.Type.unavailable) {
                            registered = false;
                        }

                        GatewayButton button = uiMap.get(transport);
                        button.signedIn(registered);
                    }
                }
                else if (packet instanceof Message) {
                    Message message = (Message)packet;
                    String from = message.getFrom();
                    boolean hasError = message.getType() == Message.Type.error;
                    String body = message.getBody();

                    if (from != null && hasError) {
                        Transport transport = TransportUtils.getTransport(from);
                        String title = "Alert from " + transport.getName();
                        if (transport != null) {
                            // Show error
                            MessageDialog.showAlert(body, title, "Information", SparkRes.getImageIcon(SparkRes.INFORMATION_IMAGE));
                        }
                    }
                }
            }
        }, orFilter);


        ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addContactItemHandler(this);

        // Iterate through Contacts and check for
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        for (ContactGroup contactGroup : contactList.getContactGroups()) {
            for (ContactItem contactItem : contactGroup.getContactItems()) {
                Presence presence = contactItem.getPresence();
                if (presence.isAvailable()) {
                    String domain = StringUtils.parseServer(presence.getFrom());
                    Transport transport = TransportUtils.getTransport(domain);
                    if (transport != null) {
                        handlePresence(contactItem, presence);
                        contactGroup.fireContactGroupUpdated();
                    }
                }
            }
        }

        SparkManager.getSessionManager().addPresenceListener(new PresenceListener() {
            public void presenceChanged(Presence presence) {
                for (Transport transport : TransportUtils.getTransports()) {
                    GatewayButton button = uiMap.get(transport);
                    if (button.isLoggedIn()) {
                        // Create new presence
                        Presence p = new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());
                        p.setTo(transport.getServiceName());
                        SparkManager.getConnection().sendPacket(p);
                    }
                }
            }
        });
    }


    public boolean handlePresence(ContactItem item, Presence presence) {
        if (presence.isAvailable()) {
            String domain = StringUtils.parseServer(presence.getFrom());
            Transport transport = TransportUtils.getTransport(domain);
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

    public Icon getIcon(String jid) {
        String domain = StringUtils.parseServer(jid);
        Transport transport = TransportUtils.getTransport(domain);
        if (transport != null) {
            if (PresenceManager.isOnline(jid)) {
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
