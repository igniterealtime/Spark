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

import org.jivesoftware.smack.packet.Presence;

/**
 * The <code>PresenceListener</code> is used to listen for Personal Presence changes within the system.
 * <p/>
 * Presence listeners can be registered using the {@link org.jivesoftware.spark.SessionManager}
 */
public interface PresenceListener {

    /**
     * Called when the user of Sparks presence has changed.
     *
     * @param presence the presence.
     */
    void presenceChanged(Presence presence);

}
