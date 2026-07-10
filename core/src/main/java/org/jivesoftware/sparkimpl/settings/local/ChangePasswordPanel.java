/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2 (the "License");
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

import org.jivesoftware.MainWindow;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class ChangePasswordPanel extends JPanel {
    // Password changing
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmationPasswordField = new JPasswordField();
    private final JLabel passwordLabel = new JLabel();
    private final JLabel confirmationPasswordLabel = new JLabel();
    private final JButton btnChangePassword = new JButton(Res.getString("button.changePassword"));
    private JDialog dialog;

    public ChangePasswordPanel() {
        setLayout(new GridBagLayout());
        // Setup Resources
        ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.change.password.to") + ":");
        ResourceUtils.resLabel(confirmationPasswordLabel, confirmationPasswordField, Res.getString("label.confirm.password") + ":");
        ResourceUtils.resButton(btnChangePassword, Res.getString("button.changePassword"));

        Insets insets = new Insets(5, 5, 5, 5);
        add(passwordLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, NORTHWEST, NONE, insets, 0, 0));
        add(passwordField, new GridBagConstraints(1, 1, 1, 1, 1, 0, NORTHWEST, NONE, insets, 100, 0));
        add(confirmationPasswordLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, NORTHWEST, NONE, insets, 0, 0));
        add(confirmationPasswordField, new GridBagConstraints(1, 2, 1, 1, 1, 0, NORTHWEST, NONE, insets, 100, 0));

        add(btnChangePassword, new GridBagConstraints(0, 3, 4, 1, 1, 0, NORTHWEST, NONE, insets, 100, 0));

        btnChangePassword.addActionListener(this::btnChangePasswordClick);
    }

    private void btnChangePasswordClick(ActionEvent event) {
        String newPassword = getPassword();
        String confirmationPassword = getConfirmationPassword();
        if (isBlank(newPassword) || isBlank(confirmationPassword)) {
            return;
        }

        if (!newPassword.equals(confirmationPassword)) {
            MessageDialog.showErrorDialog(Res.getString("message.passwords.no.match"), null);
            return;
        }
        SparkManager.getUserManager().changePassword(newPassword);
        dialog.dispose();
    }

    /**
     * Returns the new password to use.
     */
    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    /**
     * Returns the confirmation password used to compare to the first password.
     */
    public String getConfirmationPassword() {
        return new String(confirmationPasswordField.getPassword()).trim();
    }

    public void invokeDialog(JFrame parent) {
        if (dialog != null) {
            dialog.dispose();
        }
        dialog = new JDialog(parent, Res.getString("button.changePassword"), false);
        dialog.add(this);
        dialog.pack();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        GraphicUtils.centerWindowOnComponent(dialog, parent);
        dialog.setVisible(true);

        passwordField.requestFocus();
    }
}
