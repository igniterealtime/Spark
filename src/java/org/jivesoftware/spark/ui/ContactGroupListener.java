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

import java.awt.event.MouseEvent;
import java.util.Collection;

/**
 * The ContactGroupListener interface is one of the interfaces extension writers use to add functionality to Spark.
 * <p/>
 * In general, you implement this interface in order to listen for mouse and key events on <code>ContactGroup</code>s within
 * the Spark client <code>ContactList</code>.
 */
public interface ContactGroupListener {

    /**
     * Notifies a user that a new <code>ContactItem</code> has been added to the ContactGroup.
     *
     * @param item the ContactItem.
     */
    public void contactItemAdded(ContactItem item);

    /**
     * Notifies the user that a <code>ContactItem</code> has been removed from a ContactGroup.
     *
     * @param item the ContactItem removed.
     */
    public void contactItemRemoved(ContactItem item);

    /**
     * Notifies the user that a ContactItem within the ContactGroup has been double-clicked.
     *
     * @param item the ContactItem double clicked.
     */
    public void contactItemDoubleClicked(ContactItem item);

    /**
     * Notifies the user that a ContactItem within the ContactGroup has been clicked.
     *
     * @param item the ContactItem clicked.
     */
    public void contactItemClicked(ContactItem item);

    /**
     * Notifies the user that a popup call has occured on the ContactGroup.
     *
     * @param e    the MouseEvent that triggered the event.
     * @param item the ContactItem clicked within the ContactGroup.
     * @deprecated see <code>ContextMenuListener</code>
     */
    public void showPopup(MouseEvent e, ContactItem item);

    /**
     * Notifies the user that a popup call has occured on the ContactGroup.
     *
     * @param e     the MouseEvent that triggered the event.
     * @param items the ContactItems within the ContactGroup.
     * @deprecated see <code>ContextMenuListener</code>
     */
    public void showPopup(MouseEvent e, Collection<ContactItem> items);

    /**
     * Notifies the user that a Popup event has occured on the ContactGroup title
     * bar.
     *
     * @param e     the MouseEvent that triggered the event.
     * @param group the ContactGroup.
     */
    public void contactGroupPopup(MouseEvent e, ContactGroup group);
}
