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
package org.jivesoftware.spark.component.panes;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.ColorUtil;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * Internal implementation of the Title pane in the northern region of a CollapsiblePane.
 *
 * @author Derek DeMoro
 */
public class CollapsibleTitlePane extends BaseCollapsibleTitlePane {
	private static final long serialVersionUID = 2528585101535037612L;
	private JLabel titleLabel;
    private JLabel iconLabel;

    private JLabel preIconLabel;

    private boolean collapsed;

    private Color startColor;
    private Color endColor;

    private Color titleColor;

    private boolean subPane;

    private Image backgroundImage;

    public CollapsibleTitlePane() {
        setLayout(new GridBagLayout());

        titleColor = new Color(33, 93, 198);
        Font titleFont = new Font("Dialog", Font.BOLD, 11);

        // Initialize color
        startColor = new Color(238,242,253);
        endColor = Color.white;
        titleLabel = new JLabel();
        iconLabel = new JLabel();

        preIconLabel = new JLabel();

        add(preIconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 2, 0), 0, 0));
        add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 2), 0, 0));
        add(iconLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 0, 2), 0, 0));

        setCollapsed(false);

        Color customTitleColor = (Color)UIManager.get("CollapsiblePane.titleColor");
        if (customTitleColor != null) {
            titleLabel.setForeground(customTitleColor);
        }
        else {
            titleLabel.setForeground(titleColor);
        }

        titleLabel.setFont(titleFont);

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setCursor(GraphicUtils.HAND_CURSOR);
            }

            public void mouseExited(MouseEvent e) {
                setCursor(GraphicUtils.DEFAULT_CURSOR);
            }
        });

        // Handle Custom Spark Job.
//        if (false) {
//            titleColor = getColor(Default.getString(Default.TEXT_COLOR));
//            String start = Default.getString(Default.CONTACT_GROUP_START_COLOR);
//            String end = Default.getString(Default.CONTACT_GROUP_END_COLOR);
//            startColor = getColor(start);
//            endColor = getColor(end);
//        }


    }

    public void setStartColor(Color color) {
        // Initialize color
        startColor = color;
    }

    public void setEndColor(Color color) {
        endColor = color;
    }

    public void setIcon(Icon icon) {
        titleLabel.setIcon(icon);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;

        if (!isSubPane()) {

            if (!collapsed) {
                preIconLabel.setIcon(SparkRes.getImageIcon(SparkRes.PANE_DOWN_ARROW_IMAGE));
            }
            else {
                preIconLabel.setIcon(SparkRes.getImageIcon(SparkRes.PANE_UP_ARROW_IMAGE));
            }
        }
        else {
            iconLabel.setIcon(null);
            if (collapsed) {
                preIconLabel.setIcon(SparkRes.getImageIcon(SparkRes.PLUS_SIGN));
            }
            else {
                preIconLabel.setIcon(SparkRes.getImageIcon(SparkRes.MINUS_SIGN));
            }
        }
    }

    public void setTitleColor(Color color) {
        titleColor = color;

        titleLabel.setForeground(color);
    }

    public Color getTitleColor() {
        return titleColor;
    }


    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
            double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
            AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
            ((Graphics2D)g).drawImage(backgroundImage, xform, this);
            return;
        }

        Color stopColor = endColor;
        Color starterColor = startColor;

        Color customStartColor = (Color)UIManager.get("CollapsiblePane.startColor");
        Color customEndColor = (Color)UIManager.get("CollapsiblePane.endColor");

        if (customEndColor != null) {
            stopColor = customEndColor;
        }

        if (customStartColor != null) {
            starterColor = customStartColor;
        }

        if (isSubPane()) {
            stopColor = ColorUtil.lighter(stopColor, 0.05);
        }

        Graphics2D g2 = (Graphics2D)g;

        int w = getWidth();
        int h = getHeight();

        GradientPaint gradient = new GradientPaint(0, 0, starterColor, w, h, stopColor, true);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);
    }

    protected boolean isSubPane() {
        return subPane;
    }

    @Override
    public void setSubPane(boolean subPane) {
        this.subPane = subPane;
        setCollapsed(isCollapsed());
    }

    public static Color getColor(String commaColorString) {
        Color color;
        try {
            StringTokenizer tkn = new StringTokenizer(commaColorString, ",");
            color = new Color(Integer.parseInt(tkn.nextToken()), Integer.parseInt(tkn.nextToken()), Integer.parseInt(tkn.nextToken()));
        }
        catch (NumberFormatException e1) {
            Log.error(e1);
            return Color.white;
        }
        return color;
    }

    public void setTitleForeground(Color color) {
        titleLabel.setForeground(color);
    }

    public void useImageAsBackground(Image image) {
        this.backgroundImage = image;
    }
}
