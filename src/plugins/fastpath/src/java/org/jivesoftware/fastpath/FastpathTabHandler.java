/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.fastpath;

import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.Workpane.RoomState;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.SparkTabHandler;

import java.awt.Color;
import java.awt.Component;

/**
 * The tab handler for Fastpath tab actions.
 */
public class FastpathTabHandler extends SparkTabHandler {


    public boolean isTabHandled(SparkTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused) {
        if (component instanceof ChatRoom) {
            RoomState roomState = FastpathPlugin.getLitWorkspace().getRoomState((ChatRoom)component);
            if (roomState == null) {
                // This is not a fastpath room.
                return false;
            }

            // This is a fastpath room.
            handleFastpathRoom(tab, (ChatRoom)component, isSelectedTab, chatFrameFocused);
            return true;
        }


        return false;
    }

    private void handleFastpathRoom(SparkTab tab, ChatRoom room, boolean isSelectedTab, boolean chatFrameFocused) {
        RoomState roomState = FastpathPlugin.getLitWorkspace().getRoomState(room);
        boolean isTyping = SparkManager.getChatManager().containsTypingNotification(room);

        // Check if is typing.
        if (isTyping) {
            tab.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_EDIT_IMAGE));
        }
        else {
            tab.setIcon(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));
        }


        if (!chatFrameFocused || !isSelectedTab) {
            if (room.getUnreadMessageCount() > 0 || RoomState.incomingRequest == roomState || RoomState.invitationRequest == roomState) {
                // Make tab red.
                tab.setTitleColor(Color.red);
                tab.setTabBold(true);
            }

            // Handle unread message count.
            int unreadMessageCount = room.getUnreadMessageCount();
            String appendedMessage = "";
            if (unreadMessageCount > 1) {
                appendedMessage = " (" + unreadMessageCount + ")";
            }

            tab.setTabTitle(room.getTabTitle() + appendedMessage);

        }

        // Should only set the icon to default if the frame is in focus
        // and the tab is the selected component.
        if (isSelectedTab && chatFrameFocused) {
            tab.setTitleColor(Color.black);
            tab.setTabFont(tab.getDefaultFont());
            tab.setTabTitle(room.getTabTitle());

            // Clear unread message count.
            room.clearUnreadMessageCount();
        }

    }
}
