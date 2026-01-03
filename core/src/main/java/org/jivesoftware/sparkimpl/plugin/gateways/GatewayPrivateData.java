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
package org.jivesoftware.sparkimpl.plugin.gateways;

import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.jivesoftware.smackx.iqprivate.provider.PrivateDataProvider;
import org.jxmpp.JxmppContext;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handle Gateway preferences through private data to persist through seperate locations of the client.
 *
 * @author Derek DeMoro
 */
public class GatewayPrivateData implements PrivateData {

    private final Map<DomainBareJid, String> loginSettingsMap = new HashMap<>();

    public static final String ELEMENT = "gateway-settings";
    public static final String NAMESPACE = "http://www.jivesoftware.org/spark";

    public void addService(DomainBareJid serviceName, boolean autoLogin) {
        loginSettingsMap.put(serviceName, Boolean.toString(autoLogin));
    }

    public boolean autoLogin(DomainBareJid serviceName) {
        String str = loginSettingsMap.get(serviceName);
        if(str == null){
            return true;
        }

        return Boolean.parseBoolean(str);
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
	public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        buf.append("<gateways>");
        for (DomainBareJid serviceName : loginSettingsMap.keySet()) {
            buf.append("<gateway>");
            String autoLogin = loginSettingsMap.get(serviceName);
            buf.append("<serviceName>").append(serviceName).append("</serviceName>");
            buf.append("<autoLogin>").append(autoLogin).append("</autoLogin>");
            buf.append("</gateway>");
        }
        buf.append("</gateways>");


        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }

    public static class ConferencePrivateDataProvider implements PrivateDataProvider {

        public ConferencePrivateDataProvider() {
        }

        @Override
        public PrivateData parsePrivateData(XmlPullParser parser, JxmppContext jxmppContext) throws XmlPullParserException, IOException {
            GatewayPrivateData data = new GatewayPrivateData();

            boolean done = false;

            boolean isInstalled = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && parser.getName().equals("gateways")) {
                    isInstalled = true;
                }

                if (eventType == XmlPullParser.Event.START_ELEMENT && parser.getName().equals("gateway")) {
                    boolean gatewayDone = false;
                    DomainBareJid serviceName = null;
                    String autoLogin = null;
                    while (!gatewayDone) {
                        XmlPullParser.Event eType = parser.next();
                        if (eType == XmlPullParser.Event.START_ELEMENT && parser.getName().equals("serviceName")) {
                            String serviceNameString = parser.nextText();
                            serviceName = JidCreate.domainBareFrom(serviceNameString);
                        }
                        else if (eType == XmlPullParser.Event.START_ELEMENT && parser.getName().equals("autoLogin")) {
                            autoLogin = parser.nextText();
                        }
                        else if (eType == XmlPullParser.Event.END_ELEMENT && parser.getName().equals("gateway")) {
                            data.addService(serviceName, Boolean.parseBoolean(autoLogin));
                            gatewayDone = true;
                        }
                    }
                }

                else if (eventType == XmlPullParser.Event.END_ELEMENT && parser.getName().equals("gateways")) {
                    done = true;
                }
                else if (!isInstalled) {
                    done = true;
                }
            }
            return data;
        }
    }


}
