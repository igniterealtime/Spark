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
 
package com.jtattoo.plaf.smart;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class SmartBorders extends BaseBorders {

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

    public static Border getPaletteBorder() {
        if (paletteBorder == null) {
            paletteBorder = new PaletteBorder();
        }
        return paletteBorder;
    }

    //------------------------------------------------------------------------------------
    // Implementation of border classes
    //------------------------------------------------------------------------------------
    public static class ButtonBorder implements Border, UIResource {

        private static final Color defaultColorHi = new Color(220, 230, 245);
        private static final Color defaultColorMed = new Color(212, 224, 243);
        private static final Color defaultColorLo = new Color(200, 215, 240);
        private static final Insets insets = new Insets(3, 6, 3, 6);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2D = (Graphics2D) g;
            Color frameColor = AbstractLookAndFeel.getTheme().getFrameColor();
            if (!JTattooUtilities.isFrameActive(button)) {
                frameColor = ColorHelper.brighter(frameColor, 40);
            }
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (button.getRootPane() != null && button.equals(button.getRootPane().getDefaultButton())) {
                if (!button.getModel().isRollover()) {
                    g2D.setColor(defaultColorHi);
                    g2D.drawRoundRect(x + 1, y + 1, w - 4, h - 2, 6, 6);
                    g2D.setColor(defaultColorMed);
                    g2D.drawRoundRect(x + 2, y + 2, w - 6, h - 6, 6, 6);
                    g2D.setColor(defaultColorLo);
                    g2D.drawLine(x + 3, h - 3, w - 3, h - 3);
                    g2D.drawLine(w - 3, y + 4, w - 3, h - 4);
                }
            }

            g2D.setColor(Color.white);
            g2D.drawRoundRect(x, y, w - 1, h - 1, 6, 6);

            g2D.setColor(frameColor);
            g2D.drawRoundRect(x, y, w - 2, h - 2, 6, 6);

            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
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

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            if (model.isEnabled()) {
                if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                    Color frameColor = ColorHelper.darker(AbstractLookAndFeel.getToolbarBackgroundColor(), 30);
                    g.setColor(frameColor);
                    g.drawRect(x, y, w - 1, h - 1);

                    Graphics2D g2D = (Graphics2D) g;
                    Composite composite = g2D.getComposite();
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
                    g2D.setComposite(alpha);
                    g.setColor(Color.black);
                    g.fillRect(x + 1, y + 1, w - 2, h - 2);
                    g2D.setComposite(composite);
                } else if (model.isRollover()) {
                    Color frameColor = AbstractLookAndFeel.getToolbarBackgroundColor();
                    Color frameHiColor = ColorHelper.darker(frameColor, 5);
                    Color frameLoColor = ColorHelper.darker(frameColor, 20);
                    JTattooUtilities.draw3DBorder(g, frameHiColor, frameLoColor, x, y, w, h);
                    frameHiColor = Color.white;
                    frameLoColor = ColorHelper.brighter(frameLoColor, 60);
                    JTattooUtilities.draw3DBorder(g, frameHiColor, frameLoColor, x + 1, y + 1, w - 2, h - 2);

                    Graphics2D g2D = (Graphics2D) g;
                    Composite composite = g2D.getComposite();
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
                    g2D.setComposite(alpha);
                    g.setColor(Color.white);
                    g.fillRect(x + 2, y + 2, w - 4, h - 4);
                    g2D.setComposite(composite);

                    g.setColor(AbstractLookAndFeel.getFocusColor());
                    g.drawLine(x + 1, y + 1, x + w - 1, y + 1);
                    g.drawLine(x + 1, y + 2, x + w - 2, y + 2);
                } else if (model.isSelected()) {
                    Color frameColor = AbstractLookAndFeel.getToolbarBackgroundColor();
                    Color frameHiColor = Color.white;
                    Color frameLoColor = ColorHelper.darker(frameColor, 30);
                    JTattooUtilities.draw3DBorder(g, frameLoColor, frameHiColor, x, y, w, h);
                }
            }
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

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            boolean active = isActive(c);
            boolean resizable = isResizable(c);
            int th = getTitleHeight(c);
            Color frameColor = AbstractLookAndFeel.getWindowInactiveBorderColor();
            Color titleColor = AbstractLookAndFeel.getWindowInactiveTitleColorLight();
            if (active) {
                titleColor = AbstractLookAndFeel.getWindowTitleColorLight();
                frameColor = AbstractLookAndFeel.getWindowBorderColor();
            }

            if (!resizable) {
                Insets bi = getBorderInsets(c);
                g.setColor(frameColor);
                g.drawRect(x, y, w - 1, h - 1);
                if (active) {
                    g.setColor(AbstractLookAndFeel.getWindowTitleColorDark());
                } else {
                    g.setColor(AbstractLookAndFeel.getWindowInactiveTitleColorDark());
                }
                for (int i = 1; i < bi.left; i++) {
                    g.drawRect(i, i, w - (2 * i) - 1, h - (2 * i) - 1);
                }
                g.setColor(frameColor);
                g.drawLine(bi.left - 1, y + th + bi.top, bi.left - 1, y + h - bi.bottom);
                g.drawLine(w - bi.right, y + th + bi.top, w - bi.right, y + h - bi.bottom);
                g.drawLine(bi.left - 1, y + h - bi.bottom, w - bi.right, y + h - bi.bottom);
                return;
            }
            g.setColor(titleColor);
            g.fillRect(x, y + 1, w, dw - 1);
            g.fillRect(x + 1, y + h - dw, w - 2, dw - 1);
            Color color = ColorHelper.brighter(AbstractLookAndFeel.getWindowTitleColorDark(), 30);
            if (active) {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 1, dw, dw, th + 1);
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), w - dw, dw, dw, th + 1);

                Color c1 = AbstractLookAndFeel.getTheme().getWindowTitleColorDark();
                Color c2 = AbstractLookAndFeel.getTheme().getWindowTitleColorLight();
                g2D.setPaint(new GradientPaint(0, dw + th + 1, c1, 0, h - th - (2 * dw), c2));
                g.fillRect(1, dw + th + 1, dw - 1, h - th - (2 * dw));
                g.fillRect(w - dw, dw + th + 1, dw - 1, h - th - (2 * dw));
                g2D.setPaint(null);
            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 1, dw, dw, th + 1);
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), w - dw, dw, dw, th + 1);

                Color c1 = AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorDark();
                Color c2 = AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorLight();
                g2D.setPaint(new GradientPaint(0, dw + th + 1, c1, 0, h - th - (2 * dw), c2));
                g.fillRect(1, dw + th + 1, dw - 1, h - th - (2 * dw));
                g.fillRect(w - dw, dw + th + 1, dw - 1, h - th - (2 * dw));
                g2D.setPaint(null);
            }
            if (active && resizable) {
                int d = dw + 12;
                // unten
                color = AbstractLookAndFeel.getWindowTitleColorDark();
                Color cHi = ColorHelper.brighter(color, 30);
                Color cLo = ColorHelper.darker(color, 20);

                // links
                g.setColor(color);
                g.fillRect(x + 1, y + h - d, dw - 1, d - 1);
                g.fillRect(x + dw, y + h - dw, d - dw - 1, d - dw - 1);

                g.setColor(cLo);
                g.drawLine(x + 1, y + h - d - 2, x + dw - 2, y + h - d - 2);
                g.drawLine(x + dw - 2, y + h - d - 2, x + dw - 2, y + h - dw);
                g.drawLine(x + dw - 2, y + h - dw, x + d - 1, y + h - dw);
                g.drawLine(x + d - 1, y + h - dw, x + d - 1, y + h - 1);

                g.setColor(cHi);
                g.drawLine(x + 1, y + h - d - 1, x + dw - 3, y + h - d - 1);
                g.drawLine(x + dw - 1, y + h - d - 1, x + dw - 1, y + h - dw - 1);
                g.drawLine(x + dw - 1, y + h - dw + 1, x + d - 2, y + h - dw + 1);
                g.drawLine(x + d - 2, y + h - dw + 1, x + d - 2, y + h - 1);

                // rechts
                g.setColor(color);
                g.fillRect(x + w - d - 1, y + h - dw, d, dw - 1);
                g.fillRect(x + w - dw, y + h - d - 1, dw - 1, d);

                g.setColor(cLo);
                g.drawLine(x + w - dw - 1, y + h - d - 2, x + w - 1, y + h - d - 2);
                g.drawLine(x + w - dw, y + h - d - 2, x + w - dw, y + h - dw);
                g.drawLine(x + w - d - 1, y + h - dw, x + w - dw, y + h - dw);
                g.drawLine(x + w - d - 1, y + h - dw, x + w - d - 1, y + h - 1);

                g.setColor(cHi);
                g.drawLine(x + w - dw + 1, y + h - d - 1, x + w - 1, y + h - d - 1);
                g.drawLine(x + w - dw + 1, y + h - d - 1, x + w - dw + 1, y + h - dw);
                g.drawLine(x + w - d, y + h - dw + 1, x + w - dw + 1, y + h - dw + 1);
                g.drawLine(x + w - d, y + h - dw + 1, x + w - d, y + h - 1);
            }
            g.setColor(frameColor);
            g.drawRect(x, y, w - 1, h - 1);
            g.drawLine(x + dw - 1, y + dw + th, x + dw - 1, y + h - dw);
            g.drawLine(x + w - dw, y + dw + th, x + w - dw, y + h - dw);
            g.drawLine(x + dw - 1, y + h - dw, x + w - dw, y + h - dw);
        }
    } // class InternalFrameBorder

    public static class PaletteBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (JTattooUtilities.isFrameActive((JComponent) c)) {
                g.setColor(AbstractLookAndFeel.getFrameColor());
            } else {
                g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 40));
            }
            g.drawRect(x, y, w - 1, h - 1);
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
    } // class PaletteBorder
} // class SmartBorders

