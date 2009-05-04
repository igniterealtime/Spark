/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.updater;

import org.jivesoftware.Spark;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

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

    }

    public String getChildElementXML() {
        StringBuffer buf = new StringBuffer();
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
        return buf.toString();
    }

    /**
     * An IQProvider for SparkVersion packets.
     *
     * @author Derek DeMoro
     */
    public static class Provider implements IQProvider {

        public Provider() {
            super();
        }

        public IQ parseIQ(XmlPullParser parser) throws Exception {
            SparkVersion version = new SparkVersion();

            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("version")) {
                        version.setVersion(parser.nextText());
                    }
                    else if (parser.getName().equals("updatedTime")) {
                        Long time = Long.valueOf(parser.nextText());
                        version.setUpdateTime(time);
                    }
                    else if (parser.getName().equals("downloadURL")) {
                        version.setDownloadURL(parser.nextText());
                    }
                    else if (parser.getName().equals("displayMessage")) {
                        version.setDisplayMessage(parser.nextText());
                    }
                }

                else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals(ELEMENT_NAME)) {
                        done = true;
                    }
                }
            }

            return version;
        }
    }
}
