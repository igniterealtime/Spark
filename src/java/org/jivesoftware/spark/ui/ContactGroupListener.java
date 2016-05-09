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

import java.awt.event.MouseEvent;
import java.util.Collection;

import org.jivesoftware.spark.plugin.ContextMenuListener;

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
     * @deprecated see {@link ContextMenuListener}
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
