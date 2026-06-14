/**
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

import org.jdesktop.swingx.JXDatePicker;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.jivesoftware.spark.util.GraphicUtils.localDatePickerGet;
import static org.jivesoftware.spark.util.GraphicUtils.localDatePickerSet;

public class PersonalPanel extends JPanel {
    private final JTextField firstNameField = new JTextField();
    private final JTextField middleNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JTextField nicknameField = new JTextField();
    private final JComboBox<String> genderField = new JComboBox<>();
    private final JXDatePicker birthdayField = new JXDatePicker();
    private final JTextField emailAddressField = new JTextField();
    private final JTextArea descriptionField = new JTextArea();

    public PersonalPanel() {
        setLayout(new GridBagLayout());
        // Handle First Name
        JLabel firstNameLabel = new JLabel();
        ResourceUtils.resLabel(firstNameLabel, firstNameField, Res.getString("label.first.name") + ":");
        Insets insets = new Insets(5, 5, 5, 5);
        int row = 0;
        add(firstNameLabel, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(firstNameField, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        // Handle Middle Name
        row++;
        JLabel middleNameLabel = new JLabel();
        ResourceUtils.resLabel(middleNameLabel, middleNameField, Res.getString("label.middle.name") + ":");
        add(middleNameLabel, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(middleNameField, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        // Handle Last Name
        row++;
        JLabel lastNameLabel = new JLabel();
        ResourceUtils.resLabel(lastNameLabel, lastNameField, Res.getString("label.last.name") + ":");
        add(lastNameLabel, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(lastNameField, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        // Handle Nickname
        row++;
        JLabel nicknameLabel = new JLabel();
        ResourceUtils.resLabel(nicknameLabel, nicknameField, Res.getString("label.nickname") + ":");
        add(nicknameLabel, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(nicknameField, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        // Gender
        row++;
        genderField.addItem("");
        genderField.addItem(Res.getString("label.gender.male"));
        genderField.addItem(Res.getString("label.gender.female"));
        JLabel genderLabel = new JLabel();
        ResourceUtils.resLabel(genderLabel, genderField, Res.getString("label.gender") + ":");
        add(genderLabel, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(genderField, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        // Birthday
        row++;
        JLabel birthdayLabel = new JLabel();
        ResourceUtils.resLabel(birthdayLabel, birthdayField, Res.getString("label.birthday") + ":");
        add(birthdayLabel, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(birthdayField, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        // Handle Email Address
        row++;
        JLabel emailAddressLabel = new JLabel();
        ResourceUtils.resLabel(emailAddressLabel, emailAddressField, Res.getString("label.email.address") + ":");
        add(emailAddressLabel, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(emailAddressField, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        // About memo
        row++;
        JLabel descriptionLabel = new JLabel();
        ResourceUtils.resLabel(descriptionLabel, descriptionField, Res.getString("label.description") + ":");
        descriptionField.setLineWrap(true);
        add(descriptionLabel, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        row++;
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionField);
        add(descriptionScrollPane, new GridBagConstraints(0, row, 2, 4, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
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

    public void setGender(String gender) {
        if (isEmpty(gender)) {
            genderField.setSelectedIndex(0);
            return;
        }
        int selectedIndex;
        switch (gender) {
            case "M":
                selectedIndex = 1;
                break;
            case "F":
                selectedIndex = 2;
                break;
            default:
                selectedIndex = 0;
        }
        genderField.setSelectedIndex(selectedIndex);
    }

    public String getGender() {
        switch (genderField.getSelectedIndex()) {
            case 1:
                return "M";
            case 2:
                return "F";
            default:
                return "";
        }
    }

    public void setBirthday(String bday) {
        localDatePickerSet(birthdayField, bday);
    }

    public String getBirthday() {
        return localDatePickerGet(birthdayField);
    }

    public void setEmailAddress(String emailAddress) {
        emailAddressField.setText(emailAddress);
    }

    public String getEmailAddress() {
        return emailAddressField.getText();
    }

    public String getDescription() {
        return descriptionField.getText();
    }

    public void setDescription(String description) {
        descriptionField.setText(description);
    }

    public void focus() {
        firstNameField.requestFocus();
    }

}
