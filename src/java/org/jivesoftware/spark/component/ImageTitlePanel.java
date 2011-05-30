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
package org.jivesoftware.spark.component;

import org.jivesoftware.resource.Default;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.geom.AffineTransform;

/**
 * Fancy title panel that displays gradient colors, text and components.
 */
public class ImageTitlePanel extends JPanel {
    private static final long serialVersionUID = -4942953711496567252L;
    private Image backgroundImage;
    private final JLabel titleLabel = new JLabel();
    private final JLabel iconLabel = new JLabel();
    private final GridBagLayout gridBagLayout = new GridBagLayout();
    private final WrappedLabel descriptionLabel = new WrappedLabel();

    /**
     * Creates a new ImageTitlePanel.
     *
     * @param title the title to use for this label.
     */
    public ImageTitlePanel(String title) {
        backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();

        init();

        titleLabel.setText(title);

        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
    }

    /**
     * Creates a new ImageTitlePanel object.
     */
    public ImageTitlePanel() {
        backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();

        init();

     
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
    }

    public void paintComponent(Graphics g) {
        double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
        double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
        ((Graphics2D)g).drawImage(backgroundImage, xform, this);
    }

    private void init() {
        setLayout(gridBagLayout);
        add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    /**
     * Set the description for the label.
     *
     * @param description the description for the label.
     */
    public void setDescription(String description) {
        descriptionLabel.setText(description);
        add(descriptionLabel, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    /**
     * Set the font of the description label.
     *
     * @param font the font to use in the description label.
     */
    public void setDescriptionFont(Font font) {
        descriptionLabel.setFont(font);
    }

    /**
     * Returns the description label.
     *
     * @return the description label.
     */
    public JTextArea getDescriptionLabel() {
        return descriptionLabel;
    }

    /**
     * Sets the title to use in the label.
     *
     * @param title the title to use.
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Returns the title label.
     *
     * @return the title label.
     */
    public JLabel getTitleLabel() {
        return titleLabel;
    }

    /**
     * Set the font of the title label.
     *
     * @param font the font to use for title label.
     */
    public void setTitleFont(Font font) {
        titleLabel.setFont(font);
    }

    /**
     * Specify a component to use on this label.
     *
     * @param component the component to use with this label.
     */
    public void setComponent(JComponent component) {
        add(new JLabel(),
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(component,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    /**
     * Specify the icon to use with this label.
     *
     * @param icon the icon to use with this label.
     */
    public void setIcon(ImageIcon icon) {
        add(new JLabel(),
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        iconLabel.setIcon(icon);
        add(iconLabel,
                new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }
}