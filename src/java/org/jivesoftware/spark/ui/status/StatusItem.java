/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.status;

import org.jivesoftware.smack.packet.Presence;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class StatusItem extends JLabel {
    private Presence presence;
    private ImageIcon icon;

    public StatusItem(Presence presence, ImageIcon icon) {
        this.presence = presence;
        this.icon = icon;
        setIcon(icon);
        setText(presence.getStatus());
    }

    public Presence getPresence() {
        return presence;
    }

    public ImageIcon getImageIcon() {
        return icon;
    }


}
