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
package org.jivesoftware.spark.roar;

import java.util.HashMap;

import javax.swing.JFrame;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.roar.displaytype.PropertyBundle;
import org.jivesoftware.spark.roar.displaytype.RoarDisplayType;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.GlobalMessageListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;

/**
 * Message Listener<br>
 * 
 * @author wolf.posdorfer
 * 
 */
public class RoarMessageListener implements GlobalMessageListener {

    private RoarDisplayType _displaytype;
    private RoarProperties _properties;

    private HashMap<String, Long> _rooms = new HashMap<>();

    public RoarMessageListener() {
        _displaytype = RoarProperties.getInstance().getDisplayTypeClass();
        _properties = RoarProperties.getInstance();
    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {

        try {
            ChatRoom activeroom = SparkManager.getChatManager().getChatContainer().getActiveChatRoom();

            int framestate = SparkManager.getChatManager().getChatContainer().getChatFrame().getState();

            if (framestate == JFrame.NORMAL && activeroom.equals(room) && room.isShowing()
                    && (isOldGroupChat(room) || isMessageFromRoom(room, message))) {
                // Do Nothing
            } else {
                decideForRoomAndMessage(room, message);
            }

        } catch (ChatRoomNotFoundException e) {
            // i dont care
        }

    }
    
    private void decideForRoomAndMessage(ChatRoom room, Message message) {
        if (doesMessageMatchKeywords(message)) {
            _displaytype.messageReceived(room, message, isKeyWordDifferent() ? getKeywordBundle() : getSingleBundle());
        } else if (room instanceof ChatRoomImpl && !isSingleRoomDisabled()) {
            _displaytype.messageReceived(room, message, getSingleBundle());
        } else if (room instanceof GroupChatRoom && !isMutliRoomDisabled()) {
            _displaytype.messageReceived(room, message, isMultiRoomDifferent() ? getMultiBundle() : getSingleBundle());
        }
    }

    private boolean doesMessageMatchKeywords(Message message) {
        for (String keyword : _properties.getKeywords()) {
            if (message.getBody().contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSingleRoomDisabled()
    {
        return _properties.getBoolean("roar.disable.single", false);
    }
    
    private boolean isMutliRoomDisabled()
    {
        return _properties.getBoolean("group.disable", false);
    }
    
    private boolean isKeyWordDifferent() 
    {
        return _properties.getBoolean("keyword.different.enabled", false);
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
                _properties.getInt("group.duration"));
    }
    private PropertyBundle getKeywordBundle() {
        return new PropertyBundle(
                _properties.getColor(RoarProperties.BACKGROUNDCOLOR_KEYWORD, _properties.getBackgroundColor()),
                _properties.getColor(RoarProperties.HEADERCOLOR_KEYWORD, _properties.getHeaderColor()),
                _properties.getColor(RoarProperties.TEXTCOLOR_KEYWORD, _properties.getTextColor()),
                _properties.getInt("keyword.duration"));
    }

    private boolean isOldGroupChat(ChatRoom room) {

        boolean result = false;

        if (room.getChatType() == Message.Type.groupchat) {

            if (_rooms.containsKey(room.getRoomname()) && _rooms.get(room.getRoomname()) == -1L) {
                return true;
            }

            if (!_rooms.containsKey(room.getRoomname())) {
                _rooms.put(room.getRoomname(), System.currentTimeMillis());
                return true;
            } else {
                long start = _rooms.get(room.getRoomname());
                long now = System.currentTimeMillis();

                result = (now - start) < 1500;
                if (result) {
                    _rooms.put(room.getRoomname(), -1L);
                }

            }
        }

        return result;
    }

    @Override
    public void messageSent(ChatRoom room, Message message) {
        _displaytype.messageSent(room, message);
    }

    /**
     * Check if the message comes directly from the room
     * 
     * @param room
     * @param message
     * @return boolean
     */
    private boolean isMessageFromRoom(ChatRoom room, Message message) {
        return message.getFrom().equals(room.getRoomname());
    }

}
