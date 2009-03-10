/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date:  $
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.fastpath.workspace.invite;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.workgroup.packet.RoomInvitation;
import org.jivesoftware.smackx.workgroup.packet.RoomTransfer;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.log.Log;

public class InvitationManager {
    private static List<ChatRoom> inviteListeners = new ArrayList<ChatRoom>();

    private InvitationManager() {

    }

    /**
     * Invite a user to a chat room.
     *
     * @param chatRoom    the <code>ChatRoom</code> to invite or transfer.
     * @param sessionID   the sessionID of this Fastpath session.
     * @param jid         the jid of the room.
     * @param messageText the message to send to the user.
     * @param transfer    true if this is a transfer.
     */
    public static void transferOrInviteUser(ChatRoom chatRoom, String workgroup, String sessionID, final String jid, String messageText, final boolean transfer) {
        messageText = StringUtils.escapeForXML(messageText);


        String msg = messageText != null ? messageText : FpRes.getString("message.please.join.me.in.conference");
        try {
            if (!transfer) {
            	// TODO : CHECK FASHPATH
                FastpathPlugin.getAgentSession().sendRoomInvitation(RoomInvitation.Type.user, jid, sessionID, msg);
            }
            else {
            	// TODO : CHECK FASHPATH
                FastpathPlugin.getAgentSession().sendRoomTransfer(RoomTransfer.Type.user, jid, sessionID, msg);
            }
        }
        catch (XMPPException e) {
            Log.error(e);
        }


        String username = SparkManager.getUserManager().getUserNicknameFromJID(jid);

        String notification = FpRes.getString("message.user.has.been.invited", username);
        if (transfer) {
            notification = FpRes.getString("message.waiting.for.user", username);
        }
        chatRoom.getTranscriptWindow().insertNotificationMessage(notification, ChatManager.NOTIFICATION_COLOR);
    }

    /**
     * Invite or transfer a queue.
     *
     * @param chatRoom    the <code>ChatRoom</code> to invite or transfer.
     * @param sessionID   the sessionID of this Fastpath session.
     * @param jid         the jid of the room.
     * @param messageText the message to send to the user.
     * @param transfer    true if this is a transfer.
     */
    public static void transferOrInviteToQueue(ChatRoom chatRoom, String workgroup, String sessionID, final String jid, String messageText, final boolean transfer) {
        messageText = StringUtils.escapeForXML(messageText);


        String msg = messageText != null ? messageText : FpRes.getString("message.please.join.me.in.conference");
        try {
            if (!transfer) {
            	// TODO : CHECK FASHPATH
                FastpathPlugin.getAgentSession().sendRoomInvitation(RoomInvitation.Type.queue, jid, sessionID, msg);
            }
            else {
            	// TODO : CHECK FASHPATH
                FastpathPlugin.getAgentSession().sendRoomTransfer(RoomTransfer.Type.queue, jid, sessionID, msg);
            }
        }
        catch (XMPPException e) {
            Log.error(e);
        }


        String username = SparkManager.getUserManager().getUserNicknameFromJID(jid);

        String notification = FpRes.getString("message.user.has.been.invited", username);
        if (transfer) {
            notification = FpRes.getString("message.waiting.for.user", username);
        }
        chatRoom.getTranscriptWindow().insertNotificationMessage(notification, ChatManager.NOTIFICATION_COLOR);
    }

    /**
     * Invite or transfer a queue.
     *
     * @param chatRoom    the <code>ChatRoom</code> to invite or transfer.
     * @param sessionID   the sessionID of this Fastpath session.
     * @param jid         the jid of the room.
     * @param messageText the message to send to the user.
     * @param transfer    true if this is a transfer.
     */
    public static void transferOrInviteToWorkgroup(ChatRoom chatRoom, String workgroup, String sessionID, final String jid, String messageText, final boolean transfer) {
        messageText = StringUtils.escapeForXML(messageText);


        String msg = messageText != null ? messageText : FpRes.getString("message.please.join.me.in.conference");
        try {
            if (!transfer) {
            	// TODO : CHECK FASHPATH
                FastpathPlugin.getAgentSession().sendRoomInvitation(RoomInvitation.Type.workgroup, jid, sessionID, msg);
            }
            else {
                FastpathPlugin.getAgentSession().sendRoomTransfer(RoomTransfer.Type.workgroup, jid, sessionID, msg);
            }
        }
        catch (XMPPException e) {
            Log.error(e);
        }


        String username = SparkManager.getUserManager().getUserNicknameFromJID(jid);

        String notification = FpRes.getString("message.user.has.been.invited", username);
        if (transfer) {
            notification = FpRes.getString("message.waiting.for.user", username);
        }
        chatRoom.getTranscriptWindow().insertNotificationMessage(notification, ChatManager.NOTIFICATION_COLOR);
    }


}
