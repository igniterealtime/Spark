/**
 * $RCSfile: ,v $
 * $Revision: 1.0 $
 * $Date: 2005/05/25 04:20:03 $
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.fastpath.workspace.panes;

import org.jivesoftware.resource.Default;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * An implementation of a colored background panel. Allows implementations
 * to specify an image to use in the background of the panel.
 */
public class BackgroundPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private ImageIcon backgroundImage;

    /**
     * Creates a background panel using the default Spark background image.
     */
    public BackgroundPane() {
       backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE);
    }

    /**
     * Creates a new background panel based on the icon specified.
     *
     * @param icon the icon to use in the background.
     */
    public BackgroundPane(ImageIcon icon) {
        this.backgroundImage = icon;
    }

    /**
     * Creates a new background panel with the specified layout.
     *
     * @param layout the type of layout to use with this panel. Note: The icon
     *               used will be the default Spark background image.
     */
    public BackgroundPane(LayoutManager layout) {
        super(layout);
        backgroundImage = new ImageIcon(getClass().getResource("/images/gray-background.png"));
    }

    /**
     * Specifies the new image to use as the background image in this panel.
     *
     * @param image the new image to use.
     */
    public void setBackgroundImage(ImageIcon image) {
        this.backgroundImage = image;
    }

    public void paintComponent(Graphics g) {
        Image backgroundImage = this.backgroundImage.getImage();
        double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
        double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
        ((Graphics2D)g).drawImage(backgroundImage, xform, this);
    }
}

