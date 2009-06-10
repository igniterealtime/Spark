/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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

        for (int i = 0; i < 3; i++) {
            try {
                rtpMgrs[i] = RTPManager.newInstance();

                localAddr = new SessionAddress(InetAddress.getByName(this.localIpAddress),
                        localPort);

                destAddr = new SessionAddress(InetAddress.getByName(this.remoteIpAddress),
                        remotePort);

                rtpMgrs[i].addReceiveStreamListener(audioReceiver);
                rtpMgrs[i].addSessionListener(audioReceiver);

                BufferControl bc = (BufferControl) rtpMgrs[i].getControl("javax.media.control.BufferControl");
                if (bc != null) {
                    int bl = 160;
                    bl = SIPConfig.getDefaultBufferLength() != -1 ? SIPConfig.getDefaultBufferLength()
                            : bl;

                    bc.setBufferLength(bl);
                }

                rtpMgrs[i].initialize(localAddr);

                rtpMgrs[i].addTarget(destAddr);

                System.err.println("Created RTP session at " + localPort);
                break;

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        return null;
    }

}
