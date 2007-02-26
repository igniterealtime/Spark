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

import org.jivesoftware.resource.Res;
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

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CallMessage extends JPanel implements JingleSessionStateListener {

    private FileDragLabel imageLabel = new FileDragLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel fileLabel = new JLabel();

    private CallMessage.CallButton cancelButton = new CallMessage.CallButton();
    private JingleSession session = null;

    private CallMessage.CallButton retryButton = new CallMessage.CallButton();
    private CallMessage.CallButton acceptButton = new CallMessage.CallButton();
    private String fullJID;

    private JingleNegotiator.State lastState = null;

    private AudioClip ringing = null;

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
            e.printStackTrace();
        }
        System.out.println(ringing != null ? "RRR" : "NNN");

        setLayout(new GridBagLayout());

        setBackground(new Color(250, 249, 242));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));
        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        cancelButton.setText(Res.getString("cancel"));
        retryButton.setText(Res.getString("retry"));
        acceptButton.setText("Accept");

        add(cancelButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(retryButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(acceptButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        retryButton.setVisible(false);
        acceptButton.setVisible(false);

        retryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        final CallMessage callMessage = this;
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setAnswer(true);
            }
        });

        cancelButton.setForeground(new Color(73, 113, 196));
        cancelButton.setFont(new Font("Dialog", Font.BOLD, 11));
        cancelButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

        retryButton.setForeground(new Color(73, 113, 196));
        retryButton.setFont(new Font("Dialog", Font.BOLD, 11));
        retryButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

        acceptButton.setForeground(new Color(73, 113, 196));
        acceptButton.setFont(new Font("Dialog", Font.BOLD, 11));
        acceptButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

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
        retryButton.setVisible(false);
        this.fullJID = jid;

        this.session = session;

        this.session.addStateListener(this);

        fileLabel.setText(fullJID);

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(jid);

        titleLabel.setText("Calling: " + contactItem.getNickname());

        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
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
    }

    private void updateBar() {

        if (session == null || session.isClosed()) {
            showAlert(true);
            titleLabel.setText("Call Ended");
            cancelButton.setVisible(false);
            retryButton.setVisible(false);
            acceptButton.setVisible(false);
            if (ringing != null) ringing.stop();
            chatRoom.getSplitPane().setRightComponent(null);
        }
        else if (session instanceof OutgoingJingleSession) {
            acceptButton.setVisible(false);
            showAlert(false);
            if (session.getState() instanceof OutgoingJingleSession.Active) {
                cancelButton.setVisible(true);
                retryButton.setVisible(false);
                titleLabel.setText("On Phone");
                roomUI = new JingleRoomUI(session, chatRoom);
                chatRoom.getSplitPane().setRightComponent(roomUI);
                chatRoom.getSplitPane().setResizeWeight(.60);
            }
            else if (session.getState() instanceof OutgoingJingleSession.Pending) {
                titleLabel.setText("Ringing...");
                cancelButton.setVisible(true);
                retryButton.setVisible(false);
            }
            lastState = session.getState();
        }
        else if (session instanceof IncomingJingleSession) {
            acceptButton.setVisible(false);
            showAlert(false);
            if (session.getState() instanceof IncomingJingleSession.Pending) {
                titleLabel.setText("Establishing...");
            }
            else if (session.getState() instanceof IncomingJingleSession.Active) {
                titleLabel.setText("On Phone");
                cancelButton.setVisible(true);
                lastState = session.getState();
                if (ringing != null) ringing.stop();
                roomUI = new JingleRoomUI(session, chatRoom);
                chatRoom.getSplitPane().setRightComponent(roomUI);
                chatRoom.getSplitPane().setResizeWeight(.60);

            }
            else {
                showAlert(true);
                titleLabel.setText("Call Ended");
                cancelButton.setVisible(false);
                retryButton.setVisible(false);
                acceptButton.setVisible(false);
                if (ringing != null) ringing.stop();
                chatRoom.getSplitPane().setRightComponent(null);
            }
        }
    }

    private void makeClickable(final JLabel label) {
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
                e.printStackTrace();
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
            titleLabel.setText("Incoming Call");
            cancelButton.setVisible(true);
            retryButton.setVisible(false);
            acceptButton.setVisible(true);
            if ((lastState == null))
                if (ringing != null) ringing.loop();

            while (!answer && (session != null && !session.isClosed())) {

                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
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
