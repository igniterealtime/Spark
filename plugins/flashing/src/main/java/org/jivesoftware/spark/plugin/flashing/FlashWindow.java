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
package org.jivesoftware.spark.plugin.flashing;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFrame;

import org.jivesoftware.spark.PluginManager;
import org.jivesoftware.spark.util.log.Log;

public class FlashWindow {
    private final HashMap<Window, Thread> flashings = new HashMap<>();

    static {
        boolean is64bit = System.getProperty("sun.arch.data.model").equals("64");
        String arch = "";
        if (is64bit) {
            arch = "64";
        }
        try {
            System.load(PluginManager.PLUGINS_DIRECTORY.getCanonicalPath() +
                File.separator + "flashing" + File.separator + "native" + File.separator + "FlashWindow" + arch + ".dll");
        } catch (UnsatisfiedLinkError | IOException e) {
            Log.error(e);
        }
    }

    public native void flash(String name, boolean bool);

    /*
     * @param frame The JFrame to be flashed
     * @param intratime The amount of time between the on and off states of a
     * single flash
     * @param intertime The amount of time between different flashes
     * @param count The number of times to flash the window
     */
    public void flash(Window window, int intratime, int count) {
        if (!(window instanceof JFrame)) {
            return;
        }
        JFrame jFrame = (JFrame) window;
        new Thread(() -> {
            try {
                // flash on and off each time
                for (int i = 0; i < count; i++) {
                    flash(jFrame.getTitle(), true);
                    Thread.sleep(intratime);
                }
                // turn the flash off
                flash(jFrame.getTitle(), false);
            } catch (Exception ignored) {
            }
        }).start();
    }

    public void startFlashing(final Window window) {
        if (flashings.get(window) != null) {
            return;
        }
        if (!(window instanceof JFrame)) {
            return;
        }
        JFrame jFrame = (JFrame) window;
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1500);
                    flash(jFrame.getTitle(), true);
                } catch (Exception ex) {
                    flash(jFrame.getTitle(), false);
                    break;
                }
            }
        });
        t.start();
        flashings.put(window, t);
    }

    public void stopFlashing(final Window window) {
        Thread windowFlashingThread = flashings.remove(window);
        if (windowFlashingThread != null) {
            windowFlashingThread.interrupt();
        }
    }
}
