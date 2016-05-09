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
package org.jivesoftware.sparkplugin.ui;

import org.jivesoftware.resource.Default;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

/**
 *
 */
public class Backdrop extends JPanel {
	private static final long serialVersionUID = -1062382698141913525L;


	/**
     * Creates a background panel using the default Spark background image.
     */
    public Backdrop() {
    }


    public void paintComponent(Graphics g) {
        final Image backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();
        double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
        double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
        ((Graphics2D)g).drawImage(backgroundImage, xform, this);
    }
}
