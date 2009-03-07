/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;

import org.jivesoftware.resource.SoundsRes;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This manager is responsible for the playing, stopping and caching of sounds within Spark.  You would
 * use this manager when you wish to play audio without adding too much memory overhead.
 */
public class SoundManager {

    private final Map<String,AudioClip> clipMap = new HashMap<String,AudioClip>();
    private final Map<URL,AudioClip> fileMap = new HashMap<URL,AudioClip>();

    /**
     * Default constructor
     */
    public SoundManager() {
    }

    /**
     * Plays an audio clip of local clips deployed with Spark.
     *
     * @param clip the properties value found in la.properties.
     * @return the AudioClip found. If no audio clip was found, returns null.
     */
    public AudioClip getClip(String clip) {
        if (!clipMap.containsKey(clip)) {
            // Add new clip
            final AudioClip newClip = loadClipForURL(clip);
            if (newClip != null) {
                clipMap.put(clip, newClip);
            }
        }

        return clipMap.get(clip);
    }

    /**
     * Plays an AudioClip.
     *
     * @param clip the audioclip to play.
     */
    public void playClip(final AudioClip clip) {

        final Runnable playThread = new Runnable() {
            public void run() {
                try {
                    clip.play();
                }
                catch (Exception ex) {
                    System.err.println("Unable to load sound file");
                }
            }
        };

        TaskEngine.getInstance().submit(playThread);
    }

    /**
     * Plays an AudioClip.
     *
     * @param clipToPlay the properties value found in la.properties.
     */
    public void playClip(String clipToPlay) {
        AudioClip clip = getClip(clipToPlay);
        try {
            clip.play();
        }
        catch (Exception ex) {
            System.err.println("Unable to load sound file");
        }
    }

    /**
     * Plays a sound file.
     *
     * @param soundFile the File object representing the wav file.
     */
    public void playClip(final File soundFile) {
        final Runnable playThread = new Runnable() {
            public void run() {
                try {
                    final URL url = soundFile.toURI().toURL();
                    AudioClip ac = fileMap.get(url);
                    if (ac == null) {
                        ac = Applet.newAudioClip(url);
                        fileMap.put(url, ac);
                    }
                    ac.play();
                }
                catch (MalformedURLException e) {
                    Log.error(e);
                }
            }
        };

        TaskEngine.getInstance().submit(playThread);
    }

    /**
     * Creates an AudioClip from a URL.
     *
     * @param clipOfURL the url of the AudioClip to play. We only support .wav files at the moment.
     * @return the AudioFile found. If no audio file  was found,returns null.
     */
    private AudioClip loadClipForURL(String clipOfURL) {
        final URL url = SoundsRes.getURL(clipOfURL);
        AudioClip clip = null;

        try {
            clip = Applet.newAudioClip(url);

        }
        catch (Exception e) {
            Log.error("Unable to load sound url: " + url + "\n\t: " + e);
        }

        return clip;
    }


}