/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
        pref.setUseSystemLookAndFeel(panel.useSystemLookAndFeel());
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


