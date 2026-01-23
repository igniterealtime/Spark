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
package org.jivesoftware.spark.ui.rooms;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.xevent.MessageEventManager;
import org.jivesoftware.smackx.xevent.packet.MessageEvent;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscript;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscripts;
import org.jivesoftware.sparkimpl.plugin.transcripts.HistoryMessage;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.*;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is the Person to Person implementation of <code>ChatRoom</code>
 * This room only allows for 1 to 1 conversations.
 */
public class ChatRoomImpl extends ChatRoom {
    private static final long serialVersionUID = 6163762803773980872L;
    private final CopyOnWriteArrayList<MessageEventListener> messageEventListeners = new CopyOnWriteArrayList<>();
    private EntityBareJid roomname;
    private Icon tabIcon;
    private String roomTitle;
    private String tabTitle;

    /**
     * The participants XMPP address, can be a full or bare JID.
     */
    private EntityJid participantJID;

    private final Resourcepart participantNickname;

    private Presence presence;

    private boolean offlineSent;

    private final Roster roster;

    private String threadID;

    private long lastActivity;

    private boolean active;

    // True if this is a one-on-one with a participant of a multi-user chatroom.
    private final boolean privateChat;

    // Information button
    private ChatRoomButton infoButton;

    private final ChatRoomButton addToRosterButton;
    private VCardPanel vcardPanel;
    
    private JComponent chatStatePanel;    

    public ChatRoomImpl(final EntityJid participantJID, Resourcepart participantNickname, CharSequence title) {
        this(participantJID, participantNickname, title, true);
    }

    /**
     * Constructs a 1-to-1 ChatRoom.
     *
     * Note that the participantJID value can be a bare JID, or a full JID. In regular one-on-one chats, a bare JID is
     * expected. This instance will then display relevant data sent by any of the (full) JIDs associated to the bare JID.
     * When this instance is created to reflect a private message in MUC context, a full JID is expected to be provided
     * as the participantJID value (room@service/nick). In such case, only data sent from that full JID is displayed.
     *
     * @param participantJID      the participants jid to chat with.
     * @param participantNickname the nickname of the participant.
     * @param title               the title of the room.
     */
    public ChatRoomImpl(final EntityJid participantJID, Resourcepart participantNickname, CharSequence title, boolean initUi) {
        Log.debug("Loading chat room impl: " + title);
        this.active = true;
        //activateNotificationTime = System.currentTimeMillis();
        setParticipantJID(participantJID);
        this.participantNickname = participantNickname;

        // Loads the current history for this user.
        SwingUtilities.invokeLater(this::loadHistory);

        // Register StanzaListeners
        final StanzaFilter directFilter = new AndFilter(
            FromMatchesFilter.create( participantJID ),
            new OrFilter( new StanzaTypeFilter( Presence.class ),
                          new StanzaTypeFilter( Message.class ) )
        );

        final StanzaFilter carbonFilter = new AndFilter(
            FromMatchesFilter.create( SparkManager.getSessionManager().getUserBareAddress() ), // Security Consideration, see https://xmpp.org/extensions/xep-0280.html#security
            new StanzaTypeFilter( Message.class ),
            new OrFilter(
                new StanzaExtensionFilter( "sent", CarbonExtension.NAMESPACE ),
                new StanzaExtensionFilter( "received", CarbonExtension.NAMESPACE )
            )
        );

        SparkManager.getConnection().addSyncStanzaListener( this, new OrFilter( directFilter, carbonFilter ) );

        // Use the agents username as the Tab Title
        this.tabTitle = title.toString();

        // The name of the room will be the node of the user jid + conversation.
        this.roomTitle = participantNickname.toString();

        // Add RoomInfo
        this.getSplitPane().setRightComponent(null);
        getSplitPane().setDividerSize(0);


        presence = PresenceManager.getPresence(participantJID.asBareJid());

        roster = Roster.getInstanceFor( SparkManager.getConnection() );

        RosterEntry entry = roster.getEntry(participantJID.asBareJid());

        tabIcon = PresenceManager.getIconFromPresence(presence);

        if (initUi) {
            // Create toolbar buttons.
            infoButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.PROFILE_IMAGE_24x24));
            infoButton.setToolTipText(Res.getString("message.view.information.about.this.user"));
            // Create basic toolbar.
            addChatRoomButton(infoButton);
            // Show VCard.
            infoButton.addActionListener(this);
        }

        // If this is a private chat from a group chat room, do not show toolbar.
        privateChat = participantNickname.equals(participantJID.getResourceOrNull());
        if ( privateChat ) {
            getToolBar().setVisible(false);
        }

        // If the user is not in the roster, then allow user to add them.
        addToRosterButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.ADD_IMAGE_24x24));
        if (entry == null && !privateChat) {
            addToRosterButton.setToolTipText(Res.getString("message.add.this.user.to.your.roster"));
            if (!Default.getBoolean(Default.ADD_CONTACT_DISABLED) && Enterprise.containsFeature(Enterprise.ADD_CONTACTS_FEATURE)) addChatRoomButton(addToRosterButton);
            addToRosterButton.addActionListener(this);
        }

        lastActivity = System.currentTimeMillis();
        Log.debug("Loaded chat room impl: " + title);
    }

    /**
     * Set the XMPP address of the participant.
     *
     * @param jid the XMPP address
     */
    private void setParticipantJID(Jid jid) {
        if (!jid.isEntityJid()) {
            return;
        }
        participantJID = jid.asEntityJidOrThrow();
        roomname = participantJID.asEntityBareJid();
    }

    /**
     * Returns true if this is a private chat from a group chat room.
     *
     * @return true if this this is a PM-based chat with one MUC participant, otherwise false.
     */
    public boolean isPrivateChat()
    {
        return privateChat;
    }

    @Override
	public void closeChatRoom() {
        // If already closed, don't bother.
        if (!active) {
            return;
        }

        super.closeChatRoom();

        removeListeners();

        SparkManager.getChatManager().removeChat(this);

        SparkManager.getConnection().removeAsyncStanzaListener(this);

        active = false;
        vcardPanel = null;

        this.removeAll();
    }

    protected void removeListeners() {
        // Remove info listener
        infoButton.removeActionListener(this);
        addToRosterButton.removeActionListener(this);
        SparkManager.getConnection().removeSyncStanzaListener(this);
    }

    @Override
	public void sendMessage() {
        String text = getChatInputEditor().getText();
        sendMessage(text);
    }

    @Override
	public void sendMessage(String text) {
        MessageBuilder messageBuilder = StanzaBuilder.buildMessage();

        if (threadID == null) {
            threadID = StringUtils.randomString(6);
        }
        messageBuilder.setThread(threadID);

        if ( privateChat )
        {
            // XEP-0045: 7.5 Sending a Private Message
            messageBuilder.addExtension( new MUCUser() );
        }

        // Set the body of the message using typedMessage and remove control
        // characters
        text = text.replaceAll("[\\u0001-\\u0008\\u000B-\\u001F]", "");
        messageBuilder.setBody(text);
        
        // IF there is no body, just return and do nothing
        if (!ModelUtil.hasLength(text)) {
            return;
        }

        // Fire Message Filters
        SparkManager.getChatManager().filterOutgoingMessage(this, messageBuilder);

        // Fire Global Filters
        Message message = messageBuilder.build();
        SparkManager.getChatManager().fireGlobalMessageSentListeners(this, message);

        sendMessage(messageBuilder);
    }

    /**
     * Sends a message to the appropriate jid. The message is automatically added to the transcript.
     *
     * @param messageBuilder the message to send.
     */
    @Override
	public void sendMessage(MessageBuilder messageBuilder) {
        //Before sending message, let's add our full jid for full verification
        //Set message attributes before insertMessage is called - this is useful when transcript window is extended
        //more information will be available to be displayed for the chat area Document
        messageBuilder.ofType(Message.Type.chat);
        // Set chat state to 'active'
        messageBuilder.addExtension( new ChatStateExtension( ChatState.active ) );
        messageBuilder.to(participantJID);
        messageBuilder.from(SparkManager.getSessionManager().getJID());
        Message message = messageBuilder.build();

        displaySendMessage( message );

        // No need to request displayed or delivered as we aren't doing anything with this
        // information.
        MessageEventManager.addNotificationsRequests(message, true, false, false, true);

        // Send the message that contains the notifications request
        try {
            fireOutgoingMessageSending(message);
            SparkManager.getConnection().sendStanza(message);
        }
        catch (Exception ex) {
            Log.error("Error sending message", ex);
        }
    }

    /**
     * Adds a message that is to be sent to the transcript window.
     *
     * @param message The message to be displayed.
     */
    private void displaySendMessage( Message message )
    {
        lastActivity = System.currentTimeMillis();

        try {
            getTranscriptWindow().insertMessage( getNickname(), message, ChatManager.TO_COLOR);
            getChatInputEditor().selectAll();

            getTranscriptWindow().validate();
            getTranscriptWindow().repaint();
            getChatInputEditor().clear();
        }
        catch (Exception ex) {
            Log.error( "Error sending message", ex);
        }

        // Notify users that message has been sent
        fireMessageSent(message);

        addToTranscript(message, false);

        getChatInputEditor().setCaretPosition(0);
        getChatInputEditor().requestFocusInWindow();
        scrollToBottom();
    }

    @Override
    public EntityBareJid getBareJid() {
        return roomname;
    }


    @Override
	public Icon getTabIcon() {
        return tabIcon;
    }

    public void setTabIcon(Icon icon) {
        this.tabIcon = icon;
    }

    @Override
	public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    @Override
	public String getRoomTitle() {
        return roomTitle;
    }

    @Override
	public Message.Type getChatType() {
        return Message.Type.chat;
    }

    @Override
	public void leaveChatRoom() {
        // There really is no such thing in Agent to Agent
    }

    @Override
	public boolean isActive() {
        return true;
    }

    /**
     * Returns the Bare-Participant JID
     *
     * <b> user@server.com </b> <br>
     * for retrieving the full Jid use ChatRoomImpl.getJID()
     *
     * @return
     */
    public EntityBareJid getParticipantJID() {
        return participantJID.asEntityBareJid();
    }

    /**
     * Returns the users full jid (ex. macbeth@jivesoftware.com/spark).
     *
     * @return the users Full JID.
     */
    public EntityFullJid getJID() {
        presence = PresenceManager.getPresence(getParticipantJID());
        return presence.getFrom().asEntityFullJidOrThrow();
    }

    @Override
    public EntityJid getJid() {
        return participantJID;
    }

    /**
     * Process incoming packets.
     *
     * @param stanza - the packet to process
     */
    @Override
    public void processStanza(final Stanza stanza) {
        final Runnable runnable = () -> {
            try
            {
                if ( stanza instanceof Presence )
                {

                    Presence.Type oldType = presence.getType();

                    presence = (Presence) stanza;

                    final Presence presence1 = (Presence) stanza;

                    ContactList list = SparkManager.getWorkspace().getContactList();
                    ContactItem contactItem = list.getContactItemByJID( getParticipantJID() );

                    String time = DateFormat.getTimeInstance( DateFormat.SHORT ).format( new Date() );

                    if ( presence1.getType() == Presence.Type.unavailable && contactItem != null )
                    {
                        getTranscriptWindow().insertNotificationMessage( "*** " + Res.getString( "message.went.offline", participantNickname, time ), ChatManager.NOTIFICATION_COLOR );
                    }
                    else if ( oldType == Presence.Type.unavailable && presence1.getType() == Presence.Type.available )
                    {
                        getTranscriptWindow().insertNotificationMessage( "*** " + Res.getString( "message.came.online", participantNickname, time ), ChatManager.NOTIFICATION_COLOR );
                    }
                }
                else if ( stanza instanceof Message )
                {
                    lastActivity = System.currentTimeMillis();


                    // Do something with the incoming packet here.
                    final Message message = (Message) stanza;
                    fireReceivingIncomingMessage( message );
                    if ( message.getError() != null )
                    {
                        if ( message.getError().getCondition() == StanzaError.Condition.item_not_found )
                        {
                            // Check to see if the user is online to recieve this message.
                            RosterEntry entry = roster.getEntry( participantJID.asBareJid() );
                            if ( !presence.isAvailable() && !offlineSent && entry != null )
                            {
                                getTranscriptWindow().insertNotificationMessage( Res.getString( "message.offline.error" ), ChatManager.ERROR_COLOR );
                                offlineSent = true;
                            }
                        }
                        else
                        {
                            // Show a generic error if something went wrong: SPARK-1895.
                            getTranscriptWindow().insertNotificationMessage( Res.getString( "message.default.error" ) + ": " + message.getError().getCondition(), ChatManager.ERROR_COLOR );
                        }
                        return;
                    }

                    // Check to see if the user is online to recieve this message.
                    RosterEntry entry = roster.getEntry( participantJID.asBareJid() );
                    if ( !presence.isAvailable() && !offlineSent && entry != null )
                    {
                        getTranscriptWindow().insertNotificationMessage( Res.getString( "message.offline" ), ChatManager.ERROR_COLOR );
                        offlineSent = true;
                    }

                    if ( threadID == null )
                    {
                        threadID = message.getThread();
                        if ( threadID == null )
                        {
                            threadID = StringUtils.randomString( 6 );
                        }
                    }

                    final JivePropertiesExtension extension = ( (JivePropertiesExtension) message.getExtension( JivePropertiesExtension.NAMESPACE ) );
                    final boolean broadcast = extension != null && extension.getProperty( "broadcast" ) != null;

                    // If this is a group chat message, discard
                    if ( message.getType() == Message.Type.groupchat || broadcast || message.getType() == Message.Type.normal ||
                            message.getType() == Message.Type.headline )
                    {
                        return;
                    }

                    // Do not accept Administrative messages.
                    final DomainBareJid host = SparkManager.getSessionManager().getServerAddress();
                    if ( host.equals( message.getFrom() ) )
                    {
                        return;
                    }

                    // Do not Handle offline messages. Offline messages are handling by Workspace.
                    if (message.hasExtension(DelayInformation.class) &&
                        (message.getType() == Message.Type.chat ||
                        message.getType() == Message.Type.normal)) {
                        return;
                    }

                    final CarbonExtension carbon = (CarbonExtension) message.getExtension( CarbonExtension.NAMESPACE );
                    if ( carbon != null )
                    {
                        // Is the a carbon copy?
                        final Message forwardedStanza = (Message) carbon.getForwarded().getForwardedStanza();
                        if ( forwardedStanza.getBody() != null )
                        {
                            if ( carbon.getDirection() == CarbonExtension.Direction.received )
                            {
                                if ((forwardedStanza.getFrom().equals(getJid()) && privateChat) || //private chat from MUC, match the full JID
                                    (forwardedStanza.getFrom().asBareJid().equals(getJid().asBareJid()) && !privateChat)) //person to person chat, match bare jids
                                {
                                    // This is a stanza that we received from someone on one of our other clients.
                                    setParticipantJID(forwardedStanza.getFrom());
                                    insertMessage( forwardedStanza );
                                }

                            }
                            else
                            {
                                if ((forwardedStanza.getTo().equals(getJid()) && privateChat) || //private chat from MUC, match full JID
                                    (forwardedStanza.getTo().asBareJid().equals(getJid().asBareJid()) && !privateChat)) //person to person chat, match bare jids
                                {
                                    // This is a stanza that one of our own clients sent.
                                    setParticipantJID(forwardedStanza.getTo());
                                    displaySendMessage( forwardedStanza );
                                }

                            }
                            showTyping( false );
                        }
                    }
                    else if ( message.getBody() != null )
                    {
                        // If the message is not from the current agent. Append to chat.
                        setParticipantJID(message.getFrom());
                        insertMessage( message );

                        showTyping( false );
                    }
                }
            }
            catch ( Exception e )
            {
                Log.error( "An exception occurred while processing this incoming stanza: " + stanza, e );
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Returns the nickname of the user chatting with.
     *
     * @return the nickname of the chatting user.
     */
    public Resourcepart getParticipantNickname() {
        return participantNickname;
    }


    /**
     * The current SendField has been updated somehow.
     *
     * @param e - the DocumentEvent to respond to.
     */
    @Override
	public void insertUpdate(DocumentEvent e) {
        super.insertUpdate( e );
    }

    @Override
	public void insertMessage(Message message) {
        // Debug info
        super.insertMessage(message);
        MessageEvent messageEvent = message.getExtension(MessageEvent.class);
        if (messageEvent != null) {
            checkEvents(message.getFrom(), message.getStanzaId(), messageEvent);
        }

        getTranscriptWindow().insertMessage(participantNickname, message, ChatManager.FROM_COLOR);

        // Set the participant jid to their full JID.
        setParticipantJID(message.getFrom());
    }

    private void checkEvents(Jid from, String packetID, MessageEvent messageEvent) {
        if (messageEvent.isDelivered() || messageEvent.isDisplayed()) {
            // Create the message to send
            MessageBuilder messageBuilder = StanzaBuilder.buildMessage();
            // Create a MessageEvent Package and add it to the message
            MessageEvent event = new MessageEvent();
            if (messageEvent.isDelivered()) {
                event.setDelivered(true);
            }
            if (messageEvent.isDisplayed()) {
                event.setDisplayed(true);
            }
            event.setStanzaId(packetID);
            messageBuilder.addExtension(event);
            messageBuilder.from(from);
            Message msg = messageBuilder.build();
            // Send the packet
            try
            {
                SparkManager.getConnection().sendStanza(msg);
            }
            catch ( SmackException.NotConnectedException | InterruptedException e )
            {
                Log.warning( "Unable to send message to " + msg.getTo(), e );
            }
        }
    }

    public void addMessageEventListener(MessageEventListener listener) {
        messageEventListeners.addIfAbsent(listener);
    }

    public void removeMessageEventListener(MessageEventListener listener) {
        messageEventListeners.remove(listener);
    }

    public Collection<MessageEventListener> getMessageEventListeners() {
        return messageEventListeners;
    }

    public void fireOutgoingMessageSending( Message message )
    {
        for ( final MessageEventListener listener : messageEventListeners )
        {
            try
            {
                listener.sendingMessage( message );
            }
            catch ( Exception e )
            {
                Log.error( "A MessageEventListener ('" + listener + "') threw an exception while processing an outgoing message (to '" + message.getTo() + "').", e );
            }
        }
    }

    public void fireReceivingIncomingMessage( Message message )
    {
        for ( final MessageEventListener listener : messageEventListeners )
        {
            try
            {
                listener.receivingMessage( message );
            }
            catch ( Exception e )
            {
                Log.error( "A MessageEventListener ('" + listener + "') threw an exception while processing an incoming message (from '" + message.getFrom() + "').", e );
            }
        }
    }


    /**
     * Show the typing notification.
     *
     * @param typing true if the typing notification should show, otherwise hide it.
     */
    public void showTyping(boolean typing) {
        if (typing) {
            String isTypingText = Res.getString("message.is.typing.a.message", participantNickname);
            getNotificationLabel().setText(isTypingText);
            getNotificationLabel().setIcon(SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_EDIT_IMAGE));
        }
        else {
            // Remove is typing text.
            getNotificationLabel().setText("");
            getNotificationLabel().setIcon(SparkRes.getImageIcon(SparkRes.BLANK_IMAGE));
        }

    }

    /**
     * The last time this chat room sent or received a message.
     *
     * @return the last time this chat room sent or receieved a message.sendChatState
     */
    @Override
	public long getLastActivity() {
        return lastActivity;
    }

    /**
     * Returns the current presence of the client this room was created for.
     *
     * @return the presence
     */
    public Presence getPresence() {
        return presence;
    }


    @Override
    public void connected( XMPPConnection xmppConnection )
    {

    }

    @Override
    public void authenticated( XMPPConnection xmppConnection, boolean b )
    {
        reconnectionSuccessful();
    }

    @Override
	public void connectionClosed() {
        handleDisconnect();

        String message = Res.getString("message.disconnected.error");
        getTranscriptWindow().insertNotificationMessage(message, ChatManager.ERROR_COLOR);
    }

    @Override
	public void connectionClosedOnError(Exception ex) {
        handleDisconnect();

        String message = Res.getString("message.disconnected.error");

        if (ex instanceof XMPPException.StreamErrorException && ((XMPPException.StreamErrorException) ex).getStreamError().getCondition() == StreamError.Condition.conflict )
        {
            message = Res.getString("message.disconnected.conflict.error");
        }

        getTranscriptWindow().insertNotificationMessage(message, ChatManager.ERROR_COLOR);
    }

    public void reconnectionSuccessful() {
        Presence usersPresence = PresenceManager.getPresence(getParticipantJID());
        if (usersPresence.isAvailable()) {
            presence = usersPresence;
        }

        SparkManager.getChatManager().getChatContainer().fireChatRoomStateUpdated(this);
        getChatInputEditor().setEnabled(true);
        getSendButton().setEnabled(true);
    }

    private void handleDisconnect() {
        presence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unavailable)
            .build();
        getChatInputEditor().setEnabled(false);
        getSendButton().setEnabled(false);
        SparkManager.getChatManager().getChatContainer().fireChatRoomStateUpdated(this);
    }


    protected void loadHistory() {
    	// Add VCard Panel
    	vcardPanel = new VCardPanel(participantJID.asBareJid());
    	getToolBar().add(vcardPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0));

       	if (!Default.getBoolean(Default.HISTORY_DISABLED) && Enterprise.containsFeature(Enterprise.HISTORY_TRANSCRIPTS_FEATURE)) {
    		final LocalPreferences localPreferences = SettingsManager.getLocalPreferences();
    		if (!localPreferences.isChatHistoryEnabled()) {
    			return;
    		}

    		if (!localPreferences.isPrevChatHistoryEnabled()) {
    			return;
    		}

            final ChatTranscript chatTranscript = ChatTranscripts.getCurrentChatTranscript(getJid());
    		final String personalNickname = SparkManager.getUserManager().getNickname();
            final boolean privateChat = participantNickname.equals(participantJID.getResourceOrNull());

    		for (HistoryMessage message : chatTranscript.getMessages()) {
    			String nickname = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom().asBareJid());
    			String messageBody = message.getBody();
    			if (nickname.equals(message.getFrom().toString()) || nickname.equals(message.getFrom().asBareJid().toString())) {
    				BareJid otherJID = message.getFrom().asBareJid();
    				EntityBareJid myJID = SparkManager.getSessionManager().getUserBareAddress();

    				if (otherJID.equals(myJID)) {
    					nickname = personalNickname;
    				}
    				else {
    				    Resourcepart resourcepart = message.getFrom().getResourceOrNull();
                        Localpart localpart = message.getFrom().getLocalpartOrNull();
    				    if (resourcepart == null) {
    				        if (localpart != null) {
    				            nickname = localpart.toString();
    				        }
    				    } else {
                            if(privateChat){
                                nickname = resourcepart.toString();
                            } else {
                                if(localpart != null){
                                    nickname = localpart.toString();
                                }
                            }
    				    }
    				}
    			}

    			if (ModelUtil.hasLength(messageBody) && messageBody.startsWith("/me ")) {
    				messageBody = messageBody.replaceFirst("/me", nickname);
    			}

    			final Date messageDate = message.getDate();
    			getTranscriptWindow().insertHistoryMessage(nickname, messageBody, messageDate);
    		}
    		if ( 0 < chatTranscript.getMessages().size() ) { // Check if we have history mesages
    			getTranscriptWindow().insertHorizontalLine();
    		}
    		chatTranscript.release();
    	}
    }

    private boolean isOnline() {
        Presence presence = roster.getPresence(participantJID.asBareJid());
        return presence.isAvailable();
    }


    // I would normally use the command pattern, but
    // have no real use when dealing with just a couple options.
    @Override
	public void actionPerformed(ActionEvent e) {

        if (e.getSource() == infoButton) {
            VCardManager vcard = SparkManager.getVCardManager();
            vcard.viewProfile(participantJID.asBareJid(), SparkManager.getChatManager().getChatContainer());
        }
        else if (e.getSource() == addToRosterButton) {
            RosterDialog rosterDialog = new RosterDialog();
            rosterDialog.setDefaultJID(participantJID.asBareJid().toString());
            rosterDialog.setDefaultNickname(getParticipantNickname().toString());
            rosterDialog.showRosterDialog(SparkManager.getChatManager().getChatContainer().getChatFrame());
        } else {
            super.actionPerformed(e);
        }
    }

    public void notifyChatStateChange(ChatState state) {
    	if (chatStatePanel != null) {
    		getEditorWrapperBar().remove(chatStatePanel);
    	}
    	
    	chatStatePanel = new ChatStatePanel(state, getParticipantNickname());   	
    	getEditorWrapperBar().add(chatStatePanel, BorderLayout.SOUTH);
    	getEditorWrapperBar().revalidate();
    	getEditorWrapperBar().repaint();
    }

    @Override
    protected void sendChatState(ChatState state) throws SmackException.NotConnectedException, InterruptedException
    {
        if ( !active )
        {
            return;
        }

        MessageBuilder messageBuilder = StanzaBuilder.buildMessage()
            .ofType( Message.Type.chat );

        if (threadID == null) {
            threadID = StringUtils.randomString(6);
        }
        messageBuilder.setThread(threadID);
        messageBuilder.addExtension( new ChatStateExtension( state ) );
        messageBuilder.to(participantJID);
        messageBuilder.from(SparkManager.getSessionManager().getJID());

        Message message = messageBuilder.build();
        SparkManager.getConnection().sendStanza( message );
    }
}
