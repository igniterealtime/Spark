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
 * Thrown when a Chat Room was not found.
 *
 * @author Derek DeMoro
 */
public class ChatRoomNotFoundException extends Exception {
	private static final long serialVersionUID = 517234944941907783L;

	public ChatRoomNotFoundException() {
        super();
    }

    public ChatRoomNotFoundException(String msg) {
        super(msg);
    }
}