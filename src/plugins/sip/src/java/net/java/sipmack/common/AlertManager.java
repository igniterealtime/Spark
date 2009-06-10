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
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 */

public class AlertManager {
    private Hashtable<String, AudioClip> alertClips = new Hashtable<String, AudioClip>();

    public AlertManager() {
    }

    public void startAlert(String alertResourceName) {

        AudioClip alertClip = getAlertClip(alertResourceName, true);
        if (alertClip == null) {
            return;
        }
        boolean loop = true;
        if (!loop) {
            alertClip.play();
        } else {
            alertClip.loop();
        }
    }

    public void stopAllAlerts() {
        Enumeration<String> alertClipsEnum = alertClips.keys();
        while (alertClipsEnum.hasMoreElements()) {
            String alert = alertClipsEnum.nextElement();
            stopAlert(alert);
        }
    }

    public void stopAlert(String alertResourceName) {
        AudioClip alertClip = getAlertClip(alertResourceName, false);
        if (alertClip != null) {
            alertClip.stop();
        }
    }

    private AudioClip getAlertClip(String alertResourceName, boolean create) {
        AudioClip clip = (AudioClip) alertClips.get(alertResourceName);
        if (clip == null && create) {
            clip = Applet.newAudioClip(PhoneRes.getURL(alertResourceName));
            if (clip != null) {
                alertClips.put(alertResourceName, clip);
            }
        }
        return clip;
    }
}