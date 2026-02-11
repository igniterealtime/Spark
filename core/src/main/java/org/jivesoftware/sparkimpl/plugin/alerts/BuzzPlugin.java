/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.attention.packet.AttentionExtension;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.spark.Event;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SoundManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;

import java.util.TimerTask;

import javax.swing.SwingUtilities;

import static org.jivesoftware.spark.ChatManager.NOTIFICATION_COLOR;

/**
 * Allows users to shake a user's frame to attract their attention and play a buzz sound.
 * It's kind of "call" to get an immediate answer.
 * Such a notification may be shown even in DND status.
 * A user may disable the buzz feature in the settings, but anyway it will see the buzz as a regular message.
 * @see <a href="https://xmpp.org/extensions/xep-0224.html">XEP-0224 Attention</a>.
 */
public class BuzzPlugin implements Plugin {

    @Override
    public void initialize() {
        // Add Attention to a discovered items list.
        SparkManager.addFeature(AttentionExtension.NAMESPACE);
        SparkManager.getConnection()
            .addAsyncStanzaListener(
                stanza -> SwingUtilities.invokeLater(() -> shakeWindow((Message) stanza)),
                new AndFilter(StanzaTypeFilter.MESSAGE, s -> s.hasExtension(AttentionExtension.ELEMENT_NAME, AttentionExtension.NAMESPACE))
            );

        SparkManager.getChatManager().addChatRoomListener(
            new ChatRoomListener() {
                @Override
                public void chatRoomOpened(final ChatRoom room) {
                    TimerTask task = new SwingTimerTask() {
                        @Override
                        public void doRun() {
                            addBuzzFeatureToChatRoom(room);
                        }
                    };

                    TaskEngine.getInstance().schedule(task, 100);
                }

                @Override
                public void chatRoomLeft(ChatRoom room) {
                }

                @Override
                public void chatRoomClosed(ChatRoom room) {
                }

                @Override
                public void chatRoomActivated(ChatRoom room) {
                }

                @Override
                public void userHasJoined(ChatRoom room, String userid) {
                }

                @Override
                public void userHasLeft(ChatRoom room, String userid) {
                }
            });
    }

    private void addBuzzFeatureToChatRoom(final ChatRoom room) {
        if (room instanceof ChatRoomImpl) {
            boolean hasAttentionSupport = clientOfContactSupportsAttentions(room);
            if (!hasAttentionSupport) {
                return;
            }
            // Add the button to the toolbar
            new BuzzRoomDecorator(room);
        }
    }

    /**
     * Determine via service discovery if the contact's client supports attentions
     */
    private static boolean clientOfContactSupportsAttentions(ChatRoom room) {
        EntityFullJid fullJID = PresenceManager.getFullyQualifiedJID(room.getBareJid());
        boolean hasAttentionSupport = true;
        ServiceDiscoveryManager discoManager = SparkManager.getDiscoManager();
        try {
            DiscoverInfo discoverInfo = discoManager.discoverInfo(fullJID);
            hasAttentionSupport = discoverInfo.containsFeature(AttentionExtension.NAMESPACE);
        } catch (Exception e) {
            Log.warning(e.getMessage());
        }
        return hasAttentionSupport;
    }

    private void shakeWindow(Message message) {
        EntityBareJid bareJID = message.getFrom().asEntityBareJidOrThrow();
        String nickname;
        ContactItem contact = SparkManager.getWorkspace().getContactList().getContactItemByJID(bareJID);
        if (contact != null) {
            nickname = contact.getDisplayName();
        } else {
            nickname = bareJID.getLocalpart().asUnescapedString();
        }

        ChatContainer chatContainer = SparkManager.getChatManager().getChatContainer();
        ChatRoom room;
        try {
            room = chatContainer.getChatRoom(bareJID);
        } catch (ChatRoomNotFoundException e) {
            // Create the room if it does not exist.
            room = SparkManager.getChatManager().createChatRoom(bareJID,
                nickname, nickname);
        }

        ChatFrame chatFrame = chatContainer.getChatFrame();
        if (chatFrame != null) {
            if (SettingsManager.getLocalPreferences().isBuzzEnabled()) {
                chatFrame.buzz();
                chatContainer.activateChatRoom(room);
//TODO                SparkManager.getSoundManager().playClip(Event.ATTENTION_BUZZ);
            }
        }

        // Insert offline message
        room.getTranscriptWindow().insertNotificationMessage(
            Res.getString("message.buzz.message", nickname),
            NOTIFICATION_COLOR);
        room.scrollToBottom();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean canShutDown() {
        return true;
    }

    @Override
    public void uninstall() {
    }
}
