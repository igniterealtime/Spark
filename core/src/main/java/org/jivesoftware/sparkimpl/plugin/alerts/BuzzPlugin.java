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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.attention.packet.AttentionExtension;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;

import java.util.List;

import javax.swing.SwingUtilities;

import static org.jivesoftware.spark.ChatManager.NOTIFICATION_COLOR;
import static org.jivesoftware.spark.Event.ATTENTION_BUZZ;

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
        // We always announce it, but if the buzz is disabled, we'll not play a sound.
        SparkManager.addFeature(AttentionExtension.NAMESPACE);
        SparkManager.getConnection().addAsyncStanzaListener(
            stanza -> SwingUtilities.invokeLater(() -> shakeWindow((Message) stanza)),
            new AndFilter(StanzaTypeFilter.MESSAGE, s -> s.hasExtension(AttentionExtension.QNAME))
        );

        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListener() {
            @Override
            public void chatRoomOpened(final ChatRoom room) {
                addBuzzFeatureToChatRoom(room);
            }
        });
    }

    private void addBuzzFeatureToChatRoom(final ChatRoom room) {
        if (!(room instanceof ChatRoomImpl)) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            // Add the button to the toolbar
            BuzzRoomDecorator buzzRoomDecorator = new BuzzRoomDecorator(room);
            SwingWorker worker = new SwingWorker() {
                @Override
                public Object construct() {
                    return clientOfContactSupportsAttentions(room);
                }

                @Override
                public void finished() {
                    boolean hasAttentionSupport = (boolean) get();
                    buzzRoomDecorator.setBuzzButtonEnabled(hasAttentionSupport);
                }
            };
            worker.start();
        });
    }

    /**
     * Determine via service discovery if the contact's client supports attentions
     */
    private static boolean clientOfContactSupportsAttentions(ChatRoom room) {
        ServiceDiscoveryManager discoManager = SparkManager.getDiscoManager();
        List<Presence> allPresences = SparkManager.getRoster().getAllPresences(room.getBareJid());
        for (Presence presence : allPresences) {
            EntityFullJid fullJID = presence.getFrom().asEntityFullJidIfPossible();
            if (fullJID == null) {
                continue;
            }
            try {
                DiscoverInfo discoverInfo = discoManager.discoverInfo(fullJID);
                boolean hasAttentionSupport = discoverInfo.containsFeature(AttentionExtension.NAMESPACE);
                if (hasAttentionSupport) {
                    return true;
                }
            } catch (Exception e) {
                Log.warning(e.getMessage());
            }
        }
        return false;
    }

    private void shakeWindow(Message message) {
        EntityBareJid bareJID = message.getFrom().asEntityBareJidOrThrow();
        ContactItem contact = SparkManager.getWorkspace().getContactList().getContactItemByJID(bareJID);
        String nickname = contact != null ? contact.getDisplayName() : bareJID.getLocalpart().asUnescapedString();

        ChatContainer chatContainer = SparkManager.getChatManager().getChatContainer();
        ChatRoom room;
        try {
            room = chatContainer.getChatRoom(bareJID);
        } catch (ChatRoomNotFoundException e) {
            // Create the room if it does not exist.
            room = SparkManager.getChatManager().createChatRoom(bareJID, nickname, nickname);
        }

        ChatFrame chatFrame = chatContainer.getChatFrame();
        if (chatFrame != null) {
            if (SettingsManager.getLocalPreferences().isBuzzEnabled()) {
                chatFrame.buzz();
                chatContainer.activateChatRoom(room);
                SparkManager.getSoundManager().playClip(ATTENTION_BUZZ);
            }
        }

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
