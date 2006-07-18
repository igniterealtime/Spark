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
import org.jivesoftware.spark.util.log.Log;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicPanelUI;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.StringTokenizer;

public class TabPanelUI extends BasicPanelUI {
    private Color backgroundColor1 = Color.white;//new Color(235, 247, 223);
    private Color backgroundColor2 = Color.white;//new Color(214, 219, 191);

    private Color borderColor = new Color(86, 88, 72);
    private Color borderColorAlpha1 = new Color(86, 88, 72, 100);
    private Color borderColorAlpha2 = new Color(86, 88, 72, 50);
    private Color borderHighlight = new Color(225, 224, 224);

    private boolean selected;
    private boolean hideBorder;

    private int placement = JTabbedPane.TOP;

    // ------------------------------------------------------------------------------------------------------------------
    //  Custom installation methods
    // ------------------------------------------------------------------------------------------------------------------

    protected void installDefaults(JPanel p) {
        p.setOpaque(false);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            backgroundColor1 = getSelectedStartColor();
            backgroundColor2 = getSelectedEndColor();
        }
        else {
            backgroundColor1 = Color.white;
            backgroundColor2 = Color.white;
        }

        this.selected = selected;
    }

    // ------------------------------------------------------------------------------------------------------------------
    //  Custom painting methods
    // ------------------------------------------------------------------------------------------------------------------

    public void paint(Graphics g, JComponent c) {
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

        g2d.setClip(vButtonShape);
        g2d.setColor(backgroundColor2);
        g2d.fillRect(x, y, w, h / 2);
        g2d.setColor(backgroundColor2);
        g2d.fillRect(x, y + h / 2, w, h / 2);

        g2d.setClip(vOldClip);
        GradientPaint vPaint = new GradientPaint(x, y, borderColor, x, y + h, borderHighlight);
        g2d.setPaint(vPaint);

        if (selected) {
            g2d.setColor(Color.lightGray);
            g2d.drawRoundRect(x, y, w, h, arc, arc);
        }

        g2d.clipRect(x, y, w + 1, h - arc / 4);
        g2d.setColor(borderColorAlpha1);

        g2d.setClip(vOldClip);
        g2d.setColor(borderColorAlpha2);

        if (placement == JTabbedPane.TOP) {
            g2d.setColor(backgroundColor2);
            g2d.fillRect(x, h - 5, w, h);
        }


        if (selected) {

        }
        else if (!hideBorder) {
            // Draw border on right side.
            g2d.setColor(Color.lightGray);
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

    public void setPlacement(int placement){
        this.placement = placement;
    }
}

