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

package org.jivesoftware.sparkplugin.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import net.java.sipmack.sip.NetworkAddressManager;
import net.java.sipmack.softphone.SoftPhoneManager;
import net.java.sipmack.softphone.listeners.RegisterEvent;

import org.jivesoftware.spark.component.BackgroundPanel;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.sparkplugin.components.CloseButton;
import org.jivesoftware.sparkplugin.ui.components.RectangleButton;

/**
 * Used for notifying users of registration and failure to register.
 */
public class RegistrationStatusPanel extends BackgroundPanel implements ActionListener {
	private static final long serialVersionUID = -7289401216186994399L;
	private JLabel loadingLabel;
    private CloseButton closeButton;
    private RectangleButton retryButton;

    public RegistrationStatusPanel() {
        setLayout(new GridBagLayout());

        loadingLabel = new JLabel();
        loadingLabel.setIcon(PhoneRes.getImageIcon("NORMAL_PHONE_ICON"));
        loadingLabel.setHorizontalAlignment(JLabel.LEFT);
        loadingLabel.setFont(new Font("Verdana", Font.BOLD, 11));

        add(loadingLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        retryButton = new RectangleButton();
        retryButton.setText(PhoneRes.getIString("phone.tryagain"));
        add(retryButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
        retryButton.setVisible(false);

        closeButton = new CloseButton();
        add(closeButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 0, 2, 2), 0, 0));

        // Set Border
        setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230), 1));

        closeButton.addActionListener(this);
        retryButton.addActionListener(this);
    }

    /**
     * Call to display registration information.
     */
    public void showRegistrationProgress() {
        loadingLabel.setText(PhoneRes.getIString("phone.starting"));
        loadingLabel.setForeground(new Color(63, 102, 161));
        retryButton.setVisible(false);
    }

    /**
     * Call to display failure to register.
     *
     * @param event the RegistrationEvent with reason.
     */
    public void showRegistrationFailed(RegisterEvent event) {
        loadingLabel.setForeground(new Color(210, 0, 0));
        loadingLabel.setText(PhoneRes.getIString("phone.failed"));
        loadingLabel.setIcon(PhoneRes.getImageIcon("NORMAL_PHONE_ICON"));
        retryButton.setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            setVisible(false);
        } else {
            NetworkAddressManager.resetIndex();
            showRegistrationProgress();
            SoftPhoneManager.getInstance().register();
        }
    }
}
