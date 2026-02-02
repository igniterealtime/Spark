/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.plugin.jingle;

import org.jivesoftware.spark.component.FileDragLabel;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;

/**
 * UI for simple chat room notifications with Jingle.
 */
public class GenericNotification extends JPanel {

    private static final long serialVersionUID = -90291335105747619L;
    private final FileDragLabel imageLabel = new FileDragLabel();
    private final JLabel titleLabel = new JLabel();

    /**
     * Creates a generic notification panel.
     *
     * @param title the title of the notification.
     * @param icon  the icon to use in the notification.
     */
    public GenericNotification(String title, Icon icon) {
        setLayout(new GridBagLayout());
        setBackground(new Color(250, 249, 242));
        Insets insets = new Insets(5, 5, 5, 5);
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0, 0, NORTHWEST, NONE, insets, 0, 0));
        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1, 0, NORTHWEST, NONE, insets, 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));

        titleLabel.setText(title);
        imageLabel.setIcon(icon);
    }

    /**
     * Sets the title of the notification.
     *
     * @param title the title.
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Sets the icon.
     *
     * @param icon the icon.
     */
    public void setIcon(Icon icon) {
        imageLabel.setIcon(icon);
    }


    /**
     * Changes the background color. If the alert is true,
     * the background will reflect that the UI needs attention.
     *
     * @param alert true to notify users that their attention is needed.
     */
    public void showAlert(boolean alert) {
        if (alert) {
            titleLabel.setForeground(new Color(211, 174, 102));
            setBackground(new Color(250, 249, 242));
        } else {
            setBackground(new Color(239, 245, 250));
            titleLabel.setForeground(new Color(65, 139, 179));
        }
    }

}
