/**
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
package org.jivesoftware.spark.roar;

import java.awt.*;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.roar.displaytype.PropertyBundle;
import org.jivesoftware.spark.roar.displaytype.RoarDisplayType;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.GlobalMessageListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.log.Log;

/**
 * Message Listener<br>
 * 
 * @author wolf.posdorfer
 * 
 */
public class RoarMessageListener implements GlobalMessageListener {

    private final RoarProperties _properties;

    public RoarMessageListener() {
        _properties = RoarProperties.getInstance();
    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {

        try {
            ChatRoom activeroom = SparkManager.getChatManager().getChatContainer().getActiveChatRoom();
            final int framestate = SparkManager.getChatManager().getChatContainer().getChatFrame().getState();
            final boolean frameisactive = SparkManager.getChatManager().getChatContainer().getChatFrame().isActive();

            // If the message is for a chat that's currently active and showing, do not popup.
            if (activeroom.equals(room) && framestate == Frame.NORMAL && frameisactive) {
                Log.debug( "Surpressing popup: chat is currently active and showing.");
                return;
            }

            // If the message is sent by the local user (potentially using a different client), do not popup.
            if ( (room instanceof GroupChatRoom && message.getFrom().getResourceOrEmpty().equals(activeroom.getNickname()))
                || SparkManager.getSessionManager().getJID().equals(message.getFrom())) {
                Log.debug( "Surpressing popup: sender of message is the local user.");
                return;
            }

            // When none of the exceptions above are true, do a popup!
            decideForRoomAndMessage(room, message);

        } catch (ChatRoomNotFoundException e) {
            // i don't care
        }

    }
    
    private void decideForRoomAndMessage(ChatRoom room, Message message) {
        final RoarDisplayType displayType = RoarProperties.getInstance().getDisplayTypeClass();
        if (room instanceof ChatRoomImpl && !isSingleRoomDisabled()) {
            displayType.messageReceived(room, message, getSingleBundle());
        } else if (room instanceof GroupChatRoom && !isMutliRoomDisabled()) {
            displayType.messageReceived(room, message, isMultiRoomDifferent() ? getMultiBundle() : getSingleBundle());
        }
    }

    
    private boolean isSingleRoomDisabled()
    {
        return _properties.getBoolean("roar.disable.single", false);
    }
    
    private boolean isMutliRoomDisabled()
    {
        return _properties.getBoolean("group.disable", false);
    }

    private boolean isMultiRoomDifferent()
    {
        return _properties.getBoolean("group.different.enabled", false);
    }
    
    private PropertyBundle getSingleBundle() {
        return new PropertyBundle(_properties.getBackgroundColor(), _properties.getHeaderColor(),
                _properties.getTextColor(), _properties.getDuration());
    }
    
    private PropertyBundle getMultiBundle() {
        return new PropertyBundle(
                _properties.getColor(RoarProperties.BACKGROUNDCOLOR_GROUP, _properties.getBackgroundColor()),
                _properties.getColor(RoarProperties.HEADERCOLOR_GROUP, _properties.getHeaderColor()),
                _properties.getColor(RoarProperties.TEXTCOLOR_GROUP, _properties.getTextColor()),
                _properties.getDuration("group.duration"));
    }

    @Override
    public void messageSent(ChatRoom room, Message message) {
        final RoarDisplayType displayType = RoarProperties.getInstance().getDisplayTypeClass();
        displayType.messageSent(room, message);
    }

}
