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

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This manager is responsible for the playing, stopping and caching of sounds within Spark.  You would
 * use this manager when you wish to play audio without adding too much memory overhead.
 */
public class SoundManager {

    private final Map<URL,AudioClip> fileMap = new HashMap<>();

    /**
     * Default constructor
     */
    public SoundManager() {
    }

    /**
     * Plays a sound file.
     *
     * @param soundFile the File object representing the wav file.
     */
    public void playClip(final File soundFile) {
        final Runnable playThread = () -> {
            try {
                final URL url = soundFile.toURI().toURL();
                AudioClip ac = fileMap.get(url);
                if (ac == null) {
                    // Add new clip
                    ac = Applet.newAudioClip(url);
                    fileMap.put(url, ac);
                }
                ac.play();
            }
            catch (Exception e) {
                Log.error("Unable to load sound: " + soundFile + "\n\t: " + e);
            }
        };

        TaskEngine.getInstance().submit(playThread);
    }

}
