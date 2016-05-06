/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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
package org.jivesoftware.sparkplugin;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.jingleold.JingleSession;
import org.jivesoftware.smackx.jingleold.JingleSessionRequest;
import org.jivesoftware.smackx.jingleold.listeners.JingleSessionListener;
import org.jivesoftware.smackx.jingleold.media.PayloadType;
import org.jivesoftware.smackx.jingleold.nat.TransportCandidate;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
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

    private Map<ChatRoom, JingleRoom> callMap = new HashMap<>();

    private GenericNotification notificationUI;

    private JingleSession session;

    private boolean established = false;

    private boolean mediaReceived = false;

    private TimerTask mediaReceivedTask;

    private static final long WAIT_FOR_MEDIA_DELAY = 20000;

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

        notificationUI = new GenericNotification(JingleResources.getString("label.establishing.call"), SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));

        // Accept the request
        try {
            session = request.accept();
        }
        catch (XMPPException | SmackException e) {
            Log.error(e);
        }

        session.addListener(this);

        showIncomingCall(request);
    }


    /**
     * Appends the JingleRoom to the ChatRoom.
     */
    private void showCallAnsweredState() {
        SwingUtilities.invokeLater( () -> {
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
        } );
    }

    /**
     * Removes the JingleRoom from the ChatRoom.
     */
    private void showCallEndedState(final String reason) {
        SwingUtilities.invokeLater( () -> {
            if (ringing != null) {
                ringing.stop();
            }

            notificationUI.setTitle(reason);
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
        } );
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
            catch (XMPPException | SmackException e) {
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

        incomingCall.getAcceptButton().addActionListener( e -> acceptSession(request) );

        incomingCall.getRejectButton().addActionListener( e -> rejectIncomingCall() );

        // Start the ringing.
        final Runnable ringer = () -> ringing.loop();

        TaskEngine.getInstance().submit(ringer);

        // End after 30 seconds max.
        TimerTask endTask = new SwingTimerTask() {
            public void doRun() {
                if (!session.isFullyEstablished()) {
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

        // Start the call
        session.startIncoming();

        if (chatRoom == null) {
            chatRoom = SparkManager.getChatManager().getChatRoom( XmppStringUtils.parseBareJid(request.getFrom()) );
            SparkManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);
            SparkManager.getChatManager().getChatContainer().getChatFrame().toFront();
            notifyRoom();
        }
    }

    public void sessionMediaReceived(JingleSession jingleSession, String participant) {
        mediaReceived = true;
        TaskEngine.getInstance().cancelScheduledTask(mediaReceivedTask);
        showCallAnsweredState();
    }

    public void sessionEstablished(PayloadType payloadType, TransportCandidate transportCandidate, TransportCandidate transportCandidate1, JingleSession jingleSession) {

        established = true;
        mediaReceivedTask = new SwingTimerTask() {
            public void doRun() {
                if (!mediaReceived) {
                    if (session != null) {
                        try {
                            session.terminate("No Media Received. This may be caused by firewall configuration problems.");
                        }
                        catch (XMPPException | SmackException e) {
                            Log.error(e);
                        }
                    }
                }
            }
        };
        TaskEngine.getInstance().schedule(mediaReceivedTask, WAIT_FOR_MEDIA_DELAY, WAIT_FOR_MEDIA_DELAY);
    }

    public void sessionDeclined(String string, JingleSession jingleSession) {
        showCallEndedState("Voice chat was rejected");
    }

    public void sessionRedirected(String string, JingleSession jingleSession) {
    }

    public void sessionClosed(String string, JingleSession jingleSession) {
        if (established && mediaReceived) {
            final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
            showCallEndedState("Voice chat ended on " + formatter.format(new Date()));
        } else {
            showCallEndedState("Voice chat ended: " + string);
        }
        if(PhoneManager.isUseStaticLocator()&&PhoneManager.isUsingMediaLocator()){
            PhoneManager.setUsingMediaLocator(false);
        }
    }

    public void sessionClosedOnError(XMPPException xmppException, JingleSession jingleSession) {
        showCallEndedState("Voice chat ended due an error: " + xmppException.getMessage());
        if(PhoneManager.isUseStaticLocator()&&PhoneManager.isUsingMediaLocator()){
            PhoneManager.setUsingMediaLocator(false);
        }        
    }

    public void closing() {
        if (session != null) {
            try {
                session.terminate();
            }
            catch (XMPPException | SmackException e) {
                Log.error(e);
            }
        }

        JingleStateManager.getInstance().removeJingleSession(chatRoom);
    }
}
