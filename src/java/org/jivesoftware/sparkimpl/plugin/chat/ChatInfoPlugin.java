/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.chat;

import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactInfoHandler;
import org.jivesoftware.spark.ui.ContactInfo;
import org.jivesoftware.spark.ui.ChatRoomButton;

/**
 *
 */
public class ChatInfoPlugin implements Plugin {


    public void initialize() {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addContactInfoHandler(new ContactInfoHandler() {
            public void handleContactInfo(ContactInfo contactInfo) {
                final ChatRoomButton button = new ChatRoomButton("HOLA");
                contactInfo.addChatRoomButton(button);
            }
        });
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
    }
}
