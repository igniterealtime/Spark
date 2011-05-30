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

package org.jivesoftware.sparkimpl.settings;

import org.jivesoftware.smackx.packet.PrivateData;

import java.util.Iterator;
import java.util.Map;

public class SettingsData implements PrivateData {
    private Map<String,String> settingsMap;


    public SettingsData(Map<String,String> map) {
        settingsMap = map;
    }

    public Map<String,String> getMap() {
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
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        String key;
        for (Iterator<String> iter = settingsMap.keySet().iterator(); iter.hasNext(); buf.append("</").append(key).append("></entry>")) {
            key = iter.next();
            String value = settingsMap.get(key);
            buf.append("<entry xmlns=\"\">");
            buf.append("<").append(key).append(">");
            buf.append(value);
        }

        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }


}