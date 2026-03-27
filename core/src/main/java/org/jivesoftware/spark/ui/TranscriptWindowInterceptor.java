/**
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

import java.util.Date;

/**
 * Allows users to intercept messages before they are inserted into the TranscriptWindow.
 *
 * @see TranscriptWindow
 * @see org.jivesoftware.spark.ChatManager
 */
public interface TranscriptWindowInterceptor {

    /**
     * Is called before a message by this user is inserted into the TranscriptWindow.
     *
     * @param window  the TranscriptWindow.
     * @param userid  the userid.
     * @param message the message to be inserted.
     * @return true if it should be handled by a custom interceptor.
     */
    boolean isMessageIntercepted(TranscriptWindow window, String userid, Message message);

    /**
     * Is called before a historic text message by this user is inserted into the TranscriptWindow.
     * History messages are typically messages that were added to a chat before the local user joined the chat.
     *
     * @param window  the TranscriptWindow.
     * @param userid  the userid.
     * @param message the message to be inserted.
     * @param date    the timestamp of the message
     * @return true if it should be handled by a custom interceptor.
     */
    default boolean isHistoryMessageIntercepted(TranscriptWindow window, String userid, Message message, Date date)
    {
        return false;
    }
}
