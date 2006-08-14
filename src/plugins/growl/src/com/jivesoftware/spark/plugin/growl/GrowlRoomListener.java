/**
 * $Revision: 22540 $
 * $Date: 2005-10-10 08:44:25 -0700 (Mon, 10 Oct 2005) $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package com.jivesoftware.spark.plugin.growl;

import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;


/**
 * @author Andrew Wright
 */
public class GrowlRoomListener implements ChatRoomListener {

    private GrowlMessageListener growlMessageListener;

    public GrowlRoomListener() {
        growlMessageListener = new GrowlMessageListener();
    }

    public void chatRoomOpened(ChatRoom room) {
        room.addMessageListener(growlMessageListener);
    }

    public void chatRoomLeft(ChatRoom room) {
        // Do nothing
    }

    public void chatRoomClosed(ChatRoom room) {
        // Do nothing
    }

    public void chatRoomActivated(ChatRoom room) {
        // Do nothing
    }

    public void userHasJoined(ChatRoom room, String userid) {
        // Do nothing
    }

    public void userHasLeft(ChatRoom room, String userid) {
        // Do nothing
    }
}
