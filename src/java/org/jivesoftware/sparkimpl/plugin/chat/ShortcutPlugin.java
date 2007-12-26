/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
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
