/**
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

package org.jivesoftware.sparkimpl.preference.sounds;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.MessageListener;
import org.jivesoftware.spark.util.TaskEngine;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

import static org.jivesoftware.spark.Event.*;

/**
 * Sounds Plugin.
 * Adds sound preferences for Spark.
 *
 * @author Derek DeMoro
 */
public class SoundPlugin implements Plugin {

    @Override
    public void initialize() {
        SoundPreference soundPreference = new SoundPreference();
        SparkManager.getPreferenceManager().addPreference(soundPreference);
        // Load sound preferences.
        TaskEngine.getInstance().submit(soundPreference::loadFromFile);

        EntityBareJid myBareJid = SparkManager.getSessionManager().getUserBareAddress();
        MessageListener messageListener = new MessageListener() {
            @Override
            public void messageReceived(ChatRoom room, Message message) {
                // Do not play sounds on history updates.
                if (message.hasExtension(DelayInformation.class)) {
                    return;
                }
                // Ignore own messages
                if (message.getFrom() == null || message.getFrom().asBareJid().equals(myBareJid)) {
                    return;
                }
                SparkManager.getSoundManager().playClip(MSG_INCOMING);
            }

            @Override
            public void messageSent(ChatRoom room, Message message) {
                SparkManager.getSoundManager().playClip(MSG_OUTCOMING);
            }
        };
        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListener() {
            @Override
            public void chatRoomOpened(ChatRoom room) {
                room.addMessageListener(messageListener);
            }

            @Override
            public void chatRoomClosed(ChatRoom room) {
                room.removeMessageListener(messageListener);
            }
        });

        SparkManager.getRoster().addPresenceEventListener(new PresenceEventListener() {
            @Override
            public void presenceAvailable(FullJid from, Presence presence) {
            }

            @Override
            public void presenceUnavailable(FullJid from, Presence presence) {
                // When the shutdown is ongoing ignore all the events
                if (!SparkManager.getConnection().isConnected()) {
                    return;
                }
                BareJid presenceBareJid = from.asBareJid();
                // Ignore own presence updates
                if (presenceBareJid.equals(myBareJid)) {
                    return;
                }
                SparkManager.getSoundManager().playClip(STATUS_OFFLINE);
            }

            @Override
            public void presenceError(Jid address, Presence errorPresence) {
            }

            @Override
            public void presenceSubscribed(BareJid address, Presence subscribedPresence) {
            }

            @Override
            public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
            }
        });

        MultiUserChatManager mucManager = SparkManager.getMucManager();
        mucManager.addInvitationListener( ( xmppConnection, muc, inviter, reason, password, message, invitation ) -> {
            SparkManager.getSoundManager().playClip(INCOMING_INVITATION);
        } );
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
        // Do nothing.
    }
}
