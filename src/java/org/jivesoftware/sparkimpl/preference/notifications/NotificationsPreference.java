/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.notifications;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Handles the preferences for notification behavior within the Spark IM Client.
 * 
 * @author Derek DeMoro
 */
public class NotificationsPreference implements Preference {

	private NotificationsUI panel = new NotificationsUI();

	/**
	 * Define the Namespace used for this preference.
	 */
	public static final String NAMESPACE = "http://www.jivesoftware.org/spark/notifications";

	public String getTitle() {
		return Res.getString("title.notifications");
	}

	public String getListName() {
		return Res.getString("title.notifications");
	}

	public String getTooltip() {
		return Res.getString("tooltip.notifications");
	}

	public Icon getIcon() {
		return SparkRes.getImageIcon(SparkRes.PROFILE_ICON);
	}

	public void load() {
		SwingWorker thread = new SwingWorker() {
			LocalPreferences localPreferences;

			public Object construct() {
				localPreferences = SettingsManager.getLocalPreferences();
				return localPreferences;
			}

			public void finished() {
				boolean toaster = localPreferences.getShowToasterPopup();
				boolean windowFocus = localPreferences.getWindowTakesFocus();
				boolean offlineNotification = localPreferences
						.isOfflineNotificationsOn();
				boolean onlineNotification = localPreferences
						.isOnlineNotificationsOn();
				boolean betaChecking = localPreferences.isBetaCheckingEnabled();
				boolean typingNotification = localPreferences
						.isTypingNotificationShown();
				boolean systemTrayNotification = localPreferences
						.isSystemTrayNotificationEnabled();

				panel.setShowToaster(toaster);
				panel.setShowWindowPopup(windowFocus);
				panel.setOfflineNotification(offlineNotification);
				panel.setOnlineNotification(onlineNotification);
				panel.setCheckForBeta(betaChecking);
				panel.setTypingNotification(typingNotification);
				panel.setSystemTrayNotification(systemTrayNotification);
			}
		};

		thread.start();

	}

	public void commit() {
		LocalPreferences pref = SettingsManager.getLocalPreferences();

		pref.setShowToasterPopup(panel.showToaster());
		pref.setWindowTakesFocus(panel.shouldWindowPopup());
		pref.setOfflineNotifications(panel.isOfflineNotificationOn());
		pref.setOnlineNotifications(panel.isOnlineNotificationOn());
		pref.setCheckForBeta(panel.isBetaCheckingEnabled());
		pref.setTypingNotificationOn(panel.isTypingNotification());
		pref.setSystemTrayNotificationEnabled(panel.isSystemTrayNotificationEnabled());
		SettingsManager.saveSettings();
	}

	public Object getData() {
		return SettingsManager.getLocalPreferences();
	}

	public String getErrorMessage() {
		return "";
	}

	public boolean isDataValid() {
		return true;
	}

	public JComponent getGUI() {
		return panel;
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void shutdown() {
		commit();
	}

}
