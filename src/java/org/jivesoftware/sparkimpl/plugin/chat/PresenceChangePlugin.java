/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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
package org.jivesoftware.sparkimpl.plugin.chat;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
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
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * Allows users to place activity listeners on individual users. This class notifies users when other users
 * come from away or offline to available.
 *
 * @author Derek DeMoro
 */
public class PresenceChangePlugin implements Plugin {

    private final Set<String> sparkContacts = new HashSet<String>();
    private LocalPreferences localPref = SettingsManager.getLocalPreferences(); 

    public void initialize() {
        // Listen for right-clicks on ContactItem
        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        final Action listenAction = new AbstractAction() {
	    private static final long serialVersionUID = 7705539667621148816L;

	    public void actionPerformed(ActionEvent e) {
		
		for (ContactItem item : contactList.getSelectedUsers()) {
		    String bareAddress = StringUtils.parseBareAddress(item
			    .getJID());
		    sparkContacts.add(bareAddress);
		}
            }
        };

        listenAction.putValue(Action.NAME, Res.getString("menuitem.alert.when.online"));
        listenAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_ALARM_CLOCK));

	final Action removeAction = new AbstractAction() {
	    private static final long serialVersionUID = -8726129089417116105L;

	    public void actionPerformed(ActionEvent e) {

		for (ContactItem item : contactList.getSelectedUsers()) {
		    String bareAddress = StringUtils.parseBareAddress(item
			    .getJID());
		    sparkContacts.remove(bareAddress);
		}

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
	    public void processPacket(final Packet packet) {
		try {
		    EventQueue.invokeAndWait(new Runnable() {
			public void run() {
			    Presence presence = (Presence) packet;
			    if (!presence.isAvailable() || presence.isAway()) {
				return;
			    }
			    String from = presence.getFrom();

			    ArrayList<String> removelater = new ArrayList<String>();
			    
			    for (final String jid : sparkContacts) {
				if (jid.equals(StringUtils
					.parseBareAddress(from))) {
				    removelater.add(jid);
				    // sparkContacts.remove(jid);

				    String nickname = SparkManager
					    .getUserManager()
					    .getUserNicknameFromJID(jid);
				    String time = SparkManager.DATE_SECOND_FORMATTER
					    .format(new Date());
				    String infoText = Res
					    .getString(
						    "message.user.now.available.to.chat",
						    nickname, time);

				    if (localPref.getShowToasterPopup()) {
					SparkToaster toaster = new SparkToaster();
					toaster.setDisplayTime(5000);
					toaster.setBorder(BorderFactory
						.createBevelBorder(0));

					toaster.setToasterHeight(150);
					toaster.setToasterWidth(200);

					toaster.setTitle(nickname);
					toaster.showToaster(null, infoText);

					toaster.setCustomAction(new AbstractAction() {
					    private static final long serialVersionUID = 4827542713848133369L;

					    @Override
					    public void actionPerformed(
						    ActionEvent e) {
						SparkManager.getChatManager()
							.getChatRoom(jid);
					    }
					});
				    } 
				   
				    ChatRoom room = SparkManager.getChatManager().getChatRoom(jid);
				    
				    if (localPref.getWindowTakesFocus())
				    {
					SparkManager.getChatManager().activateChat(jid, nickname);
				    }
				   
				    room.getTranscriptWindow().insertNotificationMessage(infoText, ChatManager.NOTIFICATION_COLOR);
				    
				}
			    }
			    for(String s : removelater){
				sparkContacts.remove(s);
			    }
			}
		    });
		} catch (Exception ex) {
		    ex.printStackTrace();
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

    public void addWatch(String user){
	String bareAddress = StringUtils.parseBareAddress(user);
	sparkContacts.add(bareAddress);
    }

    public void removeWatch(String user){
	String bareAddress = StringUtils.parseBareAddress(user);
	sparkContacts.remove(bareAddress);
    }

    public boolean getWatched(String user)
    {
	String bareAddress = StringUtils.parseBareAddress(user);
	return sparkContacts.contains(bareAddress)
;    }

}
