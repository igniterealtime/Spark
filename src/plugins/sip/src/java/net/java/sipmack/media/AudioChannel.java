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

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.format.AudioFormat;
import javax.media.rtp.ReceiveStreamListener;

import org.jitsi.impl.neomedia.MediaUtils;
import org.jitsi.impl.neomedia.device.AudioMediaDeviceImpl;
import org.jitsi.impl.neomedia.device.MediaDeviceImpl;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.DefaultStreamConnector;
import org.jitsi.service.neomedia.MediaDirection;
import org.jitsi.service.neomedia.MediaService;
import org.jitsi.service.neomedia.MediaStream;
import org.jitsi.service.neomedia.MediaStreamTarget;
import org.jitsi.service.neomedia.MediaType;
import org.jitsi.service.neomedia.MediaUseCase;
import org.jitsi.service.neomedia.StreamConnector;
import org.jitsi.service.neomedia.device.MediaDevice;
import org.jitsi.service.neomedia.format.MediaFormat;


public class AudioChannel {

    private MediaLocator inLocator;
    private String localIpAddress;
    private String ipAddress;
    private int localPort;
    private int localRTCPPort;
    private int remotePort;
    private int remoteRTCPPort;
    private MediaFormat format;
    private StreamConnector connector;
    private boolean started = false;
    MediaStream mediaStream = null;

    /**
     * Creates an Audio Channel for a desired jmf locator. For instance: new MediaLocator("dsound://")
     *
     * @param locator
     * @param ipAddress
     * @param localPort
     * @param remotePort
     * @param format
     */
    public AudioChannel(MediaLocator inLocator,
                        String localIpAddress,
                        String ipAddress,
                        int localPort,
                        int localRTCPPort,
                        int remotePort,
                        int remoteRTCPPort,
                        MediaFormat format) {

        this.inLocator = inLocator;
        this.localIpAddress = localIpAddress;
        this.ipAddress = ipAddress;
        this.localPort = localPort;
        this.remotePort = remotePort;
        this.format = format;
 
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

	    	MediaDevice device = null;
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
	        
	        mediaStream.setDirection(MediaDirection.SENDRECV);
	        
	        
	        mediaStream.setFormat(format);
	        connector = new DefaultStreamConnector(new DatagramSocket(this.localPort),new DatagramSocket(this.localRTCPPort));
	        mediaStream.setConnector(connector);
	        mediaStream.setTarget(
	                new MediaStreamTarget(
	                        new InetSocketAddress(this.ipAddress, this.remotePort),
	                        new InetSocketAddress(this.ipAddress, this.remoteRTCPPort)));
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
     * Add Receive Listeners. It monitors RTCP packets and signalling.
     *
     * @param listener listener to add
     */
    public void addReceiverListener(ReceiveStreamListener listener) {

    }

    /**
     * Removes Receive Listener.
     *
     * @param listener listener to remove
     */
    public void removeReceiverListener(ReceiveStreamListener listener) {

    }

    /**
     * Stops the transmission if already started.
     * Stops the receiver also.
     */
    public void stop() {
    	if (connector != null) {
    		connector.stopped();
    		connector.close();
    		connector = null;
    	}
    	if (mediaStream != null) {
	    	mediaStream.stop();
	    	mediaStream.close();
	    	mediaStream = null;	    	
    	}
    }


    /**
     * Set transmit activity. If the active is true, the instance should trasmit.
     * If it is set to false, the instance should pause transmit.
     *
     * @param active
     */
    public void setTrasmit(boolean active) {
    	mediaStream.setMute(active);
    }


    public static void main(String args[]) {

        InetAddress localhost;
        try {

        	LibJitsi.start();
    		MediaType[] mediaTypes = MediaType.values();
    		MediaService mediaService = LibJitsi.getMediaService();
        	
    		Vector<CaptureDeviceInfo> vectorAudioDevices = CaptureDeviceManager.getDeviceList(new AudioFormat(AudioFormat.LINEAR));
    		for ( CaptureDeviceInfo infoCaptureDevice : vectorAudioDevices)
    		{			     
    			System.out.println(infoCaptureDevice.getLocator());			

    		}
    		
            localhost = InetAddress.getLocalHost();

            byte format = 3;

            
            AudioChannel audioChannel0 = new AudioChannel(vectorAudioDevices.get(0).getLocator(), localhost.getHostAddress(), localhost.getHostAddress(), 7002, -1, 7020, -1, MediaUtils.getMediaFormat(new AudioFormat(AudioFormat.GSM_RTP)));
            AudioChannel audioChannel1 = new AudioChannel(vectorAudioDevices.get(0).getLocator(), localhost.getHostAddress(), localhost.getHostAddress(), 7020, -1, 7002, -1, MediaUtils.getMediaFormat(new AudioFormat(AudioFormat.GSM_RTP)));

            audioChannel0.start();
            audioChannel1.start();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            audioChannel0.setTrasmit(false);
            audioChannel1.setTrasmit(false);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            audioChannel0.setTrasmit(true);
            audioChannel1.setTrasmit(true);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            audioChannel0.stop();
            audioChannel1.stop();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}