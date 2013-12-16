/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2013 Jive Software. All rights reserved.
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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.UserStatusListener;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
/**
 * This listener is notified for every group chat room. The affected chat room
 * is sent as parameter in every method. Keeping such listener tied to a group chat room instance
 * is wrong because all listeners will be notified when someone joins a particular chat room for example
 */
public class GroupChatRoomListener implements ChatRoomListener {
    @Override
	public void chatRoomOpened(ChatRoom room) {
		if (!(room instanceof GroupChatRoom)) {
			return;
		}
		GroupChatRoom groupChatRoom = (GroupChatRoom)room;
		MultiUserChat chat = groupChatRoom.getMultiUserChat();
		chat.addUserStatusListener(new UserStatusListener() {
			public void kicked(String actor, String reason) {

			}

			public void voiceGranted() {

			}

			public void voiceRevoked() {

			}

			public void banned(String actor, String reason) {

			}

			public void membershipGranted() {

			}

			public void membershipRevoked() {

			}

			public void moderatorGranted() {

			}

			public void moderatorRevoked() {

			}

			public void ownershipGranted() {
			}

			public void ownershipRevoked() {

			}

			public void adminGranted() {

			}

			public void adminRevoked() {

			}
		});
	}

    @Override
	public void chatRoomLeft(ChatRoom room) {
		if (!(room instanceof GroupChatRoom)) {
			return;
		}
		GroupChatRoom groupChatRoom = (GroupChatRoom)room;
		groupChatRoom.getConferenceRoomInfo().getAgentInfoPanel().setVisible(false);		
	}

    @Override
	public void chatRoomClosed(ChatRoom room) {
		if (!(room instanceof GroupChatRoom)) {
			return;
		}
		GroupChatRoom groupChatRoom = (GroupChatRoom)room;
		MultiUserChat chat = groupChatRoom.getMultiUserChat();		
		chat.removeParticipantListener(groupChatRoom.getConferenceRoomInfo().getListener());		
	}

	@Override
	public void chatRoomActivated(ChatRoom room) {
		
	}

	@Override
	public void userHasJoined(ChatRoom room, String userid) {
		
	}

    @Override
	public void userHasLeft(ChatRoom room, String userid) {
		if (!(room instanceof GroupChatRoom)) {
			return;
		}
		GroupChatRoom groupChatRoom = (GroupChatRoom)room;

		int index = groupChatRoom.getConferenceRoomInfo().getIndex(userid);

		if (index != -1) {
			groupChatRoom.getConferenceRoomInfo().removeUser(userid);
			groupChatRoom.getConferenceRoomInfo().getUserMap().remove(userid);
		}
	}

}