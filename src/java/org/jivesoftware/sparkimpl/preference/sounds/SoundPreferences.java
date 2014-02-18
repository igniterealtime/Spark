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

package org.jivesoftware.sparkimpl.preference.sounds;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.IOException;

public class SoundPreferences {

    private String outgoingSound;
    private String incomingSound;
    private String offlineSound;
    private String incomingInvitationSound;

    private boolean playOutgoingSound = false;
    private boolean playIncomingSound = false;
    private boolean playOfflineSound = false;
    private boolean playIncomingInvitationSound = false;

    public SoundPreferences() {
        // Set initial sounds
        try {
            outgoingSound = new File(Spark.getResourceDirectory(), "sounds/outgoing.wav").getCanonicalPath();
            incomingSound = new File(Spark.getResourceDirectory(), "sounds/incoming.wav").getCanonicalPath();
            incomingInvitationSound = new File(Spark.getResourceDirectory(), "sounds/incoming.wav").getCanonicalPath();
            offlineSound = new File(Spark.getResourceDirectory(), "sounds/presence_changed.wav").getCanonicalPath();
        }
        catch (IOException e) {
            Log.error(e);
        }
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



    public void setIncomingInvitationSoundFile(String sound) {
        incomingInvitationSound = sound;
    }

    public String getIncomingInvitationSoundFile() {
        return incomingInvitationSound;
    }

    public boolean playIncomingInvitationSound() {
        return playIncomingInvitationSound;
    }

    public void setPlayIncomingInvitationSound(boolean play) {
        this.playIncomingInvitationSound = play;
    }
}
