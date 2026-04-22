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
package org.jivesoftware.spark;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.decorator.DefaultTabHandler;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.ui.conferences.RoomInvitationListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles the Chat Management of each individual <code>Workspace</code>.
 * The ChatManager is responsible for the creation and removal of chat rooms, transcripts, transfers, and room invitations.
 */
public class ChatManager {
    /**
     * User can open chat with dummy@dummy.example and also when hovering the cursor, a window with contact information is displayed.
     */
    public static final BareJid TESTING_JID = JidCreate.bareFromOrThrowUnchecked("dummy@dummy.example");
    private static ChatManager singleton;
    private static final Object LOCK = new Object();

    // Define Default Colors
    public static final Color TO_COLOR = (Color)UIManager.get("User.foreground");
    public static final Color FROM_COLOR = (Color)UIManager.get("OtherUser.foreground");
    public static final Color NOTIFICATION_COLOR = (Color)UIManager.get("Notification.foreground");
    public static final Color ERROR_COLOR = (Color)UIManager.get("Error.foreground");

    private final CopyOnWriteArrayList<MessageFilter> messageFilters = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<GlobalMessageListener> globalMessageListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<RoomInvitationListener> invitationListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<TranscriptWindowInterceptor> interceptors = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SparkTabHandler> sparkTabHandlers = new CopyOnWriteArrayList<>();

    private final ChatContainer chatContainer;
    private String conferenceService;
    private final CopyOnWriteArrayList<ContactItemHandler> contactItemHandlers = new CopyOnWriteArrayList<>();
    private final Set<ChatRoom> typingNotificationList = new HashSet<>();
    private final CopyOnWriteArrayList<ChatMessageHandler> chatMessageHandlers = new CopyOnWriteArrayList<>();

    public static ChatManager getInstance() {
        synchronized (LOCK) {
            if (null == singleton) {
                ChatManager controller = new ChatManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private ChatManager() {
        chatContainer = UIComponentRegistry.createChatContainer();        
        // Add Default Chat Room Decorator
        addSparkTabHandler(new DefaultTabHandler());
        // Add a Message Handler
        ChatStateManager chatStateManager = ChatStateManager.getInstance(SparkManager.getConnection());
        chatStateManager.addChatStateListener(new SmackChatStateListener());
    }

    /**
     * Used to listen for rooms opening, closing or being activated (already opened, but tabbed to)
     */
    public void addChatRoomListener(ChatRoomListener listener) {
        getChatContainer().addChatRoomListener(listener);
    }

    public void removeChatRoomListener(ChatRoomListener listener) {
        getChatContainer().removeChatRoomListener(listener);
    }

    /**
     * Removes the personal 1 to 1 chat from the ChatFrame.
     */
    public void removeChat(ChatRoom chatRoom) {
        chatContainer.closeTab(chatRoom);
    }


    /**
     * Returns all ChatRooms currently active.
     */
    public ChatContainer getChatContainer() {
        return chatContainer;
    }

	public GroupChatRoom getGroupChat(EntityBareJid roomAddress) throws ChatNotFoundException {
        if ( roomAddress == null ) {
            throw new ChatNotFoundException();
        }
		return getGroupChat(roomAddress.toString());
	}

    /**
     * Returns the MultiUserChat associated with the specified roomName.
     *
     * @param roomName the name of the chat room.
     * @return the MultiUserChat found for the roomName.
     * @throws ChatNotFoundException thrown if no ChatRoom is found.
     */
    public GroupChatRoom getGroupChat(String roomName) throws ChatNotFoundException {
        for (ChatRoom chatRoom : getChatContainer().getChatRooms()) {
            if (chatRoom instanceof GroupChatRoom) {
                GroupChatRoom groupChat = (GroupChatRoom)chatRoom;
                if (groupChat.getBareJid().equals(roomName)) {
                    return groupChat;
                }
            }
        }
        throw new ChatNotFoundException("Could not locate Group Chat Room - " + roomName);
    }

    /**
     * Creates and/or opens a chat room with the specified user.
     *
     * @param jid  the jid of the user to chat with.
     * @param nicknameCs the nickname to use for the user.
     * @param title    the title to use for the room.
     * @return the newly created <code>ChatRoom</code>.
     */
    public ChatRoom createChatRoom(EntityJid jid, CharSequence nicknameCs, CharSequence title) {
        Resourcepart nickname = Resourcepart.fromOrThrowUnchecked(nicknameCs);
        ChatRoom chatRoom;
        try {
            chatRoom = getChatContainer().getChatRoom(jid);
        }
        catch (ChatRoomNotFoundException e) {
            chatRoom = UIComponentRegistry.createChatRoom(jid, nickname, title);
            getChatContainer().addChatRoom(chatRoom);
        }
        return chatRoom;
    }

    /**
     * Returns the <code>ChatRoom</code> for the giving jid. If the ChatRoom is not found,
     * a new ChatRoom will be created.
     *
     * @param bareJid the jid of the user to chat with.
     */
    public ChatRoom getChatRoom(EntityBareJid bareJid) {
        EntityBareJid jid = bareJid.asEntityBareJidOrThrow();
        ChatRoom chatRoom;
        try {
            chatRoom = getChatContainer().getChatRoom(jid);
        }
        catch (ChatRoomNotFoundException e) {
            ContactList contactList = SparkManager.getWorkspace().getContactList();
            ContactItem item = contactList.getContactItemByJID(jid);
            if (item != null) {
                String nicknameString = item.getDisplayName();
                Resourcepart nickname = Resourcepart.fromOrThrowUnchecked(nicknameString);
                chatRoom = UIComponentRegistry.createChatRoom(jid, nickname, nickname);
            }
            else {
                // TODO Better nickname?
                Resourcepart nickname = Resourcepart.EMPTY;
                chatRoom = UIComponentRegistry.createChatRoom(jid, nickname, jid);
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
     * @return the new ChatRoom created. If an error occurred, null will be returned.
     */
    public ChatRoom createConferenceRoom(Localpart roomName, DomainBareJid serviceName) {
        EntityBareJid roomAddress = JidCreate.entityBareFrom(roomName, serviceName);
        final MultiUserChat chatRoom = SparkManager.getMucManager().getMultiUserChat( roomAddress);
        final GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(chatRoom);
        try {
            LocalPreferences pref = SettingsManager.getLocalPreferences();
            Resourcepart nickname = pref.getNickname();
            chatRoom.create(nickname).makeInstant();
        }
        catch (XMPPException | SmackException | InterruptedException e1) {
            Log.error("Unable to send conference room chat configuration form.", e1);
            return null;
        }
        getChatContainer().addChatRoom(room);
        return room;
    }

    /**
     * Activate a chat room with the selected user.
     *
     * @param nicknameString the nickname of the user.
     */
    public void activateChat(BareJid jid, final String nicknameString) {
        final Resourcepart nickname = Resourcepart.fromOrThrowUnchecked(nicknameString);
        ChatRoom chatRoom;
        ChatContainer chatRooms = SparkManager.getChatManager().getChatContainer();
        try {
            chatRoom = chatRooms.getChatRoom(jid);
        } catch (ChatRoomNotFoundException e) {
            // create new room
            if (jid instanceof EntityJid) {
                chatRoom = UIComponentRegistry.createChatRoom((EntityJid) jid, nickname, nickname);
                chatRooms.addChatRoom(chatRoom);
            } else {
                //TODO implement chat with a BareJid like just a DomainBareJid
                Log.warning("Unsupported bare jid " + jid);
                return;
            }
        }
        chatRooms.activateChatRoom(chatRoom);
    }

    /**
     * Checks if a <code>ChatRoom</code> exists.
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
     */
    public void addMessageFilter(MessageFilter filter) {
        messageFilters.addIfAbsent(filter);
    }

    public void removeMessageFilter(MessageFilter filter) {
        messageFilters.remove(filter);
    }

    /**
     * Adds a new <code>GlobalMessageListener</code>.
     */
    public void addGlobalMessageListener(GlobalMessageListener listener) {
        globalMessageListeners.addIfAbsent(listener);
    }

    public void removeGlobalMessageListener(GlobalMessageListener listener) {
        globalMessageListeners.remove(listener);
    }

    /**
     * Notifies all <code>GlobalMessageListeners</code> of a new incoming message.
     *
     * @param chatRoom the <code>ChatRoom</code> where the message was sent to.
     * @param message  the <code>Message</code>
     */
    public void fireGlobalMessageReceivedListeners(ChatRoom chatRoom, Message message )
    {
        for ( GlobalMessageListener listener : globalMessageListeners )
        {
            try
            {
                listener.messageReceived( chatRoom, message );
            }
            catch ( Exception e )
            {
                Log.error( "A GlobalMessageListener ('" + listener + "') threw an exception while processing an incoming chat message (from '" + message.getFrom() + "') in a chat room ('" + chatRoom + "').", e );
            }
        }
    }

    /**
     * Notifies all <code>GlobalMessageListeners</code> of a new message sent.
     *
     * @param chatRoom the <code>ChatRoom</code> where the message was sent from.
     * @param message  the <code>Message</code> sent.
     */
    public void fireGlobalMessageSentListeners( ChatRoom chatRoom, Message message )
    {
        for ( GlobalMessageListener listener : globalMessageListeners )
        {
            try
            {
                listener.messageSent( chatRoom, message );
            }
            catch ( Exception e )
            {
                Log.error( "A GlobalMessageListener ('" + listener + "') threw an exception while processing an outgoing chat message (to '" + message.getTo() + "') in a chat room ('" + chatRoom + "').", e );
            }
        }
    }

    /**
     * Filters all incoming messages.
     *
     * @param room    the room the message belongs to.
     * @param messageBuilder the message to filter.
     */
    public void filterIncomingMessage( ChatRoom room, MessageBuilder messageBuilder )
    {
        try
        {
            // TODO This probably does not belong here (but in a filter?)
            cancelledNotification( messageBuilder.getFrom(), ChatState.paused );
        }
        catch ( Exception e )
        {
            Log.error( e );
        }

        for ( final MessageFilter filter : messageFilters )
        {
            try
            {
                filter.filterIncoming( room, messageBuilder );
            }
            catch ( Exception e )
            {
                Log.error( "A MessageFilter ('" + filter + "') threw an exception while processing an incoming chat message (from '" + messageBuilder.getFrom() + "') in a chat room ('" + room + "').", e );
            }
        }
    }

    /**
     * Notifies all <code>MessageFilter</code>s about a new outgoing message.
     *
     * @param room    the <code>ChatRoom</code> the message belongs too.
     * @param messageBuilder the <code>Message</code> being sent.
     */
    public void filterOutgoingMessage(ChatRoom room, MessageBuilder messageBuilder )
    {
        for ( final MessageFilter filter : messageFilters )
        {
            try
            {
                filter.filterOutgoing( room, messageBuilder );
            }
            catch ( Exception e )
            {
                Log.error( "A MessageFilter ('" + filter + "') threw an exception while processing an outgoing chat message (from '" + messageBuilder.getFrom() + "') in a chat room ('" + room + "').", e );
            }
        }
    }

    /**
     * Adds a <code>RoomInvitationListener</code>. A RoomInvitationListener is
     */
    public void addInvitationListener(RoomInvitationListener listener) {
        invitationListeners.addIfAbsent(listener);
    }

    /**
     * Removes a <code>RoomInvitationListener</code>.
     */
    public void removeInvitationListener(RoomInvitationListener listener) {
        invitationListeners.remove(listener);
    }

    /**
     * Returns all registered <code>RoomInvitationListener</code>s.
     */
    public Collection<RoomInvitationListener> getInvitationListeners() {
        return Collections.unmodifiableCollection(invitationListeners);
    }

    /**
     * Returns the default conference service to interact with MUC. (ex. conference.jivesoftware.com)
     */
    public String getDefaultConferenceService() {
        if (conferenceService == null) {
            try {
                final MultiUserChatManager multiUserChatManager = SparkManager.getMucManager();
                List<DomainBareJid> col = multiUserChatManager.getMucServiceDomains();
                if (!col.isEmpty()) {
                    conferenceService = col.iterator().next().toString();
                }
            }
            catch (XMPPException | SmackException | InterruptedException e) {
                Log.error(e);
            }
        }
        return conferenceService;
    }

    /**
     * Adds a new <code>ContactItemHandler</code>.
     */
    public void addContactItemHandler(ContactItemHandler handler) {
        contactItemHandlers.addIfAbsent(handler);
    }
    
    public void addChatMessageHandler(ChatMessageHandler handler) {
    	chatMessageHandlers.addIfAbsent(handler);
    }
    
    public void removeChatMessageHandler(ChatMessageHandler handler) {
    	chatMessageHandlers.remove(handler);
    }

    /**
     * Removes a <code>ContactItemHandler</code>.
     */
    public void removeContactItemHandler(ContactItemHandler handler) {
        contactItemHandlers.remove(handler);
    }

    public void fireMessageReceived( Message message )
    {
        for ( ChatMessageHandler handler : chatMessageHandlers )
        {
            try
            {
                handler.messageReceived( message );
            }
            catch ( Exception e )
            {
                Log.error( "A ChatMessageHandler ('" + handler + "') threw an exception while processing this message: " + message, e );
            }
        }
    }

    /**
     * Notifies all <code>ContactItemHandler</code>s of presence changes.
     *
     * @param item     the ContactItem where the presence changed.
     * @param presence the new presence.
     * @return true if it was handled.
     */
    public boolean fireContactItemPresenceChanged( ContactItem item, Presence presence )
    {
        for ( ContactItemHandler handler : contactItemHandlers )
        {
            try
            {
                if ( handler.handlePresence( item, presence ) )
                {
                    return true;
                }
            }
            catch ( Exception e )
            {
                Log.error( "A ContactItemHandler ('" + handler + "') threw an exception while processing a presence change (ContactItem: '" + item + "', presence: [" + presence + "])", e );
            }
        }
        return false;
    }

    /**
     * Notifies all <code>ContactItemHandlers</code> that a <code>ContactItem</code> was double-clicked.
     *
     * @param item the ContactItem that was double-clicked.
     * @return true if the event was intercepted and handled.
     */
    public boolean fireContactItemDoubleClicked( ContactItem item )
    {
        for ( ContactItemHandler handler : contactItemHandlers )
        {
            try
            {
                if ( handler.handleDoubleClick( item ) )
                {
                    return true;
                }
            }
            catch ( Exception e )
            {
                Log.error( "A ContactItemHandler ('" + handler + "') threw an exception while processing a double click on ContactItem: '" + item + "'.", e );
            }
        }
        return false;
    }

    /**
     * Returns the icon from a <code>ContactItemHandler</code>.
     *
     * @param jid the jid.
     */
    public Icon getIconForContactHandler( String jid )
    {
        BareJid bareJid = JidCreate.bareFromOrThrowUnchecked(jid);
        for ( ContactItemHandler handler : contactItemHandlers )
        {
            try
            {
                Icon icon = handler.getIcon( bareJid );
                if ( icon != null )
                {
                    return icon;
                }
            }
            catch ( Exception e )
            {
                Log.error( "A ContactItemHandler ('" + handler + "') threw an exception while processing an icon request for: '" + jid + "'.", e );
            }
        }
        return null;
    }

    /**
     * Returns the icon to use in the tab.
     */
    public Icon getTabIconForContactHandler( Presence presence )
    {
        for ( ContactItemHandler handler : contactItemHandlers )
        {
            try
            {
                Icon icon = handler.getTabIcon( presence );
                if ( icon != null )
                {
                    return icon;
                }
            }
            catch ( Exception e )
            {
                Log.error( "A ContactItemHandler ('" + handler + "') threw an exception while processing a tab icon request for: '" + presence + "'.", e );
            }
        }
        return null;
    }

    public void composingNotification(final Jid from) {
        SwingUtilities.invokeLater( () -> {
            final ContactList contactList = SparkManager.getWorkspace().getContactList();
            ChatRoom chatRoom;
            try {
                chatRoom = getChatContainer().getChatRoom( from.asBareJid() );
                if (chatRoom instanceof ChatRoomImpl) {
                    typingNotificationList.add(chatRoom);
                    // Notify Decorators
                    notifySparkTabHandlers(chatRoom);
                    ((ChatRoomImpl)chatRoom).notifyChatStateChange(ChatState.composing);
                }
            }
            catch (ChatRoomNotFoundException ignored) {
                // Do nothing
            }
            contactList.setIconFor(from, SparkRes.getImageIcon(SparkRes.Icon.SMALL_MESSAGE_EDIT_IMAGE));
        } );
    }

    public void cancelledNotification(final Jid from, final ChatState state) {
        SwingUtilities.invokeLater( () -> {
            ContactList contactList = SparkManager.getWorkspace().getContactList();
            ChatRoom chatRoom;
            try {
                chatRoom = getChatContainer().getChatRoom(from.asBareJid());
                if (chatRoom instanceof ChatRoomImpl) {
                    typingNotificationList.remove(chatRoom);
                    // Notify Decorators
                    notifySparkTabHandlers(chatRoom);
                    ((ChatRoomImpl)chatRoom).notifyChatStateChange(state);
                }
            }
            catch (ChatRoomNotFoundException ignored) {
                // Do nothing
            }
            contactList.useDefaults(from);
        } );
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
     */
    public void removeTypingNotification(ChatRoom chatRoom) {
        typingNotificationList.remove(chatRoom);
    }

    /**
     * Returns true if the <code>ChatRoom</code> state is in typing mode.
     */
    public boolean containsTypingNotification(ChatRoom chatRoom) {
        return typingNotificationList.contains(chatRoom);
    }

    /**
     * Returns true if the room is "stale". A stale room is a room that has
     * not been active for a specific amount of time.
     */
    public boolean isStaleRoom(ChatRoom chatRoom) {
        // Check if the room is stale
        return chatContainer.getStaleChatRooms().contains(chatRoom);
    }

    /**
     * Adds a TranscriptWindowInterceptor.
     */
    public void addTranscriptWindowInterceptor(TranscriptWindowInterceptor interceptor) {
        interceptors.addIfAbsent(interceptor);
    }

    /**
     * Removes a TranscriptWindowInterceptor.
     */
    public void removeTranscriptWindowInterceptor(TranscriptWindowInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    /**
     * Returns the list of <code>TranscriptWindowInterceptors</code>.
     */
    public Collection<TranscriptWindowInterceptor> getTranscriptWindowInterceptors() {
        return interceptors;
    }

    /**
     * Adds a new <code>ContainerDecorator</code>. The ContainerDecorator will be added to the top of the stack and will therefore
     * take priority on notification calls. If all decorators return false, the <code>DefaultChatRoomDecorator</code> will be used.
     */
    public void addSparkTabHandler(SparkTabHandler decorator) {
        sparkTabHandlers.add(0, decorator);
    }

    /**
     * Removes a <code>ContainerDecorator</code>
     */
    public void removeSparkTabHandler(SparkTabHandler decorator) {
        sparkTabHandlers.remove(decorator);
    }

    /**
     * Notifies all <code>ContainerDecorator</code>
     *
     * @param component the component within the tab.
     */
    public void notifySparkTabHandlers( Component component )
    {
        final SparkTab tab = chatContainer.getTabContainingComponent( component );
        if ( tab == null )
        {
            return;
        }
        boolean isChatFrameInFocus = getChatContainer().getChatFrame().isInFocus();
        boolean isSelectedTab = getChatContainer().getSelectedComponent() == component;
        for ( SparkTabHandler decorator : sparkTabHandlers )
        {
            try
            {
                boolean isHandled = decorator.isTabHandled( tab, component, isSelectedTab, isChatFrameInFocus );
                if ( isHandled )
                {
                    tab.validateTab();
                    return;
                }
            }
            catch ( Exception e )
            {
                Log.error( "A SparkTabHandler ('" + decorator + "') threw an exception.", e );
            }
        }
    }

    /**
     * Returns all selected <code>ContactItem</code> users in the <code>ContactList</code>.
     */
    public List<ContactItem> getSelectedContactItems() {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        return contactList.getSelectedUsers();
    }

    /**
     * The listener that we use to track chat state notifications according to XEP-0085.
     */
    private class SmackChatStateListener implements ChatStateListener {
        /**
         * Called by smack when the state of a chat changes.
         *
         * @param chat the chat that is concerned by this event.
         * @param state the new state of the chat.
         */
    	@Override
        public void stateChanged(Chat chat, ChatState state, Message message) {
    	    Jid participant = chat.getXmppAddressOfChatPartner();
            if (state == ChatState.composing) {
                composingNotification(participant);
            } else {
            	cancelledNotification(participant, state);
            }
        }
    }
}
