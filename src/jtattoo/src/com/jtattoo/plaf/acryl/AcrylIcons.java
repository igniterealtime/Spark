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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/**
 * @author Michael Hagen
 */
public class AcrylIcons extends BaseIcons {

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                iconIcon = new MacIconIcon();
            } else {
                iconIcon = new TitleButtonIcon(TitleButtonIcon.ICON_ICON_TYP);
            }
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                minIcon = new MacMinIcon();
            } else {
                minIcon = new TitleButtonIcon(TitleButtonIcon.MIN_ICON_TYP);
            }
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                maxIcon = new MacMaxIcon();
            } else {
                maxIcon = new TitleButtonIcon(TitleButtonIcon.MAX_ICON_TYP);
            }
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                closeIcon = new MacCloseIcon();
            } else {
                closeIcon = new TitleButtonIcon(TitleButtonIcon.CLOSE_ICON_TYP);
            }
        }
        return closeIcon;
    }

    public static Icon getTreeControlIcon(boolean isCollapsed) {
        if (!AbstractLookAndFeel.getControlColorLight().equals(new ColorUIResource(96, 98, 100))) {
            return BaseIcons.getTreeControlIcon(isCollapsed);
        }

        if (isCollapsed) {
            if (treeClosedIcon == null) {
                treeClosedIcon = new LazyImageIcon("acryl/icons/TreeClosedButton.gif");
            }
            return treeClosedIcon;
        } else {
            if (treeOpenIcon == null) {
                treeOpenIcon = new LazyImageIcon("acryl/icons/TreeOpenButton.gif");
            }
            return treeOpenIcon;
        }
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
        if (!AbstractLookAndFeel.getControlColorLight().equals(new ColorUIResource(96, 98, 100))) {
            return BaseIcons.getThumbHorIcon();
        }

        if (thumbHorIcon == null) {
            thumbHorIcon = new LazyImageIcon("acryl/icons/thumb_hor.gif");
        }
        return thumbHorIcon;
    }

    public static Icon getThumbVerIcon() {
        if (!AbstractLookAndFeel.getControlColorLight().equals(new ColorUIResource(96, 98, 100))) {
            return BaseIcons.getThumbVerIcon();
        }

        if (thumbVerIcon == null) {
            thumbVerIcon = new LazyImageIcon("acryl/icons/thumb_ver.gif");
        }
        return thumbVerIcon;
    }

    public static Icon getThumbHorIconRollover() {
        if (!AbstractLookAndFeel.getControlColorLight().equals(new ColorUIResource(96, 98, 100))) {
            return BaseIcons.getThumbHorIconRollover();
        }

        if (thumbHorIconRollover == null) {
            thumbHorIconRollover = new LazyImageIcon("acryl/icons/thumb_hor_rollover.gif");
        }
        return thumbHorIconRollover;
    }

    public static Icon getThumbVerIconRollover() {
        if (!AbstractLookAndFeel.getControlColorLight().equals(new ColorUIResource(96, 98, 100))) {
            return BaseIcons.getThumbVerIconRollover();
        }

        if (thumbVerIconRollover == null) {
            thumbVerIconRollover = new LazyImageIcon("acryl/icons/thumb_ver_rollover.gif");
        }
        return thumbVerIconRollover;
    }

//------------------------------------------------------------------------------
    private static class TitleButtonIcon implements Icon {

        private static Color extraLightGray = new Color(240, 240, 240);
        private static Color closerColorLight = new Color(241, 172, 154);
        private static Color closerColorDark = new Color(224, 56, 2);
        public static final int ICON_ICON_TYP = 0;
        public static final int MIN_ICON_TYP = 1;
        public static final int MAX_ICON_TYP = 2;
        public static final int CLOSE_ICON_TYP = 3;
        private int iconTyp = ICON_ICON_TYP;

        public TitleButtonIcon(int typ) {
            iconTyp = typ;
        }

        public int getIconHeight() {
            return 20;
        }

        public int getIconWidth() {
            return 20;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            int w = c.getWidth();
            int h = c.getHeight();

            JButton b = (JButton) c;
            Graphics2D g2D = (Graphics2D) g;

            boolean isPressed = b.getModel().isPressed();
            boolean isArmed = b.getModel().isArmed();
            boolean isRollover = b.getModel().isRollover();

            Color cFrame = AbstractLookAndFeel.getTheme().getWindowBorderColor();
            Color cFrameInner = ColorHelper.brighter(cFrame, 60);
            Color cHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowTitleColorLight(), 40);
            Color cLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 10);
            Color cShadow = Color.black;
            if (iconTyp == CLOSE_ICON_TYP) {
                cHi = closerColorLight;
                cLo = closerColorDark;
            }

            if (isPressed && isArmed) {
                Color cTemp = ColorHelper.darker(cLo, 10);
                cLo = ColorHelper.darker(cHi, 10);
                cHi = cTemp;
                g2D.setPaint(new GradientPaint(0, 0, cHi, w, h, cLo));
                g.fillRect(2, 2, w - 3, h - 3);
            } else if (isRollover) {
                cFrameInner = ColorHelper.brighter(cFrameInner, 50);
                if (iconTyp == CLOSE_ICON_TYP) {
                    cHi = closerColorLight;
                    cLo = closerColorDark;
                    cShadow = cLo;
                    g2D.setPaint(new GradientPaint(0, 0, cHi, w, h, cLo));
                    g2D.fillRect(2, 2, w - 3, h - 3);
                } else {
                    JTattooUtilities.fillHorGradient(g2D, AbstractLookAndFeel.getTheme().getRolloverColors(), 2, 2, w - 3, h - 3);
                }
            }

            g2D.setColor(cFrame);
            g2D.drawLine(1, 0, w - 2, 0);
            g2D.drawLine(1, h - 1, w - 2, h - 1);
            g2D.drawLine(0, 1, 0, h - 2);
            g2D.drawLine(w - 1, 1, w - 1, h - 2);

            g2D.setColor(cFrameInner);
            g2D.drawRect(1, 1, w - 3, h - 3);

            Icon icon = null;
            if (iconTyp == ICON_ICON_TYP) {
                icon = new BaseIcons.IconSymbol(extraLightGray, cShadow, null, new Insets(0, 5, 0, 5));
            } else if (iconTyp == MIN_ICON_TYP) {
                icon = new BaseIcons.MinSymbol(extraLightGray, cShadow, null, new Insets(0, 4, 0, 4));
            } else if (iconTyp == MAX_ICON_TYP) {
                icon = new BaseIcons.MaxSymbol(extraLightGray, cShadow, null, new Insets(0, 4, 0, 4));
            } else if (iconTyp == CLOSE_ICON_TYP) {
                icon = new BaseIcons.CloseSymbol(Color.white, ColorHelper.darker(cShadow, 50), null, new Insets(0, 5, 0, 5));
            }
            if (icon != null) {
                icon.paintIcon(c, g, 0, 0);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------
    private static class CheckBoxIcon implements Icon {

        private static Icon checkIcon = new LazyImageIcon("acryl/icons/CheckSymbol.gif");
        private static Icon checkPressedIcon = new LazyImageIcon("acryl/icons/CheckPressedSymbol.gif");
        private static Icon checkInactiveIcon = new LazyImageIcon("icons/CheckSymbol.gif");
        private static Icon checkDisabledIcon = new LazyImageIcon("icons/CheckSymbolDisabled.gif");

        private static final int WIDTH = 14;
        private static final int HEIGHT = 14;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 3;
            }
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            Color frameColor = AbstractLookAndFeel.getFrameColor();

            if (b.isEnabled()) {
                if (b.isRolloverEnabled() && model.isRollover()) {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
                    frameColor = ColorHelper.brighter(frameColor, 30);
                } else {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDefaultColors(), x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
                }

            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDisabledColors(), x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
                frameColor = ColorHelper.brighter(frameColor, 40);
            }

            g.setColor(AbstractLookAndFeel.getTheme().getControlShadowColor());
            g.drawRect(x, y, WIDTH - 1, HEIGHT - 1);
            g.setColor(frameColor);
            g.drawLine(x + 1, y, x + WIDTH - 2, y);
            g.drawLine(x + 1, y + HEIGHT - 1, x + WIDTH - 2, y + HEIGHT - 1);
            g.drawLine(x, y + 1, x, y + HEIGHT - 2);
            g.drawLine(x + WIDTH - 1, y + 1, x + WIDTH - 1, y + HEIGHT - 2);
            g.setColor(AbstractLookAndFeel.getTheme().getControlShadowColor());
            g.drawLine(x + WIDTH, y + 2, x + WIDTH, y + HEIGHT - 1);
            g.drawLine(x + 2, y + HEIGHT, x + WIDTH - 1, y + HEIGHT);

            int xi = x + ((WIDTH - checkInactiveIcon.getIconWidth()) / 2) - 1;
            int yi = y + ((HEIGHT - checkInactiveIcon.getIconHeight()) / 2) - 1;
            if (model.isPressed() && model.isArmed()) {
                checkPressedIcon.paintIcon(c, g, xi + 1, yi + 1);
            } else if (model.isSelected()) {
                if (!model.isEnabled()) {
                    checkDisabledIcon.paintIcon(c, g, xi + 1, yi + 1);
                } else {
                    int gv = 0;
                    if (model.isRollover()) {
                        gv = ColorHelper.getGrayValue(AbstractLookAndFeel.getTheme().getRolloverColorDark());
                    } else {
                        gv = ColorHelper.getGrayValue(AbstractLookAndFeel.getTheme().getControlColorDark());
                    }
                    if (gv > 128) {
                        checkInactiveIcon.paintIcon(c, g, xi + 1, yi + 1);
                    } else {
                        checkIcon.paintIcon(c, g, xi, yi);
                    }
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

    //-----------------------------------------------------------------------------------------------------------
    private static class RadioButtonIcon implements Icon {

        private static Icon radioIcon = new LazyImageIcon("acryl/icons/RadioSymbol.gif");
        private static final int WIDTH = 13;
        private static final int HEIGHT = 13;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 3;
            }
            Graphics2D g2D = (Graphics2D) g;
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            Color frameColor = AbstractLookAndFeel.getFrameColor();
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            Shape savedClip = g.getClip();
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setColor(AbstractLookAndFeel.getTheme().getControlShadowColor());
            g2D.drawOval(x + 1, y + 1, WIDTH - 1, HEIGHT - 1);
            Area clipArea = new Area(new Ellipse2D.Double(x, y, WIDTH, HEIGHT));
            clipArea.intersect(new Area(savedClip));
            g2D.setClip(clipArea);
            if (b.isEnabled()) {
                if (b.isRolloverEnabled() && model.isRollover()) {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
                    frameColor = ColorHelper.brighter(frameColor, 30);
                } else {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDefaultColors(), x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
                }

            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDisabledColors(), x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
                frameColor = ColorHelper.brighter(frameColor, 40);
            }
            g2D.setClip(savedClip);
            g2D.setColor(frameColor);
            g2D.drawOval(x, y, WIDTH - 1, HEIGHT - 1);
            if (model.isSelected()) {
                int xi = x + ((WIDTH - radioIcon.getIconWidth()) / 2);
                int yi = y + ((HEIGHT - radioIcon.getIconHeight()) / 2);
                if (!model.isEnabled()) {
                    g.setColor(Color.gray);
                    g.fillOval(x + (WIDTH / 2) - 2, y + (HEIGHT / 2) - 2, 5, 5);
                } else {
                    int gv = 0;
                    if (model.isRollover()) {
                        gv = ColorHelper.getGrayValue(AbstractLookAndFeel.getTheme().getRolloverColorDark());
                    } else {
                        gv = ColorHelper.getGrayValue(AbstractLookAndFeel.getTheme().getControlColorDark());
                    }
                    if (gv > 128) {
                        g.setColor(Color.black);
                        g.fillOval(x + (WIDTH / 2) - 2, y + (HEIGHT / 2) - 2, 5, 5);
                    } else {
                        radioIcon.paintIcon(c, g, xi, yi);
                    }
                }
            }
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
        }

        public int getIconWidth() {
            return WIDTH + 4;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }
}