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

import javax.sound.sampled.*;
import java.io.File;
import java.util.WeakHashMap;

/**
 * This manager is responsible for the playing, stopping and caching of sounds within Spark.  You would
 * use this manager when you wish to play audio without adding too much memory overhead.
 */
public class SoundManager {

    private final WeakHashMap<String, Clip> clipCache = new WeakHashMap<>();

    /**
     * When the runtime has no usable audio device/mixer, attempting to play sounds will repeatedly throw.
     * We detect that scenario once and disable sound playback to avoid log spam and wasted work.
     */
    private boolean soundSystemUnavailable;
    private boolean soundSystemUnavailableLogged;

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
        if (soundSystemUnavailable) {
            return;
        }

        final Runnable playThread = () -> {
            Clip ac = clipCache.get(soundFile.toString());
            if (ac == null) {
                // Add new clip
                try (AudioInputStream originalStream = AudioSystem.getAudioInputStream(soundFile)) {
                    ac = tryOpenClipWithFallbacks(originalStream);
                    if (ac != null) {
                        clipCache.put(soundFile.toString(), ac);
                    }
                } catch (Exception e) {
                    Log.warning("Unable to load sound: " + soundFile, e);
                }
            }
            // Starts cached or newly loaded audio clip
            if (ac != null) {
                try {
                    if (ac.isRunning()) {
                        ac.stop();
                    }
                    ac.setFramePosition(0);
                    ac.start();
                } catch (Exception e) {
                    Log.error("Unable to play sound: " + soundFile + "\n\t: " + e);
                }
            }
        };

        TaskEngine.getInstance().submit(playThread);
    }

    private Clip tryOpenClipWithFallbacks(AudioInputStream originalStream) {
        // 1) Try original format first (fast path)
        Clip clip = tryOpenClip(originalStream);
        if (clip != null) {
            return clip;
        }

        // ... existing code ...
        // 2) Try decoding to a very common PCM format (16-bit LE, same sample rate/channels as source)
        try {
            final AudioFormat src = originalStream.getFormat();
            final float sampleRate = src.getSampleRate() > 0 ? src.getSampleRate() : 44100.0f;
            final int channels = src.getChannels() > 0 ? src.getChannels() : 2;

            final AudioFormat pcm = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                16,
                channels,
                channels * 2,
                sampleRate,
                false
            );

            try (AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcm, originalStream)) {
                clip = tryOpenClip(pcmStream);
                if (clip != null) {
                    return clip;
                }
            }
        } catch (Exception ignored) {
            // fall through to more aggressive fallbacks
        }

        // 3) Fallback: mono 22.05kHz PCM (last resort for constrained devices/backends)
        try {
            final AudioFormat monoLow = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                22050.0f,
                16,
                1,
                2,
                22050.0f,
                false
            );

            try (AudioInputStream monoLowStream = AudioSystem.getAudioInputStream(monoLow, originalStream)) {
                clip = tryOpenClip(monoLowStream);
                if (clip != null) {
                    return clip;
                }
            }
        } catch (Exception ignored) {
            // fall through
        }

        return null;
    }

    private Clip tryOpenClip(AudioInputStream stream) {
        try {
            final AudioFormat format = stream.getFormat();
            final DataLine.Info info = new DataLine.Info(Clip.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                return null;
            }
            final Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            return clip;
        } catch (LineUnavailableException e) {
            // This often indicates: no audio device/mixer, or the backend cannot provide a Clip line.
            soundSystemUnavailable = true;
            if (!soundSystemUnavailableLogged) {
                soundSystemUnavailableLogged = true;
                Log.warning("Sound playback disabled: no supported audio line is available in this environment.", e);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
