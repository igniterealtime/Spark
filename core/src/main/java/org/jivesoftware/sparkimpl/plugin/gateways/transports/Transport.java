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
package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import javax.swing.Icon;

import org.jxmpp.jid.DomainBareJid;

/**
 * XMPP Gateway description (name, icon)
 */
public abstract class Transport {
    private final DomainBareJid xmppServiceDomain;
    private final String xmppServiceName;

    public Transport(DomainBareJid xmppServiceDomain, String xmppServiceName){
        this.xmppServiceDomain = xmppServiceDomain;
        this.xmppServiceName = xmppServiceName;
    }

    public abstract String getTitle();

    public abstract String getInstructions();

    public abstract Icon getIcon();

    public abstract Icon getInactiveIcon();

    public final DomainBareJid getXMPPServiceDomain() {
        return xmppServiceDomain;
    }

    public final String getXMPPServiceName() {
        return xmppServiceName;
    }

    public abstract String getName();

    public abstract boolean requiresUsername();

    public abstract boolean requiresPassword();

    public abstract boolean requiresNickname();

}
