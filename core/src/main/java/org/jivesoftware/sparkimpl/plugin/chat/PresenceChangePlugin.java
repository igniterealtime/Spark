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
package org.jivesoftware.sparkimpl.plugin.chat;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

/**
 * Presence Change Plugin.
 * Tracks users presenences during the runtime of Spark.
 * Allows users to place activity listeners on individual users.
 * This class notifies users when other users come from away or offline to available.
 *
 * @author Derek DeMoro
 */
public class PresenceChangePlugin implements Plugin {

    private final Set<BareJid> sparkContacts = new HashSet<>();
    private final LocalPreferences localPref = SettingsManager.getLocalPreferences();

    @Override
    public void initialize() {
        // Listen for right-clicks on ContactItem
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        Action listenAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ContactItem item : contactList.getSelectedUsers()) {
                    BareJid bareAddress = item.getJid().asBareJid();
                    sparkContacts.add(bareAddress);
                }
            }
        };

        listenAction.putValue(Action.NAME, Res.getString("menuitem.alert.when.online"));
        listenAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_ALARM_CLOCK));

        Action removeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ContactItem item : contactList.getSelectedUsers()) {
                    BareJid bareAddress = item.getJid().asBareJid();
                    sparkContacts.remove(bareAddress);
                }
            }
        };

        removeAction.putValue(Action.NAME, Res.getString("menuitem.remove.alert.when.online"));
        removeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        contactList.addContextMenuListener(new ContextMenuListener() {
            @Override
            public void poppingUp(Object object, JPopupMenu popup) {
                if (!(object instanceof ContactItem)) {
                    return;
                }
                ContactItem item = (ContactItem) object;
                BareJid bareAddress = item.getJid();
                // Add actions to the popup based on contact availability
                if (!item.getPresence().isAvailable() || item.getPresence().isAway()) {
                    if (sparkContacts.contains(bareAddress)) {
                        popup.add(removeAction);
                    } else {
                        popup.add(listenAction);
                    }
                }
            }

            @Override
            public void poppingDown(JPopupMenu popup) {
            }

            @Override
            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        // Check presence changes
        SparkManager.getConnection().addAsyncStanzaListener(stanza -> {
            try {
                Presence presence = (Presence) stanza;
                if (!presence.isAvailable() || presence.isAway()) {
                    return;
                }
                BareJid from = presence.getFrom().asBareJid();
                List<BareJid> removeLater = new ArrayList<>();
                for (BareJid jid : sparkContacts) {
                    if (!jid.equals(from)) {
                        continue;
                    }
                    removeLater.add(jid);
                    // sparkContacts.remove(jid);
                    String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
                    String time = SparkManager.DATE_SECOND_FORMATTER.format(new Date());
                    String infoText = Res.getString("message.user.now.available.to.chat", nickname, time);
                    if (localPref.getShowToasterPopup()) {
                        EventQueue.invokeLater(() ->
                        {
                            showToaster(nickname, infoText, jid.asEntityBareJidOrThrow());
                        });
                    }

                    ChatRoom room = SparkManager.getChatManager().getChatRoom(jid.asEntityBareJidOrThrow());
                    if (localPref.getWindowTakesFocus()) {
                        EventQueue.invokeLater(() -> SparkManager.getChatManager().activateChat(jid, nickname));
                    }
                    EventQueue.invokeLater(() -> room.getTranscriptWindow().insertNotificationMessage(infoText, ChatManager.NOTIFICATION_COLOR));
                }
                for (BareJid s : removeLater) {
                    sparkContacts.remove(s);
                }
            } catch (Exception ex) {
                Log.error(ex);
            }
        }, new StanzaTypeFilter(Presence.class));
    }

    /**
     * Show contact status change via popup notification
     */
    private static void showToaster(String nickname, String infoText, EntityBareJid chatRoomJid) {
        SparkToaster toaster = new SparkToaster();
        toaster.setDisplayTime(5000);
        toaster.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));

        toaster.setToasterHeight(150);
        toaster.setToasterWidth(200);

        toaster.setTitle(nickname);
        toaster.showToaster(null, infoText);

        toaster.setCustomAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SparkManager.getChatManager().getChatRoom(chatRoomJid);
            }
        });
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

    public void addWatch(Jid user) {
        BareJid bareAddress = user.asBareJid();
        sparkContacts.add(bareAddress);
    }

    public void removeWatch(Jid user) {
        BareJid bareAddress = user.asBareJid();
        sparkContacts.remove(bareAddress);
    }

    public boolean getWatched(Jid user) {
        BareJid bareAddress = user.asBareJid();
        return sparkContacts.contains(bareAddress);
    }

}
