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
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Conference plugin is reponsible for the initial loading of MultiUser Chat support. To disable plugin,
 * you can remove from the plugins.xml file located in the classpath of Communicator.
 */
public class Conferences {
    private static BookmarkedConferences bookedMarkedConferences;

    private boolean mucSupported;

    public void initialize() {
        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());
        mucSupported = manager.includesFeature("http://jabber.org/protocol/muc");

        if (mucSupported) {
            // Load the conference data from Private Data
            loadBookmarks();

            // Add an invitation listener.
            addInvitationListener();

            addChatRoomListener();

            addPopupListeners();

            // Add Join Conference Button to StatusBar
            // Get command panel and add View Online/Offline, Add Contact
            StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
            JPanel commandPanel = statusBar.getCommandPanel();

            RolloverButton joinConference = new RolloverButton(SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));
            joinConference.setToolTipText(Res.getString("message.join.conference.room"));
            commandPanel.add(joinConference);
            joinConference.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ConferenceRooms rooms = new ConferenceRooms(bookedMarkedConferences.getTree(), getDefaultServiceName());
                    rooms.invoke();
                }
            });

            // Add Presence Listener to send directed presence to Group Chat Rooms.
            PresenceListener presenceListener = new PresenceListener() {
                public void presenceChanged(Presence presence) {
                    for (ChatRoom room : SparkManager.getChatManager().getChatContainer().getChatRooms()) {
                        if (room instanceof GroupChatRoom) {
                            GroupChatRoom groupChatRoom = (GroupChatRoom)room;
                            String jid = groupChatRoom.getMultiUserChat().getRoom();

                            // Send presence to room
                            String to = presence.getTo();

                            presence.setTo(jid);
                            SparkManager.getConnection().sendPacket(presence);
                            presence.setTo(to);
                        }
                    }

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
                        Collection listeners = new ArrayList(SparkManager.getChatManager().getInvitationListeners());
                        Iterator iter = listeners.iterator();
                        while (iter.hasNext()) {
                            RoomInvitationListener listener = (RoomInvitationListener)iter.next();
                            boolean handle = listener.handleInvitation(conn, room, inviter, reason, password, message);
                            if (handle) {
                                return;
                            }
                        }

                        // If no listeners handled the invitation, default to generic invite.
                        final InvitationUI inviteDialog = new InvitationUI(conn, room, inviter, reason, password, message);
                    }
                });

            }
        });
    }

    /**
     * Persists bookmarked data, if any.
     */
    public void shutdown() {
        if (!mucSupported) {
            return;
        }
    }

    /**
     * Load all bookmarked data.
     */
    private void loadBookmarks() {
        final Workspace workspace = SparkManager.getWorkspace();

        SwingWorker lazyWorker = new SwingWorker() {

            public Object construct() {
                try {
                    BookmarkManager manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());
                    return manager.getBookmarkedConferences();
                }
                catch (XMPPException e) {
                    e.printStackTrace();
                }
                return true;
            }

            public void finished() {
                bookedMarkedConferences = new BookmarkedConferences();

                workspace.getWorkspacePane().addTab(Res.getString("tab.conferences"), SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16), bookedMarkedConferences);
                bookedMarkedConferences.setBookmarks((Collection)get());
            }
        };

        lazyWorker.start();
    }

    private void addChatRoomListener() {
        ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(new ChatRoomListener() {
            public void chatRoomOpened(ChatRoom room) {
                if (room instanceof ChatRoomImpl) {
                    final ChatRoomImpl chatRoom = (ChatRoomImpl)room;

                    // Add Conference Invite Button.
                    ChatRoomButton inviteButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_24x24));
                    inviteButton.setToolTipText(Res.getString("title.invite.to.conference"));

                    room.getToolBar().addChatRoomButton(inviteButton);

                    inviteButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String userName = StringUtils.parseName(SparkManager.getSessionManager().getJID());
                            final String roomName = userName + "_" + StringUtils.randomString(3);


                            final List jids = new ArrayList();
                            jids.add(chatRoom.getParticipantJID());

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
                                        ConferenceUtils.createPrivateConference(serviceName, Res.getString("message.please.join.in.conference"), roomName, jids);
                                    }
                                };
                                worker.start();

                            }
                        }
                    });
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
        Collection services = bookedMarkedConferences.getMucServices();
        if (services != null) {
            Iterator serviceIterator = services.iterator();
            while (serviceIterator.hasNext()) {
                serviceName = (String)serviceIterator.next();
                break;
            }
        }
        return serviceName;
    }

    private void addPopupListeners() {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        // Add ContactList items.
        final Action inviteAllAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                Collection contacts = contactList.getActiveGroup().getContactItems();
                startConference(contacts);

            }
        };

        inviteAllAction.putValue(Action.NAME, Res.getString("menuitem.invite.group.to.conference"));
        inviteAllAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));


        final Action conferenceAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                Collection contacts = contactList.getSelectedUsers();
                startConference(contacts);
            }
        };

        conferenceAction.putValue(Action.NAME, Res.getString("menuitem.start.a.conference"));
        conferenceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_WORKGROUP_QUEUE_IMAGE));


        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object component, JPopupMenu popup) {
                Collection col = contactList.getSelectedUsers();
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

    private void startConference(Collection items) {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        List jids = new ArrayList();
        Iterator contacts = items.iterator();
        while (contacts.hasNext()) {
            ContactItem item = (ContactItem)contacts.next();

            ContactGroup contactGroup = contactList.getContactGroup(item.getGroupName());
            contactGroup.clearSelection();

            if (item.isAvailable()) {
                jids.add(item.getFullJID());
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
    public static BookmarkedConferences getBookmarkedConferences() {
        return bookedMarkedConferences;
    }

}
