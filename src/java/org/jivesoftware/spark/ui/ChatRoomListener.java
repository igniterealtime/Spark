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

/**
 * The <code>ChatRoomListener</code> interface is one of the interfaces extension
 * writers use to add functionality to Spark.
 * <p/>
 * In general, you implement this interface in order to listen
 * for ChatRoom activity, such as a ChatRoom opening, closing, or being
 * activated.
 */
public interface ChatRoomListener {

    /**
     * Invoked by <code>ChatRooms</code> when a new ChatRoom has been opened.
     *
     * @param room - the <code>ChatRoom</code> that has been opened.
     * @see ChatContainer
     */
    void chatRoomOpened(ChatRoom room);

    /**
     * Invoked by <code>ChatRooms</code> when a ChatRoom has been left, but not
     * closed.
     *
     * @param room - the <code>ChatRoom</code> that has been left.
     * @see ChatContainer
     */
    void chatRoomLeft(ChatRoom room);

    /**
     * Invoke by <code>ChatRooms</code> when a ChatRoom has been closed.
     *
     * @param room - the <code>ChatRoom</code> that has been closed.
     */
    void chatRoomClosed(ChatRoom room);

    /**
     * Invoked by <code>ChatRooms</code> when a ChatRoom has been activated.
     * i.e. it has already been opened, but was deactivated when the user
     * selected a new chat room, but now has selected the old one.
     *
     * @param room - the <code>ChatRoom</code> that has been selected.
     */
    void chatRoomActivated(ChatRoom room);

    /**
     * Invoked by <code>ChatRooms</code> when a person has joined a chat room.
     *
     * @param room   - the chat room the person has joined
     * @param userid - the userid of the person who has joined
     */
    void userHasJoined(ChatRoom room, String userid);

    /**
     * Invoked by <code>ChatRooms</code> when a person has left a chat room.
     *
     * @param room   - the chat room the person has left
     * @param userid - the userid of the person who has left
     */
    void userHasLeft(ChatRoom room, String userid);


}