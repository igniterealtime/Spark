/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class FastBorders extends BaseBorders {

    private static Border buttonBorder;
    private static Border toolButtonBorder;
    private static Border rolloverToolButtonBorder;
    private static Border internalFrameBorder;

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

    public static Border getToolButtonBorder() {
        if (toolButtonBorder == null) {
            toolButtonBorder = new ToolButtonBorder();
        }
        return toolButtonBorder;
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

        private static final Color defaultFrameColor = new Color(0, 64, 255);
        private static final Insets insets = new Insets(4, 8, 4, 8);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Color frameColor = ColorHelper.darker(button.getBackground(), 30);
            if (model.isEnabled()) {
                if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                    g.setColor(frameColor);
                    g.drawRect(x, y, w - 1, h - 1);
                } else {
                    g.setColor(frameColor);
                    g.drawRect(x, y, w - 1, h - 1);
                    g.setColor(ColorHelper.brighter(button.getBackground(), 40));
                    g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
                    g.drawLine(x + 1, y + 1, x + 1, y + h - 2);
                }
                if (c instanceof JButton) {
                    JButton b = (JButton) c;
                    if (b.equals(b.getRootPane().getDefaultButton())) {
                        g.setColor(defaultFrameColor);
                        g.drawRect(x, y, w - 1, h - 1);
                    }
                }
            } else {
                g.setColor(FastLookAndFeel.getDisabledForegroundColor());
                g.drawRect(x, y, w - 1, h - 1);
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
    } // class ButtonBorder

//------------------------------------------------------------------------------    
    public static class ToolButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Color hiColor = ColorHelper.brighter(FastLookAndFeel.getFrameColor(), 90);
            Color loColor = FastLookAndFeel.getFrameColor();
            if (model.isEnabled()) {
                if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                    Graphics2D g2D = (Graphics2D) g;
                    Composite composite = g2D.getComposite();
                    g.setColor(loColor);
                    g.drawRect(x, y, w - 1, h - 1);
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
                    g2D.setComposite(alpha);
                    g.setColor(Color.black);
                    g.fillRect(x + 1, y + 1, w - 2, h - 2);
                    g2D.setComposite(composite);
                } else {
                    JTattooUtilities.draw3DBorder(g, hiColor, loColor, 0, 0, w, h);
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
    } // class ToolButtonBorder

//------------------------------------------------------------------------------    
    public static class RolloverToolButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Color loColor = FastLookAndFeel.getFrameColor();
            if (model.isEnabled()) {
                if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                    Graphics2D g2D = (Graphics2D) g;
                    Composite composite = g2D.getComposite();
                    g.setColor(loColor);
                    g.drawRect(x, y, w - 1, h - 1);
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
                    g2D.setComposite(alpha);
                    g.setColor(Color.black);
                    g.fillRect(x + 1, y + 1, w - 2, h - 2);
                    g2D.setComposite(composite);
                } else if (model.isRollover()) {
                    Graphics2D g2D = (Graphics2D) g;
                    Composite composite = g2D.getComposite();
                    g.setColor(loColor);
                    g.drawRect(x, y, w - 1, h - 1);
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
                    g2D.setComposite(alpha);
                    g.setColor(Color.white);
                    g.fillRect(x + 1, y + 1, w - 2, h - 2);
                    g2D.setComposite(composite);
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

//------------------------------------------------------------------------------    
    public static class InternalFrameBorder extends BaseInternalFrameBorder {

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            boolean active = isActive(c);
            boolean resizable = isResizable(c);
            Color frameColor = FastLookAndFeel.getFrameColor();
            Color borderColor = FastLookAndFeel.getWindowInactiveBorderColor();
            if (active) {
                borderColor = FastLookAndFeel.getWindowBorderColor();
            }
            Color cHi = ColorHelper.brighter(frameColor, 40);
            Color cLo = frameColor;
            if (!resizable) {
                JTattooUtilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
                g.setColor(borderColor);
                for (int i = 1; i < dw; i++) {
                    g.drawRect(i, i, w - (2 * i) - 1, h - (2 * i) - 1);
                }
                return;
            }
            JTattooUtilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
            cHi = ColorHelper.brighter(borderColor, 40);
            cLo = ColorHelper.darker(borderColor, 20);
            JTattooUtilities.draw3DBorder(g, cHi, cLo, x + 1, y + 1, w - 2, h - 2);

            g.setColor(borderColor);
            g.drawRect(x + 2, y + 2, w - 5, h - 5);
            g.drawRect(x + 3, y + 3, w - 7, h - 7);
            JTattooUtilities.draw3DBorder(g, ColorHelper.darker(borderColor, 5), ColorHelper.brighter(borderColor, 30), x + 4, y + 4, w - 8, h - 8);
        }
    } // class InternalFrameBorder

} // class FastBorders

