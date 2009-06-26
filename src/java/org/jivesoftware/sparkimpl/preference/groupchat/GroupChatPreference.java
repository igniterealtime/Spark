/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.groupchat;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Essentially adds a new panel to the menu.
 * Allows users to define MUC/Group Chat functions.
 *
 */
public class GroupChatPreference implements Preference {

    private GroupChatPreferencePanel panel = new GroupChatPreferencePanel();

    /**
     * Define the Namespace used for this preference.
     */
    public static final String NAMESPACE = "Group Chat";

    public String getTitle() {
        return Res.getString("title.group.chat");
    }

    public String getListName() {
        return Res.getString("title.group.chat");
    }

    public String getTooltip() {
        return Res.getString("title.group.chat");
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.JOIN_GROUPCHAT_IMAGE);
    }

    public void load() {
        SwingWorker thread = new SwingWorker() {
            LocalPreferences localPreferences;

            public Object construct() {
                localPreferences = SettingsManager.getLocalPreferences();
                return localPreferences;
            }

            public void finished() {
                boolean highlightMyName 	= localPreferences.isMucHighNameEnabled();
                boolean highlightMyText 	= localPreferences.isMucHighTextEnabled();
                boolean highlightPopName	= localPreferences.isMucHighToastEnabled();
                boolean showjoinleavemessage 	= localPreferences.isShowJoinLeaveMessagesEnabled();
                
                panel.setMucHighNameEnabled(highlightMyName);
                panel.setMucHighTextEnabled(highlightMyText);
                panel.setMuchHighToastEnabled(highlightPopName);
                panel.setShowJoinLeaveMessagesEnabled(showjoinleavemessage);
            }
        };

        thread.start();

    }

    public void commit() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();

        pref.setMucHighNameEnabled(panel.isMucHighNameEnabled());
        pref.setMucHighTextEnabled(panel.isMucHighTextEnabled());
        pref.setMuchHighToastEnabled(panel.isMucHighToastEnabled());
        pref.setShowJoinLeaveMessagesEnabled(panel.isShowJoinLeaveMessagesEnabled());
        SettingsManager.saveSettings();
    }

    public Object getData() {
        return SettingsManager.getLocalPreferences();
    }

    public String getErrorMessage() {
        return null;
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

