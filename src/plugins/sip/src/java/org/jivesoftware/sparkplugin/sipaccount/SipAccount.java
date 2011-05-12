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
package org.jivesoftware.sparkplugin.sipaccount;

import net.java.sipmack.sip.SipRegisterStatus;

public class SipAccount {

    private String username = null;

    private String sipUsername = "";

    private String authUsername = "";

    private String displayName = "";

    private String password = "";

    private String server = "";

    private String stunServer = "";

    private String stunPort = "";

    private boolean useStun = false;

    private String voiceMailNumber = "";

    private boolean enabled = false;

    private String outboundproxy = "";

    private boolean promptCredentials = false;

    private SipRegisterStatus status = SipRegisterStatus.Unregistered;

    public SipAccount() {

    }

    public SipAccount(String username) {
        this.username = username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getAuthUsername() {
        return authUsername == null ? "" : authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServer() {
        return server == null ? "" : server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getVoiceMailNumber() {
        return voiceMailNumber == null ? "" : voiceMailNumber;
    }

    public void setVoiceMailNumber(String voiceMailNumber) {
        this.voiceMailNumber = voiceMailNumber;
    }

    public String getSipUsername() {
        return sipUsername == null ? "" : sipUsername;
    }

    public void setSipUsername(String sipUsername) {
        this.sipUsername = sipUsername;
    }

    public String getUsername() {
        return username == null ? "" : username;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStunPort() {
        return stunPort == null ? "" : stunPort;
    }

    public void setStunPort(String stunPort) {
        this.stunPort = stunPort;
    }

    public String getStunServer() {
        return stunServer == null ? "" : stunServer;
    }

    public void setStunServer(String stunServer) {
        this.stunServer = stunServer;
    }

    public boolean isUseStun() {
        return useStun;
    }

    public void setUseStun(boolean useStun) {
        this.useStun = useStun;
    }

    public SipRegisterStatus getStatus() {
        return status == null ? SipRegisterStatus.Unregistered : status;
    }

    public void setStatus(SipRegisterStatus status) {
        this.status = status;
    }

    public String getOutboundproxy() {
        return outboundproxy;
    }

    public void setOutboundproxy(String outboundproxy) {
        this.outboundproxy = outboundproxy;
    }

    public boolean isPromptCredentials() {
        return promptCredentials;
    }

    public void setPromptCredentials(boolean promptCredentials) {
        this.promptCredentials = promptCredentials;
    }
}
