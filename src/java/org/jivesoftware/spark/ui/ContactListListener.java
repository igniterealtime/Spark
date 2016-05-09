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
