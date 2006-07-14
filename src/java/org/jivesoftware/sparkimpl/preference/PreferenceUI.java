/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.GraphicUtils;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.MouseEvent;

public class PreferenceUI extends JLabel {
    private Preference preference;

    public PreferenceUI(Preference preference) {
        this.preference = preference;
        this.setIcon(preference.getIcon());
        this.setText(preference.getListName());

        // Set tooltip
        this.setToolTipText(GraphicUtils.createToolTip(preference.getTooltip()));
    }


    public void mouseEntered(MouseEvent e) {
        if (this.isEnabled()) {
            this.invalidate();
            this.repaint();
        }
    }


    public void decorate() {
        this.setOpaque(true);

        this.setVerticalTextPosition(JButton.BOTTOM);
        this.setHorizontalTextPosition(JButton.CENTER);
    }

    public Preference getPreference() {
        return preference;
    }

}