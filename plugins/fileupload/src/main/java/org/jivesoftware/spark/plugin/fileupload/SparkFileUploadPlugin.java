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

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.GlobalMessageListener;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.EntityBareJid;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.HashMap;
import java.util.Map;


public class SparkFileUploadPlugin implements Plugin, ChatRoomListener, GlobalMessageListener
{
    private org.jivesoftware.spark.ChatManager chatManager;
    private final Map<EntityBareJid, ChatRoomDecorator> decorators = new HashMap<>();

    public void initialize()
    {
        ProviderManager.addIQProvider("slot", UploadRequest.NAMESPACE, new UploadRequest.Provider());

        chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(this);
        chatManager.addGlobalMessageListener(this);
    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {

        try {
            String body = message.getBody();

            if ( (body.startsWith("https://") || body.startsWith("http://")) && body.contains("/httpfileupload/") )
            {
                Log.warning("http file upload get url " + message.getBody());
            }

        } catch (Exception e) {
            // i don't care
        }

    }

    @Override
    public void messageSent(ChatRoom room, Message message) {

    }

    public void shutdown()
    {
        try
        {
            Log.debug("shutdown");

            chatManager.removeChatRoomListener(this);
            chatManager.removeGlobalMessageListener(this);

            ProviderManager.removeIQProvider("slot", UploadRequest.NAMESPACE);

        }
        catch(Exception e)
        {
            Log.warning("shutdown ", e);
        }
    }

    public boolean canShutDown()
    {
        return true;
    }

    public void uninstall()
    {

    }

    public void chatRoomLeft(ChatRoom chatroom)
    {

    }

    public void chatRoomClosed(ChatRoom chatroom)
    {
        EntityBareJid roomId = chatroom.getBareJid();

        Log.debug("chatRoomClosed:  " + roomId);

        if (decorators.containsKey(roomId))
        {
            ChatRoomDecorator decorator = decorators.remove(roomId);
            decorator.finished();
        }
    }

    public void chatRoomActivated(ChatRoom chatroom)
    {
        EntityBareJid roomId = chatroom.getBareJid();

        Log.debug("chatRoomActivated:  " + roomId);
    }

    public void userHasJoined(ChatRoom room, String s)
    {
        EntityBareJid roomId = room.getBareJid();

        Log.debug("userHasJoined:  " + roomId + " " + s);
    }

    public void userHasLeft(ChatRoom room, String s)
    {
        EntityBareJid roomId = room.getBareJid();

        Log.debug("userHasLeft:  " + roomId + " " + s);
    }

    public void chatRoomOpened(final ChatRoom room)
    {
        EntityBareJid roomId = room.getBareJid();

        Log.debug("chatRoomOpened:  " + roomId);

        if (!decorators.containsKey(roomId))
        {
            decorators.put(roomId, new ChatRoomDecorator(room, this));
        }
    }

}
