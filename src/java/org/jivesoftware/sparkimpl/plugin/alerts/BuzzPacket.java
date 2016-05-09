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
package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * XEP-0224 Compliance<br>
 * see <a
 * href="http://xmpp.org/extensions/xep-0224.html">http://xmpp.org/extensions/xep-0224.html</a>
 */
public class BuzzPacket implements PacketExtension {
    public String getElementName() {
	return "attention";
    }

    public String getNamespace() {
	return "urn:xmpp:attention:0";
    }

    // TODO 2.7.0 remove buzz only attention gets to stay
    public String toXML() {
	return "<" + getElementName() + " xmlns=\"" + getNamespace()
		+ "\"/><buzz xmlns=\"http://www.jivesoftware.com/spark\"/>";
    }

}
