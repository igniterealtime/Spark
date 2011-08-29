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
package org.jivesoftware.spark.ui;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class ContactGroupTransferHandler extends TransferHandler {
	private static final long serialVersionUID = -1229773343301542259L;
	private static final DataFlavor flavors[] = {DataFlavor.imageFlavor, DataFlavor.javaFileListFlavor};


    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }


    public boolean canImport(JComponent comp, DataFlavor flavor[]) {
        if (!(comp instanceof JList)) {
            return false;
        }


        JList list = (JList)comp;
        ContactGroup group = getContactGroup(list);
        if(group == null){
            return false;
        }
        if ((group.isSharedGroup() && !flavor[0].equals(DataFlavor.javaFileListFlavor)) || group.isUnfiledGroup() || group.isOfflineGroup() || (!group.hasAvailableContacts() && flavor[0].equals(DataFlavor.javaFileListFlavor))) {
            return false;
        }

        for (int i = 0, n = flavor.length; i < n; i++) {
            for (int j = 0, m = flavors.length; j < m; j++) {
                if (flavor[i].equals(flavors[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void exportDone(JComponent c, Transferable data, int action) {
    }


    public Transferable createTransferable(JComponent comp) {

        if (comp instanceof JList) {
            JList list = (JList)comp;
            ContactItem source = (ContactItem)list.getSelectedValue();
            return new ContactItemTransferable(source);
        }
        return null;
    }

    public boolean importData(JComponent comp, Transferable t) {
        if (comp instanceof JList) {
            JList list = (JList)comp;
            ContactGroup group = getContactGroup(list);

            if (t.isDataFlavorSupported(flavors[0])) {
                try {
                    ContactItem item = (ContactItem)t.getTransferData(flavors[0]);
                    DefaultListModel model = (DefaultListModel)list.getModel();
                    int size = model.getSize();
                    for (int i = 0; i < size; i++) {
                        ContactItem it = (ContactItem)model.getElementAt(i);
                        if (it.getDisplayName().equals(item.getDisplayName())) {
                            return false;
                        }
                    }

                    addContactItem(group, item);
                    return true;
                }
                catch (UnsupportedFlavorException ignored) {
                }
                catch (IOException ignored) {
                }
            }
            else if (t.isDataFlavorSupported(flavors[1])) {
                try {
                    Object o = t.getTransferData(flavors[1]);
                    if (o instanceof java.util.Collection) {
                        Collection<File> files = (Collection<File>)o;
                        ContactItem source = (ContactItem)list.getSelectedValue();
                        if (source == null || source.getJID() == null) {
                            return false;
                        }

                        // Otherwise fire files dropped event.
                        SparkManager.getWorkspace().getContactList().fireFilesDropped(files, source);
                    }
                }
                catch (UnsupportedFlavorException e) {
                    Log.error(e);
                }
                catch (IOException e) {
                    Log.error(e);
                }
            }
        }
        return false;
    }

    public class ContactItemTransferable implements Transferable {

        private ContactItem item;

        public ContactItemTransferable(ContactItem item) {
            this.item = item;
        }

        // Returns supported flavors
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        // Returns true if flavor is supported
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns image
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return item;
        }
    }

    private ContactGroup getContactGroup(JList list) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        for (ContactGroup group : contactList.getContactGroups()) {
            if (group.getList() == list) {
                return group;
            }

            ContactGroup subGroup = getSubContactGroup(group, list);
            if (subGroup != null) {
                return subGroup;
            }
        }
        return null;
    }

    private ContactGroup getSubContactGroup(ContactGroup group, JList list) {
        for (ContactGroup g : group.getContactGroups()) {
            if (g.getList() == list) {
                return g;
            }

            // Search subs
            ContactGroup g1 = getSubContactGroup(g, list);
            if (g1 != null) {
                return g1;
            }
        }

        return null;
    }


    private ContactGroup getContactGroup(String groupName) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        return contactList.getContactGroup(groupName);
    }

    private void addContactItem(final ContactGroup contactGroup, final ContactItem item) {
        ContactItem newContact = UIComponentRegistry.createContactItem(Res.getString("group.empty"), null, null);
        newContact.setPresence(item.getPresence());
        newContact.setIcon(item.getIcon());
        newContact.getNicknameLabel().setFont(item.getNicknameLabel().getFont());

        if (!PresenceManager.isOnline(item.getJID())) {
            contactGroup.addOfflineContactItem(item.getAlias(), item.getNickname(), item.getJID(), null);
        }
        else {
            contactGroup.addContactItem(newContact);
        }
        contactGroup.clearSelection();
        contactGroup.fireContactGroupUpdated(); //Updating group title

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
                    removeContactItem(oldGroup, item);
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
}


