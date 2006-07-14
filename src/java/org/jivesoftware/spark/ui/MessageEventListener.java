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

import org.jivesoftware.smack.packet.Message;

/**
 * Listen for the sending and receiving of messages.
 *
 * @author Derek DeMoro
 */
public interface MessageEventListener {

    void sendingMessage(Message message);

    void receivingMessage(Message message);
}
