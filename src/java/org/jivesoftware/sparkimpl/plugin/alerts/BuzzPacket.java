/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 *
 */
public class BuzzPacket implements PacketExtension {
    public String getElementName() {
        return "buzz";
    }

    public String getNamespace() {
        return "http://www.jivesoftware.com/spark";
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\"/>");
        return buf.toString();
    }

}
