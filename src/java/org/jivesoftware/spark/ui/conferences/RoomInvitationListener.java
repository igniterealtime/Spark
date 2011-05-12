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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.smack.Connection;
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
    boolean handleInvitation(final Connection connection, final String room, final String inviter, final String reason, final String password, final Message message);
}
