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

import java.net.InetAddress;

import javax.media.control.BufferControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SessionAddress;

import net.java.sipmack.sip.SIPConfig;

/**
 * An Easy to use Audio Channel implemented using JMF.
 * It sends and receives jmf for and from desired IPs and ports.
 * Also has a rport Symetric behavior for better NAT Traversal.
 * It send data from a defined port and receive data in the same port, making NAT binds easier.
 * <p/>
 * Send from portA to portB and receive from portB in portA.
 * <p/>
 * Sending
 * portA ---> portB
 * <p/>
 * Receiving
 * portB ---> portA
 * <p/>
 * <i>Transmit and Receive are interdependents. To receive you MUST trasmit. </i>
 *
 * @author Thiago Camargo
 */
public class AudioReceiverChannel {

    private String localIpAddress;
    private int localPort;
    private String remoteIpAddress;
    private int remotePort;

    private RTPManager rtpMgrs[];
    private AudioReceiver audioReceiver;

    private boolean started = false;

    /**
     * Creates an Audio Receiver Channel for a desired jmf locator.
     *
     * @param localIpAddress
     * @param localPort
     */
    public AudioReceiverChannel(String localIpAddress,
                                int localPort,
                                String remoteIpAddress,
                                int remotePort) {
        this.localIpAddress = localIpAddress;
        this.localPort = localPort;
        this.remoteIpAddress = remoteIpAddress;
        this.remotePort = remotePort;
    }

    /**
     * Starts the transmission. Returns null if transmission started ok.
     * Otherwise it returns a string with the reason why the setup failed.
     * Starts receive also.
     */
    public synchronized String start() {
        if (started) return null;
        started = true;
        String result;

        // Create an RTP session to transmit the output of the
        // processor to the specified IP address and port no.
        result = createReceiver();
        if (result != null) {
            started = false;
            return result;
        }

        return null;
    }

    /**
     * Stops the transmission if already started.
     * Stops the receiver also.
     */
    public void stop() {
        if (!started) return;
        synchronized (this) {
            try {
                started = false;
                for (int i = 0; i < rtpMgrs.length; i++) {
                    rtpMgrs[i].removeReceiveStreamListener(audioReceiver);
                    rtpMgrs[i].removeSessionListener(audioReceiver);
                    rtpMgrs[i].removeTargets("Session ended.");
                    rtpMgrs[i].dispose();
                    rtpMgrs[i] = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.err.println("RTP Transmission Stopped.");
    }

    /**
     * Use the RTPManager API to create sessions for each jmf
     * track of the processor.
     */
    private String createReceiver() {

	rtpMgrs = new RTPManager[1];
	SessionAddress localAddr, destAddr;
	audioReceiver = new AudioReceiver(this);

	try {
	    rtpMgrs[0] = RTPManager.newInstance();

	    localAddr = new SessionAddress(
		    InetAddress.getByName(this.localIpAddress), localPort);

	    destAddr = new SessionAddress(
		    InetAddress.getByName(this.remoteIpAddress), remotePort);

	    rtpMgrs[0].addReceiveStreamListener(audioReceiver);
	    rtpMgrs[0].addSessionListener(audioReceiver);

	    BufferControl bc = (BufferControl) rtpMgrs[0]
		    .getControl("javax.media.control.BufferControl");
	    if (bc != null) {
		int bl = 160;
		bl = SIPConfig.getDefaultBufferLength() != -1 ? SIPConfig
			.getDefaultBufferLength() : bl;

		bc.setBufferLength(bl);
	    }

	    rtpMgrs[0].initialize(localAddr);

	    rtpMgrs[0].addTarget(destAddr);

	    System.err.println("Created RTP session at " + localPort);

	} catch (Exception e) {
	    e.printStackTrace();
	    return e.getMessage();
	}

	return null;
    }

}
