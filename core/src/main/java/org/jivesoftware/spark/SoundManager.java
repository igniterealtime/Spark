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
package org.jivesoftware.spark;

import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.preference.sounds.SoundPreference;
import org.jivesoftware.sparkimpl.preference.sounds.SoundPreferences;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.WeakHashMap;

/**
 * This manager is responsible for the playing, stopping and caching of sounds within Spark.  You would
 * use this manager when you wish to play audio without adding too much memory overhead.
 */
public class SoundManager {

    private final WeakHashMap<String, Clip> clipCache = new WeakHashMap<>();

    public void playClip(Event e) {
        SoundPreference soundPreference = (SoundPreference) SparkManager.getPreferenceManager().getPreference(new SoundPreference().getNamespace());
        SoundPreferences preferences = soundPreference.getPreferences();
        if (preferences == null) {
            return;
        }
        String soundFile = "";
        // Determines sound file based on event and preferences
        switch (e) {
            case MSG_INCOMING:
                if (preferences.isPlayIncomingSound()) {
                    soundFile = preferences.getIncomingSound();
                }
                break;
            case MSG_OUTCOMING:
                if (preferences.isPlayOutgoingSound()) {
                    soundFile = preferences.getOutgoingSound();
                }
                break;
            case CHAT_REQUEST:
                if (preferences.isPlayChatRequestSound()) {
                    soundFile = preferences.getChatRequestSound();
                }
                break;
            case INCOMING_INVITATION:
                if (preferences.playIncomingInvitationSound()) {
                    soundFile = preferences.getIncomingInvitationSoundFile();
                }
                break;
            case STATUS_OFFLINE:
                if (preferences.isPlayOfflineSound()) {
                    soundFile = preferences.getOfflineSound();
                }
                break;
            default:
                return;
        }
        if (soundFile == null || soundFile.isEmpty()) {
            return;
        }
        SparkManager.getSoundManager().playClip(new File(soundFile));
    }

    /**
     * Plays a sound file.
     *
     * @param soundFile the File object representing the wav file.
     */
    public void playClip(final File soundFile) {
        final Runnable playThread = () -> {
            Clip ac = clipCache.get(soundFile.toString());
            if (ac == null) {
                // Add new clip
                try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile)) {
                    ac = AudioSystem.getClip();
                    ac.open(audioInputStream);
                    clipCache.put(soundFile.toString(), ac);
                } catch (Exception e) {
                    Log.warning("Unable to load sound: " + soundFile, e);
                }
            }
            if (ac != null) {
                try {
                    ac.start();
                } catch (Exception e) {
                    Log.error("Unable to play sound: " + soundFile + "\n\t: " + e);
                }
            }
        };

        TaskEngine.getInstance().submit(playThread);
    }

}
