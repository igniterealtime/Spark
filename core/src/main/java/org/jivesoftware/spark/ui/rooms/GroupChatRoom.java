/**
 * <p>
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.ui.rooms;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.DefaultUserStatusListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.packet.Destroy;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xevent.MessageEventManager;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.ui.conferences.AnswerFormDialog;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.ui.conferences.DataFormDialog;
import org.jivesoftware.spark.ui.conferences.GroupChatParticipantList;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * GroupChatRoom is the conference chat room UI used to have Multi-User Chats.
 */
public class GroupChatRoom extends ChatRoom
{
    private final LocalPreferences pref = SettingsManager.getLocalPreferences();
    private final MultiUserChat chat;
    private final SubjectPanel subjectPanel;
    private final List<String> currentUserList = new ArrayList<>();
    private final List<String> blockedUsers = new ArrayList<>();
    private final GroupChatParticipantList roomInfo;
    private final RolloverButton settings;
    private Icon tabIcon = SparkRes.getImageIcon( SparkRes.CONFERENCE_IMAGE_16x16 );
    private String password = null;
    private String tabTitle;
    private boolean isActive = true;
    private long lastActivity;
    private Message lastMessage;
    private boolean chatStatEnabled;

    /**
     * Creates a GroupChatRoom from a <code>MultiUserChat</code>.
     *
     * @param chat the MultiUserChat to create a GroupChatRoom from.
     */
    public GroupChatRoom( final MultiUserChat chat )
    {
        this.chat = chat;

        // Create the filter and register with the current connection making sure to filter by room
        final StanzaFilter fromFilter = FromMatchesFilter.createBare( chat.getRoom() );
        final StanzaFilter orFilter = new OrFilter( new StanzaTypeFilter( Presence.class ), new StanzaTypeFilter( Message.class ) );
        final StanzaFilter andFilter = new AndFilter( orFilter, fromFilter );

        // Add packet Listener.
        SparkManager.getConnection().addAsyncStanzaListener( this, andFilter );

        // We are just using a generic Group Chat.
        tabTitle = XmppStringUtils.parseLocalpart( XmppStringUtils.unescapeLocalpart( chat.getRoom() ) );

        // Room Information
        roomInfo = UIComponentRegistry.createGroupChatParticipantList();
        getSplitPane().setRightComponent( roomInfo.getGUI() );
        getSplitPane().setResizeWeight( 0.8 );

        roomInfo.setChatRoom( this );

        setupListeners();

        subjectPanel = new SubjectPanel( this );

        // Do not show top toolbar
        getToolBar().add(
                subjectPanel,
                new GridBagConstraints( 0, 1, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.HORIZONTAL, new Insets( 0, 2, 0, 2 ),
                        0, 0 ) );

        // Add ContextMenuListener
        getTranscriptWindow().addContextMenuListener( new ContextMenuListener()
        {
            @Override
            public void poppingUp( Object component, JPopupMenu popup )
            {
                popup.addSeparator();
                Action inviteAction = new AbstractAction()
                {
                    @Override
                    public void actionPerformed( ActionEvent actionEvent )
                    {
                        ConferenceUtils.inviteUsersToRoom( chat, null, false );
                    }
                };

                inviteAction.putValue( Action.NAME, Res.getString( "menuitem.invite.users" ) );
                inviteAction.putValue( Action.SMALL_ICON, SparkRes.getImageIcon( SparkRes.SMALL_MESSAGE_IMAGE ) );

                popup.add( inviteAction );

                Action configureAction = new AbstractAction()
                {
                    @Override
                    public void actionPerformed( ActionEvent actionEvent )
                    {
                        try
                        {
                            ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
                            Form form = chat.getConfigurationForm().createAnswerForm();
                            new DataFormDialog( chatFrame, chat, form );
                        }
                        catch ( XMPPException | SmackException e )
                        {
                            Log.error( "Error configuring room.", e );
                        }
                    }
                };

                configureAction.putValue( Action.NAME, Res.getString( "title.configure.room" ) );
                configureAction.putValue( Action.SMALL_ICON, SparkRes.getImageIcon( SparkRes.SETTINGS_IMAGE_16x16 ) );
                if ( SparkManager.getUserManager().isOwner( (GroupChatRoom) getChatRoom(), chat.getNickname() ) )
                {
                    popup.add( configureAction );
                }

                Action subjectChangeAction = new AbstractAction()
                {
                    @Override
                    public void actionPerformed( ActionEvent actionEvent )
                    {
                        String newSubject = JOptionPane.showInputDialog(
                                getChatRoom(),
                                Res.getString( "message.enter.new.subject" ) + ":",
                                Res.getString( "title.change.subject" ),
                                JOptionPane.QUESTION_MESSAGE );
                        if ( ModelUtil.hasLength( newSubject ) )
                        {
                            try
                            {
                                chat.changeSubject( newSubject );
                            }
                            catch ( XMPPException | SmackException e )
                            {
                                Log.error( e );
                            }
                        }
                    }
                };

                subjectChangeAction.putValue( Action.NAME, Res.getString( "menuitem.change.subject" ) );
                subjectChangeAction.putValue( Action.SMALL_ICON, SparkRes.getImageIcon( SparkRes.SMALL_MESSAGE_EDIT_IMAGE ) );
                popup.add( subjectChangeAction );

                // Define actions to modify/view room information
                Action destroyRoomAction = new AbstractAction()
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        int ok = JOptionPane.showConfirmDialog(
                                getChatRoom(),
                                Res.getString( "message.confirm.destruction.of.room" ),
                                Res.getString( "title.confirmation" ),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE );
                        if ( ok == JOptionPane.NO_OPTION )
                        {
                            return;
                        }

                        String reason = JOptionPane.showInputDialog(
                                getChatRoom(),
                                Res.getString( "message.room.destruction.reason" ),
                                Res.getString( "title.enter.reason" ),
                                JOptionPane.QUESTION_MESSAGE );
                        if ( ModelUtil.hasLength( reason ) )
                        {
                            try
                            {
                                chat.destroy( reason, null );
                                getChatRoom().leaveChatRoom();
                            }
                            catch ( XMPPException | SmackException e1 )
                            {
                                Log.warning( "Unable to destroy room", e1 );
                            }
                        }
                    }
                };

                destroyRoomAction.putValue( Action.NAME, Res.getString( "menuitem.destroy.room" ) );
                destroyRoomAction.putValue( Action.SMALL_ICON, SparkRes.getImageIcon( SparkRes.SMALL_DELETE ) );
                if ( SparkManager.getUserManager().isOwner( (GroupChatRoom) getChatRoom(), getNickname() ) )
                {
                    popup.add( destroyRoomAction );
                }

            }

            @Override
            public void poppingDown( JPopupMenu popup )
            {
            }

            @Override
            public boolean handleDefaultAction( MouseEvent e )
            {
                return false;
            }
        } );

        // set last activity to be right now
        lastActivity = System.currentTimeMillis();

        final GroupChatRoomTransferHandler transferHandler = new GroupChatRoomTransferHandler( this );
        getTranscriptWindow().setTransferHandler( transferHandler );

        // Adds the Settings and Subject Button to the right Toolbar
        settings = UIComponentRegistry.getButtonFactory().createSettingsButton();
        settings.setVisible( false );
        final RolloverButton thema = UIComponentRegistry.getButtonFactory().createTemaButton();
        final RolloverButton register = UIComponentRegistry.getButtonFactory().createRegisterButton();

        addControllerButton( settings );
        addControllerButton( thema );
        addControllerButton( register );

        settings.addActionListener( new AbstractAction()
        {
            @Override
            public void actionPerformed( ActionEvent event )
            {
                try
                {
                    final ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
                    final Form form = chat.getConfigurationForm().createAnswerForm();
                    new DataFormDialog( chatFrame, chat, form );
                }
                catch ( XMPPException | SmackException xmpe )
                {
                    getTranscriptWindow().insertNotificationMessage( xmpe.getMessage(), ChatManager.ERROR_COLOR );
                    scrollToBottom();
                }
            }
        } );

        thema.addActionListener( new AbstractAction()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                final String newSubject = JOptionPane.showInputDialog( getChatRoom(),
                        Res.getString( "message.enter.new.subject" ) + ":",
                        Res.getString( "title.change.subject" ),
                        JOptionPane.QUESTION_MESSAGE );
                if ( ModelUtil.hasLength( newSubject ) )
                {
                    try
                    {
                        chat.changeSubject( newSubject );
                    }
                    catch ( XMPPException | SmackException xmpee )
                    {
                        getTranscriptWindow().insertNotificationMessage( xmpee.getMessage(), ChatManager.ERROR_COLOR );
                        scrollToBottom();
                    }
                }
            }
        } );

        register.addActionListener( event -> {
            try
            {
                final Form form = chat.getRegistrationForm();
                final ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();

                new AnswerFormDialog( chatFrame, chat, form );
            }
            catch ( XMPPException | SmackException xmpe )
            {
                getTranscriptWindow().insertNotificationMessage( xmpe.getMessage(), ChatManager.ERROR_COLOR );
                scrollToBottom();
            }
        } );
    }

    public Message getLastMessage()
    {
        return lastMessage;
    }

    /**
     * Have the user leave this chat room and then close it.
     */
    @Override
    public void closeChatRoom()
    {
        // Specify the end time.
        super.closeChatRoom();

        // Remove Listener
        SparkManager.getConnection().removeAsyncStanzaListener( this );

        final ChatContainer container = SparkManager.getChatManager().getChatContainer();
        container.leaveChatRoom( this );
        container.closeTab( this );
    }

    /**
     * Determines the background color to use for messages.
     *
     * @param nickname Nickname associated with message.
     * @param body     Body of message to scan for reasons to highlight.
     * @return Color of message background.
     */
    private Color getMessageBackground( String nickname, String body )
    {
        final String myNickName = chat.getNickname();
        final String myUserName = SparkManager.getSessionManager().getUsername();
        final Pattern usernameMatch = Pattern.compile( myUserName, Pattern.CASE_INSENSITIVE );
        final Pattern nicknameMatch = Pattern.compile( myNickName, Pattern.CASE_INSENSITIVE );

        // Should we even highlight this packet?
        if ( pref.isMucHighNameEnabled() && myNickName.equalsIgnoreCase( nickname ) )
        {
            return new Color( 244, 248, 255 );
        }
        else if ( pref.isMucHighTextEnabled() && ( usernameMatch.matcher( body ).find() || nicknameMatch.matcher( body ).find() ) )
        {
            return new Color( 255, 255, 153 );
        }
        else
        {
            return new Color( 0, 0, 0, 0 );
        }
    }

    /**
     * Sends a message.
     *
     * @param message - the message to send.
     */
    @Override
    public void sendMessage( Message message )
    {
        try
        {
            message.setTo( chat.getRoom() );
            message.setType( Message.Type.groupchat );
            MessageEventManager.addNotificationsRequests( message, true, true, true, true );
            addPacketID( message.getStanzaId() );

            SparkManager.getChatManager().filterOutgoingMessage( this, message );
            SparkManager.getChatManager().fireGlobalMessageSentListeners( this, message );

            chat.sendMessage( message );
        }
        catch ( SmackException ex )
        {
            Log.error( "Unable to send message in conference chat.", ex );
        }

        // Notify users that message has been sent.
        fireMessageSent( message );

        addToTranscript( message, false );

        getChatInputEditor().clear();
        getTranscriptWindow().validate();
        getTranscriptWindow().repaint();

        getChatInputEditor().setCaretPosition( 0 );
        getChatInputEditor().requestFocusInWindow();
        scrollToBottom();

        lastActivity = System.currentTimeMillis();
    }

    /**
     * Return name of the room specified when the room was created.
     *
     * @return the roomname.
     */
    @Override
    public String getRoomname()
    {
        return XmppStringUtils.parseBareJid( chat.getRoom() );
    }

    /**
     * Retrieve the nickname of the user in this groupchat.
     *
     * @return the nickname of the agent in this groupchat
     */
    @Override
    public String getNickname()
    {
        return chat.getNickname();
    }

    /**
     * Return the Icon that should be used in the tab of this GroupChat Pane.
     *
     * @return the Icon to use in tab.
     */
    @Override
    public Icon getTabIcon()
    {
        return tabIcon;
    }

    /**
     * Sets the icon that should be used in the tab of this GroupChat Pane.
     *
     * @param tabIcon the Icon to use in tab.
     */
    public void setTabIcon( ImageIcon tabIcon )
    {
        this.tabIcon = tabIcon;
    }

    /**
     * Return the title that should be used in the tab.
     *
     * @return the title to be used on the tab.
     */
    @Override
    public String getTabTitle()
    {
        return tabTitle;
    }

    /**
     * Sets the title to use on the tab describing the Conference room.
     *
     * @param tabTitle the title to use on the tab.
     */
    public void setTabTitle( String tabTitle )
    {
        this.tabTitle = tabTitle;
    }

    /**
     * Return the title of this room.
     *
     * @return the title of this room.
     */
    @Override
    public String getRoomTitle()
    {
        return getTabTitle();
    }

    /**
     * Return the type of chat we are in.
     *
     * @return the type of chat we are in.
     */
    @Override
    public Message.Type getChatType()
    {
        return Message.Type.groupchat;
    }

    /**
     * Implementation of leaveChatRoom.
     */
    @Override
    public void leaveChatRoom()
    {
        if ( !isActive )
        {
            return;
        }

        SparkManager.getConnection().removeAsyncStanzaListener( this );

        getChatInputEditor().showAsDisabled();

        // Do not allow other to try and invite or transfer chat
        disableToolbar();

        getToolBar().setVisible( false );

        // Update Room Notice To Inform Agent that he has left the chat.
        getTranscriptWindow().insertNotificationMessage( Res.getString( "message.user.left.room", getNickname() ), ChatManager.NOTIFICATION_COLOR );

        try
        {
            chat.leave();
        }
        catch ( Exception e )
        {
            Log.error( "Closing Group Chat Room error.", e );
        }

        // Set window as greyed out.
        getTranscriptWindow().showWindowDisabled();

        // Update Notification Label
        getNotificationLabel().setText( Res.getString( "message.chat.session.ended", SparkManager.DATE_SECOND_FORMATTER.format( new java.util.Date() ) ) );
        getNotificationLabel().setIcon( null );
        getNotificationLabel().setEnabled( false );

        getSplitPane().setRightComponent( null );
        getSplitPane().setDividerSize( 0 );

        isActive = false;
    }

    /**
     * Returns whether or not this ChatRoom is active. To be active means to
     * have the agent still engaged in a conversation with a customer.
     *
     * @return true if the ChatRoom is active.
     */
    @Override
    public boolean isActive()
    {
        return isActive;
    }

    /**
     * Implementation of processPacket to handle muc related packets.
     *
     * @param stanza the packet.
     */
    @Override
    public void processPacket( final Stanza stanza )
    {
        super.processPacket( stanza );
        if ( stanza instanceof Presence )
        {
            SwingUtilities.invokeLater( () -> handlePresencePacket( stanza ) );
        }

        if ( stanza instanceof Message )
        {
            SwingUtilities.invokeLater( () -> {
                handleMessagePacket( stanza );

                // Set last activity
                lastActivity = System.currentTimeMillis();
            } );
        }
    }

    /**
     * Handle all MUC related packets.
     *
     * @param stanza the packet.
     */
    private void handleMessagePacket( Stanza stanza )
    {
        // Do something with the incoming packet here.
        final Message message = (Message) stanza;
        lastMessage = message;
        if ( message.getType() == Message.Type.groupchat )
        {
            final DelayInformation inf = message.getExtension( "delay", "urn:xmpp:delay" );
            final Date sentDate = inf != null ? inf.getStamp() : new Date();

            // Do not accept Administrative messages.
            final String host = SparkManager.getSessionManager().getServerAddress();
            if ( host.equals( message.getFrom() ) )
            {
                return;
            }

            if ( ModelUtil.hasLength( message.getBody() ) )
            {
                final String from = XmppStringUtils.parseResource( message.getFrom() );

                if ( inf != null )
                {
                    // This is part of the MUC history. No need to add it to the transcript again.

                    // Add to the UI component that shows the chat.
                    getTranscriptWindow().insertHistoryMessage( from, message.getBody(), sentDate );
                }
                else
                {
                    // A 'regular' chat message.
                    if ( isBlocked( message.getFrom() ) )
                    {
                        return;
                    }

                    final boolean isFromRoom = !message.getFrom().contains( "/" );

                    if ( !SparkManager.getUserManager().hasVoice( this, from ) && !isFromRoom )
                    {
                        return;
                    }

                    // Update transcript
                    super.insertMessage( message );

                    // Add to the UI component that shows the chat.
                    getTranscriptWindow().insertMessage( from, message, getColor( from ), getMessageBackground( from, message.getBody() ) );
                }
            }
        }
        else if ( message.getType() == Message.Type.chat )
        {
            try
            {
                SparkManager.getChatManager().getChatContainer().getChatRoom( message.getFrom() );
            }
            catch ( ChatRoomNotFoundException e )
            {
                final String userNickname = XmppStringUtils.parseResource( message.getFrom() );
                final String roomTitle = userNickname + " - " + XmppStringUtils.parseLocalpart( getRoomname() );

                // Check to see if this is a message notification.
                if ( message.getBody() != null )
                {
                    // Create new room
                    ChatRoom chatRoom = new ChatRoomImpl( message.getFrom(), userNickname, roomTitle );
                    SparkManager.getChatManager().getChatContainer().addChatRoom( chatRoom );

                    SparkManager.getChatManager().getChatContainer().activateChatRoom( chatRoom );
                    chatRoom.insertMessage( message );
                }
            }

        }
        else if ( message.getError() != null )
        {
            String errorMessage = "";

            if ( message.getError().getCondition() == XMPPError.Condition.forbidden && message.getSubject() != null )
            {
                errorMessage = Res.getString( "message.subject.change.error" );
            }

            else if ( message.getError().getCondition() == XMPPError.Condition.forbidden )
            {
                errorMessage = Res.getString( "message.forbidden.error" );
            }

            if ( ModelUtil.hasLength( errorMessage ) )
            {
                getTranscriptWindow().insertNotificationMessage( errorMessage, ChatManager.ERROR_COLOR );
            }
        }

        //Scroll To bottom every time a message is received
        scrollToBottom();
    }

    /**
     * Handle all presence packets being sent to this Group Chat Room.
     *
     * @param stanza the presence packet.
     */
    private void handlePresencePacket( Stanza stanza )
    {
        final Presence presence = (Presence) stanza;
        if ( presence.getError() != null )
        {
            return;
        }

        final String from = presence.getFrom();
        final String nickname = XmppStringUtils.parseResource( from );

        final MUCUser mucUser = stanza.getExtension( "x", "http://jabber.org/protocol/muc#user" );
        final Set<MUCUser.Status> status = new HashSet<>();
        if ( mucUser != null )
        {
            status.addAll( mucUser.getStatus() );
            final Destroy destroy = mucUser.getDestroy();
            if ( destroy != null )
            {
                UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );
                JOptionPane.showMessageDialog( this,
                        Res.getString( "message.room.destroyed", destroy.getReason() ),
                        Res.getString( "title.room.destroyed" ),
                        JOptionPane.INFORMATION_MESSAGE );
                leaveChatRoom();
                return;
            }
        }

        if ( presence.getType() == Presence.Type.unavailable && !status.contains( MUCUser.Status.NEW_NICKNAME_303 ) )
        {
            if ( currentUserList.contains( from ) )
            {
                if ( pref.isShowJoinLeaveMessagesEnabled() )
                {
                    getTranscriptWindow().insertNotificationMessage( Res.getString( "message.user.left.room", nickname ), ChatManager.NOTIFICATION_COLOR );
                    scrollToBottom();
                }
                currentUserList.remove( from );
            }
        }
        else
        {
            if ( !currentUserList.contains( from ) )
            {
                currentUserList.add( from );
                getChatInputEditor().setEnabled( true );
                if ( pref.isShowJoinLeaveMessagesEnabled() )
                {
                    getTranscriptWindow().insertNotificationMessage(
                            Res.getString( "message.user.joined.room", nickname ),
                            ChatManager.NOTIFICATION_COLOR );
                    scrollToBottom();
                }
            }
        }
    }

    /**
     * Set up the participant listeners and status change listeners.
     */
    private void setupListeners()
    {
        chat.addParticipantStatusListener( new DefaultParticipantStatusListener()
        {
            @Override
            public void kicked( String participant, String actor, String reason )
            {
                insertText( Res.getString( "message.user.kicked.from.room", XmppStringUtils.parseResource( participant ), actor, reason ) );
            }

            @Override
            public void voiceGranted( String participant )
            {
                insertText( Res.getString( "message.user.given.voice", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void voiceRevoked( String participant )
            {
                insertText( Res.getString( "message.user.voice.revoked", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void banned( String participant, String actor, String reason )
            {
                insertText( Res.getString( "message.user.banned", XmppStringUtils.parseResource( participant ), reason ) );
            }

            @Override
            public void membershipGranted( String participant )
            {
                insertText( Res.getString( "message.user.granted.membership", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void membershipRevoked( String participant )
            {
                insertText( Res.getString( "message.user.revoked.membership", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void moderatorGranted( String participant )
            {
                insertText( Res.getString( "message.user.granted.moderator", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void moderatorRevoked( String participant )
            {
                insertText( Res.getString( "message.user.revoked.moderator", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void ownershipGranted( String participant )
            {
                insertText( Res.getString( "message.user.granted.owner", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void ownershipRevoked( String participant )
            {
                insertText( Res.getString( "message.user.revoked.owner", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void adminGranted( String participant )
            {
                insertText( Res.getString( "message.user.granted.admin", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void adminRevoked( String participant )
            {
                insertText( Res.getString( "message.user.revoked.admin", XmppStringUtils.parseResource( participant ) ) );
            }

            @Override
            public void nicknameChanged( String participant, String nickname )
            {
                insertText( Res.getString( "message.user.nickname.changed", XmppStringUtils.parseResource( participant ), nickname ) );
            }
        } );

        chat.addUserStatusListener( new DefaultUserStatusListener()
        {
            @Override
            public void kicked( String s, String reason )
            {
                if ( ModelUtil.hasLength( reason ) )
                {
                    insertText( reason );
                }
                else
                {
                    insertText( Res.getString( "message.your.kicked", s ) );
                }

                getChatInputEditor().setEnabled( false );
                getSplitPane().setRightComponent( null );
                leaveChatRoom();
            }

            @Override
            public void voiceGranted()
            {
                insertText( Res.getString( "message.your.voice.granted" ) );
                getChatInputEditor().setEnabled( true );
            }

            @Override
            public void voiceRevoked()
            {
                insertText( Res.getString( "message.your.voice.revoked" ) );
                getChatInputEditor().setEnabled( false );
            }

            @Override
            public void banned( String s, String reason )
            {
                insertText( Res.getString( "message.your.banned" ) );
            }

            @Override
            public void membershipGranted()
            {
                insertText( Res.getString( "message.your.membership.granted" ) );
            }

            @Override
            public void membershipRevoked()
            {
                insertText( Res.getString( "message.your.membership.revoked" ) );
            }

            @Override
            public void moderatorGranted()
            {
                insertText( Res.getString( "message.your.moderator.granted" ) );
            }

            @Override
            public void moderatorRevoked()
            {
                insertText( Res.getString( "message.your.moderator.revoked" ) );
            }

            @Override
            public void ownershipGranted()
            {
                insertText( Res.getString( "message.your.ownership.granted" ) );
            }

            @Override
            public void ownershipRevoked()
            {
                insertText( Res.getString( "message.your.ownership.revoked" ) );
            }

            @Override
            public void adminGranted()
            {
                insertText( Res.getString( "message.your.admin.granted" ) );
            }

            @Override
            public void adminRevoked()
            {
                insertText( Res.getString( "message.your.revoked.granted" ) );
            }
        } );

        chat.addSubjectUpdatedListener( ( subject, by ) -> {
            subjectPanel.setSubject( subject );
            final String nickname = XmppStringUtils.parseResource( by );
            final String insertMessage = Res.getString( "message.subject.has.been.changed.to", subject, nickname );
            getTranscriptWindow().insertNotificationMessage( insertMessage, ChatManager.NOTIFICATION_COLOR );
        } );
    }

    /**
     * Changes the label that is displayed for this room. Does not send an update to the XMPP server (UI only).
     *
     * @param label The new label.
     */
    public void setRoomLabel( String label ) {
        subjectPanel.setRoomLabel( label );
    }

    /**
     * Inserts a notification message within the TranscriptWindow.
     *
     * @param text the text to insert.
     */
    public void insertText( String text )
    {
        getTranscriptWindow().insertNotificationMessage( text, ChatManager.NOTIFICATION_COLOR );
    }

    /**
     * Returns the user format (e.g. darkcave@macbeth.shakespeare.lit/thirdwitch) of each user in the room.
     *
     * @return the user format (e.g. darkcave@macbeth.shakespeare.lit/thirdwitch) of each user in the room.
     */
    public Collection<String> getParticipants()
    {
        return currentUserList;
    }

    /**
     * Sends the message that is currently in the send field.
     */
    @Override
    public void sendMessage()
    {
        sendMessage( getChatInputEditor().getText() );
    }

    @Override
    public void sendMessage( String text )
    {
        // IF there is no body, just return and do nothing
        if ( !ModelUtil.hasLength( text ) )
        {
            return;
        }

        // Set the body of the message using typedMessage and remove control characters
        final Message message = new Message();
        message.setBody( text.replaceAll( "[\\u0001-\\u0008\\u000B-\\u001F]", "" ) );

        sendMessage( message );
    }

    /**
     * Returns a MultiUserChat object associated with this room.
     *
     * @return the <code>MultiUserChat</code> object associated with this room.
     */
    public MultiUserChat getMultiUserChat()
    {
        return chat;
    }

    /**
     * Adds a user to the blocked user list. Blocked users is NOT a MUC related item, but rather used by the client to
     * not display messages from certain people.
     *
     * @param usersJID the room jid of the user (ex.spark@conference.jivesoftware.com/Dan)
     */
    public void addBlockedUser( String usersJID )
    {
        blockedUsers.add( usersJID );
    }

    /**
     * Removes a user from the blocked user list.
     *
     * @param usersJID the jid of the user (ex. spark@conference.jivesoftware.com/Dan)
     */
    public void removeBlockedUser( String usersJID )
    {
        blockedUsers.remove( usersJID );
    }

    /**
     * Returns true if the user is in the blocked user list.
     *
     * @param usersJID the jid of the user (ex. spark@conference.jivesoftware.com/Dan)
     * @return true if the user is blocked, otherwise false.
     */
    public boolean isBlocked( String usersJID )
    {
        return blockedUsers.contains( usersJID );
    }

    /**
     * Invite a user to this conference room.
     *
     * @param jid     the jid of the user to invite.
     * @param message the message to send with the invitation.
     */
    public void inviteUser( String jid, String message )
    {
        message = message != null && !message.isEmpty() ? message : Res.getString( "message.please.join.in.conference" );

        try
        {
            getMultiUserChat().invite( jid, message );
            roomInfo.addInvitee( jid, message );
        }
        catch ( SmackException.NotConnectedException e )
        {
            Log.warning( "Unable to invite " + jid + " to room " + roomInfo.getName(), e );
        }
    }

    /**
     * Returns the GroupChatParticipantList which displays all users within a conference room.
     *
     * @return the GroupChatParticipantList.
     */
    public GroupChatParticipantList getConferenceRoomInfo()
    {
        return roomInfo;
    }

    @Override
    public long getLastActivity()
    {
        return lastActivity;
    }

    @Override
    public void connected( XMPPConnection xmppConnection )
    {
    }

    @Override
    public void authenticated( XMPPConnection xmppConnection, boolean b )
    {
    }

    @Override
    public void connectionClosed()
    {
        handleDisconnect();
    }

    @Override
    public void connectionClosedOnError( Exception ex )
    {
        handleDisconnect();

        getTranscriptWindow().showWindowDisabled();
        getSplitPane().setRightComponent( null );
        getTranscriptWindow().insertNotificationMessage( Res.getString( "message.disconnected.group.chat.error" ), ChatManager.ERROR_COLOR );
    }

    /**
     * Sets the Password for this GroupChat if available, to rejoin the chat after a reconnection without prompting the user
     */
    public void setPassword( String password )
    {
        this.password = password;
    }

    /**
     * Part of ConnectionListener. Gets triggered when successfully reconnected.
     */
    @Override
    public void reconnectionSuccessful()
    {
        final String roomJID = chat.getRoom();
        final String roomName = tabTitle;
        isActive = false;
        EventQueue.invokeLater( () -> {
            ConferenceUtils.joinConferenceOnSeperateThread( roomName, roomJID, password );
            closeChatRoom();
        } );
    }

    /**
     * Is called whenever Spark was unexpectedly disconnected.
     */
    private void handleDisconnect()
    {
        getChatInputEditor().setEnabled( false );
        getSendButton().setEnabled( false );
        SparkManager.getChatManager().getChatContainer().fireChatRoomStateUpdated( this );
    }

    /**
     * Returns the Color to use. Use Color.blue for yourself
     */
    public Color getColor( String nickname )
    {
        if ( nickname.equals( this.getNickname() ) )
        {
            return ChatManager.TO_COLOR;
        }
        else
        {
            if ( pref.isMucRandomColors() )
            {
                int index = 0;
                for ( int i = 0; i < nickname.length(); i++ )
                {
                    index += nickname.charAt( i ) * i;
                }

                return ChatManager.COLORS[ index % ChatManager.COLORS.length ];
            }
            else
            {
                return ChatManager.FROM_COLOR;
            }
        }
    }

    public void notifySettingsAccessRight()
    {
        if ( SparkManager.getUserManager().isOwner( (GroupChatRoom) getChatRoom(), chat.getNickname() ) )
        {
            settings.setVisible( true );
        }
    }

    public boolean isChatStatEnabled()
    {
        return chatStatEnabled;
    }

    public void setChatStatEnabled( boolean chatStatEnabled )
    {
        this.chatStatEnabled = chatStatEnabled;
    }

    @Override
    protected void sendChatState(ChatState state) throws SmackException.NotConnectedException
    {
        if (!chatStatEnabled || !SparkManager.getConnection().isConnected() )
        {
            return;
        }

        // XEP-0085: SHOULD NOT send 'gone' in a MUC.
        if ( state == ChatState.gone )
        {
            return;
        }

        for ( final String occupant : chat.getOccupants() )
        {
            final String occupantNickname = XmppStringUtils.parseResource(occupant);
            final String myNickname = chat.getNickname();
            if (occupantNickname != null && !occupantNickname.equals(myNickname))
            {
                SparkManager.getMessageEventManager().sendComposingNotification(occupant, "djn");
            }
        }
    }
}
