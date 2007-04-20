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
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.JingleSessionRequest;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionListener;
import org.jivesoftware.smackx.jingle.media.PayloadType;
import org.jivesoftware.smackx.jingle.nat.TransportCandidate;
import org.jivesoftware.smackx.packet.JingleError;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;

import javax.swing.SwingUtilities;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;

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

        notificationUI = new GenericNotification("Establishing call. Please wait...", SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));

        // Accept the request
        try {
            session = request.accept();
        }
        catch (XMPPException e) {
            Log.error(e);
        }

        session.addListener(this);

        showIncomingCall(request);
    }


    /**
     * Appends the JingleRoom to the ChatRoom.
     */
    private void showCallAnsweredState() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
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
        });
    }

    /**
     * Removes the JingleRoom from the ChatRoom.
     */
    private void showCallEndedState(final String reason) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
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
            }
        });
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

        try {
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
                        catch (XMPPException e) {
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
    }

    public void sessionClosedOnError(XMPPException xmppException, JingleSession jingleSession) {
        showCallEndedState("Voice chat ended due an error: " + xmppException.getMessage());
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
