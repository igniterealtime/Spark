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
package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;

import javax.swing.Icon;

/**
 *
 */
public class IRCTransport implements Transport {

    private String serviceName;

    public IRCTransport(String serviceName){
        this.serviceName = serviceName;
    }

    public String getTitle(){
        return Res.getString("title.irc.registration");
    }

    public String getInstructions() {
        return Res.getString("message.enter.irc");
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.IRC_TRANSPORT_ACTIVE_IMAGE);
    }

    public Icon getInactiveIcon() {
        return SparkRes.getImageIcon(SparkRes.IRC_TRANSPORT_INACTIVE_IMAGE);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getName(){
        return "IRC";
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Boolean requiresUsername() {
        return true;
    }

    public Boolean requiresPassword() {
        return false;
    }

    public Boolean requiresNickname() {
        return true;
    }

}
