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

package org.jivesoftware.sparkimpl.settings.local;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

/**
 * UI for editing Local Preferences.
 */
public class LocalPreferencePanel extends JPanel {
    private static final long serialVersionUID = -1675058807882383560L;
    private JLabel _portLabel = new JLabel();
    private JLabel _idleLabel = new JLabel();
    private JLabel _timeOutLabel = new JLabel();
    private JLabel _idleStatusLabel = new JLabel();

    private JTextField _portField = new JTextField();
    private JTextField _timeOutField = new JTextField();
    private JTextField _idleField = new JTextField();
    private JTextField _idleStatusText;

    private JCheckBox _autoLoginBox = new JCheckBox();
    private JCheckBox _savePasswordBox = new JCheckBox();
    private JCheckBox _idleBox = new JCheckBox();
    private JCheckBox _launchOnStartupBox = new JCheckBox();
    private JCheckBox _startMinimizedBox = new JCheckBox();
    private JCheckBox _useSingleTrayClick = new JCheckBox();
    

    /**
     * Construct Local Preference UI.
     */
    public LocalPreferencePanel() {
	setLayout(new VerticalFlowLayout());


	// Load local localPref
	LocalPreferences preferences = SettingsManager.getLocalPreferences();
	_portField.setText(Integer.toString(preferences.getXmppPort()));
	_timeOutField.setText(Integer.toString(preferences.getTimeOut()));
	_autoLoginBox.setSelected(preferences.isAutoLogin());
	_savePasswordBox.setSelected(preferences.isSavePassword());
	_startMinimizedBox.setSelected(preferences.isStartedHidden());
	_useSingleTrayClick.setSelected(preferences.isUsingSingleTrayClick());
	
	_idleStatusText = new JTextField(preferences.getIdleMessage());

	_savePasswordBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		_autoLoginBox.setEnabled(_savePasswordBox.isSelected());
		if (!_savePasswordBox.isSelected()) {
		    _autoLoginBox.setSelected(false);
		}
	    }
	});

	_autoLoginBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (_autoLoginBox.isSelected()) {
		    _savePasswordBox.setSelected(true);
		}
	    }
	});

	_idleBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		_idleField.setEnabled(_idleBox.isSelected());
	    }
	});


	_idleBox.setSelected(preferences.isIdleOn());
	_idleField.setText(Integer.toString(preferences.getIdleTime()));

	final JPanel inputPanel = new JPanel();
	inputPanel.setLayout(new GridBagLayout());
	inputPanel.setBorder(BorderFactory.createTitledBorder(Res
		.getString("group.login.information")));

	ResourceUtils.resLabel(_portLabel, _portField,Res.getString("label.xmpp.port") + ":");
	ResourceUtils.resLabel(_timeOutLabel, _timeOutField,
		Res.getString("label.response.timeout") + ":");
	
	ResourceUtils.resLabel(_idleStatusLabel, _idleStatusText,
		Res.getString("label.time.till.idlemessage") + ":");
	
	ResourceUtils.resButton(_autoLoginBox,
		Res.getString("checkbox.auto.login"));
	ResourceUtils.resButton(_savePasswordBox,
		Res.getString("checkbox.save.password"));
	ResourceUtils.resLabel(_idleLabel, _idleField,
		Res.getString("label.time.till.idle") + ":");
	ResourceUtils.resButton(_idleBox,
		Res.getString("checkbox.idle.enabled"));
	
	ResourceUtils.resButton(_launchOnStartupBox,
		Res.getString("checkbox.launch.on.startup"));
	ResourceUtils.resButton(_startMinimizedBox,
		Res.getString("checkbox.start.in.tray"));
	ResourceUtils.resButton(_useSingleTrayClick,
			Res.getString("checkbox.click.single.tray"));
	
	inputPanel.add(_portLabel,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	inputPanel.add(_portField,    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5), 0, 0));
	inputPanel.add(_timeOutLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(5, 5, 5, 5), 50, 0));
	inputPanel.add(_timeOutField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
	inputPanel.add(_idleLabel,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(5, 5, 5, 5), 50, 0));
	inputPanel.add(_idleField,    new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5), 50, 0));
	inputPanel.add(_idleStatusLabel,new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
	inputPanel.add(_idleStatusText, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));	
	inputPanel.add(_idleBox,        new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5), 50, 0));

	
	inputPanel.add(_savePasswordBox, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
	inputPanel.add(_autoLoginBox,    new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));

	if (Spark.isWindows()) {
	    inputPanel.add(_launchOnStartupBox, new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50,0));
	    _launchOnStartupBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setStartOnStartup(_launchOnStartupBox.isSelected());
		}
	    });

	    _launchOnStartupBox.setSelected(preferences.getStartOnStartup());
	}

	inputPanel.add(_startMinimizedBox, new GridBagConstraints(0, 8, 2, 1,0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
	inputPanel.add(_useSingleTrayClick, new GridBagConstraints(0, 9, 2, 1,0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
	inputPanel.add(new JLabel(), new GridBagConstraints(0, 10, 2, 1, 1.0,1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,new Insets(5, 5, 5, 5), 50, 0));
	
	
	add(inputPanel);
    }

    /**
     * Sets the XMPP port to comminucate on.
     * 
     * @param port
     *            the XMPP port to communicate on.
     */
    public void setPort(String port) {
	_portField.setText(port);
    }

    /**
     * Return the XMPP Port to communicate on.
     * 
     * @return the XMPP Port to communicate on.
     */
    public String getPort() {
	return _portField.getText();
    }

    /**
     * Sets the XMPP Timeout(in seconds).
     * 
     * @param timeOut
     *            the XMPP Timeout(in seconds).
     */
    public void setTimeOut(String timeOut) {
	_timeOutField.setText(timeOut);
    }

    /**
     * Return the XMPP Timeout variable.
     * 
     * @return the XMPP Timeout variable.
     */
    public String getTimeout() {
	return _timeOutField.getText();
    }

    /**
     * Sets Auto Login on and off.
     * 
     * @param auto
     *            true if Auto Login is on.
     */
    public void setAutoLogin(boolean auto) {
	_autoLoginBox.setSelected(auto);
    }

    /**
     * Return true if Auto Login is on.
     * 
     * @return true if Auto Login is on.
     */
    public boolean getAutoLogin() {
	return _autoLoginBox.isSelected();
    }

    /**
     * Set true if the password should be encoded and saved.
     * 
     * @param save
     *            true if the password should be encoded and saved.
     */
    public void setSavePassword(boolean save) {
	_savePasswordBox.setSelected(save);
    }

    /**
     * Return true if the password should be saved.
     * 
     * @return true if the password should be saved.
     */
    public boolean isSavePassword() {
	return _savePasswordBox.isSelected();
    }

    /**
     * Returns true if IDLE is on.
     * 
     * @return true if IDLE is on.
     */
    public boolean isIdleOn() {
	return _idleBox.isSelected();
    }

    /**
     * Sets the IDLE on or off.
     * 
     * @param on
     *            true if IDLE should be on.
     */
    public void setIdleOn(boolean on) {
	_idleBox.setSelected(on);
    }

    /**
     * Sets the Idle Time in minutes.
     * 
     * @param time
     *            the Idle time in minutes.
     */
    public void setIdleTime(int time) {
	String idleTime = Integer.toString(time);
	_idleField.setText(idleTime);
    }

    /**
     * Return the time to IDLE.
     * 
     * @return the time to IDLE.
     */
    public String getIdleTime() {
	return _idleField.getText();
    }

    public void startInSystemTray(boolean startInTray) {
	_startMinimizedBox.setSelected(startInTray);
    }

    public boolean startInSystemTray() {
	return _startMinimizedBox.isSelected();
    }

    public void useSingleClickInTray(boolean clickInTray) {
	_useSingleTrayClick.setSelected(clickInTray);
    }

    public boolean useSingleClickInTray() {
	return _useSingleTrayClick.isSelected();
    }
    
    public boolean startOnStartup() {
	return _launchOnStartupBox.isSelected();
    }

    public void setStartOnStartup(boolean startup) {
	_launchOnStartupBox.setSelected(startup);
    }
   
    public String getIdleMessage(){
	return _idleStatusText.getText();
    }
    public void setIdleMessage(String text){
	_idleStatusText.setText(text);
    }

}
