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