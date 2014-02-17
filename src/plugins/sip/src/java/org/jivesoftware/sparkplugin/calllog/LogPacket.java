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
package org.jivesoftware.sparkplugin.calllog;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>LogPacket</code> class is the IQ of the CallLog and its provider
 * @version 1.0, 28/09/2006
 */


public class LogPacket extends IQ {

    /**
     * Element name of the packet extension.
     */
    public static final String NAME = "logger";

    /**
     * Element name of the packet extension.
     */
    public static final String ELEMENT_NAME = "log";

    /**
     * Namespace of the packet extension.
     */
    public static final String NAMESPACE = "http://www.jivesoftware.com/protocol/log";

    //TODO REMOVE
    @SuppressWarnings("unused")
    private XmlPullParser parser = null;

    public LogPacket() {
    }

    public String getChildElementXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<" + ELEMENT_NAME + " xmlns='" + NAMESPACE + "'>");
        buf.append(this.getExtensionsXML());
        buf.append("</" + ELEMENT_NAME + ">");
        return buf.toString();
    }

    /**
     * An IQProvider for SIPark packets.
     *
     * @author Thiago Rocha
     */
    public static class Provider implements IQProvider {

        public Provider() {
            super();
        }

        public IQ parseIQ(XmlPullParser parser) throws Exception {
            boolean done = false;
            LogPacket lp = new LogPacket();
            lp.parser = parser;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG) {
                    //TODO REMOVE
                    @SuppressWarnings("unused")
                    String str = null;
                    if (parser.getName().equals("iq")) {
                        int max = parser.getAttributeCount();
                        for (int i = 0; i < max; i++) {
                            if (parser.getAttributeName(i).equals("type")) {
                                str = parser.getAttributeValue(i);
                                break;
                            }
                        }
                    }
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals(ELEMENT_NAME)) {
                        done = true;
                    }
                }
            }
            return lp;
        }

    }

    /**
     * Returns the SIP Setting for the user.
     *
     * @param connection the XMPPConnection to use.
     * @return the information for about the latest Spark Client.
     * @throws XMPPException
     */
    public static LogPacket logEvent(XMPPConnection connection, PacketExtension ext)
            throws XMPPException {

        LogPacket lp = new LogPacket();
        lp.addExtension(ext);

        lp.setTo(NAME + "." + connection.getServiceName());
        lp.setType(IQ.Type.SET);

        PacketCollector collector = connection
                .createPacketCollector(new PacketIDFilter(lp.getPacketID()));
        connection.sendPacket(lp);

        LogPacket response = (LogPacket)collector
                .nextResult(SmackConfiguration.getPacketReplyTimeout());

        // Cancel the collector.
        collector.cancel();
        if (response == null) {
            throw new XMPPException("No response from server.");
        }
        if (response.getError() != null) {
            throw new XMPPException(response.getError());
        }
        return response;
    }
	
}
