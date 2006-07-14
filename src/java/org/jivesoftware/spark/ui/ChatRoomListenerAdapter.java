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
 * An abstract adapter class for receiving Chat Room Events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * <p/>
 * Chat Room events let you track when a room is opened, closed, joined, left and activated.
 * <p/>
 * Extend this class to methods for the events of interest. (If you implement the
 * <code>ChatRoomListener</code> interface, you have to define all of
 * the methods in it. This abstract class defines null methods for them
 * all, so you can only have to define methods for events you care about.)
 * <p/>
 * Create a listener object using the extended class and then register it with
 * the <code>ChatManager</code>'s <code>addChatRoomListener</code> method.
 *
 * @author Derek DeMoro
 * @see ChatRoomListener
 */
public abstract class ChatRoomListenerAdapter implements ChatRoomListener {

    public void chatRoomOpened(ChatRoom room) {

    }

    public void chatRoomLeft(ChatRoom room) {

    }

    public void chatRoomClosed(ChatRoom room) {

    }

    public void chatRoomActivated(ChatRoom room) {

    }

    public void userHasJoined(ChatRoom room, String userid) {

    }

    public void userHasLeft(ChatRoom room, String userid) {

    }
}
