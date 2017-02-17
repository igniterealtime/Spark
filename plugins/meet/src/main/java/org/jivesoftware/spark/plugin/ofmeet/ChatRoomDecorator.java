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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.log.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import org.jxmpp.util.XmppStringUtils;

import sun.misc.BASE64Decoder;

public class ChatRoomDecorator
{
	public RolloverButton ofmeetButton;
	public ChatRoom room;


	private final String url;

	public ChatRoomDecorator(final ChatRoom room, final String url, final String server, final String port, final SparkMeetPlugin plugin)
	{
		this.room = room;
		this.url = url;

		try {
			BASE64Decoder decoder = new BASE64Decoder();
			String imageString = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACZUlEQVR42o1TS09TQRg999mWgtTGQosiLEQhXVA0Me5swsYFRjBxoyatvwBi4g9g66p7F+2exIDvBYldGOIzbUwMGqw2Sr1YH+21Qsud2zvOTB9UkMQv+e6de2fOme+c+QaUUtCdjLF8TPdGpv7rc4wUnoEYL2F/X4VTKwuMxB8selmmWUawK5zKOpzSB5D8Mqy1e1n2K6qPTJn6iRnIB49B3Q9cL74GtSoirXeLIGt3YPuNiKIhXX2SicKxTT18RRDMdYLtjVeQJBlW7iH7qMHZLMIurGC7x0D/VVFtxLwtzVm5R/P62KU2QQP85Tlo7SdI4Sns9RUG3gBIFZRsQvJ0yCKYk4F5PuYEPlHy1wwk1S3A5P0D2MYLkBDKUJBykVBaK3eVi7ckUBU+veyf1ifOQeoKCAIRlDogqwsgH5cFWBnFIgYQZ1Omy3sd1bs3oP32Q3b7oY5OLmkj5yH3HAGrBHlOwHVD9QiwcxSp3ot0xn+GgmVyW7lZsgOgsrefqsOTJVf4clI+MMjNFwSptjhZ4c88c/oae483yePewaRP5h7IKuTuoA+6N85253PjfxPULbYglLC2mHeV+4mWP5U3U6gTVqXi7jxpPpdQW0fIPZCYBMkbzGqGAXNpKqrxIhmQmIBmAMrESTAZnURRTjAtjiN4qmGmXYXFB58yqDdX6R5mXt9hBu6DMnAaSiDcLoO3Mm1qzbK2zdKtbwlrdcGEopfqpZyQoBwaE02lHb/Q3qgZ+dZd2LHhx9tG48hajLVzCno3lN4hIXEXWEjovIn/yrP8JtL9Y3ZPBf8RQ03feJMN/wHiFFIvPgJ8vgAAAABJRU5ErkJggg==";
			byte[] imageByte = decoder.decodeBuffer(imageString);
			ImageIcon ofmeetIcon = new ImageIcon(imageByte);
			ofmeetButton = new RolloverButton(ofmeetIcon);
			ofmeetButton.setToolTipText(GraphicUtils.createToolTip("Openfire Meetings"));
			final String roomId = getNode(room.getRoomname());
			final String sessionID = roomId + "-" + System.currentTimeMillis();
			final String nickname = getNode(XmppStringUtils.parseBareAddress(SparkManager.getSessionManager().getJID()));

			ofmeetButton.addActionListener( new ActionListener()
			{
					public void actionPerformed(ActionEvent event)
					{
						String newUrl, newRoomId;

						if ("groupchat".equals(room.getChatType().toString()))
						{
							newRoomId = roomId;
							newUrl = url + "r=" + newRoomId;
							sendInvite(room.getRoomname(), newUrl, Message.Type.groupchat);

						} else {

							newRoomId = sessionID;
							newUrl = url + "r=" + newRoomId;
							sendInvite(room.getRoomname(), newUrl, Message.Type.chat);
						}

						plugin.openURL(newRoomId);
					}
			});
			room.getEditorBar().add(ofmeetButton);

		} catch (Exception e) {

			Log.error("cannot create openfire meetings icon", e);
		}

	}

	public void finished()
	{
		Log.warning("ChatRoomDecorator: finished " + room.getRoomname());
	}

	private String getNode(String jid)
	{
		String node = jid;
		int pos = node.indexOf("@");

		if (pos > -1)
			node = jid.substring(0, pos);

		return node;
	}

	private void sendInvite(String jid, String url, Message.Type type)
	{
		Message message2 = new Message();
		message2.setTo(jid);
		message2.setType(type);
		message2.setBody(url);
		room.sendMessage(message2);
	}
}
