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

import org.jivesoftware.Spark;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Button UI for handling of rollover buttons.
 *
 * @author Derek DeMoro
 */
public class RolloverButton extends JButton {

    /**
     * Create a new RolloverButton.
     */
    public RolloverButton() {
        decorate();
    }

    public RolloverButton(String text) {
        super(text);
        decorate();
    }

    public RolloverButton(Action action) {
        super(action);
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

                    // Handle background border on mac.
                    if (!Spark.isMac()) {
                        setContentAreaFilled(true);
                    }
                }
            }

            public void mouseExited(MouseEvent e) {
                setBorderPainted(false);
                setContentAreaFilled(false);
            }
        });

    }


}