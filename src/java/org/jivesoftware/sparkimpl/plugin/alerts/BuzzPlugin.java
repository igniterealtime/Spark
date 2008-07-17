/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
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

import java.util.TimerTask;

import javax.swing.SwingUtilities;

/**
 *
 */
public class BuzzPlugin implements Plugin {


    public void initialize() {
        ProviderManager.getInstance().addExtensionProvider("buzz", "http://www.jivesoftware.com/spark", BuzzPacket.class);

        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                if (packet instanceof Message) {
                    final Message message = (Message)packet;
                    if (message.getExtension("buzz", "http://www.jivesoftware.com/spark") != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                shakeWindow(message);
                            }
                        });
                    }
                }
            }
        }, new PacketTypeFilter(Message.class));


        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListener() {
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
        if (!SettingsManager.getLocalPreferences().isBuzzEnabled()) {
            return;
        }
        String bareJID = StringUtils.parseBareAddress(message.getFrom());
        ContactItem contact = SparkManager.getWorkspace().getContactList().getContactItemByJID(bareJID);
        String nickname = StringUtils.parseName(bareJID);
        if (contact != null) {
            nickname = contact.getDisplayName();
        }

        ChatRoom room;
        try {
            room = SparkManager.getChatManager().getChatContainer().getChatRoom(bareJID);
        }
        catch (ChatRoomNotFoundException e) {
            // Create the room if it does not exist.
            room = SparkManager.getChatManager().createChatRoom(bareJID, nickname, nickname);
        }

        ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
        if (chatFrame != null && chatFrame.isVisible()) {
            chatFrame.buzz();
            SparkManager.getChatManager().getChatContainer().activateChatRoom(room);
        }

        // Insert offline message
        room.getTranscriptWindow().insertNotificationMessage("BUZZ", ChatManager.NOTIFICATION_COLOR);
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
