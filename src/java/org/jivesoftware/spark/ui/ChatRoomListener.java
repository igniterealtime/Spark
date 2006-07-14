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