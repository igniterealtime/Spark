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
 * The <code>MessageListener</code> interface is one of the interfaces extension
 * writers use to add functionality to Spark.
 * <p/>
 * In general, you implement this interface in order to listen
 * for incoming or outgoing messages to particular ChatRooms and to be notified
 * about the message itself.
 */
public interface MessageListener {

    /**
     * Invoked by the <code>ChatRoom</code> when it is receives a new message.
     *
     * @param room    the <code>ChatRoom</code> the message was sent to.
     * @param message the message received.
     * @see ChatRoom
     */
    void messageReceived(ChatRoom room, Message message);

    /**
     * Invoked by the <code>ChatRoom</code> when a new message has
     * been sent.
     *
     * @param room    the <code>ChatRoom</code> that sent the message.
     * @param message the message sent.
     * @see ChatRoom
     */
    void messageSent(ChatRoom room, Message message);
}