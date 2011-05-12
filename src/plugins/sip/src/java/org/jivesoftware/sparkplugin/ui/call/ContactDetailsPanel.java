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
package org.jivesoftware.sparkplugin.ui.call;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.java.sipmack.sip.InterlocutorUI;

import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.LinkLabel;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkplugin.ui.TopLabel;

/**
 * ContactDetailsPanel handles does a mapping of the phone number with a VCard search on the server. If a JID is found
 * that is associated with this number, the jid will be returned and the VCard information will be displayed.
 */
public class ContactDetailsPanel extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 7323351117571880259L;
    private LinkLabel emailLabel;
    private JLabel contactNameLabel;
    private JLabel jobTitleLabel;
    private LinkLabel viewProfileLabel;

    public ContactDetailsPanel(InterlocutorUI ic) {
        setLayout(new GridBagLayout());

        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black));

        final TopLabel currentCallLabel = new TopLabel(PhoneRes.getIString("phone.contactdetails"));
        add(currentCallLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 5));

        // Add subject label        
        contactNameLabel = new JLabel(PhoneRes.getIString("phone.noinfo"));
        contactNameLabel.setFont(contactNameLabel.getFont().deriveFont(Font.BOLD));
        add(contactNameLabel, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 2), 0, 0));

        // Add phone label
        emailLabel = new LinkLabel("", "", Color.BLACK, Color.BLUE);
        emailLabel.setInvokeBrowser(false);
        emailLabel.addMouseListener(this);
        add(emailLabel, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 2), 0, 0));

        // Add Title Label
        jobTitleLabel = new JLabel("");
        add(jobTitleLabel, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 2), 0, 0));

        // Add Avatar Label
        // avatarLabel = new JLabel(SparkRes.getImageIcon(SparkRes.DEFAULT_AVATAR_64x64_IMAGE));
        // add(avatarLabel, new GridBagConstraints(2, 4, 1, 1, 0.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        viewProfileLabel = new LinkLabel(PhoneRes.getIString("phone.viewfullprofile"), "", Color.LIGHT_GRAY, Color.BLUE);
        viewProfileLabel.setVisible(false);
        viewProfileLabel.setInvokeBrowser(false);
        viewProfileLabel.setHorizontalTextPosition(JLabel.CENTER);
        viewProfileLabel.setHorizontalAlignment(JLabel.CENTER);
        add(viewProfileLabel, new GridBagConstraints(0, 5, 3, 1, 1.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        viewProfileLabel.addMouseListener(this);

        // Load the VCard information
        VCard vcard = SparkManager.getVCardManager().searchPhoneNumber(ic.getCall().getNumber());
        if (vcard != null) {
            displayVCard(vcard);
        }
    }

    private void displayVCard(VCard vCard) {
        if (vCard.getError() != null) {
            return;
        }
        String firstName = vCard.getFirstName();
        String lastName = vCard.getLastName();
        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
            contactNameLabel.setText(firstName + " " + lastName);
        }
        else if (ModelUtil.hasLength(firstName)) {
            contactNameLabel.setText(firstName);
        }
        else {
            contactNameLabel.setText(PhoneRes.getIString("phone.unknown"));
        }

        String jobTitle = vCard.getField("TITLE");
        if (jobTitle != null) {
            jobTitleLabel.setText(jobTitle);
        }

        String email = vCard.getEmailWork();
        if (email == null) {
            email = vCard.getEmailHome();
        }

        if (email != null) {
            emailLabel.setText(email);
        }

        invalidate();
        validate();
        repaint();

        viewProfileLabel.setVisible(true);
    }


    public void mouseClicked(MouseEvent e) {
    	URI uriMailTo = null;
        if (e.getSource() == emailLabel) {
            try {
            	uriMailTo = new URI("mailto", emailLabel.getText(),null);
                Desktop.getDesktop().mail(uriMailTo);
            }
            catch (Exception e1) {
                Log.error(e1);
            }
        }
        else if (e.getSource() == viewProfileLabel) {
            SparkManager.getVCardManager().viewProfile(emailLabel.getText(), this);
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void actionPerformed(ActionEvent e) {

    }
}
