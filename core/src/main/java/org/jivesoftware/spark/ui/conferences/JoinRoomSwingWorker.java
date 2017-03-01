package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.RequestFocusListener;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An executable that makes the user join a room.
 *
 * When no password is provided, and the room to be joined is determined to be password protected, a password dialog
 * will be presented.
 *
 * The user will join the room using the nickname as saved in the local preferences, unless explicitly overridden in
 * the constructor.
 *
 * An optional 'follow-up' action can be registered. When such a follow-up is registered, it shall be executed,
 * asynchronously, after the room was successfully joined.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class JoinRoomSwingWorker extends SwingWorker
{
    private final List<String> errors = new ArrayList<>();
    private final String roomJID;
    private String nickname;
    private String password;
    private final String tabTitle;

    private MultiUserChat groupChat;

    private SwingWorker followUp;

    public JoinRoomSwingWorker( String roomJID )
    {
        this( roomJID, null, null, XmppStringUtils.parseLocalpart( roomJID ) );
    }

    public JoinRoomSwingWorker( String roomJID, String password, String tabTitle )
    {
        this( roomJID, null, password, tabTitle );
    }

    public JoinRoomSwingWorker( String roomJID, String nickname, String password, String tabTitle )
    {
        this.roomJID = roomJID;
        this.nickname = nickname;
        this.password = password;
        this.tabTitle = tabTitle;
    }

    /**
     * Set a follow-up action, to be executed upon successful join of the room.
     *
     * Not that a follow-up must be set, before this instance of JoinRoomSwingWorker is started.
     *
     * @param followUp a follow-up action (can be null).
     */
    public void setFollowUp( SwingWorker followUp )
    {
        this.followUp = followUp;
    }

    @Override
    public Object construct()
    {
        try
        {
            groupChat = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getMultiUserChat( roomJID );

            // Create a UI component, if one was not yet created. It is important that this happens before the MUC is
            // joined server-side, as the UI component needs to be able to display data that is sent by the server upon
            // joining the room.
            ChatRoom room;
            try
            {
                final String roomName = XmppStringUtils.parseBareJid( groupChat.getRoom() );
                room = SparkManager.getChatManager().getChatContainer().getChatRoom( roomName );
            }
            catch ( ChatRoomNotFoundException e )
            {
                room = UIComponentRegistry.createGroupChatRoom( groupChat );
                ((GroupChatRoom) room).setPassword( password );
                ((GroupChatRoom) room).setTabTitle( tabTitle );
            }

            // Use the default nickname, if none has been provided.
            if ( !ModelUtil.hasLength( nickname ) )
            {
                nickname = SettingsManager.getRelodLocalPreferences().getNickname().trim();
            }

            // Join the MUC server-sided, if we're not already in.
            if ( !groupChat.isJoined() )
            {
                if ( password == null && ConferenceUtils.isPasswordRequired( roomJID ) )
                {
                    JLabel label = new JLabel(Res.getString("message.enter.room.password"));
                    JPasswordField passwordField = new JPasswordField();
                    passwordField.addAncestorListener(new RequestFocusListener());
                    JOptionPane.showConfirmDialog(null, new Object[]{label, passwordField}, Res.getString("title.password.required"), JOptionPane.OK_CANCEL_OPTION);
                    password = new String(passwordField.getPassword());

                    if ( !ModelUtil.hasLength( password ) )
                    {
                        return null;
                    }
                }

                if ( !ConferenceUtils.confirmToRevealVisibility() )
                {
                    return null;
                }

                if ( ModelUtil.hasLength( password ) )
                {
                    groupChat.join( nickname, password );
                }
                else
                {
                    groupChat.join( nickname );
                }
            }
            return room;
        }
        catch ( XMPPException | SmackException ex )
        {
            Log.error( "An exception occurred while trying to join room '" + roomJID + "'.", ex );
            XMPPError error = null;
            if ( ex instanceof XMPPException.XMPPErrorException )
            {
                error = ( (XMPPException.XMPPErrorException) ex ).getXMPPError();

                if ( XMPPError.Condition.conflict.equals( error.getCondition() ) )
                {
                    final Object userInput = JOptionPane.showInputDialog(
                            SparkManager.getMainWindow(),
                            Res.getString( "message.nickname.in.use" ),
                            Res.getString( "title.change.nickname" ),
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            null, // null selection values implies text field.
                            nickname
                            );

                    if ( userInput != null )
                    {
                        Log.debug( "Retry joining room '" + roomJID + "', using nickname: " + userInput );
                        this.nickname = (String) userInput;
                        return construct();
                    }
                }
            }

            final String errorText = ConferenceUtils.getReason( error );
            errors.add( errorText );
            return null;
        }
    }

    @Override
    public void finished()
    {
        UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );

        if ( errors.size() > 0 )
        {
            String error = errors.get( 0 );
            JOptionPane.showMessageDialog( SparkManager.getMainWindow(), error, Res.getString("message.error.unable.join.room"), JOptionPane.ERROR_MESSAGE );
        }
        else if ( groupChat.isJoined() && getValue() != null )
        {
            final ChatRoom room = (ChatRoom) getValue();
            ConferenceUtils.changePresenceToAvailableIfInvisible();
            final ChatContainer container = SparkManager.getChatManager().getChatContainer();
            if ( !container.getChatRooms().contains( room ) )
            {
                container.addChatRoom( room );
            }
            container.activateChatRoom( room );

            if ( followUp != null )
            {
                followUp.start();
            }
        }
        else
        {
            JOptionPane.showMessageDialog( SparkManager.getMainWindow(), Res.getString("message.error.unable.join.room"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE );
        }
    }
}
