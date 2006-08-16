/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.log.Log;

/**
 * Uses the Windows registry to perform URI XMPP mappings.
 *
 * @author Derek DeMoro
 */
public class SparkStartupListener implements com.install4j.api.launcher.StartupNotification.Listener {

    public void startupPerformed(String string) {
        if (string.indexOf("xmpp") == -1) {
            return;
        }

        if (string.indexOf("?message") != -1) {
            try {
                handleJID(string);
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
        else if (string.indexOf("?join") != -1) {
            try {
                handleConference(string);
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
        else if (string.indexOf("?") == -1) {
            // Then use the direct jid
            int index = string.indexOf(":");
            if (index != -1) {
                String jid = string.substring(index + 1);

                UserManager userManager = SparkManager.getUserManager();
                String nickname = userManager.getUserNicknameFromJID(jid);
                if (nickname == null) {
                    nickname = jid;
                }

                ChatManager chatManager = SparkManager.getChatManager();
                ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname, nickname);
                chatManager.getChatContainer().activateChatRoom(chatRoom);
            }
        }

    }

    /**
     * Factory method to handle different types of URI Mappings.
     *
     * @param uriMapping the uri mapping string.
     * @throws Exception thrown if an exception occurs.
     */
    public void handleJID(String uriMapping) throws Exception {
        int index = uriMapping.indexOf("xmpp:");
        int messageIndex = uriMapping.indexOf("?message");

        int bodyIndex = uriMapping.indexOf("body=");

        String jid = uriMapping.substring(index + 5, messageIndex);
        String body = null;

        // Find body
        if (bodyIndex != -1) {
            body = uriMapping.substring(bodyIndex + 5);
        }

        UserManager userManager = SparkManager.getUserManager();
        String nickname = userManager.getUserNicknameFromJID(jid);
        if (nickname == null) {
            nickname = jid;
        }

        ChatManager chatManager = SparkManager.getChatManager();
        ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname, nickname);
        if (body != null) {
            Message message = new Message();
            message.setBody(body);
            chatRoom.insertMessage(message);
            chatRoom.sendMessage(message);
        }

        chatManager.getChatContainer().activateChatRoom(chatRoom);
    }

    /**
     * Handles the URI Mapping to join a conference room.
     *
     * @param uriMapping the uri mapping.
     * @throws Exception thrown if the conference cannot be joined.
     */
    public void handleConference(String uriMapping) throws Exception {
        int index = uriMapping.indexOf("xmpp:");
        int join = uriMapping.indexOf("?join");

        String conference = uriMapping.substring(index + 5, join);
        ConferenceUtils.autoJoinConferenceRoom(conference, conference, null);
    }
}
