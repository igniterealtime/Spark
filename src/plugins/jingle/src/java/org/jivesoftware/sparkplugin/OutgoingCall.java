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
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.JingleSessionStatePending;
import org.jivesoftware.smackx.jingle.listeners.JingleSessionListener;
import org.jivesoftware.smackx.jingle.media.PayloadType;
import org.jivesoftware.smackx.jingle.nat.TransportCandidate;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.FileDragLabel;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;

/**
 * Handles UI controls for outgoing jingle calls.
 */
public class OutgoingCall extends JPanel implements JingleSessionListener, ChatRoomClosingListener {

	private static final long serialVersionUID = 7051515951813136423L;
	private FileDragLabel imageLabel = new FileDragLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel fileLabel = new JLabel();

    private CallButton cancelButton = new CallButton();

    private JingleSession session;
    private JingleRoom jingleRoom;

    private AudioClip ringing;

    private ChatRoom chatRoom;

    private boolean established = false;

    private boolean mediaReceived = false;

    private TimerTask mediaReceivedTask;

    private static final long WAIT_FOR_MEDIA_DELAY = 20000;

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

        cancelButton.setText(Res.getString("cancel"));
        add(cancelButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

        cancelButton.setForeground(new Color(73, 113, 196));
        cancelButton.setFont(new Font("Dialog", Font.BOLD, 11));
        cancelButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

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

        // Start the call
        this.session.startOutgoing();

        fileLabel.setText(jid);

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(jid);


        titleLabel.setText(JingleResources.getString("label.outgoing.voicechat", contactItem.getNickname()));


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

        // Notify state change
        SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);

        updateOutgoingCallPanel();
    }

    /**
     * Updates the UI to reflect the current state.
     */
    private void updateOutgoingCallPanel() {
        if (session == null || session.isClosed()) {
            return;
        }
        else if (session instanceof JingleSession) {
            showAlert(false);
            if (session.getSessionState() instanceof JingleSessionStatePending) {
                titleLabel.setText("Calling user. Please wait...");
                cancelButton.setVisible(true);
            }
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
        if (ringing != null) {
            ringing.stop();
        }

        jingleRoom = new JingleRoom(session, chatRoom);
        chatRoom.getChatPanel().add(jingleRoom, new GridBagConstraints(1, 1, 1, 1, 0.05, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        chatRoom.getChatPanel().invalidate();
        chatRoom.getChatPanel().validate();
        chatRoom.getChatPanel().repaint();

        // Add state
        JingleStateManager.getInstance().addJingleSession(chatRoom, JingleStateManager.JingleRoomState.inJingleCall);

        // Notify state change
        SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
    }

    /**
     * Called when the call has ended.
     */
    private void showCallEndedState(String reason) {
        titleLabel.setText(reason);

        showAlert(true);
        cancelButton.setVisible(false);
        if (ringing != null) {
            ringing.stop();
        }

        if (chatRoom != null && jingleRoom != null) {
            chatRoom.getChatPanel().remove(jingleRoom);
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
		private static final long serialVersionUID = 7083309769944609925L;

		public CallButton() {
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
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        showCallEndedState("Voice chat ended on " + formatter.format(new Date()));
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

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateOutgoingCallPanel();
            }
        });
    }

    public void sessionDeclined(String string, JingleSession jingleSession) {
        showCallEndedState("The Session was rejected.");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateOutgoingCallPanel();
            }
        });
    }

    public void sessionRedirected(String string, JingleSession jingleSession) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateOutgoingCallPanel();
            }
        });
    }

    public void sessionClosed(String string, JingleSession jingleSession) {
        
        if (jingleSession instanceof JingleSession) {
            if (established && mediaReceived) {
                final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
                showCallEndedState("Voice chat ended on " + formatter.format(new Date()));
            }
            else {
                showCallEndedState("Session closed due to " + string);
            }
        }
        if(PhoneManager.isUseStaticLocator()&&PhoneManager.isUsingMediaLocator()){
            PhoneManager.setUsingMediaLocator(false);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateOutgoingCallPanel();
            }
        });
    }

    public void sessionClosedOnError(XMPPException xmppException, JingleSession jingleSession) {
        showCallEndedState("Voice chat ended due: " + xmppException.getMessage());
        if(PhoneManager.isUseStaticLocator()&&PhoneManager.isUsingMediaLocator()){
            PhoneManager.setUsingMediaLocator(false);
        }                

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateOutgoingCallPanel();
            }
        });
    }
}
