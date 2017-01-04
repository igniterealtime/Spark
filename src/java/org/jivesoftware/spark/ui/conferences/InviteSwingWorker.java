package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.util.XmppStringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An executable that causes invitations to be sent, asking JIDs to join a chat room.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class InviteSwingWorker extends SwingWorker
{
    private final String roomJID;
    private final Set<String> invitees;
    private final String invitation;

    public InviteSwingWorker( String roomJID, Set<String> invitees, String invitation )
    {
        this.roomJID = roomJID;
        this.invitees = ( invitees == null ? Collections.emptySet() : invitees);
        this.invitation = invitation;
    }

    @Override
    public Object construct()
    {
        final Set<String> invitedJIDs = new HashSet<>();

        final MultiUserChat groupChat = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getMultiUserChat( roomJID );

        // Send invitations
        for ( final String jid : invitees)
        {
            try
            {
                groupChat.invite(jid, invitation);
                invitedJIDs.add( jid );
            }
            catch ( SmackException.NotConnectedException e )
            {
                Log.warning( "Unable to invite " + jid + " to " + roomJID, e );
            }
        }

        return invitedJIDs;
    }

    @Override
    public void finished()
    {
        final String roomName = XmppStringUtils.parseBareJid( roomJID );
        try
        {
            final ChatRoom room = SparkManager.getChatManager().getChatContainer().getChatRoom( roomName );
            final TranscriptWindow transcriptWindow = room.getTranscriptWindow();
            for ( final String jid : (Set<String>) getValue() )
            {
                final String notification = Res.getString( "message.waiting.for.user.to.join", jid );
                transcriptWindow.insertNotificationMessage( notification, ChatManager.NOTIFICATION_COLOR );
            }
        }
        catch ( ChatRoomNotFoundException e )
        {
            Log.error( "Unable to identify chat room tab by name: " + roomName );
        }
    }
}
