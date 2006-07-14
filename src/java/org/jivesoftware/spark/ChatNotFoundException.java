/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;


/**
 * Thrown when a Chat was not found.
 *
 * @author Derek DeMoro
 */
public class ChatNotFoundException extends Exception {

    public ChatNotFoundException() {
        super();
    }

    public ChatNotFoundException(String msg) {
        super(msg);
    }

}
