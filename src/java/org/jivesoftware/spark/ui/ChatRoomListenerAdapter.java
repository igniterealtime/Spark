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
