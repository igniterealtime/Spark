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

/**
 * The <code>ContactListListener</code> interface is used to listen for model changes within the Contact List.
 * <p/>
 * In general, you implement this interface in order to listen
 * for adding and removal of both ContactItems and ContactGroups.
 */
public interface ContactListListener {

    /**
     * Notified when a <code>ContactItem</code> has been added to the ContactList.
     *
     * @param item the ContactItem added.
     */
    void contactItemAdded(ContactItem item);

    /**
     * Notified when a <code>ContactItem</code> has been removed from the ContactList.
     *
     * @param item the ContactItem removed.
     */
    void contactItemRemoved(ContactItem item);

    /**
     * Called when a <code>ContactGroup</code> has been added to the ContactList.
     *
     * @param group the ContactGroup.
     */
    void contactGroupAdded(ContactGroup group);

    /**
     * Called when a <code>ContactGroup</code> has been removed from the ContactList.
     *
     * @param group the ContactGroup.
     */
    void contactGroupRemoved(ContactGroup group);

    /**
     * Called when a <code>ContactItem</code> has been clicked in the Contact List.
     *
     * @param item         the <code>ContactItem</code> double clicked.
     */
    void contactItemClicked(ContactItem item);

     /**
     * Called when a <code>ContactItem</code> has been double clicked in the Contact List.
     *
     * @param item         the <code>ContactItem</code> double clicked.
     */
    void contactItemDoubleClicked(ContactItem item);
}
