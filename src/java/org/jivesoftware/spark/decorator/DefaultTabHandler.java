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
import javax.swing.UIManager;

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
                    tab.setTitleColor((Color) UIManager.get("Chat.unreadMessageColor"));
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
               // tab.setTabFont(tab.getDefaultFont());
                tab.setTabTitle(room.getTabTitle());

                // Clear unread message count.
                room.clearUnreadMessageCount();
            }
        }
        else {
            if (!chatFrameFocused || !isSelectedTab) {
                // Make tab red.
                tab.setTitleColor((Color) UIManager.get("Chat.unreadMessageColor"));
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
