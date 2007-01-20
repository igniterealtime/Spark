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

import org.jivesoftware.resource.Default;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <code>TitlePanel</code> class is the top panel displayed in this application. This
 * should be used to identify the application to users using a title, brief description,
 * and the company's logo.
 *
 * @author Derek DeMoro
 */
public final class TitlePanel extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final WrappedLabel descriptionLabel = new WrappedLabel();
    private final JLabel iconLabel = new JLabel();
    private final GridBagLayout gridBagLayout = new GridBagLayout();

    /**
     * Create a new TitlePanel.
     *
     * @param title           the title to use with the panel.
     * @param description     the panel description.
     * @param icon            the icon to use with the panel.
     * @param showDescription true if the descrption should be shown.
     */
    public TitlePanel(String title, String description, Icon icon, boolean showDescription) {

        // Set the icon
        iconLabel.setIcon(icon);

        // Set the title
        setTitle(title);

        // Set the description
        setDescription(description);

        setLayout(gridBagLayout);

        descriptionLabel.setBackground(Color.white);

        if (showDescription) {
            add(iconLabel, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(descriptionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 9, 5, 5), 0, 0));
            add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            setBackground(Color.white);

            titleLabel.setFont(new Font("dialog", Font.BOLD, 11));
            descriptionLabel.setFont(new Font("dialog", 0, 10));
        }
        else {
            final JPanel panel = new ImagePanel();
            panel.setBorder(BorderFactory.createEtchedBorder());

            panel.setLayout(new GridBagLayout());
            panel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            panel.add(iconLabel, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

            titleLabel.setVerticalTextPosition(JLabel.CENTER);
            titleLabel.setFont(new Font("dialog", Font.BOLD, 14));
            titleLabel.setForeground(Color.black);
            descriptionLabel.setFont(new Font("dialog", 0, 10));
            add(panel, new GridBagConstraints(0, 0, 1, 0, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
        }

    }


    /**
     * Set the icon for the panel.
     *
     * @param icon - the relative icon based on classpath. ex. /com/jivesoftware/images/Foo.gif.
     */
    public final void setIcon(Icon icon) {
        titleLabel.setIcon(icon);
    }

    /**
     * Set the main title for this panel.
     *
     * @param title - main title.
     */
    public final void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Set a brief description which will be displayed below the main title.
     *
     * @param desc - brief description
     */
    public final void setDescription(String desc) {
        descriptionLabel.setText(desc);
    }

    public class ImagePanel extends JPanel {
        final ImageIcon icons = Default.getImageIcon(Default.SECONDARY_BACKGROUND_IMAGE);

        public ImagePanel() {

        }

        public ImagePanel(LayoutManager layout) {
            super(layout);
        }

        public void paintComponent(Graphics g) {
            Image backgroundImage = icons.getImage();
            double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
            double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
            AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
            ((Graphics2D)g).drawImage(backgroundImage, xform, this);
        }
    }


}
