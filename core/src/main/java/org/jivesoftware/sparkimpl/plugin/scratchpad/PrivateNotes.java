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
package org.jivesoftware.sparkimpl.plugin.scratchpad;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.jivesoftware.smackx.iqprivate.provider.PrivateDataProvider;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.JxmppContext;

import java.io.IOException;

/**
 * @author Derek DeMoro
 */
public class PrivateNotes implements PrivateData {
    public static final String ELEMENT = "scratchpad";
    public static final String NAMESPACE = "scratchpad:notes";

    private String notes;

    public PrivateNotes() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder buf = new XmlStringBuilder();
        buf.halfOpenElement(ELEMENT).xmlnsAttribute(NAMESPACE).rightAngleBracket();
        buf.optElement("text", getNotes());
        buf.closeElement(ELEMENT);
        return buf;
    }

    /**
     * The IQ Provider for BookmarkStorage.
     */
    public static class Provider implements PrivateDataProvider {
        private final PrivateNotes notes = new PrivateNotes();

        public Provider() {
            super();
        }

        @Override
        public PrivateData parsePrivateData(XmlPullParser parser, JxmppContext jxmppContext) throws XmlPullParserException, IOException {
            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && "text".equals(parser.getName())) {
                    notes.setNotes(parser.nextText());
                } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if (ELEMENT.equals(parser.getName())) {
                        done = true;
                    }
                }
            }
            return notes;
        }
    }

    static {
        PrivateDataManager.addPrivateDataProvider(ELEMENT, NAMESPACE, new PrivateNotes.Provider());
    }

    public static void savePrivateNotes(PrivateNotes notes) {
        PrivateDataManager manager = SparkManager.getSessionManager().getPersonalDataManager();
        try {
            manager.setPrivateData(notes);
        } catch (Exception e) {
            Log.error(e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static PrivateNotes getPrivateNotes() {
        PrivateDataManager manager = SparkManager.getSessionManager().getPersonalDataManager();
        try {
            PrivateData privateData = manager.getPrivateData(ELEMENT, NAMESPACE);
            return privateData != null ? (PrivateNotes) privateData : new PrivateNotes();
        } catch (Exception e) {
            Log.error(e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
