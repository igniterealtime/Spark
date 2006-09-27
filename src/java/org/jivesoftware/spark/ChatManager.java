/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.MessageEventNotificationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.MessageFilter;
import org.jivesoftware.spark.ui.conferences.RoomInvitationListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Handles the Chat Management of each individual <code>Workspace</code>. The ChatManager is responsible
 * for creation and removal of chat rooms, transcripts, and transfers and room invitations.
 */
public class ChatManager implements MessageEventNotificationListener {
    private List<MessageFilter> messageFilters = new ArrayList<MessageFilter>();

    private List<RoomInvitationListener> invitationListeners = new ArrayList<RoomInvitationListener>();

    private final ChatContainer chatContainer;
    private String conferenceService;

    private List<ContactItemHandler> contactItemHandlers = new ArrayList<ContactItemHandler>();

    private Set<String> customList = new HashSet<String>();

    private static ChatManager singleton;
    private static final Object LOCK = new Object();

    /**
     * Returns the singleton instance of <CODE>ChatManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>ChatManager</CODE>
     */
    public static ChatManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                ChatManager controller = new ChatManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }


    /**
     * Create a new instance of ChatManager.
     */
    private ChatManager() {
        chatContainer = new ChatContainer();

        // Add a Message Handler

        SparkManager.getMessageEventManager().addMessageEventNotificationListener(this);

        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(final Packet packet) {
                try {
                    if (customList.contains(StringUtils.parseBareAddress(packet.getFrom()))) {
                        cancelledNotification(packet.getFrom(), "");
                    }
                }
                catch (Exception e) {
                    Log.error(e);
                }
            }
        }, new PacketTypeFilter(Message.class));
    }


    /**
     * Used to listen for rooms opening, closing or being
     * activated( already opened, but tabbed to )
     *
     * @param listener the ChatRoomListener to add
     */
    public void addChatRoomListener(ChatRoomListener listener) {
        getChatContainer().addChatRoomListener(listener);
    }

    /**
     * Simplace facade for chatroom. Removes a listener
     *
     * @param listener the ChatRoomListener to remove
     */
    public void removeChatRoomListener(ChatRoomListener listener) {
        getChatContainer().removeChatRoomListener(listener);
    }


    /**
     * Removes the personal 1 to 1 chat from the ChatFrame.
     *
     * @param chatRoom the ChatRoom to remove.
     */
    public void removeChat(ChatRoom chatRoom) {
        chatContainer.closeTab(chatRoom);
    }


    /**
     * Returns all ChatRooms currently active.
     *
     * @return all ChatRooms.
     */
    public ChatContainer getChatContainer() {
        return chatContainer;
    }

    /**
     * Returns the MultiUserChat associated with the specified roomname.
     *
     * @param roomName the name of the chat room.
     * @return the MultiUserChat found for that room.
     */
    public GroupChatRoom getGroupChat(String roomName) throws ChatNotFoundException {
        Iterator iter = getChatContainer().getAllChatRooms();
        while (iter.hasNext()) {
            ChatRoom chatRoom = (ChatRoom)iter.next();
            if (chatRoom instanceof GroupChatRoom) {
                GroupChatRoom groupChat = (GroupChatRoom)chatRoom;
                if (groupChat.getRoomname().equals(roomName)) {
                    return groupChat;
                }
            }

        }

        throw new ChatNotFoundException("Could not locate Group Chat Room - " + roomName);
    }


    /**
     * Creates and/or opens a chat room with the specified user.
     *
     * @param userJID  the jid of the user to chat with.
     * @param nickname the nickname to use for the user.
     */
    public ChatRoom createChatRoom(String userJID, String nickname, String title) {
        ChatRoom chatRoom = null;
        try {
            chatRoom = getChatContainer().getChatRoom(userJID);
        }
        catch (ChatRoomNotFoundException e) {
            chatRoom = new ChatRoomImpl(userJID, nickname, title);
            getChatContainer().addChatRoom(chatRoom);
        }

        return chatRoom;
    }

    /**
     * Returns the <code>ChatRoom</code> for the giving jid. If the ChatRoom is not found,
     * a new ChatRoom will be created.
     *
     * @param jid the jid of the user to chat with.
     * @return the ChatRoom.
     */
    public ChatRoom getChatRoom(String jid) {
        ChatRoom chatRoom = null;
        try {
            chatRoom = getChatContainer().getChatRoom(jid);
        }
        catch (ChatRoomNotFoundException e) {
            ContactList contactList = SparkManager.getWorkspace().getContactList();
            ContactItem item = contactList.getContactItemByJID(jid);
            if (item != null) {
                String nickname = item.getNickname();
                chatRoom = new ChatRoomImpl(jid, nickname, nickname);
            }
            else {
                chatRoom = new ChatRoomImpl(jid, jid, jid);
            }


            getChatContainer().addChatRoom(chatRoom);
        }

        return chatRoom;
    }

    /**
     * Creates a new public Conference Room.
     *
     * @param roomName    the name of the room.
     * @param serviceName the service name to use (ex.conference.jivesoftware.com)
     * @return the new ChatRoom created. If an error occured, null will be returned.
     */
    public ChatRoom createConferenceRoom(String roomName, String serviceName) {
        final MultiUserChat chatRoom = new MultiUserChat(SparkManager.getConnection(), roomName + "@" + serviceName);

        final GroupChatRoom room = new GroupChatRoom(chatRoom);

        try {
            LocalPreferences pref = SettingsManager.getLocalPreferences();
            chatRoom.create(pref.getNickname());

            // Send an empty room configuration form which indicates that we want
            // an instant room
            chatRoom.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
        }
        catch (XMPPException e1) {
            Log.error("Unable to send conference room chat configuration form.", e1);
            return null;
        }

        getChatContainer().addChatRoom(room);
        return room;
    }

    /**
     * Activate a chat room with the selected user.
     */
    public void activateChat(final String userJID, final String nickname) {
        if (!ModelUtil.hasLength(userJID)) {
            return;
        }

        SwingWorker worker = new SwingWorker() {
            final ChatManager chatManager = SparkManager.getChatManager();
            ChatRoom chatRoom;

            public Object construct() {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    Log.error("Error in activate chat.", e);
                }

                ChatContainer chatRooms = chatManager.getChatContainer();

                try {
                    chatRoom = chatRooms.getChatRoom(userJID);
                }
                catch (ChatRoomNotFoundException e) {

                }
                return chatRoom;
            }

            public void finished() {
                if (chatRoom == null) {
                    chatRoom = new ChatRoomImpl(userJID, nickname, nickname);
                    chatManager.getChatContainer().addChatRoom(chatRoom);
                }
                chatManager.getChatContainer().activateChatRoom(chatRoom);
            }
        };

        worker.start();

    }

    /**
     * Adds a new <code>MessageFilter</code>.
     *
     * @param filter the MessageFilter.
     */
    public void addMessageFilter(MessageFilter filter) {
        messageFilters.add(filter);
    }

    /**
     * Removes a <code>MessageFilter</code>.
     *
     * @param filter the MessageFilter.
     */
    public void removeMessageFilter(MessageFilter filter) {
        messageFilters.remove(filter);
    }

    /**
     * Returns a Collection of MessageFilters registered to Spark.
     *
     * @return the Collection of MessageFilters.
     */
    public Collection getMessageFilters() {
        return messageFilters;
    }

    public void filterIncomingMessage(ChatRoom room, Message message) {
        // Fire Message Filters
        final ChatManager chatManager = SparkManager.getChatManager();
        Iterator filters = chatManager.getMessageFilters().iterator();
        while (filters.hasNext()) {
            ((MessageFilter)filters.next()).filterIncoming(room, message);
        }
    }

    public void filterOutgoingMessage(ChatRoom room, Message message) {
        // Fire Message Filters
        final ChatManager chatManager = SparkManager.getChatManager();
        Iterator filters = chatManager.getMessageFilters().iterator();
        while (filters.hasNext()) {
            ((MessageFilter)filters.next()).filterOutgoing(room, message);
        }
    }

    public void addInvitationListener(RoomInvitationListener listener) {
        invitationListeners.add(listener);
    }

    public void removeInvitationListener(RoomInvitationListener listener) {
        invitationListeners.remove(listener);
    }

    public Collection getInvitationListeners() {
        return invitationListeners;
    }

    public String getDefaultConferenceService() {
        if (conferenceService == null) {
            try {
                Collection col = MultiUserChat.getServiceNames(SparkManager.getConnection());
                if (col.size() > 0) {
                    conferenceService = (String)col.iterator().next();
                }
            }
            catch (XMPPException e) {
                Log.error(e);
            }
        }

        return conferenceService;
    }

    public void addContactItemHandler(ContactItemHandler handler) {
        contactItemHandlers.add(handler);
    }

    public void removeContactItemHandler(ContactItemHandler handler) {
        contactItemHandlers.remove(handler);
    }

    public boolean fireContactItemPresenceChanged(ContactItem item, Presence presence) {
        for (ContactItemHandler handler : contactItemHandlers) {
            if (handler.handlePresence(item, presence)) {
                return true;
            }
        }

        return false;
    }

    public boolean fireContactItemDoubleClicked(ContactItem item) {
        for (ContactItemHandler handler : contactItemHandlers) {
            if (handler.handleDoubleClick(item)) {
                return true;
            }
        }

        return false;
    }

    public Icon getPresenceIconForContactHandler(Presence presence) {
        for (ContactItemHandler handler : contactItemHandlers) {
            Icon icon = handler.getIcon(presence);
            if (icon != null) {
                return icon;
            }
        }

        return null;
    }

    public Icon getTabIconForContactHandler(Presence presence) {
        for (ContactItemHandler handler : contactItemHandlers) {
            Icon icon = handler.getTabIcon(presence);
            if (icon != null) {
                return icon;
            }
        }

        return null;
    }

    // Implemenation of MessageEventListener

    public void deliveredNotification(String from, String packetID) {

    }

    public void displayedNotification(String from, String packetID) {
    }

    public void composingNotification(final String from, String packetID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final ContactList contactList = SparkManager.getWorkspace().getContactList();

                ChatRoom chatRoom = null;
                try {
                    chatRoom = getChatContainer().getChatRoom(StringUtils.parseBareAddress(from));
                    if (chatRoom != null && chatRoom instanceof ChatRoomImpl) {
                        ((ChatRoomImpl)chatRoom).showTyping(true);
                    }
                }
                catch (ChatRoomNotFoundException e) {
                }

                contactList.setIconFor(from, SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_EDIT_IMAGE));
                customList.add(StringUtils.parseBareAddress(from));
            }
        });
    }

    public void offlineNotification(String from, String packetID) {
    }

    public void cancelledNotification(final String from, String packetID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ContactList contactList = SparkManager.getWorkspace().getContactList();

                ChatRoom chatRoom = null;
                try {
                    chatRoom = getChatContainer().getChatRoom(StringUtils.parseBareAddress(from));
                    if (chatRoom != null && chatRoom instanceof ChatRoomImpl) {
                        ((ChatRoomImpl)chatRoom).showTyping(false);
                    }
                }
                catch (ChatRoomNotFoundException e) {
                }

                contactList.useDefaults(from);
                customList.remove(StringUtils.parseBareAddress(from));
            }
        });
    }
}