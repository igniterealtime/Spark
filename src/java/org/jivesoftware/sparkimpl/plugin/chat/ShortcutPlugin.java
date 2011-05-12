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

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.TranscriptWindowInterceptor;
import org.jivesoftware.spark.util.ModelUtil;

import java.awt.Color;

/**
 * The ShortcutPlugin is used to handle IRC-style shortcuts.
 */
public class ShortcutPlugin implements Plugin, TranscriptWindowInterceptor {


    public void initialize() {
        // Add TranscriptWindowInterceptor
        SparkManager.getChatManager().addTranscriptWindowInterceptor(this);
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
    }


    public boolean isMessageIntercepted(TranscriptWindow window, String userid, Message message) {
        String body = message.getBody();
        if (ModelUtil.hasLength(body) && body.startsWith("/me ")) {
            body = body.replaceFirst("/me", userid);
            window.insertNotificationMessage(body, Color.MAGENTA);
            return true;
        }
        return false;
    }

}
