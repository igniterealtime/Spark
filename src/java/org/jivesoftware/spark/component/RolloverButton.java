/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import javax.swing.Icon;
import javax.swing.JButton;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Button UI for handling of rollover buttons.
 */
public class RolloverButton extends JButton {

    /**
     * Create a new RolloverButton.
     */
    public RolloverButton() {
        decorate();
    }

    /**
     * Create a new RolloverButton.
     *
     * @param icon the icon to use on the button.
     */
    public RolloverButton(Icon icon) {
        super(icon);
        decorate();
    }

    /**
     * Create a new RolloverButton.
     *
     * @param text the button text.
     * @param icon the button icon.
     */
    public RolloverButton(String text, Icon icon) {
        super(text, icon);
        decorate();
    }


    /**
     * Decorates the button with the approriate UI configurations.
     */
    private void decorate() {
        setBorderPainted(false);
        setOpaque(true);

        setContentAreaFilled(false);
        setMargin(new Insets(1, 1, 1, 1));

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

    }


}