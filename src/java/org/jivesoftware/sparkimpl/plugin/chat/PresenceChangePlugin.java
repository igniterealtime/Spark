/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.chat;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

/**
 * Allows users to place activity listeners on individual users. This class notifies users when other users
 * come from away or offline to available.
 *
 * @author Derek DeMoro
 */
public class PresenceChangePlugin implements Plugin {

    private final Set<String> sparkContacts = new HashSet<String>();

    public void initialize() {
        // Listen for right-clicks on ContactItem
        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        final Action listenAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ContactItem item = contactList.getSelectedUsers().iterator().next();
                String bareAddress = StringUtils.parseBareAddress(item.getJID());
                sparkContacts.add(bareAddress);
            }
        };

        listenAction.putValue(Action.NAME, Res.getString("menuitem.alert.when.online"));
        listenAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_ALARM_CLOCK));

        final Action removeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ContactItem item = contactList.getSelectedUsers().iterator().next();
                String bareAddress = StringUtils.parseBareAddress(item.getJID());
                sparkContacts.remove(bareAddress);
            }
        };

        removeAction.putValue(Action.NAME, Res.getString("menuitem.remove.alert.when.online"));
        removeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));


        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                if (object instanceof ContactItem) {
                    ContactItem item = (ContactItem)object;
                    String bareAddress = StringUtils.parseBareAddress(item.getJID());
                    if (!item.getPresence().isAvailable() || item.getPresence().isAway()) {
                        if (sparkContacts.contains(bareAddress)) {
                            popup.add(removeAction);
                        }
                        else {
                            popup.add(listenAction);
                        }
                    }
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        // Check presence changes
        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Presence presence = (Presence)packet;
                if (!presence.isAvailable() || presence.isAway()) {
                    return;
                }
                String from = presence.getFrom();

                for (String jid : sparkContacts) {
                    if (jid.equals(StringUtils.parseBareAddress(from))) {
                        sparkContacts.remove(jid);

                        ChatManager chatManager = SparkManager.getChatManager();
                        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
                        ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname, nickname);


                        String time = SparkManager.DATE_SECOND_FORMATTER.format(new Date());

                        String infoText = Res.getString("message.user.now.available.to.chat", nickname, time);
                        chatRoom.getTranscriptWindow().insertNotificationMessage(infoText, ChatManager.NOTIFICATION_COLOR);
                        Message message = new Message();
                        message.setFrom(jid);
                        message.setBody(infoText);
                        chatManager.getChatContainer().messageReceived(chatRoom, message);
                    }
                }

            }
        }, new PacketTypeFilter(Presence.class));
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
        // Do nothing.
    }
}
