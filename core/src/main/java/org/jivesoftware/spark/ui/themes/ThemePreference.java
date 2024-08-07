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
package org.jivesoftware.spark.ui.themes;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 */
public class ThemePreference implements Preference {

    private MainThemePanel panel;

    public static final String NAMESPACE = "themes";

    public ThemePreference() {}

    @Override
	public String getTitle() {
        return Res.getString("title.appearance.preferences");
    }

    @Override
	public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.PALETTE_24x24_IMAGE);
    }

    @Override
	public String getTooltip() {
        return Res.getString("tooltip.appearance");
    }

    @Override
	public String getListName() {
        return Res.getString("title.appearance");
    }

    @Override
	public String getNamespace() {
        return NAMESPACE;
    }

    @Override
	public JComponent getGUI() {
        panel = new MainThemePanel();
        return panel;
    }

    @Override
	public void load() {}

    @Override
	public boolean isDataValid() {
        return true;
    }

    @Override
	public String getErrorMessage() {
        return null;
    }

    @Override
	public Object getData() {
        return null;
    }

    @Override
	public void commit() {
        final String pack = panel.getThemePanel().getSelectedEmoticonPack();
        boolean emotEnabled = panel.getThemePanel().areEmoticonsEnabled();
        boolean grayingOutEnabled = panel.getThemePanel().isGrayingOutEnabled();
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if(pack != null){
            pref.setEmoticonPack(pack);
        }
        pref.setEmoticonsEnabled(emotEnabled);
        pref.setLookAndFeel(panel.getThemePanel().getSelectedLookAndFeelClassName());
        pref.setAvatarVisible(panel.getThemePanel().areAvatarsVisible());
        pref.setContactListIconSize(panel.getThemePanel().getContactListIconSize());
        pref.setVCardsVisible(panel.getThemePanel().areVCardsVisible());
        pref.setGrayingOutEnabled(grayingOutEnabled);
        pref.setReconnectPanelType(panel.getThemePanel().getReconnectPanelType());

        try {
            String chatRoomFontSize = panel.getThemePanel().getChatRoomFontSize();
            String contactListFontSize = panel.getThemePanel().getContactListFontSize();
            String maxCurrentHistorySize = panel.getThemePanel().getMaxCurrentHistorySize();

            pref.setChatRoomFontSize(Integer.parseInt(chatRoomFontSize));
            pref.setContactListFontSize(Integer.parseInt(contactListFontSize));
            pref.setMaxCurrentHistorySize(Integer.parseInt(maxCurrentHistorySize));
        }
        catch (NumberFormatException e) {
            Log.error(e);
        }
        
        
        ColorSettingManager.saveColorSettings();
        SettingsManager.saveSettings();
    }

    @Override
	public void shutdown() {}
}


