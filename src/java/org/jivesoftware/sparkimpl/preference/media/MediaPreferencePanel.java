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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.fmj.media.RegistryDefaults;
import net.sf.fmj.media.cdp.GlobalCaptureDevicePlugger;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

public class MediaPreferencePanel  extends JPanel {
	private static final long serialVersionUID = 8297469864676223072L;
	private Vector<CaptureDeviceInfo> vectorAudioDevices;
	private Vector<CaptureDeviceInfo> vectorVideoDevices;
	
	private JComboBox audioDevice = new JComboBox();
	private JComboBox videoDevice = new JComboBox();
	private JTextField _stunServerInput = new JTextField();
        private JTextField _stunPortInput = new JTextField();
	
    public MediaPreferencePanel() {
		setLayout(new VerticalFlowLayout());
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(Res.getString("title.general.media")));
		add(panel);
			
		panel.setLayout(new GridBagLayout());
		
		JLabel lAudio = new JLabel(); // Res.getString("label.audio.device"));
		panel.add( lAudio, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
		panel.add(audioDevice, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
		
		
		JLabel lVideo = new JLabel(); // Res.getString("label.video.device"));
		panel.add( lVideo, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
        panel.add(videoDevice, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
	
        JButton redetect = new JButton(); // Res.getString("button.re.detect") );
        redetect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				scanDevices();
			}
        	
        });
        
        panel.add(redetect,new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
    
        // Setup Mnemonics
        ResourceUtils.resButton(redetect, Res.getString("button.re.detect"));
        ResourceUtils.resLabel(lVideo, videoDevice, Res.getString("label.video.device") + ":");
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
		
		// FMJ
		RegistryDefaults.registerAll(RegistryDefaults.FMJ | RegistryDefaults.FMJ_NATIVE);
		GlobalCaptureDevicePlugger.addCaptureDevices();
		
		vectorAudioDevices = CaptureDeviceManager.getDeviceList(new AudioFormat(AudioFormat.LINEAR));	
		for ( CaptureDeviceInfo infoCaptureDevice : vectorAudioDevices)
		{			     
			audioDevice.addItem(convertSysString(infoCaptureDevice.getName()));
		}
		
		vectorVideoDevices = CaptureDeviceManager.getDeviceList(new VideoFormat(VideoFormat.RGB));
		for (  CaptureDeviceInfo infoCaptureDevice : vectorVideoDevices )
		{
            videoDevice.addItem(convertSysString(infoCaptureDevice.getName()));		
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
		for ( CaptureDeviceInfo infoCaptureDevice : vectorAudioDevices) {
			if (infoCaptureDevice.getLocator().toExternalForm().equals(device)) {
				audioDevice.setSelectedItem(infoCaptureDevice.getName());
			}
		}	
	}
	
	public void setVideoDevice(String device) {
		for ( CaptureDeviceInfo infoCaptureDevice : vectorVideoDevices) {
			if (infoCaptureDevice.getLocator().toExternalForm().equals(device)) {
				videoDevice.setSelectedItem(infoCaptureDevice.getName());
			}
		}	
	}
	
	public String getVideoDevice() {
		if (videoDevice.getSelectedIndex() >= 0) {
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
