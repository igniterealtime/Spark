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
package org.jivesoftware.sparkplugin.ui.call;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import net.java.sipmack.softphone.SoftPhoneManager;
import net.java.sipmack.softphone.SoftPhoneManager.CallRoomState;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.SparkTabHandler;

import java.awt.Color;
import java.awt.Component;

/**
 *
 */
public class SoftPhoneTabHandler extends SparkTabHandler {
    private SoftPhoneManager manager;

    public SoftPhoneTabHandler() {
        manager = SoftPhoneManager.getInstance();
    }

    public boolean isTabHandled(SparkTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused) {
        CallRoomState callState = manager.getCallRoomState(component);
        if (callState != null) {
            // This is a room with a call.
            handlePhoneCall(callState, tab, component, isSelectedTab, chatFrameFocused);
            return true;
        }


        return false;
    }

    /**
     * Called when the underlying component has a phone call.
     *
     * @param state            the CallRoomState.
     * @param tab              the SparkTab.
     * @param component        the component within the tab.
     * @param isSelectedTab    true if the tab is selected.
     * @param chatFrameFocused true if the chat frame is in focus.
     */
    private void handlePhoneCall(CallRoomState state, SparkTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused) {
        boolean isTyping = false;
        if (component instanceof ChatRoom) {
            isTyping = SparkManager.getChatManager().containsTypingNotification(((ChatRoom)component));
        }

        // Check if is typing.
        if (isTyping) {
            tab.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_EDIT_IMAGE));
        }
        else if (CallRoomState.inCall == state) {
            tab.setIcon(PhoneRes.getImageIcon("RECEIVER2_IMAGE"));
        }
        else if (CallRoomState.muted == state) {
            tab.setIcon(PhoneRes.getImageIcon("MUTE_IMAGE"));

        }
        else if (CallRoomState.onHold == state) {
            tab.setIcon(PhoneRes.getImageIcon("ON_HOLD_IMAGE"));
        }
        else if (CallRoomState.callWasEnded == state) {
            tab.setIcon(PhoneRes.getImageIcon("HANG_UP_PHONE_16x16_IMAGE"));
        }

        if (component instanceof ChatRoom) {
            handleChatRoom(component, tab, chatFrameFocused, isSelectedTab);
            return;
        }
        else {
            if (isSelectedTab && chatFrameFocused) {
                tab.setTitleColor(Color.black);
                tab.setTabFont(tab.getDefaultFont());
            }
        }

        // Handle title frame
        if (isSelectedTab && component instanceof PhonePanel) {
            final ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
            chatFrame.setTitle(((PhonePanel)component).getFrameTitle());
        }
    }

    private void handleChatRoom(Component component, SparkTab tab, boolean chatFrameFocused, boolean isSelectedTab) {
        final ChatRoom chatRoom = (ChatRoom)component;
        if (!chatFrameFocused || !isSelectedTab) {
            if (chatRoom.getUnreadMessageCount() > 0) {
                // Make tab red.
                tab.setTitleColor(Color.red);
                tab.setTabBold(true);
            }

            // Handle unread message count.
            int unreadMessageCount = chatRoom.getUnreadMessageCount();
            String appendedMessage = "";
            if (unreadMessageCount > 1) {
                appendedMessage = " (" + unreadMessageCount + ")";
            }

            tab.setTabTitle(chatRoom.getTabTitle() + appendedMessage);

        }

        // Should only set the icon to default if the frame is in focus
        // and the tab is the selected component.
        if (isSelectedTab && chatFrameFocused) {
            tab.setTitleColor(Color.black);
            tab.setTabFont(tab.getDefaultFont());
            tab.setTabTitle(chatRoom.getTabTitle());

            // Clear unread message count.
            chatRoom.clearUnreadMessageCount();
        }
    }


}
