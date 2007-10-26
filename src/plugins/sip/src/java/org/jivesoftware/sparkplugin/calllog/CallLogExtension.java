/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.calllog;

import org.jivesoftware.sparkplugin.sipaccount.SipAccountPacket;
import org.jivesoftware.smack.packet.DefaultPacketExtension;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>CallLogExtension</code> class is the CallLog PacketExtension
 * @version 1.0, 28/09/2006
 */

public class CallLogExtension extends DefaultPacketExtension {

    final static String ELEMENT_NAME = "callLog";

    final static String NAMESPACE = SipAccountPacket.NAMESPACE + "/log";

    public CallLogExtension() {
        super(ELEMENT_NAME, NAMESPACE);
    }

}
