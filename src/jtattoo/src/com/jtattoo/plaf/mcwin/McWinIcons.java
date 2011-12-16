/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;
import com.jtattoo.plaf.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

/**
 * @author Michael Hagen
 */
public class McWinIcons extends BaseIcons {

    private static Icon PEARL_RED_24x24 = new LazyImageIcon("mcwin/icons/pearl_red_24x24.png");
    private static Icon PEARL_YELLOW_24x24 = new LazyImageIcon("mcwin/icons/pearl_yellow_24x24.png");
    private static Icon PEARL_GREEN_24x24 = new LazyImageIcon("mcwin/icons/pearl_green_24x24.png");
    private static Icon PEARL_GREY_24x24 = new LazyImageIcon("mcwin/icons/pearl_grey_24x24.png");
    private static Icon PEARL_RED_28x28 = new LazyImageIcon("mcwin/icons/pearl_red_28x28.png");
    private static Icon PEARL_YELLOW_28x28 = new LazyImageIcon("mcwin/icons/pearl_yellow_28x28.png");
    private static Icon PEARL_GREEN_28x28 = new LazyImageIcon("mcwin/icons/pearl_green_28x28.png");
    private static Icon PEARL_GREY_28x28 = new LazyImageIcon("mcwin/icons/pearl_grey_28x28.png");
    private static Icon PEARL_RED_32x32 = new LazyImageIcon("mcwin/icons/pearl_red_32x32.png");
    private static Icon PEARL_YELLOW_32x32 = new LazyImageIcon("mcwin/icons/pearl_yellow_32x32.png");
    private static Icon PEARL_GREEN_32x32 = new LazyImageIcon("mcwin/icons/pearl_green_32x32.png");
    private static Icon PEARL_GREY_32x32 = new LazyImageIcon("mcwin/icons/pearl_grey_32x32.png");
    private static Icon ICONIZER_10x10 = new LazyImageIcon("mcwin/icons/iconizer_10x10.png");
    private static Icon ICONIZER_12x12 = new LazyImageIcon("mcwin/icons/iconizer_12x12.png");
    private static Icon MINIMIZER_10x10 = new LazyImageIcon("mcwin/icons/minimizer_10x10.png");
    private static Icon MINIMIZER_12x12 = new LazyImageIcon("mcwin/icons/minimizer_12x12.png");
    private static Icon MAXIMIZER_10x10 = new LazyImageIcon("mcwin/icons/maximizer_10x10.png");
    private static Icon MAXIMIZER_12x12 = new LazyImageIcon("mcwin/icons/maximizer_12x12.png");
    private static Icon CLOSER_10x10 = new LazyImageIcon("mcwin/icons/closer_10x10.png");
    private static Icon CLOSER_12x12 = new LazyImageIcon("mcwin/icons/closer_12x12.png");

    private static Icon iconIcon = null;
    private static Icon maxIcon = null;
    private static Icon minIcon = null;
    private static Icon closeIcon = null;
    private static Icon radioButtonIcon;
    private static Icon checkBoxIcon;
    private static Icon thumbIcon = null;
    private static Icon thumbIconRollover = null;

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            iconIcon = new IconIcon();
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            minIcon = new MinIcon();
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            maxIcon = new MaxIcon();
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            closeIcon = new CloseIcon();
        }
        return closeIcon;
    }

    public static Icon getRadioButtonIcon() {
        if (radioButtonIcon == null) {
            radioButtonIcon = new RadioButtonIcon();
        }
        return radioButtonIcon;
    }

    public static Icon getCheckBoxIcon() {
        if (checkBoxIcon == null) {
            checkBoxIcon = new CheckBoxIcon();
        }
        return checkBoxIcon;
    }

    public static Icon getThumbHorIcon() {
        if (thumbIcon == null) {
            thumbIcon = new ThumbIcon(false);
        }
        return thumbIcon;
    }

    public static Icon getThumbVerIcon() {
        if (thumbIcon == null) {
            thumbIcon = new ThumbIcon(false);
        }
        return thumbIcon;
    }

    public static Icon getThumbHorIconRollover() {
        if (thumbIconRollover == null) {
            thumbIconRollover = new ThumbIcon(true);
        }
        return thumbIconRollover;
    }

    public static Icon getThumbVerIconRollover() {
        if (thumbIconRollover == null) {
            thumbIconRollover = new ThumbIcon(true);
        }
        return thumbIconRollover;
    }

//--------------------------------------------------------------------------------------------------------
    private static class IconIcon implements Icon, UIResource {

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            int w = c.getWidth();
            int h = c.getHeight();
            Icon iconizerIcon = null;
            Icon pearlIcon = null;
            if (w <= 18) {
                iconizerIcon = ICONIZER_10x10;
                pearlIcon = PEARL_GREEN_24x24;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_24x24;
                }
            } else if (w <= 22) {
                iconizerIcon = ICONIZER_12x12;
                pearlIcon = PEARL_GREEN_28x28;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_28x28;
                }
            } else {
                iconizerIcon = ICONIZER_12x12;
                pearlIcon = PEARL_GREEN_32x32;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_32x32;
                }
            }
            x = (w - pearlIcon.getIconWidth()) / 2;
            y = (h - pearlIcon.getIconHeight()) / 2;
            pearlIcon.paintIcon(c, g, x, y);
            if (model.isRollover()) {
                x = ((w - iconizerIcon.getIconWidth()) / 2) + (w % 2);
                y = ((h - iconizerIcon.getIconHeight()) / 2) + (h % 2);
                iconizerIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconHeight() {
            return 24;
        }

        public int getIconWidth() {
            return 24;
        }
    }

    private static class MinIcon implements Icon, UIResource {

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            int w = c.getWidth();
            int h = c.getHeight();
            Icon minimizerIcon = null;
            Icon pearlIcon = null;
            if (w <= 18) {
                minimizerIcon = MINIMIZER_10x10;
                pearlIcon = PEARL_YELLOW_24x24;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_24x24;
                }
            } else if (w <= 22) {
                minimizerIcon = MINIMIZER_12x12;
                pearlIcon = PEARL_YELLOW_28x28;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_28x28;
                }
            } else {
                minimizerIcon = MINIMIZER_12x12;
                pearlIcon = PEARL_YELLOW_32x32;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_32x32;
                }
            }
            x = (w - pearlIcon.getIconWidth()) / 2;
            y = (h - pearlIcon.getIconHeight()) / 2;
            pearlIcon.paintIcon(c, g, x, y);
            if (model.isRollover()) {
                x = ((w - minimizerIcon.getIconWidth()) / 2) + (w % 2);
                y = ((h - minimizerIcon.getIconHeight()) / 2) + (h % 2);
                minimizerIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconHeight() {
            return 24;
        }

        public int getIconWidth() {
            return 24;
        }
    }

    private static class MaxIcon implements Icon, UIResource {

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            int w = c.getWidth();
            int h = c.getHeight();
            Icon maximizerIcon = null;
            Icon pearlIcon = null;
            if (w <= 18) {
                maximizerIcon = MAXIMIZER_10x10;
                pearlIcon = PEARL_YELLOW_24x24;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_24x24;
                }
            } else if (w <= 22) {
                maximizerIcon = MAXIMIZER_12x12;
                pearlIcon = PEARL_YELLOW_28x28;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_28x28;
                }
            } else {
                maximizerIcon = MAXIMIZER_12x12;
                pearlIcon = PEARL_YELLOW_32x32;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_32x32;
                }
            }
            x = (w - pearlIcon.getIconWidth()) / 2;
            y = (h - pearlIcon.getIconHeight()) / 2;
            pearlIcon.paintIcon(c, g, x, y);
            if (model.isRollover()) {
                x = ((w - maximizerIcon.getIconWidth()) / 2) + (w % 2);
                y = ((h - maximizerIcon.getIconHeight()) / 2) + (h % 2);
                maximizerIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconHeight() {
            return 24;
        }

        public int getIconWidth() {
            return 24;
        }
    }

    private static class CloseIcon implements Icon, UIResource {

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            int w = c.getWidth();
            int h = c.getHeight();
            Icon closerIcon = null;
            Icon pearlIcon = null;
            if (w <= 18) {
                closerIcon = CLOSER_10x10;
                pearlIcon = PEARL_RED_24x24;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_24x24;
                }
            } else if (w <= 22) {
                closerIcon = CLOSER_12x12;
                pearlIcon = PEARL_RED_28x28;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_28x28;
                }
            } else {
                closerIcon = CLOSER_12x12;
                pearlIcon = PEARL_RED_32x32;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_32x32;
                }
            }
            x = (w - pearlIcon.getIconWidth()) / 2;
            y = (h - pearlIcon.getIconHeight()) / 2;
            pearlIcon.paintIcon(c, g, x, y);
            if (model.isRollover()) {
                x = ((w - closerIcon.getIconWidth()) / 2) + (w % 2);
                y = ((h - closerIcon.getIconHeight()) / 2) + (h % 2);
                closerIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconHeight() {
            return 24;
        }

        public int getIconWidth() {
            return 24;
        }
    }


//--------------------------------------------------------------------------------------------------------
    private static class CheckBoxIcon implements Icon, UIResource, Serializable {

        private static Icon checkIcon = new LazyImageIcon("mcwin/icons/CheckSymbol.gif");
        private static Icon checkDisabledIcon = new LazyImageIcon("mcwin/icons/CheckSymbolDisabled.gif");
        private static Icon checkPressedIcon = new LazyImageIcon("mcwin/icons/CheckPressedSymbol.gif");

        private final static int WIDTH = 13;
        private final static int HEIGHT = 14;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 3;
            }
            JCheckBox cb = (JCheckBox) c;
            ButtonModel model = cb.getModel();
            Color colors[] = null;
            if (cb.isEnabled()) {
                if (cb.isRolloverEnabled() && model.isRollover()) {
                    colors = AbstractLookAndFeel.getTheme().getRolloverColors();
                } else if (!JTattooUtilities.isFrameActive(cb)) {
                    colors = AbstractLookAndFeel.getTheme().getInActiveColors();
                } else if (cb.isSelected()) {
                    colors = AbstractLookAndFeel.getTheme().getDefaultColors();
                } else {
                    colors = AbstractLookAndFeel.getTheme().getButtonColors();
                }
                JTattooUtilities.fillHorGradient(g, colors, x + 1, y + 1, WIDTH - 1, HEIGHT - 1);
                g.setColor(AbstractLookAndFeel.getFrameColor());
                g.drawRect(x, y, WIDTH, HEIGHT);
            } else {
                colors = AbstractLookAndFeel.getTheme().getDisabledColors();
                JTattooUtilities.fillHorGradient(g, colors, x + 1, y + 1, WIDTH - 1, HEIGHT - 1);
                g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 20));
                g.drawRect(x, y, WIDTH, HEIGHT);
            }
            if (cb.isEnabled() && !model.isRollover() && !model.isPressed() && !model.isSelected()) {
                g.setColor(Color.white);
                g.drawLine(x + 1, y + 1, x + 1, y + HEIGHT - 2);
                g.drawLine(x + WIDTH - 1, y + 1, x + WIDTH - 1, y + HEIGHT - 2);
            }
            if (model.isPressed()) {
                int xi = x + ((WIDTH - checkPressedIcon.getIconWidth()) / 2) + 1;
                int yi = y + ((HEIGHT - checkPressedIcon.getIconHeight()) / 2) + 1;
                checkPressedIcon.paintIcon(c, g, xi, yi);
            } else if (model.isSelected()) {
                int xi = x + ((WIDTH - checkIcon.getIconWidth()) / 2) + 1;
                int yi = y + ((HEIGHT - checkIcon.getIconHeight()) / 2);
                if (model.isEnabled())
                    checkIcon.paintIcon(c, g, xi + 2, yi);
                else
                    checkDisabledIcon.paintIcon(c, g, xi + 2, yi);
            }
        }

        public int getIconWidth() {
            return WIDTH + 6;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }

    private static class RadioButtonIcon implements Icon, UIResource, Serializable {

        private static Icon radioIcon = new LazyImageIcon("mcwin/icons/RadioSymbol.gif");
        private static Icon radioDisabledIcon = new LazyImageIcon("mcwin/icons/RadioSymbolDisabled.gif");

        private final static int WIDTH = 14;
        private final static int HEIGHT = 14;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 3;
            }
            Graphics2D g2D = (Graphics2D) g;
            JRadioButton cb = (JRadioButton) c;
            ButtonModel model = cb.getModel();
            Color colors[] = null;
            if (cb.isEnabled()) {
                if (cb.isRolloverEnabled() && model.isRollover() && !model.isArmed()) {
                    colors = AbstractLookAndFeel.getTheme().getRolloverColors();
                } else if (!JTattooUtilities.isFrameActive(cb)) {
                    colors = AbstractLookAndFeel.getTheme().getInActiveColors();
                } else if (cb.isSelected()) {
                    colors = AbstractLookAndFeel.getTheme().getDefaultColors();
                } else {
                    colors = AbstractLookAndFeel.getTheme().getButtonColors();
                }
            } else {
                colors = AbstractLookAndFeel.getTheme().getDisabledColors();
            }

            Shape savedClip = g.getClip();
            Area clipArea = new Area(savedClip);
            Area ellipseArea = new Area(new Ellipse2D.Double(x, y, WIDTH + 1, HEIGHT + 1));
            ellipseArea.intersect(clipArea);
            g2D.setClip(ellipseArea);
            JTattooUtilities.fillHorGradient(g, colors, x, y, WIDTH, HEIGHT);
            g2D.setClip(savedClip);

            if (cb.isEnabled()) {
                g2D.setColor(AbstractLookAndFeel.getFrameColor());
            } else {
                g2D.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 20));
            }
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.drawOval(x, y, WIDTH, HEIGHT);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);

            if (model.isSelected()) {
                int xi = x + ((WIDTH - radioIcon.getIconWidth()) / 2) + 1;
                int yi = y + ((HEIGHT - radioIcon.getIconHeight()) / 2) + 1;
                if (model.isEnabled())
                    radioIcon.paintIcon(c, g, xi, yi);
                else
                    radioDisabledIcon.paintIcon(c, g, xi, yi);
            }
        }

        public int getIconWidth() {
            return WIDTH + 4;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }

    private static class ThumbIcon implements Icon, UIResource, Serializable {

        private final static int WIDTH = 15;
        private final static int HEIGHT = 15;
        private boolean isRollover = false;

        public ThumbIcon(boolean isRollover) {
            this.isRollover = isRollover;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2D = (Graphics2D) g;
            Color colors[] = null;
            if (isRollover) {
                colors = AbstractLookAndFeel.getTheme().getRolloverColors();
            } else {
                if (McWinLookAndFeel.getTheme().isBrightMode()) {
                    colors = AbstractLookAndFeel.getTheme().getButtonColors();
                } else {
                    colors = AbstractLookAndFeel.getTheme().getSelectedColors();
                }
            }

            Shape savedClip = g2D.getClip();
            if (savedClip != null) {
                Area clipArea = new Area(savedClip);
                Area ellipseArea = new Area(new Ellipse2D.Double(x + 1, y + 1, WIDTH, HEIGHT));
                ellipseArea.intersect(clipArea);
                g2D.setClip(ellipseArea);
                JTattooUtilities.fillHorGradient(g, colors, x + 1, y + 1, WIDTH, HEIGHT);
                g2D.setClip(savedClip);
            } else {
                Area ellipseArea = new Area(new Ellipse2D.Double(x + 1, y + 1, WIDTH, HEIGHT));
                g2D.setClip(ellipseArea);
                JTattooUtilities.fillHorGradient(g, colors, x, y, WIDTH, HEIGHT);
                g2D.setClip(null);
            }
            g2D.setColor(AbstractLookAndFeel.getFrameColor());
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.drawOval(x + 1, y + 1, WIDTH - 1, HEIGHT - 1);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
        }

        public int getIconWidth() {
            return WIDTH + 2;
        }

        public int getIconHeight() {
            return HEIGHT + 2;
        }
    }
}
