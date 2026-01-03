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
package org.jivesoftware.sparkimpl.plugin.gateways;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.component.VerticalFlowLayout;

import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.*;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;

import javax.swing.*;


import java.awt.Color;
import java.util.HashMap;
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
    private boolean useTab;

    private final Map<Transport, GatewayItem> uiMap = new HashMap<>();
    private final JPanel transferTab = new JPanel();

    @Override
	public void initialize() {
	ProviderManager.addIQProvider(Gateway.ELEMENT_NAME, Gateway.NAMESPACE, new Gateway.Provider());
	LocalPreferences localPref = SettingsManager.getLocalPreferences();
	useTab = localPref.getShowTransportTab();
	transferTab.setBackground((Color)UIManager.get("ContactItem.background"));
        SwingWorker thread = new SwingWorker() {
            @Override
			public Object construct() {
                try {
                    // Let's try and avoid any timing issues with the gateway presence.
                    Thread.sleep(5000);
                    populateTransports();
                }
                catch (Exception e) {
                    Log.error(e);
                    return false;
                }

                return true;
            }

            @Override
			public void finished() {
        	
        	transferTab.setLayout(new VerticalFlowLayout(0,0,0,true,false));
        	Boolean transportExists = (Boolean)get();
                if (!transportExists) {
                    return;
                } 
                
                
                
                if (TransportUtils.getTransports().size() > 0  && useTab) {
                   SparkManager.getWorkspace().getWorkspacePane().addTab(Res.getString("title.transports"), SparkRes.getImageIcon(SparkRes.TRANSPORT_ICON), transferTab); 
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

    @Override
	public void shutdown() {
    }

    @Override
	public boolean canShutDown() {
        return false;
    }

    @Override
	public void uninstall() {
    }

    private void populateTransports() {
        DiscoverItems discoItems = SparkManager.getSessionManager().getDiscoveredItems();
        for (DiscoverItems.Item item : discoItems.getItems()) {
            String entityName = item.getEntityID().toString();
            int dotPos = entityName.indexOf('.');
            if (dotPos == -1) {
                continue;
            }
            String transportPrefix = entityName.substring(0, dotPos);
            switch (transportPrefix) {
                case "xmpp":
                    XMPPTransport xmppTransport = new XMPPTransport(item.getEntityID().asDomainBareJid());
                    TransportUtils.addTransport(item.getEntityID().asDomainBareJid(), xmppTransport);
                    break;
                case "irc":
                    IRCTransport ircTransport = new IRCTransport(item.getEntityID().asDomainBareJid());
                    TransportUtils.addTransport(item.getEntityID().asDomainBareJid(), ircTransport);
                    break;
                case "sip":
                case "simple":
                    SimpleTransport simpleTransport = new SimpleTransport(item.getEntityID().asDomainBareJid());
                    TransportUtils.addTransport(item.getEntityID().asDomainBareJid(), simpleTransport);
                    break;
                case "gadugadu":
                    GaduGaduTransport gadugaduTransport = new GaduGaduTransport(item.getEntityID().asDomainBareJid());
                    TransportUtils.addTransport(item.getEntityID().asDomainBareJid(), gadugaduTransport);
                    break;
                case "qq":
                    QQTransport qqTransport = new QQTransport(item.getEntityID().asDomainBareJid());
                    TransportUtils.addTransport(item.getEntityID().asDomainBareJid(), qqTransport);
                    break;
                case "sametime":
                    SametimeTransport sametimeTransport = new SametimeTransport(item.getEntityID().asDomainBareJid());
                    TransportUtils.addTransport(item.getEntityID().asDomainBareJid(), sametimeTransport);
                    break;
                case "facebook":
                    FacebookTransport facebookTransport = new FacebookTransport(item.getEntityID().asDomainBareJid());
                    TransportUtils.addTransport(item.getEntityID().asDomainBareJid(), facebookTransport);
                    break;
                case "myspace":
                case "myspaceim":
                    MySpaceTransport myspaceTransport = new MySpaceTransport(item.getEntityID().asDomainBareJid());
                    TransportUtils.addTransport(item.getEntityID().asDomainBareJid(), myspaceTransport);
                    break;
            }
        }
    }

    private void addTransport(final Transport transport) {
	GatewayItem item;
	if (useTab)
        {
            item = new GatewayTabItem(transport);
            transferTab.add((GatewayTabItem)item);
            }
        else
        {
           item = new GatewayButton(transport);
        }
        uiMap.put(transport, item);
      //  transferTab.add(button);
      //  transferTab.add(new GatewayTabItem(transport));
    }

    private void registerPresenceListener() {
        StanzaFilter orFilter = new OrFilter(new StanzaTypeFilter(Presence.class), new StanzaTypeFilter(Message.class));

        SparkManager.getConnection().addAsyncStanzaListener( stanza -> {
            if (stanza instanceof Presence) {
                Presence presence = (Presence)stanza;
                Transport transport = TransportUtils.getTransport(stanza.getFrom().asDomainBareJid());
                if (transport != null) {
                    boolean registered = true;
                    if (presence.getType() == Presence.Type.unavailable) {
                        registered = false;
                    }


                    GatewayItem button = uiMap.get(transport);

                    button.signedIn(registered);

                    SwingWorker worker = new SwingWorker() {

            @Override
            public Object construct() {
            transferTab.revalidate();
            transferTab.repaint();
            return 41;
            }
        };
        worker.start();
                }
            }
            else if (stanza instanceof Message) {
                Message message = (Message)stanza;
                DomainBareJid from = message.getFrom().asDomainBareJid();
                boolean hasError = message.getType() == Message.Type.error;
                String body = message.getBody();

                if (from != null && hasError) {
                    Transport transport = TransportUtils.getTransport(from);
                    if (transport != null) {
                        String title = "Alert from " + transport.getName();
                        // Show error
                        MessageDialog.showAlert(body, title, "Information", SparkRes.getImageIcon(SparkRes.INFORMATION_IMAGE));
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
                    DomainBareJid domain = presence.getFrom().asDomainBareJid();
                    Transport transport = TransportUtils.getTransport(domain);
                    if (transport != null) {
                        handlePresence(contactItem, presence);
                        contactGroup.fireContactGroupUpdated();
                    }
                }
            }
        }

        SparkManager.getSessionManager().addPresenceListener( presence -> {
            for (Transport transport : TransportUtils.getTransports()) {
                GatewayItem button = uiMap.get(transport);
                if (button.isLoggedIn()) {
                    if (!presence.isAvailable()) {
                        return;
                    }
                    // Create new presence
                    Presence p = StanzaBuilder.buildPresence()
                        .ofType(presence.getType())
                        .setStatus(presence.getStatus())
                        .setPriority(presence.getPriority())
                        .setMode(presence.getMode())
                        .build();

                    p.setTo(transport.getXMPPServiceDomain());
                    try
                    {
                        SparkManager.getConnection().sendStanza(p);
                    }
                    catch ( SmackException.NotConnectedException | InterruptedException e )
                    {
                        Log.warning( "Unable to forward presence change to transport.", e );
                    }
                }
            }
        } );
    }


    @Override
	public boolean handlePresence(ContactItem item, Presence presence) {
        if (presence.isAvailable()) {
            Jid from = presence.getFrom();
            if (from == null) {
                return false;
            }
            DomainBareJid domain = from.asDomainBareJid();
            Transport transport = TransportUtils.getTransport(domain);
            if (transport != null) {
                if (presence.getType() == Presence.Type.available) {
                    item.setSpecialIcon(transport.getIcon());
                }
                else {
                    item.setSpecialIcon(transport.getInactiveIcon());
                }
                return false;
            }
        }

        return false;
    }

    @Override
	public boolean handleDoubleClick(ContactItem item) {
        return false;
    }

    @Override
	public Icon getIcon(BareJid jid) {
        DomainBareJid domain = jid.asDomainBareJid();
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

    @Override
	public Icon getTabIcon(Presence presence) {
        return null;
    }
}
