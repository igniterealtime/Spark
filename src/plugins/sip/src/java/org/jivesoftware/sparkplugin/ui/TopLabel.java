/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.ui;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 */
public class TopLabel extends JPanel {

	private static final long serialVersionUID = -820396327537797012L;
	private JLabel label;
    private Image backgroundImage;

    public TopLabel(String text) {
        setLayout(new BorderLayout());
        label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.BOLD, 12));
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);

        label.setForeground(Color.white);
        add(label, BorderLayout.CENTER);
        backgroundImage = PhoneRes.getImageIcon("TITLE_PANE").getImage();
    }

    public void setText(String text){
        label.setText(text);
    }

    public void paintComponent(Graphics g) {
        double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
        double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
        ((Graphics2D)g).drawImage(backgroundImage, xform, this);
    }


}
