package org.jivesoftware.spellchecker;

import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;


public class SpellcheckChatRoomListener implements ChatRoomListener 
{
	
	public SpellcheckChatRoomListener()
	{
	}
	
	public void chatRoomActivated(ChatRoom room) {
		
	}

	public void chatRoomClosed(ChatRoom room) {
		
	}

	public void chatRoomLeft(ChatRoom room) {
		
	}

	public void chatRoomOpened(ChatRoom room) {
		new SpellcheckChatRoomDecorator(room);
	}

	public void userHasJoined(ChatRoom room, String userid) {
		
	}

	public void userHasLeft(ChatRoom room, String userid) {
		
	}

}
