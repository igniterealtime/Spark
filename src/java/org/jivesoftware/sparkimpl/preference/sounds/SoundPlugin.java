/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.sounds;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.MessageListener;
import org.jivesoftware.spark.util.TaskEngine;

import java.io.File;

public class SoundPlugin implements Plugin, MessageListener, ChatRoomListener {
    SoundPreference soundPreference;

    public void initialize() {
        soundPreference = new SoundPreference();
        SparkManager.getPreferenceManager().addPreference(soundPreference);

        SparkManager.getChatManager().addChatRoomListener(this);

        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Presence presence = (Presence)packet;
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
            }
        }, new PacketTypeFilter(Presence.class));

        // Load sound preferences.
        final Runnable soundLoader = new Runnable() {
            public void run() {
                soundPreference.loadFromFile();
            }
        };

        TaskEngine.getInstance().submit(soundLoader);

        MultiUserChat.addInvitationListener(SparkManager.getConnection(), new InvitationListener() {
            public void invitationReceived(XMPPConnection xmppConnection, String string, String string1, String string2, String string3, Message message) {
                SoundPreferences preferences = soundPreference.getPreferences();
                if (preferences != null && preferences.playIncomingInvitationSound()) {
                    String incomingSoundFile = preferences.getIncomingInvitationSoundFile();
                    File offlineFile = new File(incomingSoundFile);
                    SparkManager.getSoundManager().playClip(offlineFile);
                }
            }
        });

    }

    public void messageReceived(ChatRoom room, Message message) {

        // Do not play sounds on history updates.
        DelayInformation inf = (DelayInformation)message.getExtension("x", "jabber:x:delay");
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
