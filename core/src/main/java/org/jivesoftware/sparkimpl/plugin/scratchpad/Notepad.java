/**
 * Copyright (C) 2026 Ignite Realtime. All rights reserved.
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Notepad represents a collection of notes unlike the Scratchpad that is a single note.
 * It's compatible with Miranda IM and Psi <a href="https://psi-im.org/wiki/doku.php?id=en:plugins#storage_notes_plugin">Storage Notes Plugin</a>.
 * <pre>
 * <iq type="set" id="strnotes_1">
 *     <query xmlns="jabber:iq:private">
 *         <storage xmlns="http://miranda-im.org/storage#notes">
 *             <note tags="tag1 tag2">
 *                 <title>test3</title>
 *                 <text>Body</text>
 *             </note>
 *             <note tags="tag2">
 *                 <title>test3</title>
 *                 <text>Body</text>
 *             </note>
 *          </storage>
 *     </query>
 * </id>
 * </pre>
 *
 * @author Sergey Ponomarev
 */
public class Notepad implements PrivateData {
    public static final String NAMESPACE = "http://miranda-im.org/storage#notes";
    public static final String ELEMENT = "storage";

    private List<NotepadNote> notes = new ArrayList<>();

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder buf = new XmlStringBuilder();
        buf.halfOpenElement(ELEMENT).xmlnsAttribute(NAMESPACE).rightAngleBracket();
        for (NotepadNote note : notes) {
            buf.halfOpenElement("note");
            if (note.getTags() != null && !note.getTags().isEmpty()) {
                buf.attribute("tags", String.join(" ", note.getTags()));
            }
            buf.rightAngleBracket();
            buf.optElement("title", note.getTitle());
            buf.optElement("text", note.getText());
            buf.closeElement("note");
        }
        buf.closeElement(ELEMENT);
        return buf;
    }

    public void setNotes(List<NotepadNote> notes) {
        this.notes = notes;
    }

    public List<NotepadNote> getNotes() {
        return notes;
    }

    public static class NotepadNote {
        private String title;
        private List<String> tags = new ArrayList<>();
        private String text;
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "Notepad{" +
                "title='" + title + '\'' +
                ", tags=" + String.join(" ", tags) +
                ", text='" + text + '\'' +
                '}';
        }

    }

    /**
     * The IQ Provider for Notepad
     */
    public static class Provider implements PrivateDataProvider {

        public Provider() {
            super();
        }

        @Override
        public PrivateData parsePrivateData(XmlPullParser parser, JxmppContext jxmppContext) throws XmlPullParserException, IOException {
            Notepad notepad = new Notepad();
            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && "note".equals(parser.getName())) {
                    notepad.getNotes().add(parseNotepadNote(parser));
                } else if (eventType == XmlPullParser.Event.END_ELEMENT && ELEMENT.equals(parser.getName())) {
                    done = true;
                }
            }
            return notepad;
        }

        private NotepadNote parseNotepadNote(XmlPullParser parser) throws IOException, XmlPullParserException {
            NotepadNote note = new NotepadNote();
            String tagsAttr = parser.getAttributeValue("", "tags");

            List<String> tags = tagsAttr != null ? Stream.of(tagsAttr.split(" ")).collect(Collectors.toList()) : List.of();
            note.setTags(tags);

            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && "title".equals(parser.getName())) {
                    note.setTitle(parser.nextText());
                } else if (eventType == XmlPullParser.Event.START_ELEMENT && "text".equals(parser.getName())) {
                    note.setText(parser.nextText());
                } else if (eventType == XmlPullParser.Event.END_ELEMENT && "note".equals(parser.getName())) {
                    done = true;
                }
            }
            return note;
        }

    }

    static {
        PrivateDataManager.addPrivateDataProvider(Notepad.ELEMENT, Notepad.NAMESPACE, new Notepad.Provider());
    }


    public static void saveNotepad(Notepad notepad) {
        PrivateDataManager manager = SparkManager.getSessionManager().getPersonalDataManager();
        try {
            manager.setPrivateData(notepad);
        } catch (Exception e) {
            Log.error(e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Notepad getNotepad() {
        PrivateDataManager manager = SparkManager.getSessionManager().getPersonalDataManager();
        try {
            PrivateData privateData = manager.getPrivateData(ELEMENT, NAMESPACE);
            return privateData != null ? (Notepad) privateData : new Notepad();
        } catch (Exception e) {
            Log.error(e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
