/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.component.panes.CollapsiblePane;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Iterator;
import java.util.List;

/**
 * Container representing a RosterGroup within the Contact List.
 */
public class ContactGroup extends CollapsiblePane implements MouseListener {
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
    private final ContactItem noContacts = new ContactItem("There are no online contacts in this group.", null);

    private final ListMotionListener motionListener = new ListMotionListener();

    private boolean canShowPopup;

    private MouseEvent mouseEvent;

    private LocalPreferences preferences;


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

            public void checkPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
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
     * @param nickname the nickname of the offline contact.
     * @param jid      the jid of the offline contact.
     */
    public void addOfflineContactItem(String nickname, String jid, String status) {
        // Build new ContactItem
        final ContactItem offlineItem = new ContactItem(nickname, jid);
        offlineItem.setGroupName(getGroupName());

        final Presence offlinePresence = PresenceManager.getPresence(jid);
        offlineItem.setPresence(offlinePresence);

        // set offline icon
        offlineItem.setIcon(PresenceManager.getIconFromPresence(offlinePresence));

        // Set status if applicable.
        if (ModelUtil.hasLength(status)) {
            offlineItem.setStatusText(status);
        }

        // Add to offlien contacts.
        offlineContacts.add(offlineItem);

        insertOfflineContactItem(offlineItem);
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
        removeContactItem(item);
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

        if ("Offline Group".equals(groupName)) {
            item.getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
            item.getNicknameLabel().setForeground(Color.GRAY);
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

    /**
     * Call whenever the UI needs to be updated.
     */
    public void fireContactGroupUpdated() {
        contactItemList.validate();
        contactItemList.repaint();
        updateTitle();
    }

    public void addContactGroup(ContactGroup contactGroup) {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.add(contactGroup, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 15, 0, 0), 0, 0));
        panel.setBackground(Color.white);
        contactGroup.setSubPane(true);

        // contactGroup.setStyle(CollapsiblePane.TREE_STYLE);
        listPanel.add(panel);
        contactGroups.add(contactGroup);
    }

    /**
     * Removes a child ContactGroup.
     *
     * @param contactGroup the contact group to remove.
     */
    public void removeContactGroup(ContactGroup contactGroup) {
        Component[] comps = listPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component comp = comps[i];
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel)comp;
                ContactGroup group = (ContactGroup)panel.getComponent(0);
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
        for (int i = 0; i < comps.length; i++) {
            Component comp = comps[i];
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel)comp;
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
        final Iterator groups = new ArrayList(contactGroups).iterator();
        while (groups.hasNext()) {
            ContactGroup group = (ContactGroup)groups.next();
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

        model.removeElement(item);
        updateTitle();

        fireContactItemRemoved(item);
    }

    /**
     * Returns a <code>ContactItem</code> by the nickname the user has been assigned.
     *
     * @param nickname the nickname of the user.
     * @return the ContactItem.
     */
    public ContactItem getContactItemByNickname(String nickname) {
        final Iterator iter = new ArrayList(contactItems).iterator();
        while (iter.hasNext()) {
            ContactItem item = (ContactItem)iter.next();
            if (item.getNickname().equals(nickname)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a <code>ContactItem</code> by the users bare bareJID.
     *
     * @param bareJID the bareJID of the user.
     * @return the ContactItem.
     */
    public ContactItem getContactItemByJID(String bareJID) {
        final Iterator iter = new ArrayList(contactItems).iterator();
        while (iter.hasNext()) {
            ContactItem item = (ContactItem)iter.next();
            if (item.getJID().equals(bareJID)) {
                return item;
            }
        }
        return null;
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

        ContactItem item = (ContactItem)o;
        if (item == null) {
            return;
        }

        contactItemList.setCursor(GraphicUtils.HAND_CURSOR);
    }

    public void mouseExited(MouseEvent e) {
        Object o = null;
        try {
            int loc = contactItemList.locationToIndex(e.getPoint());
            if (loc == -1) {
                return;
            }

            o = model.getElementAt(loc);
            if (!(o instanceof ContactItem)) {
                ContactInfoWindow.getInstance().dispose();
                return;
            }
        }
        catch (Exception e1) {
            Log.error(e1);
            return;
        }

        ContactItem item = (ContactItem)o;
        if (item == null) {
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
                for(int i=0; i<indexes.length; i++){
                    int o = indexes[i];
                    if(index == o){
                        selected = true;
                    }
                }

                if (!selected){
                   contactItemList.setSelectedIndex(index);
                   fireContactItemClicked((ContactItem)contactItemList.getSelectedValue());
                }
            }


            final Collection selectedItems = SparkManager.getChatManager().getSelectedContactItems();
            if (selectedItems.size() > 1) {
                firePopupEvent(e, selectedItems);
                return;
            }
            else if (selectedItems.size() == 1) {
                final ContactItem contactItem = (ContactItem)selectedItems.iterator().next();
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
        final Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
            ((ContactGroupListener)iter.next()).contactItemClicked(item);
        }
    }

    private void fireContactItemDoubleClicked(ContactItem item) {
        final Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
            ((ContactGroupListener)iter.next()).contactItemDoubleClicked(item);
        }
    }


    private void firePopupEvent(MouseEvent e, ContactItem item) {
        final Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
            ((ContactGroupListener)iter.next()).showPopup(e, item);
        }
    }

    private void firePopupEvent(MouseEvent e, Collection items) {
        final Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
            ((ContactGroupListener)iter.next()).showPopup(e, items);
        }
    }

    private void fireContactGroupPopupEvent(MouseEvent e) {
        final Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
            ((ContactGroupListener)iter.next()).contactGroupPopup(e, this);
        }
    }

    private void fireContactItemAdded(ContactItem item) {
        final Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
            ((ContactGroupListener)iter.next()).contactItemAdded(item);
        }
    }

    private void fireContactItemRemoved(ContactItem item) {
        final Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
            ((ContactGroupListener)iter.next()).contactItemRemoved(item);
        }
    }

    private void updateTitle() {
        if ("Offline Group".equals(groupName)) {
            setTitle("Offline Group");
            return;
        }

        int count = 0;
        List list = new ArrayList(getContactItems());
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ContactItem it = (ContactItem)list.get(i);
            if (it.isAvailable()) {
                count++;
            }
        }

        setTitle(getGroupTitle(groupName) + " (" + count + " online)");


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

    /**
     * Returns true if the ContactGroup contains available users.
     *
     * @return true if the ContactGroup contains available users.
     */
    public boolean hasAvailableContacts() {
        final Iterator iter = contactGroups.iterator();
        while (iter.hasNext()) {
            ContactGroup group = (ContactGroup)iter.next();

            if (group.hasAvailableContacts()) {
                return true;
            }
        }

        Iterator contacts = getContactItems().iterator();
        while (contacts.hasNext()) {
            ContactItem item = (ContactItem)contacts.next();
            if (item.getPresence() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sorts ContactItems.
     */
    final Comparator<ContactItem> itemComparator = new Comparator() {
        public int compare(Object contactItemOne, Object contactItemTwo) {
            final ContactItem item1 = (ContactItem)contactItemOne;
            final ContactItem item2 = (ContactItem)contactItemTwo;
            return item1.getNickname().toLowerCase().compareTo(item2.getNickname().toLowerCase());
        }
    };

    /**
     * Returns true if this ContactGroup is the Offline Group.
     *
     * @return true if OfflineGroup.
     */
    public boolean isOfflineGroup() {
        return "Offline Group".equals(getGroupName());
    }

    /**
     * Returns true if this ContactGroup is the Unfiled Group.
     *
     * @return true if UnfiledGroup.
     */
    public boolean isUnfiledGroup() {
        return "Unfiled".equals(getGroupName());
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
    public List getSelectedContacts() {
        final List items = new ArrayList();
        Object[] selections = contactItemList.getSelectedValues();
        final int no = selections != null ? selections.length : 0;
        for (int i = 0; i < no; i++) {
            ContactItem item = (ContactItem)selections[i];
            items.add(item);
        }
        return items;
    }

    public JPanel getContainerPanel() {
        return listPanel;
    }

    public Collection getContactGroups() {
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
        final Timer timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                canShowPopup = true;
            }
        });

        contactItemList.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                timer.start();
            }

            public void mouseExited(MouseEvent mouseEvent) {
                timer.stop();
                canShowPopup = false;
                ContactInfoWindow.getInstance().dispose();
            }
        });


        contactItemList.addMouseMotionListener(motionListener);
    }


    private class ListMotionListener extends MouseMotionAdapter {

        public void mouseMoved(MouseEvent e) {
            if (e != null) {
                mouseEvent = e;
            }

            if (!canShowPopup) {
                return;
            }

            if (e == null) {
                return;
            }

            displayWindow(e);
        }
    }

    /**
     * Displays the <code>ContactInfoWindow</code>.
     *
     * @param e the mouseEvent that triggered this event.
     */
    private void displayWindow(MouseEvent e) {
        ContactInfoWindow.getInstance().display(this, e);
    }
}


