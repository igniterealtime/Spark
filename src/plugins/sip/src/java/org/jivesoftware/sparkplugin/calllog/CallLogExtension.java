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

package org.jivesoftware.sparkplugin.calllog;

import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.sparkplugin.sipaccount.SipAccountPacket;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>CallLogExtension</code> class is the CallLog PacketExtension
 * @version 1.0, 28/09/2006
 */

public class CallLogExtension extends DefaultExtensionElement {

    final static String ELEMENT_NAME = "callLog";

    final static String NAMESPACE = SipAccountPacket.NAMESPACE + "/log";

    public CallLogExtension() {
        super(ELEMENT_NAME, NAMESPACE);
    }

}
