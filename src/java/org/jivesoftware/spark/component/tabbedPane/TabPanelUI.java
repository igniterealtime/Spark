/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.tabbedPane;


import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * Represents a single instance of a Tab Paint Component.
 *
 * @author Derek DeMoro
 */
public class TabPanelUI extends BasicPanelUI {
    private Color backgroundColor = new Color(0, 0, 0, 0);

    private boolean selected;
    private boolean hideBorder;

    private int placement = JTabbedPane.TOP;


    private Color fillerColor;
    private Color border;

    // ------------------------------------------------------------------------------------------------------------------
    //  Custom installation methods
    // ------------------------------------------------------------------------------------------------------------------

    protected void installDefaults(JPanel p) {
        p.setOpaque(false);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            backgroundColor = getSelectedEndColor();
        }
        else {
            backgroundColor = new Color(0, 0, 0, 0);
        }

        this.selected = selected;
    }

    // ------------------------------------------------------------------------------------------------------------------
    //  Custom painting methods
    // ------------------------------------------------------------------------------------------------------------------

    public void paint(Graphics g, JComponent c) {
        Color borderColor = getBorderColor();
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Insets vInsets = c.getInsets();

        int w = c.getWidth() - (vInsets.left + vInsets.right);
        int h = c.getHeight() - (vInsets.top + vInsets.bottom);

        int x = vInsets.left;
        int y = vInsets.top;
        int arc = 8;


        Shape vButtonShape = new RoundRectangle2D.Double((double)x, (double)y, (double)w, (double)h, (double)arc, (double)arc);
        Shape vOldClip = g.getClip();

        if (!Spark.isMac()) {
            //   g2d.setClip(vButtonShape);
        }
        // g2d.setColor(backgroundColor);
        BufferedImage theImage = null;

        if (selected) {
            try {
                theImage = GraphicUtils.convert(Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                theImage = GraphicUtils.convert(SparkRes.getImageIcon(SparkRes.BLANK_24x24).getImage());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        g2d.setPaint(new TexturePaint(theImage, new Rectangle(x, y, w, h)));
        g2d.fillRect(x, y, w, h);

        g2d.setClip(vOldClip);
        GradientPaint vPaint = new GradientPaint(x, y, borderColor, x, y + h, borderColor);
        g2d.setPaint(vPaint);

        // Handle custom actions.
        if (placement == JTabbedPane.TOP) {
            if (selected) {
                g2d.setColor(borderColor);
                g2d.drawRect(x, y, w, h);
            }
        }
        else {
            if (selected) {
                g2d.setColor(borderColor);
                g2d.drawLine(w - 1, 0, w - 1, h);
                g2d.drawLine(x, y, x, h);
                g2d.drawLine(0, h - 1, w - 1, h - 1);
            }
        }

        if (selected) {
            // Draw border on right side.
            g2d.setColor(borderColor);
            g2d.drawLine(w - 1, 0, w - 1, h);
        }
        else if (!hideBorder) {
            // Draw border on right side.
            g2d.setColor(borderColor);
            g2d.drawLine(w - 1, 4, w - 1, h - 4);
        }
    }


    public void setHideBorder(boolean hide) {
        hideBorder = hide;
    }


    private Color getSelectedStartColor() {
        Color uiStartColor = (Color)UIManager.get("SparkTabbedPane.startColor");
        if (uiStartColor != null) {
            return uiStartColor;
        }

        if (Spark.isCustomBuild()) {
            String end = Default.getString(Default.CONTACT_GROUP_END_COLOR);
            return getColor(end);
        }
        else {
            return new Color(193, 216, 248);
        }
    }


    private Color getSelectedEndColor() {
        if (fillerColor != null) {
            return fillerColor;
        }

        Color uiEndColor = (Color)UIManager.get("SparkTabbedPane.endColor");
        if (uiEndColor != null) {
            return uiEndColor;
        }

        if (Spark.isCustomBuild()) {
            String end = Default.getString(Default.CONTACT_GROUP_END_COLOR);
            return getColor(end);
        }
        else {
            return new Color(180, 207, 247);
        }
    }

    private Color getBorderColor() {
        if (border != null) {
            return border;
        }

        Color color = (Color)UIManager.get("SparkTabbedPane.borderColor");
        if (color != null) {
            return color;
        }

        if (Spark.isCustomBuild()) {
            String end = Default.getString(Default.CONTACT_GROUP_END_COLOR);
            return getColor(end);
        }
        else {
            return Color.lightGray;
        }
    }


    private static Color getColor(String commaColorString) {
        Color color = null;
        try {
            color = null;

            StringTokenizer tkn = new StringTokenizer(commaColorString, ",");
            color = new Color(Integer.parseInt(tkn.nextToken()), Integer.parseInt(tkn.nextToken()), Integer.parseInt(tkn.nextToken()));
        }
        catch (NumberFormatException e1) {
            Log.error(e1);
            return Color.white;
        }
        return color;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }


    public Color getFillerColor() {
        return fillerColor;
    }

    public void setFillerColor(Color fillerColor) {
        this.fillerColor = fillerColor;
    }

    public Color getBorder() {
        return border;
    }

    public void setBorder(Color border) {
        this.border = border;
    }
}

