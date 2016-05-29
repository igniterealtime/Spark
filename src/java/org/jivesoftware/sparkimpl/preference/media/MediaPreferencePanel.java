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
package org.jivesoftware.sparkimpl.preference.media;

import org.jitsi.impl.neomedia.MediaServiceImpl;
import org.jitsi.impl.neomedia.codec.video.AVFrameFormat;
import org.jitsi.impl.neomedia.device.AudioSystem;
import org.jitsi.impl.neomedia.device.AudioSystem.DataFlow;
import org.jitsi.impl.neomedia.device.CaptureDeviceInfo2;
import org.jitsi.service.configuration.ConfigurationService;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.MediaService;
import org.jitsi.service.neomedia.MediaType;
import org.jitsi.service.neomedia.MediaUseCase;
import org.jitsi.service.neomedia.device.MediaDevice;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Vector;

public class MediaPreferencePanel  extends JPanel {
	private static final long serialVersionUID = 8297469864676223072L;
	private Vector<CaptureDeviceInfo2> vectorAudioDevices;
	private Vector<CaptureDeviceInfo> vectorVideoDevices;
	private Vector<AudioSystem> vectorAudioSystem  = new Vector<>();
	private Vector<CaptureDeviceInfo2> vectorPlaybackDevices = new Vector<>();

	private JComboBox audioDevice = new JComboBox();
	private JComboBox audioSystem = new JComboBox();
	private JComboBox playbackDevice = new JComboBox();
	private JComboBox videoDevice = new JComboBox();
	private JTextField _stunServerInput = new JTextField();
    private JTextField _stunPortInput = new JTextField();
    
    public MediaPreferencePanel() {
		setLayout(new VerticalFlowLayout());
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(Res.getString("title.general.media")));
		add(panel);
			
		panel.setLayout(new GridBagLayout());

		JLabel lAudioSystem = new JLabel(); // Res.getString("label.audio.device"));
		panel.add( lAudioSystem, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
		panel.add(audioSystem, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));

		
		JLabel lPlaybackAudio = new JLabel(); // Res.getString("label.audio.device"));
		panel.add( lPlaybackAudio, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
		panel.add(playbackDevice, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));

		
		JLabel lAudio = new JLabel(); // Res.getString("label.audio.device"));
		panel.add( lAudio, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
		panel.add(audioDevice, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
		
		
		JLabel lVideo = new JLabel(); // Res.getString("label.video.device"));
		panel.add( lVideo, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
        panel.add(videoDevice, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
	
        JButton redetect = new JButton(); // Res.getString("button.re.detect") );
        redetect.addActionListener( event -> scanDevices() );
        
        panel.add(redetect,new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
    
        // Setup Mnemonics
        ResourceUtils.resButton(redetect, Res.getString("button.re.detect"));
        ResourceUtils.resLabel(lVideo, videoDevice, Res.getString("label.video.device") + ":");
        ResourceUtils.resLabel(lPlaybackAudio, playbackDevice, Res.getString("label.audio.device") + ":");
        ResourceUtils.resLabel(lAudio, audioDevice, Res.getString("label.audio.device") + ":");
        
        scanDevices();
        
        createSTUNPanel();
	}
	
	private String convertSysString(String src)
	{
		String res = src;  
		try {
			res = new String(src.getBytes("ISO-8859-1"),Charset.defaultCharset());
		} catch (UnsupportedEncodingException e) {
			Log.error("convertSysString" , e);
		}
		return res;
    }
	
	
	
	@SuppressWarnings("unchecked")
	public void scanDevices()
	{
		// Remove all Items
		audioDevice.removeAllItems();
		videoDevice.removeAllItems();
		playbackDevice.removeAllItems();
		audioSystem.removeAllItems();
		
		vectorPlaybackDevices.removeAllElements();
		vectorAudioSystem.removeAllElements();
		// FMJ
		System.setProperty(ConfigurationService.PNAME_SC_HOME_DIR_LOCATION, Spark.getUserHome());
		System.setProperty(ConfigurationService.PNAME_SC_HOME_DIR_NAME, ".");
		System.setProperty(ConfigurationService.PNAME_SC_CACHE_DIR_LOCATION, Spark.getUserHome());
		System.setProperty(ConfigurationService.PNAME_SC_LOG_DIR_LOCATION, Spark.getUserHome());
		
		LibJitsi.start();
		
		MediaType[] mediaTypes = MediaType.values();
		MediaService mediaService = LibJitsi.getMediaService();
		for (MediaType mediaType : mediaTypes)
		{
			System.err.println("================================");
			System.err.println("MediaType: " + mediaType);
			System.out.println(mediaService);		
		
			MediaDevice device = mediaService.getDefaultDevice(mediaType, MediaUseCase.CALL);
			if (device != null)
			{
				System.out.println(device.getDirection());
			}
			System.err.println("Device: " + device);
			System.err.println("================================");
		}

		
		vectorAudioDevices = CaptureDeviceManager.getDeviceList(new AudioFormat(AudioFormat.LINEAR));	
		for ( CaptureDeviceInfo infoCaptureDevice : vectorAudioDevices)
		{			     
			String protocol = infoCaptureDevice.getLocator().getProtocol();
			audioDevice.addItem("[" + protocol + "]" + convertSysString(infoCaptureDevice.getName()));
		}

		vectorVideoDevices = CaptureDeviceManager.getDeviceList(new AVFrameFormat());
		for (  CaptureDeviceInfo infoCaptureDevice : vectorVideoDevices )
		{
            videoDevice.addItem(convertSysString(infoCaptureDevice.getName()));		
		}
		vectorVideoDevices.add(null);
		videoDevice.addItem("<None>");	
		
		AudioSystem mediaAudioSystem = ((MediaServiceImpl)LibJitsi.getMediaService()).getDeviceConfiguration().getAudioSystem();
		for (AudioSystem system : AudioSystem.getAudioSystems())
		{
			System.out.println(system);
			vectorAudioSystem.add(system);
			audioSystem.addItem(system);
		}
		
		for (CaptureDeviceInfo2 device : mediaAudioSystem.getDevices(DataFlow.PLAYBACK)) {
			playbackDevice.addItem(convertSysString(device.getName()));
			vectorPlaybackDevices.add(device);
		}
	}
	
	private void createSTUNPanel() 
	{
	    JPanel stunPanel = new JPanel(new GridBagLayout());
	    stunPanel.setBorder(BorderFactory.createTitledBorder(Res.getString("stun.border.label")));
	    add(stunPanel);
	    
	    JLabel stunServer = new JLabel(Res.getString("stun.server.addr"));
	    JLabel stunPort = new JLabel(Res.getString("stun.server.port"));
	    
	    
	    _stunServerInput.setPreferredSize(new Dimension(120, 20));
	    _stunPortInput.setPreferredSize(new Dimension(120, 20));
	    
	    stunPanel.add(stunServer, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
	    stunPanel.add(_stunServerInput, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
	    stunPanel.add(stunPort, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
            stunPanel.add(_stunPortInput, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0)); 
	}
	
	public String getAudioDevice() {
		if (audioDevice.getSelectedIndex() >= 0) {
			return vectorAudioDevices.get(audioDevice.getSelectedIndex()).getLocator().toExternalForm();
		}
		return "";
	}
	
	public void setAudioDevice(String device) {
        AudioSystem audioSystem = ((MediaServiceImpl)LibJitsi.getMediaService()).getDeviceConfiguration().getAudioSystem();
		for ( CaptureDeviceInfo2 infoCaptureDevice : vectorAudioDevices) {
			System.out.println(device);
			if (infoCaptureDevice.getLocator().toExternalForm().equals(device)) {
				audioDevice.setSelectedIndex(vectorAudioDevices.indexOf(infoCaptureDevice));
				audioSystem.setDevice(DataFlow.CAPTURE, infoCaptureDevice, true);
			}
		}	
	}
	
	public String getPlaybackDevice() {
		if (playbackDevice.getSelectedIndex() >= 0) {
			return vectorPlaybackDevices.get(playbackDevice.getSelectedIndex()).getLocator().toExternalForm();
		}
		return "";
	}
	
	public void setPlaybackDevice(String device) {
        AudioSystem audioSystem = ((MediaServiceImpl)LibJitsi.getMediaService()).getDeviceConfiguration().getAudioSystem();
		for ( CaptureDeviceInfo2 infoCaptureDevice : vectorPlaybackDevices) {
			if (infoCaptureDevice.getLocator().toExternalForm().equals(device)) {
				playbackDevice.setSelectedItem(infoCaptureDevice.getName());
				audioSystem.setDevice(DataFlow.PLAYBACK, infoCaptureDevice, true);
				audioSystem.setDevice(DataFlow.NOTIFY  , infoCaptureDevice, true);
			}
		}	
	}
	
	public String getAudioSystem() {
		if (audioSystem.getSelectedIndex() >= 0)
		{			
			return vectorAudioSystem.get(audioSystem.getSelectedIndex()).getLocatorProtocol();
		}
		return null;
	}
		
	public void setAudioSystem(String selectedAudioSystem) {
		for (AudioSystem system : vectorAudioSystem)
		{
			if (system.getLocatorProtocol().equals(selectedAudioSystem))
			{
				System.out.println("setAudioSystem:" + system);
				audioSystem.setSelectedIndex(vectorAudioSystem.indexOf(system));
				((MediaServiceImpl)LibJitsi.getMediaService()).getDeviceConfiguration().setAudioSystem(system, true);
			}
		}
		System.out.println("AudioSystem:" + ((MediaServiceImpl)LibJitsi.getMediaService()).getDeviceConfiguration().getAudioSystem());
	}
	
	
	public void setVideoDevice(String device) {
		for ( CaptureDeviceInfo infoCaptureDevice : vectorVideoDevices) {
			if (infoCaptureDevice != null && 
				infoCaptureDevice.getLocator().toExternalForm().equals(device)) {
				videoDevice.setSelectedItem(infoCaptureDevice.getName());
			}
		}	
	}
	
	public String getVideoDevice() {
		if (videoDevice.getSelectedIndex() >= 0) {
			if (vectorVideoDevices.get(videoDevice.getSelectedIndex()) == null) {
				return "";
			}
			return vectorVideoDevices.get(videoDevice.getSelectedIndex()).getLocator().toExternalForm();
		}
		return "";
	}
	
	       
        public String getStunServer() {
            return _stunServerInput.getText();
        }

        public void setStunServer(String server) {
            this._stunServerInput.setText(server);
        }

        public int getStunPort() {
            return Integer.valueOf(_stunPortInput.getText());
        }

        public void setStunPort(int port) {
         
            this._stunPortInput.setText(String.valueOf(port));
        }
	
	
	
	/**
	 * Logs the audio devices
	 */
    public void logAudioDevices() {
        @SuppressWarnings("unchecked")
        final Vector<CaptureDeviceInfo> vectorDevices = CaptureDeviceManager.getDeviceList(null);

        for (CaptureDeviceInfo infoCaptureDevice : vectorDevices) {
            System.err.println(convertSysString(infoCaptureDevice.getName()));
            for (Format format : infoCaptureDevice.getFormats()) {
                System.err.println("   " + format);
            }
        }
    }
}
