/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.component;

import org.jivesoftware.spark.util.ModelUtil;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;

/**
 * Implementation of a popup field from a TextField.
 *
 * @author Derek DeMoro
 */
public class JPopupField extends JPanel {

    private JTextField textField = new JTextField();
    private DefaultListModel model = new DefaultListModel();
    private JList list = new JList(model);
    private JWindow popup;
    private List<String> items;

    public JPopupField(List items) {
        setLayout(new BorderLayout());
        this.items = items;

        add(textField, BorderLayout.CENTER);


        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
                char ch = keyEvent.getKeyChar();
                if (validateChar(ch)) {
                    showPopupMenu();
                }

                if (ch == KeyEvent.VK_ENTER) {
                    int index = list.getSelectedIndex();
                    if (index >= 0) {
                        String selection = (String)list.getSelectedValue();
                        textField.setText(selection);
                        popup.setVisible(false);
                    }

                    dispatchEvent(keyEvent);
                }
            }

            public void keyPressed(KeyEvent e) {
                if (isArrowKey(e)) {
                    list.dispatchEvent(e);
                }

            }
        });


        popup = new JWindow(new JFrame());


        popup.getContentPane().add(new JScrollPane(list));
    }

    public void setItems(List list) {
        this.items = items;
    }

    private void showPopupMenu() {
        model.removeAllElements();

        String typedItem = textField.getText();

        final List<String> validItems = new ArrayList<String>();
        for (String string : items) {
            if (string.startsWith(typedItem)) {
                validItems.add(string);
            }
        }


        if (validItems.size() > 0) {
            for (final String str : validItems) {
                model.addElement(str);
            }
        }

        if (validItems.size() != 0 && !popup.isVisible()) {
            popup.pack();
            popup.setSize(textField.getWidth(), 200);
            Point pt = textField.getLocationOnScreen();
            pt.translate(0, textField.getHeight());
            popup.setLocation(pt);
            popup.toFront();
            popup.setVisible(true);
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
            && ch != '.' && ch != ',' && ch != ' ' && ch != KeyEvent.VK_BACK_SPACE && ch != KeyEvent.CTRL_DOWN_MASK
            && ch != KeyEvent.CTRL_MASK) {
            return false;
        }

        return true;
    }

    public boolean isArrowKey(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            return true;
        }
        return false;
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }


}
