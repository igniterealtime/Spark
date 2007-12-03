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
 * The <code>MessageFilter</code> interface is one of the interfaces extension
 * writers use to add functionality to Spark.
 * <p/>
 * In general, you implement this interface in order to modify the body of the message.
 * <p/>
 * <pre>
 * String currentBody = message.getBody();
 * currentBody = removeAllBadWords(currentBody);
 * message.setBody(currentBody);
 */
public interface MessageFilter {

    /**
     * Update the body of an outgoing message.
     *
     * @param room Room the message is attached to.
     * @param message the message to update.
     */
    void filterOutgoing(ChatRoom room, Message message);

    /**
     * Updates the body of an incoming message.
     *
     * @param room Room the message is attached to.
     * @param message the message to update.
     */
    void filterIncoming(ChatRoom room, Message message);


}
