/**
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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHEAST;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;

/**
 * <code>TitlePanel</code> class is the top panel displayed in this application.
 * This should be used to identify the application to users using a title, brief
 * description, and the company's logo.
 *
 * @author Derek DeMoro
 */
public final class TitlePanel extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final JLabel descriptionLabel = new JLabel();

    /**
     * Create a new TitlePanel.
     *
     * @param title the title to use with the panel.
     * @param description the panel description.
     * @param icon the icon to use with the panel.
     */
    public TitlePanel(String title, String description, Icon icon) {
        // Set the icon
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(icon);
        // Set the title
        setTitle(title);
        // Set the description
        setDescription(description);
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        
        descriptionLabel.setBackground(Color.white);
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.white.brighter());
        panel.setLayout(new GridBagLayout());
        panel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        panel.add(iconLabel, new GridBagConstraints(2, 0, 1, 2, 0, 0, NORTHEAST, NONE, new Insets(2, 2, 2, 2), 0, 0));
        panel.add(descriptionLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, NORTHWEST, HORIZONTAL, new Insets(0, 9, 5, 5), 0, 0));

        titleLabel.setVerticalTextPosition(JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        titleLabel.setForeground(Color.black);
        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(panel, new GridBagConstraints(0, 0, 1, 0, 1, 0, WEST, HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
    }

    /**
     * Set the icon for the panel.
     *
     * @param icon - the relative icon based on classpath. ex.
     * /com/jivesoftware/images/Foo.gif.
     */
    public void setIcon(Icon icon) {
        titleLabel.setIcon(icon);
    }

    /**
     * Set the main title for this panel.
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Set a brief description which will be displayed below the main title.
     */
    public void setDescription(String desc) {
        if (desc != null) {
            desc = "<HTML><BODY>" + desc + "</HTML></BODY>";
        }
        descriptionLabel.setText(desc);
    }

}
