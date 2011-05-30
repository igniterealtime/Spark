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
package org.jivesoftware.sparkplugin.ui.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Base Panel for Controls.
 *
 * @author Derek DeMoro
 */
public class ControlPanel extends JPanel {

    private static final long serialVersionUID = -8596701082529183291L;

    public ControlPanel() {
        setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230)));
    }

    public ControlPanel(LayoutManager layout) {
        super(layout);
        setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230)));
    }

    public void paintComponent(Graphics g) {
        final BufferedImage bufferedImage = new BufferedImage(2, getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        GradientPaint paint = new GradientPaint(0, 0, Color.white, 0, getHeight(), new Color(235, 241, 246), true);

        g2d.setPaint(paint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();

        g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
    }
}
