/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.components;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 */
public class CloseButton extends JButton implements MouseListener {

    private Icon normalIcon;
    private Icon hoverIcon;
    private Icon downIcon;

    public CloseButton() {
        super();

        normalIcon = PhoneRes.getImageIcon("CLOSE_BUTTON");
        hoverIcon = PhoneRes.getImageIcon("CLOSE_BUTTON_HOVER");
        downIcon = PhoneRes.getImageIcon("CLOSE_BUTTON_DOWN");
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
