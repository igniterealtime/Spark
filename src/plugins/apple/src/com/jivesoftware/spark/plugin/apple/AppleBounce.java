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
package com.jivesoftware.spark.plugin.apple;

import org.jivesoftware.spark.SparkManager;

import com.apple.eawt.Application;

/**
 * Utilities for dealing with the apple dockicon
 * 
 * @author Wolf.Posdorfer
 */
public final class AppleBounce {

    private boolean _flash;
    final Application _app;
    private AppleProperties _props;

    @SuppressWarnings("deprecation")
    public AppleBounce(AppleProperties props) {
	_app = new Application();
	_props = props;

	final Thread iconThread = new Thread(new Runnable() {

	    public void run() {
		while (true) {
		    if (!_flash) {
			setDockBadge(_app, getMessageCount());
			try {
			    Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		    } else {

			setDockBadge(_app, getMessageCount());
			try {
			    Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			setDockBadge(_app, getMessageCount());
			try {
			    Thread.sleep(500);
			} catch (InterruptedException e) {
			}

		    }

		}
	    }
	});

	iconThread.start();

    }

    /**
     * Sets Flashing to false
     */
    public void resetDock() {
	if (_flash) {
	    _flash = false;
	}
    }

    /**
     * Returns a String of the current unread message count
     * 
     * @return String like "10"
     */
    public static String getMessageCount() {
	int no = SparkManager.getChatManager().getChatContainer().getTotalNumberOfUnreadMessages();

	if (no > 999)
	    no = 999;

	if (no == 0) {
	    return "";
	} else {
	    return "" + no;
	}

    }

    /**
     * Sets the Dock badge off the {@link Application} to the specified String
     * 
     * @param app
     * @param s
     */
    public void setDockBadge(Application app, String s) {
	if (_props.getDockBadges())
	    app.setDockIconBadge(s);
    }

    /**
     * Bounce the application's dock icon to get the user's attention.
     * 
     * @param critical
     *            Bounce the icon repeatedly if this is true. Bounce it only
     *            once if this is false.
     */
    public void bounceDockIcon(boolean critical) {
	_app.requestUserAttention(critical);
    }

}
