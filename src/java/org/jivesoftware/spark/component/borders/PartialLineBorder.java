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
package org.jivesoftware.spark.component.borders;

import javax.swing.border.AbstractBorder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

/**
 * Renders edge borders for a concave appearance.
 *
 * @author Derek DeMoro
 */
public class PartialLineBorder extends AbstractBorder {
    private static final long serialVersionUID = -5125856347919451956L;
    private Color color;
    private int thickness;

    boolean top,
            left,
            bottom,
            right;

    public PartialLineBorder(Color color, int thickness) {
        top = true;
        left = true;
        bottom = true;
        right = true;

        this.color = color;
        this.thickness = thickness;


    }

    public boolean isBorderOpaque() {
        return true;
    }

    public Insets getBorderInsets(Component component) {
        return new Insets(2, 2, 2, 2);
    }

    public int getThickness() {
        return thickness;
    }

    public void paintBorder(Component component, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(color);


        if (top) {
            g2.drawLine(x, y, x + width, y);
        }
        if (left) {
            g2.drawLine(x, y, x, y + height);
        }
        if (bottom) {
            g2.drawLine(x, y + height, x + width, y + height);
        }
        if (right) {
            g2.drawLine(x + width, y, x + width, y + height);
        }
    }
}