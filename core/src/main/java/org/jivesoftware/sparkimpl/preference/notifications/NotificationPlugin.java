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

package org.jivesoftware.sparkimpl.preference.notifications;

import java.awt.Color;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

/**
 * Adds a simple notification system to alert users to presence changes.
 *
 * @author Derek DeMoro
 */
public class NotificationPlugin implements Plugin, StanzaListener {

    private final Set<BareJid> onlineUsers = new HashSet<>();
    private LocalPreferences preferences;


    @Override
	public void initialize() {
        // Add the preferences
        NotificationsPreference notifications = new NotificationsPreference();
        SparkManager.getPreferenceManager().addPreference(notifications);
        notifications.load();

        final TimerTask registerTask = new SwingTimerTask() {
            @Override
			public void doRun() {
                registerListener();
            }
        };

        TaskEngine.getInstance().schedule(registerTask, 5000);
    }

    private void registerListener() {
        preferences = SettingsManager.getLocalPreferences();

        // Iterate through all online users and add them to the list.
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        for (ContactGroup contactGroup : contactList.getContactGroups()) {
            for (ContactItem item : contactGroup.getContactItems()) {
                if (item != null && item.getJid() != null && item.getPresence().isAvailable()) {
                    BareJid bareJID = item.getJid().asBareJid();
                    onlineUsers.add(bareJID);
                }
            }
        }

        // Add Presence Listener
        SparkManager.getConnection().addAsyncStanzaListener(this, new StanzaTypeFilter(Presence.class));
    }


    @Override
    public void processStanza(Stanza stanza) {
        final Presence presence = (Presence)stanza;
        Jid jid = presence.getFrom();
        if (jid == null) {
            return;
        }

        // Make sure the user is in the contact list.
        ContactItem contactItem = SparkManager.getWorkspace().getContactList().getContactItemByJID(jid);
        if (contactItem == null) {
            return;
        }

        BareJid bareJid = jid.asBareJid();
        boolean isOnline = onlineUsers.contains(bareJid);

        if (presence.isAvailable()) {
            if (preferences.isOnlineNotificationsOn()) {
                if (!isOnline) {
                    notifyUserOnline(bareJid, presence);
                }
            }

            onlineUsers.add(bareJid);
        }
        else {
            if (preferences.isOfflineNotificationsOn() && isOnline) {
                notifyUserOffline(bareJid, presence);
            }

            onlineUsers.remove(bareJid);
        }
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
        SparkManager.getConnection().removeAsyncStanzaListener(this);
    }

    /**
     * Notify client that a user has come online.
     *
     * @param jid the jid of the user that has come online.
     * @param presence Presence of the online user.
     */
    private void notifyUserOnline(final BareJid jid, final Presence presence) {
   	 try {
   		 EventQueue.invokeAndWait( () -> {
                SparkToaster toaster = new SparkToaster();
                toaster.setDisplayTime(preferences.getNotificationsDisplayTime());
                toaster.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
                toaster.setCustomAction(new ChatAction(jid));
                NotificationAlertUI alertUI = new NotificationAlertUI(jid, true, presence);

                toaster.setToasterHeight((int)alertUI.getPreferredSize().getHeight() + 40);

                int width = (int)alertUI.getPreferredSize().getWidth() + 40;
                if (width < 300) {
                    width = 300;
                }

                toaster.setToasterWidth(width);

               toaster.showToaster(alertUI.topLabel.getText(), alertUI);
               toaster.setTitleAlert(new Font("Dialog", Font.BOLD, 13), presence);
            } );
   	 }
   	 catch(Exception ex) {
   		Log.error(ex); 
   	 }
    }

    /**
     * Notify client that a user has gone offline.
     *
     * @param jid the jid of the user who has gone offline.
     * @param presence of the offline user.
     */
    private void notifyUserOffline(final BareJid jid, final Presence presence) {
   	 try {
   		 EventQueue.invokeAndWait( () -> {
                SparkToaster toaster = new SparkToaster();
                toaster.setCustomAction(new ChatAction(jid));
                toaster.setDisplayTime(preferences.getNotificationsDisplayTime());
                toaster.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));

                NotificationAlertUI alertUI = new NotificationAlertUI(jid, false, presence);

                toaster.setToasterHeight((int)alertUI.getPreferredSize().getHeight() + 40);

                int width = (int)alertUI.getPreferredSize().getWidth() + 40;
                if (width < 300) {
                    width = 300;
                }

                toaster.setToasterWidth(width);

                toaster.showToaster(alertUI.topLabel.getText(), alertUI);
                toaster.setTitleAlert(new Font("Dialog", Font.BOLD, 13), presence);
            } );
   	 }
   	 catch(Exception ex) {
   		Log.error(ex); 
   	 }
    }

    private static class ChatAction extends AbstractAction {

	private static final long serialVersionUID = 4752515615833181939L;
	private final BareJid jid;

        public ChatAction(BareJid jid) {
            this.jid = jid;
        }

        @Override
		public void actionPerformed(ActionEvent e) {
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
            SparkManager.getChatManager().activateChat(jid, nickname);
        }
    }


}
