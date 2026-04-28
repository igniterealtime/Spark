/**
 * Copyright (C) 2026 Ignite Realtime. All rights reserved.
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

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jxmpp.jid.DomainBareJid;

import javax.swing.Icon;

import static org.apache.commons.lang3.StringUtils.substringBefore;

public class GatewayDescription extends Transport {
    public GatewayDescription(DomainBareJid xmppServiceDomain, String xmppServiceName) {
        super(xmppServiceDomain, xmppServiceName);
    }

    @Override
    public String getTitle() {
        return Res.getString("message.register.transports");
    }

    @Override
    public String getInstructions() {
        return Res.getString("message.enter.gateway");
    }

    @Override
    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.Icon.TRANSPORT_ICON);
    }

    @Override
    public Icon getInactiveIcon() {
        return SparkRes.getImageIcon(SparkRes.Icon.TRANSPORT_ICON);
    }

    @Override
    public String getName() {
        return substringBefore(getXMPPServiceDomain().toString(), ".");
    }

    @Override
    public boolean requiresUsername() {
        return false;
    }

    @Override
    public boolean requiresPassword() {
        return false;
    }

    @Override
    public boolean requiresNickname() {
        return false;
    }
}
