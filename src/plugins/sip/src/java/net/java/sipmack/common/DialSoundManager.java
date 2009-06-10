/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package net.java.sipmack.common;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

import java.applet.Applet;
import java.applet.AudioClip;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 */

public class DialSoundManager {

    private AudioClip clips[];


    public DialSoundManager() {
        clips = new AudioClip[12];
        for (int i = 0; i < 12; i++) {
            clips[i] = Applet.newAudioClip(PhoneRes.getURL("DTMF" + i + "_SOUND"));
        }
    }

    public void play(int n) {
        clips[n].play();
    }

    public void play(String s) {
        int n = -1;
        if (s.equals("*"))
            n = 10;
        else if (s.equals("#"))
            n = 11;
        else
            try {
                n = Integer.parseInt(s);
            }
            catch (Exception e) {
            }

        if (n >= 0 && n <= 11)
            clips[n].play();
    }
}
