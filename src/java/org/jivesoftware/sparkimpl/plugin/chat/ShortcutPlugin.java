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


    public boolean interceptToMessage(TranscriptWindow window, String userid, Message message) {
        return checkForME(window, userid, message);
    }

    public boolean interceptFromMessage(TranscriptWindow window, String userid, Message message) {
        return checkForME(window, userid, message);
    }

    /**
     * Returns true if the message was handled by this interceptor.
     * @param window the TranscriptWindow.
     * @param userid the userid.
     * @param message the message being handled.
     * @return true if the message was handled.
     */
    private boolean checkForME(TranscriptWindow window, String userid, Message message) {
        String body = message.getBody();
        if (ModelUtil.hasLength(body) && body.startsWith("/me ")) {
            body = body.replaceAll("/me", userid);
            window.insertCustomNotification(body, Color.MAGENTA);
            return true;
        }
        return false;
    }

}
