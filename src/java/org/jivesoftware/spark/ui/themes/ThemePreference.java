/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
 *
 */
public class ThemePreference implements Preference {

    private ThemePanel panel;

    public static String NAMESPACE = "themes";

    public ThemePreference() {

    }


    public String getTitle() {
        return Res.getString("title.appearance.preferences");
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.PALETTE_24x24_IMAGE);
    }

    public String getTooltip() {
        return Res.getString("tooltip.appearance");
    }

    public String getListName() {
        return Res.getString("title.appearance");
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public JComponent getGUI() {
        panel = new ThemePanel();

        return panel;
    }
    
    /**
     * Returns the LookAndFeel with package origin <br>
     * for example:
     * <code>com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel</code>
     * 
     * @return {@link String}
     */
    public String getLookAndFeel()
    {
	return panel.getLookAndFeel();
    }

    public void loadFromFile() {

    }

    public void load() {

    }

    public boolean isDataValid() {
        return true;
    }

    public String getErrorMessage() {
        return null;
    }

    public Object getData() {
        return null;
    }

    public void commit() {
        final String pack = panel.getSelectedEmoticonPack();
        boolean emotEnabled = panel.areEmoticonsEnabled();
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if(pack != null){
            pref.setEmoticonPack(pack);
        }
        pref.setEmoticonsEnabled(emotEnabled);
        pref.setLookAndFeel(panel.getLookAndFeel());
        pref.setAvatarVisible(panel.areAvatarsVisible());
        pref.setContactListIconSize(panel.getContactListIconSize());
        pref.setVCardsVisible(panel.areVCardsVisible());

        try {
            String chatRoomFontSize = panel.getChatRoomFontSize();
            String contactListFontSize = panel.getContactListFontSize();

            pref.setChatRoomFontSize(Integer.parseInt(chatRoomFontSize));
            pref.setContactListFontSize(Integer.parseInt(contactListFontSize));
        }
        catch (NumberFormatException e) {
            Log.error(e);
        }
        SettingsManager.saveSettings();
    }


    public void shutdown() {

    }


}


