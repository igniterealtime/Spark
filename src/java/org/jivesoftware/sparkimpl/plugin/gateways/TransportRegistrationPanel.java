/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.gateways;

import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportManager;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.resource.Res;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


/**
 *
 */
public class TransportRegistrationPanel extends JPanel {

    private TitlePanel titlePanel;
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();

    public TransportRegistrationPanel(String serviceName) {
        setLayout(new GridBagLayout());

        final Transport transport = TransportManager.getTransport(serviceName);

        titlePanel = new TitlePanel(transport.getTitle(), transport.getInstructions(), transport.getIcon(), true);

        add(titlePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        final JLabel usernameLabel = new JLabel();
        usernameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        ResourceUtils.resLabel(usernameLabel, usernameField, Res.getString("label.username") + ":");
        add(usernameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(usernameField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        final JLabel passwordLabel = new JLabel();
        passwordLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.password") + ":");
        add(passwordLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(passwordField, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    public String getScreenName() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}
