/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.settings.local;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Represents the Local Preference inside the Preference Manager.
 */
public class LocalPreference implements Preference {
    private LocalPreferencePanel panel;
    private LocalPreferences preferences;
    private String errorMessage = "Error";

    /**
     * Initalize and load local preference.
     */
    public LocalPreference() {
        preferences = SettingsManager.getLocalPreferences();
    }

    public String getTitle() {
        return "Login Settings";
    }

    public String getListName() {
        return "Login";
    }

    public String getTooltip() {
        return "Spark Login Settings";
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.LOGIN_KEY_IMAGE);
    }

    public void load() {
        preferences = SettingsManager.getLocalPreferences();
    }

    public void commit() {
        getData();
    }

    public Object getData() {
        preferences = SettingsManager.getLocalPreferences();
        preferences.setAutoLogin(panel.getAutoLogin());
        preferences.setTimeOut(Integer.parseInt(panel.getTimeout()));
        preferences.setXmppPort(Integer.parseInt(panel.getPort()));
        preferences.setSavePassword(panel.isSavePassword());
        preferences.setIdleOn(panel.isIdleOn());
        preferences.setIdleTime(Integer.parseInt(panel.getIdleTime()));
        preferences.setStartedHidden(panel.startInSystemTray());

        return preferences;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isDataValid() {
        preferences.setTimeOut(Integer.parseInt(panel.getTimeout()));
        preferences.setXmppPort(Integer.parseInt(panel.getPort()));

        try {
            Integer.parseInt(panel.getTimeout());
            Integer.parseInt(panel.getPort());
            Integer.parseInt(panel.getIdleTime());
        }
        catch (Exception ex) {
            errorMessage = "You must specify a valid timeout and port.";
            return false;
        }

        int timeOut = Integer.parseInt(panel.getTimeout());
        if (timeOut < 5) {
            errorMessage = "The timeout must be 5 seconds or greater.";
            return false;
        }

        return true;
    }

    public JComponent getGUI() {
        panel = new LocalPreferencePanel();

        return panel;
    }

    public String getNamespace() {
        return "LOGIN";
    }

    public void shutdown() {
        // Commit to file.
        SettingsManager.saveSettings();
    }

}