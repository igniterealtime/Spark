/**
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
package org.jivesoftware.spark.plugin.battleship.gui;

import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.spark.plugin.battleship.types.Ship;

/**
 * Display panel with ship labels
 */
public class Display extends JPanel {
    private static final long serialVersionUID = 2343499579008942774L;

    private final JLabel[] _labels;
    private final JLabel _textLabel;

    public Display() {
        setLayout(new GridLayout(6, 1));
        _textLabel = new JLabel("");
        _labels = new JLabel[5];

        _labels[0] = new JLabel(Ship.TWO.getScaledInstance(100, 50, Image.SCALE_SMOOTH));
        _labels[1] = new JLabel(Ship.THREE.getScaledInstance(100, 50, Image.SCALE_SMOOTH));
        _labels[2] = new JLabel(Ship.THREE.getScaledInstance(100, 50, Image.SCALE_SMOOTH));
        _labels[3] = new JLabel(Ship.FOUR.getScaledInstance(100, 50, Image.SCALE_SMOOTH));
        _labels[4] = new JLabel(Ship.FIVE.getScaledInstance(100, 50, Image.SCALE_SMOOTH));

        add(_textLabel);
        for (JLabel l : _labels) {
            add(l);
        }
    }

    /**
     * Returns the X's ships Label
     */
    public JLabel getLabel(int x) {
        return _labels[x];
    }

    public void setMessage(String text) {
        _textLabel.setText(text);
        this.repaint();
        this.revalidate();
    }

}
