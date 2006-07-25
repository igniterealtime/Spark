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

/**
 * The ContactItemHandler allows users to customize the actions that take place within
 * a <code>ContactItem</code> within a users presence changes or the item is double clicked.
 */
public interface ContactItemHandler {

    /**
     * The users presence has been changed.
     *
     * @param presence the users new presence.
     */
    boolean handlePresence(ContactItem item, Presence presence);

    /**
     * The <code>ContactItem</code> has been double-clicked by the user.
     *
     * @return true if you wish to handle the double-click event.
     */
    boolean handleDoubleClick(ContactItem item);
}
