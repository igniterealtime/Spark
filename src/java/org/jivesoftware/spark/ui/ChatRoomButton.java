/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import javax.swing.Icon;
import javax.swing.JButton;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Button to use with ChatRooms to allow for conformity in the Chat Room look and feel.
 */
public class ChatRoomButton extends JButton {

    /**
     * Create a new ChatRoomButton.
     */
    public ChatRoomButton() {
        decorate();
    }

    /**
     * Create a new ChatRoomButton
     *
     * @param icon the icon to use on the button.
     */
    public ChatRoomButton(Icon icon) {
        super(icon);
        decorate();
    }

    /**
     * Create a new ChatRoomButton.
     *
     * @param text the button text.
     * @param icon the button icon.
     */
    public ChatRoomButton(String text, Icon icon) {
        super(text, icon);
        decorate();
    }

    /**
     * Creates a new ChatRoomButton.
     *
     * @param text the text to display on the button.
     */
    public ChatRoomButton(String text) {
        super(text);

        decorate();
    }


    /**
     * Decorates the button with the approriate UI configurations.
     */
    private void decorate() {
        setBorderPainted(false);
        setOpaque(true);

        setContentAreaFilled(false);
        setMargin(new Insets(0, 0, 0, 0));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBorderPainted(true);
                    setContentAreaFilled(true);
                }
            }

            public void mouseExited(MouseEvent e) {
                setBorderPainted(false);
                setContentAreaFilled(false);
            }
        });

        setVerticalTextPosition(JButton.BOTTOM);
        setHorizontalTextPosition(JButton.CENTER);
        setIconTextGap(2);
    }


}
