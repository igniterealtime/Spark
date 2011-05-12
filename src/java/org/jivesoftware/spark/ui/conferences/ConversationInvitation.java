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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.ui.ContainerComponent;
import org.jivesoftware.spark.util.ResourceUtils;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Handles incoming Conference invitations.
 *
 * @author Derek DeMoro
 */
public class ConversationInvitation extends JPanel implements ContainerComponent, ActionListener {
    private static final long serialVersionUID = -5830187619047598274L;

    private JButton joinButton;

    private String roomName;
    private String password;
    private String inviter;

    private String invitationDateFormat = ((SimpleDateFormat)SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM)).toPattern();
    private String tabTitle;
    private String frameTitle;
    private String descriptionText;

    /**
     * Builds a new Conference Invitation UI.
     *
     * @param conn     the XMPPConnection the invitation came in on.
     * @param roomName the name of the room.
     * @param inviter  the person who sent the invitation.
     * @param reason   the reason they want to talk.
     * @param password the password of the room if any.
     * @param message  any additional message.
     */
    public ConversationInvitation(XMPPConnection conn, final String roomName, final String inviter, String reason, final String password, Message message) {
        this.roomName = roomName;
        this.password = password;
        this.inviter = inviter;

        // Set Layout
        setLayout(new GridBagLayout());

        // Build UI
        final JLabel titleLabel = new JLabel();
        final WrappedLabel description = new WrappedLabel();
        description.setBackground(Color.white);

        final JLabel dateLabel = new JLabel();
        final JLabel dateLabelValue = new JLabel();


        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(inviter);


        add(titleLabel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));

        add(description, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 9, 2, 5), 0, 0));


        add(dateLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        add(dateLabelValue, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));

        titleLabel.setFont(new Font("dialog", Font.BOLD, 12));
        description.setFont(new Font("dialog", 0, 12));

        titleLabel.setText(Res.getString("title.conference.invitation"));
        description.setText(nickname + " has invited you to chat in " + roomName + ". " + nickname + " writes \"" + reason + "\"");

        tabTitle = "Chat Invite";
        descriptionText = reason;
        frameTitle = Res.getString("title.conference.invitation");

        // Set Date Label
        dateLabel.setFont(new Font("dialog", Font.BOLD, 12));
        dateLabel.setText(Res.getString("date") + ":");
        final SimpleDateFormat formatter = new SimpleDateFormat(invitationDateFormat);
        final String date = formatter.format(new Date());
        dateLabelValue.setText(date);
        dateLabelValue.setFont(new Font("dialog", Font.PLAIN, 12));

        // Add accept and reject buttons
        joinButton = new JButton("");
        JButton declineButton = new JButton("");
        ResourceUtils.resButton(joinButton, Res.getString("button.accept"));
        ResourceUtils.resButton(declineButton, Res.getString("button.decline"));


        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(joinButton);
        buttonPanel.add(declineButton);
        add(buttonPanel, new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 5, 2, 5), 0, 0));


        add(new JLabel(), new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(2, 5, 2, 5), 0, 0));


        joinButton.addActionListener(this);
        declineButton.addActionListener(this);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
        setBackground(Color.white);

        // Add to Chat window
        ChatManager chatManager = SparkManager.getChatManager();
        chatManager.getChatContainer().addContainerComponent(this);
    }


    public void actionPerformed(ActionEvent actionEvent) {
        final Object obj = actionEvent.getSource();
        if (obj == joinButton) {
            String name = StringUtils.parseName(roomName);
            ConferenceUtils.enterRoomOnSameThread(name, roomName, password);
        }
        else {
            MultiUserChat.decline(SparkManager.getConnection(), roomName, inviter, "No thank you");
        }

        // Close Container
        ChatManager chatManager = SparkManager.getChatManager();
        chatManager.getChatContainer().closeTab(this);
    }


    public String getTabTitle() {
        return tabTitle;
    }

    public String getFrameTitle() {
        return frameTitle;
    }

    public ImageIcon getTabIcon() {
        return SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16);
    }

    public JComponent getGUI() {
        return this;
    }

    public String getToolTipDescription() {
        return descriptionText;
    }

    public boolean closing() {
        return true;
    }
}
