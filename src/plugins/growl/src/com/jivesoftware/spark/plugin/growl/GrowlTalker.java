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
package com.jivesoftware.spark.plugin.growl;

import info.growl.Growl;
import info.growl.GrowlCallbackListener;
import info.growl.GrowlException;
import info.growl.GrowlUtils;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.log.Log;

/**
 * GrowlTalker Class to send Messages to the GrowlInstance
 * 
 * @author Wolf.Posdorfer
 * 
 */
public class GrowlTalker implements GrowlCallbackListener {

    private Growl _growl;
    private final String SPARK = "Spark";

    public GrowlTalker() throws GrowlException {

	_growl = GrowlUtils.getGrowlInstance(SPARK);

	_growl.addNotification(SPARK, true);
	_growl.addCallbackListener(this);

	_growl.register();

    }

    @Override
    public void notificationWasClicked(String arg0) {
	String jid = StringUtils.parseBareAddress(arg0);
	ChatRoom room = SparkManager.getChatManager().getChatRoom(jid);

	SparkManager.getChatManager().getChatContainer().activateChatRoom(room);
	SparkManager.getChatManager().getChatContainer().requestFocusInWindow();

    }

    /**
     * Sends a simple Notification
     * 
     * @param name
     * @param title
     * @param body
     */
    public void sendNotification(String title, String body) {
	try {
	    _growl.sendNotification(SPARK, title, body);
	} catch (GrowlException e) {
	    Log.error("growl error", e);
	}

    }

    /**
     * Sends a notification with a CallBackContext
     * 
     * @param title
     *            the title to display
     * @param body
     *            the body to display
     * @param callbackContext
     *            a callback context
     */
    public void sendNotificationWithCallback(String title, String body, String callbackContext) {
	try {
	    _growl.sendNotification(SPARK, title, body, callbackContext);
	} catch (GrowlException e) {
	    System.out.println("growl error" + e);
	}

    }

}
