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

import java.io.File;

import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.MessageListener;
import org.jivesoftware.spark.util.TaskEngine;

public class SoundPlugin implements Plugin, MessageListener, ChatRoomListener {
    SoundPreference soundPreference;

    public void initialize() {
        soundPreference = new SoundPreference();
        SparkManager.getPreferenceManager().addPreference(soundPreference);

        SparkManager.getChatManager().addChatRoomListener(this);

        SparkManager.getConnection().addAsyncStanzaListener( stanza -> {
            Presence presence = (Presence)stanza;
            if (!presence.isAvailable()) {
                SoundPreferences preferences = soundPreference.getPreferences();
                if (preferences != null && preferences.isPlayOfflineSound()) {
                    if (!PresenceManager.isOnline(presence.getFrom())) {
                        String offline = preferences.getOfflineSound();
                        File offlineFile = new File(offline);
                        SparkManager.getSoundManager().playClip(offlineFile);
                    }
                }
            }
        }, new StanzaTypeFilter(Presence.class));

        // Load sound preferences.
        final Runnable soundLoader = () -> soundPreference.loadFromFile();

        TaskEngine.getInstance().submit(soundLoader);

        MultiUserChatManager.getInstanceFor(SparkManager.getConnection()).addInvitationListener( ( xmppConnection, muc, string1, string2, string3, message ) -> {
            SoundPreferences preferences = soundPreference.getPreferences();
            if (preferences != null && preferences.playIncomingInvitationSound()) {
                String incomingSoundFile = preferences.getIncomingInvitationSoundFile();
                File offlineFile = new File(incomingSoundFile);
                SparkManager.getSoundManager().playClip(offlineFile);
            }
        } );

    }

    public void messageReceived(ChatRoom room, Message message) {

        // Do not play sounds on history updates.
        DelayInformation inf = message.getExtension("delay", "urn:xmpp:delay");
        if (inf != null) {
            return;
        }

        SoundPreferences preferences = soundPreference.getPreferences();
        if (preferences.isPlayIncomingSound()) {
            File incomingFile = new File(preferences.getIncomingSound());
            SparkManager.getSoundManager().playClip(incomingFile);
        }
    }

    public void messageSent(ChatRoom room, Message message) {
        SoundPreferences preferences = soundPreference.getPreferences();
        if (preferences.isPlayOutgoingSound()) {
            File outgoingFile = new File(preferences.getOutgoingSound());
            SparkManager.getSoundManager().playClip(outgoingFile);
        }
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    public void chatRoomOpened(ChatRoom room) {
        room.addMessageListener(this);
    }

    public void chatRoomLeft(ChatRoom room) {

    }

    public void chatRoomClosed(ChatRoom room) {
        room.removeMessageListener(this);
    }

    public void chatRoomActivated(ChatRoom room) {

    }

    public void userHasJoined(ChatRoom room, String userid) {

    }

    public void userHasLeft(ChatRoom room, String userid) {

    }

    public void uninstall() {
        // Do nothing.
    }
}
