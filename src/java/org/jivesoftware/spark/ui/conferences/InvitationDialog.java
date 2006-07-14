/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InvitationDialog extends JPanel {
    private TitlePanel titlePanel;
    private JEditorPane messageLabel;
    private RolloverButton acceptButton;
    private RolloverButton declineButton;

    private String password;
    private String roomName;
    private String inviter;

    private JFrame frame;

    public InvitationDialog() {
        setLayout(new GridBagLayout());

        titlePanel = new TitlePanel("Conference Invitation", null, SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_24x24), true);
        add(titlePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


        messageLabel = new JEditorPane();
        acceptButton = new RolloverButton();
        declineButton = new RolloverButton();

        ResourceUtils.resButton(acceptButton, "&Accept");
        ResourceUtils.resButton(declineButton, "&Decline");

        add(new JScrollPane(messageLabel), new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        add(acceptButton, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(declineButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                acceptInvite();
            }
        });

        declineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                declineInvite();
            }
        });
    }

    public void invitationReceived(XMPPConnection conn, final String room, final String inviter, String reason, final String password, Message message) {
        this.password = password;
        this.roomName = room;
        this.inviter = inviter;

        frame = new JFrame("Conference Invite");
        titlePanel.setDescription("Invitation from " + inviter);
        frame.setIconImage(SparkManager.getMainWindow().getIconImage());

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this);

        messageLabel.setEditorKit(new HTMLEditorKit());

        StringBuffer buf = new StringBuffer();
        buf.append("<font name=dialog>");
        buf.append("<b><u>Room Name</u></b><br> ").append(room);
        buf.append("<br><br>");

        if (reason == null) {
            reason = "Please join me in this conference room.";
        }

        buf.append("<b><u>Message</b></u><br>").append(reason);

        messageLabel.setText(buf.toString());
        frame.setFocusableWindowState(false);
        frame.pack();
        frame.setSize(400, 400);

        GraphicUtils.centerWindowOnScreen(frame);

        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent windowEvent) {
                SparkManager.getAlertManager().stopFlashing(frame);
            }
        });

        // Blink frame if necessary
        SparkManager.getChatManager().getChatContainer().blinkFrameIfNecessary(frame);
    }


    private void acceptInvite() {
        frame.dispose();
        ConferenceUtils.autoJoinConferenceRoom(roomName, roomName, password);
    }

    private void declineInvite() {
        frame.dispose();

        MultiUserChat.decline(SparkManager.getConnection(), roomName, inviter, "No thank you");
    }


}
