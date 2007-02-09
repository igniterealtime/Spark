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
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.text.StyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple Jingle Plugin for Spark that uses server Media Proxy for the transport and NAT Traversal
 */
public class JinglePlugin implements Plugin, JingleSessionListener {

    final Map<String, JingleSession> sessions = new HashMap<String, JingleSession>();

    JingleManager jm = null;

    final JingleSessionListener jingleListener = this;

    public void initialize() {

        JMFInit.start(false);

        JingleTransportManager transportManager = new ICETransportManager(SparkManager.getConnection(), "jivesoftware.com", 3478);

        jm = new JingleManager(SparkManager.getConnection(), transportManager, new JmfMediaManager());

        if (transportManager instanceof BridgedTransportManager)
            jm.addCreationListener((BridgedTransportManager) transportManager);

        jm.addJingleSessionRequestListener(new JingleSessionRequestListener() {
            public void sessionRequested(JingleSessionRequest request) {

                if (sessions.containsKey(request.getFrom())) {
                    IncomingJingleSession session = null;
                    try {
                        session = request.accept();
                        session.setInitialSessionRequest(request);
                        session.terminate();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                ChatRoom room = SparkManager.getChatManager().getChatRoom(request.getFrom().split("/")[0]);

                if (room != null) {

                    TranscriptWindow transcriptWindow = room.getTranscriptWindow();
                    StyledDocument doc = (StyledDocument) transcriptWindow.getDocument();
                    Style style = doc.addStyle("StyleName", null);

                    try {
                        IncomingJingleSession session = request.accept();
                        session.setInitialSessionRequest(request);

                        session.addListener(jingleListener);
                        sessions.put(session.getInitiator(), session);

                        CallMessage callMessage = new CallMessage();
                        callMessage.call(session, request.getFrom());
                        StyleConstants.setComponent(style, callMessage);

                        // Insert the image at the end of the text
                        try {
                            doc.insertString(doc.getLength(), "ignored text", style);
                            doc.insertString(doc.getLength(), "\n", null);
                        }
                        catch (BadLocationException e) {
                            Log.error(e);
                        }

                        room.scrollToBottom();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
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
                        placeCall(roomImpl);
                    }
                });

                room.getToolBar().addChatRoomButton(callButton);
            }

            public void chatRoomClosed(ChatRoom room) {
                final ChatRoomImpl roomImpl = (ChatRoomImpl) room;
                if (sessions.containsKey(roomImpl.getJID())) {
                    endCall(roomImpl);
                }
            }
        });
    }

    public void endCall(ChatRoomImpl room) {
        JingleSession session = sessions.get(room.getJID());
        if (session == null) return;
        try {
            session.terminate();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        sessions.remove(room.getJID());
    }

    public void placeCall(ChatRoomImpl room) {

        if (sessions.containsKey(room.getJID())) return;

        // Create a new Jingle Call with a full JID
        OutgoingJingleSession session = null;
        try {
            session = jm.createOutgoingJingleSession(room.getJID());
        }
        catch (XMPPException e) {
            Log.error(e);
        }

        // Start the call
        if (session != null) {
            session.addListener(jingleListener);
            session.start();
            sessions.put(room.getJID(), session);
        }

        TranscriptWindow transcriptWindow = room.getTranscriptWindow();
        StyledDocument doc = (StyledDocument) transcriptWindow.getDocument();
        Style style = doc.addStyle("StyleName", null);

        CallMessage callMessage = new CallMessage();
        callMessage.call(session, room.getJID());
        StyleConstants.setComponent(style, callMessage);

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

    public void sessionEstablished(PayloadType payloadType, TransportCandidate transportCandidate, TransportCandidate transportCandidate1, JingleSession jingleSession) {

    }

    public void sessionDeclined(String string, JingleSession jingleSession) {
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
