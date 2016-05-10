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

import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.jivesoftware.smackx.iqprivate.provider.PrivateDataProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsDataProvider implements PrivateDataProvider {

    public SettingsDataProvider() {
    }

    public PrivateData parsePrivateData(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Map<String,String> map = new HashMap<>();
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