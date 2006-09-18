/**
 * $Revision: 22540 $
 * $Date: 2005-10-10 08:44:25 -0700 (Mon, 10 Oct 2005) $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */

package com.jivesoftware.spark.plugin.apple;


import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.MessageListener;

/**
 * When a message is received and the user does not have presence
 * the dock will bounce.
 *
 * @author Andrew Wright
 */
public class DockMessageListener implements MessageListener {


    public void messageReceived(ChatRoom room, Message message) {

        if (!SparkManager.getMainWindow().isFocused()) {
            AppleUtils.bounceDockIcon(false);
        }

    }

    public void messageSent(ChatRoom room, Message message) {
    }
}
