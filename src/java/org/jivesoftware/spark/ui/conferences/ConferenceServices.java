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

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bookmark.BookmarkManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * Conference plugin is reponsible for the initial loading of MultiUser Chat support. To disable plugin,
 * you can remove from the plugins.xml file located in the classpath of Communicator.
 */
public class ConferenceServices {
    private static BookmarksUI bookmarksUI;

    public ConferenceServices() {
        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());
        boolean mucSupported = manager.includesFeature("http://jabber.org/protocol/muc");

        if (mucSupported) {
            // Add an invitation listener.
            addInvitationListener();

            addChatRoomListener();

            addPopupListeners();

            // Add Join Conference Button to StatusBar
            // Get command panel and add View Online/Offline, Add Contact
            JPanel commandPanel = SparkManager.getWorkspace().getCommandPanel();

            RolloverButton joinConference = new RolloverButton(SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
            joinConference.setToolTipText(Res.getString("message.join.conference.room"));
            commandPanel.add(joinConference);
            joinConference.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ConferenceRoomBrowser rooms = new ConferenceRoomBrowser(bookmarksUI, getDefaultServiceName());
                    rooms.invoke();
                }
            });

            // Add Presence Listener to send directed presence to Group Chat Rooms.
            PresenceListener presenceListener = new PresenceListener() {
                public void presenceChanged(final Presence presence) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            for (ChatRoom room : SparkManager.getChatManager().getChatContainer().getChatRooms()) {
                                if (room instanceof GroupChatRoom) {
                                    final Presence p = new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());

                                    GroupChatRoom groupChatRoom = (GroupChatRoom)room;
                                    String jid = groupChatRoom.getMultiUserChat().getRoom();

                                    p.setTo(jid);
                                    SparkManager.getConnection().sendPacket(p);
                                }
                            }
                        }
                    });
                }
            };

            SparkManager.getSessionManager().addPresenceListener(presenceListener);
        }
    }

    /**
     * Adds an invitation listener to check for any MUC invites.
     */
    private static void addInvitationListener() {
        // Add Invite Listener
        MultiUserChat.addInvitationListener(SparkManager.getConnection(), new InvitationListener() {
            public void invitationReceived(final XMPPConnection conn, final String room, final String inviter, final String reason, final String password, final Message message) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Collection<RoomInvitationListener> listeners = new ArrayList<RoomInvitationListener>(SparkManager.getChatManager().getInvitationListeners());
                        for (RoomInvitationListener listener : listeners) {
                            boolean handle = listener.handleInvitation(conn, room, inviter, reason, password, message);
                            if (handle) {
                                return;
                            }
                        }

                        // Make sure the user is not already in the room.
                        try {
                            SparkManager.getChatManager().getChatContainer().getChatRoom(room);
                            return;
                        }
                        catch (ChatRoomNotFoundException e) {
                            // Ignore :)
                        }

                        final GroupChatInvitationUI invitationUI = new GroupChatInvitationUI(room, inviter, password, reason);

                        String bareJID = StringUtils.parseBareAddress(inviter);
                        try {
                            ChatRoom chatRoom = SparkManager.getChatManager().getChatContainer().getChatRoom(bareJID);

                            // If the ChatRoom exists, add an invitation UI.
                            chatRoom.getTranscriptWindow().addComponent(invitationUI);

                            // Notify user of incoming invitation.
                            chatRoom.increaseUnreadMessageCount();

                            chatRoom.scrollToBottom();

                            SparkManager.getChatManager().getChatContainer().fireNotifyOnMessage(chatRoom, false, null, null);
                        }
                        catch (ChatRoomNotFoundException e) {
                            // If it doesn't exists. Create a new Group Chat Room
                            // Create the Group Chat Room
                            final MultiUserChat chat = new MultiUserChat(SparkManager.getConnection(), room);

                            GroupChatRoom groupChatRoom = new GroupChatRoom(chat);
                            groupChatRoom.getSplitPane().setDividerSize(5);
                            groupChatRoom.getVerticalSlipPane().setDividerLocation(0.6);
                            groupChatRoom.getSplitPane().setDividerLocation(0.6);

                            String roomName = StringUtils.parseName(room);
                            groupChatRoom.setTabTitle(roomName);
                            groupChatRoom.getToolBar().setVisible(true);

                            SparkManager.getChatManager().getChatContainer().addChatRoom(groupChatRoom);
                            groupChatRoom.getTranscriptWindow().addComponent(invitationUI);

                            // Notify user of incoming invitation.
                            groupChatRoom.increaseUnreadMessageCount();

                            groupChatRoom.scrollToBottom();

                            SparkManager.getChatManager().getChatContainer().fireNotifyOnMessage(groupChatRoom, false, null, null);
                        }
                        // If no listeners handled the invitation, default to generic invite.
                        //new ConversationInvitation(conn, room, inviter, reason, password, message);
                    }
                });

            }
        });
    }

    /**
     * Persists bookmarked data, if any.
     */
    public void shutdown() {
    }

    /**
     * Load all bookmarked data.
     */
    public void loadConferenceBookmarks() {
        final Workspace workspace = SparkManager.getWorkspace();

        final SwingWorker bookmarkLoader = new SwingWorker() {

            public Object construct() {
                try {
                    BookmarkManager manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());
                    return manager.getBookmarkedConferences();
                }
                catch (XMPPException e) {
                    Log.error(e);
                }
                return true;
            }

            public void finished() {
                bookmarksUI = new BookmarksUI();
                workspace.getWorkspacePane().addTab(Res.getString("tab.conferences"), SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16), bookmarksUI);
            }
        };

        bookmarkLoader.start();
    }

    private void addChatRoomListener() {
        ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(new ChatRoomListener() {
            public void chatRoomOpened(final ChatRoom room) {
                if (room instanceof ChatRoomImpl) {
                    final ChatRoomDecorator decorator = new ChatRoomDecorator(room);
                    decorator.decorate();
                }
            }

            public void chatRoomLeft(ChatRoom room) {

            }

            public void chatRoomClosed(ChatRoom room) {

            }

            public void chatRoomActivated(ChatRoom room) {

            }

            public void userHasJoined(ChatRoom room, String userid) {

            }

            public void userHasLeft(ChatRoom room, String userid) {

            }
        });
    }


    public boolean canShutDown() {
        return true;
    }

    public static String getDefaultServiceName() {
        String serviceName = null;
        Collection<String> services = bookmarksUI.getMucServices();
        if (services != null) {
            for (Object service : services) {
                serviceName = (String) service;
                break;
            }
        }
        return serviceName;
    }

    private void addPopupListeners() {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        // Add ContactList items.
        final Action inviteAllAction = new AbstractAction() {
	    private static final long serialVersionUID = -7486282521151183678L;

	    public void actionPerformed(ActionEvent actionEvent) {
                Collection<ContactItem> contacts = contactList.getActiveGroup().getContactItems();
                startConference(contacts);

            }
        };

        inviteAllAction.putValue(Action.NAME, Res.getString("menuitem.invite.group.to.conference"));
        inviteAllAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));


        final Action conferenceAction = new AbstractAction() {
	    private static final long serialVersionUID = 4724119680969496581L;

	    public void actionPerformed(ActionEvent actionEvent) {
                Collection<ContactItem> contacts = contactList.getSelectedUsers();
                startConference(contacts);
            }
        };

        conferenceAction.putValue(Action.NAME, Res.getString("menuitem.start.a.conference"));
        conferenceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_WORKGROUP_QUEUE_IMAGE));


        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object component, JPopupMenu popup) {
                Collection<ContactItem> col = contactList.getSelectedUsers();
                if (component instanceof ContactGroup) {
                    popup.add(inviteAllAction);
                }
                else if (component instanceof Collection && col.size() > 0) {
                    popup.add(conferenceAction);
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        // Add to Actions Menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        actionsMenu.add(conferenceAction);
    }

    private void startConference(Collection<ContactItem> items) {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        List<String> jids = new ArrayList<String>();
        for (ContactItem item : items) {
            ContactGroup contactGroup = contactList.getContactGroup(item.getGroupName());
            contactGroup.clearSelection();

            if (item.isAvailable()) {
                jids.add(item.getJID());
            }
        }

        String userName = StringUtils.parseName(SparkManager.getSessionManager().getJID());
        final String roomName = userName + "_" + StringUtils.randomString(3);

        String serviceName = getDefaultServiceName();
        if (ModelUtil.hasLength(serviceName)) {
            ConferenceUtils.inviteUsersToRoom(serviceName, roomName, jids);
        }
    }

    /**
     * Returns the UI for the addition and removal of Conference bookmarks.
     *
     * @return the BookedMarkedConferences UI.
     */
    public static BookmarksUI getBookmarkedConferences() {
        return bookmarksUI;
    }

    private class ChatRoomDecorator implements ActionListener, ChatRoomClosingListener {
        private ChatRoom chatRoom;
        private ChatRoomButton inviteButton;

        public ChatRoomDecorator(ChatRoom room) {
            this.chatRoom = room;
            chatRoom.addClosingListener(this);
        }

        public void decorate() {

            // Add Conference Invite Button.
            inviteButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_24x24));
            inviteButton.setToolTipText(Res.getString("title.invite.to.conference"));

            chatRoom.getToolBar().addChatRoomButton(inviteButton);

            inviteButton.addActionListener(this);
        }


        public void closing() {
            inviteButton.removeActionListener(this);
            chatRoom.removeClosingListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            String userName = StringUtils.parseName(SparkManager.getSessionManager().getJID());
            final String roomName = userName + "_" + StringUtils.randomString(3);


            final List<String> jids = new ArrayList<String>();
            jids.add(((ChatRoomImpl)chatRoom).getParticipantJID());

            final String serviceName = getDefaultServiceName();
            if (serviceName != null) {
                SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        try {
                            Thread.sleep(25);
                        }
                        catch (InterruptedException e1) {
                            Log.error(e1);
                        }
                        return "ok";
                    }

                    public void finished() {
                        try {
                            ConferenceUtils.createPrivateConference(serviceName, Res.getString("message.please.join.in.conference"), roomName, jids);
                        }
                        catch (XMPPException e1) {
                            JOptionPane.showMessageDialog(chatRoom, ConferenceUtils.getReason(e1), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.start();
            }
        }
    }

}
