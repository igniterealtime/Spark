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
package org.jivesoftware.spark.plugins.transfersettings;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.preference.PreferenceManager;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Preference object for file transfer settings. Used by Spark to show the associated UI and commit changes to preference settings.
 */
public class TransferSettingsPreference implements Preference {
    private TransferSettingsPanel gui = new TransferSettingsPanel();
    private FileTransferSettings settings = new FileTransferSettings();

    /**
     * Called when preference settings should be persisted.
     */
    public void commit() {
        gui.storeSettings(settings);
        settings.store();
    }

    /**
     * Returns the underlying data object. In this case it is a {@link FileTransferSettings} instance.
     *
     * @return object data
     */
    public Object getData() {
        return settings;
    }

    /**
     * Returns an error message to display if calls to {@link #isDataValid} return false.
     *
     * @return an error message to display if calls to {@link #isDataValid} return false.
     */
    public String getErrorMessage() {
        return "What happend here?";
    }

    /**
     * Returns the GUI for setting and viewing preference settings.
     *
     * @return the GUI for setting and viewing preference settings.
     */
    public javax.swing.JComponent getGUI() {
        gui.applySettings(settings);
        return gui;
    }

    /**
     * Returns the {@link Icon} to show in the preferences ui.
     *
     * @return the {@link Icon} to show in the preferences ui.
     */
    public javax.swing.Icon getIcon() {
        return new ImageIcon(getClass().getResource("/images/knight.png"));
    }

    /**
     * Returns the name displayed in the list of preferences.
     *
     * @return the name displayed in the list of preferences.
     */
    public String getListName() {
    	return TGuardRes.getString("guard.settings.title.list");
    }

    /**
     * Returns the key to retrive this instance from the {@link PreferenceManager}.
     *
     * @return the key to retrive this instance from the {@link PreferenceManager}.
     */
    public String getNamespace() {
        return "transferSettings";
    }

    /**
     * Returns the title of the preference panel for this preference.
     *
     * @return the title of the preference panel for this preference.
     */
    public String getTitle() {
        return TGuardRes.getString("guard.settings.title.settings");
    }

    /**
     * Returns the tooltip text to display in the preferences list.
     *
     * @return the tooltip text to display in the preferences list.
     */
    public String getTooltip() {
        return "Configure allowed file transfer types, sizes, and senders";
    }

    /**
     * Returns true if the supplied preference settings are valid. If not, an associated error message
     * can be retrieved by a call to {@link #getErrorMessage}. This implementation always returns true.
     *
     * @return true.
     */
    public boolean isDataValid() {
        return true;
    }

    /**
     * Called when data should be loaded from the persistent stor.
     */
    public void load() {
        settings.load();
    }

    /**
     * Called when the application shuts down.
     */
    public void shutdown() {
    }
}