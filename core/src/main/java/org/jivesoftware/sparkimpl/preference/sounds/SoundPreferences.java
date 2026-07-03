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
    private String chatRequestSound;
    private String attentionBuzzSound;

    private boolean playOutgoingSound;
    private boolean playIncomingSound;
    private boolean playOfflineSound;
    private boolean playIncomingInvitationSound;
    private boolean playChatRequestSound;
    private boolean playAttentionBuzzSound;

    public SoundPreferences() {
        // Set initial sounds
        try {
            outgoingSound = new File(Spark.getResourceDirectory(), "sounds/outgoing.wav").getCanonicalPath();
            incomingSound = new File(Spark.getResourceDirectory(), "sounds/incoming.wav").getCanonicalPath();
            incomingInvitationSound = new File(Spark.getResourceDirectory(), "sounds/incoming.wav").getCanonicalPath();
            offlineSound = new File(Spark.getResourceDirectory(), "sounds/presence_changed.wav").getCanonicalPath();
            chatRequestSound = new File(Spark.getResourceDirectory(), "sounds/chat_request.wav").getCanonicalPath();
            attentionBuzzSound = new File(Spark.getResourceDirectory(), "sounds/bell.wav").getCanonicalPath();
        }
        catch (IOException e) {
            Log.error(e);
        }
    }

    public String getIncomingSound() {
        return incomingSound;
    }

    public void setIncomingSound(String sound) {
        this.incomingSound = sound;
    }

    public boolean isPlayIncomingSound() {
        return playIncomingSound;
    }

    public void setPlayIncomingSound(boolean play) {
        this.playIncomingSound = play;
    }


    public String getOutgoingSound() {
        return outgoingSound;
    }

    public void setOutgoingSound(String sound) {
        this.outgoingSound = sound;
    }

    public boolean isPlayOutgoingSound() {
        return playOutgoingSound;
    }

    public void setPlayOutgoingSound(boolean play) {
        this.playOutgoingSound = play;
    }


    public String getOfflineSound() {
        return offlineSound;
    }

    public void setOfflineSound(String sound) {
        this.offlineSound = sound;
    }

    public boolean isPlayOfflineSound() {
        return playOfflineSound;
    }

    public void setPlayOfflineSound(boolean play) {
        this.playOfflineSound = play;
    }


    public void setIncomingInvitationSound(String sound) {
        incomingInvitationSound = sound;
    }

    public String getIncomingInvitationSound() {
        return incomingInvitationSound;
    }

    public boolean isPlayIncomingInvitationSound() {
        return playIncomingInvitationSound;
    }

    public void setPlayIncomingInvitationSound(boolean play) {
        this.playIncomingInvitationSound = play;
    }


    public String getChatRequestSound() {
        return chatRequestSound;
    }

    public void setChatRequestSound(String sound) {
        this.chatRequestSound = sound;
    }

    public boolean isPlayChatRequestSound() {
        return playChatRequestSound;
    }

    public void setPlayChatRequestSound(boolean play) {
        this.playChatRequestSound = play;
    }


    public String getAttentionBuzzSound() {
        return attentionBuzzSound;
    }

    public void setAttentionBuzzSound(String sound) {
        this.attentionBuzzSound = sound;
    }

    public boolean isPlayAttentionBuzzSound() {
        return playAttentionBuzzSound;
    }

    public void setPlayAttentionBuzzSound(boolean play) {
        this.playAttentionBuzzSound = play;
    }
}
