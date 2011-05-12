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
package org.jivesoftware.sparkimpl.plugin.gateways;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;


import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * Dialog to allow the addition of gateways within Spark.
 *
 * @author Derek DeMoro
 */
public class TransportRegistrationDialog extends JPanel implements ActionListener, KeyListener {
	private static final long serialVersionUID = -5766084489027807577L;
	private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField nicknameField = new JTextField();
    private RolloverButton registerButton = new RolloverButton("", null);
    private RolloverButton cancelButton = new RolloverButton("", null);
    private JDialog dialog;
    private String serviceName;
    private Transport transport;

    /**
     * Initiation Dialog with the tranport service name.
     *
     * @param serviceName the name of the transport service.
     */
    public TransportRegistrationDialog(String serviceName) {
	
        setLayout(new GridBagLayout());

        this.serviceName = serviceName;

        ResourceUtils.resButton(registerButton, Res.getString("button.save"));
        ResourceUtils.resButton(cancelButton, Res.getString("button.cancel"));


        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(registerButton);
        registerButton.requestFocus();
        buttonPanel.add(cancelButton);


        transport = TransportUtils.getTransport(serviceName);

        final TitlePanel titlePanel = new TitlePanel(transport.getTitle(), transport.getInstructions(), transport.getIcon(), true);

        int line = 0;
        add(titlePanel, new GridBagConstraints(0, line, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        line++;
        final JLabel usernameLabel = new JLabel();
        usernameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        ResourceUtils.resLabel(usernameLabel, usernameField, Res.getString("label.username") + ":");
        add(usernameLabel, new GridBagConstraints(0, line, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(usernameField, new GridBagConstraints(1, line, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        line++;
        final JLabel passwordLabel = new JLabel();
        passwordLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.password") + ":");
        add(passwordLabel, new GridBagConstraints(0, line, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(passwordField, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        if (transport.requiresNickname()) {
            line++;
            final JLabel nicknameLabel = new JLabel();
            nicknameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
            ResourceUtils.resLabel(nicknameLabel, nicknameField, Res.getString("label.nickname") + ":");
            add(nicknameLabel, new GridBagConstraints(0, line, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(nicknameField, new GridBagConstraints(1, line, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0,0));
        }

        line++;
        add(buttonPanel, new GridBagConstraints(0, line, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    /**
     * Invoke the Dialog.
     */
    public void invoke() {
        dialog = new JDialog(SparkManager.getMainWindow(), transport.getTitle(), false);
        dialog.add(this);
        dialog.pack();
        dialog.setSize(400, 200);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        GraphicUtils.centerWindowOnComponent(dialog, SparkManager.getMainWindow());
        dialog.setVisible(true);

        usernameField.requestFocus();

        usernameField.addKeyListener(this);
        passwordField.addKeyListener(this);
        registerButton.addActionListener(this);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.dispose();
            }
        });
    }

    public void addCancelActionListener(ActionListener a)
    {
	cancelButton.addActionListener(a);
    }
    
    public String getScreenName() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getNickname() {
        return nicknameField.getText();
    }

    public void actionPerformed(ActionEvent e) {
        String username = getScreenName();
        String password = getPassword();
        String nickname = getNickname();
        if (transport.requiresUsername() && !ModelUtil.hasLength(username)) {
            JOptionPane.showMessageDialog(this, Res.getString("message.gateway.username.error"), Res.getString("title.registration.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (transport.requiresPassword() && !ModelUtil.hasLength(password)) {
            JOptionPane.showMessageDialog(this, Res.getString("message.gateway.password.error"), Res.getString("title.registration.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (transport.requiresNickname() && !ModelUtil.hasLength(nickname)) {
            JOptionPane.showMessageDialog(this, Res.getString("message.gateway.nickname.error"), Res.getString("title.registration.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            TransportUtils.registerUser(SparkManager.getConnection(), serviceName, username, password, nickname);

            // Send Directed Presence
            final StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
            Presence presence = statusBar.getPresence();
            presence.setTo(transport.getServiceName());
            SparkManager.getConnection().sendPacket(presence);
        }
        catch (XMPPException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, Res.getString("message.registration.transport.failed"), Res.getString("title.registration.error"), JOptionPane.ERROR_MESSAGE);
        }

        dialog.dispose();
    }


    
    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            actionPerformed(null);
        }

    }

    public void keyReleased(KeyEvent keyEvent) {
    }
}
