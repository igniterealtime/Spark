/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Default;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.WrappedLabel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class InvitationUI extends JPanel implements ActionListener {

    private RolloverButton joinButton;
    private RolloverButton declineButton;

    private String roomName;
    private String password;
    private String inviter;

    private Image backgroundImage;


    public InvitationUI(XMPPConnection conn, final String roomName, final String inviter, String reason, final String password, Message message) {
        this.roomName = roomName;
        this.password = password;
        this.inviter = inviter;

        backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();

        // Set Layout
        setLayout(new GridBagLayout());

        // Build UI
        final JLabel titleLabel = new JLabel();
        final WrappedLabel description = new WrappedLabel();

        final JLabel dateLabel = new JLabel();
        final JLabel dateLabelValue = new JLabel();

        final JLabel inviterLabel = new JLabel("From:");

        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(inviter);
        final JLabel inviterValueLabel = new JLabel(nickname);


        add(titleLabel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));

        add(description, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 9, 2, 5), 0, 0));

        add(inviterLabel, new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        add(inviterValueLabel, new GridBagConstraints(1, 2, 4, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));

        add(dateLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        add(dateLabelValue, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));


        inviterLabel.setFont(new Font("dialog", Font.BOLD, 11));
        inviterValueLabel.setFont(new Font("dialog", Font.PLAIN, 11));

        titleLabel.setFont(new Font("dialog", Font.BOLD, 11));
        description.setFont(new Font("dialog", 0, 11));

        titleLabel.setText("Conference Invitation");
        description.setText(reason);

        // Set Date Label
        dateLabel.setFont(new Font("dialog", Font.BOLD, 11));
        dateLabel.setText("Date:");
        final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        final String date = formatter.format(new Date());
        dateLabelValue.setText(date);
        dateLabelValue.setFont(new Font("dialog", Font.PLAIN, 11));

        // Add accept and reject buttons
        joinButton = new RolloverButton("Join", null);
        declineButton = new RolloverButton("Decline", null);

        add(joinButton, new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        add(declineButton, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));


        joinButton.addActionListener(this);
        declineButton.addActionListener(this);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
        setBackground(Color.white);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        final Object obj = actionEvent.getSource();
        if (obj == joinButton) {
            String name = StringUtils.parseName(roomName);
            ConferenceUtils.autoJoinConferenceRoom(name, roomName, password);
        }
        else {
            MultiUserChat.decline(SparkManager.getConnection(), roomName, inviter, "No thank you");
        }

        SparkManager.getWorkspace().removeAlert(this);
    }


}
