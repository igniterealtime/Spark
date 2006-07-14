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

public class VCardUpdateExtension implements PacketExtension {
    private String photoHash;

    public void setPhotoHash(String hash) {
        photoHash = hash;
    }

    public String getElementName() {
        return "x";
    }

    public String getNamespace() {
        return "vcard-temp:x:update";
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        buf.append("<photo>");
        buf.append(photoHash);
        buf.append("</photo>");
        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }
}
