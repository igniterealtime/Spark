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
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.ModelUtil;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a popup field from a TextField.
 *
 * @author Derek DeMoro
 */
public class JContactItemField extends JPanel {

    private static final long serialVersionUID = -8556694682789891531L;
    private JTextField textField = new JTextField();
    private DefaultListModel model = new DefaultListModel();
    private JList list;
    private JWindow popup;
    private List<ContactItem> items;

    public JContactItemField(List<ContactItem> items) {
        setLayout(new BorderLayout());
        list = new JList(model) {
	    private static final long serialVersionUID = -9031169221430835595L;

	    public String getToolTipText(MouseEvent e) {
                int row = locationToIndex(e.getPoint());
                if (row >= 0)
                {
                    final ContactItem item = (ContactItem)getModel().getElementAt(row);
                    if (item != null) {
                        return item.getJID();
                }
                }
                return null;
            }
        };

        this.items = items;

        add(textField, BorderLayout.CENTER);


        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
                char ch = keyEvent.getKeyChar();
                if (validateChar(ch)) {
                    showPopupMenu();
                }

                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    int index = list.getSelectedIndex();
                    if (index >= 0) {
                        ContactItem selection = (ContactItem)list.getSelectedValue();
                        textField.setText(selection.getDisplayName());
                        popup.setVisible(false);
                    }
                }

                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.setVisible(false);
                }
                dispatchEvent(keyEvent);
            }

            public void keyPressed(KeyEvent e) {
                if (isArrowKey(e)) {
                    list.dispatchEvent(e);
                }

            }
        });


        textField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                textField.requestFocusInWindow();
            }
        });


        popup = new JWindow();


        popup.getContentPane().add(new JScrollPane(list));
        popup.setAlwaysOnTop(true);


        list.setCellRenderer(new PopupRenderer());
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.getSelectedIndex();
                    if (index >= 0) {
                        ContactItem selection = (ContactItem)list.getSelectedValue();
                        textField.setText(selection.getDisplayName());
                        popup.setVisible(false);
                    }
                }
            }
        });
    }

    public void dispose() {
        popup.dispose();
    }

    public void setItems(List<ContactItem> list) {
        this.items = list;
    }

    public JList getList() {
        return list;
    }

    private void showPopupMenu() {
        model.removeAllElements();

        String typedItem = textField.getText();

	final List<ContactItem> validItems = new ArrayList<ContactItem>();
	for (ContactItem contactItem : items) {
	    String nickname = contactItem.getDisplayName().toLowerCase();
	    if (nickname.startsWith(typedItem.toLowerCase())) {
		validItems.add(contactItem);
	    } else if (typedItem.length() > 2 && nickname.contains(typedItem.toLowerCase())) {
		validItems.add(contactItem);
	    }
	}


        if (validItems.size() > 0) {
            for (final ContactItem label : validItems) {
                model.addElement(label);
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

        // set initial selection
        if (validItems.size() > 0) {
            list.setSelectedIndex(0);
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

    public ContactItem getSelectedContactItem() {
        return (ContactItem)list.getSelectedValue();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void focus() {
        textField.requestFocus();
    }

    public JTextField getTextField() {
        return textField;
    }

    public JWindow getPopup() {
        return popup;
    }

    class PopupRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = 239608430590852355L;

	/**
         * Construct Default JLabelIconRenderer.
         */
        public PopupRenderer() {
            setOpaque(true);
            this.setHorizontalTextPosition(JLabel.RIGHT);
            this.setHorizontalAlignment(JLabel.LEFT);
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }


            ContactItem contactItem = (ContactItem)value;
            setText(contactItem.getDisplayName());
            if (contactItem.getIcon() == null) {
                setIcon(SparkRes.getImageIcon(SparkRes.CLEAR_BALL_ICON));
            }
            else {
                setIcon(contactItem.getIcon());
            }
            setFont(contactItem.getNicknameLabel().getFont());
            setForeground(contactItem.getForeground());

            return this;
        }
    }

    public boolean canClose() {
        return !textField.hasFocus();
    }
    
    /**
     * sets the selected Index using the Point of a given {@link MouseEvent}
     * @param mouseevent - {@link MouseEvent} to get The {@link Point} from
     */
    public void setSelectetIndex(MouseEvent mouseevent)
    {	
	list.setSelectedIndex(list.locationToIndex(mouseevent.getPoint()));
    }


}
