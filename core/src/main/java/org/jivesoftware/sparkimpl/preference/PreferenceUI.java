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

package org.jivesoftware.sparkimpl.preference;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.MouseEvent;

public class PreferenceUI extends JLabel {
    private static final long serialVersionUID = 1094181037849878301L;
    private final Preference preference;

    public PreferenceUI(Preference preference) {
        this.preference = preference;
        String listName = "";
        String toolTip = "";
        Icon icon = null;
        try {
            listName = preference.getListName();
            toolTip = GraphicUtils.createToolTip(preference.getTooltip());
            icon = preference.getIcon();
        } catch (Exception e) {
            Log.error(e);
        }
        this.setText(listName);
        this.setToolTipText(toolTip);
        this.setIcon(icon);
    }


    public void mouseEntered(MouseEvent e) {
        if (this.isEnabled()) {
            this.invalidate();
            this.repaint();
        }
    }


    public void decorate() {
        this.setOpaque(true);

        this.setVerticalTextPosition(JButton.CENTER);
        this.setHorizontalTextPosition(JButton.LEADING);
    }

    public Preference getPreference() {
        return preference;
    }

}
