/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.notifications;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * Adds a simple notification system to alert users to presence changes.
 *
 * @author Derek DeMoro
 */
public class NotificationPlugin implements Plugin, PacketListener {

    private List<String> onlineUsers = new ArrayList<String>();
    private LocalPreferences preferences;


    public void initialize() {
        // Add the preferences
        NotificationsPreference notifications = new NotificationsPreference();
        SparkManager.getPreferenceManager().addPreference(notifications);
        notifications.load();

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }

            public void finished() {
                registerListener();
            }

        };

        worker.start();
    }

    private void registerListener() {
        preferences = SettingsManager.getLocalPreferences();

        // Iterate through all online users and add them to the list.
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        for (ContactGroup contactGroup : contactList.getContactGroups()) {
            for (ContactItem item : contactGroup.getContactItems()) {
                if (item != null && item.getJID() != null && item.getPresence() != null) {
                    String bareJID = StringUtils.parseBareAddress(item.getJID());
                    onlineUsers.add(bareJID);
                }
            }
        }

        // Add Presence Listener
        SparkManager.getConnection().addPacketListener(this, new PacketTypeFilter(Presence.class));
    }


    public void processPacket(Packet packet) {
        final Presence presence = (Presence)packet;
        String jid = presence.getFrom();
        if (jid == null) {
            return;
        }

        jid = StringUtils.parseBareAddress(jid);

        if (presence.getType().equals(Presence.Type.available)) {
            boolean isOnline = onlineUsers.contains(jid);
            if (preferences.isOnlineNotificationsOn()) {
                if (!isOnline) {
                    notifyUserOnline(jid);
                }
            }
            onlineUsers.add(jid);
        }
        else if (presence.getType().equals(Presence.Type.unavailable)) {
            if (preferences.isOfflineNotificationsOn()) {
                notifyUserOffline(jid);
            }

            onlineUsers.remove(jid);
        }
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
        SparkManager.getConnection().removePacketListener(this);
    }

    /**
     * Notify client that a user has come online.
     *
     * @param jid the jid of the user that has come online.
     */
    private void notifyUserOnline(String jid) {
        SparkToaster toaster = new SparkToaster();
        toaster.setDisplayTime(5000);
        toaster.setBorder(BorderFactory.createBevelBorder(0));
        toaster.setCustomAction(new ChatAction(jid));
        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);

        ContactItem item = SparkManager.getWorkspace().getContactList().getContactItemByJID(jid);
        if (item != null) {
            final JLabel label = new JLabel("<html><body><table width=100% cellpadding=0 cellspacing=0><tr><td align=center>" + nickname + "<br>"+Res.getString("user.has.signed.in")+"</td></tr></table></body></html>");
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            toaster.showToaster(Res.getString("title.notification"), label);
        }
    }

    /**
     * Notify client that a user has gone offline.
     *
     * @param jid the jid of the user who has gone offline.
     */
    private void notifyUserOffline(String jid) {
        SparkToaster toaster = new SparkToaster();
        toaster.setDisplayTime(5000);
        toaster.setBorder(BorderFactory.createBevelBorder(0));
        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
        ContactItem item = SparkManager.getWorkspace().getContactList().getContactItemByJID(jid);
        if (item != null) {
            final JLabel label = new JLabel("<html><body><table width=100% cellpadding=0 cellspacing=0><tr><td align=center>" + nickname + "<br>"+Res.getString("user.has.signed.off")+"</td></tr></table></body></html>");
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            toaster.showToaster(Res.getString("title.notification"), label);
        }
    }

    private class ChatAction extends AbstractAction {

        private String jid;

        public ChatAction(String jid) {
            this.jid = jid;
        }

        public void actionPerformed(ActionEvent e) {
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
            SparkManager.getChatManager().activateChat(jid, nickname);
        }
    }


}
