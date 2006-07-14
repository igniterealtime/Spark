/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.profile;

import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PersonalPanel extends JPanel {

    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTextField lastNameField;
    private JTextField nicknameField;
    private JTextField emailAddressField;

    public PersonalPanel() {
        setLayout(new GridBagLayout());

        // Handle First Name
        JLabel firstNameLabel = new JLabel();
        firstNameField = new JTextField();
        ResourceUtils.resLabel(firstNameLabel, firstNameField, "&First Name:");

        add(firstNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(firstNameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Handle Middle Name
        JLabel middleNameLabel = new JLabel();
        middleNameField = new JTextField();
        ResourceUtils.resLabel(middleNameLabel, middleNameField, "&Middle Name:");
        add(middleNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(middleNameField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Handle Last Name
        JLabel lastNameLabel = new JLabel();
        lastNameField = new JTextField();
        ResourceUtils.resLabel(lastNameLabel, lastNameField, "&Last Name:");
        add(lastNameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(lastNameField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Handle Nickname
        JLabel nicknameLabel = new JLabel();
        nicknameField = new JTextField();
        ResourceUtils.resLabel(nicknameLabel, nicknameField, "&Nickname:");
        add(nicknameLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(nicknameField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Handle Email Address
        JLabel emaiAddressLabel = new JLabel();
        emailAddressField = new JTextField();
        ResourceUtils.resLabel(emaiAddressLabel, emailAddressField, "&Email Address:");
        add(emaiAddressLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(emailAddressField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    public String getFirstName() {
        return firstNameField.getText();
    }

    public void setFirstName(String firstName) {
        firstNameField.setText(firstName);
    }

    public void setMiddleName(String middleName) {
        middleNameField.setText(middleName);
    }

    public String getMiddleName() {
        return middleNameField.getText();
    }

    public void setLastName(String lastName) {
        lastNameField.setText(lastName);
    }

    public String getLastName() {
        return lastNameField.getText();
    }


    public void setNickname(String nickname) {
        nicknameField.setText(nickname);
    }

    public String getNickname() {
        return nicknameField.getText();
    }

    public void setEmailAddress(String emailAddress) {
        emailAddressField.setText(emailAddress);
    }

    public String getEmailAddress() {
        return emailAddressField.getText();
    }

    public void focus() {
        firstNameField.requestFocus();
    }

    public void allowEditing(boolean allowEditing) {
        Component[] comps = getComponents();
        final int no = comps != null ? comps.length : 0;
        for (int i = 0; i < no; i++) {
            Component comp = comps[i];
            if (comp instanceof JTextField) {
                ((JTextField)comp).setEditable(allowEditing);
            }
        }
    }

}
