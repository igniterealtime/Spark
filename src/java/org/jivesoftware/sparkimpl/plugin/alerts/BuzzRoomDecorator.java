/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

import javax.swing.JLabel;

/**
 * Adds a simple buzz operation button the each newly created ChatRoom.
 *
 * @author Derek DeMoro
 */
public class BuzzRoomDecorator implements ActionListener, ChatRoomClosingListener {

    private ChatRoom chatRoom;
    private RolloverButton buzzButton;

    public BuzzRoomDecorator(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;

        buzzButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.BUZZ_IMAGE));
        buzzButton.setToolTipText(Res.getString("message.buzz.alert.notification"));
        buzzButton.addActionListener(this);

        final JLabel dividerLabel = new JLabel(SparkRes.getImageIcon("DIVIDER_IMAGE"));
        chatRoom.getEditorBar().add(dividerLabel);
        chatRoom.getEditorBar().add(buzzButton);

        chatRoom.addClosingListener(this);
    }


    public void actionPerformed(ActionEvent e) {
        final String jid = ((ChatRoomImpl)chatRoom).getParticipantJID();
        Message message = new Message();
        message.setTo(jid);
        message.addExtension(new BuzzPacket());
        SparkManager.getConnection().sendPacket(message);

        chatRoom.getTranscriptWindow().insertNotificationMessage(Res.getString("message.buzz.sent"), ChatManager.NOTIFICATION_COLOR);
        buzzButton.setEnabled(false);

        // Enable the button after 30 seconds to prevent abuse.
        final TimerTask enableTask = new SwingTimerTask() {
            public void doRun() {
                buzzButton.setEnabled(true);
            }
        };

        TaskEngine.getInstance().schedule(enableTask, 30000);
    }


    public void closing() {
        chatRoom.removeClosingListener(this);
        buzzButton.removeActionListener(this);
    }
}
