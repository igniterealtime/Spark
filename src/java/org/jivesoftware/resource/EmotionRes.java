/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.resource;

import javax.swing.ImageIcon;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EmotionRes {
    private static final Map emotionMap = new LinkedHashMap();
    static ClassLoader cl = EmotionRes.class.getClassLoader();

    static {
        emotionMap.put(":)", "images/emoticons/happy.gif");
        emotionMap.put(":-)", "images/emoticons/happy.gif");
        emotionMap.put(":(", "images/emoticons/sad.gif");
        emotionMap.put(":D", "images/emoticons/grin.gif");
        emotionMap.put(":x", "images/emoticons/love.gif");
        emotionMap.put(";\\", "images/emoticons/mischief.gif");
        emotionMap.put("B-)", "images/emoticons/cool.gif");
        emotionMap.put("]:)", "images/emoticons/devil.gif");
        emotionMap.put(":p", "images/emoticons/silly.gif");
        emotionMap.put("X-(", "images/emoticons/angry.gif");
        emotionMap.put(":^0", "images/emoticons/laugh.gif");
        emotionMap.put(";)", "images/emoticons/wink.gif");
        emotionMap.put(";-)", "images/emoticons/wink.gif");
        emotionMap.put(":8}", "images/emoticons/blush.gif");
        emotionMap.put(":_|", "images/emoticons/cry.gif");
        emotionMap.put("?:|", "images/emoticons/confused.gif");
        emotionMap.put(":0", "images/emoticons/shocked.gif");
        emotionMap.put(":|", "images/emoticons/plain.gif");
        emotionMap.put("8-)", "images/emoticons/eyeRoll.gif");
        emotionMap.put("|-)", "images/emoticons/sleepy.gif");
        emotionMap.put("<:o)", "images/emoticons/party.gif");
    }

    public static final ImageIcon getImageIcon(String face) {
        final String value = (String)emotionMap.get(face);
        if (value != null) {
            final URL url = cl.getResource(value);
            if (url != null) {
                return new ImageIcon(url);
            }
        }
        return null;
    }

    public static final URL getURL(String face) {
        final String value = (String)emotionMap.get(face);
        if (value != null) {
            final URL url = cl.getResource(value);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    public static Map getEmoticonMap() {
        Map newMap = new HashMap(emotionMap);
        newMap.remove("8-)");
        newMap.remove("|-)");
        newMap.remove("<:o)");
        newMap.remove(":-)");
        return newMap;
    }

}