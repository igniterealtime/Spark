/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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
package org.jivesoftware.sparkimpl.plugin.phone;

import com.sun.media.ExclusiveUse;
import com.sun.media.util.Registry;
import org.jivesoftware.spark.util.log.Log;

import javax.media.Format;
import javax.media.PlugInManager;
import javax.media.Renderer;
import javax.media.format.AudioFormat;

import java.awt.Frame;
import java.util.Vector;

public class JMFInit extends Frame implements Runnable {
	private static final long serialVersionUID = 6591937615313371376L;

	public JMFInit(String[] args, boolean visible) {
        super("Initializing JMF...");

        Registry.set("secure.allowCaptureFromApplets", true);
        Registry.set("secure.allowSaveFileFromApplets", true);


        updateTemp(args);

        try {
            Registry.commit();
        }
        catch (Exception e) {

            message("Failed to commit to JMFRegistry!");
        }

        Thread detectThread = new Thread(this);
        detectThread.run();

        /*
           * int slept = 0; while (!done && slept < 60 * 1000 * 2) { try {
           * Thread.currentThread().sleep(500); } catch (InterruptedException ie) { }
           * slept += 500; }
           *
           * if (!done) { console.error("Detection is taking too long!
           * Aborting!"); message("Detection is taking too long! Aborting!"); }
           *
           * try { Thread.currentThread().sleep(2000); } catch
           * (InterruptedException ie) { }
           */
    }

    public void run() {
        detectDirectAudio();
        detectS8DirectAudio();
        detectCaptureDevices();
    }

    private void updateTemp(String[] args) {
        if (args != null && args.length > 0) {
            String tempDir = args[0];

            message("Setting cache directory to " + tempDir);
            try {
                Registry.set("secure.cacheDir", tempDir);
                Registry.commit();

                message("Updated registry");
            }
            catch (Exception e) {
                message("Couldn't update registry!");
            }
        }
    }

    private void detectCaptureDevices() {
        // check if JavaSound capture is available
        message("Looking for Audio capturer");
        Class<?> dsauto;
        try {
            dsauto = Class.forName("DirectSoundAuto");
            dsauto.newInstance();
            message("Finished detecting DirectSound capturer");
        }
        catch (ThreadDeath td) {
            throw td;
        }
        catch (Throwable t) {
            // Nothing to do
        }

        Class<?> jsauto;
        try {
            jsauto = Class.forName("JavaSoundAuto");
            jsauto.newInstance();
            message("Finished detecting javasound capturer");
        }
        catch (ThreadDeath td) {
            throw td;
        }
        catch (Throwable t) {
            message("JavaSound capturer detection failed!");
        }

        /*
        // Check if VFWAuto or SunVideoAuto is available
        message("Looking for video capture devices");
        Class auto = null;
        Class autoPlus = null;
        try {
            auto = Class.forName("VFWAuto");
        }
        catch (Exception e) {
        }
        if (auto == null) {
            try {
                auto = Class.forName("SunVideoAuto");
            }
            catch (Exception ee) {

            }
            try {
                autoPlus = Class.forName("SunVideoPlusAuto");
            }
            catch (Exception ee) {

            }
        }
        if (auto == null) {
            try {
                auto = Class.forName("V4LAuto");
            }
            catch (Exception ee) {

            }
        }
        try {
            Object instance = auto.newInstance();
            if (autoPlus != null) {
                Object instancePlus = autoPlus.newInstance();
            }

            message("Finished detecting video capture devices");
        }
        catch (ThreadDeath td) {
            throw td;
        }
        catch (Throwable t) {

            message("Capture device detection failed!");
        }
        */
    }

    private void detectDirectAudio() {
        Class<?> cls;
        int plType = PlugInManager.RENDERER;
        String dar = "com.sun.media.renderer.audio.DirectAudioRenderer";
        try {
            // Check if this is the Windows Performance Pack - hack
            Class.forName("VFWAuto");
            // Check if DS capture is supported, otherwise fail DS renderer
            // since NT doesn't have capture
            Class.forName("com.sun.media.protocol.dsound.DSound");
            // Find the renderer class and instantiate it.
            cls = Class.forName(dar);

            Renderer rend = (Renderer)cls.newInstance();
            try {
                // Set the format and open the device
                AudioFormat af = new AudioFormat(AudioFormat.LINEAR, 44100, 16,
                        2);
                rend.setInputFormat(af);
                rend.open();
                Format[] inputFormats = rend.getSupportedInputFormats();
                // Register the device
                PlugInManager.addPlugIn(dar, inputFormats, new Format[0],
                        plType);
                // Move it to the top of the list
                Vector<String> rendList = PlugInManager.getPlugInList(null, null, plType);
                int listSize = rendList.size();
                if (rendList.elementAt(listSize - 1).equals(dar)) {
                    rendList.removeElementAt(listSize - 1);
                    rendList.insertElementAt(dar, 0);
                    PlugInManager.setPlugInList(rendList, plType);
                    PlugInManager.commit();
                    // Log.debug("registered");
                }
                rend.close();
            }
            catch (Throwable t) {
                // Log.debug("Error " + t);
            }
        }
        catch (Throwable tt) {
            // Nothing to do
        }
    }

    private void detectS8DirectAudio() {
        Class<?> cls;
        int plType = PlugInManager.RENDERER;
        String dar = "com.sun.media.renderer.audio.DirectAudioRenderer";
        try {
            // Check if this is the solaris Performance Pack - hack
            Class.forName("SunVideoAuto");

            // Find the renderer class and instantiate it.
            cls = Class.forName(dar);

            Renderer rend = (Renderer)cls.newInstance();

            if (rend instanceof ExclusiveUse
                    && !((ExclusiveUse)rend).isExclusive()) {
                // sol8+, DAR supports mixing
                Vector<String> rendList = PlugInManager.getPlugInList(null, null,
                        plType);
                int listSize = rendList.size();
                boolean found = false;
                String rname;

                for (int i = 0; i < listSize; i++) {
                    rname = (String)(rendList.elementAt(i));
                    if (rname.equals(dar)) { // DAR is in the registry
                        found = true;
                        rendList.removeElementAt(i);
                        break;
                    }
                }

                if (found) {
                    rendList.insertElementAt(dar, 0);
                    PlugInManager.setPlugInList(rendList, plType);
                    PlugInManager.commit();
                }
            }
        }
        catch (Throwable tt) {
            // Nothing to do
        }
    }

    private void message(String message) {
        Log.debug(message);
    }

//    private void createGUI() {
//        TextArea textBox = new TextArea(5, 50);
//        add("Center", textBox);
//        textBox.setEditable(false);
//        addNotify();
//        pack();
//
//        int scrWidth = (int)Toolkit.getDefaultToolkit().getScreenSize()
//                .getWidth();
//        int scrHeight = (int)Toolkit.getDefaultToolkit().getScreenSize()
//                .getHeight();
//
//        setLocation((scrWidth - getWidth()) / 2, (scrHeight - getHeight()) / 2);
//
//        setVisible(visible);
//
//    }

    public static void start(boolean visible) {
        new JMFInit(null, visible);
    }
}
