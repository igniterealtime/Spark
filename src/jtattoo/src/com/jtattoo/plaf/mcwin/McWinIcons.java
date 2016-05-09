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
 
package com.jtattoo.plaf.mcwin;

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
public class McWinIcons extends BaseIcons {

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            iconIcon = new MacIconIcon();
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            minIcon = new MacMinIcon();
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            maxIcon = new MacMaxIcon();
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            closeIcon = new MacCloseIcon();
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
        if (thumbHorIcon == null) {
            thumbHorIcon = new ThumbIcon(false);
        }
        return thumbHorIcon;
    }

    public static Icon getThumbVerIcon() {
        if (thumbVerIcon == null) {
            thumbVerIcon = new ThumbIcon(false);
        }
        return thumbVerIcon;
    }

    public static Icon getThumbHorIconRollover() {
        if (thumbHorIconRollover == null) {
            thumbHorIconRollover = new ThumbIcon(true);
        }
        return thumbHorIconRollover;
    }

    public static Icon getThumbVerIconRollover() {
        if (thumbVerIconRollover == null) {
            thumbVerIconRollover = new ThumbIcon(true);
        }
        return thumbVerIconRollover;
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
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Color colors[] = null;
            if (button.isEnabled()) {
                if (button.isRolloverEnabled() && model.isRollover()) {
                    colors = AbstractLookAndFeel.getTheme().getRolloverColors();
                } else if (!JTattooUtilities.isFrameActive(button)) {
                    colors = AbstractLookAndFeel.getTheme().getInActiveColors();
                } else if (button.isSelected()) {
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
            if (button.isEnabled() && !model.isRollover() && !model.isPressed() && !model.isSelected()) {
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
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Color colors[] = null;
            if (button.isEnabled()) {
                if (button.isRolloverEnabled() && model.isRollover() && !model.isArmed()) {
                    colors = AbstractLookAndFeel.getTheme().getRolloverColors();
                } else if (!JTattooUtilities.isFrameActive(button)) {
                    colors = AbstractLookAndFeel.getTheme().getInActiveColors();
                } else if (button.isSelected()) {
                    colors = AbstractLookAndFeel.getTheme().getDefaultColors();
                } else {
                    colors = AbstractLookAndFeel.getTheme().getButtonColors();
                }
            } else {
                colors = AbstractLookAndFeel.getTheme().getDisabledColors();
            }

            Shape savedClip = g.getClip();
            Area clipArea = new Area(new Ellipse2D.Double(x, y, WIDTH + 1, HEIGHT + 1));
            clipArea.intersect(new Area(savedClip));
            g2D.setClip(clipArea);
            JTattooUtilities.fillHorGradient(g, colors, x, y, WIDTH, HEIGHT);
            g2D.setClip(savedClip);

            if (button.isEnabled()) {
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
                if (AbstractLookAndFeel.getTheme().isBrightMode()) {
                    colors = AbstractLookAndFeel.getTheme().getButtonColors();
                } else {
                    colors = AbstractLookAndFeel.getTheme().getSelectedColors();
                }
            }

            Shape savedClip = g2D.getClip();
            if (savedClip != null) {
                Area clipArea = new Area(new Ellipse2D.Double(x + 1, y + 1, WIDTH, HEIGHT));
                clipArea.intersect(new Area(savedClip));
                g2D.setClip(clipArea);
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
