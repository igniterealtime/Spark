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

package org.jivesoftware.sparkimpl.profile.ext;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class VCardUpdateExtension implements ExtensionElement {

    public static final String ELEMENT_NAME = "x";

    public static final String NAMESPACE = "vcard-temp:x:update";

    private String photoHash;

    public void setPhotoHash(String hash) {
        photoHash = hash;
    }

    public String getPhotoHash() {
        return photoHash;
    }

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML() {
        String buf = "<" + getElementName() + " xmlns=\"" + getNamespace() + "\">" +
                "<photo>" +
                photoHash +
                "</photo>" +
                "</" + getElementName() + ">";
        return buf;
    }

    public static class Provider extends ExtensionElementProvider<VCardUpdateExtension>
    {
        public Provider() {
        }

        @Override
        public VCardUpdateExtension parse( XmlPullParser parser, int i ) throws XmlPullParserException, IOException, SmackException
        {
            final VCardUpdateExtension result = new VCardUpdateExtension();

            while ( true )
            {
                parser.next();
                String elementName = parser.getName();
                switch ( parser.getEventType() )
                {
                    case XmlPullParser.START_TAG:
                        if ( "photo".equals( elementName ) )
                        {
                            result.setPhotoHash( parser.nextText() );
                        }
                        break;

                    case XmlPullParser.END_TAG:
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
