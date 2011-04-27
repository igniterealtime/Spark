package com.jtattoo.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Michael Hagen
 */
public class LazyImageIcon implements Icon {

    private String name = null;
    private Icon icon = null;

    public LazyImageIcon(String name) {
        this.name = name;
    }

    private Icon getIcon() {
        if (icon == null) {
            try {
                icon = new ImageIcon(LazyImageIcon.class.getResource(name));
            } catch (Throwable t) {
                System.out.println("ERROR: loading image " + name + " failed!");
            }
        }
        return icon;
    }

    public int getIconHeight() {
        if (getIcon() != null)
            return getIcon().getIconHeight();
        else
            return 16;
    }

    public int getIconWidth() {
        if (getIcon() != null)
            return getIcon().getIconWidth();
        else
            return 16;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (getIcon() != null) {
            getIcon().paintIcon(c, g, x, y);
        }
        else {
            g.setColor(Color.red);
            g.fillRect(x, y, 16, 16);
            g.setColor(Color.white);
            g.drawLine(x, y, x + 15, y + 15);
            g.drawLine(x + 15, y, x, y + 15);
        }
    }


}
