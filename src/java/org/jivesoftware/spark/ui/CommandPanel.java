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
package org.jivesoftware.spark.ui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.jivesoftware.Spark;

/**
 *
 */
public class CommandPanel extends JPanel {

	private static final long serialVersionUID = -720715661649067658L;
	//private final Image backgroundImage;

	public CommandPanel() {
		this(true);
	}

    public CommandPanel(boolean doLayout) {
	if (doLayout) {
		if (Spark.isWindows()) {
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		}
		else {
			setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		}

      setOpaque(false);
      
       // backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();
	}
     //   setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, new Color(197, 213, 230)));
    }


//    public void paintComponent(Graphics g) {
//        double scaleX = getWidth() / (double) backgroundImage.getWidth(null);
//        double scaleY = getHeight() / (double) backgroundImage.getHeight(null);
//        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
//        ((Graphics2D) g).drawImage(backgroundImage, xform, this);
//    }
}
