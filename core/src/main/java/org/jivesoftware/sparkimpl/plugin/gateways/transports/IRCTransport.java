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

import org.jivesoftware.resource.SparkRes;
import org.jxmpp.jid.DomainBareJid;
import org.jivesoftware.resource.Res;

import javax.swing.Icon;

/**
 */
public class IRCTransport implements Transport {

    private DomainBareJid serviceName;

    public IRCTransport(DomainBareJid serviceName){
        this.serviceName = serviceName;
    }

    @Override
	public String getTitle(){
        return Res.getString("title.irc.registration");
    }

    @Override
	public String getInstructions() {
        return Res.getString("message.enter.irc");
    }

    @Override
	public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.IRC_TRANSPORT_ACTIVE_IMAGE);
    }

    @Override
	public Icon getInactiveIcon() {
        return SparkRes.getImageIcon(SparkRes.IRC_TRANSPORT_INACTIVE_IMAGE);
    }

    @Override
    public DomainBareJid getXMPPServiceDomain() {
        return serviceName;
    }

    @Override
	public String getName(){
        return "IRC";
    }

    public void setServiceName(DomainBareJid serviceName) {
        this.serviceName = serviceName;
    }

    @Override
	public Boolean requiresUsername() {
        return true;
    }

    @Override
	public Boolean requiresPassword() {
        return false;
    }

    @Override
	public Boolean requiresNickname() {
        return true;
    }

}
