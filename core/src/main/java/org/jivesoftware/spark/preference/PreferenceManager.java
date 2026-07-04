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
package org.jivesoftware.spark.preference;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.ui.PrivacyPreferences;
import org.jivesoftware.sparkimpl.preference.PreferenceDialog;
import org.jivesoftware.sparkimpl.preference.PreferencesPanel;
import org.jivesoftware.sparkimpl.preference.chat.ChatPreference;
import org.jivesoftware.sparkimpl.preference.groupchat.GroupChatPreference;
import org.jivesoftware.sparkimpl.settings.local.LocalPreference;

import javax.swing.JDialog;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Usage of the PreferenceManager to handle loading of preferences within Spark.
 *
 * @author Derek DeMoro
 */
public class PreferenceManager {
    private final Map<String, Preference> map = new LinkedHashMap<>();
    private final Set<String> loadedPrefs = new HashSet<>();
    private PreferenceDialog preferenceDialog;

    public PreferenceManager() {
        // Initialize base preferences
        LocalPreference localPreferences = new LocalPreference();
        addPreference(localPreferences);
        getPreferenceData(LocalPreference.NAMESPACE);

        ChatPreference chatPreferences = new ChatPreference();
        addPreference(chatPreferences);
        getPreferenceData(ChatPreference.NAMESPACE);

        GroupChatPreference groupChatPreferences = new GroupChatPreference();
        addPreference(groupChatPreferences);
        getPreferenceData(GroupChatPreference.NAMESPACE);

        PrivacyPreferences privacy = new PrivacyPreferences();
        addPreference(privacy);
        getPreferenceData(PrivacyPreferences.NAMESPACE);
    }

    /**
     * <h1>showPreferences</h1>
     * This will open the Preference-Dialog and select the given preference.
     *
     * @param selectedPref the preference you want to select
     */
    public void showPreferences(Preference selectedPref) {
        preferenceDialog = new PreferenceDialog();

        preferenceDialog.invoke(SparkManager.getMainWindow(), new PreferencesPanel(getPreferences(), selectedPref));
    }
    
    public void showPreferences() {
        preferenceDialog = new PreferenceDialog();

        preferenceDialog.invoke(SparkManager.getMainWindow(), new PreferencesPanel(getPreferences()));
    }


    public void addPreference(Preference preference) {
        map.put(preference.getNamespace(), preference);
    }

    public void removePreference(Preference preference) {
        map.remove(preference.getNamespace());
    }

    public Preference getPreference(String namespace) {
        return map.get(namespace);
    }

    public Object getPreferenceData(String namespace) {
        Preference preference = getPreference(namespace);
        if (preference == null) {
            Log.error("Unable to load preference " + namespace + ": not registered");
            return null;
        }
        boolean notLoaded = loadedPrefs.add(namespace);
        if (notLoaded) {
            try {
                preference.load();
            } catch (Exception e) {
                Log.error("Unable to load preference " + namespace, e);
            }
        }
        return preference.getData();
    }

    public Collection<Preference> getPreferences() {
        return map.values();
    }

    public JDialog getPreferenceDialog() {
        return preferenceDialog.getDialog();
    }
}
