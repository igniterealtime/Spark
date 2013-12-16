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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.component.panes.CollapsiblePane;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 * Container representing a RosterGroup within the Contact List.
 */
public class ContactGroup extends CollapsiblePane implements MouseListener {
	private static final long serialVersionUID = 6578057848913010799L;
	private List<ContactItem> contactItems = new ArrayList<ContactItem>();
    private List<ContactGroup> contactGroups = new ArrayList<ContactGroup>();
    private List<ContactGroupListener> listeners = new ArrayList<ContactGroupListener>();
    private List<ContactItem> offlineContacts = new ArrayList<ContactItem>();

    private String groupName;
    private DefaultListModel model;
    private JList contactItemList;
    private boolean sharedGroup;
    private JPanel listPanel;

    // Used to display no contacts in list.
    private final ContactItem noContacts = UIComponentRegistry.createContactItem(
			Res.getString("group.empty"), null, null);

    private final ListMotionListener motionListener = new ListMotionListener();

    private boolean canShowPopup;
    
    private boolean mouseDragged = false;

    private LocalPreferences preferences;

    private ContactList contactList =  Workspace.getInstance().getContactList();    
    
    private DisplayWindowTask timerTask = null;
    
    private Timer timer = new Timer();

    /**
     * Create a new ContactGroup.
     *
     * @param groupName the name of the new ContactGroup.
     */
    public ContactGroup(String groupName) {
        // Initialize Model and UI
        model = new DefaultListModel();
        contactItemList = new JList(model);

        preferences = SettingsManager.getLocalPreferences();

        setTitle(getGroupTitle(groupName));

        // Use JPanel Renderer
        contactItemList.setCellRenderer(new JPanelRenderer());

        this.groupName = groupName;

        listPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        listPanel.add(contactItemList, listPanel);
        this.setContentPane(listPanel);

        if (!isOfflineGroup()) {
            contactItemList.setDragEnabled(true);
            contactItemList.setTransferHandler(new ContactGroupTransferHandler());
        }

        // Allow for mouse events to take place on the title bar
        getTitlePane().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                checkPopup(e);

            }

            public void mouseReleased(MouseEvent e) {
                checkPopup(e);
            }

            public void mouseClicked(MouseEvent e) {
	             if(e.getButton() == MouseEvent.BUTTON1)
	             {
	            	 contactList =  Workspace.getInstance().getContactList();
	           	 	 contactList.saveState();
	             }
            }

            public void checkPopup(MouseEvent e) {
	             if (e.isPopupTrigger())
	             {
	               e.consume();
	               fireContactGroupPopupEvent(e);
	             }
            }
        });

        // Items should have selection listener
        contactItemList.addMouseListener(this);

        contactItemList.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) {

            }

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                    ContactItem item = (ContactItem)contactItemList.getSelectedValue();
                    fireContactItemDoubleClicked(item);
                }

                ContactList.activeKeyEvent = keyEvent;
            }

            public void keyReleased(KeyEvent keyEvent) {
                ContactList.activeKeyEvent = null;
            }
        });

        noContacts.getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
        noContacts.getNicknameLabel().setForeground(Color.GRAY);
        model.addElement(noContacts);

        // Add Popup Window
        addPopupWindow();


    }

    /**
     * Adds a new offline contact.
     *
     * @param alias    the alias of the offline contact.
     * @param nickname the nickname of the offline contact.
     * @param jid      the jid of the offline contact.
     * @param status   the current status of the offline contact.
     */
    public void addOfflineContactItem(final String alias, final String nickname, final String jid, final String status) {
    	if(EventQueue.isDispatchThread()) {
    	   // Build new ContactItem
	   final ContactItem offlineItem = UIComponentRegistry.createContactItem(alias, nickname, jid);
    	   offlineItem.setGroupName(getGroupName());

    	   final Presence offlinePresence = PresenceManager.getPresence(jid);
    	   offlineItem.setPresence(offlinePresence);

    	   // set offline icon
    	   offlineItem.setIcon(PresenceManager.getIconFromPresence(offlinePresence));

    	   // Set status if applicable.
    	   if (ModelUtil.hasLength(status)) {
    		   offlineItem.setStatusText(status);
    	   }
    	   // Add to offline contacts.
    	   offlineContacts.add(offlineItem);

    	   insertOfflineContactItem(offlineItem);
       }
       else {
	    	try {
	    		// invokeAndWait, because the contacts must be added before they can moved to offline group
		      	 EventQueue.invokeAndWait(new Runnable(){
		      		 public void run() {
		      			 // Build new ContactItem
					 final ContactItem offlineItem = UIComponentRegistry.createContactItem(alias, nickname, jid);
		      			 offlineItem.setGroupName(getGroupName());

		      			 final Presence offlinePresence = PresenceManager.getPresence(jid);
		      			 offlineItem.setPresence(offlinePresence);

		      			 // set offline icon
		      			 offlineItem.setIcon(PresenceManager.getIconFromPresence(offlinePresence));

		      			 // Set status if applicable.
		      			 if (ModelUtil.hasLength(status)) {
		      				 offlineItem.setStatusText(status);
		      			 }
		      			 // Add to offline contacts.
		      			 offlineContacts.add(offlineItem);

		      			 insertOfflineContactItem(offlineItem);
		      		 }
		      	 });
		    }
		    catch(Exception ex) {
		      	 Log.error(ex);
		    }
       }
    }

    /**
     * Inserts a new offline <code>ContactItem</code> into the ui model.
     *
     * @param offlineItem the ContactItem to add.
     */
    public void insertOfflineContactItem(ContactItem offlineItem) {
        if (model.contains(offlineItem)) {
            return;
        }

        if (!preferences.isOfflineGroupVisible()) {
            Collections.sort(offlineContacts, itemComparator);
            int index = offlineContacts.indexOf(offlineItem);

            int totalListSize = contactItems.size();
            int newPos = totalListSize + index;

            if (newPos > model.size()) {
                newPos = model.size();
            }

            model.insertElementAt(offlineItem, newPos);

            if (model.contains(noContacts)) {
                model.removeElement(noContacts);
            }
        }
    }

    /**
     * Removes an offline <code>ContactItem</code> from the Offline contact
     * model and ui.
     *
     * @param item the offline contact item to remove.
     */
    public void removeOfflineContactItem(ContactItem item) {
        offlineContacts.remove(item);
        //removeContactItem(item);
    }

    /**
     * Removes an offline <code>ContactItem</code> from the offline contact model and ui.
     *
     * @param jid the offline contact item to remove.
     */
    public void removeOfflineContactItem(String jid) {
        final List<ContactItem> items = new ArrayList<ContactItem>(offlineContacts);
        for (ContactItem item : items) {
            if (item.getJID().equals(jid)) {
                removeOfflineContactItem(item);
            }
        }
    }

    /**
     * Toggles the visibility of Offline Contacts.
     *
     * @param show true if offline contacts should be shown, otherwise false.
     */
    public void toggleOfflineVisibility(boolean show) {
        final List<ContactItem> items = new ArrayList<ContactItem>(offlineContacts);
        for (ContactItem item : items) {
            if (show) {
                insertOfflineContactItem(item);
            }
            else {
                model.removeElement(item);
            }
        }
        if (model.getSize() == 0) {
            model.addElement(noContacts);
        }
    }


    /**
     * Adds a <code>ContactItem</code> to the ContactGroup.
     *
     * @param item the ContactItem.
     */
    public void addContactItem(ContactItem item) {
        // Remove from offline group if it exists
        removeOfflineContactItem(item.getJID());

        if (model.contains(noContacts)) {
            model.remove(0);
        }

        if (Res.getString("group.offline").equals(groupName)) {
            setOfflineGroupNameFont(item);
        }

        item.setGroupName(getGroupName());
        contactItems.add(item);

        List<ContactItem> tempItems = getContactItems();


        Collections.sort(tempItems, itemComparator);


        int index = tempItems.indexOf(item);


        Object[] objs = contactItemList.getSelectedValues();

        model.insertElementAt(item, index);

        int[] intList = new int[objs.length];
        for (int i = 0; i < objs.length; i++) {
            ContactItem contact = (ContactItem)objs[i];
            intList[i] = model.indexOf(contact);
        }

        if (intList.length > 0) {
            contactItemList.setSelectedIndices(intList);
        }

        fireContactItemAdded(item);
    }

	protected void setOfflineGroupNameFont(ContactItem item) {
		item.getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
		item.getNicknameLabel().setForeground(Color.GRAY);
	}

    /**
     * Call whenever the UI needs to be updated.
     */
    public void fireContactGroupUpdated() {
        contactItemList.validate();
        contactItemList.repaint();
        updateTitle();
    }

    /**
     * Adds a sub group to this Contact group.
     *
     * @param contactGroup
     *            that should be the new subgroup
     */
    public void addContactGroup(ContactGroup contactGroup) {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.add(contactGroup, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 15, 0, 0), 0, 0));
        panel.setBackground(Color.white);
        contactGroup.setSubPane(true);

        // contactGroup.setStyle(CollapsiblePane.TREE_STYLE);
        contactGroups.add(contactGroup);
        Collections.sort(contactGroups, ContactList.GROUP_COMPARATOR);
        listPanel.add(panel, contactGroups.indexOf(contactGroup));
    }

    /**
     * Removes a child ContactGroup.
     *
     * @param contactGroup the contact group to remove.
     */
    public void removeContactGroup(ContactGroup contactGroup) {
        Component[] comps = listPanel.getComponents();
        for (Component comp : comps) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                ContactGroup group = (ContactGroup) panel.getComponent(0);
                if (group == contactGroup) {
                    listPanel.remove(panel);
                    break;
                }
            }
        }


        contactGroups.remove(contactGroup);
    }

    public void setPanelBackground(Color color) {
        Component[] comps = listPanel.getComponents();
        for (Component comp : comps) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(color);
            }
        }

    }

    /**
     * Returns a ContactGroup based on it's name.
     *
     * @param groupName the name of the group.
     * @return the ContactGroup.
     */
    public ContactGroup getContactGroup(String groupName) {
        for (ContactGroup group : new ArrayList<ContactGroup>(contactGroups)) {
            if (group.getGroupName().equals(groupName)) {
                return group;
            }
        }

        return null;
    }

    /**
     * Removes a <code>ContactItem</code>.
     *
     * @param item the ContactItem to remove.
     */
    public void removeContactItem(ContactItem item) {
        contactItems.remove(item);
        if (contactItems.isEmpty()) {
            removeContactGroup(this);
        }

        model.removeElement(item);
        updateTitle();

        fireContactItemRemoved(item);
    }

    /**
     * Returns a <code>ContactItem</code> by the displayed name the user has been assigned.
     *
     * @param displayName the displayed name of the user.
     * @return the ContactItem.
     */
    public ContactItem getContactItemByDisplayName(String displayName) {
        for (ContactItem item : new ArrayList<ContactItem>(contactItems)) {
            if (item.getDisplayName().equals(displayName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a <code>ContactItem</code> from offlineContacts by the displayed name the user has been assigned.
     *
     * @param displayName the displayed name of the user.
     * @return the ContactItem.
     */
    public ContactItem getOfflineContactItemByDisplayName(String displayName) {
        for (ContactItem item : new ArrayList<ContactItem>(offlineContacts)) {
            if (item.getDisplayName().equals(displayName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a <code>ContactItem</code> by the displayed name the user has been assigned.
     *
     * @param displayName the displayed name of the user.
     * @param searchInOffline should we search <code>ContactItem</code> in offline contacts
     * @return the ContactItem.
     */
    public ContactItem getContactItemByDisplayName(String displayName, boolean searchInOffline) {
        if (searchInOffline) {
            ContactItem item = getContactItemByDisplayName(displayName);
            if (item == null) {
                item = getOfflineContactItemByDisplayName(displayName);
            }
            return item;
        }
        return getContactItemByDisplayName(displayName);
    }

    /**
     * Returns a <code>ContactItem</code> by the users bare bareJID.
     *
     * @param bareJID the bareJID of the user.
     * @return the ContactItem.
     */
    public ContactItem getContactItemByJID(String bareJID) {
        for (ContactItem item : new ArrayList<ContactItem>(contactItems)) {
            if (item != null && item.getJID().equals(bareJID)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a <code>ContactItem</code> from offlineContacts by the users bare bareJID.
     *
     * @param bareJID the bareJID of the user.
     * @return the ContactItem.
     */
    public ContactItem getOfflineContactItemByJID(String bareJID) {
        for (ContactItem item : new ArrayList<ContactItem>(offlineContacts)) {
            if (item.getJID().equals(bareJID)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a <code>ContactItem</code> by the users bare bareJID.
     *
     * @param bareJID the bareJID of the user.
     * @param searchInOffline should we search <code>ContactItem</code> in offline contacts
     * @return the ContactItem.
     */
    public ContactItem getContactItemByJID(String bareJID, boolean searchInOffline) {
        if (searchInOffline) {
            ContactItem item = getContactItemByJID(bareJID);
            if (item == null) {
                item = getOfflineContactItemByJID(bareJID);
            }
            return item;
        }
        return getContactItemByJID(bareJID);
    }

    /**
     * Returns all <code>ContactItem</cod>s in the ContactGroup.
     *
     * @return all ContactItems.
     */
    public List<ContactItem> getContactItems() {
        final List<ContactItem> list = new ArrayList<ContactItem>(contactItems);
        Collections.sort(list, itemComparator);
        return list;
    }

    /**
     * Returns the name of the ContactGroup.
     *
     * @return the name of the ContactGroup.
     */
    public String getGroupName() {
        return groupName;
    }


    public void mouseClicked(MouseEvent e) {

        Object o = contactItemList.getSelectedValue();
        if (!(o instanceof ContactItem)) {
            return;
        }

        // Iterator through rest
        ContactItem item = (ContactItem)o;

        if (e.getClickCount() == 2) {
            fireContactItemDoubleClicked(item);
        }
        else if (e.getClickCount() == 1) {
            fireContactItemClicked(item);
        }
    }

    public void mouseEntered(MouseEvent e) {
        int loc = contactItemList.locationToIndex(e.getPoint());

        Object o = model.getElementAt(loc);
        if (!(o instanceof ContactItem)) {
            return;
        }

        contactItemList.setCursor(GraphicUtils.HAND_CURSOR);
    }

    public void mouseExited(MouseEvent e) {
        Object o;
        try {
            int loc = contactItemList.locationToIndex(e.getPoint());
            if (loc == -1) {
                return;
            }

            o = model.getElementAt(loc);
            if (!(o instanceof ContactItem)) {
            	UIComponentRegistry.getContactInfoWindow().dispose();
                return;
            }
        }
        catch (Exception e1) {
            Log.error(e1);
            return;
        }

        contactItemList.setCursor(GraphicUtils.DEFAULT_CURSOR);

    }

    public void mousePressed(MouseEvent e) {
        checkPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        checkPopup(e);
    }

    private void checkPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            // Otherwise, handle single selection
            int index = contactItemList.locationToIndex(e.getPoint());
            if (index != -1) {
                int[] indexes = contactItemList.getSelectedIndices();
                boolean selected = false;
                for (int o : indexes) {
                    if (index == o) {
                        selected = true;
                    }
                }

                if (!selected) {
                    contactItemList.setSelectedIndex(index);
                    fireContactItemClicked((ContactItem)contactItemList.getSelectedValue());
                }
            }


            final Collection<ContactItem> selectedItems = SparkManager.getChatManager().getSelectedContactItems();
            if (selectedItems.size() > 1) {
                firePopupEvent(e, selectedItems);
            }
            else if (selectedItems.size() == 1) {
                final ContactItem contactItem = selectedItems.iterator().next();
                firePopupEvent(e, contactItem);
            }
        }
    }

    /**
     * Add a <code>ContactGroupListener</code>.
     *
     * @param listener the ContactGroupListener.
     */
    public void addContactGroupListener(ContactGroupListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a <code>ContactGroupListener</code>.
     *
     * @param listener the ContactGroupListener.
     */
    public void removeContactGroupListener(ContactGroupListener listener) {
        listeners.remove(listener);
    }

    private void fireContactItemClicked(ContactItem item) {
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) {
            contactGroupListener.contactItemClicked(item);
        }
    }

    private void fireContactItemDoubleClicked(ContactItem item) {
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) {
            contactGroupListener.contactItemDoubleClicked(item);
        }
    }


    private void firePopupEvent(MouseEvent e, ContactItem item) {
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) {
            contactGroupListener.showPopup(e, item);
        }
    }

    private void firePopupEvent(MouseEvent e, Collection<ContactItem> items) {
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) {
            contactGroupListener.showPopup(e, items);
        }
    }

    private void fireContactGroupPopupEvent(MouseEvent e) {
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) {
            contactGroupListener.contactGroupPopup(e, this);
        }
    }

    private void fireContactItemAdded(ContactItem item) {
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) {
            contactGroupListener.contactItemAdded(item);
        }
    }

    private void fireContactItemRemoved(ContactItem item) {
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) {
            contactGroupListener.contactItemRemoved(item);
        }
    }

    private void updateTitle() {
        if (Res.getString("group.offline").equals(groupName)) {
            setTitle(Res.getString("group.offline"));
            return;
        }

        int count = 0;
        List<ContactItem> list = new ArrayList<ContactItem>(getContactItems());
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ContactItem it = list.get(i);
            if (it.isAvailable()) {
                count++;
            }
        }

        setTitle(getGroupTitle(groupName) + " (" + count + " " + Res.getString("online") + ")");


        if (model.getSize() == 0) {
            model.addElement(noContacts);
        }
    }

    /**
     * Returns the containing <code>JList</code> of the ContactGroup.
     *
     * @return the JList.
     */
    public JList getList() {
        return contactItemList;
    }

    /**
     * Clears all selections within this group.
     */
    public void clearSelection() {
        contactItemList.clearSelection();
    }

    public void removeAllContacts() {
        // Remove all users from online group.
        for (ContactItem item : new ArrayList<ContactItem>(getContactItems())) {
            removeContactItem(item);
        }

        // Remove all users from offline group.
        for (ContactItem item : getOfflineContacts()) {
            removeOfflineContactItem(item);
        }
    }

    /**
     * Returns true if the ContactGroup contains available users.
     *
     * @return true if the ContactGroup contains available users.
     */
    public boolean hasAvailableContacts() {
        for (ContactGroup group : contactGroups) {
            if (group.hasAvailableContacts()) {
                return true;
            }
        }

        for (ContactItem item : getContactItems()) {
            if (item.getPresence() != null) {
                return true;
            }
        }
        return false;
    }

    public Collection<ContactItem> getOfflineContacts() {
        return new ArrayList<ContactItem>(offlineContacts);
    }

    /**
     * Sorts ContactItems.
     */
    final protected Comparator<ContactItem> itemComparator = new Comparator<ContactItem>() {
        public int compare(ContactItem item1, ContactItem item2) {
            return item1.getDisplayName().toLowerCase().compareTo(item2.getDisplayName().toLowerCase());
        }
    };

    /**
     * Returns true if this ContactGroup is the Offline Group.
     *
     * @return true if OfflineGroup.
     */
    public boolean isOfflineGroup() {
        return Res.getString("group.offline").equals(getGroupName());
    }

    /**
     * Returns true if this ContactGroup is the Unfiled Group.
     *
     * @return true if UnfiledGroup.
     */
    public boolean isUnfiledGroup() {
        //TODO: Don't identify the unfiled group by name, because the user
        //could have a custom group of that name.

        return Res.getString("unfiled").equals(getGroupName());
    }

    public String toString() {
        return getGroupName();
    }

    /**
     * Returns true if ContactGroup is a Shared Group.
     *
     * @return true if Shared Group.
     */
    public boolean isSharedGroup() {
        return sharedGroup;
    }

    /**
     * Set to true if this ContactGroup is a shared Group.
     *
     * @param sharedGroup true if shared group.
     */
    protected void setSharedGroup(boolean sharedGroup) {
        this.sharedGroup = sharedGroup;
        if (sharedGroup) {
            setToolTipText(Res.getString("message.is.shared.group", getGroupName()));
        }
    }

    /**
     * Returns all Selected Contacts within the ContactGroup.
     *
     * @return all selected ContactItems.
     */
    public List<ContactItem> getSelectedContacts() {
        final List<ContactItem> items = new ArrayList<ContactItem>();
        Object[] selections = contactItemList.getSelectedValues();
        final int no = selections != null ? selections.length : 0;
        for (int i = 0; i < no; i++) {
            try {
                ContactItem item = (ContactItem)selections[i];
                items.add(item);
            }
            catch (NullPointerException e) {
                // TODO: Evaluate if we should do something here.
            }
        }
        return items;
    }

    public JPanel getContainerPanel() {
        return listPanel;
    }

    public Collection<ContactGroup> getContactGroups() {
        return contactGroups;
    }

    /**
     * Lets make sure that the panel doesn't stretch past the
     * scrollpane view pane.
     *
     * @return the preferred dimension
     */
    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }

    /**
     * Sets the name of group.
     *
     * @param groupName the contact group name.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Returns the "pretty" title of the ContactGroup.
     *
     * @param title the title.
     * @return the new title.
     */
    public String getGroupTitle(String title) {
        int lastIndex = title.lastIndexOf("::");
        if (lastIndex != -1) {
            title = title.substring(lastIndex + 2);
        }

        return title;
    }

    /**
     * Returns true if the group is nested.
     *
     * @param groupName the name of the group.
     * @return true if the group is nested.
     */
    public boolean isSubGroup(String groupName) {
        return groupName.indexOf("::") != -1;
    }

    /**
     * Returns true if this group is nested.
     *
     * @return true if nested.
     */
    public boolean isSubGroup() {
        return isSubGroup(getGroupName());
    }

    /**
     * Returns the underlying container for the JList.
     *
     * @return the underlying container of the JList.
     */
    public JPanel getListPanel() {
        return listPanel;
    }

    /**
     * Adds an internal popup listesner.
     */
    private void addPopupWindow() {
        contactItemList.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {               
            	canShowPopup = true;
            	timerTask = new DisplayWindowTask(mouseEvent);            
            	timer.schedule(timerTask, 500, 1000);
            }

            public void mouseExited(MouseEvent mouseEvent) {               
                canShowPopup = false;
                UIComponentRegistry.getContactInfoWindow().dispose();
            }
        });


        contactItemList.addMouseMotionListener(motionListener);
    }

    private class DisplayWindowTask extends TimerTask {
        private MouseEvent event;
		private boolean newPopupShown = false;
        
		public DisplayWindowTask(MouseEvent e) {
			event = e;
		}	

		@Override
		public void run() {		
			if (canShowPopup) {
				if (!newPopupShown && !mouseDragged) {
					displayWindow(event);
					newPopupShown = true;					
				}
			}
		}
		
        public void setEvent(MouseEvent event) {
			this.event = event;
		}	

		public void setNewPopupShown(boolean popupChanged) {
			this.newPopupShown = popupChanged;
		}

		public boolean isNewPopupShown() {
			return newPopupShown;
		}		
    }
    
    private class ListMotionListener extends MouseMotionAdapter {
    	
    	@Override
        public void mouseMoved(MouseEvent e) {
            if (!canShowPopup) {
                return;
            }

            if (e == null) {
                return;
            }
            timerTask.setEvent(e);           
            if (needToChangePopup(e) && timerTask.isNewPopupShown()) {
            	UIComponentRegistry.getContactInfoWindow().dispose();            	
            	timerTask.setNewPopupShown(false);            	
            }
            mouseDragged = false;
        }
    	
    	@Override
    	public void mouseDragged(MouseEvent e) {
    		if(timerTask.isNewPopupShown()) {
    	    	UIComponentRegistry.getContactInfoWindow().dispose();    	    	
    		}
    		mouseDragged = true;
    	}
    }
    

    /**
     * Displays the <code>ContactInfoWindow</code>.
     *
     * @param e the mouseEvent that triggered this event.
     */
    private void displayWindow(MouseEvent e) {
    	if(preferences.areVCardsVisible()) {
    		UIComponentRegistry.getContactInfoWindow().display(this, e);
    	}
    }
    
    private boolean needToChangePopup(MouseEvent e) {
    	ContactInfoWindow contact = UIComponentRegistry.getContactInfoWindow();
        int loc = getList().locationToIndex(e.getPoint());
        ContactItem item = (ContactItem)getList().getModel().getElementAt(loc);
        return item == null || contact == null || contact.getContactItem() == null ? true : !contact.getContactItem().getJID().equals(item.getJID());
    }

    protected DefaultListModel getModel() {
        return model;
    }

    protected JList getContactItemList() {
        return contactItemList;
    }
}