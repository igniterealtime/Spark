/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 1999-2010 Jive Software. All rights reserved.
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
package org.jivesoftware.sparkimpl.preference.notifications;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class NotificationAlertUI extends JPanel {
    private static final long serialVersionUID = 3359608942567718697L;
    private JLabel avatarLabel = new JLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel emailAddressLabel = new JLabel();
    private JLabel professionLabel = new JLabel();

    private VCard vcard;
    private String jid;

    private boolean available;

    final JLabel topLabel = new JLabel();
    
    private static final int AVATAR_HEIGHT = 64;
    private static final int AVATAR_WIDTH = 64;

    public NotificationAlertUI(String jid, boolean available, Presence presence) {
        setLayout(new GridBagLayout());

        this.available = available;
        this.jid = StringUtils.parseBareAddress(jid);

        vcard = SparkManager.getVCardManager().getVCardFromMemory(StringUtils.parseBareAddress(jid));

        final Icon presenceIcon = PresenceManager.getIconFromPresence(presence);

        topLabel.setIcon(presenceIcon);
        topLabel.setHorizontalTextPosition(JLabel.RIGHT);
        topLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        topLabel.setForeground(Color.DARK_GRAY);

        // Add Top Label
        add(topLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Add Calller Block
        buildInnerBlock();
    }


    /**
     * Builds the part of the incoming call UI with the Callers information.
     */
    private void buildInnerBlock() {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230), 1));

        titleLabel.setHorizontalTextPosition(JLabel.RIGHT);

        // Add Avatar
        panel.add(avatarLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 15, 5, 0), 0, 0));

        // Add Avatar information
        panel.add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
        panel.add(professionLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 0, 0), 0, 0));
        panel.add(emailAddressLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 0, 0), 0, 0));

        // Set default settings
        titleLabel.setForeground(new Color(64, 103, 162));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));


        if (vcard != null) {
            handleVCardInformation(vcard);
        }
        else {
            updateWithGenericInfo();
        }

        // Add to panel
        add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    }


    private void updateWithGenericInfo() {
        String title = SparkManager.getUserManager().getUserNicknameFromJID(jid);

        titleLabel.setText(title);

        avatarLabel.setIcon(SparkRes.getImageIcon(SparkRes.DEFAULT_AVATAR_64x64_IMAGE));
        avatarLabel.invalidate();
        avatarLabel.validate();
        avatarLabel.repaint();


        invalidate();
        validate();
        repaint();
    }

    private void handleVCardInformation(VCard vcard) {
        if (vcard.getError() != null) {
            updateWithGenericInfo();
            return;
        }

        // Nickname label should show presence and nickname.
        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);

        String firstName = vcard.getFirstName();
        String lastName = vcard.getLastName();
        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
            titleLabel.setText(firstName + " " + lastName);
        }
        else if (ModelUtil.hasLength(firstName)) {
            titleLabel.setText(firstName);
        }
        else {
            titleLabel.setText(nickname);
        }

        final StringBuilder builder = new StringBuilder();

        String username = titleLabel.getText();
        builder.append(username);
        builder.append(" is ");

        builder.append(available ? "Online" : "Offline");
        topLabel.setText(builder.toString());

        String jobTitle = vcard.getField("TITLE");
        if (jobTitle != null) {
            professionLabel.setText(jobTitle);
        }

        String emailAddress = vcard.getEmailHome();
        if (ModelUtil.hasLength(emailAddress)) {
            emailAddressLabel.setText(emailAddress);

            final Color linkColor = new Color(49, 89, 151);
            final String unselectedText = "<html><body><font color=" + GraphicUtils.toHTMLColor(linkColor) + "><u>" + emailAddress + "</u></font></body></html>";
            final String hoverText = "<html><body><font color=red><u>" + emailAddress + "</u></font></body></html>";
            emailAddressLabel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    emailAddressLabel.setText(hoverText);
                }

                public void mouseExited(MouseEvent e) {
                    emailAddressLabel.setText(unselectedText);
                }
            });
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
                avatarLabel.setIcon(GraphicUtils.scale(avatarIcon, AVATAR_HEIGHT, AVATAR_WIDTH));
            }
            catch (Exception e) {
                // no issue
            }
        }
        else {
            avatarLabel.setIcon(SparkRes.getImageIcon(SparkRes.DEFAULT_AVATAR_64x64_IMAGE));
        }

        avatarLabel.invalidate();
        avatarLabel.validate();
        avatarLabel.repaint();

        invalidate();
        validate();
        repaint();
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

