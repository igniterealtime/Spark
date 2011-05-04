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
            iconIcon = new TitleButtonIcon(new LazyImageIcon("luna/icons/iconify_active.gif"),
        	    new LazyImageIcon("luna/icons/iconify_inactive.gif"));
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            minIcon = new TitleButtonIcon(new LazyImageIcon("luna/icons/min_active.gif"),
        	    new LazyImageIcon("luna/icons/min_inactive.gif"));
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            maxIcon = new TitleButtonIcon(new LazyImageIcon("luna/icons/max_active.gif"),
        	    new LazyImageIcon("luna/icons/max_inactive.gif"));
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            closeIcon = new TitleButtonIcon(new LazyImageIcon("luna/icons/close_active.gif"),
        	    new LazyImageIcon("luna/icons/close_inactive.gif"));
        }
        return closeIcon;
    }

//------------------------------------------------------------------------------
    private static class TitleButtonIcon implements Icon {

        private Icon _active;
        private Icon _inactive;

        public TitleButtonIcon(Icon active, Icon inactive) {
       _active = active;
       _inactive = inactive;
        }

        public int getIconHeight() {
            return 20;
        }

        public int getIconWidth() {
            return 20;
        }
        
	public void paintIcon(Component c, Graphics g, int x, int y) {
	    AbstractButton btn = (AbstractButton) c;

	    Graphics2D g2D = (Graphics2D) g;
	    Color fc = Color.white;
	    Color cHi = new Color(154, 183, 250);
	    Color cLo = new Color(0, 69, 211);
	    int w = c.getWidth();
	    int h = c.getHeight();
	    g2D.setPaint(new GradientPaint(0, 0, cHi, w, h, cLo));
	    g.fillRect(1, 1, w - 2, h - 2);

	    g.setColor(fc);
	    g.drawLine(1, 0, w - 2, 0);
	    g.drawLine(0, 1, 0, h - 2);
	    g.drawLine(1, h - 1, w - 2, h - 1);
	    g.drawLine(w - 1, 1, w - 1, h - 2);
	    Composite composite = g2D.getComposite();
	    AlphaComposite alpha = AlphaComposite.getInstance(
		    AlphaComposite.SRC_OVER, 0.2f);
	    g2D.setComposite(alpha);
	    g2D.setColor(cLo);
	    g.drawLine(2, 1, w - 2, 1);
	    g.drawLine(1, 2, 1, h - 2);
	    g2D.setColor(ColorHelper.darker(cLo, 40));
	    g.drawLine(2, h - 2, w - 2, h - 2);
	    g.drawLine(w - 2, 2, w - 2, h - 2);

	    g2D.setComposite(composite);

	    ButtonModel model = btn.getModel();
	    Icon ico = _active;
	    if (JTattooUtilities.isActive(btn)) {
		if (model.isRollover()) {
		    ico = _inactive;
		}
	    } else {
		if (model.isRollover()) {
		    ico = _inactive;
		} else {
		    ico = _active;
		}
	    }
	    ico.paintIcon(c, g, 1, 1);
	}
    }
}
