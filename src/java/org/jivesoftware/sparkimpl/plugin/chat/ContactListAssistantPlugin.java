/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.chat;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.PreferenceListener;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Adds extra functionallity to the <code>ContactList</code>. This includes copying and moving of <code>ContactItem</code>.
 */
public class ContactListAssistantPlugin implements Plugin {

    private JMenu moveToMenu;
    private JMenu copyToMenu;

    public void initialize() {

        moveToMenu = new JMenu(Res.getString("menuitem.move.to"));
        copyToMenu = new JMenu(Res.getString("menuitem.copy.to"));

        final ContactList contactList = SparkManager.getContactList();
        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, final JPopupMenu popup) {
                final Collection<ContactItem> contactItems = Collections.unmodifiableCollection(contactList.getSelectedUsers());
                if (!contactItems.isEmpty()) {
                    final List<ContactGroup> contactGroups = contactList.getContactGroups();
                    Collections.sort(contactGroups, ContactList.GROUP_COMPARATOR);

                    for (final ContactGroup group : contactGroups) {
                        if (group.isUnfiledGroup() || group.isOfflineGroup()) {
                            continue;
                        }
                        final Action moveAction = new AbstractAction() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                moveItems(contactItems, group.getGroupName());
                            }
                        };

                        final Action copyAction = new AbstractAction() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                copyItems(contactItems, group.getGroupName());
                            }
                        };

                        moveAction.putValue(Action.NAME, group.getGroupName());
                        moveToMenu.add(moveAction);

                        copyAction.putValue(Action.NAME, group.getGroupName());
                        copyToMenu.add(copyAction);
                    }

                    popup.addPopupMenuListener(new PopupMenuListener() {
                        public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                        }

                        public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                            moveToMenu.removeAll();
                            copyToMenu.removeAll();
                            popup.removePopupMenuListener(this);
                        }

                        public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
                            moveToMenu.removeAll();
                            copyToMenu.removeAll();
                            popup.removePopupMenuListener(this);
                        }
                    });

                    int index = -1;
                    for (int i = 0; i < popup.getComponentCount(); i++) {
                        Object o = popup.getComponent(i);
                        if (o instanceof JMenuItem && ((JMenuItem)o).getText().equals(Res.getString("menuitem.rename"))) {
                            index = i;
                            break;
                        }
                    }
                    if (contactItems.size() == 1) {
                        // Add right after the rename item.
                        if (index != -1) {
                            popup.add(moveToMenu, index + 1);
                            popup.add(copyToMenu, index + 2);
                        }
                    }
                    else if (contactItems.size() > 1) {
                        popup.addSeparator();
                        popup.add(moveToMenu);
                        popup.add(copyToMenu);
                        popup.addSeparator();
                    }


                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        updateAvatarsInContactList();

        SettingsManager.addPreferenceListener(new PreferenceListener() {
            public void preferencesChanged(LocalPreferences preference) {
                updateAvatarsInContactList();
            }
        });
    }

    /**
     * Moves a collection of <code>ContactItem</code>s to the specified group.
     *
     * @param contactItems the contact items to move.
     * @param groupName    the name of the group to move to.
     */
    private void moveItems(Collection<ContactItem> contactItems, String groupName) {
        final ContactGroup contactGroup = getContactGroup(groupName);
        for (ContactItem contactItem : contactItems) {
            addContactItem(contactGroup, contactItem, true);
        }
    }

    /**
     * Copies a collection of <code>ContactItem</code>s to a specified group.
     *
     * @param contactItems the collection of contact items.
     * @param groupName    the name of the group to move to.
     */
    private void copyItems(Collection<ContactItem> contactItems, String groupName) {
        final ContactGroup contactGroup = getContactGroup(groupName);
        for (ContactItem contactItem : contactItems) {
            addContactItem(contactGroup, contactItem, false);
        }
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
    }

    private void updateContactItem(ContactItem contactItem) {
        contactItem.updateAvatarInSideIcon();
    }

    private void updateAvatarsInContactList() {
        final ContactList contactList = SparkManager.getContactList();
        for (ContactGroup contactGroup : contactList.getContactGroups()) {
            if (contactGroup.isOfflineGroup()) {
                continue;
            }

            for (ContactItem contactItem : contactGroup.getContactItems()) {
                updateContactItem(contactItem);
            }
        }
    }

    /**
     * Copies or moves a new <code>ContactItem</code> into the <code>ContactGroup</code>.
     *
     * @param contactGroup the ContactGroup.
     * @param item         the ContactItem to move.
     * @param move         true if the ContactItem should be moved, otherwise false.
     */
    private void addContactItem(final ContactGroup contactGroup, final ContactItem item, final boolean move) {
        ContactItem newContact = new ContactItem(item.getAlias(), item.getNickname(), item.getJID());
        newContact.setPresence(item.getPresence());
        newContact.setIcon(item.getIcon());
        newContact.getNicknameLabel().setFont(item.getNicknameLabel().getFont());

        // Do not copy/move a contact item only if it is not already in the Group.
        if (contactGroup.getContactItemByJID(item.getJID()) != null) {
            return;
        }

        if (!PresenceManager.isOnline(item.getJID())) {
            contactGroup.addOfflineContactItem(item.getAlias(), item.getNickname(), item.getJID(), null);
        }
        else {
            contactGroup.addContactItem(newContact);
        }
        contactGroup.clearSelection();

        final ContactGroup oldGroup = getContactGroup(item.getGroupName());

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                Roster roster = SparkManager.getConnection().getRoster();
                RosterEntry entry = roster.getEntry(item.getJID());

                RosterGroup groupFound = null;

                for (RosterGroup group : roster.getGroups()) {
                    if (group.getName().equals(contactGroup.getGroupName())) {
                        try {
                            groupFound = group;
                            group.addEntry(entry);
                        }
                        catch (XMPPException e1) {
                            Log.error(e1);
                            return false;
                        }
                    }
                }

                // This is a new group
                if (groupFound == null) {
                    groupFound = roster.createGroup(contactGroup.getGroupName());
                    try {
                        groupFound.addEntry(entry);
                    }
                    catch (XMPPException e) {
                        Log.error(e);
                    }
                }
                return true;
            }

            public void finished() {
                if ((Boolean)get()) {
                    // Now try and remove the group from the old one.
                    if (move) {
                        removeContactItem(oldGroup, item);
                    }
                }
            }

        };

        worker.start();
    }


    public boolean removeContactItem(ContactGroup contactGroup, ContactItem item) {
        if (contactGroup.isSharedGroup()) {
            return false;
        }

        if (contactGroup.isUnfiledGroup()) {
            contactGroup.removeContactItem(item);
            contactGroup.fireContactGroupUpdated();
            return true;
        }

        // Remove entry from Roster Group
        Roster roster = SparkManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());

        RosterGroup rosterGroup = null;

        for (RosterGroup group : roster.getGroups()) {
            if (group.getName().equals(contactGroup.getGroupName())) {
                try {
                    rosterGroup = group;
                    group.removeEntry(entry);
                }
                catch (XMPPException e1) {
                    return false;
                }
            }
        }

        if (rosterGroup == null) {
            return false;
        }

        if (!rosterGroup.contains(entry)) {
            contactGroup.removeContactItem(item);
            return true;
        }

        return false;
    }

    private ContactGroup getContactGroup(String groupName) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        return contactList.getContactGroup(groupName);
    }

}
