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
import org.jivesoftware.smackx.jingle.listeners.JingleSessionStateListener;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.FileDragLabel;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.log.Log;

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

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CallMessage extends JPanel implements JingleSessionStateListener {

    private FileDragLabel imageLabel = new FileDragLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel fileLabel = new JLabel();

    private CallButton cancelButton = new CallButton();
    private CallButton answerButton = new CallButton();
    private String fullJID;

    private JingleNegotiator.State lastState;
    private JingleSession session;

    private AudioClip ringing;

    private boolean answer = false;

    private JingleRoomUI roomUI;
    private ChatRoom chatRoom;

    public CallMessage() {
        buildUI();
    }

    public void buildUI() {
        try {
            ringing = Applet.newAudioClip(JinglePhoneRes.getURL("RINGING"));
        }
        catch (Exception e) {
            Log.error(e);
        }
        System.out.println(ringing != null ? "RRR" : "NNN");

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
                setAnswer(true);
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

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public void call(final JingleSession session, ChatRoom chatRoom, final String jid) {
        this.chatRoom = chatRoom;
        cancelButton.setVisible(true);
        this.fullJID = jid;

        this.session = session;

        this.session.addStateListener(this);

        fileLabel.setText(fullJID);

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(jid);

        if (session instanceof IncomingJingleSession) {
            titleLabel.setText("Incoming Call From " + contactItem.getNickname() + ". Establishing connection...");
        }
        else {
            titleLabel.setText("Outgoing Call To " + contactItem.getNickname());
        }


        cancelButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                cancelCall();
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
    }

    private void updateBar() {

        if (session == null || session.isClosed()) {
            showCallEndedState();
        }
        else if (session instanceof OutgoingJingleSession) {
            answerButton.setVisible(false);
            showAlert(false);
            if (session.getState() instanceof OutgoingJingleSession.Active) {
                showCallAnsweredState();
            }
            else if (session.getState() instanceof OutgoingJingleSession.Pending) {
                titleLabel.setText("User is being notified...");
                cancelButton.setVisible(true);
            }
            lastState = session.getState();
        }
        else if (session instanceof IncomingJingleSession) {
            answerButton.setVisible(false);
            showAlert(false);
            if (session.getState() instanceof IncomingJingleSession.Pending) {
                titleLabel.setText("Establishing...");
            }
            else if (session.getState() instanceof IncomingJingleSession.Active) {
                showCallAnsweredState();
            }
            else {
                showCallEndedState();
            }
        }
    }

    private void showCallAnsweredState() {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        titleLabel.setText("Phone call started on " + formatter.format(new Date()));
        cancelButton.setVisible(false);
        lastState = session.getState();
        if (ringing != null) {
            ringing.stop();
        }
        roomUI = new JingleRoomUI(session, chatRoom);
        chatRoom.getSplitPane().setRightComponent(roomUI);
        chatRoom.getSplitPane().setResizeWeight(.60);
        chatRoom.getSplitPane().setDividerSize(5);
    }

    private void showCallEndedState() {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        titleLabel.setText("Phone call ended on " + formatter.format(new Date()));
        showAlert(true);
        cancelButton.setVisible(false);
        answerButton.setVisible(false);
        if (ringing != null) {
            ringing.stop();
        }
        chatRoom.getSplitPane().setRightComponent(null);
        chatRoom.getSplitPane().setDividerSize(0);
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

    public void cancelCall() {
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

    public JingleSession getSession() {
        return session;
    }

    public void setSession(JingleSession session) {
        this.session = session;
    }

    public void beforeChange(JingleNegotiator.State old, JingleNegotiator.State newOne) throws JingleNegotiator.JingleException {
        if (newOne != null && newOne instanceof IncomingJingleSession.Active) {
            showAlert(true);
            titleLabel.setText("Incoming Call From " + SparkManager.getUserManager().getUserNicknameFromJID(fullJID));
            cancelButton.setText("Reject");
            cancelButton.setVisible(true);
            answerButton.setVisible(true);
            if ((lastState == null))
                if (ringing != null) {
                    ringing.loop();
                }

            while (!answer && (session != null && !session.isClosed())) {

                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    Log.error(e);
                }

            }
            if (!answer) {
                cancelCall();
                throw new JingleNegotiator.JingleException("Not Accepted");
            }
        }
    }

    public void afterChanged(JingleNegotiator.State old, JingleNegotiator.State newOne) {
        updateBar();
    }

}
