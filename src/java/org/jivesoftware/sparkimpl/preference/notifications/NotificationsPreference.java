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
				boolean asteriskToaster = localPreferences.getDisableAsteriskToasterPopup();
				boolean windowFocus = localPreferences.getWindowTakesFocus();
				boolean offlineNotification = localPreferences
						.isOfflineNotificationsOn();
				boolean onlineNotification = localPreferences
						.isOnlineNotificationsOn();
				boolean betaChecking = localPreferences.isBetaCheckingEnabled();
				int DisplayTime = localPreferences.getNotificationsDisplayTime();
				boolean typingNotification = localPreferences
						.isTypingNotificationShown();
				boolean systemTrayNotification = localPreferences
						.isSystemTrayNotificationEnabled();

				panel.setShowToaster(toaster);
				panel.setDisableAsteriskToaster(asteriskToaster);
				panel.setShowWindowPopup(windowFocus);
				panel.setOfflineNotification(offlineNotification);
				panel.setOnlineNotification(onlineNotification);
				panel.setCheckForBeta(betaChecking);
				panel.setNotificationsDisplayTime(DisplayTime/1000);
				panel.setTypingNotification(typingNotification);
				panel.setSystemTrayNotification(systemTrayNotification);
				
				// when windowFocus is selected the systemtraynotification doesn't work --> disable it
				if(windowFocus) {
					panel.setSystemTrayNotification(false);
					panel.setSystemTrayNotificationEnabled(false);
				}
				else
					panel.setSystemTrayNotificationEnabled(true);
				
				if(systemTrayNotification) {
					panel.setShowWindowPopup(false);
					panel.setShowWindowPopupEnabled(false);
				}
				else
					panel.setShowWindowPopupEnabled(true);
			}
		};

		thread.start();

	}

	public void commit() {
		LocalPreferences pref = SettingsManager.getLocalPreferences();

		pref.setShowToasterPopup(panel.showToaster());
		pref.setDisableAsteriskToasterPopup(panel.disableAsteriskToaster());
		pref.setWindowTakesFocus(panel.shouldWindowPopup());
		pref.setNotificationsDisplayTime(panel.getNotificationsDisplayTime());
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
