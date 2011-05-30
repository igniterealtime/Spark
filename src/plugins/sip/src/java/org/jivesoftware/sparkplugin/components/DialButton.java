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
package org.jivesoftware.sparkplugin.components;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.AbstractAction;

/**
 *
 */
public class DialButton extends JButton implements MouseListener {

    private static final long serialVersionUID = 1025696002616464711L;
    private Icon normalIcon;
    private Icon hoverIcon;
    private Icon downIcon;
    private String textOnTop;

    private boolean block;

    private String number;

    private boolean selected;

    private Action action;

    public DialButton(String topText, final Action action) {
        super();

        this.textOnTop = topText;
        this.action = action;

        number = (String)action.getValue(Action.NAME);


        normalIcon = PhoneRes.getImageIcon("DIALPAD_BUTTON");
        hoverIcon = PhoneRes.getImageIcon("DIALPAD_BUTTON_HOVER");
        downIcon = PhoneRes.getImageIcon("DIALPAD_BUTTON_DOWN");

        setIcon(normalIcon);

        decorate();

        addMouseListener(this);
    }

    /**
     * Decorates the button with the approriate UI configurations.
     */
    private void decorate() {
        setBorderPainted(false);
        setOpaque(true);

        setContentAreaFilled(false);
        setMargin(new Insets(0, 0, 0, 0));
    }


    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        setIcon(downIcon);
        if (block) {
            return;
        }
        action.actionPerformed(null);
    }

    public void mouseReleased(MouseEvent e) {
        if (!selected) {
            setIcon(normalIcon);
        }

    }

    public void mouseEntered(MouseEvent e) {
        if (!selected) {
            setIcon(hoverIcon);
        }
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
        if (!selected) {
            setIcon(normalIcon);
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void setButtonSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setIcon(downIcon);
        }
        else {
            setIcon(normalIcon);
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();

        g.setColor(Color.gray);
        g.setFont(new Font("Tahoma", Font.PLAIN, 10));

        int topTextWidth = g.getFontMetrics().stringWidth(textOnTop);
        int x = (width - topTextWidth) / 2;
        int y = height - 26;
        g.drawString(textOnTop, x, y);

        g.setColor(Color.black);
        g.setFont(new Font("Tahoma", Font.BOLD, 11));
        int numberWidth = g.getFontMetrics().stringWidth(number);
        x = (width - numberWidth) / 2;
        y = height - 13;
        g.drawString(number, x, y);
    }

    public String getNumber() {
        return number;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }


    public static void main(String args[]) {
        JFrame frame = new JFrame();
        Action action = new AbstractAction() {
	    private static final long serialVersionUID = -6243205463327629493L;

	    public void actionPerformed(ActionEvent e) {
            }
        };

        action.putValue(Action.NAME, "5");
        frame.add(new DialButton("ABC", action));
        frame.pack();
        frame.setVisible(true);
    }


}