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
package org.jivesoftware.spark.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * ImageCombiner
 * 
 * @author wolf.posdorfer
 */
public class ImageCombiner {
    
    
    /**
     * Combines two images into one
     * 
     * @param image1
     *            left image
     * @param image2
     *            right image
     * @return combined Image
     */
    public static Image combine(Image image1, Image image2)
    {
	return combine(new ImageIcon(image1), new ImageIcon(image2));
    }

    /**
     * Combines two images into one
     * 
     * @param image1
     *            left image
     * @param image2
     *            right image
     * @return combined Image
     */
    public static Image combine(ImageIcon image1, ImageIcon image2) {

	ImageObserver comp = new JComponent() {
	    private static final long serialVersionUID = 1L;
	};

	int w = image1.getIconWidth() + image2.getIconWidth();
	int h = Math.max(image1.getIconHeight(), image2.getIconHeight());

	BufferedImage image = new BufferedImage(w, h,
		BufferedImage.TYPE_INT_ARGB);

	Graphics2D g2 = image.createGraphics();

	g2.drawImage(image1.getImage(), 0, 0, comp);
	g2.drawImage(image2.getImage(), image1.getIconWidth(), 0, comp);
	g2.dispose();

	return image;
    }

    public static Image returnTransparentImage(int w, int h) {

	BufferedImage image = new BufferedImage(w, h,
		BufferedImage.TYPE_INT_ARGB);

	return image;

    }
    
 
    /**
     * Creates an Image from the specified Icon
     * 
     * @param icon
     *            that should be converted to an image
     * @return the new image
     */
    public static Image iconToImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }
    
}
