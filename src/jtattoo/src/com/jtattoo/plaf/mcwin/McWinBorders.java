/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinBorders extends BaseBorders {

    private static Border buttonBorder = null;
    private static Border rolloverToolButtonBorder = null;
    private static Border internalFrameBorder = null;

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

        private static final Insets insets = new Insets(2, 12, 2, 12);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public boolean isBorderOpaque() {
            return false;
        }
    } // class ButtonBorder

    public static class RolloverToolButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);
        private static final Color HICOLOR = new Color(192, 192, 192);
        private static final Color LOCOLOR = new Color(164, 164, 164);
        private static final Color ROLLOVER_HICOLOR = new Color(144, 225, 181);
        private static final Color ROLLOVER_LOCOLOR = new Color(124, 195, 160);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                JTattooUtilities.draw3DBorder(g, LOCOLOR, HICOLOR, x, y, w - 1, h);
            } else if (model.isRollover()) {
                JTattooUtilities.draw3DBorder(g, ROLLOVER_HICOLOR, ROLLOVER_LOCOLOR, x, y, w - 1, h);
            } else {
                g.setColor(Color.lightGray);
                g.drawRect(x, y, w - 2, h - 1);
            }
            g.setColor(Color.white);
            g.drawLine(w - 1, 0, w - 1, h - 1);

        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class RolloverToolButtonBorder

    public static class InternalFrameBorder extends BaseInternalFrameBorder {

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            boolean active = isActive(c);
            boolean resizable = isResizable(c);
            int th = getTitleHeight(c);
            Color frameColor = McWinLookAndFeel.getWindowInactiveBorderColor();
            Color titleColor = McWinLookAndFeel.getWindowInactiveTitleColorLight();
            Color borderColor = McWinLookAndFeel.getWindowInactiveTitleColorDark();
            if (active) {
                frameColor = McWinLookAndFeel.getWindowBorderColor();
                titleColor = McWinLookAndFeel.getWindowTitleColorLight();
                borderColor = McWinLookAndFeel.getWindowTitleColorDark();
            }
            if (!resizable) {
                Insets insets = getBorderInsets(c);
                g.setColor(frameColor);
                g.drawRect(x, y, w - 1, h - 1);
                if (active) {
                    g.setColor(McWinLookAndFeel.getWindowTitleColorDark());
                } else {
                    g.setColor(McWinLookAndFeel.getWindowInactiveTitleColorDark());
                }
                for (int i = 1; i < insets.left; i++) {
                    g.drawRect(i, i, w - (2 * i) - 1, h - (2 * i) - 1);
                }
                g.setColor(ColorHelper.brighter(frameColor, 20));
                g.drawLine(insets.left - 1, y + th + insets.top, insets.left - 1, y + h - insets.bottom);
                g.drawLine(w - insets.right, y + th + insets.top, w - insets.right, y + h - insets.bottom);
                g.drawLine(insets.left - 1, y + h - insets.bottom, w - insets.right, y + h - insets.bottom);
                return;
            }
            g.setColor(titleColor);
            g.fillRect(x, y + 1, w, dw - 1);
            g.setColor(borderColor);
            g.fillRect(x + 1, y + h - dw, w - 2, dw - 1);
            if (active) {
                JTattooUtilities.fillHorGradient(g, McWinLookAndFeel.getTheme().getWindowTitleColors(), 1, dw, dw, th + 1);
                JTattooUtilities.fillHorGradient(g, McWinLookAndFeel.getTheme().getWindowTitleColors(), w - dw, dw, dw, th + 1);
            } else {
                JTattooUtilities.fillHorGradient(g, McWinLookAndFeel.getTheme().getWindowInactiveTitleColors(), 1, dw, dw, th + 1);
                JTattooUtilities.fillHorGradient(g, McWinLookAndFeel.getTheme().getWindowInactiveTitleColors(), w - dw, dw, dw, th + 1);
            }
            g.setColor(borderColor);
            g.fillRect(1, insets.top + th + 1, dw - 1, h - th - dw);
            g.fillRect(w - dw, insets.top + th + 1, dw - 1, h - th - dw);

            g.setColor(ColorHelper.darker(frameColor, 10));
            g.drawRect(x, y, w - 1, h - 1);
            g.setColor(frameColor);
            g.drawLine(x + dw - 1, y + dw + th, x + dw - 1, y + h - dw);
            g.drawLine(x + w - dw, y + dw + th, x + w - dw, y + h - dw);
            g.drawLine(x + dw - 1, y + h - dw, x + w - dw, y + h - dw);
        }
    } // class InternalFrameBorder
} // class McWinBorders

