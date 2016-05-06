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
package org.jivesoftware.sparkimpl.plugin.gateways;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.spark.SparkManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 *
 */
public class Gateway extends IQ {

    private String jid;
    private String username;

    protected Gateway()
    {
        super( ELEMENT_NAME, NAMESPACE );
    }


    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Element name of the packet extension.
     */
    public static final String ELEMENT_NAME = "query";

    /**
     * Namespace of the packet extension.
     */
    public static final String NAMESPACE = "jabber:iq:gateway";

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        buf.rightAngleBracket();
        buf.append("<query xmlns=\"").append(NAMESPACE).append("\">");
        buf.append("<prompt>").append(username).append("</prompt>");
        buf.append("</query>");
        return buf;
    }

    /**
     * An IQProvider for Gateway packet.
     *
     * @author Derek DeMoro
     */
    public static class Provider extends IQProvider<Gateway> {

        public Provider() {
            super();
        }

        public Gateway parse(XmlPullParser parser, int i) throws IOException, XmlPullParserException
        {
            Gateway version = new Gateway();

            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("jid")) {
                        version.setJid(parser.nextText());
                    }
                    else if (parser.getName().equals("username")) {
                        version.setUsername(parser.nextText());
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

    /**
     * Returns the fully qualified JID of a user.
     *
     * @param serviceName the service the user belongs to.
     * @param username    the name of the user.
     * @return the JID.
     */
    public static String getJID(String serviceName, String username) throws SmackException.NotConnectedException
    {
        Gateway registration = new Gateway();
        registration.setType(IQ.Type.set);
        registration.setTo(serviceName);
        registration.setUsername(username);

        XMPPConnection con = SparkManager.getConnection();
        PacketCollector collector = con.createPacketCollector(new StanzaIdFilter(registration.getStanzaId()));
        try
        {
            con.sendStanza( registration );

            Gateway response = collector.nextResult( SmackConfiguration.getDefaultPacketReplyTimeout() );
            return response.getJid();
        }
        finally
        {
            collector.cancel();
        }
    }


}


