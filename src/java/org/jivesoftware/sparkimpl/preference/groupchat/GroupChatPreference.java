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
                boolean showroleicons 		= localPreferences.isShowingRoleIcons();
                boolean autoAcceptMucInvite	= localPreferences.isAutoAcceptMucInvite();
                boolean randomColors 		= localPreferences.isMucRandomColors();
                boolean inviteToBookmark    = !localPreferences.isUseAdHocRoom();

                panel.setMucHighNameEnabled(highlightMyName);
                panel.setMucHighTextEnabled(highlightMyText);
                panel.setMuchHighToastEnabled(highlightPopName);
                panel.setShowJoinLeaveMessagesEnabled(showjoinleavemessage);
                panel.setShowRoleIconInsteadStatusIcon(showroleicons);
                panel.setAutoAcceptMuc(autoAcceptMucInvite);
                panel.setRandomColors(randomColors);
                panel.setInviteToBookmark(inviteToBookmark);
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
        pref.setShowRoleIconInsteadStatusIcon(panel.isShowingRoleIcons());
        pref.setAutoAcceptMucInvite(panel.isAutoAcceptMuc());
        pref.setMucRandomColors(panel.isRandomColors());
        pref.setUseAdHocRoom(!panel.isInviteToBookmark());
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

