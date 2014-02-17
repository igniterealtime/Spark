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
package org.jivesoftware.sparkplugin.ui;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.util.ModelUtil;

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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 *
 */

/**
 * Creates a Firefox type box that allows for icons inside of a textfield. This
 * could be used to build out your own search objects.
 */
public class TelephoneTextField extends JPanel implements FocusListener, MouseListener, KeyListener {
	private static final long serialVersionUID = 1091481535990834763L;
	private JTextField textField;
    private JLabel imageComponent;

    private PhonePad pad;

    private final String textFieldText = PhoneRes.getIString("phone.enternumber");

    /**
     * Creates a new IconTextField with Icon.
     */
    public TelephoneTextField() {
        setLayout(new GridBagLayout());

        setBackground(new Color(212, 223, 237));

        pad = new PhonePad();

        textField = new JTextField();

        textField.setBorder(null);
        setBorder(new JTextField().getBorder());


        imageComponent = new JLabel(PhoneRes.getImageIcon("ICON_NUMBERPAD_IMAGE"));

        add(imageComponent, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        add(textField, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));

        imageComponent.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                displayPad(e);
            }
        });

        textField.requestFocus();


        textField.setForeground((Color)UIManager.get("TextField.lightforeground"));
        textField.setText(textFieldText);

        textField.addFocusListener(this);
        textField.addMouseListener(this);
        textField.addKeyListener(this);
    }

    public void validateTextField() {
        if (isEdited()) {
            textField.setForeground((Color)UIManager.get("TextField.foreground"));
        }
        else {
            textField.setForeground((Color)UIManager.get("TextField.lightforeground"));
        }
    }

    public boolean isEdited() {
        return (!textFieldText.equals(textField.getText()));
    }

    /**
     * Sets the text of the textfield.
     *
     * @param text the text.
     */
    public void setText(String text) {
        textField.setText(text);
    }

    public void reset() {
        textField.setForeground((Color)UIManager.get("TextField.lightforeground"));
        textField.setText(textFieldText);
    }

    public void appendNumber(String number) {
        if (!isEdited()) {
            textField.setText("");
        }

        String text = textField.getText();
        text += number;
        setText(text);
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

    private void displayPad(MouseEvent e) {
        pad.showDialpad(this);
    }


    public void focusGained(final FocusEvent e) {
        if (!isEdited()) {
            textField.setText("");
        }
        textField.setForeground(Color.black);
    }

    public void focusLost(FocusEvent e) {
        if (!ModelUtil.hasLength(textField.getText())) {
            textField.setForeground((Color)UIManager.get("TextField.lightforeground"));
            textField.setText(textFieldText);
        }
    }


    public void keyTyped(KeyEvent keyEvent) {
        if(keyEvent.getKeyChar() == KeyEvent.VK_ENTER){
            if(pad != null){
                pad.hide();
            }
            return;
        }

        if(pad != null){
            pad.numberEntered(keyEvent.getKeyChar());
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (!pad.isShowing()) {
            pad.showDialpad(this);
        }
    }


    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }
}

