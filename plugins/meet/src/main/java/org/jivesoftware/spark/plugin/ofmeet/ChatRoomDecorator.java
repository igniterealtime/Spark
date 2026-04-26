/**
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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

package org.jivesoftware.spark.plugin.ofmeet;

import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.log.*;
import org.jxmpp.jid.parts.Localpart;

public class ChatRoomDecorator
{
    public RolloverButton ofmeetButton;

    public ChatRoomDecorator(final ChatRoom room, final SparkMeetPlugin plugin)
    {
        ofmeetButton = new RolloverButton(SparkMeetResource.PLUGIN_ICON);
        ofmeetButton.setToolTipText(GraphicUtils.createToolTip(SparkMeetResource.getString("name")));
        Localpart roomId = room.getJid().getLocalpart();
        final String sessionID = String.valueOf(System.currentTimeMillis());

        ofmeetButton.addActionListener(event -> {
            String newRoomId = roomId + "-" + sessionID;
            String meetUrl = plugin.url + newRoomId;
            plugin.handleClick(room, meetUrl);
        });
        room.getEditorBar().add(ofmeetButton);
    }

    public void finished()
    {
        Log.debug("ChatRoomDecorator: finished");
    }

}
