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
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.preference.Preference;
import org.jxmpp.jid.EntityBareJid;

import javax.swing.*;

/**
 * Represents the Local Preference inside the Preference Manager.
 */
public class LocalPreference implements Preference {
    public static final String NAMESPACE = "LOGIN";
    private LocalPreferencePanel panel;
    private final LocalPreferences preferences = SettingsManager.getLocalPreferences();
    private String errorMessage = "Error";

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
        return SparkRes.getImageIcon(SparkRes.Icon.LOGIN_KEY_IMAGE);
    }

    @Override
	public void load() {
    }

    @Override
	public void commit() {
        // SPARK-1600: Remove a password the "Save password" is unchecked
        if (!panel.isSavePassword()) {
            EntityBareJid myBareJid = SparkManager.getSessionManager().getUserBareAddress();
            preferences.setPasswordForUser(myBareJid, null);
        }
        preferences.setAutoLogin(panel.getAutoLogin());
        preferences.setTimeOut(Integer.parseInt(panel.getTimeout()));
        preferences.setReconnectDelay(Integer.parseInt(panel.getReconnectDelay()));
        preferences.setXmppPort(Integer.parseInt(panel.getPort()));
        preferences.setSavePassword(panel.isSavePassword());
        preferences.setIdleOn(panel.isIdleOn());
        preferences.setIdleTime(Integer.parseInt(panel.getIdleTime()));
        preferences.setStartedHidden(panel.isStartInSystemTray());
        preferences.setStartOnStartup(panel.isStartOnStartup());
        preferences.setIdleMessage(panel.getIdleMessage());
        preferences.setUsingSingleTrayClick(panel.useSingleClickInTray());
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
        // UI panel field values are loaded inside the LocalPreferencePanel constructor
        return panel;
    }

    @Override
	public String getNamespace() {
        return NAMESPACE;
    }
}
