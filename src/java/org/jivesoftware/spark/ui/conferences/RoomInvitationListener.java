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
     * @param connection the XMPPConnection.
     * @param room       the room the invitation was sent from.
     * @param inviter    the person who is inviting the user.
     * @param reason     the reason for the invitation.
     * @param password   the password of the room, if any. This value can be null.
     * @param message    the appened message.
     * @return true if you wish to intercept this invitation.
     */
    boolean handleInvitation(final XMPPConnection connection, final String room, final String inviter, final String reason, final String password, final Message message);
}
