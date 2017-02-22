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

package org.jivesoftware.sparkplugin.sipaccount;

import org.jivesoftware.smack.*;
import org.jivesoftware.sparkplugin.calllog.LogPacket;
import net.java.sipmack.common.Log;
import net.java.sipmack.sip.SipRegisterStatus;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class SipAccountPacket extends IQ {

    private SipAccount sipAccount;

    private String content = "";

    private Type type = Type.registration;

    public SipAccountPacket() {
        super( ELEMENT_NAME, NAMESPACE );
    }

    public SipAccountPacket(Type type) {
        super( ELEMENT_NAME, NAMESPACE );
        this.type = type;
    }

    public enum Type {
        registration, status
    }

    /**
     * Element name of the packet extension.
     */
    public static final String NAME = "sipark";

    /**
     * Element name of the packet extension.
     */
    public static final String ELEMENT_NAME = "phone";

    /**
     * Namespace of the packet extension.
     */
    public static final String NAMESPACE = "http://www.jivesoftware.com/protocol/sipark";

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        buf.rightAngleBracket();
        buf.append("<").append(ELEMENT_NAME).append(" xmlns='").append(
                NAMESPACE).append("'>");
        buf.append("<").append(this.type.name()).append(">");
        buf.append(content);
        buf.append("</").append(this.type.name()).append(">");
        buf.append("</").append(ELEMENT_NAME).append(">");
        return buf;    }

    /**
     * An IQProvider for SparkVersion packets.
     *
     * @author Derek DeMoro
     */
    public static class Provider extends IQProvider<SipAccountPacket> {

        public Provider() {
            super();
        }

        public SipAccountPacket parse(XmlPullParser parser, int i) throws XmlPullParserException, IOException {
            SipAccountPacket packet = new SipAccountPacket();
            SipAccount sip = new SipAccount();

            String type = parser.getAttributeValue("", "type");
            if ("unregistered".equals(type)) {
                return packet;
            }

            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("username")) {
                        sip.setSipUsername(parser.nextText());
                    }
                    else if (parser.getName().equals("authUsername")) {
                        sip.setAuthUsername(parser.nextText());
                    }
                    else if (parser.getName().equals("displayPhoneNum")) {
                        sip.setDisplayName(parser.nextText());
                    }
                    else if (parser.getName().equals("password")) {
                        sip.setPassword(parser.nextText());
                    }
                    else if (parser.getName().equals("server")) {
                        sip.setServer(parser.nextText());
                    }
                    else if (parser.getName().equals("voicemail")) {
                        sip.setVoiceMailNumber(parser.nextText());
                    }
                    else if (parser.getName().equals("stunServer")) {
                        sip.setStunServer(parser.nextText());
                    }
                    else if (parser.getName().equals("useStun")) {
                        sip.setUseStun(Boolean.parseBoolean(parser.nextText()));
                    }
                    else if (parser.getName().equals("stunPort")) {
                        sip.setStunPort(parser.nextText());
                    }
                    else if (parser.getName().equals("enabled")) {
                        sip.setEnabled(Boolean.parseBoolean(parser.nextText()));
                    }
                    else if (parser.getName().equals("outboundproxy")) {
                        sip.setOutboundproxy(parser.nextText());
                    }
                    else if (parser.getName().equals("promptCredentials")) {
                        sip.setPromptCredentials(Boolean.parseBoolean(parser.nextText()));
                    }

                }

                else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals(ELEMENT_NAME)) {
                        done = true;
                    }
                }
            }
            packet.setSipAcccount(sip);
            return packet;
        }
    }

    /**
     * Returns the SIP Setting for the user.
     *
     * @param connection the XMPPConnection to use.
     * @return the information for about the latest Spark Client.
     * @throws XMPPException thrown if an exception occurs while retrieving Sip Settings.
     */
    public static SipAccountPacket getSipSettings(XMPPConnection connection) throws XMPPException, SmackException
    {
        SipAccountPacket sp = new SipAccountPacket();

        sp.setTo("sipark." + connection.getServiceName());
        sp.setType(IQ.Type.get);

        PacketCollector collector = connection.createPacketCollector(new PacketIDFilter(sp.getPacketID()));
        connection.sendStanza(sp);

        SipAccountPacket response = (SipAccountPacket)collector.nextResult(SmackConfiguration.getDefaultPacketReplyTimeout());

        // Cancel the collector.
        collector.cancel();
        if (response == null) {
            throw SmackException.NoResponseException.newWith( connection, collector );
        }
        XMPPException.XMPPErrorException.ifHasErrorThenThrow( response );
        return response;
    }

    /**
     * Sends the current SIP registering status.
     *
     * @param connection the XMPPConnection to use.
     * @param register   the current registration status.
     * @throws XMPPException thrown if an exception occurs.
     */
    public static void setSipRegisterStatus(XMPPConnection connection, SipRegisterStatus register) throws XMPPException, SmackException {
        if(!connection.isConnected()){
            return;
        }
        SipAccountPacket sp = new SipAccountPacket(SipAccountPacket.Type.status);

        sp.setTo("sipark." + connection.getServiceName());
        sp.setType(IQ.Type.set);
        sp.setContent(register.name());

        PacketCollector collector = connection
                .createPacketCollector(new PacketIDFilter(sp.getPacketID()));
        connection.sendStanza(sp);

        SipAccountPacket response = (SipAccountPacket)collector
                .nextResult(SmackConfiguration.getDefaultPacketReplyTimeout());

        // Cancel the collector.
        collector.cancel();
        if (response == null) {
            throw SmackException.NoResponseException.newWith( connection, collector );
        }
        XMPPException.XMPPErrorException.ifHasErrorThenThrow( response );
    }

    /**
     * Does a service discovery on the server to see if a SIPpark Manager is
     * enabled.
     *
     * @param con the XMPPConnection to use.
     * @return true if SIPpark Manager is available.
     */
    public static boolean isSoftPhonePluginInstalled(XMPPConnection con) {
        if (!con.isConnected()) {
            return false;
        }

        ServiceDiscoveryManager disco = ServiceDiscoveryManager
                .getInstanceFor(con);
        try {
            DiscoverItems items = disco.discoverItems(con.getServiceName());
            for ( DiscoverItems.Item item : items.getItems() ) {
                if ("SIP Controller".equals(item.getName())) {
                    Log.debug("SIP Controller Found");
                    return true;
                }
            }
        }
        catch (XMPPException | SmackException e) {
            Log.error("isSparkPluginInstalled", e);
        }

        return false;

    }

    public SipAccount getSipAccount() {
        return sipAccount;
    }

    public void setSipAcccount(SipAccount sipAccount) {
        this.sipAccount = sipAccount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (this.type == Type.status) {
            this.content = content;
        }
    }
}
