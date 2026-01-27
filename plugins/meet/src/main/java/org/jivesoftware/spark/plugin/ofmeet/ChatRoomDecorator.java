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

import javax.swing.ImageIcon;

import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.log.*;
import org.jivesoftware.smack.packet.*;

import javax.xml.bind.DatatypeConverter;
import org.jivesoftware.resource.SparkRes;

public class ChatRoomDecorator
{
    public RolloverButton ofmeetButton;
    public final ChatRoom room;
	public static String ICON_STRING = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAKDSURBVDhPXVM7axVBFP5m7maN5gFyJTdNEEVDUt3C/yAIKkRSidjEUsTKRyrtLSyi/gArSRfFOiBqY5UqICaRYDCvm7177+7Ozj7G70wSSJzlmzk75/WdmTMqLwq3ubKCcGQEOgjgBgKoYACq0YBrKGh+rqrh6gKqqFGVBVCUKJMexqenoba2t937qSmcu3QRmk5aN7yz4tfQGrVzkOG4ermuUBPdzT+4v7wMHYYhMg3k1JkaSJktyQyigwPsdfYRRV0keY7EWpiyRFpWMGJbVQjPDCLQSiGJe9Bxn2lI0VUYao1jcmYGDTJJow6+v3mH860xlkSGcNBOI4oPyBGgv0bKmpIiR7+wPmP7ziyuP37EWi2utK/hdZKgZwyyNEWaW29r8hJK84w0p5yRLBWWNAVRt4vVL9+w+OIlXt27i6+LHzC78BbR7i5y6gUZfRTZ0531EDlrt5VDQWS5Qd9knmJIbP1cg7HG6wViK0lFHwiDHQqms8eZTGqHDg9tdH8XG/y/PDmJG/PP8XC8hR5Pv087cfxLaEq8KEbkT8lAHpTjuIuxdhvzH5dw8+kTzDWb2NneQR3oU3aHh8hFAuQ8WSugbEjxx6fPeHbrNhbmHqDb6bA3gkM9IbapBOCtkYA7OgPeLyHBMpvD8r4HKQtN9qDXHcPbUsd7lxKopGjJR8BGRZJkiPsxqhP7/0MS0R/K9mN3dXgUY7JxNKQMqXGIOGzk04P++E382lgnQ7avZK25e4yA/0JfgggLjxN6gfhI+Srd33PDzQtoycbR4NPwmY9XGbIyl88u2CKi1VWosq5db30N4dkh35q+tRRfI7tMwImJ6MpsruZKyMussgzhxAT+AQRKd557vsR7AAAAAElFTkSuQmCC";

    public ChatRoomDecorator(final ChatRoom room, final SparkMeetPlugin plugin)
    {
        this.room = room;

        try {
            byte[] imageByte = DatatypeConverter.parseBase64Binary(ICON_STRING);
            ImageIcon ofmeetIcon = new ImageIcon(imageByte);
            ofmeetButton = new RolloverButton(SparkRes.getImageIcon("PADE_ICON"));
            ofmeetButton.setToolTipText(GraphicUtils.createToolTip(SparkMeetResource.getString("name")));
            final String roomId = room.getBareJid().getLocalpart().toString();
            final String sessionID = String.valueOf(System.currentTimeMillis());

            ofmeetButton.addActionListener(event -> {
                String newUrl, newRoomId = roomId + "-" + sessionID;

                if (room.getChatType() == Message.Type.groupchat)
                {
                    newUrl = plugin.url + newRoomId;
                    plugin.handleClick(newUrl, room, newUrl, Message.Type.groupchat);
                } else {
                    newUrl = plugin.url + newRoomId;
                    plugin.handleClick(newUrl, room, newUrl, Message.Type.chat);
                }
            });
            room.getEditorBar().add(ofmeetButton);

        } catch (Exception e) {
            Log.error("cannot create pade meetings icon", e);
        }
    }

    public void finished()
    {
        Log.debug("ChatRoomDecorator: finished " + room.getBareJid());
    }

}
