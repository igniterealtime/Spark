/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.plugin.jingle;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.jingleold.JingleManager;
import org.jivesoftware.smackx.jingleold.JingleSession;
import org.jivesoftware.smackx.jingleold.JingleSessionRequest;
import org.jivesoftware.smackx.jingleold.media.JingleMediaManager;
import org.jivesoftware.smackx.jingleold.mediaimpl.jmf.JmfMediaManager;
import org.jivesoftware.smackx.jingleold.mediaimpl.jspeex.SpeexMediaManager;
import org.jivesoftware.smackx.jingleold.nat.BridgedTransportManager;
import org.jivesoftware.smackx.jingleold.nat.ICETransportManager;
import org.jivesoftware.smackx.jingleold.nat.JingleTransportManager;
import org.jivesoftware.smackx.jingleold.nat.STUN;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.phone.Phone;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.util.*;


/**
 * A simple Jingle Plugin for Spark that uses server Media Proxy for the transport and NAT Traversal
 */
public class JinglePlugin implements Plugin, Phone, ConnectionListener {

    private static final String JINGLE_NAMESPACE = "http://www.xmpp.org/extensions/xep-0166.html#ns";
    private JingleManager jingleManager;
    private String stunServer = "";
    private int stunPort = 0;
    private boolean readyToConnect = false;
    private final Map<String, Boolean> jingleFeature = new HashMap<>();
    private boolean fallbackStunEnabled = false;
    private final LocalPreferences pref = SettingsManager.getLocalPreferences();

    @Override
    public void initialize() {
        // Add Jingle to a discovered items list.
        SparkManager.addFeature(JINGLE_NAMESPACE);

        //If there is a server entered in spark.properties use it as fallback
        if (!pref.getStunFallbackHost().isEmpty()) {
            fallbackStunEnabled = true;
        }

        // Get the default port
        stunPort = pref.getStunFallbackPort();

        // Set Jingle Enabled
        JingleManager.setJingleServiceEnabled();
        JingleManager.setServiceEnabled(SparkManager.getConnection(), true);

        // Add to PhoneManager
        PhoneManager.getInstance().addPhone(this);

        // Adds a tab handler.
        SparkManager.getChatManager().addSparkTabHandler(new JingleTabHandler());

        final SwingWorker jingleLoadingThread = new SwingWorker() {
            @Override
            public Object construct() {
                if (fallbackStunEnabled) {
                    stunServer = pref.getStunFallbackHost();
                    readyToConnect = true;
                }

                try {
                    if (STUN.serviceAvailable(SparkManager.getConnection())) {
                        STUN stun = STUN.getSTUNServer(SparkManager.getConnection());
                        if (stun != null) {
                            List<STUN.StunServerAddress> servers = stun.getServers();
                            if (!servers.isEmpty()) {
                                stunServer = servers.get(0).getServer();
                                stunPort = Integer.parseInt(servers.get(0).getPort());
                                readyToConnect = true;
                            }
                        }
                    }

                    // Initializes Jingle with STUN and media managers
                    if (readyToConnect) {
                        JingleTransportManager transportManager = new ICETransportManager(SparkManager.getConnection(), stunServer, stunPort);
                        List<JingleMediaManager> mediaManagers = new ArrayList<>();

                        // Get the Locator from the Settings
                        String locator = SettingsManager.getLocalPreferences().getAudioDevice();

                        mediaManagers.add(new JmfMediaManager(locator, transportManager));
                        mediaManagers.add(new SpeexMediaManager(transportManager));
                        //mediaManagers.add(new ScreenShareMediaManager(transportManager));

                        jingleManager = new JingleManager(SparkManager.getConnection(), mediaManagers);
                        if (transportManager instanceof BridgedTransportManager) {
                            jingleManager.addCreationListener((BridgedTransportManager) transportManager);
                        } else if (transportManager instanceof ICETransportManager) {
                            jingleManager.addCreationListener((ICETransportManager) transportManager);
                        }
                    }
                } catch (Exception e) {
                    Log.error("Unable to initialize", e);
                }
                return true;
            }

            @Override
            public void finished() {
                addListeners();
            }
        };

        jingleLoadingThread.start();

        // Add Presence listener for better service discovery.
        addPresenceListener();

        SparkManager.getConnection().addConnectionListener(this);
    }

    /**
     * Adds Jingle and ChatRoom listeners.
     */
    private void addListeners() {
        if (jingleManager == null) {
            if (readyToConnect) {
                Log.error("Unable to resolve Jingle Connection (Host: " + stunServer + " Port: " + stunPort + ")");
            }
            return;
        }
        // Listen in for new incoming Jingle requests.
        jingleManager.addJingleSessionRequestListener(request -> SwingUtilities.invokeLater(() -> incomingJingleSession(request)));
    }

    @Override
    public Collection<Action> getPhoneActions(EntityBareJid jid) {
        // Do not even disco gateway clients.
        if (TransportUtils.isFromGateway(jid) || jingleManager == null) {
            return List.of();
        }

        Boolean supportsJingle = jingleFeature.get(jid.toString());
        if (supportsJingle == null) {
            // Disco for event.
            // Obtain the ServiceDiscoveryManager associated with my XMPPConnection
            ServiceDiscoveryManager discoManager = SparkManager.getDiscoManager();

            EntityFullJid fullJID = PresenceManager.getFullyQualifiedJID(jid);

            // Get the items of a given XMPP entity
            DiscoverInfo discoverInfo = null;
            try {
                discoverInfo = discoManager.discoverInfo(fullJID);
            } catch (Exception e) {
                Log.debug("Unable to disco " + fullJID);
            }

            if (discoverInfo != null) {
                // Get the discovered items of the queried XMPP entity
                supportsJingle = discoverInfo.containsFeature(JINGLE_NAMESPACE);
                jingleFeature.put(jid.toString(), supportsJingle);
            } else {
                jingleFeature.put(jid.toString(), false);
                supportsJingle = false;
            }
        }

        if (!supportsJingle) {
            return Collections.emptyList();
        }

        Action action = new AbstractAction() {
            private static final long serialVersionUID = 1467355627829748086L;

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    placeCall(jid);
                } catch (SmackException e1) {
                    Log.warning("Unable to place call to " + jid, e1);
                }
            }
        };

        action.putValue(Action.NAME, "<html><b>" + JingleResources.getString("label.computer.to.computer") + "</b></html>");
        action.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.COMPUTER_IMAGE_16x16));
        List<Action> actions = new ArrayList<>(1);
        actions.add(action);
        return actions;
    }


    public void placeCall(BareJid bareJid) throws SmackException {
        // cancel call request if no Media Locator available
        if (PhoneManager.isUseStaticLocator() && PhoneManager.isUsingMediaLocator()) {
            return;
        }
        PhoneManager.setUsingMediaLocator(true);
        EntityFullJid jid = SparkManager.getUserManager().getFullJID(bareJid);

        ChatRoom room = SparkManager.getChatManager().getChatRoom(jid.asEntityBareJid());
        if (JingleStateManager.getInstance().getJingleRoomState(room) != null) {
            return;
        }

        SparkManager.getChatManager().getChatContainer().activateChatRoom(room);
        // Create a new Jingle Call with a full JID
        JingleSession session;
        try {
            session = jingleManager.createOutgoingJingleSession(jid);
        } catch (XMPPException e) {
            Log.error(e);
            return;
        }

        TranscriptWindow transcriptWindow = room.getTranscriptWindow();
        StyledDocument doc = (StyledDocument) transcriptWindow.getDocument();
        Style style = doc.addStyle("StyleName", null);

        OutgoingCall outgoingCall = new OutgoingCall();
        outgoingCall.handleOutgoingCall(session, room, jid);
        StyleConstants.setComponent(style, outgoingCall);

        // Insert the image at the end of the text
        try {
            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
        } catch (BadLocationException e) {
            Log.error(e);
        }

        room.scrollToBottom();
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

    /**
     * Notify user that a new incoming jingle request has been receieved.
     *
     * @param request the <code>JingleSessionRequest</code>.
     */
    private void incomingJingleSession(JingleSessionRequest request) {
        if (PhoneManager.isUseStaticLocator() && PhoneManager.isUsingMediaLocator()) {
            request.reject();
        } else {
            PhoneManager.setUsingMediaLocator(true);
            new IncomingCall(request);
        }
    }

    /**
     * Adds a presence listener to remove offline users from discovered features.
     */
    private void addPresenceListener() {
        // Check presence changes
        SparkManager.getConnection().addAsyncStanzaListener(stanza -> {
            Presence presence = (Presence) stanza;
            if (!presence.isAvailable()) {
                Jid from = presence.getFrom();
                if (from != null) {
                    // Remove from
                    jingleFeature.remove(from.toString());
                }
            }
        }, new StanzaTypeFilter(Presence.class));
    }

    @Override
    public void connected(XMPPConnection xmppConnection) {
        SparkManager.addFeature(JINGLE_NAMESPACE);
    }

}
