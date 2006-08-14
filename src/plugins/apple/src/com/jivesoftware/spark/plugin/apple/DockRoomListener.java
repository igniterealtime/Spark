/**
 * $Revision: 22540 $
 * $Date: 2005-10-10 08:44:25 -0700 (Mon, 10 Oct 2005) $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package com.jivesoftware.spark.plugin.apple;

import com.jivesoftware.spark.plugin.apple.DockMessageListener;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;


/**
 * Registers a new {@link com.jivesoftware.spark.plugin.apple.DockMessageListener} every time
 * a new chat room is opened.
 * 
 * @author Andrew Wright
 */
public class DockRoomListener implements ChatRoomListener {

    private DockMessageListener messageListener;

    public DockRoomListener() {
        messageListener = new DockMessageListener();
    }

    public void chatRoomOpened(ChatRoom room) {
        room.addMessageListener(messageListener);
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
