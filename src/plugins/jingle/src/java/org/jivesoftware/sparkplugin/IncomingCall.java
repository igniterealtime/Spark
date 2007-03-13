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

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.jingle.IncomingJingleSession;
import org.jivesoftware.smackx.jingle.JingleNegotiator;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.OutgoingJingleSession;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionStateListener;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * Incoming call handles a single incoming Jingle call.
 */
public class IncomingCall implements JingleSessionStateListener {

    private SparkToaster toasterManager;

    private JingleNegotiator.State lastState;

    private JingleSession session;

    private AudioClip ringing;

    private boolean answered;

    private ChatRoom chatRoom;

    private Map<ChatRoom, JingleRoom> callMap = new HashMap<ChatRoom, JingleRoom>();

    /**
     * Initializes a new IncomingCall with the required JingleSession.
     *
     * @param session the <code>JingleSession</code>
     */
    public IncomingCall(final JingleSession session) {
        this.session = session;

        this.session.addStateListener(this);

        try {
            ringing = Applet.newAudioClip(JinglePhoneRes.getURL("RINGING"));
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Returns true if the call was answered.
     *
     * @return true if the call was answered.
     */
    public boolean isAnswered() {
        return answered;
    }

    /**
     * Appends the JingleRoom to the ChatRoom.
     */
    private void showCallAnsweredState() {
        lastState = session.getState();

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
    }

    /**
     * Returns the <code>JingleSession</code> associated with this incoming call.
     *
     * @return the session.
     */
    public JingleSession getSession() {
        return session;
    }

    public void beforeChange(JingleNegotiator.State old, JingleNegotiator.State newOne) throws JingleNegotiator.JingleException {
        if (newOne != null && newOne instanceof IncomingJingleSession.Active) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    showIncomingCall(session);
                }
            });

            if (lastState == null && ringing != null) {
                ringing.loop();
            }

            while (!answered && (session != null && !session.isClosed())) {
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                    Log.error(e);
                }
            }
            if (!answered) {
                rejectIncomingCall();
                throw new JingleNegotiator.JingleException("Not Accepted");
            }
        }


    }

    public void afterChanged(JingleNegotiator.State old, JingleNegotiator.State newOne) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateState();
            }
        });
    }

    /**
     * Updates the state of the call.
     */
    private void updateState() {
        if (session == null || session.isClosed()) {
            showCallEndedState();
        }
        else if (session instanceof OutgoingJingleSession) {
            if (session.getState() instanceof OutgoingJingleSession.Active) {
                showCallAnsweredState();
            }

            lastState = session.getState();
        }
        else if (session instanceof IncomingJingleSession) {
            if (session.getState() instanceof IncomingJingleSession.Active) {
                showCallAnsweredState();
            }
            else {
                showCallEndedState();
            }
        }
    }

    /**
     * Notifies user of an incoming call. The UI allows for users to either accept or reject
     * the incoming session.
     *
     * @param jingleSession the JingleSession.
     */
    private void showIncomingCall(final JingleSession jingleSession) {
        toasterManager = new SparkToaster();
        toasterManager.setHidable(false);

        final IncomingCallUI incomingCall = new IncomingCallUI(jingleSession.getInitiator());
        toasterManager.setToasterHeight(175);
        toasterManager.setToasterWidth(300);
        toasterManager.setDisplayTime(500000000);

        toasterManager.showToaster("Incoming Voice Chat", incomingCall);
        toasterManager.hideTitle();

        incomingCall.getAcceptButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toasterManager.close();
                if (ringing != null) {
                    ringing.stop();
                }

                if (chatRoom == null) {
                    chatRoom = SparkManager.getChatManager().getChatRoom(StringUtils.parseBareAddress(jingleSession.getInitiator()));
                    SparkManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);
                    SparkManager.getChatManager().getChatContainer().getChatFrame().toFront();
                }
                answered = true;
            }
        });

        incomingCall.getRejectButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rejectIncomingCall();
            }
        });
    }

}
