/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.luna;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

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

        private static final Color loFrameColor = new Color(240, 240, 244);

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
            if (JTattooUtilities.isLeftToRight(this)) {
                JTattooUtilities.draw3DBorder(g, Color.white, loFrameColor, 1, 0, width - 1, height);
            } else {
                JTattooUtilities.draw3DBorder(g, Color.white, loFrameColor, 0, 0, width - 1, height);
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
            if (getModel().isPressed() && getModel().isArmed()) {
                icon.paintIcon(this, g, x + 2, y + 1);
            } else {
                icon.paintIcon(this, g, x + 1, y);
            }
        }
    } // end class ArrowButton
}
