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

import org.jivesoftware.smack.packet.PacketExtension;


public class JabberAvatarExtension implements PacketExtension {
    private String photoHash;

    public void setPhotoHash(String hash) {
        photoHash = hash;
    }

    public String getElementName() {
        return "x";
    }

    public String getNamespace() {
        return "jabber:x:avatar";
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        buf.append("<hash>");
        buf.append(photoHash);
        buf.append("</hash>");
        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }
}