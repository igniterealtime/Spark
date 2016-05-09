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
 
package com.jtattoo.plaf.hifi;

import com.jtattoo.plaf.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class HiFiIcons extends BaseIcons {

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                iconIcon = new MacIconIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                iconIcon = new BaseIcons.IconSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(-1, -1, 0, 0));
            }
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                minIcon = new MacMinIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                minIcon = new BaseIcons.MinSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(-1, -1, 0, 0));
            }
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                maxIcon = new MacMaxIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                maxIcon = new BaseIcons.MaxSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(-1, -1, 0, 0));
            }
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                closeIcon = new MacCloseIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                closeIcon = new BaseIcons.CloseSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(-1, -1, 0, 0));
            }
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
        if (menuArrowIcon == null) {
            menuArrowIcon = new LazyMenuArrowImageIcon("hifi/icons/RightArrow.gif", "hifi/icons/LeftArrow.gif");
        }
        return menuArrowIcon;
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

        private final int WIDTH = 17;
        private final int HEIGHT = 17;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 4;
            }

            g.translate(x, y);

            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Graphics2D g2D = (Graphics2D) g;

            boolean isRollover = button.isRolloverEnabled() && model.isRollover();
            Color colors[] = null;
            if (button.isEnabled()) {
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

            Color hiFrameColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getButtonBackgroundColor(), 14);
            Color frameColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getButtonBackgroundColor(), 6);
            Color loFrameColor = ColorHelper.darker(AbstractLookAndFeel.getTheme().getButtonBackgroundColor(), 50);

            g.setColor(hiFrameColor);
            g.drawLine(1, 0, WIDTH - 3, 0);
            g.drawLine(0, 1, 0, HEIGHT - 3);
            g.setColor(frameColor);
            g.drawLine(WIDTH - 2, 1, WIDTH - 2, HEIGHT - 3);
            g.drawLine(1, HEIGHT - 2, WIDTH - 3, HEIGHT - 2);

            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2D.setComposite(alpha);
            g2D.setColor(loFrameColor);
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
        private final int WIDTH = 16;
        private final int HEIGHT = 16;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 4;
            }

            Graphics2D g2D = (Graphics2D) g;
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            boolean isRollover = button.isRolloverEnabled() && model.isRollover();
            Color colors[] = null;
            if (button.isEnabled()) {
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
            Color hiFrameColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getButtonBackgroundColor(), 20);
            Color loFrameColor = ColorHelper.darker(AbstractLookAndFeel.getTheme().getButtonBackgroundColor(), 60);

            Shape savedClip = g.getClip();
            Area clipArea = new Area(new Ellipse2D.Double(x, y, WIDTH + 1, HEIGHT + 1));
            clipArea.intersect(new Area(savedClip));
            g2D.setClip(clipArea);
            JTattooUtilities.fillHorGradient(g, colors, x, y, WIDTH, HEIGHT);
            g2D.setClip(savedClip);

            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (ColorHelper.getGrayValue(AbstractLookAndFeel.getButtonForegroundColor()) > 128) {
                g.setColor(hiFrameColor);
                g.drawOval(x, y, WIDTH, HEIGHT);
            } else {
                g.setColor(loFrameColor);
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
