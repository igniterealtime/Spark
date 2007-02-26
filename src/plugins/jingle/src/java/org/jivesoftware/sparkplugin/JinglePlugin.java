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
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.jingle.IncomingJingleSession;
import org.jivesoftware.smackx.jingle.JingleManager;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.JingleSessionRequest;
import org.jivesoftware.smackx.jingle.OutgoingJingleSession;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionListener;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionRequestListener;
import org.jivesoftware.smackx.jingle.media.PayloadType;
import org.jivesoftware.smackx.jingle.nat.BridgedTransportManager;
import org.jivesoftware.smackx.jingle.nat.ICETransportManager;
import org.jivesoftware.smackx.jingle.nat.JingleTransportManager;
import org.jivesoftware.smackx.jingle.nat.TransportCandidate;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.phone.Phone;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * A simple Jingle Plugin for Spark that uses server Media Proxy for the transport and NAT Traversal
 */
public class JinglePlugin implements Plugin, JingleSessionListener, Phone {

    final Map<String, JingleSession> sessions = new HashMap<String, JingleSession>();

    JingleManager jm;

    final JingleSessionListener jingleListener = this;

    public void initialize() {
        // Add to PhoneManager
        PhoneManager.getInstance().addPhone(this);

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                JingleTransportManager transportManager = new ICETransportManager(SparkManager.getConnection(), "stun.xten.net", 3478);

                jm = new JingleManager(SparkManager.getConnection(), transportManager, new JmfMediaManager());

                if (transportManager instanceof BridgedTransportManager)
                    jm.addCreationListener((BridgedTransportManager)transportManager);
                return true;
            }

            public void finished() {
                initUI();
            }
        };

        worker.start();
    }


    private void initUI() {
        if (jm == null) {
            Log.error("Unable to resolve Jingle Connection");
            return;
        }

        jm.addJingleSessionRequestListener(new JingleSessionRequestListener() {
            public void sessionRequested(JingleSessionRequest request) {
                final String from = request.getFrom();

                if (!sessions.containsKey(from)) {
                    IncomingJingleSession session = null;
                    try {
                        session = request.accept();
                        session.addListener(jingleListener);
                        session.start(request);
                        sessions.put(request.getFrom(), session);

                        final ChatRoom room = SparkManager.getChatManager().getChatRoom(StringUtils.parseBareAddress(from));

                        TranscriptWindow transcriptWindow = room.getTranscriptWindow();
                        StyledDocument doc = (StyledDocument)transcriptWindow.getDocument();
                        Style style = doc.addStyle("StyleName", null);

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
                    }
                    catch (XMPPException e) {
                        Log.error(e);
                    }

                }
            }
        });

        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomClosed(ChatRoom room) {
                final ChatRoomImpl roomImpl = (ChatRoomImpl)room;
                if (sessions.containsKey(roomImpl.getJID())) {
                    endCall(roomImpl);
                }
            }
        });
    }


    public Collection<Action> getPhoneActions(final String jid) {
        if(jm == null){
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

    public void endCall(ChatRoomImpl room) {
        JingleSession session = sessions.get(room.getJID());
        if (session == null) return;
        try {
            session.terminate();
        }
        catch (XMPPException e) {
            Log.error(e);
        }
        sessions.remove(room.getJID());
    }

    public void placeCall(String jid) {
        jid = SparkManager.getUserManager().getFullJID(jid);
        
        if (sessions.containsKey(jid)) {
            return;
        }

        // Create a new Jingle Call with a full JID
        OutgoingJingleSession session = null;
        try {
            session = jm.createOutgoingJingleSession(jid);
        }
        catch (XMPPException e) {
            Log.error(e);
        }

        // Start the call
        if (session != null) {
            session.addListener(jingleListener);
            session.start();
            sessions.put(jid, session);
        }

        ChatRoom room = SparkManager.getChatManager().getChatRoom(jid);

        TranscriptWindow transcriptWindow = room.getTranscriptWindow();
        StyledDocument doc = (StyledDocument)transcriptWindow.getDocument();
        Style style = doc.addStyle("StyleName", null);

        CallMessage callMessage = new CallMessage();
        callMessage.call(session, jid);
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

        }
        catch (Exception e) {
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

        }
        catch (Exception e) {
            // Do Nothing
        }
    }

    public void sessionClosedOnError(XMPPException xmppException, JingleSession jingleSession) {
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

        }
        catch (Exception e) {
            // Do Nothing
        }
    }


}
