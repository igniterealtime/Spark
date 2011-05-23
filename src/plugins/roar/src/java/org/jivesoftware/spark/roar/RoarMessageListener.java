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

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.roar.displaytype.RoarDisplayType;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.GlobalMessageListener;

/**
 * Message Listener<br>
 * 
 * @author wolf.posdorfer
 * 
 */
public class RoarMessageListener implements GlobalMessageListener {

    private RoarDisplayType _displaytype;

    public RoarMessageListener() {

	_displaytype = RoarProperties.getInstance().getDisplayTypeClass();

    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {
	_displaytype.messageReceived(room, message);

    }

    @Override
    public void messageSent(ChatRoom room, Message message) {
	_displaytype.messageSent(room, message);
    }
    

}
