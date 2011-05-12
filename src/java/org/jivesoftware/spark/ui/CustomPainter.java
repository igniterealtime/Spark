/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
