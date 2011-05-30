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

import org.jivesoftware.resource.SparkRes;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Creates a Firefox Search type box that allows for icons inside of a textfield. This
 * could be used to build out your own search objects.
 */
public class IconTextField extends JPanel {
    private static final long serialVersionUID = -7000758637988415370L;
    private JTextField textField;
    private JLabel imageComponent;
    private JLabel downOption;

    /**
     * Creates a new IconTextField with Icon.
     *
     * @param icon the icon.
     */
    public IconTextField(Icon icon) {
        setLayout(new GridBagLayout());
        setBackground((Color)UIManager.get("TextField.background"));

        textField = new JTextField();
        textField.setBorder(null);
        setBorder(new JTextField().getBorder());

        imageComponent = new JLabel(icon);
        downOption = new JLabel(SparkRes.getImageIcon(SparkRes.DOWN_OPTION_IMAGE));

        add(downOption, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(imageComponent, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(textField, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));

        downOption.setVisible(false);
    }

    public void enableDropdown(boolean enable) {
        downOption.setVisible(enable);
    }

    /**
     * Sets the text of the textfield.
     *
     * @param text the text.
     */
    public void setText(String text) {
        textField.setText(text);
    }

    /**
     * Returns the text inside of the textfield.
     *
     * @return the text inside of the textfield.
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Sets the icon to use inside of the textfield.
     *
     * @param icon the icon.
     */
    public void setIcon(Icon icon) {
        imageComponent.setIcon(icon);
    }

    /**
     * Returns the current icon used in the textfield.
     *
     * @return the icon used in the textfield.
     */
    public Icon getIcon() {
        return imageComponent.getIcon();
    }

    /**
     * Returns the component that holds the icon.
     *
     * @return the component that is the container for the icon.
     */
    public JComponent getImageComponent() {
        return imageComponent;
    }

    /**
     * Returns the text component used.
     *
     * @return the text component used.
     */
    public JTextField getTextComponent() {
        return textField;
    }
}






