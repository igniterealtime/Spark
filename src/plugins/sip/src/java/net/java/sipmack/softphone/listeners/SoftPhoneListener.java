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
