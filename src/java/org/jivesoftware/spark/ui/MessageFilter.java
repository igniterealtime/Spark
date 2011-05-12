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
