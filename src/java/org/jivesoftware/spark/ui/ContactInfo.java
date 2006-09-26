/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.component.borders.PartialLineBorder;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents the UI for the "ToolTip" functionallity in the ContactList.
 *
 * @author Derek DeMoro
 */
public class ContactInfo extends JPanel {
    private final WrappedLabel nicknameLabel = new WrappedLabel();
    private final WrappedLabel statusLabel = new WrappedLabel();
    private final JLabel fullJIDLabel = new JLabel();
    private final JLabel imageLabel = new JLabel();


    public ContactInfo() {
        setLayout(new GridBagLayout());
        setBackground(Color.white);


        add(nicknameLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        add(statusLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 5, 5), 0, 0));
        add(fullJIDLabel, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(imageLabel, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));


        nicknameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        statusLabel.setForeground(Color.gray);
        fullJIDLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        fullJIDLabel.setForeground(Color.gray);


        nicknameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
        fullJIDLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));

        setBorder(BorderFactory.createEtchedBorder());
    }

    public void setContactItem(ContactItem contactItem) {
        nicknameLabel.setText(contactItem.getNickname());
        statusLabel.setText(contactItem.getStatus());
        fullJIDLabel.setText(contactItem.getFullJID());

        imageLabel.setBorder(null);

        try {
            URL avatarURL = contactItem.getAvatarURL();
            ImageIcon icon = null;
            if (avatarURL != null) {
                icon = new ImageIcon(avatarURL);
            }

            if (icon != null && icon.getIconHeight() > 1) {
                icon = GraphicUtils.scaleImageIcon(icon, 96, 96);
                imageLabel.setIcon(icon);

                imageLabel.setBorder(new PartialLineBorder(Color.gray, 1));
            }
            else {
                icon = new ImageIcon(SparkRes.getImageIcon(SparkRes.BLANK_24x24).getImage().getScaledInstance(1, 64, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);


            }
        }
        catch (MalformedURLException e) {
            Log.error(e);
        }
    }


    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 250;
        return size;
    }
}
