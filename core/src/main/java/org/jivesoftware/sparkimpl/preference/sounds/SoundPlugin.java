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

import static org.jivesoftware.spark.Event.*;

/**
 * Sounds Plugin.
 * Adds sound preferences for Spark.
 *
 * @author Derek DeMoro
 */
public class SoundPlugin implements Plugin, MessageListener, ChatRoomListener {
    private SoundPreference soundPreference;

    @Override
	public void initialize() {
        soundPreference = new SoundPreference();
        SparkManager.getPreferenceManager().addPreference(soundPreference);

        SparkManager.getChatManager().addChatRoomListener(this);

        SparkManager.getConnection().addAsyncStanzaListener( stanza -> {
            Presence presence = (Presence)stanza;
            if (!presence.isAvailable()) {
                if (!PresenceManager.isOnline(presence.getFrom().asBareJid())) {
                    SparkManager.getSoundManager().playClip(STATUS_OFFLINE);
                }
            }
        }, new StanzaTypeFilter(Presence.class));

        // Load sound preferences.
        final Runnable soundLoader = () -> soundPreference.loadFromFile();

        TaskEngine.getInstance().submit(soundLoader);
        MultiUserChatManager mucManager = SparkManager.getMucManager();
        mucManager.addInvitationListener( ( xmppConnection, muc, inviter, reason, password, message, invitation ) -> {
            SparkManager.getSoundManager().playClip(INCOMING_INVITATION);
        } );
    }

    @Override
	public void messageReceived(ChatRoom room, Message message) {
        // Do not play sounds on history updates.
        if (message.hasExtension(DelayInformation.class)) {
            return;
        }
        SparkManager.getSoundManager().playClip(MSG_INCOMING);
    }

    @Override
	public void messageSent(ChatRoom room, Message message) {
        SparkManager.getSoundManager().playClip(MSG_OUTCOMING);
    }

    @Override
	public void shutdown() {

    }

    @Override
	public boolean canShutDown() {
        return false;
    }

    @Override
	public void chatRoomOpened(ChatRoom room) {
        room.addMessageListener(this);
    }

    @Override
	public void chatRoomLeft(ChatRoom room) {

    }

    @Override
	public void chatRoomClosed(ChatRoom room) {
        room.removeMessageListener(this);
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

    @Override
	public void uninstall() {
        // Do nothing.
    }
}
