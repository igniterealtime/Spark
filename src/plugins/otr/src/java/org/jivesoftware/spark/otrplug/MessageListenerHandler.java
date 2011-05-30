package org.jivesoftware.spark.otrplug;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.MessageEventListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

public class MessageListenerHandler extends ChatRoomListenerAdapter {

    final ChatManager chatManager = SparkManager.getChatManager();

    public MessageListenerHandler() {
        chatManager.addChatRoomListener(this);
    }

    @Override
    public void chatRoomOpened(ChatRoom room) {
        super.chatRoomOpened(room);
        if (room instanceof ChatRoomImpl) {
            OTRManager.getInstance().startOTRSession((ChatRoomImpl) room, ((ChatRoomImpl) room).getParticipantJID());
            
            
            ((ChatRoomImpl) room).addMessageEventListener(new MessageEventListener() {

                @Override
                public void sendingMessage(Message message) {
                   //System.out.println(message.getBody());

                }

                @Override
                public void receivingMessage(Message message) {
                   // System.out.println(message.getBody());

                }
            });

        }
    }

}
