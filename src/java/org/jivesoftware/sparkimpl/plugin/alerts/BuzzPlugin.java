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
package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

import java.util.TimerTask;

import javax.swing.SwingUtilities;

/**
 *
 */
public class BuzzPlugin implements Plugin {

    private static final String ELEMENTNAME = "attention";
    private static final String NAMESPACE = "urn:xmpp:attention:0";

    private static final String ELEMENTNAME_OLD = "buzz";
    private static final String NAMESPACE_OLD = "http://www.jivesoftware.com/spark";

    public void initialize() {
	ProviderManager.addExtensionProvider(ELEMENTNAME,
		NAMESPACE, new BuzzPacket.Provider());

	ProviderManager.addExtensionProvider(ELEMENTNAME_OLD,
		NAMESPACE_OLD, new BuzzPacket.Provider() );

	SparkManager.getConnection().addAsyncStanzaListener( stanza -> {
    if (stanza instanceof Message) {
        final Message message = (Message) stanza;

        boolean buzz = message.getExtension(ELEMENTNAME_OLD,
            NAMESPACE_OLD) != null
            || message.getExtension(ELEMENTNAME, NAMESPACE) != null;
        if (buzz) {
        SwingUtilities.invokeLater( () -> shakeWindow(message) );
        }
    }
    }, new StanzaTypeFilter(Message.class));

	SparkManager.getChatManager().addChatRoomListener(
		new ChatRoomListener() {
		    public void chatRoomOpened(final ChatRoom room) {
			TimerTask task = new SwingTimerTask() {
			    public void doRun() {
				addBuzzFeatureToChatRoom(room);
			    }
			};

			TaskEngine.getInstance().schedule(task, 100);
		    }

		    public void chatRoomLeft(ChatRoom room) {
		    }

		    public void chatRoomClosed(ChatRoom room) {
		    }

		    public void chatRoomActivated(ChatRoom room) {
		    }

		    public void userHasJoined(ChatRoom room, String userid) {
		    }

		    public void userHasLeft(ChatRoom room, String userid) {
		    }
		});
    }

    private void addBuzzFeatureToChatRoom(final ChatRoom room) {
	if (room instanceof ChatRoomImpl) {
	    // Add Button to toolbar
	    if (!SettingsManager.getLocalPreferences().isBuzzEnabled()) {
		return;
	    }

	    new BuzzRoomDecorator(room);
	}

    }

    private void shakeWindow(Message message) {

	String bareJID = XmppStringUtils.parseBareJid(message.getFrom());
	ContactItem contact = SparkManager.getWorkspace().getContactList()
		.getContactItemByJID(bareJID);
	String nickname = XmppStringUtils.parseLocalpart(bareJID);
	if (contact != null) {
	    nickname = contact.getDisplayName();
	}

	ChatRoom room;
	try {
	    room = SparkManager.getChatManager().getChatContainer()
		    .getChatRoom(bareJID);
	} catch (ChatRoomNotFoundException e) {
	    // Create the room if it does not exist.
	    room = SparkManager.getChatManager().createChatRoom(bareJID,
		    nickname, nickname);
	}

	ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer()
		.getChatFrame();
	if (chatFrame != null) {
	    if (SettingsManager.getLocalPreferences().isBuzzEnabled()) {
		chatFrame.buzz();
		SparkManager.getChatManager().getChatContainer()
			.activateChatRoom(room);
	    }
	}

	// Insert offline message
	room.getTranscriptWindow().insertNotificationMessage(
		Res.getString("message.buzz.message", nickname),
		ChatManager.NOTIFICATION_COLOR);
	room.scrollToBottom();
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
	return true;
    }

    public void uninstall() {
    }
}
