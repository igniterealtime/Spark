/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package net.java.sipmack.softphone.listeners;

import net.java.sipmack.sip.event.CallRejectedEvent;
import net.java.sipmack.sip.event.CallStateEvent;
import net.java.sipmack.sip.event.MessageEvent;
import net.java.sipmack.sip.event.UnknownMessageEvent;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 */

public interface SoftPhoneListener {

    /**
     * Fired when a message is received
     *
     * @param evt MessageEvent
     */
    public void messageReceived(MessageEvent evt);

    /**
     * Fired when an Unknow message is received
     *
     * @param evt UnknownMessageEvent
     */
    public void receivedUnknownMessage(UnknownMessageEvent evt);

    /**
     * Fired when global status of SIP Registering changed
     *
     * @param evt RegisterEvent
     */
    public void registerStatusChanged(RegisterEvent evt);

    /**
     * Fired when a call State Change
     *
     * @param evt CallStateEvent
     */
    public void callStateChanged(CallStateEvent evt);

    /**
     * Fired when a call was rejected Remotely
     * 
     * @param evt
     */
    public void callRejectedRemotely(CallRejectedEvent evt);

}
