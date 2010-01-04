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

import org.jivesoftware.resource.Default;
import org.jivesoftware.Spark;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 */
public class CommandPanel extends JPanel {

	private static final long serialVersionUID = -720715661649067658L;
	private final Image backgroundImage;

    public CommandPanel() {
        if (Spark.isWindows()) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        }
        else {
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        }


        backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();

        setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, new Color(197, 213, 230)));
    }


    public void paintComponent(Graphics g) {
        double scaleX = getWidth() / (double) backgroundImage.getWidth(null);
        double scaleY = getHeight() / (double) backgroundImage.getHeight(null);
        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
        ((Graphics2D) g).drawImage(backgroundImage, xform, this);
    }
}
