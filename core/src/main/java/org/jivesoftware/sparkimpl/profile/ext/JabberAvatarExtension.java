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

package org.jivesoftware.sparkimpl.profile.ext;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * XEP-0008: IQ-Based Avatars
 * TODO Should be replaced with XEP-0084: User Avatar
 */
public class JabberAvatarExtension implements ExtensionElement {

    public static final String ELEMENT_NAME = "x";

    public static final String NAMESPACE = "jabber:x:avatar";

    public static final QName QNAME = new QName(NAMESPACE, ELEMENT_NAME);

    private String photoHash;

    public void setPhotoHash(String hash) {
        photoHash = hash;
    }

    @Override
	public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
	public String getNamespace() {
        return NAMESPACE;
    }

    @Override
	public String toXML(XmlEnvironment xmlEnvironment) {
        return "<" + getElementName() + " xmlns=\"" + getNamespace() + "\">"
            + "<hash>" + photoHash + "</hash>"
            + "</" + getElementName() + ">";
    }

    public static class Provider extends ExtensionElementProvider<JabberAvatarExtension>
    {
        public Provider() {
        }

        @Override
        public JabberAvatarExtension parse(XmlPullParser parser, int i, XmlEnvironment xmlEnvironment)
                throws XmlPullParserException, IOException {
            final JabberAvatarExtension result = new JabberAvatarExtension();

            while ( true )
            {
                parser.next();
                String elementName;
                switch ( parser.getEventType() )
                {
                    case START_ELEMENT:
                        elementName = parser.getName();
                        if ( "photo".equals( elementName ) )
                        {
                            result.setPhotoHash( parser.nextText() );
                        }
                        break;

                    case END_ELEMENT:
                        elementName = parser.getName();
                        if ( ELEMENT_NAME.equals( elementName ) )
                        {
                            return result;
                        }
                        break;
                }
            }
        }
    }

}
