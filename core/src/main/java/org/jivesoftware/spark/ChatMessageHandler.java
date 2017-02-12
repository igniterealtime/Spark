package org.jivesoftware.spark;

import org.jivesoftware.smack.packet.Message;

public interface ChatMessageHandler {
	void messageReceived(Message message);	
}
