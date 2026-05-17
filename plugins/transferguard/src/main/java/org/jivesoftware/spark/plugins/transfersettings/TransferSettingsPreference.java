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
package org.jivesoftware.spark.plugins.transfersettings;

import org.jivesoftware.spark.preference.Preference;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Preference object for file transfer settings. Used by Spark to show the associated UI and commit changes to preference settings.
 */
public class TransferSettingsPreference implements Preference {
    static final String NAMESPACE = "transferSettings";
    private TransferSettingsPanel gui;
    private final FileTransferSettings settings = new FileTransferSettings();

    @Override
    public void commit() {
        gui.storeSettings(settings);
        settings.store();
    }

    @Override
    public Object getData() {
        return settings;
    }

    @Override
    public String getErrorMessage() {
        return "What happened here?";
    }

    @Override
    public JComponent getGUI() {
        gui = new TransferSettingsPanel();
        gui.applySettings(settings);
        return gui;
    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(getClass().getResource("/images/guard.png"));
    }

    @Override
    public String getListName() {
    	return TGuardRes.getString("guard.settings.title.list");
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getTitle() {
        return TGuardRes.getString("guard.settings.title.settings");
    }

    @Override
    public String getTooltip() {
        return "Configure allowed file transfer types, sizes, and senders";
    }

    @Override
    public boolean isDataValid() {
        return true;
    }

    @Override
    public void load() {
        settings.load();
    }
}
