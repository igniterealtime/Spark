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

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.NativeHandler;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

/**
 * Plugins for handling Mac OS X specific functionality supports 10.6+
 * 
 * @author Wolf Posdorfer
 */
public class ApplePlugin implements Plugin, NativeHandler {

    @SuppressWarnings("unused")
    private AppleDock _appledock;

    private AppleBounce _applebounce;
    private boolean unavailable;
    private int previousPriority;
    private boolean addedFrameListener;
    private long lastActive;

    private ChatFrame chatFrame;

    private Application _application;

    private AppleProperties _props;

    @SuppressWarnings("deprecation")
    public void initialize() {

	_props = new AppleProperties();
	ApplePreference pref = new ApplePreference(_props);
	SparkManager.getPreferenceManager().addPreference(pref);

	_applebounce = new AppleBounce(_props);
	_appledock = new AppleDock();

	SparkManager.getNativeManager().addNativeHandler(this);

	handleIdle();

	// // register an application listener to show the about box
	_application = Application.getApplication();

	_application.setEnabledPreferencesMenu(true);
	_application.addPreferencesMenuItem();
	_application.addApplicationListener(new ApplicationAdapter() {

	    public void handlePreferences(ApplicationEvent applicationEvent) {
		SparkManager.getPreferenceManager().showPreferences();
	    }

	    public void handleReOpenApplication(ApplicationEvent event) {
		MainWindow mainWindow = SparkManager.getMainWindow();
		if (!mainWindow.isVisible()) {
		    mainWindow.setState(Frame.NORMAL);
		    mainWindow.setVisible(true);
		}

		if (SparkManager.getChatManager().getChatContainer()
			.getTotalNumberOfUnreadMessages() > 0) {
		    final ChatFrame frame = SparkManager.getChatManager().getChatContainer()
			    .getChatFrame();
		    frame.setState(Frame.NORMAL);
		    frame.setVisible(true);
		    frame.toFront();
		}
	    }

	    public void handleQuit(ApplicationEvent applicationEvent) {
		SparkManager.getMainWindow().shutdown();
	    }

	});

    }

    public void shutdown() {
	if (Spark.isMac()) {
	    SparkManager.getNativeManager().removeNativeHandler(this);
	}
    }

    public boolean canShutDown() {
	return false;
    }

    public void uninstall() {
	// No need, since this is internal
    }

    public void flashWindow(Window window) {
	if (_props.getDockBounce())
	    _applebounce.bounceDockIcon(_props.getRepeatBounce());
    }

    public void flashWindowStopWhenFocused(Window window) {

    }

    public void stopFlashing(Window window) {
	_applebounce.resetDock();
    }

    public boolean handleNotification() {
	return Spark.isMac();
    }

    private void handleIdle() {
	SparkManager.getMainWindow().addComponentListener(new ComponentListener() {
	    public void componentResized(ComponentEvent componentEvent) {
		setActivity();
	    }

	    public void componentMoved(ComponentEvent componentEvent) {
		setActivity();
	    }

	    public void componentShown(ComponentEvent componentEvent) {
		setActivity();
	    }

	    public void componentHidden(ComponentEvent componentEvent) {
		setActivity();
	    }
	});

	SparkManager.getChatManager().addChatRoomListener(new ChatRoomListenerAdapter() {
	    public void chatRoomOpened(ChatRoom room) {
		if (!addedFrameListener) {
		    chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
		    chatFrame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent componentEvent) {
			    setActivity();
			}

			public void componentMoved(ComponentEvent componentEvent) {
			    setActivity();
			}

			public void componentShown(ComponentEvent componentEvent) {
			    setActivity();
			}

			public void componentHidden(ComponentEvent componentEvent) {
			    setActivity();
			}
		    });

		    addedFrameListener = true;

		}

		setActivity();
	    }

	    public void chatRoomClosed(ChatRoom room) {
		setActivity();
	    }
	});

	SparkManager.getSessionManager().addPresenceListener(new PresenceListener() {
	    public void presenceChanged(Presence presence) {
		if (presence.isAvailable() && !presence.isAway()) {
		    lastActive = System.currentTimeMillis();
		}
	    }
	});

	final Timer timer = new Timer();
	timer.scheduleAtFixedRate(new TimerTask() {
	    public void run() {
		sparkIsIdle();
	    }
	}, 10000, 10000);

	lastActive = System.currentTimeMillis();

    }

    public void setActivity() {
	lastActive = System.currentTimeMillis();
	setAvailableIfActive();
    }

    private void sparkIsIdle() {
	LocalPreferences localPref = SettingsManager.getLocalPreferences();
	if (!localPref.isIdleOn()) {
	    return;
	}

	try {
	    // Handle if spark is not connected to the server.
	    if (SparkManager.getConnection() == null || !SparkManager.getConnection().isConnected()) {
		return;
	    }

	    // Change Status
	    Workspace workspace = SparkManager.getWorkspace();
	    if (workspace != null) {
		Presence presence = workspace.getStatusBar().getPresence();
		long diff = System.currentTimeMillis() - lastActive;
		boolean idle = diff > 60000 * 60;
		if (presence.getMode() == Presence.Mode.available && idle) {
		    unavailable = true;
		    StatusItem away = workspace.getStatusBar().getStatusItem("Away");
		    Presence p = away.getPresence();
		    p.setStatus(Res.getString("message.away.idle"));

		    previousPriority = presence.getPriority();

		    p.setPriority(0);

		    SparkManager.getSessionManager().changePresence(p);
		}
	    }
	} catch (Exception e) {
	    Log.error("Error with IDLE status.", e);
	}
    }

    private void setAvailableIfActive() {
	if (!unavailable) {
	    return;
	}
	// Handle if spark is not connected to the server.
	if (SparkManager.getConnection() == null || !SparkManager.getConnection().isConnected()) {
	    return;
	}

	// Change Status
	Workspace workspace = SparkManager.getWorkspace();
	if (workspace != null) {
	    Presence presence = workspace.getStatusBar().getStatusItem(Res.getString("available"))
		    .getPresence();
	    if (previousPriority != -1) {
		presence.setPriority(previousPriority);
	    }

	    SparkManager.getSessionManager().changePresence(presence);
	    unavailable = false;
	    lastActive = System.currentTimeMillis();
	}
    }

    public boolean openFile(File file) {
	return false;
    }

    public boolean launchEmailClient(String to, String subject) {
	return false;
    }

    public boolean launchBrowser(String url) {
	try {
	    BrowserLauncher.openURL(url);
	} catch (IOException e) {
	    Log.error(e);
	} catch (Exception e) {
	    Log.error(e);
	}
	return true;
    }
}
