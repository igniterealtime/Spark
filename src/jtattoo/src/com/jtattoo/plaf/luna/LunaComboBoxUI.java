/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/
 
package com.jtattoo.plaf.luna;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public class LunaComboBoxUI extends BaseComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new LunaComboBoxUI();
    }

    public JButton createArrowButton() {
        return new ArrowButton();
    }

    protected void setButtonBorder() {
    }

//--------------------------------------------------------------------------------------------------    
    static class ArrowButton extends NoFocusButton {

        public ArrowButton() {
            setBorder(BorderFactory.createEmptyBorder());
            setBorderPainted(false);
            setContentAreaFilled(false);
        }

        public void paint(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;

            boolean isPressed = getModel().isPressed();
            boolean isRollover = getModel().isRollover();

            int width = getWidth();
            int height = getHeight();

            Color[] tc = AbstractLookAndFeel.getTheme().getThumbColors();
            Color c1 = tc[0];
            Color c2 = tc[tc.length - 1];

            if (isPressed) {
                c1 = ColorHelper.darker(c1, 5);
                c2 = ColorHelper.darker(c2, 5);
            } else if (isRollover) {
                c1 = ColorHelper.brighter(c1, 20);
                c2 = ColorHelper.brighter(c2, 20);
            }

            g2D.setPaint(new GradientPaint(0, 0, c1, width, height, c2));
            g.fillRect(0, 0, width, height);
            g2D.setPaint(null);
            g2D.setColor(Color.white);
            if (JTattooUtilities.isLeftToRight(this)) {
                g2D.drawRect(1, 0, width - 2, height - 1);
            } else {
                g2D.drawRect(0, 0, width - 2, height - 1);
            }

            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            g2D.setColor(c2);
            if (JTattooUtilities.isLeftToRight(this)) {
                g.drawLine(2, 1, width - 2, 1);
                g.drawLine(2, 2, 2, height - 2);
            } else {
                g.drawLine(1, 1, width - 3, 1);
                g.drawLine(1, 2, 1, height - 2);
            }
            g2D.setComposite(composite);

            // paint the icon
            Icon icon = LunaIcons.getComboBoxIcon();
            int x = (width - icon.getIconWidth()) / 2;
            int y = (height - icon.getIconHeight()) / 2;
            int dx = (JTattooUtilities.isLeftToRight(this)) ? 0 : -1;
            if (getModel().isPressed() && getModel().isArmed()) {
                icon.paintIcon(this, g, x + dx + 2, y + 1);
            } else {
                icon.paintIcon(this, g, x + dx + 1, y);
            }
        }
    } // end class ArrowButton
}
