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
import org.jivesoftware.smackx.provider.PrivateDataProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

public class SettingsDataProvider implements PrivateDataProvider {

    public SettingsDataProvider() {
    }

    public PrivateData parsePrivateData(XmlPullParser parser) throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        parser.getEventType();
        parser.nextTag();
        for (String text = parser.getName(); text.equals("entry"); text = parser.getName()) {
            parser.nextTag();
            String name = parser.getName();
            text = parser.nextText();
            map.put(name, text);
            parser.nextTag();
            parser.nextTag();
        }

        return new SettingsData(map);
    }
}