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

import net.sf.fmj.media.RegistryDefaults;
import org.jitsi.impl.neomedia.device.MediaDeviceImpl;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.*;
import org.jitsi.service.neomedia.device.MediaDevice;
import org.jitsi.service.neomedia.format.MediaFormat;
import org.jitsi.service.neomedia.format.MediaFormatFactory;
import org.jitsi.util.event.VideoEvent;
import org.jitsi.util.event.VideoListener;
import org.jitsi.util.swing.VideoContainer;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.format.VideoFormat;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SendStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class VideoChannel {

    private MediaLocator locator;
    private String localIpAddress;
    private String ipAddress;
    private int localPort;
    private int localRTCPPort;
    private int remotePort;
    private int remoteRTCPPort;
    private Format format;
    private StreamConnector connector;
    private List<SendStream> sendStreams = new ArrayList<SendStream>();
    private List<ReceiveStreamListener> receiveListeners = new ArrayList<ReceiveStreamListener>();
    private MediaStream mediaStream = null;
    private JFrame frame = null;
    
    /**
     * Creates an Audio Channel for a desired jmf locator. For instance: new MediaLocator("dsound://")
     *
     * @param locator
     * @param ipAddress
     * @param localPort
     * @param remotePort
     * @param format
     */
    public VideoChannel(MediaLocator locator,
                        String localIpAddress,
                        String ipAddress,
                        int localPort,
                        int remotePort,
                        Format format) {

        this.locator = locator;
        this.localIpAddress = localIpAddress;
        this.ipAddress = ipAddress;
        this.localPort = localPort;
        this.localRTCPPort = (localPort + 1);
        this.remotePort = remotePort;
        this.remoteRTCPPort = (remotePort + 1);
        this.format = format;
        
    }
    
    public synchronized void checkVideo()
    {
        if (frame == null)
        {
            VideoMediaStream vms = ((VideoMediaStream) mediaStream);
            frame = new VideoFrame("Frame",vms);
            frame.setSize(640, 480);
            frame.setVisible(true);
        }
    }
    public class VideoFrame extends JFrame
    {
        private static final long serialVersionUID = -3359422087122668632L;

        private VideoMediaStream vms;

        public VideoFrame(String name,VideoMediaStream vms)
        {
            super(name);
            final VideoFrame videoframe = this;

            this.setSize(640, 480);
            this.setLayout(new BorderLayout());
            this.vms = vms;

            JPanel visualComponent = new JPanel( new BorderLayout() );

            for( Component c : vms.getVisualComponents() ) {
                VideoContainer vc = new VideoContainer(c,true);
                visualComponent.add(vc);
            }

            JPanel  controlComponent  = new JPanel( new FlowLayout() );
            JButton play = new JButton("Play");
            play.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    videoframe.pause();
                }

            });

            JButton pause = new JButton("Pause");
            pause.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    videoframe.pause();
                }

            });

            controlComponent.add(play);
            controlComponent.add(pause);

            this.add(visualComponent,BorderLayout.CENTER);
            this.add(controlComponent,BorderLayout.SOUTH);

            this.pack();
        }

        public void pause() {
            vms.setMute(true);
        }

        public void resume() {
            vms.setMute(false);

        }
    }

    /**
     * Starts the transmission. Returns null if transmission started ok.
     * Otherwise it returns a string with the reason why the setup failed.
     * Starts receive also.
     */
    public synchronized String start() {
    	try {
	    	MediaService mediaService = LibJitsi.getMediaService();

	        MediaDevice device = mediaService.getDefaultDevice(MediaType.VIDEO, MediaUseCase.ANY);
            List<MediaDevice> devices = mediaService.getDevices(MediaType.VIDEO, MediaUseCase.ANY);
            for (MediaDevice foundDevice : devices)
            {
                if (foundDevice instanceof MediaDeviceImpl)
                {
                    MediaDeviceImpl amdi = (MediaDeviceImpl) foundDevice;
                    if(locator.equals(amdi.getCaptureDeviceInfo().getLocator()))
                    {
                        System.out.println("Test" + locator + "-" + amdi.getCaptureDeviceInfo().getLocator());
                        device = foundDevice;
                    }
                }
            }
            System.out.println("Device:" + device);
            mediaStream = mediaService.createMediaStream(device);
	        mediaStream.setDirection(MediaDirection.SENDRECV);
	  
	        MediaFormat usedformat = mediaService.getFormatFactory().createMediaFormat(
	        		"H264",
	        		 MediaFormatFactory.CLOCK_RATE_NOT_SPECIFIED);
	        
	        byte dynamicRTPPayloadType = 99;
            mediaStream.addDynamicRTPPayloadType(
            		dynamicRTPPayloadType,
            		usedformat);
	        
	        mediaStream.setFormat(usedformat);
	  
	        connector = new DefaultStreamConnector(new DatagramSocket(this.localPort),new DatagramSocket(this.localRTCPPort));
	        mediaStream.setConnector(connector);
	        mediaStream.setTarget(
	                new MediaStreamTarget(
	                        new InetSocketAddress(this.ipAddress, this.remotePort),
	                        new InetSocketAddress(this.ipAddress, this.remoteRTCPPort)));
	        mediaStream.setName(MediaType.VIDEO.toString());
	        
	        ((VideoMediaStream) mediaStream).addVideoListener(new VideoListener(){

				@Override
				public void videoAdded(VideoEvent arg0) {
					checkVideo();
				}

				@Override
				public void videoRemoved(VideoEvent arg0) {
				}

				@Override
				public void videoUpdate(VideoEvent arg0) {
				}
	        	
	        });
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
        this.receiveListeners.add(listener);

    }

    /**
     * Removes Receive Listener.
     *
     * @param listener listener to remove
     */
    public void removeReceiverListener(ReceiveStreamListener listener) {
        this.receiveListeners.remove(listener);
    }

    /**
     * Removes All Receive Listeners.
     */
    private void remevoAllReceiverListener() {
        this.receiveListeners.clear();
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

        if (frame != null)
        {
            frame.dispose();
            frame = null;
        }
    	
    	remevoAllReceiverListener();
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
    		// FMJ
    		RegistryDefaults.registerAll(RegistryDefaults.FMJ | RegistryDefaults.FMJ_NATIVE );
    		//PlugInManager.addPlugIn(, in, out, type)

    		LibJitsi.start();
    		// Add Device
    		MediaType[] mediaTypes = MediaType.values();
    		MediaService mediaService = LibJitsi.getMediaService();    		
    		// LOG ALL Devices
    		final Vector<CaptureDeviceInfo> vectorDevices = CaptureDeviceManager.getDeviceList(null);
    		for ( CaptureDeviceInfo infoCaptureDevice : vectorDevices )
    		{
    			System.err.println("===========> " + infoCaptureDevice.getName());
    			for (Format format : infoCaptureDevice.getFormats())
    			{
    				System.err.println(format);
    			}		
    		}
        	
        	
            localhost = InetAddress.getLocalHost();

            VideoChannel videoChannel0 = new VideoChannel(new MediaLocator("civil:/dev/video0"), localhost.getHostAddress(), localhost.getHostAddress(), 7002, 7020, new VideoFormat(VideoFormat.JPEG_RTP));
            VideoChannel videoChannel1 = new VideoChannel(new MediaLocator("civil:/dev/video1"), localhost.getHostAddress(), localhost.getHostAddress(), 7020, 7002, new VideoFormat(VideoFormat.JPEG_RTP));

            videoChannel0.start();
            videoChannel1.start();

            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            videoChannel0.setTrasmit(false);
            videoChannel1.setTrasmit(false);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            videoChannel0.setTrasmit(true);
            videoChannel1.setTrasmit(true);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            videoChannel0.stop();
            videoChannel1.stop();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}