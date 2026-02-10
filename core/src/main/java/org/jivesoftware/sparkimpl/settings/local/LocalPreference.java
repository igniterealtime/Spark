/**
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

package org.jivesoftware.sparkimpl.settings.local;

import org.jivesoftware.resource.Res;
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

    @Override
	public String getTitle() {
        return Res.getString("title.login.settings");
    }

    @Override
	public String getListName() {
        return Res.getString("title.login");
    }

    @Override
	public String getTooltip() {
        return Res.getString("title.login.settings");
    }

    @Override
	public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.LOGIN_KEY_IMAGE);
    }

    @Override
	public void load() {
        preferences = SettingsManager.getLocalPreferences();
    }

    @Override
	public void commit() {
        getData();
    }

    @Override
	public Object getData() {
        preferences = SettingsManager.getLocalPreferences();
        preferences.setAutoLogin(panel.getAutoLogin());
        preferences.setTimeOut(Integer.parseInt(panel.getTimeout()));
        preferences.setReconnectDelay(Integer.parseInt(panel.getReconnectDelay()));
        preferences.setXmppPort(Integer.parseInt(panel.getPort()));
        preferences.setSavePassword(panel.isSavePassword());
        preferences.setIdleOn(panel.isIdleOn());
        preferences.setIdleTime(Integer.parseInt(panel.getIdleTime()));
        preferences.setStartedHidden(panel.startInSystemTray());
        preferences.setStartOnStartup(panel.startOnStartup());
        preferences.setIdleMessage(panel.getIdleMessage());
        preferences.setUsingSingleTrayClick(panel.useSingleClickInTray());

        return preferences;
    }

    @Override
	public String getErrorMessage() {
        return errorMessage;
    }

    @Override
	public boolean isDataValid() {
        preferences.setTimeOut(Integer.parseInt(panel.getTimeout()));
        preferences.setXmppPort(Integer.parseInt(panel.getPort()));

        try {
            Integer.parseInt(panel.getTimeout());
            Integer.parseInt(panel.getPort());
            Integer.parseInt(panel.getIdleTime());
        }
        catch (Exception ex) {
            errorMessage = Res.getString("message.specify.valid.time.error");
            return false;
        }

        int timeOut = Integer.parseInt(panel.getTimeout());
        if (timeOut < 5) {
            errorMessage = Res.getString("message.timeout.error");
            return false;
        }

        return true;
    }

    @Override
	public JComponent getGUI() {
        panel = new LocalPreferencePanel();

        return panel;
    }

    @Override
	public String getNamespace() {
        return "LOGIN";
    }

    @Override
	public void shutdown() {
        // Commit to file.
    }

}
