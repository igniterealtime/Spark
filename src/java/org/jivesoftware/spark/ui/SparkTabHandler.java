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

import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.resource.SparkRes;

import java.awt.Component;
import java.awt.Color;

/**
 * Allows users to control the decoration of a <code>SparkTab</code> component within the <code>ChatContainer</code>.
 */
public abstract class SparkTabHandler {

    public abstract boolean isTabHandled(SparkTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused);

    /**
     * Updates the SparkTab to show it is in a stale state.
     *
     * @param tab      the SparkTab.
     * @param chatRoom the ChatRoom of the SparkTab.
     */
    protected void decorateStaleTab(SparkTab tab, ChatRoom chatRoom) {
        tab.setTitleColor(Color.gray);
        tab.setTabFont(tab.getDefaultFont());

        String jid = ((ChatRoomImpl)chatRoom).getParticipantJID();
        Presence presence = PresenceManager.getPresence(jid);

        if (!presence.isAvailable()) {
            tab.setIcon(SparkRes.getImageIcon(SparkRes.IM_UNAVAILABLE_STALE_IMAGE));
        }
        else {
            Presence.Mode mode = presence.getMode();
            if (mode == Presence.Mode.available || mode == null) {
                tab.setIcon(SparkRes.getImageIcon(SparkRes.IM_AVAILABLE_STALE_IMAGE));
            }
            else if (mode == Presence.Mode.away) {
                tab.setIcon(SparkRes.getImageIcon(SparkRes.IM_AWAY_STALE_IMAGE));
            }
            else if (mode == Presence.Mode.chat) {
                tab.setIcon(SparkRes.getImageIcon(SparkRes.IM_FREE_CHAT_STALE_IMAGE));
            }
            else if (mode == Presence.Mode.dnd) {
                tab.setIcon(SparkRes.getImageIcon(SparkRes.IM_DND_STALE_IMAGE));
            }
            else if (mode == Presence.Mode.xa) {
                tab.setIcon(SparkRes.getImageIcon(SparkRes.IM_DND_STALE_IMAGE));
            }
        }

        tab.validateTab();
    }
    
}
