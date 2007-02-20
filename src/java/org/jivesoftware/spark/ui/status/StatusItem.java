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

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * Represents a single UI instance in the status bar.
 */
public class StatusItem extends JLabel {
    private Presence presence;

    /**
     * Creates a single StatusItem UI object.
     *
     * @param presence the presence.
     * @param icon     the icon
     */
    public StatusItem(Presence presence, Icon icon) {
        this.presence = presence;
        setIcon(icon);
        setText(presence.getStatus());
    }

    /**
     * Returns the Presence related to this item.
     *
     * @return the presence associated with this item.
     */
    public Presence getPresence() {
        return presence;
    }


}
