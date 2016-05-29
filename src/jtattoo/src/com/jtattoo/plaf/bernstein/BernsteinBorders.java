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
 
package com.jtattoo.plaf.bernstein;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class BernsteinBorders extends BaseBorders {

    //------------------------------------------------------------------------------------
    // Lazy access methods
    //------------------------------------------------------------------------------------
    public static Border getButtonBorder() {
        if (buttonBorder == null) {
            buttonBorder = new ButtonBorder();
        }
        return buttonBorder;
    }

    public static Border getToggleButtonBorder() {
        return getButtonBorder();
    }

    public static Border getRolloverToolButtonBorder() {
        if (rolloverToolButtonBorder == null) {
            rolloverToolButtonBorder = new RolloverToolButtonBorder();
        }
        return rolloverToolButtonBorder;
    }

    public static Border getInternalFrameBorder() {
        if (internalFrameBorder == null) {
            internalFrameBorder = new InternalFrameBorder();
        }
        return internalFrameBorder;
    }

    //------------------------------------------------------------------------------------
    // Implementation of border classes
    //------------------------------------------------------------------------------------
    public static class ButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(4, 8, 4, 8);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color cHi = AbstractLookAndFeel.getControlDarkShadow();
            Color cLo = ColorHelper.darker(cHi, 8);
            JTattooUtilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class ButtonBorder

    public static class RolloverToolButtonBorder implements Border, UIResource {

        private static final Color frameHiColor = ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 60);
        private static final Color frameLoColor = AbstractLookAndFeel.getFrameColor();
        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            if (model.isEnabled()) {
                if (model.isRollover()) {
                    JTattooUtilities.draw3DBorder(g, frameHiColor, frameLoColor, x, y, w - 1, h);
                    JTattooUtilities.draw3DBorder(g, Color.white, frameHiColor, x + 1, y + 1, w - 2, h - 2);
                } else if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                    g.setColor(frameHiColor);
                    g.drawRect(x, y, w - 2, h - 1);
                } else {
                    g.setColor(AbstractLookAndFeel.getFrameColor());
                    g.drawRect(x, y, w - 2, h - 1);
                }
            } else {
                g.setColor(frameHiColor);
                g.drawRect(x, y, w - 2, h - 1);
            }
            g.setColor(Color.white);
            g.drawLine(w - 1, 0, w - 1, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class RolloverToolButtonBorder

    public static class InternalFrameBorder extends BaseInternalFrameBorder {

        private static final Color borderColor = new Color(255, 244, 128);
        private static final Color FRAME_COLORS[] = {
            new Color(229, 187, 0),
            new Color(251, 232, 0),
            new Color(247, 225, 0),
            new Color(243, 216, 0),
            new Color(229, 187, 0),};

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (!isResizable(c)) {
                Color cHi = ColorHelper.brighter(borderColor, 40);
                Color cLo = ColorHelper.darker(borderColor, 20);
                JTattooUtilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
                cHi = ColorHelper.darker(cHi, 20);
                cLo = ColorHelper.brighter(cLo, 20);
                JTattooUtilities.draw3DBorder(g, cHi, cLo, x + 1, y + 1, w - 2, h - 2);
                g.setColor(borderColor);
                for (int i = 2; i < dw; i++) {
                    g.drawRect(i, i, w - (2 * i) - 1, h - (2 * i) - 1);
                }
                return;
            }
            int dt = w / 3;
            int db = w * 2 / 3;
            h--;
            w--;

            Color cr = borderColor;
            g.setColor(FRAME_COLORS[0]);
            g.drawLine(x, y, x, y + h);
            g.setColor(FRAME_COLORS[1]);
            g.drawLine(x + 1, y + 1, x + 1, y + h - 1);
            g.setColor(FRAME_COLORS[2]);
            g.drawLine(x + 2, y + 2, x + 2, y + h - 2);
            g.setColor(FRAME_COLORS[3]);
            g.drawLine(x + 3, y + 3, x + 3, y + h - 3);
            g.setColor(FRAME_COLORS[4]);
            g.drawLine(x + 4, y + 4, x + 4, y + h - 4);

            // rechts
            g.setColor(cr);
            g.drawLine(x + w, y, x + w, y + h);
            g.setColor(ColorHelper.brighter(cr, 30));
            g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
            g.setColor(ColorHelper.brighter(cr, 60));
            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2);
            g.setColor(ColorHelper.brighter(cr, 90));
            g.drawLine(x + w - 3, y + 3, x + w - 3, y + h - 3);
            g.setColor(cr);
            g.drawLine(x + w - 4, y + 4, x + w - 4, y + h - 4);

            g.setColor(FRAME_COLORS[0]);
            g.drawLine(x + w, y, x + w, y + trackWidth);
            g.setColor(FRAME_COLORS[1]);
            g.drawLine(x + w - 1, y + 1, x + w - 1, y + trackWidth);
            g.setColor(FRAME_COLORS[2]);
            g.drawLine(x + w - 2, y + 2, x + w - 2, y + trackWidth);
            g.setColor(FRAME_COLORS[3]);
            g.drawLine(x + w - 3, y + 3, x + w - 3, y + trackWidth);
            g.setColor(FRAME_COLORS[4]);
            g.drawLine(x + w - 4, y + 4, x + w - 4, y + trackWidth);

            g.setColor(FRAME_COLORS[0]);
            g.drawLine(x + w, y + h - trackWidth, x + w, y + h);
            g.setColor(FRAME_COLORS[1]);
            g.drawLine(x + w - 1, y + h - trackWidth, x + w - 1, y + h - 1);
            g.setColor(FRAME_COLORS[2]);
            g.drawLine(x + w - 2, y + h - trackWidth, x + w - 2, y + h - 2);
            g.setColor(FRAME_COLORS[3]);
            g.drawLine(x + w - 3, y + h - trackWidth, x + w - 3, y + h - 3);
            g.setColor(FRAME_COLORS[4]);
            g.drawLine(x + w - 4, y + h - trackWidth, x + w - 4, y + h - 4);
            // oben
            g.setColor(FRAME_COLORS[0]);
            g.drawLine(x, y, x + dt, y);
            g.setColor(FRAME_COLORS[1]);
            g.drawLine(x + 1, y + 1, x + dt, y + 1);
            g.setColor(FRAME_COLORS[2]);
            g.drawLine(x + 2, y + 2, x + dt, y + 2);
            g.setColor(FRAME_COLORS[3]);
            g.drawLine(x + 3, y + 3, x + dt, y + 3);
            g.setColor(FRAME_COLORS[4]);
            g.drawLine(x + 4, y + 4, x + dt, y + 4);

            g.setColor(cr);
            g.drawLine(x + dt, y, x + w, y);
            g.setColor(ColorHelper.brighter(cr, 90));
            g.drawLine(x + dt, y + 1, x + w - 1, y + 1);
            g.setColor(ColorHelper.brighter(cr, 60));
            g.drawLine(x + dt, y + 2, x + w - 2, y + 2);
            g.setColor(ColorHelper.brighter(cr, 30));
            g.drawLine(x + dt, y + 3, x + w - 3, y + 3);
            g.setColor(cr);
            g.drawLine(x + dt, y + 4, x + w - 4, y + 4);

            g.setColor(FRAME_COLORS[0]);
            g.drawLine(x + w - trackWidth, y, x + w, y);
            g.setColor(FRAME_COLORS[1]);
            g.drawLine(x + w - trackWidth, y + 1, x + w - 1, y + 1);
            g.setColor(FRAME_COLORS[2]);
            g.drawLine(x + w - trackWidth, y + 2, x + w - 2, y + 2);
            g.setColor(FRAME_COLORS[3]);
            g.drawLine(x + w - trackWidth, y + 3, x + w - 3, y + 3);
            g.setColor(FRAME_COLORS[4]);
            g.drawLine(x + w - trackWidth, y + 4, x + w - 4, y + 4);

            // unten
            g.setColor(FRAME_COLORS[0]);
            g.drawLine(x, y + h, x + db, y + h);
            g.setColor(FRAME_COLORS[1]);
            g.drawLine(x + 1, y + h - 1, x + db, y + h - 1);
            g.setColor(FRAME_COLORS[2]);
            g.drawLine(x + 2, y + h - 2, x + db, y + h - 2);
            g.setColor(FRAME_COLORS[3]);
            g.drawLine(x + 3, y + h - 3, x + db, y + h - 3);
            g.setColor(FRAME_COLORS[4]);
            g.drawLine(x + 4, y + h - 4, x + db, y + h - 4);

            g.setColor(cr);
            g.drawLine(x + db, y + h, x + w, y + h);
            g.setColor(ColorHelper.brighter(cr, 30));
            g.drawLine(x + db, y + h - 1, x + w - 1, y + h - 1);
            g.setColor(ColorHelper.brighter(cr, 60));
            g.drawLine(x + db, y + h - 2, x + w - 2, y + h - 2);
            g.setColor(ColorHelper.brighter(cr, 90));
            g.drawLine(x + db, y + h - 3, x + w - 3, y + h - 3);
            g.setColor(cr);
            g.drawLine(x + db, y + h - 4, x + w - 4, y + h - 4);

            g.setColor(FRAME_COLORS[0]);
            g.drawLine(x + w - trackWidth, y + h, x + w, y + h);
            g.setColor(FRAME_COLORS[1]);
            g.drawLine(x + w - trackWidth, y + h - 1, x + w - 1, y + h - 1);
            g.setColor(FRAME_COLORS[2]);
            g.drawLine(x + w - trackWidth, y + h - 2, x + w - 2, y + h - 2);
            g.setColor(FRAME_COLORS[3]);
            g.drawLine(x + w - trackWidth, y + h - 3, x + w - 3, y + h - 3);
            g.setColor(FRAME_COLORS[4]);
            g.drawLine(x + w - trackWidth, y + h - 4, x + w - 4, y + h - 4);
        }
    } // class InternalFrameBorder
} // class BernsteinBorders

