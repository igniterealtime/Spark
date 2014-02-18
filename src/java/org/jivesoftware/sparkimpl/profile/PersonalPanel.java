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

package org.jivesoftware.sparkimpl.profile;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PersonalPanel extends JPanel {

    private static final long serialVersionUID = 8348926698365178921L;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTextField lastNameField;
    private JTextField nicknameField;
    private JTextField emailAddressField;
    private JTextField jidField;
    private JLabel jidLabel = new JLabel();


    public PersonalPanel() {
        setLayout(new GridBagLayout());

        // Handle First Name
        JLabel firstNameLabel = new JLabel();
        firstNameField = new JTextField();
        ResourceUtils.resLabel(firstNameLabel, firstNameField, Res.getString("label.first.name") + ":");

        add(firstNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(firstNameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Handle Middle Name
        JLabel middleNameLabel = new JLabel();
        middleNameField = new JTextField();
        ResourceUtils.resLabel(middleNameLabel, middleNameField, Res.getString("label.middle.name") + ":");
        add(middleNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(middleNameField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Handle Last Name
        JLabel lastNameLabel = new JLabel();
        lastNameField = new JTextField();
        ResourceUtils.resLabel(lastNameLabel, lastNameField, Res.getString("label.last.name") + ":");
        add(lastNameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(lastNameField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Handle Nickname
        JLabel nicknameLabel = new JLabel();
        nicknameField = new JTextField();
        ResourceUtils.resLabel(nicknameLabel, nicknameField, Res.getString("label.nickname") + ":");
        add(nicknameLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(nicknameField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Handle Email Address
        JLabel emaiAddressLabel = new JLabel();
        emailAddressField = new JTextField();
        ResourceUtils.resLabel(emaiAddressLabel, emailAddressField, Res.getString("label.email.address") + ":");
        add(emaiAddressLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(emailAddressField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


        jidField = new JTextField();
        ResourceUtils.resLabel(jidLabel, jidField, Res.getString("label.jid") + ":");
        add(jidLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(jidField, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        jidLabel.setVisible(false);
        jidField.setVisible(false);

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

    public void setJID(String jid) {
        jidField.setText(jid);
    }

    public void showJID(boolean show) {
        jidLabel.setVisible(show);
        jidField.setVisible(show);
    }

    public void allowEditing(boolean allowEditing) {
        Component[] comps = getComponents();
        if (comps != null) {
            final int no = comps.length;
            for (int i = 0; i < no; i++) {
                Component comp = comps[i];
                if (comp instanceof JTextField) {
                    ((JTextField)comp).setEditable(allowEditing);
                }
            }
        }
    }

}
