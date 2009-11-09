/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.decorator;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.SparkTabHandler;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;

/**
 *
 */
public class DefaultTabHandler extends SparkTabHandler {

    public DefaultTabHandler() {

    }

    public boolean isTabHandled(SparkTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused) {

        if (component instanceof ChatRoom) {
            ChatRoom room = (ChatRoom)component;

            boolean isStaleRoom = SparkManager.getChatManager().isStaleRoom(room);

            boolean isTyping = SparkManager.getChatManager().containsTypingNotification((ChatRoom)component);

            // Check if is typing.
            if (isTyping) {
                tab.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_EDIT_IMAGE));
            }
            else if (room instanceof ChatRoomImpl && !isStaleRoom) {
                // User is not typing, therefore show default presence icon.
                String participantJID = ((ChatRoomImpl)room).getParticipantJID();
                Presence presence = PresenceManager.getPresence(participantJID);
                Icon icon = PresenceManager.getIconFromPresence(presence);
                tab.setIcon(icon);
            }


            if (!chatFrameFocused || !isSelectedTab) {
                if (room.getUnreadMessageCount() > 0) {
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

            // Check if the room is stale.
            if (isStaleRoom && component instanceof ChatRoomImpl) {
                decorateStaleTab(tab, (ChatRoom)component);
            }
            // Should only set the icon to default if the frame is in focus
            // and the tab is the selected component.
            else if (isSelectedTab && chatFrameFocused) {
                tab.setTitleColor(Color.black);
                tab.setTabFont(tab.getDefaultFont());
                tab.setTabTitle(room.getTabTitle());

                // Clear unread message count.
                room.clearUnreadMessageCount();
            }
        }
        else {
            if (!chatFrameFocused || !isSelectedTab) {
                // Make tab red.
                tab.setTitleColor(Color.red);
                tab.setTabBold(true);
            }
            if (isSelectedTab && chatFrameFocused) {
                tab.setTitleColor(Color.black);
                tab.setTabFont(tab.getDefaultFont());
            }
        }
        return true;
    }


}
