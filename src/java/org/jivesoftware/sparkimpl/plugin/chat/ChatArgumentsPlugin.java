/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.chat;

import org.jivesoftware.Spark;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;

public class ChatArgumentsPlugin implements Plugin {

    public void initialize() {
        String start_chat_jid = Spark.getArgumentValue("start_chat_jid");
        String start_chat_muc = Spark.getArgumentValue("start_chat_muc");

        if (start_chat_jid != null) {
            String nickname = StringUtils.parseName(start_chat_jid);
            SparkManager.getChatManager().createChatRoom(start_chat_jid, nickname, start_chat_jid);
        }

        if (start_chat_muc != null) {
            ConferenceUtils.joinConferenceOnSeperateThread(start_chat_muc, start_chat_muc, null);
        }

    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
        // Do nothing.
    }
}
