/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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
