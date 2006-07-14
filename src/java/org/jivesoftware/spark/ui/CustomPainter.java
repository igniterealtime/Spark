/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

public class CustomPainter {
    /*

    public Color getCollapsiblePaneTitleForeground() {
        String start = Default.getString(Default.TEXT_COLOR);
        return getColor(start);
    }

    public Color getCollapsiblePaneFocusTitleForeground() {
        String start = Default.getString(Default.HOVER_TEXT_COLOR);
        return getColor(start);
    }

    public Color getBackgroundLt() {
        String start = Default.getString(Default.TAB_START_COLOR);
        return getColor(start);
    }

    public Color getBackgroundDk() {
        String start = Default.getString(Default.TAB_END_COLOR);
        return getColor(start);
    }

    public Color getColor(Object object) {
        return Color.pink;
    }

    public Color getCollapsiblePaneContentBackground() {
        return Color.pink;
    }


    public Color getCollapsiblePaneTitleForegroundEmphasized() {
        return Color.DARK_GRAY;
    }

    public Color getCollapsiblePaneFocusTitleForegroundEmphasized() {
        return Color.DARK_GRAY;
    }


    public void paintCollapsiblePaneTitlePaneBackground(JComponent jComponent, Graphics graphics, Rectangle rectangle, int i, int i1) {
        String start = Default.getString(Default.CONTACT_GROUP_START_COLOR);
        String end = Default.getString(Default.CONTACT_GROUP_END_COLOR);
        Color startColor = getColor(start);
        Color endColor = getColor(end);


        Graphics2D g2d = (Graphics2D)graphics;
        // A non-cyclic gradient
        GradientPaint gradient = new GradientPaint(0, 0, startColor, (float)rectangle.getWidth(), (float)rectangle.getHeight(), endColor);
        g2d.setPaint(gradient);

        // A cyclic gradient
        g2d.setPaint(gradient);
        g2d.fill(rectangle);
    }

    public void paintCollapsiblePanesBackground(JComponent jComponent, Graphics graphics, Rectangle rectangle, int i, int i1) {
        Color startColor = Color.gray;
        Color endColor = Color.white;

        Graphics2D g2d = (Graphics2D)graphics;
        // A non-cyclic gradient
        GradientPaint gradient = new GradientPaint(0, 0, startColor, (float)rectangle.getWidth(), (float)rectangle.getHeight(), endColor);
        g2d.setPaint(gradient);

        // A cyclic gradient
        g2d.setPaint(gradient);
        g2d.fill(rectangle);
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
    }*/

}
