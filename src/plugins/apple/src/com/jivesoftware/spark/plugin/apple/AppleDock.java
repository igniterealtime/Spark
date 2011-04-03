/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
package com.jivesoftware.spark.plugin.apple;

//import com.apple.cocoa.application.*;
//import com.apple.cocoa.foundation.NSSelector;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JFrame;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.PresenceListener;

import com.apple.eawt.Application;

/**
 * @author Wolf.Posdorfer
 */
public class AppleDock implements ActionListener, RosterListener, PresenceListener {
    //
    // private final NSMenu contactMenu;
    // private final Hashtable<String,NSMenuItem> entries;
    // private final NSStatusItem statusItem;
    // private final NSMenuItem freeToChatItem;
    // private final NSMenuItem availableItem;
    // private final NSMenuItem awayItem;
    // private final NSMenuItem extendedAwayItem;
    // private final NSMenuItem doNotDisturbItem;

    // private final JMenuBar _contactMenu;

    public AppleDock() {

	Application app = new Application();

	PopupMenu menu = new PopupMenu();

	PopupMenu statusmenu = new PopupMenu(Res.getString("menuitem.status"));

	for (Presence p : PresenceManager.getPresences()) {
	    MenuItem dd = new MenuItem(p.getStatus());
	    dd.addActionListener(this);
	    statusmenu.add(dd);
	}

	menu.add(statusmenu);

	JFrame frame = SparkManager.getMainWindow();
	frame.add(menu);

	// set dock menu
	app.setDockMenu(menu);

	SparkManager.getSessionManager().addPresenceListener(this);

    }

    public void display() {
	// we'll rather use the standard SystemTray provided by Spark
	// statusItem.setEnabled(true);
    }

    public void showBlackIcon() {
	// we'll rather use the standard SystemTray provided by Spark
	// statusItem.setImage(AppleUtils.getImage("/images/black-spark.gif"));
    }

    public void showActiveIcon() {
	// we'll rather use the standard SystemTray provided by Spark
	// statusItem.setImage(AppleUtils.getImage("/images/spark-16x16.png"));
    }

    /**
     * Called when NEW entries are added.
     * 
     * @param addresses
     *            the addressss added.
     */
    public void entriesAdded(final Collection addresses) {
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// Roster roster = SparkManager.getConnection().getRoster();
	// for (Object address : addresses) {
	// String jid = (String) address;
	// RosterEntry entry = roster.getEntry(jid);
	// addEntry(entry);
	// }
	// }
	// });
    }

    public void entriesUpdated(final Collection addresses) {
    }

    public void entriesDeleted(final Collection addresses) {
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// Roster roster = SparkManager.getConnection().getRoster();
	// for (Object address : addresses) {
	// String jid = (String) address;
	// RosterEntry entry = roster.getEntry(jid);
	// removeEntry(entry);
	// }
	// }
	// });
    }

    public void presenceChanged(final String user) {
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// Roster roster = SparkManager.getConnection().getRoster();
	// Presence presence = roster.getPresence(user);
	//
	// if (Presence.Mode.away.equals(presence.getMode())) {
	// RosterEntry entry = roster.getEntry(user);
	// removeEntry(entry);
	// } else if (Presence.Mode.available.equals(presence.getMode())
	// || Presence.Mode.chat.equals(presence.getMode())) {
	// RosterEntry entry = roster.getEntry(user);
	// addEntry(entry);
	// }
	//
	// }
	// });
    }

    public void presenceChanged(final Presence presence) {
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// if (Presence.Mode.chat.equals(presence.getMode())) {
	// freeToChatItem.setState(NSCell.OnState);
	// availableItem.setState(NSCell.OffState);
	// awayItem.setState(NSCell.OffState);
	// extendedAwayItem.setState(NSCell.OffState);
	// doNotDisturbItem.setState(NSCell.OffState);
	//
	// }
	// else if (Presence.Mode.available.equals(presence.getMode())) {
	// freeToChatItem.setState(NSCell.OffState);
	// availableItem.setState(NSCell.OnState);
	// awayItem.setState(NSCell.OffState);
	// extendedAwayItem.setState(NSCell.OffState);
	// doNotDisturbItem.setState(NSCell.OffState);
	//
	// }
	// else if (Presence.Mode.away.equals(presence.getMode())) {
	// freeToChatItem.setState(NSCell.OffState);
	// availableItem.setState(NSCell.OffState);
	// awayItem.setState(NSCell.OnState);
	// extendedAwayItem.setState(NSCell.OffState);
	// doNotDisturbItem.setState(NSCell.OffState);
	// }
	// else if (Presence.Mode.xa.equals(presence.getMode())) {
	// freeToChatItem.setState(NSCell.OffState);
	// availableItem.setState(NSCell.OffState);
	// awayItem.setState(NSCell.OffState);
	// extendedAwayItem.setState(NSCell.OnState);
	// doNotDisturbItem.setState(NSCell.OffState);
	// }
	// else if (Presence.Mode.dnd.equals(presence.getMode())) {
	// freeToChatItem.setState(NSCell.OffState);
	// availableItem.setState(NSCell.OffState);
	// awayItem.setState(NSCell.OffState);
	// extendedAwayItem.setState(NSCell.OffState);
	// doNotDisturbItem.setState(NSCell.OnState);
	// }
	// }
	// });

    }

    public void createChatRoom(final String item) {
	//
	// Runnable runnable = new Runnable() {
	//
	// public void run() {
	//
	// String nickname = item.title();
	// String jid =
	// SparkManager.getUserManager().getJIDFromDisplayName(nickname);
	// if (jid != null) {
	// String bareJID = StringUtils.parseBareAddress(jid);
	// ChatManager chatManager = SparkManager.getChatManager();
	// ChatRoom chatRoom = chatManager.createChatRoom(bareJID, nickname,
	// nickname);
	// chatManager.getChatContainer().activateChatRoom(chatRoom);
	// ChatFrame frame = chatManager.getChatContainer().getChatFrame();
	// frame.setState(Frame.NORMAL);
	// frame.setVisible(true);
	// frame.toFront();
	// NSApplication.sharedApplication().activateIgnoringOtherApps(true);
	// }
	// else {
	// Log.error("Cannot create chat room, could not find jid for nickname "
	// + nickname);
	// }
	// }
	//
	// };
	//
	// SwingUtilities.invokeLater(runnable);
    }

    public void handleStatusChange(final String item) {
	//
	// String status = item.title();
	//
	// if (freeToChatItem.title().equals(status)) {
	// freeToChatItem.setState(NSCell.OnState);
	// availableItem.setState(NSCell.OffState);
	// awayItem.setState(NSCell.OffState);
	// extendedAwayItem.setState(NSCell.OffState);
	// doNotDisturbItem.setState(NSCell.OffState);
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// StatusItem si =
	// SparkManager.getWorkspace().getStatusBar().getStatusItem("Free To Chat");
	// SparkManager.getSessionManager().changePresence(si.getPresence());
	// }
	// });
	// }
	// else if (availableItem.title().equals(status)) {
	// freeToChatItem.setState(NSCell.OffState);
	// availableItem.setState(NSCell.OnState);
	// awayItem.setState(NSCell.OffState);
	// extendedAwayItem.setState(NSCell.OffState);
	// doNotDisturbItem.setState(NSCell.OffState);
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// StatusItem si =
	// SparkManager.getWorkspace().getStatusBar().getStatusItem("Online");
	// SparkManager.getSessionManager().changePresence(si.getPresence());
	// }
	// });
	//
	// }
	// else if (awayItem.title().equals(status)) {
	//
	// freeToChatItem.setState(NSCell.OffState);
	// availableItem.setState(NSCell.OffState);
	// awayItem.setState(NSCell.OnState);
	// extendedAwayItem.setState(NSCell.OffState);
	// doNotDisturbItem.setState(NSCell.OffState);
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// StatusItem si =
	// SparkManager.getWorkspace().getStatusBar().getStatusItem("Away");
	// SparkManager.getSessionManager().changePresence(si.getPresence());
	// }
	// });
	// }
	// else if (extendedAwayItem.title().equals(status)) {
	//
	// freeToChatItem.setState(NSCell.OffState);
	// availableItem.setState(NSCell.OffState);
	// awayItem.setState(NSCell.OffState);
	// extendedAwayItem.setState(NSCell.OnState);
	// doNotDisturbItem.setState(NSCell.OffState);
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// StatusItem si =
	// SparkManager.getWorkspace().getStatusBar().getStatusItem("Extended Away");
	// SparkManager.getSessionManager().changePresence(si.getPresence());
	// }
	// });
	// }
	// else if (doNotDisturbItem.title().equals(status)) {
	//
	// freeToChatItem.setState(NSCell.OffState);
	// availableItem.setState(NSCell.OffState);
	// awayItem.setState(NSCell.OffState);
	// extendedAwayItem.setState(NSCell.OffState);
	// doNotDisturbItem.setState(NSCell.OnState);
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// StatusItem si =
	// SparkManager.getWorkspace().getStatusBar().getStatusItem("Do Not Disturb");
	// SparkManager.getSessionManager().changePresence(si.getPresence());
	// }
	// });
	// }
    }

    private void populateMenu(Roster roster) {
	//
	// NSMenuItem item = new NSMenuItem();
	// item.setTitle("Available Contacts");
	// item.setEnabled(false);
	// contactMenu.addItem(item);
	//
	// for (RosterEntry entry : roster.getEntries()) {
	// final Presence p = roster.getPresence(entry.getUser());
	// if (p.isAvailable()) {
	// addEntry(entry);
	// }
	//
	// }

    }

    private void addEntry(RosterEntry entry) {
	// if (entry == null) {
	// return;
	// }
	// String nickname = entry.getName();
	// if (nickname == null) {
	// nickname = entry.getUser();
	// }
	//
	// // if there isn't already an entry add it
	// if (!entries.contains(nickname)) {
	// NSMenuItem menuItem = new NSMenuItem();
	// menuItem.setIndentationLevel(2);
	// menuItem.setEnabled(true);
	// menuItem.setTitle(nickname);
	// menuItem.setAction(new NSSelector("createChatRoom", new
	// Class[]{NSMenuItem.class}));
	// menuItem.setTarget(this);
	// contactMenu.addItem(menuItem);
	// entries.put(nickname, menuItem);
	// }
    }

    private void removeEntry(RosterEntry entry) {
	// if (entry == null) {
	// return;
	// }
	// String nickname = entry.getName();
	// NSMenuItem menuItem = entries.remove(nickname);
	// if (menuItem != null) {
	// contactMenu.removeItem(menuItem);
	// }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

	Presence presence = null;
	for (Presence p : PresenceManager.getPresences()) {
	    if (p.getStatus().equals(e.getActionCommand())) {
		presence = p;
		break;
	    }
	}

	SparkManager.getSessionManager().changePresence(presence);

    }

}
