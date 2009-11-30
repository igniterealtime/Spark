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

import org.jivesoftware.spark.component.RolloverButton;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TranscriptAlert extends JPanel {
	private static final long serialVersionUID = -4882289773265904417L;
	private JLabel imageLabel = new JLabel();
    private JLabel titleLabel = new JLabel();
    private RolloverButton yesButton = new RolloverButton();
    private RolloverButton cancelButton = new RolloverButton();

    public TranscriptAlert() {
        setLayout(new GridBagLayout());

        setBackground(new Color(250, 249, 242));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));

        add(yesButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(cancelButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

        yesButton.setForeground(new Color(73, 113, 196));
        cancelButton.setForeground(new Color(73, 113, 196));

        cancelButton.setFont(new Font("Dialog", Font.BOLD, 10));
        yesButton.setFont(new Font("Dialog", Font.BOLD, 10));

        yesButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));
        cancelButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

        cancelButton.setVisible(false);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));
    }

    public void setCancelButtonText(String cancelText) {
        cancelButton.setText(cancelText);
    }

    public void showCancelButton(boolean show) {
        cancelButton.setVisible(show);
    }

    public void setYesButtonText(String yesText) {
        yesButton.setText(yesText);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setIcon(Icon icon) {
        imageLabel.setIcon(icon);
    }


}
