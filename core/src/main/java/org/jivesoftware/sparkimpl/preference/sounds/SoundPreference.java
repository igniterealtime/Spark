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

package org.jivesoftware.sparkimpl.preference.sounds;

import com.thoughtworks.xstream.XStream;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Preferences to handle Sounds played within Spark.
 *
 * @author Derek DeMoro
 */
public class SoundPreference implements Preference {
    private XStream xstream;
    private SoundPreferences preferences;
    private SoundPanel soundPanel;

    public static final String NAMESPACE = "Sounds";

    @Override
	public String getTitle() {
        return Res.getString("title.sound.preferences");
    }

    @Override
	public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.Icon.SOUND_PREFERENCES_IMAGE);
    }

    @Override
	public String getTooltip() {
        return Res.getString("title.sounds");
    }

    @Override
	public String getListName() {
        return Res.getString("title.sounds");
    }

    @Override
	public String getNamespace() {
        return NAMESPACE;
    }

    @Override
	public JComponent getGUI() {
		soundPanel = new SoundPanel();
        // Set default settings
        soundPanel.setIncomingMessageSound(preferences.getIncomingSound());
        soundPanel.setPlayIncomingMessageSound(preferences.isPlayIncomingSound());

        soundPanel.setOutgoingMessageSound(preferences.getOutgoingSound());
        soundPanel.setPlayOutgoingSound(preferences.isPlayOutgoingSound());

        soundPanel.setOfflineSound(preferences.getOfflineSound());
        soundPanel.setPlayOfflineSound(preferences.isPlayOfflineSound());

        soundPanel.setInvitationSound(preferences.getIncomingInvitationSound());
        soundPanel.setPlayInvitationSound(preferences.isPlayIncomingInvitationSound());

        soundPanel.setChatRequestSound(preferences.getChatRequestSound());
        soundPanel.setPlayChatRequestSound(preferences.isPlayChatRequestSound());

        soundPanel.setAttentionBuzzSound(preferences.getAttentionBuzzSound());
        soundPanel.setPlayAttentionBuzzSound(preferences.isPlayAttentionBuzzSound());
        return soundPanel;
    }

    public void loadFromFile() {
        if (preferences != null) {
            return;
        }

        if (!getSoundSettingsFile().exists()) {
            preferences = new SoundPreferences();
            return;
        }
            // Do Initial Load from FileSystem.
            File settingsFile = getSoundSettingsFile();
            try {
                FileReader reader = new FileReader(settingsFile);
                preferences = (SoundPreferences)getXStream().fromXML(reader);
            }
            catch (Exception e) {
                Log.error("Error loading Sound Preferences.", e);
                preferences = new SoundPreferences();
            }
    }

    @Override
	public void load() {
        loadFromFile();
    }

    @Override
	public void commit() {
        preferences.setIncomingSound(soundPanel.getIncomingSound());
        preferences.setOutgoingSound(soundPanel.getOutgoingSound());

        preferences.setOfflineSound(soundPanel.getOfflineSound());
        preferences.setPlayOfflineSound(soundPanel.playOfflineSound());

        preferences.setPlayIncomingSound(soundPanel.isPlayIncomingMessageSound());
        preferences.setPlayOutgoingSound(soundPanel.isPlayOutgoingSound());

        preferences.setIncomingInvitationSound(soundPanel.getInvitationSound());
        preferences.setPlayIncomingInvitationSound(soundPanel.isPlayInvitationSound());

        preferences.setChatRequestSound(soundPanel.getChatRequestSound());
        preferences.setPlayChatRequestSound(soundPanel.isPlayChatRequestSound());

        preferences.setAttentionBuzzSound(soundPanel.getAttentionBuzzSound());
        preferences.setPlayAttentionBuzzSound(soundPanel.isPlayAttentionBuzzSound());

        saveSoundsFile();
    }

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
        return preferences;
    }

    private File getSoundSettingsFile() {
        File file = Spark.getSparkUserHome();
        return new File(file, "sound-settings.xml");
    }

    private void saveSoundsFile() {
        try {
            FileWriter writer = new FileWriter(getSoundSettingsFile());
            getXStream().toXML(preferences, writer);
        }
        catch (Exception e) {
            Log.error("Error saving sound settings.", e);
        }
    }

    private XStream getXStream() {
        if (xstream == null) {
            xstream = new XStream();
            xstream.allowTypes(new Class[]{SoundPreferences.class});
            xstream.alias("sounds", SoundPreferences.class);
            xstream.ignoreUnknownElements();
        }
        return xstream;
    }

}
