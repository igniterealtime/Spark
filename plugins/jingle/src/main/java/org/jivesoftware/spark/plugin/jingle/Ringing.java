package org.jivesoftware.spark.plugin.jingle;

import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Ringing {
    private static Clip ringing;

    static {
        URL soundFile = JinglePhoneRes.getURL("RINGING");
        try (AudioInputStream originalStream = AudioSystem.getAudioInputStream(soundFile)) {
            Clip clip = (Clip) AudioSystem.getClip();
            clip.open(originalStream);
            ringing = clip;
        } catch (Exception e) {
            Log.warning("Unable to load sound: " + soundFile, e);
        }
    }

    public static void startRinging() {
        if (ringing == null) {
            return;
        }
        // Start the ringing.
        Runnable ringer = () -> {
            try {
                if (ringing.isRunning()) {
                    ringing.stop();
                }
                ringing.setFramePosition(0);
                ringing.loop(5);
            } catch (Exception e) {
                Log.error("Unable to play ring sound: " + e);
            }
        };
        TaskEngine.getInstance().submit(ringer);
    }

    public static void stopRinging() {
        if (ringing == null) {
            return;
        }

        if (ringing.isRunning()) {
            ringing.stop();
        }
    }

}
