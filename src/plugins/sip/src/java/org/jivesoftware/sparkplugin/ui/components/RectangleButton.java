/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.ui.components;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 */
public class RectangleButton extends JButton implements MouseListener {

    private Icon normalIcon;
    private Icon hoverIcon;
    private Icon downIcon;

    public RectangleButton() {
        super();

        normalIcon = PhoneRes.getImageIcon("RECTANGLE_BUTTON");
        hoverIcon = PhoneRes.getImageIcon("RECTANGLE_BUTTON_HOVER");
        downIcon = PhoneRes.getImageIcon("RECTANGLE_BUTTON_DOWN");
        setIcon(normalIcon);
        decorate();

        addMouseListener(this);

        setForeground(new Color(63, 102, 161));
        setFont(new Font("Verdana", Font.BOLD, 10));

        // Set text placement
        setHorizontalTextPosition(JLabel.CENTER);
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
    }

    public void mouseReleased(MouseEvent e) {
        setIcon(normalIcon);
    }

    public void mouseEntered(MouseEvent e) {
        setIcon(hoverIcon);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
        setIcon(normalIcon);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}
