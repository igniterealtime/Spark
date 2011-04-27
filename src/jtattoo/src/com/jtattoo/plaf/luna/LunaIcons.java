/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.luna;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class LunaIcons extends BaseIcons {

    private static Icon iconIcon = null;
    private static Icon maxIcon = null;
    private static Icon minIcon = null;
    private static Icon closeIcon = null;
    private static Icon comboBoxIcon;

    public static Icon getComboBoxIcon() {
        if (comboBoxIcon == null) {
            comboBoxIcon = new LazyImageIcon("luna/icons/DownArrow.gif");
        }
        return comboBoxIcon;
    }

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

        private static Color blueFrameColor = Color.white;
        private static Color blueColorLight = new Color(154, 183, 250);
        private static Color blueColorDark = new Color(0, 69, 211);
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

            Color fc = blueFrameColor;
            Color cHi = blueColorLight;
            Color cLo = blueColorDark;
            if (iconTyp == CLOSE_ICON_TYP) {
                cHi = closerColorLight;
                cLo = closerColorDark;
            }

            if (!isActive) {
                cHi = ColorHelper.brighter(cHi, 20);
                cLo = ColorHelper.brighter(cLo, 10);
            }
            if (isPressed && isArmed) {
                Color cTemp = ColorHelper.darker(cLo, 10);
                cLo = ColorHelper.darker(cHi, 10);
                cHi = cTemp;
            } else if (isRollover) {
                cHi = ColorHelper.brighter(cHi, 30);
                cLo = ColorHelper.brighter(cLo, 30);
            }

            g2D.setPaint(new GradientPaint(0, 0, cHi, w, h, cLo));
            g.fillRect(1, 1, w - 2, h - 2);

            g.setColor(fc);
            g.drawLine(1, 0, w - 2, 0);
            g.drawLine(0, 1, 0, h - 2);
            g.drawLine(1, h - 1, w - 2, h - 1);
            g.drawLine(w - 1, 1, w - 1, h - 2);
            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
            g2D.setComposite(alpha);
            g2D.setColor(cLo);
            g.drawLine(2, 1, w - 2, 1);
            g.drawLine(1, 2, 1, h - 2);
            g2D.setColor(ColorHelper.darker(cLo, 40));
            g.drawLine(2, h - 2, w - 2, h - 2);
            g.drawLine(w - 2, 2, w - 2, h - 2);

            g2D.setComposite(composite);

            // Paint the icon
            cHi = Color.white;
            cLo = ColorHelper.darker(cLo, 30);
            Icon icon = null;
            if (iconTyp == ICON_ICON_TYP) {
                icon = new BaseIcons.IconSymbol(cHi, cLo, null, new Insets(0, 0, 0, 1));
            } else if (iconTyp == MIN_ICON_TYP) {
                icon = new BaseIcons.MinSymbol(cHi, cLo, null, new Insets(0, 0, 0, 1));
            } else if (iconTyp == MAX_ICON_TYP) {
                icon = new BaseIcons.MaxSymbol(cHi, cLo, null, new Insets(0, 0, 0, 1));
            } else if (iconTyp == CLOSE_ICON_TYP) {
                icon = new BaseIcons.CloseSymbol(cHi, cLo, null, new Insets(0, 0, 0, 1));
            }
            if (icon != null) {
                icon.paintIcon(c, g, 0, 0);
            }
        }
    }
}
