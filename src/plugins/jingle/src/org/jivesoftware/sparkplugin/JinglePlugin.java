/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkplugin;

import org.jivesoftware.jingleaudio.jmf.JmfMediaManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.jingle.*;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionListener;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionRequestListener;
import org.jivesoftware.smackx.jingle.media.PayloadType;
import org.jivesoftware.smackx.jingle.nat.BridgedTransportManager;
import org.jivesoftware.smackx.jingle.nat.ICETransportManager;
import org.jivesoftware.smackx.jingle.nat.JingleTransportManager;
import org.jivesoftware.smackx.jingle.nat.TransportCandidate;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.log.Log;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple Jingle Plugin for Spark that uses server Media Proxy for the transport and NAT Traversal
 */
public class JinglePlugin implements Plugin, JingleSessionListener {

    final Map<String, JingleSession> sessions = new HashMap<String, JingleSession>();

    public void initialize() {

        JingleTransportManager transportManager = new ICETransportManager();

        final JingleManager jm = new JingleManager(SparkManager.getConnection(), transportManager, new JmfMediaManager());

        final JingleSessionListener jingleListener = this;

        if (transportManager instanceof BridgedTransportManager)
            jm.addCreationListener((BridgedTransportManager) transportManager);

        jm.addJingleSessionRequestListener(new JingleSessionRequestListener() {
            public void sessionRequested(JingleSessionRequest request) {

                if (sessions.containsKey(request.getFrom())) {
                    request.reject();
                    return;
                }

                // Accept the call
                IncomingJingleSession session = null;
                try {
                    session = request.accept();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }

                // Start the call
                if (session != null) {
                    try {
                        session.start();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    session.addListener(jingleListener);
                    sessions.put(session.getInitiator(), session);
                }
            }
        });

        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                if (!(room instanceof ChatRoomImpl)) {
                    return;
                }
                final ChatRoomImpl roomImpl = (ChatRoomImpl) room;
                final ChatRoomButton callButton = new ChatRoomButton("Call");
                callButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {

                        if (sessions.containsKey(roomImpl.getJID())) return;

                        // Create a new Jingle Call with a full JID
                        OutgoingJingleSession session = null;
                        try {
                            session = jm.createOutgoingJingleSession(roomImpl.getJID());
                        }
                        catch (XMPPException e) {
                            Log.error(e);
                        }

                        // Start the call
                        if (session != null) {
                            System.out.println("OUT");
                            session.addListener(jingleListener);
                            session.start();
                            sessions.put(roomImpl.getJID(), session);
                        }

                    }
                });

                room.getToolBar().addChatRoomButton(callButton);
            }

            public void chatRoomClosed(ChatRoom room) {
                final ChatRoomImpl roomImpl = (ChatRoomImpl) room;
                if (sessions.containsKey(roomImpl.getJID())) {
                    JingleSession session = sessions.get(roomImpl.getJID());
                    try {
                        session.terminate();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    sessions.remove(roomImpl.getJID());
                }
            }
        });
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
    }

    public void sessionEstablished(PayloadType payloadType, TransportCandidate transportCandidate, TransportCandidate transportCandidate1, JingleSession jingleSession) {

    }

    public void sessionDeclined(String string, JingleSession jingleSession) {

    }

    public void sessionRedirected(String string, JingleSession jingleSession) {

    }

    public void sessionClosed(String string, JingleSession jingleSession) {
        try {
            if (sessions.containsValue(jingleSession)) {
                String found = null;
                for (String key : sessions.keySet()) {
                    System.err.println("D:" + key);
                    if (jingleSession.equals(sessions.get(key))) {
                        found = key;
                    }
                }
                System.err.println("REMOVED:" + found);
                if (found != null)
                    sessions.remove(found);
            }

        } catch (Exception e) {
              // Do Nothing
        }
    }

    public void sessionClosedOnError(XMPPException xmppException, JingleSession jingleSession) {

    }
}
