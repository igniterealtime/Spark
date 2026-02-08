/**
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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatConstants;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

/**
 * Conference plugin is responsible for the initial loading of Multi User Chat support.
 */
public class ConferenceServices implements InvitationListener {
    private static final BookmarksUI bookmarksUI = new BookmarksUI();
    private final LocalPreferences pref = SettingsManager.getLocalPreferences();

    public ConferenceServices() {
        addInvitationListener();
        addChatRoomListener();
        addPopupListeners();

        // Add Join Conference Button to ActionMenu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        JMenuItem actionMenuItem = new JMenuItem(Res.getString("message.join.conference.room"), SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
        actionsMenu.add(actionMenuItem, 1);
        actionMenuItem.addActionListener(e -> {
            ConferenceRoomBrowser rooms = new ConferenceRoomBrowser(bookmarksUI, getDefaultServiceName());
            rooms.invoke();
        });

        // Add Presence Listener to send directed presence to Group Chat Rooms.
        PresenceListener presenceListener = presence -> SwingUtilities.invokeLater(() -> {
            for (ChatRoom room : SparkManager.getChatManager().getChatContainer().getChatRooms()) {
                if (room instanceof GroupChatRoom) {
                    GroupChatRoom groupChatRoom = (GroupChatRoom) room;
                    EntityBareJid jid = groupChatRoom.getMultiUserChat().getRoom();
                    int priority = presence.getPriority();
                    //Sometimes priority is not set in the presence packet received. Make sure priority is in valid range
                    priority = (priority < -128 || priority > 128) ? 1 : priority;
                    final Presence p = StanzaBuilder.buildPresence()
                        .ofType(presence.getType())
                        .setStatus(presence.getStatus())
                        .setPriority(priority)
                        .setMode(presence.getMode())
                        .to(jid)
                        .build();
                    try {
                        SparkManager.getConnection().sendStanza(p);
                    } catch (SmackException.NotConnectedException | InterruptedException e) {
                        Log.warning("Unable to send stanza to " + p.getTo(), e);
                    }
                }
            }
        });
        SparkManager.getSessionManager().addPresenceListener(presenceListener);
    }

    /**
     * Adds an invitation listener to check for any MUC invites.
     */
    private void addInvitationListener() {
        SparkManager.getMucManager().addInvitationListener(this);
    }

    /**
     * Persists bookmarked data, if any.
     */
    @SuppressWarnings("EmptyMethod")
    public void shutdown() {
    }

    /**
     * Load all bookmarked data.
     */
    public void loadConferenceBookmarks() {
        final TimerTask bookmarkLoader = new TimerTask() {
            @Override
            public void run() {
                try {
                    bookmarksUI.loadUI();
                    bookmarksUI.loadBookmarks();
                    addBookmarksUI();
                } catch (Exception e) {
                    Log.error(e);
                }
            }
        };
        TaskEngine.getInstance().schedule(bookmarkLoader, 500);
    }

    /**
     * Add Bookmarks UI Tab "Conferences" to the workspace
     */
    protected void addBookmarksUI() {
        EventQueue.invokeLater(() -> {
            final Workspace workspace = SparkManager.getWorkspace();
            final boolean useTab = pref.isShowConferenceTab();
            if (useTab) {
                String title = Res.getString("tab.conferences");
                ImageIcon icon = SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16);
                workspace.getWorkspacePane().addTab(title, icon, bookmarksUI);
            }
        });
    }

    private void addChatRoomListener() {
        ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(new ChatRoomListener() {
            @Override
            public void chatRoomOpened(final ChatRoom room) {
                if (room instanceof ChatRoomImpl) {
                    final ChatRoomDecorator decorator = new ChatRoomDecorator(room);
                    decorator.decorate();
                }
            }

            @Override
            public void chatRoomLeft(ChatRoom room) {
            }

            @Override
            public void chatRoomClosed(ChatRoom room) {
            }

            @Override
            public void chatRoomActivated(ChatRoom room) {
            }

            @Override
            public void userHasJoined(ChatRoom room, String userid) {
            }

            @Override
            public void userHasLeft(ChatRoom room, String userid) {
            }
        });
    }


    public boolean canShutDown() {
        return true;
    }

    public static DomainBareJid getDefaultServiceName() {
        return bookmarksUI.getMucServices() != null ? bookmarksUI.getMucServices().get(0) : null;
    }

    private void addPopupListeners() {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        // Add ContactList items.
        final Action inviteAllAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Collection<ContactItem> contacts = contactList.getActiveGroup().getContactItems();
                startConference(contacts);
            }
        };

        inviteAllAction.putValue(Action.NAME, Res.getString("menuitem.invite.group.to.conference"));
        inviteAllAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
        final Action conferenceAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Collection<ContactItem> contacts = contactList.getSelectedUsers();
                startConference(contacts);
            }
        };

        conferenceAction.putValue(Action.NAME, Res.getString("menuitem.start.a.conference"));
        conferenceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_WORKGROUP_QUEUE_IMAGE));


        contactList.addContextMenuListener(new ContextMenuListener() {
            @Override
            public void poppingUp(Object component, JPopupMenu popup) {
                Collection<ContactItem> col = contactList.getSelectedUsers();
                if (component instanceof ContactGroup) {
                    popup.add(inviteAllAction);
                } else if (component instanceof Collection<?> && !col.isEmpty()) {
                    popup.add(conferenceAction);
                }
            }

            @Override
            public void poppingDown(JPopupMenu popup) {
            }

            @Override
            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        // Add to Actions Menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        actionsMenu.add(conferenceAction);
    }

    private void startConference(Collection<ContactItem> items) {
        DomainBareJid serviceName = getDefaultServiceName();
        if (serviceName == null) {
            return;
        }
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        List<Jid> jids = new ArrayList<>(items.size());
        for (ContactItem item : items) {
            ContactGroup contactGroup = contactList.getContactGroup(item.getGroupName());
            contactGroup.clearSelection();
            if (item.isAvailable()) {
                jids.add(item.getJid());
            }
        }
        String userName = SparkManager.getSessionManager().getJID().getLocalpart().toString();
        final Localpart roomName = Localpart.fromUnescapedOrThrowUnchecked(userName + "_" + StringUtils.randomString(3));
        EntityBareJid room = JidCreate.entityBareFrom(roomName, serviceName);
        ConferenceUtils.inviteUsersToRoom(serviceName, room, jids, true);
    }

    protected BookmarkedConference getDefaultBookmark() {
        BookmarkedConference bookmarkedConference = null;
        try {
            List<BookmarkedConference> bookmarkedConfs = ConferenceUtils.retrieveBookmarkedConferences();
            EntityBareJid implicitBookmarkedJID = pref.getDefaultBookmarkedConf();
            if (bookmarkedConfs != null && !bookmarkedConfs.isEmpty()) {
                // check if the "default" bookmarked conference is still in the bookmarks list:
                if (implicitBookmarkedJID != null) {
                    for (BookmarkedConference bc : bookmarkedConfs) {
                        if (implicitBookmarkedJID.equals(bc.getJid())) {
                            bookmarkedConference = bc;
                            break;
                        }
                    }
                }
                // If no match was found, or no "default" bookmark could be retrieved-use the first bookmark:
                if (bookmarkedConference == null) {
                    bookmarkedConference = bookmarkedConfs.iterator().next();
                }
            }
            return bookmarkedConference;
        } catch (XMPPException | SmackException | InterruptedException ex) {
            Log.warning("No default bookmark can be retrieved");
        }
        return null;
    }

    /**
     * Returns the UI for the addition and removal of Conference bookmarks.
     */
    public static BookmarksUI getBookmarkedConferences() {
        return bookmarksUI;
    }

    private class ChatRoomDecorator implements ActionListener, ChatRoomClosingListener {
        private final ChatRoom chatRoom;
        private ChatRoomButton inviteButton;

        public ChatRoomDecorator(ChatRoom room) {
            this.chatRoom = room;
            chatRoom.addClosingListener(this);
        }

        public void decorate() {
            // Add Conference Invite Button.
            inviteButton = UIComponentRegistry.getButtonFactory().createInviteConferenceButton();
            inviteButton.setToolTipText(Res.getString("title.invite.to.conference"));
            chatRoom.addChatRoomButton(inviteButton);
            inviteButton.addActionListener(this);
        }

        @Override
        public void closing() {
            inviteButton.removeActionListener(this);
            chatRoom.removeClosingListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Localpart userName = SparkManager.getSessionManager().getJID().getLocalpart();
            final String roomName = userName + "_" + StringUtils.randomString(3);
            final List<EntityBareJid> jids = new ArrayList<>();
            jids.add(((ChatRoomImpl) chatRoom).getParticipantJID());
            final DomainBareJid serviceName = getDefaultServiceName();
            if (serviceName == null) {
                return;
            }
            // Creates private conference in background thread
            SwingWorker worker = new SwingWorker() {
                @Override
                public Object construct() {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e1) {
                        Log.error(e1);
                    }
                    return "ok";
                }

                @Override
                public void finished() {
                    try {
                        BookmarkedConference selectedBookmarkedConf = pref.isUseAdHocRoom() ? null : getDefaultBookmark();
                        if (selectedBookmarkedConf == null) {
                            ConferenceUtils.createPrivateConference(serviceName,
                                Res.getString("message.please.join.in.conference"), roomName, jids);
                        } else {
                            ConferenceUtils.joinConferenceOnSeparateThread(selectedBookmarkedConf.getName(),
                                selectedBookmarkedConf.getJid(), selectedBookmarkedConf.getNickname(), selectedBookmarkedConf.getPassword(),
                                Res.getString("message.please.join.in.conference"), jids);
                        }
                    } catch (SmackException | InterruptedException ex) {
                        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                        JOptionPane.showMessageDialog(chatRoom, "An error occurred.", Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.start();
        }
    }

    @Override
    public void invitationReceived(final XMPPConnection conn, final MultiUserChat room, final EntityJid inviterEntity, final String reason,
                                   final String password, final Message message, MUCUser.Invite invitation) {
        EntityBareJid inviter = inviterEntity.asEntityBareJid();
        SwingUtilities.invokeLater(() -> {
            for (RoomInvitationListener listener : SparkManager.getChatManager().getInvitationListeners()) {
                boolean handle = listener.handleInvitation(conn, room, inviter, reason, password, message);
                if (handle) {
                    return;
                }
            }
            // Make sure the user is not already in the room.
            ChatContainer chatContainer = SparkManager.getChatManager().getChatContainer();
            try {
                chatContainer.getChatRoom(room.getRoom());
                return;
            } catch (ChatRoomNotFoundException ignored) {
            }

            final GroupChatInvitationUI invitationUI = new GroupChatInvitationUI(room.getRoom(), inviter, password, reason);
            String message1 = Res.getString("message.invite.to.groupchat", inviter);
            String title = Res.getString("title.group.chat");
            EntityBareJid bareJID = inviter.asEntityBareJid();

            if (pref.isAutoAcceptMucInvite()) {
                ConferenceUtils.enterRoomOnSameThread(room.getRoom().getLocalpart().toString(), room.getRoom(), password);
                MultiUserChatManager mucManager = SparkManager.getMucManager();
                GroupChatRoom chat = UIComponentRegistry.createGroupChatRoom(mucManager.getMultiUserChat(room.getRoom()));
                showToaster(message1, title, chat);
                return;
                // Nothing to do here, we want to join the room, and stuff
            }
            try {
                ChatRoom chatRoom = chatContainer.getChatRoom(bareJID);
                // If the ChatRoom exists, add an invitationUI.
                chatRoom.getTranscriptWindow().addComponent(invitationUI);
                // Notify user of incoming invitation.
                chatRoom.increaseUnreadMessageCount();
                chatRoom.scrollToBottom();
                chatContainer.fireNotifyOnMessage(chatRoom, true, message1, title);
            } catch (ChatRoomNotFoundException e) {
                // If it doesn't exist, create a new Group Chat Room
                final MultiUserChatManager mucManager = SparkManager.getMucManager();
                final GroupChatRoom groupChatRoom = UIComponentRegistry.createGroupChatRoom(mucManager.getMultiUserChat(room.getRoom()));
                showToaster(message1, title, groupChatRoom);

                groupChatRoom.getSplitPane().setDividerSize(5);
                groupChatRoom.getVerticalSlipPane().setDividerLocation(0.6);
                groupChatRoom.getSplitPane().setDividerLocation(0.6);
                Localpart roomName = room.getRoom().getLocalpart();
                groupChatRoom.setTabTitle(roomName);
                groupChatRoom.getToolBar().setVisible(true);
                chatContainer.addChatRoom(groupChatRoom);
                groupChatRoom.getTranscriptWindow().addComponent(invitationUI);
                // Notify user of incoming invitation.
                groupChatRoom.increaseUnreadMessageCount();
                groupChatRoom.scrollToBottom();
                chatContainer.fireNotifyOnMessage(groupChatRoom, true, message1, title);
            }
            // If no listeners handled the invitation default to generic invite.
            // new ConversationInvitation(conn, room, inviter, reason, password, message);
        });
    }

    private void showToaster(String message, String title, GroupChatRoom groupChatRoom) {
        if (!pref.getShowToasterPopup()) {
            return;
        }
        SparkToaster toaster = new SparkToaster();
        toaster.setCustomAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
                chatFrame.setState(Frame.NORMAL);
                chatFrame.setVisible(true);
            }
        });
        toaster.setDisplayTime(5000);
        toaster.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
        toaster.setToasterHeight(150);
        toaster.setToasterWidth(200);
        toaster.setTitle(title);
        toaster.showToaster(groupChatRoom.getTabIcon(), message);
    }

}
