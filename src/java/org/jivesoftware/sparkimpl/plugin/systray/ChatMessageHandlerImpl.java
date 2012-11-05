package org.jivesoftware.sparkimpl.plugin.systray;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ChatMessageHandler;

public class ChatMessageHandlerImpl implements ChatMessageHandler {
	private int unreadMessages;
	@Override
	public void messageReceived(Message message) {
		unreadMessages ++;		
	}
	public int getUnreadMessages() {
		return unreadMessages;
	}
	public void clearUnreadMessages() {
		unreadMessages = 0;
	}
}
