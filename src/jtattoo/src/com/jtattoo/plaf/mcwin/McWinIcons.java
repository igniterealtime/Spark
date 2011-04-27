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

    private static final Color foreColor = new Color(32, 32, 32);
    private static final Color shadowColor = new Color(240, 240, 240);
    private static final Color rolloverColor = new Color(164, 0, 0);
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
            iconIcon = new TitleButtonIcon(
                    new LazyImageIcon("mcwin/icons/pearl_green.gif"),
                    new LazyImageIcon("mcwin/icons/iconizer.gif"),
                    new LazyImageIcon("mcwin/icons/pearl_white.gif"),
                    new LazyImageIcon("mcwin/icons/iconizer_inactive.gif"));
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            minIcon = new TitleButtonIcon(
                    new LazyImageIcon("mcwin/icons/pearl_orange.gif"),
                    new LazyImageIcon("mcwin/icons/minimizer.gif"),
                    new LazyImageIcon("mcwin/icons/pearl_white.gif"),
                    new LazyImageIcon("mcwin/icons/minimizer_inactive.gif"));
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            maxIcon = new TitleButtonIcon(
                    new LazyImageIcon("mcwin/icons/pearl_orange.gif"),
                    new LazyImageIcon("mcwin/icons/maximizer.gif"),
                    new LazyImageIcon("mcwin/icons/pearl_white.gif"),
                    new LazyImageIcon("mcwin/icons/maximizer_inactive.gif"));
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            closeIcon = new TitleButtonIcon(
                    new LazyImageIcon("mcwin/icons/pearl_red.gif"),
                    new LazyImageIcon("mcwin/icons/closer.gif"),
                    new LazyImageIcon("mcwin/icons/pearl_white.gif"),
                    new LazyImageIcon("mcwin/icons/closer_inactive.gif"));
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
    private static class TitleButtonIcon implements Icon, UIResource {

        private Icon icon = null;
        private Icon rolloverIcon = null;
        private Icon inactiveIcon = null;
        private Icon inactiveRolloverIcon = null;

        public TitleButtonIcon(Icon icon,
                Icon rolloverIcon,
                Icon inactiveIcon,
                Icon inactiveRolloverIcon) {
            this.icon = icon;
            this.rolloverIcon = rolloverIcon;
            this.inactiveIcon = inactiveIcon;
            this.inactiveRolloverIcon = inactiveRolloverIcon;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            Icon ico = icon;
            if (JTattooUtilities.isActive(btn)) {
                if (model.isRollover()) {
                    ico = rolloverIcon;
                }
            } else {
                if (model.isRollover()) {
                    ico = inactiveRolloverIcon;
                } else {
                    ico = inactiveIcon;
                }
            }
            ico.paintIcon(c, g, x, y);
        }

        public int getIconWidth() {
            return icon.getIconWidth();
        }

        public int getIconHeight() {
            return icon.getIconHeight();
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
                colors = AbstractLookAndFeel.getTheme().getSelectedColors();
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
