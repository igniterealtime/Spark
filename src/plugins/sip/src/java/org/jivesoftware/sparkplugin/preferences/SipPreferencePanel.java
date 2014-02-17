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
package org.jivesoftware.sparkplugin.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>SipPreferencePanel</code> class created Spark Preferences Panel
 * @version 1.0, 28/09/2006
 */

public class SipPreferencePanel extends JPanel {

	private static final long	serialVersionUID	= 3514377990815313963L;

	private JPanel generalPanel = new JPanel();

    private JPanel networkPanel = new JPanel();

    private JTextField userNameField = new JTextField();

    private JTextField authUserNameField = new JTextField();

    private JPasswordField passwordField = new JPasswordField();

    private JTextField serverField = new JTextField();

    private JCheckBox registerCheckBox = new JCheckBox();

    private JTextField stunServerField = new JTextField();

    private JTextField stunPortField = new JTextField();

    private JCheckBox useStun = new JCheckBox();

    private JLabel userNameLabel = new JLabel();

    private JLabel authUserNameLabel = new JLabel();

    private JLabel passwordLabel = new JLabel();

    private JLabel serverLabel = new JLabel();

    private JLabel stunServerLabel = new JLabel();

    private JLabel stunPortLabel = new JLabel();


    /**
     * Constructor invokes UI setup.
     */
    public SipPreferencePanel() {
			// Build the UI
		   createUI();
    }

    private void createUI() {
        setLayout(new VerticalFlowLayout());

// Setup Mnemonics
        ResourceUtils.resLabel(userNameLabel, userNameField, "&Username:");
        ResourceUtils.resLabel(authUserNameLabel, authUserNameField,
            "&AuthUsername:");
        ResourceUtils.resLabel(passwordLabel, passwordField, "&Password:");
        ResourceUtils.resLabel(serverLabel, serverField, "&Server:");
        ResourceUtils.resButton(registerCheckBox, "&Register in start");

        ResourceUtils.resLabel(stunServerLabel, stunServerField,
            "&Stun Server:");
        ResourceUtils.resLabel(stunPortLabel, stunPortField, "&Stun Port:");
        ResourceUtils.resButton(useStun, "&Use stun");

        generalPanel.setBorder(BorderFactory
            .createTitledBorder("General Information"));

        add(generalPanel);

        generalPanel.setLayout(new GridBagLayout());

        generalPanel.add(userNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0,
            0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(userNameField, new GridBagConstraints(1, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 100, 0));
        generalPanel.add(authUserNameLabel, new GridBagConstraints(0, 2, 1, 1,
            0.0, 0.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(authUserNameField, new GridBagConstraints(1, 2, 1, 1,
            1.0, 0.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));
        generalPanel.add(passwordLabel, new GridBagConstraints(0, 3, 1, 1, 0.0,
            0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(passwordField, new GridBagConstraints(1, 3, 1, 1, 1.0,
            0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 100, 0));
        generalPanel.add(serverLabel, new GridBagConstraints(0, 4, 1, 1, 0.0,
            0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(serverField, new GridBagConstraints(1, 4, 1, 1, 1.0,
            0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 100, 0));
        generalPanel.add(registerCheckBox, new GridBagConstraints(1, 5, 1, 1,
            1.0, 0.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));

// Network Panel

        networkPanel.setBorder(BorderFactory
            .createTitledBorder("Network Information"));

        add(networkPanel);

        networkPanel.setLayout(new GridBagLayout());

        networkPanel.add(stunServerLabel, new GridBagConstraints(0, 1, 1, 1, 0.0,
            0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        networkPanel.add(stunServerField, new GridBagConstraints(1, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 100, 0));
        networkPanel.add(stunPortLabel, new GridBagConstraints(0, 2, 1, 1,
            0.0, 0.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        networkPanel.add(stunPortField, new GridBagConstraints(1, 2, 1, 1,
            1.0, 0.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));
        networkPanel.add(useStun, new GridBagConstraints(1, 3, 1, 1,
            1.0, 0.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));
    }


    /**
     * Returns the SIP password to use.
     *
     * @return the SIP password to use.
     */
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    /**
     * Returns the SIP UserName to use.
     *
     * @return the SIP UserName to use.
     */
    public String getUserName() {
        return new String(userNameField.getText());
    }

    /**
     * Returns the SIP AuthUserName to use.
     *
     * @return the SIP AuthUserName to use.
     */
    public String getAuthUserName() {
        return new String(authUserNameField.getText());
    }

    /**
     * Returns the SIP Server to use.
     *
     * @return the SIP Server to use.
     */
    public String getServer() {
        return new String(serverField.getText());
    }

    /**
     * Sets if SIPark should register at start.
     *
     * @return true if the user is registered.
     */
    public boolean getRegister() {
        return registerCheckBox.isSelected();
    }

    /**
     * Sets the SIP password to use.
     *
     * @return the SIP password to use.
     */
    public void setPassword(String pass) {
        passwordField.setText(pass);
    }

    /**
     * Sets the SIP UserName to use.
     *
     * @param user The SIP UserName to use.
     */
    public void setUserName(String user) {
        userNameField.setText(user);
    }

    /**
     * Sets the SIP AuthUserName to use.
     *
     * @param auth SIP AuthUserName to use.
     */
    public void setAuthUserName(String auth) {
        authUserNameField.setText(auth);
    }

    /**
     * Gets the Stun Server to use.
     *
     * @return the
     *         Stun Server to use.
     */
    public String getStunServer() {
        return stunServerField.getText();
    }

    /**
     * Gets the Stun Port to use.
     *
     * @return the
     *         Stun Port to use.
     */
    public String getStunPort() {
        return stunPortField.getText();
    }

    /**
     * Gets the Stun Use.
     *
     * @return the
     *         Stun Use.
     */
    public boolean getUseStun() {
        return useStun.isSelected();
    }

    /**
     * Sets the Stun Server to use.
     *
     * @param server Stun Server to use.
     */
    public void setStunServer(String server) {
        stunServerField.setText(server);
    }

    /**
     * Sets the Stun Port to use.
     *
     * @param port Stun Port to use.
     */
    public void setStunPort(String port) {
        stunPortField.setText(port);
    }

    /**
     * Sets the Stun Use.
     *
     * @param use Stun Use.
     */
    public void setUseStun(boolean use) {
        useStun.setSelected(use);
    }

    /**
     * Sets the SIP Server to use.
     *
     * @param server SIP Server to use.
     */
    public void setServer(String server) {
        serverField.setText(server);
    }

    /**
     * Sets if SIPark should register at start.
     *
     * @param register value.
     */
    public void setRegister(boolean register) {
        registerCheckBox.setSelected(register);
    }
}