/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.io.Serializable;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiIcons extends BaseIcons {

    private static Icon iconIcon = null;
    private static Icon maxIcon = null;
    private static Icon minIcon = null;
    private static Icon closeIcon = null;
    private static Icon radioButtonIcon = null;
    private static Icon checkBoxIcon = null;
    private static Icon treeOpenIcon = null;
    private static Icon treeClosedIcon = null;
    private static Icon upArrowIcon = null;
    private static Icon downArrowIcon = null;
    private static Icon leftArrowIcon = null;
    private static Icon rightArrowIcon = null;
    private static Icon splitterUpArrowIcon = null;
    private static Icon splitterDownArrowIcon = null;
    private static Icon splitterLeftArrowIcon = null;
    private static Icon splitterRightArrowIcon = null;
    private static Icon splitterHorBumpIcon = null;
    private static Icon splitterVerBumpIcon = null;
    private static Icon thumbHorIcon = null;
    private static Icon thumbVerIcon = null;
    private static Icon thumbHorIconRollover = null;
    private static Icon thumbVerIconRollover = null;

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
            Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
            Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
            iconIcon = new BaseIcons.IconSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(-1, -1, 0, 0));
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
            Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
            Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
            minIcon = new BaseIcons.MinSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(-1, -1, 0, 0));
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
            Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
            Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
            maxIcon = new BaseIcons.MaxSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(-1, -1, 0, 0));
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
            Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
            Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
            closeIcon = new BaseIcons.CloseSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(-1, -1, 0, 0));
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

    public static Icon getTreeControlIcon(boolean isCollapsed) {
        if (isCollapsed) {
            if (treeClosedIcon == null) {
                treeClosedIcon = new LazyImageIcon("hifi/icons/TreeClosedButton.gif");
            }
            return treeClosedIcon;
        } else {
            if (treeOpenIcon == null) {
                treeOpenIcon = new LazyImageIcon("hifi/icons/TreeOpenButton.gif");
            }
            return treeOpenIcon;
        }
    }

    public static Icon getMenuArrowIcon() {
        return getRightArrowIcon();
    }

    public static Icon getComboBoxIcon() {
        return getDownArrowIcon();
    }

    public static Icon getSplitterUpArrowIcon() {
        if (splitterUpArrowIcon == null) {
            splitterUpArrowIcon = new LazyImageIcon("hifi/icons/SplitterUpArrow.gif");
        }
        return splitterUpArrowIcon;
    }

    public static Icon getSplitterDownArrowIcon() {
        if (splitterDownArrowIcon == null) {
            splitterDownArrowIcon = new LazyImageIcon("hifi/icons/SplitterDownArrow.gif");
        }
        return splitterDownArrowIcon;
    }

    public static Icon getSplitterLeftArrowIcon() {
        if (splitterLeftArrowIcon == null) {
            splitterLeftArrowIcon = new LazyImageIcon("hifi/icons/SplitterLeftArrow.gif");
        }
        return splitterLeftArrowIcon;
    }

    public static Icon getSplitterRightArrowIcon() {
        if (splitterRightArrowIcon == null) {
            splitterRightArrowIcon = new LazyImageIcon("hifi/icons/SplitterRightArrow.gif");
        }
        return splitterRightArrowIcon;
    }

    public static Icon getSplitterHorBumpIcon() {
        if (splitterHorBumpIcon == null) {
            splitterHorBumpIcon = new LazyImageIcon("hifi/icons/SplitterHorBumps.gif");
        }
        return splitterHorBumpIcon;
    }

    public static Icon getSplitterVerBumpIcon() {
        if (splitterVerBumpIcon == null) {
            splitterVerBumpIcon = new LazyImageIcon("hifi/icons/SplitterVerBumps.gif");
        }
        return splitterVerBumpIcon;
    }

    public static Icon getThumbHorIcon() {
        if (thumbHorIcon == null) {
            thumbHorIcon = new LazyImageIcon("hifi/icons/thumb_hor.gif");
        }
        return thumbHorIcon;
    }

    public static Icon getThumbVerIcon() {
        if (thumbVerIcon == null) {
            thumbVerIcon = new LazyImageIcon("hifi/icons/thumb_ver.gif");
        }
        return thumbVerIcon;
    }

    public static Icon getThumbHorIconRollover() {
        if (thumbHorIconRollover == null) {
            thumbHorIconRollover = new LazyImageIcon("hifi/icons/thumb_hor_rollover.gif");
        }
        return thumbHorIconRollover;
    }

    public static Icon getThumbVerIconRollover() {
        if (thumbVerIconRollover == null) {
            thumbVerIconRollover = new LazyImageIcon("hifi/icons/thumb_ver_rollover.gif");
        }
        return thumbVerIconRollover;
    }
    //--------------------------------------------------------------------------------------------------------

    private static class CheckBoxIcon implements Icon, UIResource, Serializable {

        private static Icon checkIcon = new LazyImageIcon("hifi/icons/CheckSymbol.gif");
        private static Icon checkPressedIcon = new LazyImageIcon("hifi/icons/CheckPressedSymbol.gif");
        private static Icon baseCheckIcon = new LazyImageIcon("icons/CheckSymbol.gif");

        private static final Color hiColor = new Color(120, 120, 120);
        private static final Color medColor = new Color(96, 96, 96);
        private static final Color lowColor = new Color(32, 32, 32);
        private final int WIDTH = 17;
        private final int HEIGHT = 17;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 4;
            }

            g.translate(x, y);

            JCheckBox cb = (JCheckBox) c;
            ButtonModel model = cb.getModel();
            Graphics2D g2D = (Graphics2D) g;

            boolean isRollover = cb.isRolloverEnabled() && model.isRollover();
            Color colors[] = null;
            if (cb.isEnabled()) {
                if (isRollover) {
                    colors = AbstractLookAndFeel.getTheme().getRolloverColors();
                } else if (model.isPressed()) {
                    colors = AbstractLookAndFeel.getTheme().getPressedColors();
                } else {
                    colors = AbstractLookAndFeel.getTheme().getButtonColors();
                }
            } else {
                colors = AbstractLookAndFeel.getTheme().getDisabledColors();
            }
            JTattooUtilities.fillHorGradient(g, colors, 1, 1, WIDTH - 1, HEIGHT - 1);


            g.setColor(hiColor);
            g.drawLine(1, 0, WIDTH - 3, 0);
            g.drawLine(0, 1, 0, HEIGHT - 3);
            g.setColor(medColor);
            g.drawLine(WIDTH - 2, 1, WIDTH - 2, HEIGHT - 3);
            g.drawLine(1, HEIGHT - 2, WIDTH - 3, HEIGHT - 2);

            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2D.setComposite(alpha);
            g2D.setColor(lowColor);
            g.drawLine(1, 1, WIDTH - 3, 1);
            g.drawLine(1, 2, 1, HEIGHT - 3);
            g.setColor(Color.black);
            g.drawLine(WIDTH - 1, 1, WIDTH - 1, HEIGHT - 1);
            g.drawLine(1, HEIGHT - 1, WIDTH - 1, HEIGHT - 1);
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
            g2D.setComposite(alpha);
            g.drawLine(1, HEIGHT - 2, 2, HEIGHT - 1);
            g2D.setComposite(composite);

            int xi = ((WIDTH - checkIcon.getIconWidth()) / 2);
            int yi = ((HEIGHT - checkIcon.getIconHeight()) / 2);
            if (model.isPressed() && model.isArmed()) {
                checkPressedIcon.paintIcon(c, g, xi, yi);
            } else if (model.isSelected()) {
                if (ColorHelper.getGrayValue(AbstractLookAndFeel.getButtonForegroundColor()) > 128) {
                    checkIcon.paintIcon(c, g, xi, yi);
                } else {
                    baseCheckIcon.paintIcon(c, g, xi, yi);
                }
            }
            g.translate(-x, -y);
        }

        public int getIconWidth() {
            return WIDTH + 4;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }

    private static class RadioButtonIcon implements Icon, UIResource, Serializable {

        private static Icon radioIcon = new LazyImageIcon("hifi/icons/RadioSymbol.gif");
        private static Icon baseRadioIcon = new LazyImageIcon("icons/RadioSymbol.gif");
        private static final Color hiColor = new Color(164, 164, 164);
        private final int WIDTH = 16;
        private final int HEIGHT = 16;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 4;
            }

            Graphics2D g2D = (Graphics2D) g;
            JRadioButton rb = (JRadioButton) c;
            ButtonModel model = rb.getModel();
            boolean isRollover = rb.isRolloverEnabled() && model.isRollover();
            Color colors[] = null;
            if (rb.isEnabled()) {
                if (model.isPressed()) {
                    colors = AbstractLookAndFeel.getTheme().getPressedColors();
                } else if (isRollover) {
                    colors = AbstractLookAndFeel.getTheme().getRolloverColors();
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

            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (ColorHelper.getGrayValue(AbstractLookAndFeel.getButtonForegroundColor()) > 128) {
                g.setColor(hiColor);
                g.drawOval(x, y, WIDTH, HEIGHT);
            } else {
                g.setColor(AbstractLookAndFeel.getFrameColor());
                g.drawOval(x - 1, y - 1, WIDTH + 2, HEIGHT + 2);
                g.drawOval(x, y, WIDTH, HEIGHT);
            }
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);

            if (model.isSelected()) {
                int xi = x + ((WIDTH - radioIcon.getIconWidth()) / 2) + 1;
                int yi = y + ((HEIGHT - radioIcon.getIconHeight()) / 2) + 1;
                if (ColorHelper.getGrayValue(AbstractLookAndFeel.getButtonForegroundColor()) > 128) {
                    radioIcon.paintIcon(c, g, xi, yi);
                } else {
                    baseRadioIcon.paintIcon(c, g, xi, yi);
                }
            }

        }

        public int getIconWidth() {
            return WIDTH + 4;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }

    public static Icon getUpArrowIcon() {
        if (upArrowIcon == null) {
            upArrowIcon = new LazyImageIcon("hifi/icons/UpArrow.gif");
        }
        return upArrowIcon;
    }

    public static Icon getDownArrowIcon() {
        if (downArrowIcon == null) {
            downArrowIcon = new LazyImageIcon("hifi/icons/DownArrow.gif");
        }
        return downArrowIcon;
    }

    public static Icon getLeftArrowIcon() {
        if (leftArrowIcon == null) {
            leftArrowIcon = new LazyImageIcon("hifi/icons/LeftArrow.gif");
        }
        return leftArrowIcon;
    }

    public static Icon getRightArrowIcon() {
        if (rightArrowIcon == null) {
            rightArrowIcon = new LazyImageIcon("hifi/icons/RightArrow.gif");
        }
        return rightArrowIcon;
    }
}
