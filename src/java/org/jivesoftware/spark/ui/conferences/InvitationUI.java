/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.ChatNotFoundException;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.spark.util.ResourceUtils;

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
    private GroupChatRoom room = null;

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

        final JLabel inviterLabel = new JLabel(Res.getString("from") + ":");

        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(inviter);
        final JLabel inviterValueLabel = new JLabel(nickname);


        add(titleLabel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));

        add(description, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 9, 2, 5), 0, 0));

        add(inviterLabel, new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        add(inviterValueLabel, new GridBagConstraints(1, 2, 4, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));

        add(dateLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        add(dateLabelValue, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));


        inviterLabel.setFont(new Font("dialog", Font.BOLD, 12));
        inviterValueLabel.setFont(new Font("dialog", Font.PLAIN, 12));

        titleLabel.setFont(new Font("dialog", Font.BOLD, 12));
        description.setFont(new Font("dialog", 0, 12));

        titleLabel.setText(Res.getString("title.conference.invitation"));
        description.setText(reason);

        // Set Date Label
        dateLabel.setFont(new Font("dialog", Font.BOLD, 12));
        dateLabel.setText(Res.getString("date") + ":");
        final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        final String date = formatter.format(new Date());
        dateLabelValue.setText(date);
        dateLabelValue.setFont(new Font("dialog", Font.PLAIN, 12));

        // Add accept and reject buttons
        joinButton = new RolloverButton("", SparkRes.getImageIcon(SparkRes.CIRCLE_CHECK_IMAGE));
        declineButton = new RolloverButton("", SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        ResourceUtils.resButton(joinButton, Res.getString("button.join"));
        ResourceUtils.resButton(declineButton, Res.getString("button.decline"));


        add(joinButton, new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        add(declineButton, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));


        add(new JLabel(), new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(2, 5, 2, 5), 0, 0));


        joinButton.addActionListener(this);
        declineButton.addActionListener(this);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
        setBackground(Color.white);

        // Add to Chat window
        ChatManager chatManager = SparkManager.getChatManager();

        try {
            room = chatManager.getGroupChat(roomName);
        }
        catch (ChatNotFoundException e) {
            MultiUserChat chat = new MultiUserChat(SparkManager.getConnection(), roomName);
            room = new GroupChatRoom(chat);
        }

        room.setTabTitle(Res.getString("title.conference.invitation"));
        room.setTabIcon(SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
        room.getChatWindowPanel().add(this, new GridBagConstraints(0, 9, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(1, 0, 1, 0), 0, 0));

        // set invisible
        room.getSplitPane().getRightComponent().setVisible(false);
        room.getBottomPanel().setVisible(false);
        room.getScrollPaneForTranscriptWindow().setVisible(false);

        SparkManager.getChatManager().getChatContainer().addChatRoom(room);
        SparkManager.getChatManager().getChatContainer().makeTabRed(room);
    }


    public void actionPerformed(ActionEvent actionEvent) {
        final Object obj = actionEvent.getSource();
        if (obj == joinButton) {
            room.getSplitPane().getRightComponent().setVisible(true);
            room.getBottomPanel().setVisible(true);

            room.getScrollPaneForTranscriptWindow().setVisible(true);
            room.getSendFieldToolbar().setVisible(true);
            room.getChatInputEditor().setEnabled(true);
            room.getToolBar().setVisible(true);
            room.getVerticalSlipPane().setDividerLocation(0.8);
            room.getSplitPane().setDividerLocation(0.8);
            this.setVisible(false);

            String name = StringUtils.parseName(roomName);

            GroupChatRoom groupChatRoom = null;
            try {
                groupChatRoom = (GroupChatRoom)SparkManager.getChatManager().getChatRoom(roomName);
                groupChatRoom.setTabTitle(roomName);
                groupChatRoom.getConferenceRoomInfo().setNicknameChangeAllowed(false);

                groupChatRoom.getToolBar().setVisible(true);
                groupChatRoom.getSendFieldToolbar().setVisible(true);
                groupChatRoom.getChatInputEditor().setEnabled(true);

                ChatContainer chatContainer = SparkManager.getChatManager().getChatContainer();
                chatContainer.setChatRoomTitle(groupChatRoom, roomName);
                if (chatContainer.getActiveChatRoom() == groupChatRoom) {
                    chatContainer.getChatFrame().setTitle(roomName);
                }

            }
            catch (Exception e) {
                Log.error(e);
            }

            ConferenceUtils.enterRoomOnSameThread(name, roomName, password);
        }
        else {
            MultiUserChat.decline(SparkManager.getConnection(), roomName, inviter, "No thank you");
            // Add to Chat window
            ChatManager chatManager = SparkManager.getChatManager();
            chatManager.removeChat(room);
        }


    }


}
