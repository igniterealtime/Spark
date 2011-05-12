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
