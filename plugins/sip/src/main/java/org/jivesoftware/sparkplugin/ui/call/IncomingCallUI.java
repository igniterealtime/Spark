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
package org.jivesoftware.sparkplugin.ui.call;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.softphone.SoftPhoneManager;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkplugin.callhistory.HistoryCall;
import org.jivesoftware.sparkplugin.callhistory.TelephoneUtils;


public class IncomingCallUI extends JPanel {

	private static final long	serialVersionUID	= -7451049865930942296L;
	private JLabel avatarLabel = new JLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel professionLabel = new JLabel();
    private JLabel phoneLabel = new JLabel();
    private JLabel lastCalledLabel = new JLabel();
    private JLabel durationLabel = new JLabel();

    private RolloverButton acceptButton;
    private RolloverButton rejectButton;

    private final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");


    public IncomingCallUI(InterlocutorUI ic) {
        setLayout(new GridBagLayout());

        final JLabel topLabel = new JLabel();
        topLabel.setIcon(PhoneRes.getImageIcon("INCOMING_CALL_IMAGE"));
        topLabel.setHorizontalTextPosition(JLabel.RIGHT);
        topLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        topLabel.setText(PhoneRes.getIString("phone.incomingcallfrom")+"...");
        topLabel.setForeground(Color.gray);


        final String phoneNumber = ic.getCall().getNumber();

        // Add Top Label
        add(topLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        boolean callerID = !ic.getCall().getNumber().equals(ic.getCall().getRemoteName());
        String title = ic.getCall().getRemoteName();
        if(!callerID){
            title = phoneNumber;
        }

        // Add Caller Block
        buildCallerBlock(title, phoneNumber);

        // Add Buttons
        addButtons();
    }


    /**
     * Builds the part of the incoming call UI with the Callers information.
     */
    private void buildCallerBlock(String callerID, String phoneNumber) {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230), 1));

        // Add Avatar
        panel.add(avatarLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

        // Add Avatar information
        panel.add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
        panel.add(professionLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 0, 0), 0, 0));
        panel.add(phoneLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 0, 0), 0, 0));

        // Add History labels
        panel.add(lastCalledLabel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 0, 0), 0, 0));
        panel.add(durationLabel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

        // Set default settings
        titleLabel.setForeground(new Color(64, 103, 162));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        final VCard vcard = SparkManager.getVCardManager().searchPhoneNumber(phoneNumber);
        if (vcard != null) {
            handleVCardInformation(vcard, phoneNumber);
        }
        else {
            avatarLabel.setVisible(false);
            professionLabel.setVisible(false);
            phoneLabel.setVisible(true);

            titleLabel.setText(callerID);
            phoneLabel.setText(phoneNumber);
        }

        // Update with previous call history.
        Date lastDate = null;
        long callLength = 0;
        for (HistoryCall call : SoftPhoneManager.getInstance().getLogManager().getCallHistory()) {
            String number = TelephoneUtils.removeInvalidChars(call.getNumber());
            if (number.equals(TelephoneUtils.removeInvalidChars(phoneNumber))) {
                lastDate = new Date(call.getTime());
            }

            callLength = call.getCallLength();
        }

        final StringBuilder builder = new StringBuilder();
        builder.append(PhoneRes.getIString("phone.lastcalled")+": ");
        if (lastDate == null) {
            builder.append(PhoneRes.getIString("phone.never"));
            durationLabel.setVisible(false);
        }
        else {
            builder.append(formatter.format(lastDate));
            durationLabel.setText(PhoneRes.getIString("phone.duration")+": " + ModelUtil.getTimeFromLong(callLength*1000));
        }

        lastCalledLabel.setText(builder.toString());

        // Add To Panel
        add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    }

    private void addButtons() {
        // Build Accept Button
        acceptButton = new RolloverButton("      "+PhoneRes.getIString("phone.accept"), PhoneRes.getImageIcon("TOASTER_ACCEPT_BUTTON"));
        acceptButton.setHorizontalTextPosition(JLabel.CENTER);
        acceptButton.setFont(new Font("Dialog", Font.BOLD, 11));
        acceptButton.setForeground(new Color(91, 175, 41));
        acceptButton.setMargin(new Insets(0, 0, 0, 0));

        // Build Reject Button
        rejectButton = new RolloverButton("      "+PhoneRes.getIString("phone.reject"), PhoneRes.getImageIcon("TOASTER_REJECT_BUTTON"));
        rejectButton.setHorizontalTextPosition(JLabel.CENTER);
        rejectButton.setFont(new Font("Dialog", Font.BOLD, 11));
        rejectButton.setForeground(new Color(153, 32, 10));
        rejectButton.setMargin(new Insets(0, 0, 0, 0));
        
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(acceptButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(rejectButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(panel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    }

    private void handleVCardInformation(VCard vcard, String phoneNumber) {
        if (vcard.getError() != null) {
            return;
        }


        String firstName = vcard.getFirstName();
        String lastName = vcard.getLastName();
        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
            titleLabel.setText(firstName + " " + lastName);
        }
        else if (ModelUtil.hasLength(firstName)) {
            titleLabel.setText(firstName);
        }

        phoneLabel.setText(phoneNumber);


        String jobTitle = vcard.getField("TITLE");
        if (jobTitle != null) {
            professionLabel.setText(jobTitle);
        }


        byte[] avatarBytes = null;
        try {
            avatarBytes = vcard.getAvatar();
        }
        catch (Exception e) {
            Log.error("Cannot retrieve avatar bytes.", e);
        }

        if (avatarBytes != null) {
            try {
                ImageIcon avatarIcon = new ImageIcon(avatarBytes);
                avatarLabel.setIcon(avatarIcon);
                avatarLabel.invalidate();
                avatarLabel.validate();
                avatarLabel.repaint();
            }
            catch (Exception e) {
                // no issue
            }
        }


        invalidate();
        validate();
        repaint();
    }

    public RolloverButton getAcceptButton() {
        return acceptButton;
    }

    public RolloverButton getRejectButton() {
        return rejectButton;
    }


    public void paintComponent(Graphics g) {
        BufferedImage cache = new BufferedImage(2, getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = cache.createGraphics();

        GradientPaint paint = new GradientPaint(0, 0, new Color(233, 240, 247), 0, getHeight(), Color.white, true);

        g2d.setPaint(paint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();

        g.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
    }
}
