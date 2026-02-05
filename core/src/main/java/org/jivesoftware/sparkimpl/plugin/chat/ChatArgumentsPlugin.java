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
package org.jivesoftware.sparkimpl.plugin.chat;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import java.util.Objects;

public class ChatArgumentsPlugin implements Plugin {

    @Override
	public void initialize() {
        String value;
        EntityBareJid start_chat_jid = (value = Spark.getArgumentValue("start_chat_jid")) == null ? null
            : JidCreate.entityBareFromUnescapedOrThrowUnchecked(Objects.requireNonNull(value));
        EntityBareJid start_chat_muc = (value = Spark.getArgumentValue("start_chat_muc")) == null ? null
            : JidCreate.entityBareFromUnescapedOrThrowUnchecked(Objects.requireNonNull(value));

        if (start_chat_jid != null) {
            Localpart nickname = start_chat_jid.getLocalpart();
            SparkManager.getChatManager().createChatRoom(start_chat_jid, nickname.toString(), start_chat_jid.toString());
        }

        if (start_chat_muc != null) {
            ConferenceUtils.joinConferenceOnSeparateThread(start_chat_muc, start_chat_muc, null, null);
        }
    }

    @Override
	public void shutdown() {

    }

    @Override
	public boolean canShutDown() {
        return false;
    }

    @Override
	public void uninstall() {
        // Do nothing.
    }
}
