/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2
 * <p>
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

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jxmpp.jid.EntityBareJid;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;

/**
 * UI for editing Local Preferences.
 */
public class LocalPreferencePanel extends JPanel {
    private final JTextField _portField = new JTextField();
    private final JTextField _timeOutField = new JTextField();
    private final JTextField _reconnectDelayField = new JTextField();
    private final JTextField _idleField = new JTextField();
    private final JTextField _idleStatusText = new JTextField();

    private final JCheckBox _autoLoginBox = new JCheckBox();
    private final JCheckBox _savePasswordBox = new JCheckBox();
    private final JCheckBox _idleBox = new JCheckBox();
    private final JCheckBox _launchOnStartupBox = new JCheckBox();
    private final JCheckBox _startMinimizedBox = new JCheckBox();
    private final JCheckBox _useSingleTrayClick = new JCheckBox();

    public LocalPreferencePanel() {
        setLayout(new VerticalFlowLayout());
        // Load local localPref
        LocalPreferences preferences = SettingsManager.getLocalPreferences();
        setPort(preferences.getXmppPort());
        setTimeOut(preferences.getTimeOut());
        setReconnectDelay(preferences.getReconnectDelay());
        setAutoLogin(preferences.isAutoLogin());
        setSavePassword(preferences.isSavePassword());
        setStartInSystemTray(preferences.isStartedHidden());
        setUseSingleClickInTray(preferences.isUsingSingleTrayClick());
        setIdleMessage(preferences.getIdleMessage());

        if (preferences.isSSOEnabled()) {
            setSavePassword(false);
            _autoLoginBox.setEnabled(true);
        } else {
            _savePasswordBox.addActionListener(e -> {
                _autoLoginBox.setEnabled(_savePasswordBox.isSelected());
                if (_savePasswordBox.isSelected()) {
                    EntityBareJid user = SparkManager.getSessionManager().getUserBareAddress();
                    String password = SparkManager.getSessionManager().getPassword();
                    preferences.setPasswordForUser(user, password);
                }
                if (!_savePasswordBox.isSelected()) {
                    setAutoLogin(false);
                    try {
                        preferences.clearPasswordForAllUsers();
                    } catch (Exception e1) {
                        Log.debug("Unable to clear saved password..." + e1);
                    }
                }
            });
        }

        _autoLoginBox.addActionListener(e -> {
            if ((_autoLoginBox.isSelected()) && (!preferences.isSSOEnabled())) {
                setSavePassword(true);
            }
        });

        _idleBox.addActionListener(e -> _idleField.setEnabled(isIdleOn()));

        setIdleOn(preferences.isIdleOn());
        setIdleTime(preferences.getIdleTime());

        final JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(Res.getString("group.login.information")));

        JLabel _portLabel = new JLabel();
        ResourceUtils.resLabel(_portLabel, _portField, Res.getString("label.xmpp.port") + ":");
        JLabel _timeOutLabel = new JLabel();
        ResourceUtils.resLabel(_timeOutLabel, _timeOutField, Res.getString("label.response.timeout") + ":");

        JLabel _reconnectDelayLabel = new JLabel();
        ResourceUtils.resLabel(_reconnectDelayLabel, _reconnectDelayField, Res.getString("label.reconnect.delay") + ":");

        JLabel _idleStatusLabel = new JLabel();
        ResourceUtils.resLabel(_idleStatusLabel, _idleStatusText, Res.getString("label.time.till.idlemessage") + ":");

        ResourceUtils.resButton(_autoLoginBox, Res.getString("checkbox.auto.login"));
        ResourceUtils.resButton(_savePasswordBox, Res.getString("checkbox.save.password"));
        JLabel _idleLabel = new JLabel();
        ResourceUtils.resLabel(_idleLabel, _idleField, Res.getString("label.time.till.idle") + ":");
        ResourceUtils.resButton(_idleBox, Res.getString("checkbox.idle.enabled"));

        ResourceUtils.resButton(_launchOnStartupBox, Res.getString("checkbox.launch.on.startup"));
        ResourceUtils.resButton(_startMinimizedBox, Res.getString("checkbox.start.in.tray"));
        ResourceUtils.resButton(_useSingleTrayClick, Res.getString("checkbox.click.single.tray"));

        Insets insets = new Insets(5, 5, 5, 5);
        inputPanel.add(_portLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, NORTHWEST, NONE, insets, 0, 0));
        inputPanel.add(_portField, new GridBagConstraints(1, 0, 1, 1, 0, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        inputPanel.add(_timeOutLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, NORTHWEST, NONE, insets, 50, 0));
        inputPanel.add(_timeOutField, new GridBagConstraints(1, 1, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        inputPanel.add(_reconnectDelayLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, NORTHWEST, NONE, insets, 50, 0));
        inputPanel.add(_reconnectDelayField, new GridBagConstraints(1, 2, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        inputPanel.add(_idleLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, NORTHWEST, NONE, insets, 50, 0));
        inputPanel.add(_idleField, new GridBagConstraints(1, 3, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        inputPanel.add(_idleStatusLabel, new GridBagConstraints(0, 4, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        inputPanel.add(_idleStatusText, new GridBagConstraints(1, 4, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        inputPanel.add(_idleBox, new GridBagConstraints(0, 5, 2, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));

        if (Default.getBoolean(Default.IDLE_LOCK) || !Enterprise.containsFeature(Enterprise.IDLE_FEATURE)) {
            _idleField.setEnabled(false);
            _idleStatusText.setEnabled(false);
            _idleBox.setEnabled(false);
        }
        if (!Default.getBoolean(Default.HIDE_SAVE_PASSWORD_AND_AUTO_LOGIN) && SettingsManager.getLocalPreferences().isPswdAutologin()) {
            if (!preferences.isSSOEnabled()) {
                inputPanel.add(_savePasswordBox, new GridBagConstraints(0, 6, 2, 1, 0, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
            }
            inputPanel.add(_autoLoginBox, new GridBagConstraints(0, 7, 2, 1, 0, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        }

        inputPanel.add(_launchOnStartupBox, new GridBagConstraints(0, 8, 2, 1, 0, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        _launchOnStartupBox.addActionListener(e -> setStartOnStartup(isStartOnStartup()));
        setStartOnStartup(preferences.isStartOnStartup());

        inputPanel.add(_startMinimizedBox, new GridBagConstraints(0, 9, 2, 1, 0, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        inputPanel.add(_useSingleTrayClick, new GridBagConstraints(0, 10, 2, 1, 0, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        inputPanel.add(new JLabel(), new GridBagConstraints(0, 11, 2, 1, 1, 1, NORTHWEST, BOTH, insets, 50, 0));

        add(inputPanel);
    }

    /**
     * Return the XMPP Port to communicate on.
     */
    public String getPort() {
        return _portField.getText();
    }

    public void setPort(int port) {
        _portField.setText(Integer.toString(port));
    }

    /**
     * Return the XMPP Timeout variable.
     */
    public String getTimeout() {
        return _timeOutField.getText();
    }

    public void setTimeOut(int timeOut) {
        _timeOutField.setText(Integer.toString(timeOut));
    }

    public String getReconnectDelay() {
        return _reconnectDelayField.getText();
    }

    public void setReconnectDelay(int reconnectDelay) {
        _reconnectDelayField.setText(Integer.toString(reconnectDelay));
    }

    /**
     * Return true if Auto Login is on.
     */
    public boolean getAutoLogin() {
        return _autoLoginBox.isSelected();
    }

    public void setAutoLogin(boolean auto) {
        _autoLoginBox.setSelected(auto);
    }

    /**
     * Return true if the password should be saved.
     */
    public boolean isSavePassword() {
        return _savePasswordBox.isSelected();
    }

    public void setSavePassword(boolean save) {
        _savePasswordBox.setSelected(save);
    }

    /**
     * Returns true if IDLE is on.
     */
    public boolean isIdleOn() {
        return _idleBox.isSelected();
    }

    public void setIdleOn(boolean on) {
        _idleBox.setSelected(on);
    }

    public void setIdleTime(int time) {
        String idleTime = Integer.toString(time);
        _idleField.setText(idleTime);
    }

    /**
     * Return the time to IDLE in minutes.
     */
    public String getIdleTime() {
        return _idleField.getText();
    }

    public void setStartInSystemTray(boolean startInTray) {
        _startMinimizedBox.setSelected(startInTray);
    }

    public boolean isStartInSystemTray() {
        return _startMinimizedBox.isSelected();
    }

    public boolean useSingleClickInTray() {
        return _useSingleTrayClick.isSelected();
    }

    public void setUseSingleClickInTray(boolean clickInTray) {
        _useSingleTrayClick.setSelected(clickInTray);
    }

    public boolean isStartOnStartup() {
        return _launchOnStartupBox.isSelected();
    }

    public void setStartOnStartup(boolean startup) {
        _launchOnStartupBox.setSelected(startup);
    }

    public String getIdleMessage() {
        return _idleStatusText.getText();
    }

    public void setIdleMessage(String text) {
        _idleStatusText.setText(text);
    }

}
