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

import org.jitsi.impl.neomedia.device.AudioMediaDeviceImpl;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.*;
import org.jitsi.service.neomedia.device.MediaDevice;
import org.jitsi.service.neomedia.format.MediaFormat;

import javax.media.MediaLocator;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;

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

    private MediaLocator inLocator;
    private MediaStream mediaStream;

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
                                int remotePort,
                                int remoteRTCPPort) {
        this.inLocator = inLocator;
        this.localIpAddress = localIpAddress;
        this.localPort = localPort;
        this.remoteIpAddress = remoteIpAddress;
        this.remotePort = remotePort;
        System.out.println("AudioReceiverChannel");
    }

    /**
     * Starts the transmission. Returns null if transmission started ok.
     * Otherwise it returns a string with the reason why the setup failed.
     * Starts receive also.
     */
    public synchronized String start() {
    	try
    	{
            MediaService mediaService = LibJitsi.getMediaService();

            MediaDevice device = mediaService.getDefaultDevice(MediaType.AUDIO, MediaUseCase.CALL);
            List<MediaDevice> devices = mediaService.getDevices(MediaType.AUDIO, MediaUseCase.CALL);
            for (MediaDevice foundDevice : devices)
            {
                if (foundDevice instanceof AudioMediaDeviceImpl)
                {
                    AudioMediaDeviceImpl amdi = (AudioMediaDeviceImpl) foundDevice;
                    if(inLocator == amdi.getCaptureDeviceInfo().getLocator())
                    {
                        System.out.println("Test" + inLocator + "-" + amdi.getCaptureDeviceInfo().getLocator());
                        device = foundDevice;
                    }
                }
                System.out.println(foundDevice.getClass() + "-" +  inLocator);
            }

            mediaStream = mediaService.createMediaStream(device);
            mediaStream.setDirection(MediaDirection.RECVONLY);
            System.out.println("TEST");
            MediaFormat format = mediaService.getFormatFactory().createMediaFormat(
                    "PCMA",
                    8000);
            mediaStream.setFormat(format);
            StreamConnector connector = new DefaultStreamConnector(new DatagramSocket(this.localPort),new DatagramSocket(this.localPort + 1));
            mediaStream.setConnector(connector);
            mediaStream.setTarget(
                    new MediaStreamTarget(
                            new InetSocketAddress(this.remoteIpAddress, this.remotePort),
                            new InetSocketAddress(this.remoteIpAddress, this.remotePort+1)));
            mediaStream.setName(MediaType.AUDIO.toString());
            mediaStream.start();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }

    /**
     * Stops the transmission if already started.
     * Stops the receiver also.
     */
    public void stop() {
        mediaStream.stop();
    }

 
}
