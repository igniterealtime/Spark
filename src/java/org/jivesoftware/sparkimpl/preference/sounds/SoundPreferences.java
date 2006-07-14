/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.sounds;

import org.jivesoftware.Spark;

import java.io.File;

public class SoundPreferences {

    private String outgoingSound;
    private String incomingSound;
    private String offlineSound;

    private boolean playOutgoingSound = false;
    private boolean playIncomingSound = false;
    private boolean playOfflineSound = false;

    public SoundPreferences() {
        // Set initial sounds
        outgoingSound = new File(Spark.getResourceDirectory(), "sounds/outgoing.wav").getAbsolutePath();
        incomingSound = new File(Spark.getResourceDirectory(), "sounds/incoming.wav").getAbsolutePath();
        offlineSound = new File(Spark.getResourceDirectory(), "sounds/presence_changed.wav").getAbsolutePath();
    }

    public String getOutgoingSound() {
        return outgoingSound;
    }

    public void setOutgoingSound(String outgoingSound) {
        this.outgoingSound = outgoingSound;
    }

    public String getIncomingSound() {
        return incomingSound;
    }

    public void setIncomingSound(String incomingSound) {
        this.incomingSound = incomingSound;
    }

    public String getOfflineSound() {
        return offlineSound;
    }

    public void setOfflineSound(String offlineSound) {
        this.offlineSound = offlineSound;
    }

    public boolean isPlayOutgoingSound() {
        return playOutgoingSound;
    }

    public void setPlayOutgoingSound(boolean playOutgoingSound) {
        this.playOutgoingSound = playOutgoingSound;
    }

    public boolean isPlayIncomingSound() {
        return playIncomingSound;
    }

    public void setPlayIncomingSound(boolean playIncomingSound) {
        this.playIncomingSound = playIncomingSound;
    }

    public boolean isPlayOfflineSound() {
        return playOfflineSound;
    }

    public void setPlayOfflineSound(boolean playOfflineSound) {
        this.playOfflineSound = playOfflineSound;
    }
}
