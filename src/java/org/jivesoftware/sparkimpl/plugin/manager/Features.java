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
package org.jivesoftware.sparkimpl.plugin.manager;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;


public class Features implements PacketExtension {

    private List<String> availableFeatures = new ArrayList<String>();


    public List<String> getAvailableFeatures() {
        return availableFeatures;
    }

    public void addFeature(String feature) {
        availableFeatures.add(feature);
    }

    /**
     * Element name of the packet extension.
     */
    public static final String ELEMENT_NAME = "event";

    /**
     * Namespace of the packet extension.
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/disco#info";


    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<event xmlns=\"" + NAMESPACE + "\"").append("</event>");
        return buf.toString();
    }

    public static class Provider implements PacketExtensionProvider {

        public PacketExtension parseExtension(XmlPullParser parser) throws Exception {

            Features features = new Features();
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG && "event".equals(parser.getName())) {
                    parser.nextText();
                }
                if (eventType == XmlPullParser.START_TAG && "feature".equals(parser.getName())) {
                    String feature = parser.getAttributeValue("", "var");
                    features.addFeature(feature);
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    if ("event".equals(parser.getName())) {
                        done = true;
                    }
                }
            }

            return features;
        }
    }
}
