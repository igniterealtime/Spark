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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.jingle.IncomingJingleSession;
import org.jivesoftware.smackx.jingle.JingleNegotiator;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.JingleSessionRequest;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionListener;
import org.jivesoftware.smackx.jingle.media.PayloadType;
import org.jivesoftware.smackx.jingle.nat.TransportCandidate;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * Incoming call handles a single incoming Jingle call.
 */
public class IncomingCall implements JingleSessionListener, ChatRoomClosingListener {

    private SparkToaster toasterManager;

    private AudioClip ringing;

    private ChatRoom chatRoom;

    private Map<ChatRoom, JingleRoom> callMap = new HashMap<ChatRoom, JingleRoom>();

    private GenericNotification notificationUI;

    private IncomingJingleSession session;

    /**
     * Initializes a new IncomingCall with the required JingleSession.
     *
     * @param request the <code>JingleSessionRequest</code>
     */
    public IncomingCall(final JingleSessionRequest request) {

        try {
            ringing = Applet.newAudioClip(JinglePhoneRes.getURL("RINGING"));
        }
        catch (Exception e) {
            Log.error(e);
        }

        notificationUI = new GenericNotification("Establishing call. Please wait...", SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));

        showIncomingCall(request);
    }


    /**
     * Appends the JingleRoom to the ChatRoom.
     */
    private void showCallAnsweredState() {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        notificationUI.setTitle("Voice chat started on " + formatter.format(new Date()));
        notificationUI.showAlert(false);
        notificationUI.setIcon(null);

        if (ringing != null) {
            ringing.stop();
        }

        final JingleRoom roomUI = new JingleRoom(session, chatRoom);
        chatRoom.getChatPanel().add(roomUI, new GridBagConstraints(1, 1, 1, 1, 0.05, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        chatRoom.getChatPanel().invalidate();
        chatRoom.getChatPanel().validate();
        chatRoom.getChatPanel().repaint();
        callMap.put(chatRoom, roomUI);

        // Add state
        JingleStateManager.getInstance().addJingleSession(chatRoom, JingleStateManager.JingleRoomState.inJingleCall);

        // Notify state change
        SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
    }

    /**
     * Removes the JingleRoom from the ChatRoom.
     */
    private void showCallEndedState() {
        if (ringing != null) {
            ringing.stop();
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        notificationUI.setTitle("Voice chat ended on " + formatter.format(new Date()));
        notificationUI.setIcon(null);
        notificationUI.showAlert(false);


        if (chatRoom != null) {
            JingleRoom room = callMap.get(chatRoom);
            if (room != null) {
                chatRoom.getChatPanel().remove(room);
            }

            callMap.remove(chatRoom);
            chatRoom.getChatPanel().invalidate();
            chatRoom.getChatPanel().validate();
            chatRoom.getChatPanel().repaint();
        }

        // Add state
        JingleStateManager.getInstance().removeJingleSession(chatRoom);

        // Notify state change
        SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
    }

    /**
     * Called if an incoming Jingle Session is rejected.
     */
    public void rejectIncomingCall() {
        // Close toaster if it's up.
        if (toasterManager != null) {
            toasterManager.close();
        }

        if (session != null) {
            try {
                session.terminate();
                session = null;
            }
            catch (XMPPException e) {
                Log.error(e);
            }
        }

        if (ringing != null) {
            ringing.stop();
        }
    }

    /**
     * Returns the <code>JingleSession</code> associated with this incoming call.
     *
     * @return the session.
     */
    public JingleSession getSession() {
        return session;
    }

    private void notifyRoom() {
        notificationUI.showAlert(true);
        chatRoom.getTranscriptWindow().addComponent(notificationUI);
    }

    /**
     * Notifies user of an incoming call. The UI allows for users to either accept or reject
     * the incoming session.
     *
     * @param request the JingleSession.
     */
    private void showIncomingCall(final JingleSessionRequest request) {
        toasterManager = new SparkToaster();
        toasterManager.setHidable(false);

        final IncomingCallUI incomingCall = new IncomingCallUI(request.getFrom());
        toasterManager.setToasterHeight(175);
        toasterManager.setToasterWidth(300);
        toasterManager.setDisplayTime(500000000);

        toasterManager.showToaster("Incoming Voice Chat", incomingCall);
        toasterManager.hideTitle();

        incomingCall.getAcceptButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                acceptSession(request);
            }
        });

        incomingCall.getRejectButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                request.reject();
                rejectIncomingCall();
            }
        });

        // Start the ringing.
        final Runnable ringer = new Runnable() {
            public void run() {
                ringing.loop();
            }
        };

        TaskEngine.getInstance().submit(ringer);


        // End after 30 seconds max.
        TimerTask endTask = new SwingTimerTask() {
            public void doRun() {
                if(session == null){
                    rejectIncomingCall();
                }
            }
        };

        TaskEngine.getInstance().schedule(endTask, 30000);
    }

    /**
     * Accepts a <code>JingleSessionRequest</code>.
     *
     * @param request the request.
     */
    private void acceptSession(JingleSessionRequest request) {
        toasterManager.close();

        if (ringing != null) {
            ringing.stop();
        }

        try {
            // Accept the request
            session = request.accept();

            session.addListener(this);

            // Start the call
            session.start();
        }
        catch (XMPPException ee) {
            Log.error(ee);
        }

        if (chatRoom == null) {
            chatRoom = SparkManager.getChatManager().getChatRoom(StringUtils.parseBareAddress(request.getFrom()));
            SparkManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);
            SparkManager.getChatManager().getChatContainer().getChatFrame().toFront();
            notifyRoom();
        }

        showCallAnsweredState();
    }


    public void sessionEstablished(PayloadType payloadType, TransportCandidate transportCandidate, TransportCandidate transportCandidate1, JingleSession jingleSession) {

    }

    public void sessionDeclined(String string, JingleSession jingleSession) {
        showCallEndedState();
    }

    public void sessionRedirected(String string, JingleSession jingleSession) {
    }

    public void sessionClosed(String string, JingleSession jingleSession) {
        showCallEndedState();
    }

    public void sessionClosedOnError(XMPPException xmppException, JingleSession jingleSession) {
        showCallEndedState();
    }


    public void closing() {
        if (session != null) {
            try {
                session.terminate();
            }
            catch (XMPPException e) {
                Log.error(e);
            }
        }


        JingleStateManager.getInstance().removeJingleSession(chatRoom);
    }
}
