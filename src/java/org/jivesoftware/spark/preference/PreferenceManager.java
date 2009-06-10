/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
