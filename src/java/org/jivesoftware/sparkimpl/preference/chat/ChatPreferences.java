/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.chat;

import org.jivesoftware.spark.SparkManager;

/**
 * Model representing the Chat Preferences within Spark.
 */
public class ChatPreferences {

    /**
     * Set default to not show timestamp.
     */
    private boolean showDatesInChat;


    /**
     * Set to true to show timestamp of messages.
     *
     * @param showDatesInChat true to show timestamp of messages.
     */
    public void showDatesInChat(boolean showDatesInChat) {
        this.showDatesInChat = showDatesInChat;
    }


    /**
     * Returns true if a timestamp should be used to show messages.
     *
     * @return true if the a timestamp should be used with the messages.
     */
    public boolean showDatesInChat() {
        return showDatesInChat;
    }

    /**
     * Returns the nickname used by the agent.
     *
     * @return the nickname used by the agent.
     */
    public String getNickname() {
        return SparkManager.getUserManager().getNickname();
    }
}