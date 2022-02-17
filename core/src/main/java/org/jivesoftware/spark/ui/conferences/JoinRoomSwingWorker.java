package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.StanzaError;
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
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    private final EntityBareJid roomJID;
    private Resourcepart nickname;
    private String password;
    private final String tabTitle;

    private MultiUserChat groupChat;

    private SwingWorker followUp;

    public JoinRoomSwingWorker( EntityBareJid roomJID )
    {
        this( roomJID, null, null, roomJID.getLocalpart().toString() );
    }

    public JoinRoomSwingWorker( EntityBareJid roomJID, String password, String tabTitle )
    {
        this( roomJID, null, password, tabTitle );
    }

    public JoinRoomSwingWorker( EntityBareJid roomJID, Resourcepart nickname, String password, String tabTitle )
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
        // A SwingWorker by definition is to be used to offload operations from the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must NOT be called on the Event Dispatcher Thread (but was)");
        }

        try
        {
            Log.debug("Joining chat room " + roomJID);
            groupChat = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getMultiUserChat( roomJID );
            Log.debug("... got groupchat for " + roomJID);
            boolean passwordRequired = ConferenceUtils.isPasswordRequired( roomJID );
            Log.debug("... password required for " + roomJID + ": " + passwordRequired);

            // Use the default nickname, if none has been provided.
            if ( nickname == null )
            {
                nickname = SettingsManager.getRelodLocalPreferences().getNickname();
            }

            AtomicReference<ChatRoom> roomUIObject = new AtomicReference<>();
            EventQueue.invokeAndWait(() -> {
                // Create a UI component, if one was not yet created. It is important that this happens before the MUC is
                // joined server-side, as the UI component needs to be able to display data that is sent by the server upon
                // joining the room.
                ChatRoom room;
                try {
                    room = SparkManager.getChatManager().getChatContainer().getChatRoom(groupChat.getRoom());
                } catch (ChatRoomNotFoundException e) {
                    room = UIComponentRegistry.createGroupChatRoom(groupChat);
                    ((GroupChatRoom) room).setPassword(password);
                    ((GroupChatRoom) room).setTabTitle(tabTitle);
                }
                roomUIObject.set(room);
            });
            Log.debug("... created UI object for " + roomJID);

            if ( !groupChat.isJoined() ) {
                // Join the MUC server-sided, if we're not already in.
                if (password == null && passwordRequired) {

                    EventQueue.invokeAndWait(() -> {
                        JLabel label = new JLabel(Res.getString("message.enter.room.password"));
                        JPasswordField passwordField = new JPasswordField();
                        passwordField.addAncestorListener(new RequestFocusListener());
                        JOptionPane.showConfirmDialog(null, new Object[]{label, passwordField}, Res.getString("title.password.required"), JOptionPane.OK_CANCEL_OPTION);
                        password = new String(passwordField.getPassword());
                    });

                    if (!ModelUtil.hasLength(password)) {
                        return null;
                    }
                }

                AtomicBoolean wontJoin = new AtomicBoolean(false);
                EventQueue.invokeAndWait(() -> {
                    if (!ConferenceUtils.confirmToRevealVisibility()) {
                        wontJoin.set(true);
                    }
                });
                if (wontJoin.get()) {
                    return null;
                }
            }

            if ( !groupChat.isJoined() ) {
                Log.debug("Start server-sided join of chat room " + roomJID);
                if ( ModelUtil.hasLength( password ) )
                {
                    groupChat.join( nickname, password );
                }
                else
                {
                    groupChat.join( nickname );
                }
                Log.debug("Joined chat room " + roomJID + " on the server.");
            }

            return roomUIObject.get();
        }
        catch ( XMPPException | SmackException | InterruptedException | InvocationTargetException ex )
        {
            Log.error( "An exception occurred while trying to join room '" + roomJID + "'.", ex );
            StanzaError error = null;
            if ( ex instanceof XMPPException.XMPPErrorException )
            {
                error = ( (XMPPException.XMPPErrorException) ex ).getStanzaError();
                AtomicReference<Object> retryAttemptResult = new AtomicReference<>();
                if ( StanzaError.Condition.conflict.equals( error.getCondition() ) )
                {
                    try {
                        EventQueue.invokeAndWait(() -> {
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
                                try {
                                    this.nickname = Resourcepart.from((String) userInput);
                                } catch (XmppStringprepException e) {
                                    throw new IllegalStateException(e);
                                }
                                retryAttemptResult.set(construct());
                            }
                        });
                    } catch (InterruptedException | InvocationTargetException e) {
                        Log.error( "An exception occurred while trying to join room '" + roomJID + "' in the retry attempt. Giving up.", ex );
                    }
                    return retryAttemptResult;
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
