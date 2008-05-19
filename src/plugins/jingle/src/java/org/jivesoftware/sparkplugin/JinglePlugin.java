/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.jingle.JingleManager;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.JingleSessionRequest;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionRequestListener;
import org.jivesoftware.smackx.jingle.media.JingleMediaManager;
import org.jivesoftware.smackx.jingle.mediaimpl.jmf.JmfMediaManager;
import org.jivesoftware.smackx.jingle.mediaimpl.jspeex.SpeexMediaManager;
import org.jivesoftware.smackx.jingle.mediaimpl.sshare.ScreenShareMediaManager;
import org.jivesoftware.smackx.jingle.nat.BridgedTransportManager;
import org.jivesoftware.smackx.jingle.nat.ICETransportManager;
import org.jivesoftware.smackx.jingle.nat.JingleTransportManager;
import org.jivesoftware.smackx.jingle.nat.STUN;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.phone.Phone;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;

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

    private JingleManager jingleManager;

    private static final String JINGLE_NAMESPACE = "http://www.xmpp.org/extensions/xep-0166.html#ns";

    private Map<String, Boolean> jingleFeature = new HashMap<String, Boolean>();

    public void initialize() {
        // Add Jingle to discovered items list.
        SparkManager.addFeature(JINGLE_NAMESPACE);

        // Add to PhoneManager
        PhoneManager.getInstance().addPhone(this);

        // Adds a tab handler.
        SparkManager.getChatManager().addSparkTabHandler(new JingleTabHandler());

        final SwingWorker jingleLoadingThread = new SwingWorker() {
            public Object construct() {

                String stunServer = "stun.xten.net";
                int stunPort = 3478;

                if (STUN.serviceAvailable(SparkManager.getConnection())) {
                    STUN stun = STUN.getSTUNServer(SparkManager.getConnection());
                    if (stun != null) {
                        List<STUN.StunServerAddress> servers = stun.getServers();
                        if (servers.size() > 0) {
                            stunServer = servers.get(0).getServer();
                            stunPort = Integer.parseInt(servers.get(0).getPort());
                        }
                    }
                }

                JingleTransportManager transportManager = new ICETransportManager(SparkManager.getConnection(), stunServer, stunPort);
                List<JingleMediaManager> mediaManagers = new ArrayList<JingleMediaManager>();

                // if running on Windows use Direct Sound for better performance
                String locator = Spark.isWindows() ? "dsound://" : "javasound://";

                mediaManagers.add(new JmfMediaManager(locator, transportManager));
                mediaManagers.add(new SpeexMediaManager(transportManager));
                mediaManagers.add(new ScreenShareMediaManager(transportManager));

                jingleManager = new JingleManager(SparkManager.getConnection(), mediaManagers);

                if (transportManager instanceof BridgedTransportManager) {
                    jingleManager.addCreationListener((BridgedTransportManager)transportManager);
                }
                else if (transportManager instanceof ICETransportManager) {
                    jingleManager.addCreationListener((ICETransportManager)transportManager);
                }

                return true;
            }

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
            Log.error("Unable to resolve Jingle Connection");
            return;
        }

        // Listen in for new incoming Jingle requests.
        jingleManager.addJingleSessionRequestListener(new JingleSessionRequestListener() {
            public void sessionRequested(final JingleSessionRequest request) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        incomingJingleSession(request);
                    }
                });
            }
        });
    }


    public Collection<Action> getPhoneActions(final String jid) {
        // Do not even disco gateway clients.
        if (TransportUtils.isFromGateway(jid) || jingleManager == null) {
            return Collections.emptyList();
        }

        Boolean supportsJingle = jingleFeature.get(StringUtils.parseBareAddress(jid));
        if (supportsJingle == null) {
            // Disco for event.
            // Obtain the ServiceDiscoveryManager associated with my XMPPConnection
            ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());

            String fullJID = PresenceManager.getFullyQualifiedJID(jid);

            // Get the items of a given XMPP entity
            DiscoverInfo discoverInfo = null;
            try {
                discoverInfo = discoManager.discoverInfo(fullJID);
            }
            catch (XMPPException e) {
                Log.debug("Unable to disco " + fullJID);
            }

            if (discoverInfo != null) {
                // Get the discovered items of the queried XMPP entity
                supportsJingle = discoverInfo.containsFeature(JINGLE_NAMESPACE);
                jingleFeature.put(jid, supportsJingle);
            }
            else {
                jingleFeature.put(jid, false);
                supportsJingle = false;
            }
        }

        if (!supportsJingle) {
            return Collections.emptyList();
        }

        final List<Action> actions = new ArrayList<Action>();
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                placeCall(jid);
            }
        };

        action.putValue(Action.NAME, "<html><b>Computer To Computer</b></html>");
        action.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.COMPUTER_IMAGE_16x16));
        actions.add(action);
        return actions;
    }


    public void placeCall(String jid) {

        // cancel call request if no Media Locator available
        if (PhoneManager.isUseStaticLocator() && PhoneManager.isUsingMediaLocator()) {
            return;
        }

        PhoneManager.setUsingMediaLocator(true);

        jid = SparkManager.getUserManager().getFullJID(jid);

        ChatRoom room = SparkManager.getChatManager().getChatRoom(StringUtils.parseBareAddress(jid));
        if (JingleStateManager.getInstance().getJingleRoomState(room) != null) {
            return;
        }

        SparkManager.getChatManager().getChatContainer().activateChatRoom(room);

        // Create a new Jingle Call with a full JID
        JingleSession session = null;
        try {
            session = jingleManager.createOutgoingJingleSession(jid);
        }
        catch (XMPPException e) {
            Log.error(e);
        }

        TranscriptWindow transcriptWindow = room.getTranscriptWindow();
        StyledDocument doc = (StyledDocument)transcriptWindow.getDocument();
        Style style = doc.addStyle("StyleName", null);

        OutgoingCall outgoingCall = new OutgoingCall();
        outgoingCall.handleOutgoingCall(session, room, jid);
        StyleConstants.setComponent(style, outgoingCall);

        // Insert the image at the end of the text
        try {
            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
        }
        catch (BadLocationException e) {
            Log.error(e);
        }

        room.scrollToBottom();
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

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
        }
        else {
            PhoneManager.setUsingMediaLocator(true);
            new IncomingCall(request);
        }
    }

    /**
     * Adds a presence listener to remove offline users from discovered features.
     */
    private void addPresenceListener() {
        // Check presence changes
        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Presence presence = (Presence)packet;
                if (!presence.isAvailable()) {
                    String from = presence.getFrom();
                    if (ModelUtil.hasLength(from)) {
                        // Remove from
                        jingleFeature.remove(from);
                    }
                }


            }
        }, new PacketTypeFilter(Presence.class));
    }


    public void connectionClosed() {
    }

    public void connectionClosedOnError(Exception e) {
    }

    public void reconnectingIn(int seconds) {
    }

    public void reconnectionSuccessful() {
        // Add Jingle to discovered items list.
        SparkManager.addFeature(JINGLE_NAMESPACE);
    }

    public void reconnectionFailed(Exception e) {

    }
}
