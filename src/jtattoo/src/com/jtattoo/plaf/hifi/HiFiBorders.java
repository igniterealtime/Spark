/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiBorders extends BaseBorders {

    private static Border buttonBorder = null;
    private static Border rolloverToolButtonBorder = null;
    private static Border internalFrameBorder = null;
    private static Border scrollPaneBorder = null;
    private static Border tableScrollPaneBorder = null;
    private static Border toolBarBorder = null;

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

    public static Border getScrollPaneBorder() {
        if (scrollPaneBorder == null) {
            scrollPaneBorder = new ScrollPaneBorder();
        }
        return scrollPaneBorder;
    }

    public static Border getTableScrollPaneBorder() {
        if (tableScrollPaneBorder == null) {
            tableScrollPaneBorder = new ScrollPaneBorder();
        }
        return tableScrollPaneBorder;
    }

    public static Border getInternalFrameBorder() {
        if (internalFrameBorder == null) {
            internalFrameBorder = new InternalFrameBorder();
        }
        return internalFrameBorder;
    }

    public static Border getToolBarBorder() {
        if (toolBarBorder == null) {
            toolBarBorder = new ToolBarBorder();
        }
        return toolBarBorder;
    }

//------------------------------------------------------------------------------------
// Implementation of border classes
//------------------------------------------------------------------------------------
    public static class ButtonBorder implements Border, UIResource {

        private static final Color frameLoColor = new Color(120, 120, 120);
        private static final Color frameLowerColor = new Color(96, 96, 96);
        private static final Color frameLowestColor = new Color(32, 32, 32);
        private static final Insets insets = new Insets(4, 8, 4, 8);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            g.translate(x, y);

            g.setColor(frameLoColor);
            g.drawLine(1, 0, w - 3, 0);
            g.drawLine(0, 1, 0, h - 3);
            g.setColor(frameLowerColor);
            g.drawLine(w - 2, 0, w - 2, h - 2);
            g.drawLine(1, h - 2, w - 3, h - 2);

            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2D.setComposite(alpha);
            g2D.setColor(frameLowestColor);
            g.drawLine(1, 1, w - 3, 1);
            g.drawLine(1, 2, 1, h - 3);
            g.setColor(Color.black);
            g.drawLine(w - 1, 1, w - 1, h - 1);
            g.drawLine(1, h - 1, w - 1, h - 1);
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
            g2D.setComposite(alpha);
            g.drawLine(1, h - 2, 2, h - 1);
            g2D.setComposite(composite);

            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class ButtonBorder

//-------------------------------------------------------------------------------------------------    
    public static class RolloverToolButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            Color c1 = null;
            Color c2 = null;
            if (JTattooUtilities.isActive((JComponent) c)) {
                c1 = ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 60);
                c2 = AbstractLookAndFeel.getFrameColor();
            } else {
                c1 = AbstractLookAndFeel.getFrameColor();
                c2 = ColorHelper.darker(AbstractLookAndFeel.getFrameColor(), 20);
            }
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2D.setComposite(alpha);
            JTattooUtilities.draw3DBorder(g, c1, c2, 0, 0, w, h);
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
            g2D.setComposite(alpha);
            JTattooUtilities.draw3DBorder(g, c2, c1, 1, 1, w - 2, h - 2);
            g2D.setComposite(composite);
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class RolloverToolButtonBorder

//-------------------------------------------------------------------------------------------------    
    public static class ScrollPaneBorder implements Border, UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color frameColor = AbstractLookAndFeel.getTheme().getFrameColor();
            JTattooUtilities.draw3DBorder(g, frameColor, ColorHelper.brighter(frameColor, 10), x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class ScrollPaneBorder

//-------------------------------------------------------------------------------------------------    
    public static class TabbedPaneBorder implements Border, UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color frameColor = AbstractLookAndFeel.getTheme().getFrameColor();
            JTattooUtilities.draw3DBorder(g, frameColor, ColorHelper.brighter(frameColor, 10), x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class TabbedPaneBorder

//-------------------------------------------------------------------------------------------------    
//    public static class InternalFrameBorder extends BaseInternalFrameBorder {
//        
//        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
//            boolean active = isActive(c);
//            boolean resizable = isResizable(c);
//            Color frameColor = AbstractLookAndFeel.getFrameColor();
//            Color borderColor = AbstractLookAndFeel.getWindowInactiveBorderColor();
//            if (active)
//                borderColor = AbstractLookAndFeel.getWindowBorderColor();
//            Color cHi = ColorHelper.brighter(frameColor, 30);
//            Color cLo = frameColor;
//            if (!resizable) {
//                JTattooUtilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
//                g.setColor(borderColor);
//                for (int i = 1; i < dw; i++)
//                    g.drawRect(i, i, w - (2 * i) - 1, h - (2 * i) - 1);
//            }
//            else {
//                g.setColor(borderColor);
//                g.fillRect(0, 0, w - 1, dw);
//                g.fillRect(0, h - dw, w - 1, dw);
//                g.fillRect(0, dw, dw, h - dw);
//                g.fillRect(w - dw, dw, dw, h - dw);
//                g.setColor(cLo);
//                g.drawRect(x, y, w - 1, h - 1);
//                cLo = ColorHelper.darker(borderColor, 20);
//                JTattooUtilities.draw3DBorder(g, cHi, cLo, x + 1, y + 1, w - 2, h - 2);
//            }
//        }
//        
//    } // class InternalFrameBorder
//-------------------------------------------------------------------------------------------------    
    public static class InternalFrameBorder extends BaseInternalFrameBorder {

        public InternalFrameBorder() {
            insets.top = 3;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            boolean active = isActive(c);
            int th = getTitleHeight(c);
            Color titleColor = AbstractLookAndFeel.getWindowInactiveTitleBackgroundColor();
            Color borderColor = AbstractLookAndFeel.getWindowInactiveBorderColor();
            Color frameColor = ColorHelper.darker(AbstractLookAndFeel.getWindowInactiveBorderColor(), 10);
            if (active) {
                titleColor = AbstractLookAndFeel.getWindowTitleBackgroundColor();
                borderColor = AbstractLookAndFeel.getWindowBorderColor();
                frameColor = ColorHelper.darker(AbstractLookAndFeel.getWindowBorderColor(), 10);
            }
            g.setColor(titleColor);
            g.fillRect(x, y + 1, w, insets.top - 1);
            g.setColor(borderColor);
            g.fillRect(x + 1, y + h - dw, w - 2, dw - 1);

            if (active) {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 1, insets.top, dw, th + 1);
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), w - dw, insets.top, dw, th + 1);
            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 1, insets.top, dw - 1, th + 1);
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), w - dw, insets.top, dw - 1, th + 1);
            }

            g.setColor(borderColor);
            g.fillRect(1, insets.top + th + 1, dw - 1, h - th - dw);
            g.fillRect(w - dw, insets.top + th + 1, dw - 1, h - th - dw);

            g.setColor(frameColor);
            g.drawRect(x, y, w - 1, h - 1);
            g.drawLine(x + dw - 1, y + insets.top + th, x + dw - 1, y + h - dw);
            g.drawLine(x + w - dw, y + insets.top + th, x + w - dw, y + h - dw);
            g.drawLine(x + dw - 1, y + h - dw, x + w - dw, y + h - dw);
        }
    } // class InternalFrameBorder

    public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (((JToolBar) c).isFloatable()) {
                if (((JToolBar) c).getOrientation() == HORIZONTAL) {
                    if (!JTattooUtilities.isLeftToRight(c)) {
                        x += w - 15;
                    }
                    g.setColor(Color.gray);
                    g.drawLine(x + 3, y + 4, x + 3, h - 5);
                    g.drawLine(x + 6, y + 3, x + 6, h - 4);
                    g.drawLine(x + 9, y + 4, x + 9, h - 5);
                    g.setColor(Color.black);
                    g.drawLine(x + 4, y + 4, x + 4, h - 5);
                    g.drawLine(x + 7, y + 3, x + 7, h - 4);
                    g.drawLine(x + 10, y + 4, x + 10, h - 5);
                } else // vertical
                {
                    g.setColor(Color.gray);
                    g.drawLine(x + 3, y + 3, w - 4, y + 3);
                    g.drawLine(x + 3, y + 6, w - 4, y + 6);
                    g.drawLine(x + 3, y + 9, w - 4, y + 9);
                    g.setColor(Color.black);
                    g.drawLine(x + 3, y + 4, w - 4, y + 4);
                    g.drawLine(x + 3, y + 7, w - 4, y + 7);
                    g.drawLine(x + 3, y + 10, w - 4, y + 10);
                }
            }
        }

        public Insets getBorderInsets(Component c) {
            Insets insets = new Insets(2, 2, 2, 2);
            if (((JToolBar) c).isFloatable()) {
                if (((JToolBar) c).getOrientation() == HORIZONTAL) {
                    if (JTattooUtilities.isLeftToRight(c)) {
                        insets.left = 15;
                    } else {
                        insets.right = 15;
                    }
                } else {
                    insets.top = 15;
                }
            }
            Insets margin = ((JToolBar) c).getMargin();
            if (margin != null) {
                insets.left += margin.left;
                insets.top += margin.top;
                insets.right += margin.right;
                insets.bottom += margin.bottom;
            }
            return insets;
        }
    } // class ToolBarBorder
} // class HiFiBorders

