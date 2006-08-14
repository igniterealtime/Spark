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
        return "Notifications";
    }

    public String getListName() {
        return "Notifications";
    }

    public String getTooltip() {
        return "Notification preferences for incoming chats.";
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.USER1_MESSAGE_24x24);
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

                panel.setShowToaster(toaster);
                panel.setShowWindowPopup(windowFocus);
            }
        };

        thread.start();

    }

    public void commit() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();

        pref.setShowToasterPopup(panel.showToaster());
        pref.setWindowTakesFocus(panel.shouldWindowPopup());

        SettingsManager.saveSettings();
    }

    public Object getData() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        return pref;
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
