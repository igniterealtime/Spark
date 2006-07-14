/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.settings;

import org.jivesoftware.smackx.packet.PrivateData;

import java.util.Iterator;
import java.util.Map;

public class SettingsData implements PrivateData {
    private Map settingsMap;


    public SettingsData(Map map) {
        settingsMap = map;
    }

    public Map getMap() {
        return settingsMap;
    }

    public String getElementName() {
        return "personal_settings";
    }

    public String getNamespace() {
        return "jive:user:settings";
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<" + getElementName() + " xmlns=\"" + getNamespace() + "\">");
        String key;
        for (Iterator iter = settingsMap.keySet().iterator(); iter.hasNext(); buf.append("</" + key + "></entry>")) {
            key = (String)iter.next();
            String value = (String)settingsMap.get(key);
            buf.append("<entry xmlns=\"\">");
            buf.append("<" + key + ">");
            buf.append(value);
        }

        buf.append("</" + getElementName() + ">");
        return buf.toString();
    }


}