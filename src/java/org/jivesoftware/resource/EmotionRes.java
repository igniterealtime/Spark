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
    private static final Map<String,String> emotionMap = new LinkedHashMap<String,String>();
    static ClassLoader cl = EmotionRes.class.getClassLoader();

    static {
        emotionMap.put(":)", "images/emoticons/happy.png");
        emotionMap.put(":-)", "images/emoticons/happy.png");
        emotionMap.put(":(", "images/emoticons/sad.png");
        emotionMap.put(":D", "images/emoticons/grin.png");
        emotionMap.put(":x", "images/emoticons/love.png");
        emotionMap.put(";\\", "images/emoticons/mischief.png");
        emotionMap.put("B-)", "images/emoticons/cool.png");
        emotionMap.put("]:)", "images/emoticons/devil.png");
        emotionMap.put(":p", "images/emoticons/silly.png");
        emotionMap.put("X-(", "images/emoticons/angry.png");
        emotionMap.put(":^0", "images/emoticons/laugh.png");
        emotionMap.put(";)", "images/emoticons/wink.png");
        emotionMap.put(";-)", "images/emoticons/wink.png");
        emotionMap.put(":8}", "images/emoticons/blush.png");
        emotionMap.put(":_|", "images/emoticons/cry.png");
        emotionMap.put("?:|", "images/emoticons/confused.png");
        emotionMap.put(":0", "images/emoticons/shocked.png");
        emotionMap.put(":|", "images/emoticons/plain.png");
        emotionMap.put("8-)", "images/emoticons/eyeRoll.gif");
        emotionMap.put("|-)", "images/emoticons/sleepy.gif");
        emotionMap.put("<:o)", "images/emoticons/party.gif");
    }

    public static ImageIcon getImageIcon(String face) {
        final String value = emotionMap.get(face);
        if (value != null) {
            final URL url = cl.getResource(value);
            if (url != null) {
                return new ImageIcon(url);
            }
        }
        return null;
    }

    public static URL getURL(String face) {
        final String value = emotionMap.get(face);
        if (value != null) {
            final URL url = cl.getResource(value);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    public static Map getEmoticonMap() {
        Map<String,String> newMap = new HashMap<String,String>(emotionMap);
        newMap.remove("8-)");
        newMap.remove("|-)");
        newMap.remove("<:o)");
        newMap.remove(":-)");
        return newMap;
    }

}