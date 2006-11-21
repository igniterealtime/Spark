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
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.component.panes.CollapsiblePane;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
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

    private String groupName;
    private DefaultListModel model = new DefaultListModel();
    private JList list;
    private boolean sharedGroup;
    private JPanel listPanel;

    // Used to display no contacts in list.
    private final ContactItem noContacts = new ContactItem("There are no online contacts in this group.", null);

    private JWindow window = new JWindow();

    private final ListMotionListener motionListener = new ListMotionListener();
    private boolean canShowPopup;
    private MouseEvent mouseEvent;

    private ContactInfo contactInfoPanel;

    /**
     * Create a new ContactGroup.
     *
     * @param groupName the name of the new ContactGroup.
     */
    public ContactGroup(String groupName) {
        list = new JList(model);

        setTitle(getGroupTitle(groupName));

        // Use JPanel Renderer
        list.setCellRenderer(new JPanelRenderer());

        this.groupName = groupName;

        listPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        listPanel.add(list, listPanel);
        this.setContentPane(listPanel);

        if (!isOfflineGroup()) {
            list.setDragEnabled(true);
            list.setTransferHandler(new ContactGroupTransferHandler());
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
        list.addMouseListener(this);

        list.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) {

            }

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                    ContactItem item = (ContactItem)list.getSelectedValue();
                    fireContactItemDoubleClicked(item);
                }
            }

            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        noContacts.getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
        noContacts.getNicknameLabel().setForeground(Color.GRAY);
        model.addElement(noContacts);

        // Add Popup Window
        addPopupWindow();
    }

    /**
     * Adds a <code>ContactItem</code> to the ContactGroup.
     *
     * @param item the ContactItem.
     */
    public void addContactItem(ContactItem item) {
        if (model.getSize() == 1 && model.getElementAt(0) == noContacts) {
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


        Object[] objs = list.getSelectedValues();

        model.insertElementAt(item, index);

        int[] intList = new int[objs.length];
        for (int i = 0; i < objs.length; i++) {
            ContactItem contact = (ContactItem)objs[i];
            intList[i] = model.indexOf(contact);
        }

        if (intList.length > 0) {
            list.setSelectedIndices(intList);
        }

        fireContactItemAdded(item);
    }

    /**
     * Call whenever the UI needs to be updated.
     */
    public void fireContactGroupUpdated() {
        list.validate();
        list.repaint();
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
            if (item.getFullJID().equals(bareJID)) {
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
        return new ArrayList<ContactItem>(contactItems);
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

        Object o = list.getSelectedValue();
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
        int loc = list.locationToIndex(e.getPoint());

        Object o = model.getElementAt(loc);
        if (!(o instanceof ContactItem)) {
            return;
        }

        ContactItem item = (ContactItem)o;
        if (item == null) {
            return;
        }

        list.setCursor(GraphicUtils.HAND_CURSOR);
    }

    public void mouseExited(MouseEvent e) {
        window.setVisible(false);

        Object o = null;
        try {
            int loc = list.locationToIndex(e.getPoint());

            o = model.getElementAt(loc);
            if (!(o instanceof ContactItem)) {
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
        list.setCursor(GraphicUtils.DEFAULT_CURSOR);

    }

    public void mousePressed(MouseEvent e) {
        checkPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        checkPopup(e);
    }

    private void checkPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            // Check for multi selection
            int[] indeces = list.getSelectedIndices();
            List selection = new ArrayList();
            for (int i = 0; i < indeces.length; i++) {
                selection.add(model.getElementAt(indeces[i]));
            }

            if (selection.size() > 1) {
                firePopupEvent(e, selection);
                return;
            }

            // Otherwise, handle single selection
            int index = list.locationToIndex(e.getPoint());

            Object o = model.getElementAt(index);
            if (!(o instanceof ContactItem)) {
                return;
            }
            ContactItem item = (ContactItem)o;
            list.setSelectedIndex(index);

            firePopupEvent(e, item);
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
        return list;
    }

    /**
     * Clears all selections within this group.
     */
    public void clearSelection() {
        list.clearSelection();
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
        Object[] selections = list.getSelectedValues();
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

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupTitle(String title) {
        int lastIndex = title.lastIndexOf("::");
        if (lastIndex != -1) {
            title = title.substring(lastIndex + 2);
        }

        return title;
    }

    public boolean isSubGroup(String groupName) {
        return groupName.indexOf("::") != -1;
    }

    public boolean isSubGroup() {
        return isSubGroup(getGroupName());
    }

    public JPanel getListPanel() {
        return listPanel;
    }

    private void addPopupWindow() {
        final Timer timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                canShowPopup = true;
                motionListener.mouseMoved(mouseEvent);
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                timer.start();
            }

            public void mouseExited(MouseEvent mouseEvent) {
                timer.stop();
                canShowPopup = false;
            }
        });


        list.addMouseMotionListener(motionListener);
    }


    private class ListMotionListener extends MouseMotionAdapter {
        private ContactItem activeItem;

        public void mouseMoved(MouseEvent e) {
            if (e != null) {
                mouseEvent = e;
            }

            if (!canShowPopup) {
                return;
            }

            if(e == null){
                return;
            }

            int loc = list.locationToIndex(e.getPoint());
            Point point = list.indexToLocation(loc);

            ContactItem item = (ContactItem)model.getElementAt(loc);
            if (item == null || item.getFullJID() == null) {
                return;
            }

            if (activeItem != null && activeItem == item) {
                return;
            }

            activeItem = item;

            window.setFocusableWindowState(false);
            if (contactInfoPanel == null) {
                contactInfoPanel = new ContactInfo();
                window.getContentPane().add(contactInfoPanel);
            }

            contactInfoPanel.setContactItem(item);

            window.pack();


            Point mainWindowLocation = SparkManager.getMainWindow().getLocationOnScreen();
            Point listLocation = list.getLocationOnScreen();

            int x = (int)mainWindowLocation.getX() + SparkManager.getMainWindow().getWidth();

            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if ((int)screenSize.getWidth() - 250 >= x) {
                window.setLocation(x, (int)listLocation.getY() + (int)point.getY());
                if (!window.isVisible())
                    window.setVisible(true);
            }
            else {
                window.setLocation((int)mainWindowLocation.getX() - 250, (int)listLocation.getY() + (int)point.getY());
                if (!window.isVisible())
                    window.setVisible(true);
            }

        }
    }
}


