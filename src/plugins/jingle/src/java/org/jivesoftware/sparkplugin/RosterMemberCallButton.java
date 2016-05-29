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
package org.jivesoftware.sparkplugin;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 */
public class RosterMemberCallButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 8056708231977922612L;
	private Icon normalIcon;
    private Icon hoverIcon;
    private Icon downIcon;
    private Image backgroundImage;
    private String text;

    private boolean selected;

    public RosterMemberCallButton(Image image, String text) {
        super();

        this.text = text;

        normalIcon = JinglePhoneRes.getImageIcon("ROSTERPANEL_BUTTON");
        hoverIcon = JinglePhoneRes.getImageIcon("ROSTERPANEL_BUTTON_HOVER");
        downIcon = JinglePhoneRes.getImageIcon("ROSTERPANEL_BUTTON_DOWN");
        backgroundImage = image;

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

        int x = (width - backgroundImage.getWidth(null)) / 2;
        int y = (height - backgroundImage.getHeight(null)) / 2;
        g.drawImage(backgroundImage, 5, y, null);

        g.setColor(Color.black);
        g.setFont(new Font("Dialog", Font.PLAIN, 11));


        int stringWidth = g.getFontMetrics().stringWidth(text);

        x = (width - stringWidth) / 2;
        y = (height + 11) / 2;
        g.drawString(text, x, y);

    }

}
