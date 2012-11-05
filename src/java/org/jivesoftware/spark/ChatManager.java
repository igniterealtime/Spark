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
package org.jivesoftware.spark;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.decorator.DefaultTabHandler;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.GlobalMessageListener;
import org.jivesoftware.spark.ui.MessageFilter;
import org.jivesoftware.spark.ui.SparkTabHandler;
import org.jivesoftware.spark.ui.TranscriptWindowInterceptor;
import org.jivesoftware.spark.ui.conferences.RoomInvitationListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.uri.UriManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.Color;
import java.awt.Component;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Handles the Chat Management of each individual <code>Workspace</code>. The ChatManager is responsible
 * for creation and removal of chat rooms, transcripts, and transfers and room invitations.
 */
public class ChatManager implements ChatManagerListener {

    private static ChatManager singleton;
    private static final Object LOCK = new Object();

    // Define Default Colors
    public static Color TO_COLOR = (Color)UIManager.get("User.foreground");
    public static Color FROM_COLOR = (Color)UIManager.get("OtherUser.foreground");
    public static Color NOTIFICATION_COLOR = (Color)UIManager.get("Notification.foreground");
    public static Color ERROR_COLOR = (Color)UIManager.get("Error.foreground");

    public static Color[] COLORS = {Color.red, Color.blue, Color.gray, Color.magenta, new Color(238, 153, 247), new Color(128, 128, 0), new Color(173, 205, 50),
        new Color(181, 0, 0), new Color(0, 100, 0), new Color(237, 150, 122), new Color(0, 139, 139), new Color(218, 14, 0), new Color(147, 112, 219),
        new Color(205, 133, 63), new Color(163, 142, 35), new Color(72, 160, 237), new Color(255, 140, 0), new Color(106, 90, 205), new Color(224, 165, 32),
        new Color(255, 69, 0), new Color(255, 99, 72), new Color(109, 130, 180), new Color(233, 0, 0), new Color(139, 69, 19), new Color(255, 127, 80),
        new Color(140, 105, 225)};

    private List<MessageFilter> messageFilters = new ArrayList<MessageFilter>();

    private List<GlobalMessageListener> globalMessageListeners = new ArrayList<GlobalMessageListener>();

    private List<RoomInvitationListener> invitationListeners = new ArrayList<RoomInvitationListener>();

    private List<TranscriptWindowInterceptor> interceptors = new ArrayList<TranscriptWindowInterceptor>();

    private List<SparkTabHandler> sparkTabHandlers = new CopyOnWriteArrayList<SparkTabHandler>();


    private final ChatContainer chatContainer;

    private String conferenceService;

    private List<ContactItemHandler> contactItemHandlers = new ArrayList<ContactItemHandler>();

    private Set<ChatRoom> typingNotificationList = new HashSet<ChatRoom>();

    private UriManager _uriManager = new UriManager();
    
    private List<ChatMessageHandler> chatMessageHandlers = new ArrayList<ChatMessageHandler> ();

    /**
     * The listener instance that we use to track chat states according to
     * XEP-0085;
     */
    private SmackChatStateListener smackChatStateListener = null;    

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
        chatContainer = UIComponentRegistry.createChatContainer();        

        // Add Default Chat Room Decorator
        addSparkTabHandler(new DefaultTabHandler());
        // Add a Message Handler        
        SparkManager.getConnection().getChatManager().addChatListener(this);
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
     * @throws ChatNotFoundException thrown if no ChatRoom is found.
     */
    public GroupChatRoom getGroupChat(String roomName) throws ChatNotFoundException {
        for (ChatRoom chatRoom : getChatContainer().getChatRooms()) {
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
     * @param title    the title to use for the room.
     * @return the newly created <code>ChatRoom</code>.
     */
    public ChatRoom createChatRoom(String userJID, String nickname, String title) {
        ChatRoom chatRoom;
        try {
            chatRoom = getChatContainer().getChatRoom(userJID);
        }
        catch (ChatRoomNotFoundException e) {
            chatRoom = UIComponentRegistry.createChatRoom(userJID, nickname, title);
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
        ChatRoom chatRoom;
        try {
            chatRoom = getChatContainer().getChatRoom(jid);
        }
        catch (ChatRoomNotFoundException e) {
            ContactList contactList = SparkManager.getWorkspace().getContactList();
            ContactItem item = contactList.getContactItemByJID(jid);
            if (item != null) {
                String nickname = item.getDisplayName();
                chatRoom = UIComponentRegistry.createChatRoom(jid, nickname, nickname);
            }
            else {
                chatRoom = UIComponentRegistry.createChatRoom(jid, jid, jid);
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

        final GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(chatRoom);

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
     *
     * @param jid      the jid of the user to chat with.
     * @param nickname the nickname of the user.
     */
    public void activateChat(final String jid, final String nickname) {
        if (!ModelUtil.hasLength(jid)) {
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
                    chatRoom = chatRooms.getChatRoom(jid);
                }
                catch (ChatRoomNotFoundException e) {
                    // Do nothing
                }
                return chatRoom;
            }

            public void finished() {
                if (chatRoom == null) {
                    chatRoom = UIComponentRegistry.createChatRoom(jid, nickname, nickname);
                    chatManager.getChatContainer().addChatRoom(chatRoom);
                }
                chatManager.getChatContainer().activateChatRoom(chatRoom);
            }
        };

        worker.start();

    }

    /**
     * Checks if a <code>ChatRoom</code> exists.
     *
     * @param jid the jid of the user.
     * @return true if the ChatRoom exists.
     */
    public boolean chatRoomExists(String jid) {
        try {
            getChatContainer().getChatRoom(jid);
        }
        catch (ChatRoomNotFoundException e) {
            return false;
        }
        return true;
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
    public Collection<MessageFilter> getMessageFilters() {
        return messageFilters;
    }

    /**
     * Adds a new <code>GlobalMessageListener</code>.
     *
     * @param listener the listener.
     */
    public void addGlobalMessageListener(GlobalMessageListener listener) {
        globalMessageListeners.add(listener);
    }

    /**
     * Removes a <code>GlobalMessageListener</code>.
     *
     * @param listener the listener.
     */
    public void removeGlobalMessageListener(GlobalMessageListener listener) {
        globalMessageListeners.remove(listener);
    }

    /**
     * Notifies all <code>GlobalMessageListeners</code> of a new incoming message.
     *
     * @param chatRoom the <code>ChatRoom</code> where the message was sent to.
     * @param message  the <code>Message</code>
     */
    public void fireGlobalMessageReceievedListeners(ChatRoom chatRoom, Message message) {
        for (GlobalMessageListener listener : globalMessageListeners) {
            listener.messageReceived(chatRoom, message);
        }
    }

    /**
     * Notifies all <code>GlobalMessageListeners</code> of a new message sent.
     *
     * @param chatRoom the <code>ChatRoom</code> where the message was sent from.
     * @param message  the <code>Message</code> sent.
     */
    public void fireGlobalMessageSentListeners(ChatRoom chatRoom, Message message) {
        for (GlobalMessageListener listener : globalMessageListeners) {
            listener.messageSent(chatRoom, message);
        }
    }

    /**
     * Filters all incoming messages.
     *
     * @param room    the room the message belongs to.
     * @param message the message to filter.
     */
    public void filterIncomingMessage(ChatRoom room, Message message) {
        // Fire Message Filters
        final ChatManager chatManager = SparkManager.getChatManager();
        Iterator<MessageFilter> filters = chatManager.getMessageFilters().iterator();
        try {
            cancelledNotification(message.getFrom(), ChatState.paused);
        }
        catch (Exception e) {
            Log.error(e);
        }

        // Notify MessageFilters.
        while (filters.hasNext()) {
            (filters.next()).filterIncoming(room, message);
        }
    }

    /**
     * Notifies all <code>MessageFilter</code>s about a new outgoing message.
     *
     * @param room    the <code>ChatRoom</code> the message belongs too.
     * @param message the <code>Message</code> being sent.
     */
    public void filterOutgoingMessage(ChatRoom room, Message message) {
        // Fire Message Filters
        final ChatManager chatManager = SparkManager.getChatManager();
        for (Object o : chatManager.getMessageFilters()) {
            ((MessageFilter) o).filterOutgoing(room, message);
        }
    }

    /**
     * Adds a <code>RoomInvitationListener</code>. A RoomInvitationListener is
     *
     * @param listener the listener.
     */
    public void addInvitationListener(RoomInvitationListener listener) {
        invitationListeners.add(listener);
    }

    /**
     * Removes a <code>RoomInvitationListener</code>.
     *
     * @param listener the listener to remove.
     */
    public void removeInvitationListener(RoomInvitationListener listener) {
        invitationListeners.remove(listener);
    }

    /**
     * Returns all registered <code>RoomInvitationListener</code>s.
     *
     * @return the Collection of listeners.
     */
    public Collection<RoomInvitationListener> getInvitationListeners() {
        return Collections.unmodifiableCollection(invitationListeners);
    }

    /**
     * Returns the default conference service. (ex. conference.jivesoftware.com)
     *
     * @return the default conference service to interact with MUC.
     */
    public String getDefaultConferenceService() {
        if (conferenceService == null) {
            try {
                Collection<String> col = MultiUserChat.getServiceNames(SparkManager.getConnection());
                if (col.size() > 0) {
                    conferenceService = col.iterator().next();
                }
            }
            catch (XMPPException e) {
                Log.error(e);
            }
        }

        return conferenceService;
    }

    /**
     * Adds a new <code>ContactItemHandler</code>.
     *
     * @param handler the ContactItemHandler to add.
     */
    public void addContactItemHandler(ContactItemHandler handler) {
        contactItemHandlers.add(handler);
    }
    
    public void addChatMessageHandler(ChatMessageHandler handler) {
    	chatMessageHandlers.add(handler);
    }
    
    public void removeChatMessageHandler(ChatMessageHandler handler) {
    	chatMessageHandlers.remove(handler);
    }

    /**
     * Removes a <code>ContactItemHandler</code>.
     *
     * @param handler the ContactItemHandler to remove.
     */
    public void removeContactItemHandler(ContactItemHandler handler) {
        contactItemHandlers.remove(handler);
    }
    
    public void fireMessageReceived(Message message) {
    	for (ChatMessageHandler handler : chatMessageHandlers) {
    		handler.messageReceived(message);
    	}
    }

    /**
     * Notifies all <code>ContactItemHandler</code>s of presence changes.
     *
     * @param item     the ContactItem where the presence changed.
     * @param presence the new presence.
     * @return true if it was handled.
     */
    public boolean fireContactItemPresenceChanged(ContactItem item, Presence presence) {
        for (ContactItemHandler handler : contactItemHandlers) {
            if (handler.handlePresence(item, presence)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Notifies all <code>ContactItemHandlers</code> that a <code>ContactItem</code> was double-clicked.
     *
     * @param item the ContactItem that was double clicked.
     * @return true if the event was intercepted and handled.
     */
    public boolean fireContactItemDoubleClicked(ContactItem item) {
        for (ContactItemHandler handler : contactItemHandlers) {
            if (handler.handleDoubleClick(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the icon from a <code>ContactItemHandler</code>.
     *
     * @param jid the jid.
     * @return the icon of the handler.
     */
    public Icon getIconForContactHandler(String jid) {
        for (ContactItemHandler handler : contactItemHandlers) {
            Icon icon = handler.getIcon(jid);
            if (icon != null) {
                return icon;
            }
        }

        return null;
    }

    /**
     * Returns the icon to use in the tab.
     *
     * @param presence the presence.
     * @return the icon.
     */
    public Icon getTabIconForContactHandler(Presence presence) {
        for (ContactItemHandler handler : contactItemHandlers) {
            Icon icon = handler.getTabIcon(presence);
            if (icon != null) {
                return icon;
            }
        }

        return null;
    }

    public void composingNotification(final String from) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final ContactList contactList = SparkManager.getWorkspace().getContactList();

                ChatRoom chatRoom;
                try {
                    chatRoom = getChatContainer().getChatRoom(StringUtils.parseBareAddress(from));
                    if (chatRoom != null && chatRoom instanceof ChatRoomImpl) {                    	
                        typingNotificationList.add(chatRoom);
                        // Notify Decorators
                        notifySparkTabHandlers(chatRoom);
                        ((ChatRoomImpl)chatRoom).notifyChatStateChange(ChatState.composing);
                    }
                }
                catch (ChatRoomNotFoundException e) {
                    // Do nothing
                }
                contactList.setIconFor(from, SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_EDIT_IMAGE));
            }
        });
    }

    public void cancelledNotification(final String from, final ChatState state) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ContactList contactList = SparkManager.getWorkspace().getContactList();

                ChatRoom chatRoom;
                try {
                    chatRoom = getChatContainer().getChatRoom(StringUtils.parseBareAddress(from));
                    if (chatRoom != null && chatRoom instanceof ChatRoomImpl) {
                        typingNotificationList.remove(chatRoom);
                        // Notify Decorators
                        notifySparkTabHandlers(chatRoom);
                        ((ChatRoomImpl)chatRoom).notifyChatStateChange(state);
                    }
                }
                catch (ChatRoomNotFoundException e) {
                    // Do nothing
                }
                contactList.useDefaults(from);
            }
        });
    }

    /**
     * Adds a room where the user is typing.
     *
     * @param chatRoom the room where the user is typing.
     */
    public void addTypingNotification(ChatRoom chatRoom) {
        typingNotificationList.add(chatRoom);
    }

    /**
     * Removes a room from the typing notification list.
     *
     * @param chatRoom the room to remove.
     */
    public void removeTypingNotification(ChatRoom chatRoom) {
        typingNotificationList.remove(chatRoom);
    }

    /**
     * Returns true if the <code>ChatRoom</code> state is in typing mode.
     *
     * @param chatRoom the ChatRoom to check.
     * @return true if in typing mode.
     */
    public boolean containsTypingNotification(ChatRoom chatRoom) {
        return typingNotificationList.contains(chatRoom);
    }

    /**
     * Returns true if the room is "stale". A stale room is a room that has
     * not been active for a specific amount of time.
     *
     * @param chatRoom the ChatRoom.
     * @return true if the room is stale.
     */
    public boolean isStaleRoom(ChatRoom chatRoom) {
        // Check if room is stale
        return chatContainer.getStaleChatRooms().contains(chatRoom);
    }

    /**
     * Adds a TranscriptWindowInterceptor.
     *
     * @param interceptor the interceptor.
     */
    public void addTranscriptWindowInterceptor(TranscriptWindowInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * Removes a TranscriptWindowInterceptor.
     *
     * @param interceptor the interceptor.
     */
    public void removeTranscriptWindowInterceptor(TranscriptWindowInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    /**
     * Returns the list of <code>TranscriptWindowInterceptors</code>.
     *
     * @return the list of interceptors.
     */
    public Collection<TranscriptWindowInterceptor> getTranscriptWindowInterceptors() {
        return interceptors;
    }

    /**
     * Adds a new <code>ContainerDecorator</code>. The ContainerDecorator will be added to the top of the stack and will therefore
     * take priority on notification calls. If all decorators return false, the <code>DefaultChatRoomDecorator</code> will be used.
     *
     * @param decorator the decorator to add.
     */
    public void addSparkTabHandler(SparkTabHandler decorator) {
        sparkTabHandlers.add(0, decorator);
    }

    /**
     * Removes a <code>ContainerDecorator</code>
     *
     * @param decorator the decorator to remove.
     */
    public void removeSparkTabHandler(SparkTabHandler decorator) {
        sparkTabHandlers.remove(decorator);
    }

    /**
     * Notifies all <code>ContainerDecorator</code>
     *
     * @param component the component within the tab.
     */
    public void notifySparkTabHandlers(Component component) {
        final SparkTab tab = chatContainer.getTabContainingComponent(component);
        if (tab == null) {
            return;
        }
        boolean isChatFrameInFocus = getChatContainer().getChatFrame().isInFocus();
        boolean isSelectedTab = getChatContainer().getSelectedComponent() == component;
        for (SparkTabHandler decorator : sparkTabHandlers) {
            boolean isHandled = decorator.isTabHandled(tab, component, isSelectedTab, isChatFrameInFocus);
            if (isHandled) {
                tab.validateTab();
                return;
            }
        }
    }

    /**
     * Returns all selected users in the <code>ContactList</code>.
     *
     * @return all selected <code>ContactItem</code> in the ContactList.
     */
    public Collection<ContactItem> getSelectedContactItems() {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        return contactList.getSelectedUsers();
    }

    /**
     * Handles XMPP URI Mappings.
     *
     * @param arguments
     *            the arguments passed into Spark.
     */
    public void handleURIMapping(String arguments) {
	if (arguments == null) {
	    return;
	}

	URI uri = null;
	try {
	    uri = new URI(arguments);
	} catch (URISyntaxException e) {
	    Log.error("error parsing uri: "+arguments,e);
	    return;
	}
	if (!"xmpp".equalsIgnoreCase(uri.getScheme())) {
	    return;
	}

	String query = uri.getQuery();
	if (query == null) {
	    // No query string, so assume the URI is xmpp:JID
	    String jid = _uriManager.retrieveJID(uri);

	    UserManager userManager = SparkManager.getUserManager();
	    String nickname = userManager.getUserNicknameFromJID(jid);
	    if (nickname == null) {
		nickname = jid;
	    }

	    ChatManager chatManager = SparkManager.getChatManager();
	    ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname,
		    nickname);
	    chatManager.getChatContainer().activateChatRoom(chatRoom);
	} else if (query.startsWith(UriManager.uritypes.message.getXML())) {
	    try {
		_uriManager.handleMessage(uri);
	    } catch (Exception e) {
		Log.error("error with ?message URI", e);
	    }
	} else if (query.startsWith(UriManager.uritypes.join.getXML())) {
	    try {
		_uriManager.handleConference(uri);
	    } catch (Exception e) {
		Log.error("error with ?join URI", e);
	    }
	} else if (query.startsWith(UriManager.uritypes.subscribe.getXML())) {
	    try {
		_uriManager.handleSubscribe(uri);
	    } catch (Exception e) {
		Log.error("error with ?subscribe URI", e);
	    }
	} else if (query.startsWith(UriManager.uritypes.unsubscribe.getXML())) {
	    try {
		_uriManager.handleUnsubscribe(uri);
	    } catch (Exception e) {
		Log.error("error with ?unsubscribe URI", e);
	    }
	} else if (query.startsWith(UriManager.uritypes.roster.getXML())) {
	    try {
		_uriManager.handleRoster(uri);
	    } catch (Exception e) {
		Log.error("error with ?roster URI", e);
	    }
	} else if (query.startsWith(UriManager.uritypes.remove.getXML())) {
	    try {
		_uriManager.handleRemove(uri);
	    } catch (Exception e) {
		Log.error("error with ?remove URI", e);
	    }
	}
    }

	@Override
	public void chatCreated(Chat chat, boolean isLocal) {
        if(smackChatStateListener == null) {
            smackChatStateListener = new SmackChatStateListener();
        }        
        chat.addMessageListener(smackChatStateListener);		
	}
	
    /**
     * The listener that we use to track chat state notifications according
     * to XEP-0085.
     */
    private class SmackChatStateListener implements ChatStateListener {
        /**
         * Called by smack when the state of a chat changes.
         *
         * @param chat the chat that is concerned by this event.
         * @param state the new state of the chat.
         */
    	@Override
        public void stateChanged(Chat chat, ChatState state) {
    		String participant = chat.getParticipant();
            if (ChatState.composing.equals(state)) {
                composingNotification(participant);
            } else {
            	cancelledNotification(participant, state);
            }
            
        }

		@Override
		public void processMessage(Chat arg0, Message arg1) {			
			// TODO Auto-generated method stub			
		}
    }    	
}
