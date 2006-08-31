/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import javax.swing.Icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

/**
 * Displays a bar graph based on a percentage or relevance up to 100%.
 *
 * @author Derek DeMoro
 */
public class ProgressIcon implements Icon {
    private int percent;

    /**
     * Create new ProgressIcon.
     *
     * @param percent the percentage to display.
     */
    public ProgressIcon(int percent) {
        this.percent = percent;
    }

    public int getIconHeight() {
        return 10;
    }

    public int getIconWidth() {
        return percent;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.blue);
        g.fillRect(x, y, getIconWidth(), getIconHeight());//To change body of implemented methods use File | Settings | File Templates.
    }
}
