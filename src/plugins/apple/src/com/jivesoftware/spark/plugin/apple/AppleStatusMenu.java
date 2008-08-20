/**
 * Copyright (C) 1999-2004 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package com.jivesoftware.spark.plugin.apple;

import com.apple.cocoa.application.*;
import com.apple.cocoa.foundation.NSSelector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Frame;
import java.util.Collection;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

/**
 * @author Andrew Wright
 */
public class AppleStatusMenu implements RosterListener, PresenceListener {

    private final NSMenu contactMenu;
    private final Hashtable<String,NSMenuItem> entries;
    private final NSStatusItem statusItem;
    private final NSMenuItem freeToChatItem;
    private final NSMenuItem availableItem;
    private final NSMenuItem awayItem;
    private final NSMenuItem extendedAwayItem;
    private final NSMenuItem doNotDisturbItem;


    public AppleStatusMenu() {
        entries = new Hashtable<String,NSMenuItem>();

        Roster roster = SparkManager.getConnection().getRoster();
        roster.addRosterListener(this);

        this.contactMenu = new NSMenu();

        NSMenuItem item = new NSMenuItem();
        item.setTitle("My Status:");
        item.setEnabled(false);
        contactMenu.addItem(item);

        freeToChatItem = new NSMenuItem();
        freeToChatItem.setEnabled(true);
        freeToChatItem.setTitle("Free To Chat");
        freeToChatItem.setTarget(this);
        freeToChatItem.setAction(new NSSelector("handleStatusChange", new Class[]{NSMenuItem.class}));
        freeToChatItem.setImage(AppleUtils.getImage("/images/im_free_chat.png"));
        contactMenu.addItem(freeToChatItem);

        availableItem = new NSMenuItem();
        availableItem.setEnabled(true);
        availableItem.setTitle("Available");
        availableItem.setTarget(this);
        availableItem.setAction(new NSSelector("handleStatusChange", new Class[]{NSMenuItem.class}));
        availableItem.setImage(AppleUtils.getImage("/images/green-ball.png"));
        contactMenu.addItem(availableItem);

        awayItem = new NSMenuItem();
        awayItem.setEnabled(true);
        awayItem.setTitle("Away");
        awayItem.setTarget(this);
        awayItem.setAction(new NSSelector("handleStatusChange", new Class[]{NSMenuItem.class}));
        awayItem.setImage(AppleUtils.getImage("/images/im_away.png"));
        contactMenu.addItem(awayItem);

        extendedAwayItem = new NSMenuItem();
        extendedAwayItem.setEnabled(true);
        extendedAwayItem.setTitle("Extended Away");
        extendedAwayItem.setTarget(this);
        extendedAwayItem.setAction(new NSSelector("handleStatusChange", new Class[]{NSMenuItem.class}));
        extendedAwayItem.setImage(AppleUtils.getImage("/images/im_away.png"));
        contactMenu.addItem(extendedAwayItem);

        doNotDisturbItem = new NSMenuItem();
        doNotDisturbItem.setEnabled(true);
        doNotDisturbItem.setTitle("Do Not Disturb");
        doNotDisturbItem.setTarget(this);
        doNotDisturbItem.setAction(new NSSelector("handleStatusChange", new Class[]{NSMenuItem.class}));
        doNotDisturbItem.setImage(AppleUtils.getImage("/images/im_dnd.png"));
        contactMenu.addItem(doNotDisturbItem);

        Workspace workspace = SparkManager.getWorkspace();
        if (workspace != null) {
            Presence presence = workspace.getStatusBar().getPresence();
            if (Presence.Mode.chat.equals(presence.getMode())) {
                freeToChatItem.setState(NSCell.OnState);
                availableItem.setState(NSCell.OffState);
                awayItem.setState(NSCell.OffState);
                extendedAwayItem.setState(NSCell.OffState);
                doNotDisturbItem.setState(NSCell.OffState);

            }
            else if (Presence.Mode.available.equals(presence.getMode())) {
                freeToChatItem.setState(NSCell.OffState);
                availableItem.setState(NSCell.OnState);
                awayItem.setState(NSCell.OffState);
                extendedAwayItem.setState(NSCell.OffState);
                doNotDisturbItem.setState(NSCell.OffState);

            }
            else if (Presence.Mode.away.equals(presence.getMode())) {
                freeToChatItem.setState(NSCell.OffState);
                availableItem.setState(NSCell.OffState);
                awayItem.setState(NSCell.OnState);
                extendedAwayItem.setState(NSCell.OffState);
                doNotDisturbItem.setState(NSCell.OffState);
            }
            else if (Presence.Mode.xa.equals(presence.getMode())) {
                freeToChatItem.setState(NSCell.OffState);
                availableItem.setState(NSCell.OffState);
                awayItem.setState(NSCell.OffState);
                extendedAwayItem.setState(NSCell.OnState);
                doNotDisturbItem.setState(NSCell.OffState);
            }
            else if (Presence.Mode.dnd.equals(presence.getMode())) {
                freeToChatItem.setState(NSCell.OffState);
                availableItem.setState(NSCell.OffState);
                awayItem.setState(NSCell.OffState);
                extendedAwayItem.setState(NSCell.OffState);
                doNotDisturbItem.setState(NSCell.OnState);
            }
        }
        contactMenu.addItem(item.separatorItem());


        populateMenu(roster);

        NSStatusBar bar = NSStatusBar.systemStatusBar();
        statusItem = bar.statusItem(NSStatusBar.VariableStatusItemLength);
        statusItem.setImage(AppleUtils.getImage("/images/black-spark.gif"));
        statusItem.setHighlightMode(true);
        statusItem.setMenu(contactMenu);
        statusItem.setEnabled(false);

        SparkManager.getSessionManager().addPresenceListener(this);

    }

    public void display() {
        statusItem.setEnabled(true);
    }

    public void showBlackIcon() {
        statusItem.setImage(AppleUtils.getImage("/images/black-spark.gif"));
    }

    public void showActiveIcon() {
        statusItem.setImage(AppleUtils.getImage("/images/spark-16x16.png"));
    }

    /**
     * Called when NEW entries are added.
     *
     * @param addresses the addressss added.
     */
    public void entriesAdded(final Collection addresses) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Roster roster = SparkManager.getConnection().getRoster();
                for (Object address : addresses) {
                    String jid = (String) address;
                    RosterEntry entry = roster.getEntry(jid);
                    addEntry(entry);
                }
            }
        });
    }


    public void entriesUpdated(final Collection addresses) {
    }

    public void entriesDeleted(final Collection addresses) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Roster roster = SparkManager.getConnection().getRoster();
                for (Object address : addresses) {
                    String jid = (String) address;
                    RosterEntry entry = roster.getEntry(jid);
                    removeEntry(entry);
                }
            }
        });
    }

    public void presenceChanged(final String user) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Roster roster = SparkManager.getConnection().getRoster();
                Presence presence = roster.getPresence(user);


                if (Presence.Mode.away.equals(presence.getMode())) {
                    RosterEntry entry = roster.getEntry(user);
                    removeEntry(entry);
                }
                else if (Presence.Mode.available.equals(presence.getMode()) ||
                    Presence.Mode.chat.equals(presence.getMode())) {
                    RosterEntry entry = roster.getEntry(user);
                    addEntry(entry);
                }


            }
        });
    }

    public void presenceChanged(final Presence presence) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (Presence.Mode.chat.equals(presence.getMode())) {
                    freeToChatItem.setState(NSCell.OnState);
                    availableItem.setState(NSCell.OffState);
                    awayItem.setState(NSCell.OffState);
                    extendedAwayItem.setState(NSCell.OffState);
                    doNotDisturbItem.setState(NSCell.OffState);

                }
                else if (Presence.Mode.available.equals(presence.getMode())) {
                    freeToChatItem.setState(NSCell.OffState);
                    availableItem.setState(NSCell.OnState);
                    awayItem.setState(NSCell.OffState);
                    extendedAwayItem.setState(NSCell.OffState);
                    doNotDisturbItem.setState(NSCell.OffState);

                }
                else if (Presence.Mode.away.equals(presence.getMode())) {
                    freeToChatItem.setState(NSCell.OffState);
                    availableItem.setState(NSCell.OffState);
                    awayItem.setState(NSCell.OnState);
                    extendedAwayItem.setState(NSCell.OffState);
                    doNotDisturbItem.setState(NSCell.OffState);
                }
                else if (Presence.Mode.xa.equals(presence.getMode())) {
                    freeToChatItem.setState(NSCell.OffState);
                    availableItem.setState(NSCell.OffState);
                    awayItem.setState(NSCell.OffState);
                    extendedAwayItem.setState(NSCell.OnState);
                    doNotDisturbItem.setState(NSCell.OffState);
                }
                else if (Presence.Mode.dnd.equals(presence.getMode())) {
                    freeToChatItem.setState(NSCell.OffState);
                    availableItem.setState(NSCell.OffState);
                    awayItem.setState(NSCell.OffState);
                    extendedAwayItem.setState(NSCell.OffState);
                    doNotDisturbItem.setState(NSCell.OnState);
                }
            }
        });

    }

    public void createChatRoom(final NSMenuItem item) {

        Runnable runnable = new Runnable() {

            public void run() {

                String nickname = item.title();
                String jid = SparkManager.getUserManager().getJIDFromDisplayName(nickname);
                if (jid != null) {
                    String bareJID = StringUtils.parseBareAddress(jid);
                    ChatManager chatManager = SparkManager.getChatManager();
                    ChatRoom chatRoom = chatManager.createChatRoom(bareJID, nickname, nickname);
                    chatManager.getChatContainer().activateChatRoom(chatRoom);
                    ChatFrame frame = chatManager.getChatContainer().getChatFrame();
                    frame.setState(Frame.NORMAL);
                    frame.setVisible(true);
                    frame.toFront();
                    NSApplication.sharedApplication().activateIgnoringOtherApps(true);
                }
                else {
                    Log.error("Cannot create chat room, could not find jid for nickname " + nickname);
                }
            }

        };

        SwingUtilities.invokeLater(runnable);
    }

    public void handleStatusChange(final NSMenuItem item) {

        String status = item.title();

        if (freeToChatItem.title().equals(status)) {
            freeToChatItem.setState(NSCell.OnState);
            availableItem.setState(NSCell.OffState);
            awayItem.setState(NSCell.OffState);
            extendedAwayItem.setState(NSCell.OffState);
            doNotDisturbItem.setState(NSCell.OffState);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    StatusItem si = SparkManager.getWorkspace().getStatusBar().getStatusItem("Free To Chat");
                    SparkManager.getSessionManager().changePresence(si.getPresence());
                }
            });
        }
        else if (availableItem.title().equals(status)) {
            freeToChatItem.setState(NSCell.OffState);
            availableItem.setState(NSCell.OnState);
            awayItem.setState(NSCell.OffState);
            extendedAwayItem.setState(NSCell.OffState);
            doNotDisturbItem.setState(NSCell.OffState);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    StatusItem si = SparkManager.getWorkspace().getStatusBar().getStatusItem("Online");
                    SparkManager.getSessionManager().changePresence(si.getPresence());
                }
            });

        }
        else if (awayItem.title().equals(status)) {

            freeToChatItem.setState(NSCell.OffState);
            availableItem.setState(NSCell.OffState);
            awayItem.setState(NSCell.OnState);
            extendedAwayItem.setState(NSCell.OffState);
            doNotDisturbItem.setState(NSCell.OffState);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    StatusItem si = SparkManager.getWorkspace().getStatusBar().getStatusItem("Away");
                    SparkManager.getSessionManager().changePresence(si.getPresence());
                }
            });
        }
        else if (extendedAwayItem.title().equals(status)) {

            freeToChatItem.setState(NSCell.OffState);
            availableItem.setState(NSCell.OffState);
            awayItem.setState(NSCell.OffState);
            extendedAwayItem.setState(NSCell.OnState);
            doNotDisturbItem.setState(NSCell.OffState);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    StatusItem si = SparkManager.getWorkspace().getStatusBar().getStatusItem("Extended Away");
                    SparkManager.getSessionManager().changePresence(si.getPresence());
                }
            });
        }
        else if (doNotDisturbItem.title().equals(status)) {

            freeToChatItem.setState(NSCell.OffState);
            availableItem.setState(NSCell.OffState);
            awayItem.setState(NSCell.OffState);
            extendedAwayItem.setState(NSCell.OffState);
            doNotDisturbItem.setState(NSCell.OnState);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    StatusItem si = SparkManager.getWorkspace().getStatusBar().getStatusItem("Do Not Disturb");
                    SparkManager.getSessionManager().changePresence(si.getPresence());
                }
            });
        }
    }

    private void populateMenu(Roster roster) {

        NSMenuItem item = new NSMenuItem();
        item.setTitle("Available Contacts");
        item.setEnabled(false);
        contactMenu.addItem(item);

        for (RosterEntry entry : roster.getEntries()) {
            final Presence p = roster.getPresence(entry.getUser());
            if (p.isAvailable()) {
                addEntry(entry);
            }

        }

    }

    private void addEntry(RosterEntry entry) {
        if (entry == null) {
            return;
        }
        String nickname = entry.getName();
        if (nickname == null) {
            nickname = entry.getUser();
        }

        // if there isn't already an entry add it
        if (!entries.contains(nickname)) {
            NSMenuItem menuItem = new NSMenuItem();
            menuItem.setIndentationLevel(2);
            menuItem.setEnabled(true);
            menuItem.setTitle(nickname);
            menuItem.setAction(new NSSelector("createChatRoom", new Class[]{NSMenuItem.class}));
            menuItem.setTarget(this);
            contactMenu.addItem(menuItem);
            entries.put(nickname, menuItem);
        }
    }

    private void removeEntry(RosterEntry entry) {
        if (entry == null) {
            return;
        }
        String nickname = entry.getName();
        NSMenuItem menuItem = entries.remove(nickname);
        if (menuItem != null) {
            contactMenu.removeItem(menuItem);
        }
    }

}
