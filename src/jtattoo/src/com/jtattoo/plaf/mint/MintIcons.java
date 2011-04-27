/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mint;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class MintIcons extends BaseIcons {

    private static Icon iconIcon = null;
    private static Icon minIcon = null;
    private static Icon maxIcon = null;
    private static Icon closeIcon = null;

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            iconIcon = new TitleButtonIcon(TitleButtonIcon.ICON_ICON_TYP);
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            minIcon = new TitleButtonIcon(TitleButtonIcon.MIN_ICON_TYP);
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            maxIcon = new TitleButtonIcon(TitleButtonIcon.MAX_ICON_TYP);
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            closeIcon = new TitleButtonIcon(TitleButtonIcon.CLOSE_ICON_TYP);
        }
        return closeIcon;
    }

//------------------------------------------------------------------------------    
    private static class TitleButtonIcon implements Icon {

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

            boolean isActive = JTattooUtilities.isActive(b);
            boolean isPressed = b.getModel().isPressed();
            boolean isArmed = b.getModel().isArmed();
            boolean isRollover = b.getModel().isRollover();

            Color cHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowTitleColorLight(), 40);
            Color cLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 10);
            if (iconTyp == CLOSE_ICON_TYP) {
                cHi = closerColorLight;
                cLo = closerColorDark;
            }

            Color fcHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 80);
            Color fcLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 40);

            if (!isActive) {
                cHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorLight(), 40);
                cLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorDark(), 10);
                fcHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorLight(), 60);
                fcLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorDark(), 10);
            }
            if (isPressed && isArmed) {
                Color cTemp = ColorHelper.darker(cLo, 10);
                cLo = ColorHelper.darker(cHi, 10);
                cHi = cTemp;
            } else if (isRollover) {
                cHi = ColorHelper.brighter(cHi, 30);
                cLo = ColorHelper.brighter(cLo, 30);
            }

            Shape savedClip = g.getClip();
            Area area = new Area(new RoundRectangle2D.Double(1, 1, w - 1, h - 1, 3, 3));
            g2D.setClip(area);

            g2D.setPaint(new GradientPaint(0, 0, fcLo, w, h, fcHi));
            g.fillRect(1, 1, w - 1, h - 1);

            g2D.setPaint(new GradientPaint(0, 0, ColorHelper.brighter(cHi, 80), w, h, ColorHelper.darker(cLo, 30)));
            g.fillRect(2, 2, w - 3, h - 3);

            g2D.setPaint(new GradientPaint(0, 0, cHi, w, h, cLo));
            g.fillRect(3, 3, w - 5, h - 5);

            g2D.setClip(savedClip);

            cHi = Color.white;
            cLo = ColorHelper.darker(cLo, 30);
            Icon icon = null;
            if (iconTyp == ICON_ICON_TYP) {
                icon = new BaseIcons.IconSymbol(cHi, cLo, null);
            } else if (iconTyp == MIN_ICON_TYP) {
                icon = new BaseIcons.MinSymbol(cHi, cLo, null);
            } else if (iconTyp == MAX_ICON_TYP) {
                icon = new BaseIcons.MaxSymbol(cHi, cLo, null);
            } else if (iconTyp == CLOSE_ICON_TYP) {
                icon = new BaseIcons.CloseSymbol(cHi, cLo, null);
            }
            if (icon != null) {
                icon.paintIcon(c, g, 0, 0);
            }
        }
    }
}
