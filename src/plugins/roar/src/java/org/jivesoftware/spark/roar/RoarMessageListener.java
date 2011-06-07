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
import org.jivesoftware.spark.roar.displaytype.RoarDisplayType;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.GlobalMessageListener;

/**
 * Message Listener<br>
 * 
 * @author wolf.posdorfer
 * 
 */
public class RoarMessageListener implements GlobalMessageListener {

    private RoarDisplayType _displaytype;

    private HashMap<String, Long> _rooms = new HashMap<String, Long>();

    public RoarMessageListener() {

	_displaytype = RoarProperties.getInstance().getDisplayTypeClass();

    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {

	try {
	    ChatRoom activeroom = SparkManager.getChatManager()
		    .getChatContainer().getActiveChatRoom();

	    int framestate = SparkManager.getChatManager().getChatContainer()
		    .getChatFrame().getState();

	    // boolean isoldgroupchat = isOldGroupchat(message);

	    boolean isoldgroupchat = checkTime(room, message);

	    if (framestate == JFrame.NORMAL && activeroom.equals(room)
		    && room.isShowing()
		    && (isoldgroupchat || isMessageFromRoom(room, message))) 
	    {
		// Do Nothing
	    } else {
		_displaytype.messageReceived(room, message);
	    }

	} catch (ChatRoomNotFoundException e) {
	    // i dont care
	}

    }

    private boolean checkTime(ChatRoom room, Message message) {

	boolean result = false;
	
	if (room.getChatType() == Message.Type.groupchat) {

	    if (_rooms.containsKey(room.getRoomname())
		    && _rooms.get(room.getRoomname()) == -1L) {
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

    // /**
    // * Checks if the Messages come from a time prior entering the groupchat
    // *
    // * @param message
    // * @return true if this is an old Message
    // */
    // private boolean isOldGroupchat(Message message) {
    // Calendar cal = Calendar.getInstance();
    //
    // int day = cal.get(Calendar.DATE);
    // int month = cal.get(Calendar.MONTH) + 1;
    // int year = cal.get(Calendar.YEAR);
    //
    // StringBuilder build = new StringBuilder();
    // // Append leading 0's to hour,minute,seconds
    // build.append(year);
    // build.append(month < 10 ? "0" + month : month);
    // build.append(day < 10 ? "0" + day : day);
    //
    // int todaysDate = Integer.parseInt(build.toString());
    //
    // // Append leading 0's to hour,minute,seconds
    // String hour = cal.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
    // + cal.get(Calendar.HOUR_OF_DAY) : ""
    // + cal.get(Calendar.HOUR_OF_DAY);
    // String minute = cal.get(Calendar.MINUTE) < 10 ? "0"
    // + cal.get(Calendar.MINUTE) : "" + cal.get(Calendar.MINUTE);
    // String second = cal.get(Calendar.SECOND) < 10 ? "0"
    // + cal.get(Calendar.SECOND) : "" + cal.get(Calendar.SECOND);
    //
    // int todaysHour = Integer.parseInt(hour + minute + second);
    //
    // String stamp = "";
    //
    // // get String with timestamp
    // // 20110526T08:27:18
    // if (message.toXML().contains("stamp=")) {
    // stamp = extractDate(message.toXML());
    // }
    //
    // boolean isoldgroupchat = false;
    //
    // if (stamp.length() > 0) {
    // // 20110526T08:27:18
    // // split into 20110526
    // // and 08:27:18
    // String[] split = stamp.split("T");
    // int dateFromMessage = Integer.parseInt(split[0]);
    //
    // int hourFromMessage = Integer.parseInt(split[1].replace(":", ""));
    //
    // // if dateFromMessage < todaysDate it is an old Chat
    // isoldgroupchat = dateFromMessage < todaysDate;
    //
    // // if is still not old chat
    // if (!isoldgroupchat) {
    // // check if the time from Message < time now
    // isoldgroupchat = hourFromMessage < todaysHour;
    // }
    //
    // }
    // return isoldgroupchat;
    // }

    @Override
    public void messageSent(ChatRoom room, Message message) {
	_displaytype.messageSent(room, message);
    }

    // /**
    // * Extracts the time stamp from a given xmpp packet
    // *
    // * @param xmlstring
    // * @return String like <b>20110526T08:27:18</b>, split at <b>"T"</b>
    // */
    // private String extractDate(String xmlstring) {
    // int indexofstamp = xmlstring.indexOf("stamp=");
    // String result = xmlstring
    // .substring(indexofstamp + 7, indexofstamp + 24)
    // .replace("-", "");
    // return result;
    //
    // }

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
