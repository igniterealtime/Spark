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
package org.jivesoftware.fastpath.workspace.invite;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.smack.SmackException;
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
        String msg = messageText != null ? StringUtils.escapeForXML(messageText).toString() : FpRes.getString("message.please.join.me.in.conference");
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
        catch (XMPPException | SmackException e) {
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
        String msg = messageText != null ? StringUtils.escapeForXML(messageText).toString() : FpRes.getString("message.please.join.me.in.conference");
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
        catch (XMPPException | SmackException e) {
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
        String msg = messageText != null ? StringUtils.escapeForXML(messageText).toString() : FpRes.getString("message.please.join.me.in.conference");
        try {
            if (!transfer) {
            	// TODO : CHECK FASHPATH
                FastpathPlugin.getAgentSession().sendRoomInvitation(RoomInvitation.Type.workgroup, jid, sessionID, msg);
            }
            else {
                FastpathPlugin.getAgentSession().sendRoomTransfer(RoomTransfer.Type.workgroup, jid, sessionID, msg);
            }
        }
        catch (XMPPException | SmackException e) {
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
