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

package org.jivesoftware.sparkimpl.updater;

import org.jivesoftware.Spark;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IqData;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.parsing.SmackParsingException;
import org.jivesoftware.smack.provider.IqProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jxmpp.JxmppContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class SparkVersion extends IQ {

    private String version;
    private long updateTime;
    private String downloadURL;
    private String displayMessage;
    private String changeLogURL;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getUpdateTime() {
        return new Date(updateTime);
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getChangeLogURL() {
        return changeLogURL;
    }

    public void setChangeLogURL(String changeLogURL) {
        this.changeLogURL = changeLogURL;
    }


    /**
     * Element name of the packet extension.
     */
    public static final String ELEMENT_NAME = "query";

    /**
     * Namespace of the packet extension.
     */
    public static final String NAMESPACE = "jabber:iq:spark";

    public SparkVersion() {
        super( ELEMENT_NAME, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf)
    {
        buf.rightAngleBracket();
        buf.append("<query xmlns=\"jabber:iq:spark\">");

        // Add os specific information

        if (Spark.isWindows()) {
            buf.append("<os>windows</os>");
        }
        else if (Spark.isMac()) {
            buf.append("<os>mac</os>");
        }
        else {
            buf.append("<os>linux</os>");
        }

        buf.append("</query>");
        return buf;
    }

    /**
     * An IQProvider for SparkVersion packets.
     *
     * @author Derek DeMoro
     */
    public static class Provider extends IqProvider<SparkVersion> {

        public Provider() {
            super();
        }

        @Override
		public SparkVersion parse(XmlPullParser parser, int i, IqData iqData, XmlEnvironment xmlEnvironment, JxmppContext jxmppContext) throws XmlPullParserException, IOException{
            SparkVersion version = new SparkVersion();

            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT) {
                    switch (parser.getName()) {
                        case "version":
                            version.setVersion(parser.nextText());
                            break;
                        case "updatedTime":
                            long time = Long.parseLong(parser.nextText());
                            version.setUpdateTime(time);
                            break;
                        case "downloadURL":
                            version.setDownloadURL(parser.nextText());
                            break;
                        case "displayMessage":
                            version.setDisplayMessage(parser.nextText());
                            break;
                    }
                }

                else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if (parser.getName().equals(ELEMENT_NAME)) {
                        done = true;
                    }
                }
            }

            return version;
        }
    }
}
