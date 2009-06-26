/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.rooms;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.MessageEventNotificationListener;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.DefaultUserStatusListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Destroy;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.GroupChatRoomTransferHandler;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.ui.conferences.DataFormDialog;
import org.jivesoftware.spark.ui.conferences.GroupChatParticipantList;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * GroupChatRoom is the conference chat room UI used to have Multi-User Chats.
 */
public final class GroupChatRoom extends ChatRoom {
    private static final long serialVersionUID = 4469579438292227006L;

    private final MultiUserChat chat;

    private final String roomname;
    private Icon tabIcon = SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16);
    private String tabTitle;
    private boolean isActive = true;
    private SubjectPanel subjectPanel;

    private List<String> currentUserList = new ArrayList<String>();

    private String conferenceService;
    private List<String> blockedUsers = new ArrayList<String>();

    private ChatRoomMessageManager messageManager;
    private Timer typingTimer;
    private int typedChars;

    private GroupChatParticipantList roomInfo;

    private long lastActivity;
    
    LocalPreferences pref = SettingsManager.getLocalPreferences();
    private boolean showPresenceMessages = pref.isShowJoinLeaveMessagesEnabled();
    private boolean isMucHighlightingNameEnabled = pref.isMucHighNameEnabled();
    private boolean isMucHighlightingTextEnabled = pref.isMucHighTextEnabled();

    /**
     * Creates a GroupChatRoom from a <code>MultiUserChat</code>.
     *
     * @param chat the MultiUserChat to create a GroupChatRoom from.
     */
    public GroupChatRoom(final MultiUserChat chat) {
        this.chat = chat;

        // Create the filter and register with the current connection
        // making sure to filter by room
        PacketFilter fromFilter = new FromContainsFilter(chat.getRoom());
        PacketFilter orFilter = new OrFilter(new PacketTypeFilter(Presence.class), new PacketTypeFilter(Message.class));
        PacketFilter andFilter = new AndFilter(orFilter, fromFilter);

        // Add packet Listener.
        SparkManager.getConnection().addPacketListener(this, andFilter);

        // Thie Room Name is the same as the ChatRoom name
        roomname = chat.getRoom();

        // We are just using a generic Group Chat.
        tabTitle = StringUtils.parseName(StringUtils.unescapeNode(roomname));

        // Room Information
        roomInfo = new GroupChatParticipantList();
        getSplitPane().setRightComponent(roomInfo.getGUI());

        roomInfo.setChatRoom(this);
        getSplitPane().setResizeWeight(.60);
        getSplitPane().setDividerLocation(.60);

        setupListeners();


        conferenceService = StringUtils.parseServer(chat.getRoom());

        subjectPanel = new SubjectPanel();

        // Do not show top toolbar
        getToolBar().add(subjectPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0));

        // Add ContextMenuListener
        getTranscriptWindow().addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object component, JPopupMenu popup) {
                popup.addSeparator();
                Action inviteAction = new AbstractAction() {
		    private static final long serialVersionUID = -2493782261839364071L;

		    public void actionPerformed(ActionEvent actionEvent) {
                        ConferenceUtils.inviteUsersToRoom(conferenceService, getRoomname(), null);
                    }
                };

                inviteAction.putValue(Action.NAME, Res.getString("menuitem.invite.users"));
                inviteAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));

                popup.add(inviteAction);


                Action configureAction = new AbstractAction() {
		    private static final long serialVersionUID = -7463450970328104297L;

		    public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
                            Form form = chat.getConfigurationForm().createAnswerForm();
                            new DataFormDialog(chatFrame, chat, form);
                        }
                        catch (XMPPException e) {
                            Log.error("Error configuring room.", e);
                        }
                    }
                };

                configureAction.putValue(Action.NAME, Res.getString("title.configure.room"));
                configureAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_ALL_AGENTS_IMAGE));
                if (SparkManager.getUserManager().isOwner((GroupChatRoom)getChatRoom(), chat.getNickname())) {
                    popup.add(configureAction);
                }

                Action subjectAction = new AbstractAction() {
		    private static final long serialVersionUID = 6730534406025965089L;

		    public void actionPerformed(ActionEvent actionEvent) {
                        String newSubject = JOptionPane.showInputDialog(getChatRoom(), Res.getString("message.enter.new.subject") + ":", Res.getString("title.change.subject"), JOptionPane.QUESTION_MESSAGE);
                        if (ModelUtil.hasLength(newSubject)) {
                            try {
                                chat.changeSubject(newSubject);
                            }
                            catch (XMPPException e) {
                                Log.error(e);
                            }
                        }
                    }
                };

                subjectAction.putValue(Action.NAME, Res.getString("menuitem.change.subject"));
                subjectAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_EDIT_IMAGE));
                popup.add(subjectAction);

                // Define actions to modify/view room information
                Action destroyRoomAction = new AbstractAction() {
		    private static final long serialVersionUID = 6494204166819377882L;

		    public void actionPerformed(ActionEvent e) {
                        int ok = JOptionPane.showConfirmDialog(getChatRoom(), Res.getString("message.confirm.destruction.of.room"), Res.getString("title.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (ok == JOptionPane.NO_OPTION) {
                            return;
                        }

                        String reason = JOptionPane.showInputDialog(getChatRoom(), Res.getString("message.room.destruction.reason"), Res.getString("title.enter.reason"), JOptionPane.QUESTION_MESSAGE);
                        if (ModelUtil.hasLength(reason)) {
                            try {
                                chat.destroy(reason, null);
                                getChatRoom().leaveChatRoom();
                            }
                            catch (XMPPException e1) {
                                Log.warning("Unable to destroy room", e1);
                            }
                        }
                    }
                };

                destroyRoomAction.putValue(Action.NAME, Res.getString("menuitem.destroy.room"));
                destroyRoomAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
                if (SparkManager.getUserManager().isOwner((GroupChatRoom)getChatRoom(), getNickname())) {
                    popup.add(destroyRoomAction);
                }


            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });


        messageManager = new ChatRoomMessageManager();

        // set last activity to be right now
        lastActivity = System.currentTimeMillis();

        GroupChatRoomTransferHandler transferHandler = new GroupChatRoomTransferHandler(this);
        getTranscriptWindow().setTransferHandler(transferHandler);
    }

    /**
     * Sets the title to use on the tab describing the Conference room.
     *
     * @param tabTitle the title to use on the tab.
     */
    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    /**
     * Call this method if you wish to hide the participant list.
     */
    public void hideParticipantList() {
        getSplitPane().setRightComponent(null);
    }

    /**
     * Have the user leave this chat room and then close it.
     */
    public void closeChatRoom() {
        // Specify the end time.
        super.closeChatRoom();

        // Remove Listener
        SparkManager.getConnection().removePacketListener(this);

        ChatContainer container = SparkManager.getChatManager().getChatContainer();
        container.leaveChatRoom(this);
        container.closeTab(this);
    }

    /**
     * Determines the background color to use for messages.
     *
     * @param nickname Nickname associated with message.
     * @param body Body of message to scan for reasons to highlight.
     * @return Color of message background.
     */
    private Color getMessageBackground(String nickname, String body) {
        // define some vcard information
        String myNickName = chat.getNickname();
        String myUserName = SparkManager.getSessionManager().getUsername();
        Pattern usernameMatch = Pattern.compile(myUserName, Pattern.CASE_INSENSITIVE);
        Pattern nicknameMatch = Pattern.compile(myNickName, Pattern.CASE_INSENSITIVE);

        // Should we even highlight this packet?
        if (isMucHighlightingNameEnabled && myNickName.equalsIgnoreCase(nickname)) {
            return new Color(244, 248, 255);
        } else if (isMucHighlightingTextEnabled && (usernameMatch.matcher(body).find() || nicknameMatch.matcher(body).find())) {
            return new Color(255, 255, 153);
        } else {
            return Color.white;
        }
    }

    /**
     * Sends a message.
     *
     * @param message - the message to send.
     */
    public void sendMessage(Message message) {
        try {
            message.setTo(chat.getRoom());
            message.setType(Message.Type.groupchat);
            MessageEventManager.addNotificationsRequests(message, true, true, true, true);
            // Add packetID to list
            addPacketID(message.getPacketID());

            // Fire Message Filters
            SparkManager.getChatManager().filterOutgoingMessage(this, message);

            // Fire Global Listeners
            SparkManager.getChatManager().fireGlobalMessageSentListeners(this, message);

            chat.sendMessage(message);
        }
        catch (XMPPException ex) {
            Log.error("Unable to send message in conference chat.", ex);
        }

        try {
            getTranscriptWindow().insertMessage(getNickname(), message, getColor(getNickname()), getMessageBackground(getNickname(), message.getBody()));
            getChatInputEditor().selectAll();

            getTranscriptWindow().validate();
            getTranscriptWindow().repaint();
            getChatInputEditor().clear();
        }
        catch (Exception ex) {
            Log.error("Error sending message", ex);
        }

        // Notify users that message has been sent
        fireMessageSent(message);

        addToTranscript(message, false);

        getChatInputEditor().setCaretPosition(0);
        getChatInputEditor().requestFocusInWindow();
        scrollToBottom();

        lastActivity = System.currentTimeMillis();
    }

    /**
     * Sends a message.
     *
     * @param message - the message to send.
     */
    public void sendMessageWithoutNotification(Message message) {
        try {
            message.setTo(chat.getRoom());
            message.setType(Message.Type.groupchat);
            MessageEventManager.addNotificationsRequests(message, true, true, true, true);
            // Add packetID to list
            addPacketID(message.getPacketID());

            chat.sendMessage(message);
        }
        catch (XMPPException ex) {
            Log.error("Unable to send message in conference chat.", ex);
        }

        try {
            getTranscriptWindow().insertMessage(getNickname(), message, getColor(getNickname()), getMessageBackground(getNickname(), message.getBody()));
            getChatInputEditor().selectAll();

            getTranscriptWindow().validate();
            getTranscriptWindow().repaint();
            getChatInputEditor().clear();
        }
        catch (Exception ex) {
            Log.error("Error sending message", ex);
        }

        addToTranscript(message, false);

        getChatInputEditor().setCaretPosition(0);
        getChatInputEditor().requestFocusInWindow();
        scrollToBottom();

        lastActivity = System.currentTimeMillis();
    }

    /**
     * Return name of the room specified when the room was created.
     *
     * @return the roomname.
     */
    public String getRoomname() {
        return roomname;
    }

    /**
     * Retrieve the nickname of the user in this groupchat.
     *
     * @return the nickname of the agent in this groupchat
     */
    public String getNickname() {
        return chat.getNickname();
    }

    /**
     * Sets the icon to use on the tab.
     *
     * @param tabIcon the icon to use on the tab.
     */
    public void setTabIcon(Icon tabIcon) {
        this.tabIcon = tabIcon;
    }

    /**
     * Return the Icon that should be used in the tab of this GroupChat Pane.
     *
     * @return the Icon to use in tab.
     */
    public Icon getTabIcon() {
        return tabIcon;
    }

    /**
     * Return the title that should be used in the tab.
     *
     * @return the title to be used on the tab.
     */
    public String getTabTitle() {
        return tabTitle;
    }

    /**
     * Return the title of this room.
     *
     * @return the title of this room.
     */
    public String getRoomTitle() {
        return getTabTitle();
    }

    /**
     * Return the type of chat we are in.
     *
     * @return the type of chat we are in.
     */
    public Message.Type getChatType() {
        return Message.Type.groupchat;
    }

    /**
     * Implementation of leaveChatRoom.
     */
    public void leaveChatRoom() {
        if (!isActive) {
            return;
        }

        // Remove Packet Listener
        SparkManager.getConnection().removePacketListener(this);

        // Disable Send Field
        getChatInputEditor().showAsDisabled();

        // Do not allow other to try and invite or transfer chat
        disableToolbar();

        getToolBar().setVisible(false);
        
        // Update Room Notice To Inform Agent that he has left the chat.
        getTranscriptWindow().insertNotificationMessage(Res.getString("message.user.left.room", getNickname()), ChatManager.NOTIFICATION_COLOR);

        // Leave the Chat.
        try {
            chat.leave();
        }
        catch (Exception e) {
            Log.error("Closing Group Chat Room error.", e);
        }

        // Set window as greyed out.
        getTranscriptWindow().showWindowDisabled();

        // Update Notification Label
        getNotificationLabel().setText(Res.getString("message.chat.session.ended", SparkManager.DATE_SECOND_FORMATTER.format(new java.util.Date())));
        getNotificationLabel().setIcon(null);
        getNotificationLabel().setEnabled(false);

        getSplitPane().setRightComponent(null);
        getSplitPane().setDividerSize(0);

        isActive = false;
    }

    /**
     * If true, will display all presence messages. Set to false to turn off presence
     * notifications.
     *
     * @param showMessages true to display presence messages, otherwise false.
     */
    public void showPresenceMessages(boolean showMessages) {
        showPresenceMessages = showMessages;
    }

    /**
     * Returns whether or not this ChatRoom is active. To be active
     * means to have the agent still engaged in a conversation with a
     * customer.
     *
     * @return true if the ChatRoom is active.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Returns the number of participants in this room.
     *
     * @return the number of participants in this room.
     */
    public int getParticipantCount() {
        if (!isActive) {
            return 0;
        }
        return chat.getOccupantsCount();
    }

    /**
     * Implementation of processPacket to handle muc related packets.
     *
     * @param packet the packet.
     */
    public void processPacket(final Packet packet) {
        super.processPacket(packet);
        if (packet instanceof Presence) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handlePresencePacket(packet);
                }
            });

        }
        if (packet instanceof Message) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleMessagePacket(packet);

                    // Set last activity
                    lastActivity = System.currentTimeMillis();
                }
            });

        }
    }

    /**
     * Handle all MUC related packets.
     *
     * @param packet the packet.
     */
    private void handleMessagePacket(Packet packet) {
        // Do something with the incoming packet here.
        final Message message = (Message)packet;
        if (message.getType() == Message.Type.groupchat) {
            DelayInformation inf = (DelayInformation)message.getExtension("x", "jabber:x:delay");
            Date sentDate;
            if (inf != null) {
                sentDate = inf.getStamp();
            }
            else {
                sentDate = new Date();
            }


            // Do not accept Administrative messages.
            String host = SparkManager.getSessionManager().getServerAddress();
            if (host.equals(message.getFrom())) {
                return;
            }

            String messageNickname = StringUtils.parseResource(message.getFrom());

            boolean isFromMe = messageNickname.equals(getNickname()) && inf == null;

            // If the message is not from the current user. Append to chat.
            if (ModelUtil.hasLength(message.getBody()) && !isFromMe) {
                // Update transcript
                super.insertMessage(message);

                String from = StringUtils.parseResource(message.getFrom());

                if (inf != null) {
                    getTranscriptWindow().insertHistoryMessage(from, message.getBody(), sentDate);
                }
                else {
                    if (isBlocked(message.getFrom())) {
                        return;
                    }

                    boolean isFromRoom = message.getFrom().indexOf("/") == -1;

                    if (!SparkManager.getUserManager().hasVoice(this, StringUtils.parseResource(message.getFrom())) && !isFromRoom) {
                        return;
                    }

                    getTranscriptWindow().insertMessage(from, message, getColor(from), getMessageBackground(from, message.getBody()));
                }

                if (typingTimer != null) {
                    showDefaultTabIcon();
                }
            }
        }
        else if (message.getType() == Message.Type.chat) {
            ChatRoom chatRoom;
            try {
                chatRoom = SparkManager.getChatManager().getChatContainer().getChatRoom(message.getFrom());
                // TODO: Why was this not here before?  Is it supposed to be ignored??
                if (message.getBody() != null) {
                    chatRoom.insertMessage(message);
                }
            }
            catch (ChatRoomNotFoundException e) {
                String userNickname = StringUtils.parseResource(message.getFrom());
                String roomTitle = userNickname + " - " + StringUtils.parseName(getRoomname());

                // Create new room
                chatRoom = new ChatRoomImpl(message.getFrom(), userNickname, roomTitle);
                SparkManager.getChatManager().getChatContainer().addChatRoom(chatRoom);

                SparkManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);

                // Check to see if this is a message notification.
                if (message.getBody() != null) {
                    chatRoom.insertMessage(message);
                }
            }

        }
        else if (message.getError() != null) {
            String errorMessage = "";

            if (message.getError().getCode() == 403 && message.getSubject() != null) {
                errorMessage = Res.getString("message.subject.change.error");
            }

            else if (message.getError().getCode() == 403) {
                errorMessage = Res.getString("message.forbidden.error");
            }

            if (ModelUtil.hasLength(errorMessage)) {
                getTranscriptWindow().insertNotificationMessage(errorMessage, ChatManager.ERROR_COLOR);
            }
        }
    }

    /**
     * Handle all presence packets being sent to this Group Chat Room.
     *
     * @param packet the presence packet.
     */
    private void handlePresencePacket(Packet packet) {
        Presence presence = (Presence)packet;
        if (presence.getError() != null) {
            return;
        }

        final String from = presence.getFrom();
        final String nickname = StringUtils.parseResource(from);

        MUCUser mucUser = (MUCUser)packet.getExtension("x", "http://jabber.org/protocol/muc#user");
        String code = "";
        if (mucUser != null) {
            code = mucUser.getStatus() != null ? mucUser.getStatus().getCode() : "";

            Destroy destroy = mucUser.getDestroy();
            if (destroy != null) {
                String reason = destroy.getReason();
                JOptionPane.showMessageDialog(this, Res.getString("message.room.destroyed", reason), Res.getString("title.room.destroyed"), JOptionPane.INFORMATION_MESSAGE);
                leaveChatRoom();
                return;
            }
        }


        if (presence.getType() == Presence.Type.unavailable && !"303".equals(code)) {
            if (currentUserList.contains(from)) {
                if (showPresenceMessages) {
                    getTranscriptWindow().insertNotificationMessage(Res.getString("message.user.left.room", nickname), ChatManager.NOTIFICATION_COLOR);
                    scrollToBottom();
                }
                currentUserList.remove(from);
            }
        }
        else {
            if (!currentUserList.contains(from)) {
                currentUserList.add(from);
                getChatInputEditor().setEnabled(true);
                if (showPresenceMessages) {
                    getTranscriptWindow().insertNotificationMessage(Res.getString("message.user.joined.room", nickname), ChatManager.NOTIFICATION_COLOR);
                    scrollToBottom();
                }
            }
        }
    }

    /**
     * Set up the participant listeners and status change listeners.
     */
    private void setupListeners() {
        chat.addParticipantStatusListener(new DefaultParticipantStatusListener() {
            public void kicked(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.kicked.from.room", nickname));
            }

            public void voiceGranted(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.given.voice", nickname));
            }

            public void voiceRevoked(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.voice.revoked", nickname));
            }

            public void banned(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.banned", nickname));
            }

            public void membershipGranted(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.granted.membership", nickname));
            }

            public void membershipRevoked(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.revoked.membership", nickname));
            }

            public void moderatorGranted(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.granted.moderator", nickname));
            }

            public void moderatorRevoked(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.revoked.moderator", nickname));
            }

            public void ownershipGranted(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.granted.owner", nickname));
            }

            public void ownershipRevoked(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.revoked.owner", nickname));
            }

            public void adminGranted(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.granted.admin", nickname));
            }

            public void adminRevoked(String participant) {
                String nickname = StringUtils.parseResource(participant);
                insertText(Res.getString("message.user.revoked.admin", nickname));
            }

            public void nicknameChanged(String participant, String nickname) {
                insertText(Res.getString("message.user.nickname.changed", StringUtils.parseResource(participant), nickname));
            }
        });


        chat.addUserStatusListener(new DefaultUserStatusListener() {
            public void kicked(String s, String reason) {
                if (ModelUtil.hasLength(reason)) {
                    insertText(reason);
                }
                else {
                    insertText(Res.getString("message.your.kicked", s));
                }

                getChatInputEditor().setEnabled(false);
                getSplitPane().setRightComponent(null);
                leaveChatRoom();
            }

            public void voiceGranted() {
                insertText(Res.getString("message.your.voice.granted"));
                getChatInputEditor().setEnabled(true);
            }

            public void voiceRevoked() {
                insertText(Res.getString("message.your.voice.revoked"));
                getChatInputEditor().setEnabled(false);
            }

            public void banned(String s, String reason) {
                insertText(Res.getString("message.your.banned"));
            }

            public void membershipGranted() {
                insertText(Res.getString("message.your.membership.granted"));
            }

            public void membershipRevoked() {
                insertText(Res.getString("message.your.membership.revoked"));
            }

            public void moderatorGranted() {
                insertText(Res.getString("message.your.moderator.granted"));
            }

            public void moderatorRevoked() {
                insertText(Res.getString("message.your.moderator.revoked"));
            }

            public void ownershipGranted() {
                insertText(Res.getString("message.your.ownership.granted"));
            }

            public void ownershipRevoked() {
                insertText(Res.getString("message.your.ownership.revoked"));
            }

            public void adminGranted() {
                insertText(Res.getString("message.your.admin.granted"));
            }

            public void adminRevoked() {
                insertText(Res.getString("message.your.revoked.granted"));
            }
        });

        chat.addSubjectUpdatedListener(new SubjectListener());
    }

    /**
     * Inserts a notification message within the TranscriptWindow.
     *
     * @param text the text to insert.
     */
    public void insertText(String text) {
        getTranscriptWindow().insertNotificationMessage(text, ChatManager.NOTIFICATION_COLOR);
    }

    /**
     * Listens for a change in subject and notified containing class.
     */
    private class SubjectListener implements SubjectUpdatedListener {

        public void subjectUpdated(String subject, String by) {
            subjectPanel.setSubject(subject);
            subjectPanel.setToolTipText(subject);

            String insertMessage = Res.getString("message.subject.has.been.changed.to", subject);
            getTranscriptWindow().insertNotificationMessage(insertMessage, ChatManager.NOTIFICATION_COLOR);

        }
    }

    /**
     * Returns the user format (e.g. darkcave@macbeth.shakespeare.lit/thirdwitch) of each user in the room.
     *
     * @return the user format (e.g. darkcave@macbeth.shakespeare.lit/thirdwitch) of each user in the room.
     */
    public Collection<String> getParticipants() {
        return currentUserList;
    }

    /**
     * Sends the message that is currently in the send field. The message
     * is automatically added to the transcript for later retrieval.
     */
    public void sendMessage() {
        final String text = getChatInputEditor().getText();
        sendMessage(text);
    }

    public void sendMessage(String text) {
        // Create message object
        Message message = new Message();
        message.setBody(text);

        sendMessage(message);
    }

    /**
     * Returns a MultiUserChat object associated with this room.
     *
     * @return the <code>MultiUserChat</code> object associated with this room.
     */
    public MultiUserChat getMultiUserChat() {
        return chat;
    }


    /**
     * Returns the conference service associated with this Conference Chat.
     *
     * @return the conference service associated with this Conference Chat.
     */
    public String getConferenceService() {
        return conferenceService;
    }

    /**
     * Adds a user to the blocked user list. Blocked users  is NOT a MUC related
     * item, but rather used by the client to not display messages from certain people.
     *
     * @param usersJID the room jid of the user (ex.spark@conference.jivesoftware.com/Dan)
     */
    public void addBlockedUser(String usersJID) {
        blockedUsers.add(usersJID);
    }

    /**
     * Removes a user from the blocked user list.
     *
     * @param usersJID the jid of the user (ex. spark@conference.jivesoftware.com/Dan)
     */
    public void removeBlockedUser(String usersJID) {
        blockedUsers.remove(usersJID);
    }

    /**
     * Returns true if the user is in the blocked user list.
     *
     * @param usersJID the jid of the user (ex. spark@conference.jivesoftware.com/Dan)
     * @return true if the user is blocked, otherwise false.
     */
    public boolean isBlocked(String usersJID) {
        return blockedUsers.contains(usersJID);
    }

    /**
     * Specifies whether to use typing notifications or not. By default,
     * group chat rooms will NOT use typing notifications.
     *
     * @param sendAndReceiveTypingNotifications
     *         true to use typing notifications.
     */
    public void setSendAndReceiveTypingNotifications(boolean sendAndReceiveTypingNotifications) {
        if (sendAndReceiveTypingNotifications) {
            typingTimer = new Timer(10000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showDefaultTabIcon();
                }
            });
            SparkManager.getMessageEventManager().addMessageEventNotificationListener(messageManager);
        }
        else {
            if (typingTimer != null) {
                typingTimer.stop();
            }
            SparkManager.getMessageEventManager().removeMessageEventNotificationListener(messageManager);
        }
    }

    /**
     * Invite a user to this conference room.
     *
     * @param jid     the jid of the user to invite.
     * @param message the message to send with the invitation.
     */
    public void inviteUser(String jid, String message) {
        message = message != null ? message : Res.getString("message.please.join.in.conference");

        // Invite User
        getMultiUserChat().invite(jid, message);

        // Add Invite
        roomInfo.addInvitee(jid, message);
    }

    /**
     * Private implementation of the MessageEventNotificationListener.
     */
    private class ChatRoomMessageManager implements MessageEventNotificationListener {

        ChatRoomMessageManager() {

        }

        public void deliveredNotification(String from, String packetID) {
        }

        public void displayedNotification(String from, String packetID) {
        }

        public void composingNotification(final String from, String packetID) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String bareAddress = StringUtils.parseBareAddress(from);

                    if (bareAddress.equals(getRoomname())) {
                        showUserIsTyping();
                    }
                }
            });
        }

        public void offlineNotification(String from, String packetID) {
        }

        public void cancelledNotification(String from, String packetID) {
        }
    }


    /**
     * Displays a typing icon in the tab.
     */
    private void showUserIsTyping() {
        SparkManager.getChatManager().addTypingNotification(this);
        typingTimer.restart();

        // Notify handlers
        SparkManager.getChatManager().notifySparkTabHandlers(this);
    }

    /**
     * Displays the default icon for the chat room.
     */
    private void showDefaultTabIcon() {
        SparkManager.getChatManager().removeTypingNotification(this);

        // Notify handlers
        SparkManager.getChatManager().notifySparkTabHandlers(this);
    }

    /**
     * The current SendField has been updated somehow.
     *
     * @param e - the DocumentEvent to respond to.
     */
    public void insertUpdate(DocumentEvent e) {
        checkForText(e);

        typedChars++;

        // If the user pauses for more than two seconds, send out a new notice.
        if (typedChars >= 10) {
            try {
                if (typingTimer != null) {
                    final Iterator<String> iter = chat.getOccupants();
                    while (iter.hasNext()) {
                        String from = iter.next();
                        String tFrom = StringUtils.parseResource(from);
                        String nickname = chat.getNickname();
                        if (tFrom != null && !tFrom.equals(nickname)) {
                            SparkManager.getMessageEventManager().sendComposingNotification(from, "djn");
                        }
                    }
                }
                typedChars = 0;
            }
            catch (Exception exception) {
                Log.error("Error updating", exception);
            }
        }
    }

    /**
     * Returns the GroupChatParticpantList which displays all users within a conference room.
     *
     * @return the GroupChatParticipantList.
     */
    public GroupChatParticipantList getConferenceRoomInfo() {
        return roomInfo;
    }

    public long getLastActivity() {
        return lastActivity;
    }


    public void connectionClosed() {
        handleDisconnect();
    }

    public void connectionClosedOnError(Exception ex) {
        handleDisconnect();

        getTranscriptWindow().showWindowDisabled();
        getSplitPane().setRightComponent(null);

        String message = Res.getString("message.disconnected.group.chat.error");

        getTranscriptWindow().insertNotificationMessage(message, ChatManager.ERROR_COLOR);
    }

    /**
     * Is called whenever Spark was unexpectadly disconnected.
     */
    private void handleDisconnect() {
        getChatInputEditor().setEnabled(false);
        getSendButton().setEnabled(false);
        SparkManager.getChatManager().getChatContainer().fireChatRoomStateUpdated(this);
    }

    /**
     * An internal UI implementation for display subjects
     * within the conference room.
     */
    public class SubjectPanel extends JPanel {

	private static final long serialVersionUID = -1459165526561181321L;
	private JLabel roomJIDLabel;
        private JLabel subjectLabel;

        public SubjectPanel() {
            setLayout(new GridBagLayout());

            //iconLabel = new JLabel(SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_24x24));
            roomJIDLabel = new JLabel("<" + getMultiUserChat().getRoom() + ">");
            subjectLabel = new JLabel(getMultiUserChat().getSubject());

            //   add(iconLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
            add(roomJIDLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0));
            add(subjectLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0));

            setOpaque(false);
        }

        public void setSubject(String subject) {
            subjectLabel.setText(subject);
        }

        public void setRoomLabel(String label) {
            roomJIDLabel.setText(label);
        }
    }

    /**
     * Returns the descriptive panel at the top of the room.
     *
     * @return the SubjectPanel.
     */
    public SubjectPanel getSubjectPanel() {
        return subjectPanel;
    }

    public Color getColor(String nickname) {
        int index = 0;
        for (int i = 0; i < nickname.length(); i++) {
            index += nickname.charAt(i) * i;
        }

        return ChatManager.COLORS[index % ChatManager.COLORS.length];
    }
}
