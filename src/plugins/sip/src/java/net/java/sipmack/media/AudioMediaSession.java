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

package net.java.sipmack.media;

import org.jitsi.service.neomedia.format.MediaFormat;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.media.rtp.ReceiveStreamListener;
import java.io.IOException;
import java.net.ServerSocket;


/**
 * This Class implements a complete JingleMediaSession.
 * It sould be used to transmit and receive audio captured from the Mic.
 * This Class should be automaticly controlled by JingleSession.
 * But you could also use in any VOIP application.
 * For better NAT Traversal support this implementation don't support only receive or only transmit.
 * To receive you MUST transmit. So the only implemented and functionally methods are startTransmit() and stopTransmit()
 *
 * @author Thiago Camargo
 */
public class AudioMediaSession {

    private AudioChannel audioChannel;
    private String locator = "javasound://";
    // AudioFormat of the Session
    private MediaFormat audioFormat;
    // Local Transport details
    private TransportCandidate local;
    // Remote Transport details
    private TransportCandidate remote;

    /**
     * Creates a AudioMediaSession with defined payload type, remote and local candidates
     *
     * @param audioFormat jmf AudioFormat
     * @param remote      The remote information. The candidate that the jmf will be sent to.
     * @param local       The local information. The candidate that will receive the jmf
     */
    public AudioMediaSession(final MediaFormat audioFormat, final TransportCandidate remote,
                             final TransportCandidate local) {
        this(audioFormat, remote, local, SettingsManager.getLocalPreferences().getAudioDevice());
    }

    /**
     * Creates a AudioMediaSession with defined payload type, remote and local candidates
     *
     * @param audioFormat Payload of the jmf
     * @param remote      The remote information. The candidate that the jmf will be sent to.
     * @param local       The local information. The candidate that will receive the jmf
     */
    public AudioMediaSession(final MediaFormat audioFormat, final TransportCandidate remote,
                             final TransportCandidate local, String locator) {
   
        this.local = local;
        this.remote = remote;
        this.audioFormat = audioFormat;
        if (locator != null && !locator.equals(""))
            this.locator = locator;
        
        initialize();
    }


    /**
     * Returns the AudioFormat of the Media Session
     *
     * @return
     */
    public MediaFormat getAudioFormat() {
        return audioFormat;
    }

    /**
     * Returns the Media Session local Candidate
     *
     * @return
     */
    public TransportCandidate getLocal() {
        return local;
    }

    /**
     * Returns the Media Session remote Candidate
     *
     * @return
     */
    public TransportCandidate getRemote() {
        return remote;
    }

    /**
     * Initialize the Audio Channel to make it able to send and receive audio
     */
    public void initialize() {

        String ip;
        String localIp;
        int localPort;
        int remotePort;

        if (this.getLocal().getSymmetric() != null) {
            ip = this.getLocal().getIp();
            localIp = this.getLocal().getLocalIp();
            localPort = getFreePort();
            remotePort = this.getLocal().getSymmetric().getPort();


        } else {
            ip = this.getRemote().getIp();
            localIp = this.getLocal().getLocalIp();
            localPort = this.getLocal().getPort();
            remotePort = this.getRemote().getPort();
        }
        System.out.println(this.getLocal().getConnection() + " " + ip + ": " + localPort + "->" + remotePort + "-" + audioFormat);

        System.out.println("1----> AudioChannel");
        audioChannel = new AudioChannel(PhoneManager.getMediaLocator(locator), localIp, ip, localPort,-1,  remotePort, -1, audioFormat);
    }

    /**
     * Add Receive Listeners. It monitors RTCP packets and signalling.
     *
     * @param listener listener to add
     */
    public void addReceiverListener(ReceiveStreamListener listener) {
        audioChannel.addReceiverListener(listener);
    }

    /**
     * Removes Receive Listener.
     *
     * @param listener listener to remove
     */
    public void removeReceiverListener(ReceiveStreamListener listener) {
        audioChannel.removeReceiverListener(listener);
    }

    /**
     * Starts transmission and for NAT Traversal reasons start receiving also.
     */
    public void startTrasmit() {
        audioChannel.start();
    }

    /**
     * Set transmit activity. If the active is true, the instance should trasmit.
     * If it is set to false, the instance should pause transmit.
     *
     * @param active
     */
    public void setTrasmit(boolean active) {
        audioChannel.setTrasmit(active);
    }

    /**
     * For NAT Reasons this method does nothing. Use startTransmit() to start transmit and receive jmf
     */
    public void startReceive() {
        // Do nothing
    }

    /**
     * Stops transmission and for NAT Traversal reasons stop receiving also.
     */
    public void stopTrasmit() {
        if (audioChannel != null)
            audioChannel.stop();
    }

    /**
     * For NAT Reasons this method does nothing. Use startTransmit() to start transmit and receive jmf
     */
    public void stopReceive() {
        // Do nothing
    }

    /**
     * Closes and finalizes the session.
     * Very important to release static MediaLocator.
     */
    public void close(){

        stopTrasmit();
        stopReceive();
        PhoneManager.setUsingMediaLocator(false);

    }

    /**
     * Obtain a free port we can use.
     *
     * @return A free port number.
     */
    protected int getFreePort() {
        ServerSocket ss;
        int freePort = 0;

        for (int i = 0; i < 10; i++) {
            freePort = (int) (10000 + Math.round(Math.random() * 10000));
            freePort = freePort % 2 == 0 ? freePort : freePort + 1;
            try {
                ss = new ServerSocket(freePort);
                freePort = ss.getLocalPort();
                ss.close();
                return freePort;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ss = new ServerSocket(0);
            freePort = ss.getLocalPort();
            ss.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return freePort;
    }
}
