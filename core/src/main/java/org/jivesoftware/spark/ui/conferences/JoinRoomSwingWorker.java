package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.util.Consumer;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
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
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
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
    private final LocalPreferences pref = SettingsManager.getLocalPreferences();

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
            MultiUserChatManager mucManager = SparkManager.getMucManager();
            groupChat = mucManager.getMultiUserChat( roomJID );
            Log.debug("... got groupchat for " + roomJID);
            boolean passwordRequired = ConferenceUtils.isPasswordRequired( roomJID );
            Log.debug("... password required for " + roomJID + ": " + passwordRequired);

            // Use the default nickname, if none has been provided.
            if ( nickname == null )
            {
                nickname = pref.getNickname();
            }

            AtomicReference<ChatRoom> roomUIObject = new AtomicReference<>();
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
                EventQueue.invokeLater(() -> {
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
                groupChat.join(getMucEnterConfiguration(groupChat, nickname, password));
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

    public static MucEnterConfiguration getMucEnterConfiguration(final MultiUserChat groupChat, final Resourcepart nickname) {
        return getMucEnterConfiguration(groupChat, nickname, null);
    }

    public static MucEnterConfiguration getMucEnterConfiguration(final MultiUserChat groupChat, final Resourcepart nickname, final String password)
    {
        final MucEnterConfiguration.Builder builder = groupChat.getEnterConfigurationBuilder(nickname);
        if ( ModelUtil.hasLength( password ) )
        {
            builder.withPassword(password);
        }

        if (SparkManager.getVCardManager().getVCard() != null && SparkManager.getVCardManager().getVCard().getAvatarHash() != null) {
            final String hash = SparkManager.getVCardManager().getVCard().getAvatarHash();
            builder.withPresence( presenceBuilder -> presenceBuilder
                .addExtension(
                    StandardExtensionElement.builder("x", "vcard-temp:x:update")
                        .addElement("photo", hash)
                        .build()
                )
                .addExtension(
                    StandardExtensionElement.builder("x", "jabber:x:avatar")
                        .addElement("hash", hash)
                        .build()
                )
            );
        }
        return builder.build();
    }

    @Override
    public void finished()
    {
        UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );

        if ( errors.size() > 0 )
        {
            String error = errors.get( 0 );
            final String style = "width: 300px;";
            String body = "<html><body><p style='"+style+"'>"+ roomJID + ": " + Res.getString("message.error.unable.join.room") +"</p><p></p><p style='"+style+"'>" + error + "</p></body></html>";
            JOptionPane.showMessageDialog( SparkManager.getMainWindow(), body, Res.getString("message.error.unable.join.room"), JOptionPane.ERROR_MESSAGE );
        }
        else if ( groupChat.isJoined() && getValue() != null )
        {
            Log.debug("Activating chat room that we just joined");
            final ChatRoom room = (ChatRoom) getValue();
            ConferenceUtils.changePresenceToAvailableIfInvisible();
            final ChatContainer container = SparkManager.getChatManager().getChatContainer();
            if ( !container.getChatRooms().contains( room ) )
            {
                container.addChatRoom( room );
            }
            container.activateChatRoom( room );
            Log.debug("Activated chat room that we just joined");

            if ( followUp != null )
            {
                Log.debug("Starting chat room join follow-up");
                followUp.start();
            }
        }
        else
        {
            JOptionPane.showMessageDialog( SparkManager.getMainWindow(), Res.getString("message.error.unable.join.room"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE );
        }
    }
}
