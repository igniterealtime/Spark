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
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class BuzzPlugin implements Plugin {


    public void initialize() {
        ProviderManager.getInstance().addExtensionProvider("buzz", "http://www.jivesoftware.com/spark", BuzzPacket.class);

        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                if (packet instanceof Message) {
                    Message message = (Message)packet;
                    if (message.getExtension("buzz", "http://www.jivesoftware.com/spark") != null) {
                        String bareJID = StringUtils.parseBareAddress(message.getFrom());
                        ContactItem contact = SparkManager.getWorkspace().getContactList().getContactItemByJID(bareJID);
                        String nickname = StringUtils.parseName(bareJID);
                        if (contact != null) {
                            nickname = contact.getNickname();
                        }

                        ChatRoom room = null;
                        try {
                            room = SparkManager.getChatManager().getChatContainer().getChatRoom(bareJID);
                        }
                        catch (ChatRoomNotFoundException e) {
                            // Create the room if it does not exist.
                            room = SparkManager.getChatManager().createChatRoom(bareJID, nickname, nickname);
                        }

                        // Insert offline message
                        room.getTranscriptWindow().insertNotificationMessage("Buzz");

                        ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
                        if (chatFrame != null && chatFrame.isVisible()) {
                            chatFrame.buzz();
                        }
                    }
                }
            }
        }, new PacketTypeFilter(Message.class));


        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListener() {
            public void chatRoomOpened(final ChatRoom room) {
                if (room instanceof ChatRoomImpl) {
                    // Add Button to toolbar
                    final RolloverButton chatRoomButton = new RolloverButton("Buzz");
                    chatRoomButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            final String jid = ((ChatRoomImpl)room).getParticipantJID();
                            Message message = new Message();
                            message.setTo(jid);
                            message.addExtension(new BuzzPacket());
                            SparkManager.getConnection().sendPacket(message);

                            room.getTranscriptWindow().insertNotificationMessage("BUZZ!");
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                public void run() {
                                    chatRoomButton.setEnabled(true);
                                }
                            }, 30000);

                            chatRoomButton.setEnabled(false);
                        }
                    });
                    room.getEditorBar().add(chatRoomButton);
                }

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

    public void shutdown() {
    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
    }
}
