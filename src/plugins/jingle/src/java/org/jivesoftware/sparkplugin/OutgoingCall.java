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
import org.jivesoftware.smackx.jingle.IncomingJingleSession;
import org.jivesoftware.smackx.jingle.JingleNegotiator;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.OutgoingJingleSession;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionListener;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionStateListener;
import org.jivesoftware.smackx.jingle.media.PayloadType;
import org.jivesoftware.smackx.jingle.nat.TransportCandidate;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.FileDragLabel;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles UI controls for outgoing jingle calls.
 */
public class OutgoingCall extends JPanel implements JingleSessionStateListener, JingleSessionListener, ChatRoomClosingListener {

    private FileDragLabel imageLabel = new FileDragLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel fileLabel = new JLabel();

    private CallButton cancelButton = new CallButton();
    private CallButton answerButton = new CallButton();

    private JingleNegotiator.State lastState;
    private JingleSession session;

    private AudioClip ringing;

    private boolean answered;

    private ChatRoom chatRoom;

    private Map<ChatRoom, JingleRoom> callMap = new HashMap<ChatRoom, JingleRoom>();

    /**
     * Creates a new instance of OutgoingCall.
     */
    public OutgoingCall() {
        try {
            ringing = Applet.newAudioClip(JinglePhoneRes.getURL("RINGING"));
        }
        catch (Exception e) {
            Log.error(e);
        }

        setLayout(new GridBagLayout());

        setBackground(new Color(250, 249, 242));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));

        cancelButton.setText("Cancel");
        answerButton.setText("Answer");

        add(answerButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(cancelButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

        answerButton.setVisible(false);

        answerButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                answered = true;
            }
        });


        cancelButton.setForeground(new Color(73, 113, 196));
        cancelButton.setFont(new Font("Dialog", Font.BOLD, 11));
        cancelButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

        answerButton.setForeground(new Color(73, 113, 196));
        answerButton.setFont(new Font("Dialog", Font.BOLD, 11));
        answerButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));
    }

    /**
     * Handles a new outgoing call.
     *
     * @param session  the JingleSession for the outgoing call.
     * @param chatRoom the room the session is associated with.
     * @param jid      the users jid.
     */
    public void handleOutgoingCall(final JingleSession session, ChatRoom chatRoom, final String jid) {
        this.chatRoom = chatRoom;

        JingleStateManager.getInstance().addJingleSession(chatRoom, JingleStateManager.JingleRoomState.ringing);

        chatRoom.addClosingListener(this);
        session.addListener(this);
        cancelButton.setVisible(true);

        this.session = session;

        this.session.addStateListener(this);

        fileLabel.setText(jid);

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(jid);


        titleLabel.setText("Outgoing Voice Chat To " + contactItem.getNickname());


        cancelButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                cancel();
            }

            public void mouseEntered(MouseEvent e) {
                cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent e) {
                cancelButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        makeClickable(imageLabel);
        makeClickable(titleLabel);
        makeClickable(answerButton);

        // Notify state change
        SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
    }

    /**
     * Updates the UI to reflect the current state.
     */
    private void updateOutgoingCallPanel() {
        if (session == null || session.isClosed()) {
            return;
        }
        else if (session instanceof OutgoingJingleSession) {
            answerButton.setVisible(false);
            showAlert(false);
            if (session.getState() instanceof OutgoingJingleSession.Active) {
                showCallAnsweredState();
            }
            else if (session.getState() instanceof OutgoingJingleSession.Pending) {
                titleLabel.setText("Calling user. Please wait...");
                cancelButton.setVisible(true);
            }
            lastState = session.getState();
        }
    }


    /**
     * Called when the call has been answered. Will append the JingleRoom to the
     * associated ChatRoom.
     */
    private void showCallAnsweredState() {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        titleLabel.setText("Voice chat started on " + formatter.format(new Date()));
        cancelButton.setVisible(false);
        lastState = session.getState();
        if (ringing != null) {
            ringing.stop();
        }

        final JingleRoom jingleRoom = new JingleRoom(session, chatRoom);
        chatRoom.getChatPanel().add(jingleRoom, new GridBagConstraints(1, 1, 1, 1, 0.05, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        chatRoom.getChatPanel().invalidate();
        chatRoom.getChatPanel().validate();
        chatRoom.getChatPanel().repaint();
        callMap.put(chatRoom, jingleRoom);

        // Add state
        JingleStateManager.getInstance().addJingleSession(chatRoom, JingleStateManager.JingleRoomState.inJingleCall);

        // Notify state change
        SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
    }

    /**
     * Called when the call has ended.
     */
    private void showCallEndedState(boolean answered) {
        if (answered) {
            final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
            titleLabel.setText("Voice chat ended on " + formatter.format(new Date()));
        }
        else {
            titleLabel.setText("Voice chat was rejected.");
        }

        showAlert(true);
        cancelButton.setVisible(false);
        answerButton.setVisible(false);
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

    private void makeClickable(final JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                component.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent e) {
                component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private class CallButton extends JButton {

        public CallButton() {
            decorate();
        }

        /**
         * Create a new RolloverButton.
         *
         * @param text the button text.
         * @param icon the button icon.
         */
        public CallButton(String text, Icon icon) {
            super(text, icon);
            decorate();
        }

        /**
         * Decorates the button with the approriate UI configurations.
         */
        private void decorate() {
            setBorderPainted(false);
            setOpaque(true);

            setContentAreaFilled(false);
            setMargin(new Insets(1, 1, 1, 1));
        }

    }

    /**
     * Changes the background color. If alert is true, the background will reflect that the ui
     * needs attention.
     *
     * @param alert true to notify users that their attention is needed.
     */
    private void showAlert(boolean alert) {
        if (alert) {
            titleLabel.setForeground(new Color(211, 174, 102));
            setBackground(new Color(250, 249, 242));
        }
        else {
            setBackground(new Color(239, 245, 250));
            titleLabel.setForeground(new Color(65, 139, 179));
        }
    }

    /**
     * Call to cancel phone conversation.
     */
    public void cancel() {
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

    public void beforeChange(JingleNegotiator.State old, JingleNegotiator.State newOne) throws JingleNegotiator.JingleException {
        if (newOne != null && newOne instanceof IncomingJingleSession.Active) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (lastState == null && ringing != null) {
                        ringing.loop();
                    }
                }
            });

            while (!answered && (session != null && !session.isClosed())) {
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                    Log.error(e);
                }
            }
            if (!answered) {
                cancel();
                throw new JingleNegotiator.JingleException("Not Accepted");
            }
        }


    }

    public void afterChanged(JingleNegotiator.State old, JingleNegotiator.State newOne) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateOutgoingCallPanel();
            }
        });

    }


    public void closing() {
        try {
            session.terminate();
        }
        catch (XMPPException e) {
            Log.error(e);
        }

        JingleStateManager.getInstance().removeJingleSession(chatRoom);
    }

    public void sessionEstablished(PayloadType payloadType, TransportCandidate transportCandidate, TransportCandidate transportCandidate1, JingleSession jingleSession) {
    }

    public void sessionDeclined(String string, JingleSession jingleSession) {
    }

    public void sessionRedirected(String string, JingleSession jingleSession) {
    }

    public void sessionClosed(String string, JingleSession jingleSession) {
        if (jingleSession instanceof OutgoingJingleSession) {
            OutgoingJingleSession session = (OutgoingJingleSession)jingleSession;
            if (session.getState() instanceof OutgoingJingleSession.Active) {
                showCallEndedState(true);
            }
            else if (session.getState() instanceof OutgoingJingleSession.Pending) {
                showCallEndedState(false);
            }
        }
    }

    public void sessionClosedOnError(XMPPException xmppException, JingleSession jingleSession) {
    }


}
