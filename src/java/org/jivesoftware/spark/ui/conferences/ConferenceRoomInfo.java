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
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.UserStatusListener;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.ImageTitlePanel;
import org.jivesoftware.spark.component.renderer.ListIconRenderer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The <code>RoomInfo</code> class is used to display all room information, such as agents and room information.
 */
public final class ConferenceRoomInfo extends JPanel implements ChatRoomListener {
    private GroupChatRoom groupChatRoom;
    private final ImageTitlePanel agentInfoPanel = new ImageTitlePanel(Res.getString("message.participants.in.room"));
    private ChatManager chatManager;
    private MultiUserChat chat;

    private final Map userMap = new HashMap();

    private UserManager userManager = SparkManager.getUserManager();
    private DefaultListModel model = new DefaultListModel();
    private JList list = new JList(model);
    private PacketListener listener = null;


    private boolean allowNicknameChange = true;

    /**
     * Creates a new RoomInfo instance using the specified ChatRoom.  The RoomInfo
     * component is responsible for monitoring all activity in the ChatRoom.
     */
    public ConferenceRoomInfo() {
        chatManager = SparkManager.getChatManager();
        list.setCellRenderer(new ListIconRenderer());

        // Set the room to track
        this.setOpaque(true);

        this.setLayout(new BorderLayout());
        this.setOpaque(true);
        this.setBackground(Color.white);

        // Respond to Double-Click in Agent List to start a chat
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selectedUser = getSelectedUser();
                    startChat(groupChatRoom, (String)userMap.get(selectedUser));
                }
            }

            public void mouseReleased(final MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    checkPopup(evt);
                }
            }

            public void mousePressed(final MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    checkPopup(evt);
                }
            }
        });


        JScrollPane scroller = new JScrollPane(list);

        // Speed up scrolling. It was way too slow.
        scroller.getVerticalScrollBar().setBlockIncrement(50);
        scroller.getVerticalScrollBar().setUnitIncrement(20);
        scroller.setBackground(Color.white);
        scroller.getViewport().setBackground(Color.white);


        this.add(scroller, BorderLayout.CENTER);
    }

    public void setChatRoom(final ChatRoom chatRoom) {
        this.groupChatRoom = (GroupChatRoom)chatRoom;

        chatManager.addChatRoomListener(this);

        chat = groupChatRoom.getMultiUserChat();


        listener = new PacketListener() {
            public void processPacket(final Packet packet) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Presence p = (Presence)packet;
                        final String userid = p.getFrom();

                        String nickname = StringUtils.parseResource(userid);
                        userMap.put(nickname, userid);

                        if (p.getType() == Presence.Type.available) {
                            addParticipant(userid, p);
                            agentInfoPanel.setVisible(true);
                        }
                        else {
                            model.removeElement(nickname);
                        }
                    }
                });

            }
        };

        chat.addParticipantListener(listener);
    }

    public void chatRoomOpened(ChatRoom room) {
        if (room != groupChatRoom) {
            return;
        }


        chat.addUserStatusListener(new UserStatusListener() {
            public void kicked(String actor, String reason) {

            }

            public void voiceGranted() {

            }

            public void voiceRevoked() {

            }

            public void banned(String actor, String reason) {

            }

            public void membershipGranted() {

            }

            public void membershipRevoked() {

            }

            public void moderatorGranted() {

            }

            public void moderatorRevoked() {

            }

            public void ownershipGranted() {
            }

            public void ownershipRevoked() {

            }

            public void adminGranted() {

            }

            public void adminRevoked() {

            }
        });
    }

    public void chatRoomLeft(ChatRoom room) {
        if (this.groupChatRoom == room) {
            chatManager.removeChatRoomListener(this);
            agentInfoPanel.setVisible(false);
        }
    }

    public void chatRoomClosed(ChatRoom room) {
        if (this.groupChatRoom == room) {
            chatManager.removeChatRoomListener(this);
            chat.removeParticipantListener(listener);
        }
    }

    public void chatRoomActivated(ChatRoom room) {
    }


    public void userHasJoined(ChatRoom room, String userid) {
    }

    private ImageIcon getImageIcon(String participantJID) {
        String nickname = StringUtils.parseResource(participantJID);
        Occupant occupant = SparkManager.getUserManager().getOccupant(groupChatRoom, nickname);
        boolean isOwnerOrAdmin = SparkManager.getUserManager().isOwnerOrAdmin(occupant);
        boolean isModerator = SparkManager.getUserManager().isModerator(occupant);


        ImageIcon icon = SparkRes.getImageIcon(SparkRes.MODERATOR_IMAGE);
        if (!isOwnerOrAdmin) {
            if (isModerator) {
                icon = SparkRes.getImageIcon(SparkRes.MODERATOR_IMAGE);
            }
            else {
                icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
            }
        }

        icon.setDescription(nickname);
        return icon;
    }

    private void addParticipant(String participantJID, Presence presence) {
        String nickname = StringUtils.parseResource(participantJID);
        Occupant occupant = SparkManager.getUserManager().getOccupant(groupChatRoom, nickname);
        boolean isOwnerOrAdmin = SparkManager.getUserManager().isOwnerOrAdmin(occupant);
        boolean isModerator = SparkManager.getUserManager().isModerator(occupant);

        if (!exists(nickname)) {
            ImageIcon icon = SparkRes.getImageIcon(SparkRes.MODERATOR_IMAGE);

            if (!isOwnerOrAdmin) {
                if (isModerator) {
                    icon = SparkRes.getImageIcon(SparkRes.MODERATOR_IMAGE);
                }
                else {
                    StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
                    StatusItem item = statusBar.getItemFromPresence(presence);
                    if (item != null) {
                        icon = new ImageIcon(item.getImageIcon().getImage());
                    }
                    else {
                        icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
                    }
                }
            }

            icon.setDescription(nickname);
            model.addElement(icon);
        }
        else {
            ImageIcon icon = SparkRes.getImageIcon(SparkRes.MODERATOR_IMAGE);
            if (!isOwnerOrAdmin) {
                if (isModerator) {
                    icon = SparkRes.getImageIcon(SparkRes.MODERATOR_IMAGE);
                }
                else {
                    StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
                    StatusItem item = statusBar.getItemFromPresence(presence);
                    if (item != null) {
                        icon = new ImageIcon(item.getImageIcon().getImage());
                    }
                    else {
                        icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
                    }
                }
            }
            icon.setDescription(nickname);

            int index = getIndex(nickname);
            if (index != -1) {
                model.removeElementAt(index);
                model.insertElementAt(icon, index);
            }
        }
    }

    public void userHasLeft(ChatRoom room, String userid) {
        int index = getIndex(userid);

        if (index != -1) {
            model.removeElementAt(index);
        }
    }

    private boolean exists(String user) {
        for (int i = 0; i < model.getSize(); i++) {
            final ImageIcon icon = (ImageIcon)model.get(i);
            if (icon.getDescription().equals(user)) {
                return true;
            }
        }
        return false;
    }

    private int getIndex(String nickname) {
        for (int i = 0; i < model.getSize(); i++) {
            final ImageIcon icon = (ImageIcon)model.get(i);
            if (icon.getDescription().equals(nickname)) {
                return i;
            }
        }
        return -1;
    }

    private String getSelectedUser() {
        ImageIcon icon = (ImageIcon)list.getSelectedValue();
        if (icon == null) {
            return null;
        }
        return icon.getDescription();
    }


    private void startChat(ChatRoom groupChat, String groupJID) {
        String groupJIDNickname = StringUtils.parseResource(groupJID);
        String nickname = groupChat.getNickname();

        if (groupJIDNickname.equals(nickname)) {
            return;
        }

        ChatRoom chatRoom = null;
        try {
            chatRoom = chatManager.getChatContainer().getChatRoom(groupJID);
        }
        catch (ChatRoomNotFoundException e) {
            Log.error("Could not find chat room - " + groupJID);

            // Create new room
            chatRoom = new ChatRoomImpl(groupJID, groupJIDNickname, groupJID);
            chatManager.getChatContainer().addChatRoom(chatRoom);
        }

        chatManager.getChatContainer().activateChatRoom(chatRoom);
    }

    public void tabSelected() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getTabTitle() {
        return Res.getString("title.room.information");
    }

    public Icon getTabIcon() {
        return SparkRes.getImageIcon(SparkRes.SMALL_BUSINESS_MAN_VIEW);
    }

    public String getTabToolTip() {
        return Res.getString("title.room.information");
    }

    public JComponent getGUI() {
        return this;
    }


    /**
     * ****************************************************************
     */
    /*                     MUC Functions                                */
    private void kickUser(String nickname) {
        try {
            chat.kickParticipant(nickname, Res.getString("message.you.have.been.kicked"));
        }
        catch (XMPPException e) {
            groupChatRoom.insertText(Res.getString("message.kicked.error", nickname));
        }
    }

    private void banUser(String nickname) {
        try {
            Occupant occupant = chat.getOccupant((String)userMap.get(nickname));
            if (occupant != null) {
                String bareJID = StringUtils.parseBareAddress(occupant.getJid());
                chat.banUser(bareJID, Res.getString("message.you.have.been.banned"));
            }
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }

    private void unbanUser(String jid) {
        try {
            chat.grantMembership(jid);
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }

    private void grantVoice(String nickname) {
        try {
            chat.grantVoice(nickname);
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }

    private void revokeVoice(String nickname) {
        try {
            chat.revokeVoice(nickname);
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }


    private void grantModerator(String nickname) {
        try {
            chat.grantModerator(nickname);
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }

    private void revokeModerator(String nickname) {
        try {
            chat.revokeModerator(nickname);
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }


    /**
     * Let's make sure that the panel doesn't strech past the
     * scrollpane view pane.
     *
     * @return the preferred dimension
     */
    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 150;
        return size;
    }

    private void checkPopup(MouseEvent evt) {
        final int index = list.locationToIndex(evt.getPoint());
        list.setSelectedIndex(index);

        final String selectedUser = getSelectedUser();
        final String groupJID = (String)userMap.get(selectedUser);
        String groupJIDNickname = StringUtils.parseResource(groupJID);

        final String nickname = groupChatRoom.getNickname();
        final Occupant occupant = userManager.getOccupant(groupChatRoom, selectedUser);
        final boolean admin = SparkManager.getUserManager().isOwnerOrAdmin(groupChatRoom, chat.getNickname());
        final boolean moderator = SparkManager.getUserManager().isModerator(groupChatRoom, chat.getNickname());

        final boolean userIsAdmin = userManager.isOwnerOrAdmin(occupant);
        final boolean userIsModerator = userManager.isModerator(occupant);
        boolean isMe = groupJIDNickname.equals(nickname);

        JPopupMenu popup = new JPopupMenu();
        if (isMe) {
            Action changeNicknameAction = new AbstractAction() {
                public void actionPerformed(ActionEvent actionEvent) {
                    String newNickname = JOptionPane.showInputDialog(groupChatRoom, Res.getString("label.new.nickname") +":", Res.getString("title.change.nickname"), JOptionPane.QUESTION_MESSAGE);
                    if (ModelUtil.hasLength(newNickname)) {
                        while (true) {
                            newNickname = newNickname.trim();
                            try {
                                chat.changeNickname(newNickname);
                                break;
                            }
                            catch (XMPPException e1) {
                                newNickname = JOptionPane.showInputDialog(groupChatRoom, Res.getString("message.nickname.in.use") + ":", Res.getString("title.change.nickname"), JOptionPane.QUESTION_MESSAGE);
                                if (!ModelUtil.hasLength(newNickname)) {
                                    break;
                                }
                            }
                        }
                    }
                }
            };

            changeNicknameAction.putValue(Action.NAME, Res.getString("menuitem.change.nickname"));
            changeNicknameAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DESKTOP_IMAGE));

            if (allowNicknameChange) {
                popup.add(changeNicknameAction);
            }
        }

        Action chatAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                String selectedUser = getSelectedUser();
                startChat(groupChatRoom, (String)userMap.get(selectedUser));
            }
        };

        chatAction.putValue(Action.NAME, Res.getString("menuitem.start.a.chat"));
        chatAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
        if (!isMe) {
            popup.add(chatAction);
        }

        Action blockAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = (ImageIcon)list.getSelectedValue();
                String description = icon.getDescription();
                if (groupChatRoom.isBlocked(groupJID)) {
                    groupChatRoom.removeBlockedUser(groupJID);
                    icon = getImageIcon(groupJID);
                    model.setElementAt(icon, index);
                }
                else {
                    groupChatRoom.addBlockedUser(groupJID);
                    icon = SparkRes.getImageIcon(SparkRes.BRICKWALL_IMAGE);
                    icon.setDescription(description);
                    model.setElementAt(icon, index);
                }
            }
        };

        blockAction.putValue(Action.NAME, Res.getString("menuitem.block.user"));
        blockAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.BRICKWALL_IMAGE));
        if (!isMe) {
            if (groupChatRoom.isBlocked(groupJID)) {
                blockAction.putValue(Action.NAME, Res.getString("menuitem.unblock.user"));
            }
            popup.add(blockAction);
        }


        Action kickAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                kickUser(selectedUser);
            }
        };

        kickAction.putValue(Action.NAME, Res.getString("menuitem.kick.user"));
        kickAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        if (moderator && !userIsAdmin && !isMe) {
            popup.add(kickAction);
        }

        // Handle Voice Operations
        Action voiceAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (userManager.hasVoice(groupChatRoom, selectedUser)) {
                    revokeVoice(selectedUser);
                }
                else {
                    grantVoice(selectedUser);
                }

            }
        };

        voiceAction.putValue(Action.NAME, Res.getString("menuitem.voice"));
        voiceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
        if (moderator && !userIsModerator && !isMe) {
            if (userManager.hasVoice(groupChatRoom, selectedUser)) {
                voiceAction.putValue(Action.NAME, Res.getString("menuitem.revoke.voice"));
            }
            else {
                voiceAction.putValue(Action.NAME, Res.getString("menuitem.grant.voice"));
            }
            popup.add(voiceAction);
        }


        Action banAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                banUser(selectedUser);
            }
        };
        banAction.putValue(Action.NAME, Res.getString("menuitem.ban.user"));
        banAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.RED_FLAG_16x16));
        if (admin && !userIsModerator && !isMe) {
            popup.add(banAction);
        }


        Action moderatorAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (!userIsModerator) {
                    grantModerator(selectedUser);
                }
                else {
                    revokeModerator(selectedUser);
                }
            }
        };

        moderatorAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.MODERATOR_IMAGE));
        if (admin && !userIsModerator) {
            moderatorAction.putValue(Action.NAME, Res.getString("menuitem.grant.moderator"));
            popup.add(moderatorAction);
        }
        else if (admin && userIsModerator && !isMe) {
            moderatorAction.putValue(Action.NAME, Res.getString("menuitem.revoke.moderator"));
            popup.add(moderatorAction);
        }

        // Handle Unbanning of users.
        Action unbanAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                String jid = ((JMenuItem)actionEvent.getSource()).getText();
                unbanUser(jid);
            }
        };

        if (admin) {
            JMenu unbanMenu = new JMenu(Res.getString("menuitem.unban"));
            Iterator bannedUsers = null;
            try {
                bannedUsers = chat.getOutcasts().iterator();
            }
            catch (XMPPException e) {
                Log.error("Error loading all banned users", e);
            }

            while (bannedUsers != null && bannedUsers.hasNext()) {
                Affiliate bannedUser = (Affiliate)bannedUsers.next();
                ImageIcon icon = SparkRes.getImageIcon(SparkRes.RED_BALL);
                JMenuItem bannedItem = new JMenuItem(bannedUser.getJid(), icon);
                unbanMenu.add(bannedItem);
                bannedItem.addActionListener(unbanAction);
            }

            if (unbanMenu.getMenuComponentCount() > 0) {
                popup.add(unbanMenu);
            }
        }


        Action inviteAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                ConferenceUtils.inviteUsersToRoom(groupChatRoom.getConferenceService(), groupChatRoom.getRoomname(), null);
            }
        };

        inviteAction.putValue(Action.NAME, Res.getString("menuitem.invite.users"));
        inviteAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
        popup.addSeparator();
        popup.add(inviteAction);


        popup.show(list, evt.getX(), evt.getY());
    }

    public void setNicknameChangeAllowed(boolean allowed) {
        allowNicknameChange = allowed;
    }
}


