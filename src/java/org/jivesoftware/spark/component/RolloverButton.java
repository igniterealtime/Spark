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
    private static final long serialVersionUID = 6351541211385798436L;


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
    protected void decorate() {
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