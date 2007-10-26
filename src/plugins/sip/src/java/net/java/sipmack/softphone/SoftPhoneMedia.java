/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package net.java.sipmack.softphone;

import net.java.sipmack.common.Log;
import net.java.sipmack.common.Utils;
import net.java.sipmack.media.event.MediaErrorEvent;
import net.java.sipmack.media.event.MediaEvent;
import net.java.sipmack.media.event.MediaListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import java.awt.Dimension;

public class SoftPhoneMedia implements MediaListener {

    public JFrame videoFrame = null;

    /**
     * Fired when player started to receive the video
     *
     * @param event MediaEvent
     */
    public void playerStarting(MediaEvent event) {
        javax.media.Player player = null;
        try {
            player = (javax.media.Player) event.getSource();
            if (videoFrame != null)
                videoFrame.dispose();

            if (player.getVisualComponent() != null) {

                videoFrame = new JFrame("Video");
                videoFrame.setIconImage(new ImageIcon(Utils
                        .getResource("off.gif")).getImage());
                videoFrame.setSize(new Dimension(176, 144));
                videoFrame.add(player.getVisualComponent());
                videoFrame.setVisible(true);
                videoFrame.setAlwaysOnTop(true);
            }
        }
        catch (Exception e) {
            Log.error("playerStarting", e);
        }
    }

    /**
     * Fired when non fatal error ocurred
     *
     * @param evt MediaErrorEvent
     */
    public void nonFatalMediaErrorOccurred(MediaErrorEvent evt) {
    }

    /**
     * Fired when player stops
     */
    public void playerStopped() {
        if (videoFrame != null) {
            videoFrame.setAlwaysOnTop(false);
            videoFrame.setVisible(false);
        }
    }

}
