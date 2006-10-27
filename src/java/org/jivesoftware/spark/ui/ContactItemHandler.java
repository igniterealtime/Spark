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

import org.jivesoftware.smack.packet.Presence;

import javax.swing.Icon;

/**
 * The ContactItemHandler allows users to customize the actions that take place within
 * a <code>ContactItem</code> within a users presence changes or the item is double clicked.
 */
public interface ContactItemHandler {

    /**
     * The users presence has been changed.
     *
     * @param item     the contact item.
     * @param presence the users new presence.
     * @return true if the presence was handled.
     */
    boolean handlePresence(ContactItem item, Presence presence);

    /**
     * Return the icon used for particular presence.
     *
     * @param jid the users jid.
     * @return the icon, if any. null may be returned.
     */
    Icon getIcon(String jid);

    /**
     * Return the icon to use on the chat room tab.
     *
     * @param presence the presence of the user.
     * @return the icon to use.
     */
    Icon getTabIcon(Presence presence);


    /**
     * The <code>ContactItem</code> has been double-clicked by the user.
     *
     * @param item the ContactItem to handle.
     * @return true if you wish to handle the double-click event.
     */
    boolean handleDoubleClick(ContactItem item);
}
