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
 * Implement this interface to listen for ChatRooms closing.
 *
 * @author Derek DeMoro
 */
public interface ChatRoomClosingListener {

    /**
     * Notifies users that the room is closing.
     */
    void closing();
}
