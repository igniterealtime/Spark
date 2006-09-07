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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Date;

public class PresenceChangePlugin implements Plugin {

    private final Set contacts = new HashSet();


    public void initialize() {
        // Listen for right-clicks on ContactItem
        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        final Action listenAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ContactItem item = (ContactItem)contactList.getSelectedUsers().iterator().next();
                contacts.add(item);
            }
        };

        listenAction.putValue(Action.NAME, Res.getString("menuitem.alert.when.online"));
        listenAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_ALARM_CLOCK));

        final Action removeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ContactItem item = (ContactItem)contactList.getSelectedUsers().iterator().next();
                contacts.remove(item);
            }
        };

        removeAction.putValue(Action.NAME, Res.getString("menuitem.remove.alert.when.online"));
        removeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));


        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                if (object instanceof ContactItem) {
                    ContactItem item = (ContactItem)object;
                    if (item.getPresence() == null || (item.getPresence().getMode() != Presence.Mode.available && item.getPresence().getMode() != Presence.Mode.chat)) {
                        if (contacts.contains(item)) {
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
                if (presence == null || (presence.getMode() != Presence.Mode.available && presence.getMode() != Presence.Mode.chat)) {
                    return;
                }
                String from = presence.getFrom();

                final Iterator contactItems = new ArrayList(contacts).iterator();
                while (contactItems.hasNext()) {
                    ContactItem item = (ContactItem)contactItems.next();
                    if (item.getFullJID().equals(StringUtils.parseBareAddress(from))) {
                        contacts.remove(item);

                        ChatManager chatManager = SparkManager.getChatManager();
                        ChatRoom chatRoom = chatManager.createChatRoom(item.getFullJID(), item.getNickname(), item.getNickname());


                        String time = SparkManager.DATE_SECOND_FORMATTER.format(new Date());

                        String infoText = Res.getString("message.user.now.available.to.chat", item.getNickname(), time);
                        chatRoom.getTranscriptWindow().insertNotificationMessage(infoText);
                        Message message = new Message();
                        message.setFrom(item.getFullJID());
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
