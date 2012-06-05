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
package org.jivesoftware.spark.preference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import org.jivesoftware.MainWindowListener;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.sparkimpl.plugin.privacy.ui.PrivacyPreferences;
import org.jivesoftware.sparkimpl.preference.PreferenceDialog;
import org.jivesoftware.sparkimpl.preference.PreferencesPanel;
import org.jivesoftware.sparkimpl.preference.chat.ChatPreference;
import org.jivesoftware.sparkimpl.preference.groupchat.GroupChatPreference;
import org.jivesoftware.sparkimpl.preference.media.MediaPreference;
import org.jivesoftware.sparkimpl.settings.local.LocalPreference;

/**
 * Usage of the PreferenceManager to handle loading of preferences within Spark.
 *
 * @author Derek DeMoro
 */
public class PreferenceManager {
    private Map<String, Preference> map = new LinkedHashMap<String, Preference>();
    private PreferenceDialog preferenceDialog;

    public PreferenceManager() {
        // Initialize base preferences
        ChatPreference chatPreferences = new ChatPreference();
        addPreference(chatPreferences);
        chatPreferences.load();
        
        GroupChatPreference groupChatPreferences = new GroupChatPreference();
        addPreference(groupChatPreferences);
        groupChatPreferences.load();
        
        MediaPreference preferenes = new MediaPreference();
        addPreference(preferenes);
        preferenes.load();
        
        PrivacyPreferences privacy = new PrivacyPreferences();
        addPreference(privacy);
        privacy.load();

        LocalPreference localPreferences = new LocalPreference();
        addPreference(localPreferences);
        localPreferences.load();

        getPreferences();

        SparkManager.getMainWindow().addMainWindowListener(new MainWindowListener() {
            public void shutdown() {
                fireShutdown();
            }

            public void mainWindowActivated() {

            }

            public void mainWindowDeactivated() {

            }
        });
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
        return getPreference(namespace).getData();
    }

    public Iterator<Preference> getPreferences() {
        final List<Preference> returnList = new ArrayList<Preference>();
        for (String namespace : map.keySet()) {
            returnList.add(map.get(namespace));
        }
        return returnList.iterator();

    }

    private void fireShutdown() {
        for (String namespace : map.keySet()) {
            final Preference preference = map.get(namespace);
            preference.shutdown();
        }
    }

    public JDialog getPreferenceDialog() {
        return preferenceDialog.getDialog();
    }
}
