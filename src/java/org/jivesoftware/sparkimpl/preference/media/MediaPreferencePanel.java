package org.jivesoftware.sparkimpl.preference.media;

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

import net.sf.fmj.media.RegistryDefaults;
import net.sf.fmj.media.cdp.GlobalCaptureDevicePlugger;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.log.Log;

public class MediaPreferencePanel  extends JPanel {
	private static final long serialVersionUID = 8297469864676223072L;
	private Vector<CaptureDeviceInfo> vectorAudioDevices;
	private Vector<CaptureDeviceInfo> vectorVideoDevices;
	
	private JComboBox audioDevice = new JComboBox();
	private JComboBox videoDevice = new JComboBox();
	
	public MediaPreferencePanel() {
		setLayout(new VerticalFlowLayout());
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(Res.getString("title.general.media")));
		add(panel);
			
		panel.setLayout(new GridBagLayout());
		
		
		panel.add(new JLabel( Res.getString("label.audio.device") ), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
		panel.add(audioDevice, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
		
		
		
		panel.add(new JLabel( Res.getString("label.video.device") ), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
        panel.add(videoDevice, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
	
        JButton redetect = new JButton( Res.getString("button.re.detect") );
        redetect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				scanDevices();
			}
        	
        });
        
        panel.add(redetect,new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));
        
        scanDevices();
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
	
	public void scanDevices()
	{
		// Remove all Items
		audioDevice.removeAllItems();
		videoDevice.removeAllItems();
		
		// FMJ
		RegistryDefaults.registerAll(RegistryDefaults.FMJ | RegistryDefaults.FMJ_NATIVE);

		// Add Device
		GlobalCaptureDevicePlugger.addCaptureDevices(); 

		// LOG ALL Devices
		final Vector<CaptureDeviceInfo> vectorDevices = CaptureDeviceManager.getDeviceList(null);
		for ( CaptureDeviceInfo infoCaptureDevice : vectorDevices )
		{
			System.err.println(convertSysString(infoCaptureDevice.getName()));
			for (Format format : infoCaptureDevice.getFormats())
			{
				System.err.println("   " + format);
			}		
		}
		
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
}
