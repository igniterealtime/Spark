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
 
package com.jtattoo.plaf.acryl;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class AcrylBorders extends BaseBorders {

    //------------------------------------------------------------------------------------
    // Lazy access methods
    //------------------------------------------------------------------------------------
    public static Border getTextBorder() {
        if (textFieldBorder == null) {
            textFieldBorder = new TextFieldBorder();
        }
        return textFieldBorder;
    }

    public static Border getSpinnerBorder() {
        if (spinnerBorder == null) {
            spinnerBorder = new SpinnerBorder();
        }
        return spinnerBorder;
    }

    public static Border getTextFieldBorder() {
        return getTextBorder();
    }

    public static Border getComboBoxBorder() {
        if (comboBoxBorder == null) {
            comboBoxBorder = new ComboBoxBorder();
        }
        return comboBoxBorder;
    }

    public static Border getScrollPaneBorder() {
        if (scrollPaneBorder == null) {
            scrollPaneBorder = new ScrollPaneBorder(false);
        }
        return scrollPaneBorder;
    }

    public static Border getTableScrollPaneBorder() {
        if (tableScrollPaneBorder == null) {
            tableScrollPaneBorder = new ScrollPaneBorder(true);
        }
        return tableScrollPaneBorder;
    }

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
    public static class TextFieldBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color borderColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 50);
            g.setColor(borderColor);
            g.drawRect(x, y, width - 1, height - 1);
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

    } // class TextFieldBorder

    public static class SpinnerBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color borderColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 50);
            g.setColor(borderColor);
            g.drawRect(x, y, width - 1, height - 1);
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

    } // class SpinnerBorder

    public static class ComboBoxBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color borderColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 50);
            g.setColor(borderColor);
            g.drawRect(x, y, width - 1, height - 1);
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

    } // class ComboBoxBorder

    public static class ScrollPaneBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);
        private static final Insets tableInsets = new Insets(1, 1, 1, 1);

        private boolean tableBorder = false;

        public ScrollPaneBorder(boolean tableBorder) {
            this.tableBorder = tableBorder;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 50));
            g.drawRect(x, y, w - 1, h - 1);
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getBackgroundColor(), 50));
            g.drawRect(x + 1, y + 1, w - 3, h - 3);
        }

        public Insets getBorderInsets(Component c) {
            if (tableBorder) {
                return new Insets(tableInsets.top, tableInsets.left, tableInsets.bottom, tableInsets.right);
            } else {
                return new Insets(insets.top, insets.left, insets.bottom, insets.right);
            }
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            Insets ins = getBorderInsets(c);
            borderInsets.left = ins.left;
            borderInsets.top = ins.top;
            borderInsets.right = ins.right;
            borderInsets.bottom = ins.bottom;
            return borderInsets;
        }

    } // class ScrollPaneBorder

    public static class ButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(3, 6, 3, 6);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Graphics2D g2D = (Graphics2D) g;
            Color frameColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 50);
            if (!JTattooUtilities.isFrameActive(button)) {
                frameColor = ColorHelper.brighter(frameColor, 30);
            }
            if (model.isRollover() && !model.isPressed() && !model.isArmed()) {
                frameColor = AbstractLookAndFeel.getTheme().getRolloverColorDark();
            }
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
                    Composite savedComposit = g2D.getComposite();
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
                    g2D.setComposite(alpha);
                    g.setColor(Color.black);
                    g.fillRect(x + 1, y + 1, w - 2, h - 2);
                    g2D.setComposite(savedComposit);
                } else if (model.isRollover()) {
                    Color frameColor = AbstractLookAndFeel.getToolbarBackgroundColor();
                    Color frameHiColor = ColorHelper.darker(frameColor, 5);
                    Color frameLoColor = ColorHelper.darker(frameColor, 30);
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

        public InternalFrameBorder() {
            insets.top = 3;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            boolean active = isActive(c);
            int th = getTitleHeight(c);
            Color titleColor = ColorHelper.brighter(AbstractLookAndFeel.getWindowInactiveTitleColorLight(), 10);
            Color borderColor = AbstractLookAndFeel.getWindowInactiveTitleColorLight();
            Color frameColor = ColorHelper.darker(AbstractLookAndFeel.getWindowInactiveBorderColor(), 10);
            if (active) {
                titleColor = AbstractLookAndFeel.getWindowTitleColorLight();
                borderColor = AbstractLookAndFeel.getWindowTitleColorLight();
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

} // class AcrylBorders

