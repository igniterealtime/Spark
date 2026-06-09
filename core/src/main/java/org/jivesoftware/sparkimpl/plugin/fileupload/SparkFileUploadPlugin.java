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


package org.jivesoftware.sparkimpl.plugin.fileupload;

import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.EntityJid;

import java.util.HashMap;
import java.util.Map;


/**
 * Http File Upload Plugin.
 * Allows users to share files by uploading to a server (via XEP-0363).
 *
 * @author Dele Olajide
 */
public class SparkFileUploadPlugin implements Plugin, ChatRoomListener {
    private HttpFileUploadManager httpFileUploadManager;
    private final Map<EntityJid, ChatRoomDecorator> decorators = new HashMap<>();

    @Override
    public void initialize() {
        //TODO Use our cert manager httpFileUploadManager.setTlsContext()
        httpFileUploadManager = HttpFileUploadManager.getInstanceFor(SparkManager.getConnection());
        if (!httpFileUploadManager.isUploadServiceDiscovered()) {
            Log.warning("HTTP File Upload service not discovered.");
            httpFileUploadManager = null;
            return;
        }
        ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(this);
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void chatRoomClosed(ChatRoom chatroom) {
        if (httpFileUploadManager == null) {
            return;
        }
        EntityJid roomId = chatroom.getJid();
        ChatRoomDecorator decorator = decorators.remove(roomId);
        if (decorator != null) {
            decorator.finished();
        }
    }

    @Override
    public void chatRoomOpened(final ChatRoom room) {
        if (httpFileUploadManager == null) {
            return;
        }
        EntityJid roomId = room.getJid();
        if (!decorators.containsKey(roomId)) {
            decorators.put(roomId, new ChatRoomDecorator(httpFileUploadManager, room));
        }
    }

}
