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

