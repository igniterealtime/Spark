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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bookmark.BookmarkManager;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.ChatFrame;
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
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * Conference plugin is reponsible for the initial loading of MultiUser Chat support. To disable plugin,
 * you can remove from the plugins.xml file located in the classpath of Communicator.
 */
public class ConferenceServices implements InvitationListener {
    private static BookmarksUI bookmarksUI = new BookmarksUI(); //This variable shouldn't be null.

    private static LocalPreferences _localPreferences = SettingsManager.getLocalPreferences();
    public ConferenceServices() {
        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());
        boolean mucSupported = manager.includesFeature("http://jabber.org/protocol/muc");

        if (mucSupported) {
            // Add an invitation listener.
            addInvitationListener();

            addChatRoomListener();

            addPopupListeners();

            // Add Join Conference Button to ActionMenu

              final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
            JMenuItem actionMenuItem = new JMenuItem(Res.getString("message.join.conference.room"), SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
            actionsMenu.add(actionMenuItem,1);
            actionMenuItem.addActionListener(new ActionListener() {
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
    private void addInvitationListener() {
	MultiUserChat.addInvitationListener(SparkManager.getConnection(),this);
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
    	final TimerTask bookmarkLoader = new TimerTask(){

			@Override
			public void run() {
				Collection<BookmarkedConference> bc = null;

				while (bc == null) {
					try {
						BookmarkManager manager = BookmarkManager
								.getBookmarkManager(SparkManager.getConnection());
						bc = manager.getBookmarkedConferences();
					} catch (XMPPException error) {
						Log.error(error);
					}
				}
              bookmarksUI.loadUI();
              addBookmarksUI();
			}
    		
    	};
    	TaskEngine.getInstance().schedule(bookmarkLoader, 500);
    }

    protected void addBookmarksUI() {
        final Workspace workspace = SparkManager.getWorkspace();
        final boolean useTab = _localPreferences.isShowConferenceTab();

        if (useTab) {
            workspace.getWorkspacePane().addTab(Res.getString("tab.conferences"),
                    SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16), bookmarksUI);
        }
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
                else if (component instanceof Collection<?> && col.size() > 0) {
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
            ConferenceUtils.inviteUsersToRoom(serviceName, roomName, jids, true);
        }
    }

    protected BookmarkedConference getDefaultBookmark() {
        BookmarkedConference bookmarkedConference = null;
        try {
            Collection<BookmarkedConference> bookmarkedConfs = ConferenceUtils.retrieveBookmarkedConferences();
            String implicitBookmarkedJID = SettingsManager.getLocalPreferences().getDefaultBookmarkedConf();
            if (bookmarkedConfs != null && !bookmarkedConfs.isEmpty()) {

                // check if the "default" bookmarked conference is still in the bookmarks list:
                if (implicitBookmarkedJID != null && implicitBookmarkedJID.trim().length() > 0) {
                    for (BookmarkedConference bc : bookmarkedConfs) {
                        if (implicitBookmarkedJID.equalsIgnoreCase(bc.getJid())) {
                            bookmarkedConference = bc;
                            break;
                        }
                    }
                }
                // if no match was found, or no "default" bookmark could be retrieved-use the
                // first bookmark:
                if (bookmarkedConference == null) {
                    bookmarkedConference = bookmarkedConfs.iterator().next();
                }
            }
            return bookmarkedConference;
        } catch (XMPPException ex) {
            Log.warning("No default bookmark");
            // no bookmark can be retrieved;
        }
        return null;

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
            inviteButton = UIComponentRegistry.getButtonFactory().createInviteConferenceButton();
            inviteButton.setToolTipText(Res.getString("title.invite.to.conference"));

            chatRoom.addChatRoomButton(inviteButton);

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
                            BookmarkedConference selectedBookmarkedConf = _localPreferences.isUseAdHocRoom() ? null : getDefaultBookmark();
                            if (selectedBookmarkedConf == null) {
                                ConferenceUtils.createPrivateConference(serviceName,
                                        Res.getString("message.please.join.in.conference"), roomName, jids);
                            } else {
                                ConferenceUtils.joinConferenceOnSeperateThread(selectedBookmarkedConf.getName(),
                                        selectedBookmarkedConf.getJid(), selectedBookmarkedConf.getPassword(),
                                        Res.getString("message.please.join.in.conference"), jids);
                            }
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

    @Override
    public void invitationReceived(final Connection conn, final String room, final String inviter, final String reason,
	    final String password, final Message message) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		Collection<RoomInvitationListener> listeners = new ArrayList<RoomInvitationListener>(SparkManager
			.getChatManager().getInvitationListeners());
		for (RoomInvitationListener listener : listeners) {
		    boolean handle = listener.handleInvitation(conn, room, inviter, reason, password, message);
		    if (handle) {
			return;
		    }
		}

		// Make sure the user is not already in the
		// room.
		try {
		    SparkManager.getChatManager().getChatContainer().getChatRoom(room);
		    return;
		} catch (ChatRoomNotFoundException e) {
		    // Ignore :)
		}

		final GroupChatInvitationUI invitationUI = new GroupChatInvitationUI(room, inviter, password, reason);
		String message = Res.getString("message.invite.to.groupchat", inviter);
		String title = Res.getString("title.group.chat");
		String bareJID = StringUtils.parseBareAddress(inviter);

		if (_localPreferences.isAutoAcceptMucInvite()) {
		    ConferenceUtils.enterRoomOnSameThread(StringUtils.parseName(room), room, password);
		    GroupChatRoom chat = UIComponentRegistry.createGroupChatRoom(new MultiUserChat(SparkManager.getConnection(), room));

		    showToaster(message, title, chat);
		    return;
		    // Nothing to do here, we want to join the
		    // room, and stuff
		}
		try {
		    ChatRoom chatRoom = SparkManager.getChatManager().getChatContainer().getChatRoom(bareJID);

		    // If the ChatRoom exists, add an invitationUI.
		    chatRoom.getTranscriptWindow().addComponent(invitationUI);

		    // Notify user of incoming invitation.
		    chatRoom.increaseUnreadMessageCount();

		    chatRoom.scrollToBottom();

		    SparkManager.getChatManager().getChatContainer()
			    .fireNotifyOnMessage(chatRoom, true, message, title);
		} catch (ChatRoomNotFoundException e) {
		    // If it doesn't exists. Create a new Group
		    // Chat Room
		    // Create the Group Chat Room
		    final MultiUserChat chat = new MultiUserChat(SparkManager.getConnection(), room);

		    GroupChatRoom groupChatRoom = UIComponentRegistry.createGroupChatRoom(chat);

		    showToaster(message, title, groupChatRoom);

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
		    SparkManager.getChatManager().getChatContainer().fireNotifyOnMessage(groupChatRoom, true, message,
			    title);

		}
		// If no listeners handled the invitation,
		// default to generic invite.
		// new ConversationInvitation(conn, room,
		// inviter, reason, password, message);
	    }

	});

    }

    private void showToaster(String message, String title, GroupChatRoom groupChatRoom) {
	if (_localPreferences.getShowToasterPopup()) {
	    SparkToaster toaster = new SparkToaster();

	    toaster.setCustomAction(new AbstractAction() {
		private static final long serialVersionUID = -4546475740161533555L;

		@Override
		public void actionPerformed(ActionEvent e) {
		    ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
		    chatFrame.setState(Frame.NORMAL);
		    chatFrame.setVisible(true);

		}
	    });
	    toaster.setDisplayTime(5000);
	    toaster.setBorder(BorderFactory.createBevelBorder(0));
	    toaster.setToasterHeight(150);
	    toaster.setToasterWidth(200);
	    toaster.setTitle(title);
	    toaster.showToaster(groupChatRoom.getTabIcon(), message);
	}
    }

}
