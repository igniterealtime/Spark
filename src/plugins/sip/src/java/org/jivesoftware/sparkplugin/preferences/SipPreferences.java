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
package org.jivesoftware.sparkplugin.preferences;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>SipPreferences</code> class represents the SIP Preferrences instance
 * @version 1.0, 28/09/2006
 */

public class SipPreferences {

    private String userName;
    private String authUserName;
    private String server;
    private String password;
    private String preferredAddress;
    private String stunServer;
    private String stunPort;
    private String voicemail;
    private String outboundproxy;
    private boolean promptCredentials;

    private boolean registerAtStart;
    private boolean useStun;

    private String audioDevice;
    private float outputVolume;
    private float inputVolume;

    public SipPreferences() {
        userName = "";
        authUserName = "";
        server = "";
        password = "";
        preferredAddress = "";
        stunServer = "";
        stunPort = "";
        outboundproxy="";

        registerAtStart = false;
        useStun = false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthUserName() {
        return authUserName;
    }

    public void setAuthUserName(String authUserName) {
        this.authUserName = authUserName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVoicemail() {
        return voicemail;
    }

    public void setVoicemail(String voicemail) {
        this.voicemail = voicemail;
    }

    public String getPreferredAddress() {
        return preferredAddress;
    }

    public void setPreferredAddress(String preferredAddress) {
        this.preferredAddress = preferredAddress;
    }

    public boolean isRegisterAtStart() {
        return registerAtStart;
    }

    public void setRegisterAtStart(boolean registerAtStart) {
        this.registerAtStart = registerAtStart;
    }

    public boolean isUseStun() {
        return useStun;
    }

    public void setUseStun(boolean useStun) {
        this.useStun = useStun;
    }

    public String getStunServer() {
        return stunServer;
    }

    public void setStunServer(String stunServer) {
        this.stunServer = stunServer;
    }

    public String getStunPort() {
        return stunPort;
    }

    public void setStunPort(String stunPort) {
        this.stunPort = stunPort;
    }


    public String getAudioDevice() {
        return audioDevice;
    }

    public void setAudioDevice(String audioDevice) {
        this.audioDevice = audioDevice;
    }

    public float getOutputVolume() {
        return outputVolume;
    }

    public void setOutputVolume(float outputVolume) {
        this.outputVolume = outputVolume;
    }

    public float getInputVolume() {
        return inputVolume;
    }

    public void setInputVolume(float inputVolume) {
        this.inputVolume = inputVolume;
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
