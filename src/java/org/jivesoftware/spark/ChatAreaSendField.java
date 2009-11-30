/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.ui.ChatInputEditor;
import org.jivesoftware.spark.util.ResourceUtils;

/**
 * Creates a Firefox Search type box that allows for icons inside of a textfield. This
 * could be used to build out your own search objects.
 */
public class ChatAreaSendField extends JPanel {
	private static final long serialVersionUID = 6226413259528399476L;
	private ChatInputEditor textField;
    private JButton button;

    /**
     * Creates a new IconTextField with Icon.
     *
     * @param text the text to use on the button.
     */
    public ChatAreaSendField(String text) {
        setLayout(new GridBagLayout());
        setBackground((Color)UIManager.get("TextPane.background"));
        textField = new ChatInputEditor();
        textField.setBorder(null);
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
        button = new JButton();
        
        if (Spark.isMac()) {
            button.setContentAreaFilled(false);
        }

        ResourceUtils.resButton(button, text);

        add(button, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));

        button.setVisible(false);
        
        final JScrollPane pane = new JScrollPane(textField);
        pane.setBorder(null);
        add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        button.setEnabled(false);
    }

    public JButton getButton() {
        return button;
    }

    public void showSendButton(boolean show) {
        button.setVisible(show);
    }

    public void enableSendButton(boolean enabled) {
        button.setEnabled(enabled);
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

    public ChatInputEditor getChatInputArea() {
        return textField;
    }

}








