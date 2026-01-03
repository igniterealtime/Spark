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

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
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

    private String notes;

    /**
     * Required Empty Constructor to use Bookmarks.
     */
    public PrivateNotes() {
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
    	if(notes!=null)
    		{	
    			this.notes=notes.replaceAll("&","&amp;");
    		} else {
    			this.notes=notes;
    	}
    }

    public void setMyNotes(String notes) {
    this.notes=notes;
    
    }

    /**
     * Returns the root element name.
     *
     * @return the element name.
     */
    @Override
	public String getElementName() {
        return "scratchpad";
    }

    /**
     * Returns the root element XML namespace.
     *
     * @return the namespace.
     */
    @Override
	public String getNamespace() {
        return "scratchpad:notes";
    }

    /**
     * Returns the XML reppresentation of the PrivateData.
     *
     * @return the private data as XML.
     */
    @Override
	public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<scratchpad xmlns=\"scratchpad:notes\">");

        if (getNotes() != null) {
            buf.append("<text>").append(getNotes()).append("</text>");
        }

        buf.append("</scratchpad>");
        return buf.toString();
    }

    /**
     * The IQ Provider for BookmarkStorage.
     *
     * @author Derek DeMoro
     */
    public static class Provider implements PrivateDataProvider {
        private final PrivateNotes notes = new PrivateNotes();

        /**
         * Empty Constructor for PrivateDataProvider.
         */
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
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if ("scratchpad".equals(parser.getName())) {
                        done = true;
                    }
                }
            }


            return notes;
        }
    }

    public static void savePrivateNotes(PrivateNotes notes) {
        PrivateDataManager manager = PrivateDataManager.getInstanceFor(SparkManager.getConnection());

        PrivateDataManager.addPrivateDataProvider("scratchpad", "scratchpad:notes", new PrivateNotes.Provider());
        try {
            manager.setPrivateData(notes);
        }
        catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }
    }

    public static PrivateNotes getPrivateNotes() {
        PrivateDataManager manager = PrivateDataManager.getInstanceFor(SparkManager.getConnection());

        PrivateDataManager.addPrivateDataProvider("scratchpad", "scratchpad:notes", new PrivateNotes.Provider());

        PrivateNotes notes = null;

        try {
            notes = (PrivateNotes)manager.getPrivateData("scratchpad", "scratchpad:notes");
        }
        catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }

        if(notes.getNotes() != null )
        	{
        		String note=notes.getNotes().replaceAll("&amp;","&");
        		notes.setMyNotes(note);
        	}
        
        return notes;
    }
}
