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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.swing.JLabel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;

/**
 * Adds a simple buzz operation button the each newly created ChatRoom.
 *
 * @author Derek DeMoro
 */
public class BuzzRoomDecorator implements ActionListener {

    private ChatRoom chatRoom;
    private RolloverButton buzzButton;
    private static ArrayList<BuzzRoomDecorator> objects = new ArrayList<BuzzRoomDecorator>();
    private String jid;
    
    public BuzzRoomDecorator(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        jid = ((ChatRoomImpl)chatRoom).getParticipantJID();
        boolean added = false;
        
        for(BuzzRoomDecorator buzz : objects)
        {
      	  if(buzz.jid == ((ChatRoomImpl)chatRoom).getParticipantJID())
      	  {
      		  addBuzzButton(buzz);
      		  added = true;
      	  }
        }
        
        if(!added)
        {
	        buzzButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.BUZZ_IMAGE));
	        buzzButton.setToolTipText(Res.getString("message.buzz.alert.notification"));
	        buzzButton.addActionListener(this);
	
	        final JLabel dividerLabel = new JLabel(SparkRes.getImageIcon("DIVIDER_IMAGE"));
	        chatRoom.getEditorBar().add(dividerLabel);
	        chatRoom.getEditorBar().add(buzzButton);
	        objects.add(this);
        }
    }

    public void addBuzzButton(BuzzRoomDecorator buzzer)
    {
    	final JLabel dividerLabel = new JLabel(SparkRes.getImageIcon("DIVIDER_IMAGE"));
    	chatRoom.getEditorBar().add(dividerLabel);
    	chatRoom.getEditorBar().add(buzzer.buzzButton);
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
}
