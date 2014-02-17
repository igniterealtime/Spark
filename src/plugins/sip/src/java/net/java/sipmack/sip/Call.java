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

package net.java.sipmack.sip;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.Dialog;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;

import net.java.sipmack.common.Log;
import net.java.sipmack.media.AudioMediaSession;
import net.java.sipmack.media.AudioReceiverChannel;
import net.java.sipmack.sip.event.CallListener;
import net.java.sipmack.sip.event.CallStateEvent;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Camargo (http://www.jivesoftware.com)
 * @version 1.2
 */
public class Call implements ReceiveStreamListener {

    public static final String DIALING = "Discando";

    public static final String RINGING = "Tocando";

    public static final String ALERTING = "Chamando";

    public static final String CONNECTED = "Conectado";

    public static final String DISCONNECTED = "Desconectado";

    public static final String BUSY = "Ocupado";

    public static final String FAILED = "Falha";

    public static final String MOVING_LOCALLY = "Moving Locally";

    public static final String MOVING_REMOTELY = "Moving Remotely";

    public static final String RECONNECTED = "reConnected";

    private long start = 0;

    private Dialog dialog = null;

    private SessionDescription localSdpDescription = null;

    private SessionDescription remoteSdpDescription = null;

    private boolean holdCam = false;

    private boolean holdMic = false;

    private AudioMediaSession audioMediaSession = null;

    private AudioReceiverChannel audioReceiverChannel = null;

    /**
     * While in its early state the dialog cannot provide us with its
     * corresponding transaction as it is not yet created That's where the
     * initialRequest field comes in.
     */
    private Request initialRequest = null;

    /**
     */
    private Request lastRequest = null;

    private String callState = "";

    // Event Management
    List<CallListener> listeners = new CopyOnWriteArrayList<CallListener>();
    
    public SessionDescription getLocalSdpDescription() {
        return localSdpDescription;
    }

    public void setLocalSdpDescription(SessionDescription localSdpDescription) {
        this.localSdpDescription = localSdpDescription;
    }

    public AudioReceiverChannel getAudioReceiverChannel() {
        return audioReceiverChannel;
    }

    public void setAudioReceiverChannel(AudioReceiverChannel audioReceiverChannel) {
        this.audioReceiverChannel = audioReceiverChannel;
    }

    public String getState() {
        return callState;
    }

    public int getElapsedTime() {
        if (start == 0) return 0;
        return Math.round((System.currentTimeMillis() - start) / 1000);
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public boolean isIncoming() {
        // Let it throw a null pointer exception if necessary
        return dialog.isServer();
    }

    /**
     * @param dialog The dialog to set.
     */
    void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    /**
     * @return Returns the dialog.
     */
    Dialog getDialog() {
        return dialog;
    }

    // SDP Data

    /**
     * Set remote SDP Description of this call
     *
     * @param data the remoteSdpDescription to set.
     */
    void setRemoteSdpDescription(SessionDescription data) {
        this.remoteSdpDescription = data;
    }

    /**
     * Set remote SDP Description of this call
     *
     * @param data the remoteSdpDescription to set.
     */
    void setRemoteSdpDescription(String data) {
        if (data == null || data.equals("")) return;
        try {
            this.remoteSdpDescription = SdpFactory.getInstance().createSessionDescription(data);
        }
        catch (SdpParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get remote SDP Description of this call
     *
     * @return Returns the remoteSdpDescription.
     */
    public SessionDescription getRemoteSdpDescription() {
        return remoteSdpDescription;
    }

    public void setState(String newStatus) {
        if (newStatus.equals(getState()))
            return;

        String oldStatus = callState;
        this.callState = newStatus;
        fireCallStatusChangedEvent(oldStatus);

    }

    public String getAddress() {
        if (dialog.getState() != null) {
            return dialog.getRemoteParty().getURI().toString();
        }
        else {
            if (dialog.isServer()) {
                FromHeader fromHeader = (FromHeader)initialRequest
                        .getHeader(FromHeader.NAME);
                return fromHeader.getAddress().getURI().toString();
            }
            else {
                ToHeader toHeader = (ToHeader)initialRequest
                        .getHeader(ToHeader.NAME);
                return toHeader.getAddress().getURI().toString();
            }
        }
    }

    public String getNumber() {
        return getAddress().split(":")[1].split("@")[0];
    }

    public String getRemoteName() {
        Address address;
        if (dialog.getState() != null) {
            address = dialog.getRemoteParty();
        }
        else {
            if (dialog.isServer()) {
                FromHeader fromHeader = (FromHeader)initialRequest
                        .getHeader(FromHeader.NAME);
                address = fromHeader.getAddress();
            }
            else {
                ToHeader toHeader = (ToHeader)initialRequest
                        .getHeader(ToHeader.NAME);
                address = toHeader.getAddress();
            }
        }
        String retVal = null;
        if (address.getDisplayName() != null
                && address.getDisplayName().trim().length() > 0) {
            retVal = address.getDisplayName();
        }
        else {
            URI uri = address.getURI();
            if (uri.isSipURI()) {
                retVal = ((SipURI)uri).getUser();
            }
        }
        return retVal == null ? "" : retVal;
    }

    public int getID() {
        return hashCode();
    }

    /**
     * Set Initial request of this call
     *
     * @param initialRequest The initialRequest to set.
     */
    void setInitialRequest(Request initialRequest) {
        this.initialRequest = initialRequest;
        this.lastRequest = initialRequest;
    }

    /**
     * Get Initial request of this call
     *
     * @return Returns the initialRequest.
     */
    public Request getInitialRequest() {
        return this.initialRequest;
    }

    /**
     * Set the last request of this call
     *
     * @param lastRequest The lastRequest to set.
     */
    void setLastRequest(Request lastRequest) {
        this.lastRequest = lastRequest;
    }

    /**
     * Get last request of this call
     *
     * @return Returns the lastRequest.
     */
    public Request getLastRequest() {
        return this.lastRequest;
    }

    String getDialogID() {
        return dialog.getDialogId();
    }

    public String toString() {
        return "[ Call " + getID() + "\nde " + getRemoteName() + "@"
                + getAddress() + "\nSDP:" + getRemoteSdpDescription() + "]";
    }

    public boolean onHoldMic() {
        return holdMic;
    }

    public boolean onHoldCam() {
        return holdCam;
    }

    public void onHoldMic(boolean h) {
        holdMic = h;
    }

    public void onHoldCam(boolean h) {
        holdCam = h;
    }

    // ====================== EVENTS ===========================
    public void addStateChangeListener(CallListener listener) {
        Log.debug(listener.getClass().getCanonicalName());
        listeners.add(listener);
    }

    public void fireCallStatusChangedEvent(String oldStatus) {
        Log.debug(this.getState());
        CallStateEvent evt = new CallStateEvent(this);
        evt.setOldState(oldStatus);
        for (CallListener callListener : listeners) {
            callListener.callStateChanged(evt);
        }
    }

    public AudioMediaSession getAudioMediaSession() {
        return audioMediaSession;
    }

    public void setAudioMediaSession(AudioMediaSession audioMediaSession) {
        this.audioMediaSession = audioMediaSession;
    }

    // ====================== RECEIVE STREAMS EVENTS ==========================

    public void update(ReceiveStreamEvent receiveStreamEvent) {

    }
}