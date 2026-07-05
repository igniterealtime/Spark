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
package com.jivesoftware.spark.plugin.apple;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.spark.component.VerticalFlowLayout;

public class ApplePreferencePanel extends JPanel {
    private final JCheckBox _dockBadges;
    private final JCheckBox _dockBouncing;
    private final JCheckBox _repeatedBouncing;

    public ApplePreferencePanel() {
        setLayout(new VerticalFlowLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        add(mainPanel);

        _dockBadges = new JCheckBox(AppleRes.getString("showDockBadges"));
        _dockBouncing = new JCheckBox(AppleRes.getString("bouncingDockicon"));
        _repeatedBouncing = new JCheckBox(AppleRes.getString("bouncingDockiconRepeatedly"));

        ImageIcon badge = AppleRes.getImageIcon("images/badge.png");
        ImageIcon bounce = AppleRes.getImageIcon("images/bounce.png");

        mainPanel.add(_dockBadges, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
        mainPanel.add(_dockBouncing, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
        mainPanel.add(_repeatedBouncing, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));

        mainPanel.add(new JLabel(badge), new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
        mainPanel.add(new JLabel(bounce), new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));

        _dockBouncing.addActionListener(arg0 -> _repeatedBouncing.setEnabled(_dockBouncing.isSelected()));
    }

    public boolean getDockBadges() {
        return _dockBadges.isSelected();
    }

    public void setDockBadges(boolean dockBadging) {
        _dockBadges.setSelected(dockBadging);
    }

    public boolean getDockBounce() {
        return _dockBouncing.isSelected();
    }

    public void setDockBounce(boolean bouncing) {
        _dockBouncing.setSelected(bouncing);
        _repeatedBouncing.setEnabled(_dockBouncing.isSelected());
    }

    public boolean getRepeatBouncing() {
        return _repeatedBouncing.isSelected();
    }

    public void setRepeatBouncing(boolean repeat) {
        _repeatedBouncing.setSelected(repeat);
    }

}
