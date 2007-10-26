/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
            StringBuilder buffer = null;
            LogPacket lp = new LogPacket();
            lp.parser = parser;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG) {
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
