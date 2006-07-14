/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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