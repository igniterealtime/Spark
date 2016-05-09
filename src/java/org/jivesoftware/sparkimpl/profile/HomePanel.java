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

import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.focus.SpecifiedOrderFocusTraversalPolicy;
import org.jivesoftware.spark.util.ResourceUtils;

public class HomePanel extends JPanel {

	private static final long serialVersionUID = -838061087276345124L;
	private JLabel streetLabel = new JLabel();
    private JLabel cityLabel = new JLabel();
    private JLabel stateLabel = new JLabel();
    private JLabel zipCodeLabel = new JLabel();
    private JLabel countryLabel = new JLabel();

    private JTextField cityField = new JTextField();
    private JTextField stateField = new JTextField();
    private JTextField zipCodeField = new JTextField();
    private JTextField countryField = new JTextField();
    private JTextField streetField = new JTextField();


    private JLabel phoneLabel = new JLabel();
    private JLabel faxLabel = new JLabel();
    private JLabel pagerLabel = new JLabel();
    private JLabel mobileLabel = new JLabel();
    private JTextField phoneField = new JTextField();
    private JTextField faxField = new JTextField();
    private JTextField pagerField = new JTextField();
    private JTextField mobileField = new JTextField();
    private JTextField webPageField = new JTextField();

    public HomePanel() {
        this.setLayout(new GridBagLayout());

        // Setup Resources
        ResourceUtils.resLabel(streetLabel, streetField,  Res.getString("label.street.address") + ":");
        ResourceUtils.resLabel(cityLabel, cityField, Res.getString("label.city") + ":");
        ResourceUtils.resLabel(stateLabel, stateField,  Res.getString("label.state.and.province") + ":");
        ResourceUtils.resLabel(zipCodeLabel, zipCodeField,  Res.getString("label.postal.code") + ":");
        ResourceUtils.resLabel(countryLabel, countryField,  Res.getString("label.country") + ":");

        ResourceUtils.resLabel(phoneLabel, phoneField, Res.getString("label.phone") + ":");
        ResourceUtils.resLabel(faxLabel, faxField,  Res.getString("label.fax") + ":");
        ResourceUtils.resLabel(mobileLabel, mobileField,  Res.getString("label.mobile") + ":");
        ResourceUtils.resLabel(pagerLabel, pagerField, Res.getString("label.pager") + ":");

        this.add(streetLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(streetField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        this.add(cityLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(cityField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        this.add(stateLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(stateField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        this.add(zipCodeLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(zipCodeField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        this.add(countryLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(countryField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


        this.add(phoneLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(faxLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(pagerLabel, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(mobileLabel, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        this.add(phoneField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(faxField, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(pagerField, new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(mobileField, new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        final Component order[] = new Component[] {	 
        											 streetField, cityField, stateField,
        											 zipCodeField,countryField, phoneField, 
        											 faxField, pagerField, mobileField, 
        										   };
        FocusTraversalPolicy policy = new SpecifiedOrderFocusTraversalPolicy(order);
        setFocusTraversalPolicy(policy);
        setFocusTraversalPolicyProvider(true); 
    }


    public void setStreetAddress(String address) {
        streetField.setText(address);
    }

    public String getStreetAddress() {
        return streetField.getText();
    }

    public void setCity(String city) {
        cityField.setText(city);
    }

    public String getCity() {
        return cityField.getText();
    }

    public void setState(String state) {
        stateField.setText(state);
    }

    public String getState() {
        return stateField.getText();
    }

    public void setZipCode(String zip) {
        zipCodeField.setText(zip);
    }

    public String getZipCode() {
        return zipCodeField.getText();
    }

    public void setCountry(String country) {
        countryField.setText(country);
    }

    public String getCountry() {
        return countryField.getText();
    }


    public void setPhone(String phone) {
        phoneField.setText(phone);
    }

    public String getPhone() {
        return phoneField.getText();
    }

    public void setFax(String fax) {
        faxField.setText(fax);
    }

    public String getFax() {
        return faxField.getText();
    }

    public void setPager(String pager) {
        pagerField.setText(pager);
    }

    public String getPager() {
        return pagerField.getText();
    }

    public void setMobile(String mobile) {
        mobileField.setText(mobile);
    }

    public String getMobile() {
        return mobileField.getText();
    }

    public void setWebPage(String webPage) {
        webPageField.setText(webPage);
    }

    public String getWebPage() {
        return webPageField.getText();
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