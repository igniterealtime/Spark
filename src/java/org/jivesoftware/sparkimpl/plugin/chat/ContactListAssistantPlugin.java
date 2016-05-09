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
import org.jivesoftware.spark.util.UIComponentRegistry;
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
    private LocalPreferences localPreferences;
    
    @Override
    public void initialize() {

        moveToMenu = new JMenu(Res.getString("menuitem.move.to"));
        copyToMenu = new JMenu(Res.getString("menuitem.copy.to"));
        localPreferences = new LocalPreferences();
        
        final ContactList contactList = SparkManager.getContactList();
        contactList.addContextMenuListener(new ContextMenuListener() {
            @Override
            public void poppingUp(Object object, final JPopupMenu popup) {
                final Collection<ContactItem> contactItems = Collections.unmodifiableCollection(contactList.getSelectedUsers());
                if (!contactItems.isEmpty()) {
                    final List<ContactGroup> contactGroups = contactList.getContactGroups();
                    Collections.sort(contactGroups, ContactList.GROUP_COMPARATOR);

                    for (final ContactGroup group : contactGroups) {
                        if (group.isUnfiledGroup() || group.isOfflineGroup()) {
                            continue;
                        }
                        if (isContactItemInGroup(contactItems, group)) {
                        	continue;
                        }
                        final Action moveAction = new AbstractAction() {
			    private static final long serialVersionUID = 6542011870221162331L;

			    @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                moveItems(contactItems, group.getGroupName());
                            }
                        };

                        final Action copyAction = new AbstractAction() {
   			    private static final long serialVersionUID = 2232885525630977329L;

			    @Override
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
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                            moveToMenu.removeAll();
                            copyToMenu.removeAll();
                            popup.removePopupMenuListener(this);
                        }

                        @Override
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

            @Override
            public void poppingDown(JPopupMenu popup) {

            }

            @Override
            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        updateAvatarsInContactList();

        SettingsManager.addPreferenceListener(new PreferenceListener() {
            @Override
            public void preferencesChanged(LocalPreferences preference) {
                updateAvatarsInContactList();
            }
        });
    }
    
	private boolean isContactItemInGroup(Collection<ContactItem> contactItems, ContactGroup group) {
		boolean contactInGroup = false;
		for (ContactItem ci : contactItems) {
			if (group.getContactItemByJID(ci.getJID(), true) != null) {
				contactInGroup = true;
				break;
			}
		}
		return contactInGroup;
	}

    /**
     * Moves a collection of <code>ContactItem</code>s to the specified group.
     *
     * @param contactItems the contact items to move.
     * @param groupName    the name of the group to move to.
     */
    private void moveItems(Collection<ContactItem> contactItems, String groupName) {
        final ContactGroup contactGroup = getContactGroup(groupName);
        ContactGroup oldGroup = null;
        for (ContactItem contactItem : contactItems) {
        	oldGroup = getContactGroup(contactItem.getGroupName());
        	if (oldGroup.isSharedGroup()) {
        		continue;
        	}
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

    @Override
    public void shutdown() {
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
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
        ContactItem newContact = UIComponentRegistry.createContactItem(item.getAlias(), item.getNickname(), item.getJID());
        newContact.setPresence(item.getPresence());
        newContact.setIcon(item.getIcon());
        newContact.getNicknameLabel().setFont(item.getNicknameLabel().getFont());
        boolean groupHadAvailableContacts = false;
        
        // Do not copy/move a contact item only if it is not already in the Group.
        if (contactGroup.getContactItemByJID(item.getJID(), true) != null) {
            return;
        }

        if (!PresenceManager.isOnline(item.getJID())) {
            contactGroup.addOfflineContactItem(item.getAlias(), item.getNickname(), item.getJID(), null);
        }
        else {
            groupHadAvailableContacts = contactGroup.hasAvailableContacts();
            contactGroup.addContactItem(newContact);
        }
        contactGroup.clearSelection();
        contactGroup.fireContactGroupUpdated(); //Updating group title

        final ContactGroup oldGroup = getContactGroup(item.getGroupName());

        
        final boolean groupAvailableContacts = groupHadAvailableContacts;
        SwingWorker worker = new SwingWorker() {
            @Override
            public Object construct() {
                Roster roster = SparkManager.getConnection().getRoster();
                RosterEntry entry = roster.getEntry(item.getJID());

                RosterGroup groupFound = null;

                for (RosterGroup group : roster.getGroups()) {
                    if (group.getName().equals(contactGroup.getGroupName())) {
                        try {
                            groupFound = group;
                            if (!groupAvailableContacts)
                            {
                        	SparkManager.getContactList().toggleGroupVisibility(groupFound.getName(), true);
                            }
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
                        if (!groupAvailableContacts)
                        {
                    	SparkManager.getContactList().toggleGroupVisibility(groupFound.getName(), true);
                        }  
                    }
                    catch (XMPPException e) {
                        Log.error(e);
                    }
                }
                return true;
            }

            @Override
            public void finished() {
                if ((Boolean)get()) {
                    // Now try and remove the group from the old one.
                    if (move) {
                        removeContactItem(oldGroup, item);
                       if (!localPreferences.isEmptyGroupsShown() && !oldGroup.hasAvailableContacts())
                        {
                          SparkManager.getContactList().toggleGroupVisibility(oldGroup.getGroupName(),false);
                        }
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
            contactGroup.fireContactGroupUpdated(); //Updating group title
            return true;
        }

        return false;
    }

    private ContactGroup getContactGroup(String groupName) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        return contactList.getContactGroup(groupName);
    }

}
