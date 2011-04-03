/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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

import java.net.URL;
import javax.swing.ImageIcon;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;
import com.apple.eawt.Application;

/**
 * Utilities for dealing with the apple dock
 * 
 * @author Wolf.Posdorfer
 */
public final class AppleUtils {

    private boolean _flash;
    final Application _app;
    private boolean _usingDefaultIcon;

    @SuppressWarnings("deprecation")
    public AppleUtils() {
	_app = new Application();

	final Thread iconThread = new Thread(new Runnable() {

	    public void run() {
		while (true) {
		    if (!_flash) {
			setDockBadge(_app, getMessageCount());
			try {
			    Thread.sleep(100);
			} catch (InterruptedException e) {
			    Log.error(e);
			}
		    } else {

			setDockBadge(_app, getMessageCount());
			try {
			    Thread.sleep(500);
			} catch (InterruptedException e) {
			    // Nothing to do
			}
			setDockBadge(_app, getMessageCount());
			try {
			    Thread.sleep(500);
			} catch (InterruptedException e) {
			    // Nothing to do
			}

			_usingDefaultIcon = false;
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
	} else
	    return "" + no;

    }

    /**
     * Sets the Dock badge off the {@link Application} to the specified String
     * 
     * @param app
     * @param s
     */
    public static void setDockBadge(Application app, String s) {
	app.setDockIconBadge(s);

    }

    /**
     * Returns the Default ImageIcon for the Dock
     * 
     * @return
     */
    public static ImageIcon getDefaultImage() {
	ClassLoader loader = ApplePlugin.class.getClassLoader();
	URL url = loader.getResource("images/Spark-Dock-256-On.png");
	return new ImageIcon(url);
    }

    /**
     * Bounce the application's dock icon to get the user's attention.
     * 
     * @param critical
     *            Bounce the icon repeatedly if this is true. Bounce it only for
     *            one second (usually just one bounce) if this is false.
     */
    public void bounceDockIcon(boolean critical) {
	_app.requestUserAttention(critical);
    }

}
