/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;


/**
 * Users of this interface may want to intercept MUC Invitations for their own uses.
 */
public interface RoomInvitationListener {

    /**
     * Return true if you wish to handle this invitation.
     *
     * @param conn
     * @param room
     * @param inviter
     * @param reason
     * @param password
     * @param message
     * @return true if handled.
     */
    boolean handleInvitation(final XMPPConnection conn, final String room, final String inviter, final String reason, final String password, final Message message);
}
