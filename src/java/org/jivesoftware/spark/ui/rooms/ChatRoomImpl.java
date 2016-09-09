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
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.smackx.xevent.MessageEventManager;
import org.jivesoftware.smackx.xevent.packet.MessageEvent;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscript;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscripts;
import org.jivesoftware.sparkimpl.plugin.transcripts.HistoryMessage;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * This is the Person to Person implementation of <code>ChatRoom</code>
 * This room only allows for 1 to 1 conversations.
 */
public class ChatRoomImpl extends ChatRoom {
    private static final long serialVersionUID = 6163762803773980872L;
    private List<MessageEventListener> messageEventListeners = new ArrayList<>();
    private String roomname;
    private Icon tabIcon;
    private String roomTitle;
    private String tabTitle;
    private String participantJID;
    private String participantNickname;

    private final Color TRANSPARENT_COLOR = new Color(0,0,0,0);

    private Presence presence;

    private boolean offlineSent;

    private Roster roster;

    private String threadID;

    private long lastActivity;

    private boolean active;

    // Information button
    private ChatRoomButton infoButton;

    private ChatRoomButton addToRosterButton;
    private VCardPanel vcardPanel;
    
    private JComponent chatStatePanel;    

    public ChatRoomImpl(final String participantJID, String participantNickname, String title) {
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
    public ChatRoomImpl(final String participantJID, String participantNickname, String title, boolean initUi) {
        this.active = true;
        //activateNotificationTime = System.currentTimeMillis();
        this.participantJID = participantJID;
        this.participantNickname = participantNickname;

        // Loads the current history for this user.
        loadHistory();

        // Register StanzaListeners
        StanzaFilter fromFilter = FromMatchesFilter.create(participantJID);
        StanzaFilter orFilter = new OrFilter(new StanzaTypeFilter(Presence.class), new StanzaTypeFilter(Message.class));
        StanzaFilter andFilter = new AndFilter(orFilter, fromFilter);

        SparkManager.getConnection().addSyncStanzaListener(this, andFilter);

        // The roomname will be the participantJID
        this.roomname = participantJID;

        // Use the agents username as the Tab Title
        this.tabTitle = title;

        // The name of the room will be the node of the user jid + conversation.
        this.roomTitle = participantNickname;

        // Add RoomInfo
        this.getSplitPane().setRightComponent(null);
        getSplitPane().setDividerSize(0);


        presence = PresenceManager.getPresence(participantJID);

        roster = Roster.getInstanceFor( SparkManager.getConnection() );

        RosterEntry entry = roster.getEntry(participantJID);

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

        // If the user is not in the roster, then allow user to add them.
        addToRosterButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.ADD_IMAGE_24x24));
        if (entry == null && !XmppStringUtils.parseResource(participantJID).equals(participantNickname)) {
            addToRosterButton.setToolTipText(Res.getString("message.add.this.user.to.your.roster"));
            if(!Default.getBoolean(Default.ADD_CONTACT_DISABLED)) {
            	addChatRoomButton(addToRosterButton);
            }
            addToRosterButton.addActionListener(this);
        }

        // If this is a private chat from a group chat room, do not show toolbar.
        if (XmppStringUtils.parseResource(participantJID).equals(participantNickname)) {
            getToolBar().setVisible(false);
        }


        lastActivity = System.currentTimeMillis();
    }

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

    public void sendMessage() {
        String text = getChatInputEditor().getText();
        sendMessage(text);
    }

    public void sendMessage(String text) {
        final Message message = new Message();

        if (threadID == null) {
            threadID = StringUtils.randomString(6);
        }
        message.setThread(threadID);

        // Set the body of the message using typedMessage and remove control
        // characters
        text = text.replaceAll("[\\u0001-\\u0008\\u000B-\\u001F]", "");
        message.setBody(text);
        
        // IF there is no body, just return and do nothing
        if (!ModelUtil.hasLength(text)) {
            return;
        }

        // Fire Message Filters
        SparkManager.getChatManager().filterOutgoingMessage(this, message);

        // Fire Global Filters
        SparkManager.getChatManager().fireGlobalMessageSentListeners(this, message);

        sendMessage(message);          	    	
    }

    /**
     * Sends a message to the appropriate jid. The message is automatically added to the transcript.
     *
     * @param message the message to send.
     */
    public void sendMessage(Message message) {
        lastActivity = System.currentTimeMillis();
        //Before sending message, let's add our full jid for full verification
        //Set message attributes before insertMessage is called - this is useful when transcript window is extended
        //more information will be available to be displayed for the chat area Document
        message.setType(Message.Type.chat);
        message.setTo(participantJID);
        message.setFrom(SparkManager.getSessionManager().getJID());
        try {
            getTranscriptWindow().insertMessage(getNickname(), message, ChatManager.TO_COLOR, TRANSPARENT_COLOR);
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

        // No need to request displayed or delivered as we aren't doing anything with this
        // information.
        MessageEventManager.addNotificationsRequests(message, true, false, false, true);

        // Set chat state to 'active'
        message.addExtension( new ChatStateExtension( ChatState.active ) );

        // Send the message that contains the notifications request
        try {
            fireOutgoingMessageSending(message);
            SparkManager.getConnection().sendStanza(message);
        }
        catch (Exception ex) {
            Log.error("Error sending message", ex);
        }
    }

    public String getRoomname() {
        return roomname;
    }


    public Icon getTabIcon() {
        return tabIcon;
    }

    public void setTabIcon(Icon icon) {
        this.tabIcon = icon;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public Message.Type getChatType() {
        return Message.Type.chat;
    }

    public void leaveChatRoom() {
        // There really is no such thing in Agent to Agent
    }

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
    public String getParticipantJID() {
        return participantJID;
    }

    /**
     * Returns the users full jid (ex. macbeth@jivesoftware.com/spark).
     *
     * @return the users Full JID.
     */
    public String getJID() {
        presence = PresenceManager.getPresence(getParticipantJID());
        return presence.getFrom();
    }

    /**
     * Process incoming packets.
     *
     * @param stanza - the packet to process
     */
    public void processPacket(final Stanza stanza) {
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
                        if ( message.getError().getCondition() == XMPPError.Condition.item_not_found )
                        {
                            // Check to see if the user is online to recieve this message.
                            RosterEntry entry = roster.getEntry( participantJID );
                            if ( !presence.isAvailable() && !offlineSent && entry != null )
                            {
                                getTranscriptWindow().insertNotificationMessage( Res.getString( "message.offline.error" ), ChatManager.ERROR_COLOR );
                                offlineSent = true;
                            }
                        }
                        return;
                    }

                    // Check to see if the user is online to recieve this message.
                    RosterEntry entry = roster.getEntry( participantJID );
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
                    final String host = SparkManager.getSessionManager().getServerAddress();
                    if ( host.equals( message.getFrom() ) )
                    {
                        return;
                    }

                    // If the message is not from the current agent. Append to chat.
                    if ( message.getBody() != null )
                    {
                        participantJID = message.getFrom();
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
    public String getParticipantNickname() {
        return participantNickname;
    }


    /**
     * The current SendField has been updated somehow.
     *
     * @param e - the DocumentEvent to respond to.
     */
    public void insertUpdate(DocumentEvent e) {
        super.insertUpdate( e );
    }

    public void insertMessage(Message message) {
        // Debug info
        super.insertMessage(message);
        MessageEvent messageEvent = message.getExtension("x", "jabber:x:event");
        if (messageEvent != null) {
            checkEvents(message.getFrom(), message.getStanzaId(), messageEvent);
        }

        getTranscriptWindow().insertMessage(participantNickname, message, ChatManager.FROM_COLOR, TRANSPARENT_COLOR);

        // Set the participant jid to their full JID.
        participantJID = message.getFrom();
    }

    private void checkEvents(String from, String packetID, MessageEvent messageEvent) {
        if (messageEvent.isDelivered() || messageEvent.isDisplayed()) {
            // Create the message to send
            Message msg = new Message(from);
            // Create a MessageEvent Package and add it to the message
            MessageEvent event = new MessageEvent();
            if (messageEvent.isDelivered()) {
                event.setDelivered(true);
            }
            if (messageEvent.isDisplayed()) {
                event.setDisplayed(true);
            }
            event.setStanzaId(packetID);
            msg.addExtension(event);
            // Send the packet
            try
            {
                SparkManager.getConnection().sendStanza(msg);
            }
            catch ( SmackException.NotConnectedException e )
            {
                Log.warning( "Unable to send message to " + msg.getTo(), e );
            }
        }
    }

    public void addMessageEventListener(MessageEventListener listener) {
        messageEventListeners.add(listener);
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

    }

    public void connectionClosed() {
        handleDisconnect();

        String message = Res.getString("message.disconnected.error");
        getTranscriptWindow().insertNotificationMessage(message, ChatManager.ERROR_COLOR);
    }

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
        presence = new Presence(Presence.Type.unavailable);
        getChatInputEditor().setEnabled(false);
        getSendButton().setEnabled(false);
        SparkManager.getChatManager().getChatContainer().fireChatRoomStateUpdated(this);
    }


    protected void loadHistory() {
        // Add VCard Panel
        vcardPanel = new VCardPanel(participantJID);
        getToolBar().add(vcardPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0));

        if (!Default.getBoolean("HISTORY_DISABLED")) {
        final LocalPreferences localPreferences = SettingsManager.getLocalPreferences();
        if (!localPreferences.isChatHistoryEnabled()) {
            return;
        }

        if (!localPreferences.isPrevChatHistoryEnabled()) {
        	return;
        }

        final ChatTranscript chatTranscript = ChatTranscripts.getCurrentChatTranscript(getParticipantJID());
        final String personalNickname = SparkManager.getUserManager().getNickname();

        for (HistoryMessage message : chatTranscript.getMessages()) {
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom());
            String messageBody = message.getBody();
            if (nickname.equals(message.getFrom())) {
                String otherJID = XmppStringUtils.parseBareJid(message.getFrom());
                String myJID = SparkManager.getSessionManager().getBareAddress();

                if (otherJID.equals(myJID)) {
                    nickname = personalNickname;
                }
                else {
                    try
                    {
                        nickname = message.getFrom().substring(message.getFrom().indexOf("/")+1);
                    }
                    catch(Exception e)
                    {
                        nickname = XmppStringUtils.parseLocalpart(nickname);
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
        Presence presence = roster.getPresence(getParticipantJID());
        return presence.isAvailable();
    }


    // I would normally use the command pattern, but
    // have no real use when dealing with just a couple options.
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == infoButton) {
            VCardManager vcard = SparkManager.getVCardManager();
            vcard.viewProfile(participantJID, SparkManager.getChatManager().getChatContainer());
        }
        else if (e.getSource() == addToRosterButton) {
            RosterDialog rosterDialog = new RosterDialog();
            rosterDialog.setDefaultJID(XmppStringUtils.parseBareJid(participantJID));
            rosterDialog.setDefaultNickname(getParticipantNickname());
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
    protected void sendChatState(ChatState state) throws SmackException.NotConnectedException
    {
        if ( !active )
        {
            return;
        }

        final Message message = new Message();
        message.setType( Message.Type.chat );
        message.setTo( participantJID );
        message.setFrom( SparkManager.getSessionManager().getJID());

        if (threadID == null) {
            threadID = StringUtils.randomString(6);
        }
        message.setThread(threadID);
        message.addExtension( new ChatStateExtension( state ) );

        SparkManager.getConnection().sendStanza( message );
    }
}
