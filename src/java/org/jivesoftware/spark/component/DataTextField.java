/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.component;

import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DataTextField extends JPanel {

    private JTextField textField = new JTextField();
    private PopupMenu popupMenu = new PopupMenu();
    private String[] items;

    public DataTextField(String[] items) {
        setLayout(new BorderLayout());
        this.items = items;

        add(textField, BorderLayout.CENTER);

        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent keyEvent) {

            }

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    return;
                }

                if (validateChars(textField.getText())) {
                    showPopupMenu();
                }
                else {
                }


            }
        });
    }

    private void showPopupMenu() {
        popupMenu = new PopupMenu();

        String typedItem = textField.getText();

        final List<String> validItems = new ArrayList<String>();
        for (String string : items) {
            if (string.startsWith(typedItem)) {
                validItems.add(string);
            }
        }


        if (validItems.size() > 0) {
            for (final String str : validItems) {
                final Action action = new AbstractAction() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        textField.setText(str);
                    }
                };

                action.putValue(Action.NAME, str);
                popupMenu.add(str);
            }
        }

        if (validItems.size() == 0) {

        }
        else {
            popupMenu.show(textField, 0, textField.getHeight());
        }


    }

    /**
     * Validate the given text - to pass it must contain letters, digits, '@', '-', '_', '.', ','
     * or a space character.
     *
     * @param text the text to check
     * @return true if the given text is valid, false otherwise.
     */
    public boolean validateChars(String text) {
        if (!ModelUtil.hasLength(text)) {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isLetterOrDigit(ch) && ch != '@' && ch != '-' && ch != '_'
                    && ch != '.' && ch != ',' && ch != ' ') {
                return false;
            }
        }

        System.out.println(true);
        return true;
    }

    /**
     * Validate the given text - to pass it must contain letters, digits, '@', '-', '_', '.', ','
     * or a space character.
     *
     * @param ch the character
     * @return true if the given text is valid, false otherwise.
     */
    public boolean validateChar(char ch) {
        if (!Character.isLetterOrDigit(ch) && ch != '@' && ch != '-' && ch != '_'
                && ch != '.' && ch != ',' && ch != ' ') {
            return false;
        }

        return true;
    }


    public static void main(String args[]) {
        final JFrame frame = new JFrame();
        String[] items = {"one", "two", "three"};
        frame.add(new DataTextField(items));
        frame.setVisible(true);
        frame.pack();
        GraphicUtils.centerWindowOnScreen(frame);
    }
}
