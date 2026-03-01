/**
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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


package org.jivesoftware.spark.plugin.fileupload;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.GlobalMessageListener;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.EntityJid;

import java.util.*;


public class SparkFileUploadPlugin implements Plugin, ChatRoomListener, GlobalMessageListener {
    private HttpFileUploadManager httpFileUploadManager;
    private ChatManager chatManager;
    private final Map<EntityJid, ChatRoomDecorator> decorators = new HashMap<>();

    @Override
    public void initialize() {
        //TODO Use our cert manager httpFileUploadManager.setTlsContext()
        AbstractXMPPConnection connection = SparkManager.getConnection();
        httpFileUploadManager = HttpFileUploadManager.getInstanceFor(connection);
        if (!httpFileUploadManager.isUploadServiceDiscovered()) {
            Log.warning("HTTP File Upload service not discovered.");
            httpFileUploadManager = null;
            return;
        }
        chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(this);
        chatManager.addGlobalMessageListener(this);
    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {
    }

    @Override
    public void messageSent(ChatRoom room, Message message) {
    }

    @Override
    public void shutdown() {
        if (httpFileUploadManager == null) {
            return;
        }
        try {
            chatManager.removeChatRoomListener(this);
            chatManager.removeGlobalMessageListener(this);
            chatManager = null;
            httpFileUploadManager = null;
        } catch (Exception e) {
            Log.warning("shutdown ", e);
        }
    }

    @Override
    public boolean canShutDown() {
        return true;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void chatRoomLeft(ChatRoom chatroom) {
    }

    @Override
    public void chatRoomClosed(ChatRoom chatroom) {
        if (httpFileUploadManager == null) {
            return;
        }
        EntityJid roomId = chatroom.getJid();
        Log.debug("chatRoomClosed:  " + roomId);
        if (decorators.containsKey(roomId)) {
            ChatRoomDecorator decorator = decorators.remove(roomId);
            decorator.finished();
        }
    }

    @Override
    public void chatRoomActivated(ChatRoom chatroom) {
    }

    @Override
    public void userHasJoined(ChatRoom room, String s) {
    }

    @Override
    public void userHasLeft(ChatRoom room, String s) {
    }

    @Override
    public void chatRoomOpened(final ChatRoom room) {
        if (httpFileUploadManager == null) {
            return;
        }
        EntityJid roomId = room.getJid();
        Log.debug("chatRoomOpened:  " + roomId);
        if (!decorators.containsKey(roomId)) {
            decorators.put(roomId, new ChatRoomDecorator(httpFileUploadManager, room));
        }
    }

}
