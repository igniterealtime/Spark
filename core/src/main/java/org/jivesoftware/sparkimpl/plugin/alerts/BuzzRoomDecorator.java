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
package org.jivesoftware.sparkimpl.plugin.alerts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.attention.packet.AttentionExtension;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * Adds a simple buzz operation button the each newly created ChatRoom.
 *
 * @author Derek DeMoro
 */
public class BuzzRoomDecorator implements ActionListener {

    private final ChatRoom chatRoom;
    private final JButton buzzButton;


    public BuzzRoomDecorator(ChatRoom chatRoom) {
	this.chatRoom = chatRoom;

	buzzButton = UIComponentRegistry.getButtonFactory().createBuzzButton();
	buzzButton.setToolTipText(Res
		.getString("message.buzz.alert.notification"));
	buzzButton.addActionListener(this);

	final JLabel dividerLabel = UIComponentRegistry.getButtonFactory().createDivider();
	if (dividerLabel != null) {
	    chatRoom.addEditorComponent(dividerLabel);
	}
	chatRoom.addEditorComponent(buzzButton);
    }

    public void addBuzzButton(BuzzRoomDecorator buzzer)
    {
    	final JLabel dividerLabel = new JLabel(SparkRes.getImageIcon("DIVIDER_IMAGE"));
    	chatRoom.addEditorComponent(dividerLabel);
    	chatRoom.addEditorComponent(buzzer.buzzButton);
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        Jid jid;
        try {
            jid = JidCreate.from(((ChatRoomImpl)chatRoom).getParticipantJID());
        } catch (XmppStringprepException exception) {
            throw new IllegalStateException(exception);
        }

        XMPPConnection connection = SparkManager.getConnection();
        Message message = connection.getStanzaFactory()
            .buildMessageStanza()
            .to(jid)
            .addExtension(new AttentionExtension())
            .build();

        try
        {
            connection.sendStanza(message);
        }
        catch ( SmackException.NotConnectedException | InterruptedException e1 )
        {
            Log.warning( "Unable to send stanza to " + jid, e1 );
        }

        chatRoom.getTranscriptWindow().insertNotificationMessage(Res.getString("message.buzz.sent"), ChatManager.NOTIFICATION_COLOR);
        buzzButton.setEnabled(false);

        // Enable the button after 30 seconds to prevent abuse.
        final TimerTask enableTask = new SwingTimerTask() {
            @Override
			public void doRun() {
                buzzButton.setEnabled(true);
            }
        };

        TaskEngine.getInstance().schedule(enableTask, 30000);
    }
}
