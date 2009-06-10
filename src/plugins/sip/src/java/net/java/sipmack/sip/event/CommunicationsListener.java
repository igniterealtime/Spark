/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.sip.event;

import java.util.EventListener;

/**
 * <p/>
 * Title: SIP COMMUNICATOR
 * </p>
 * <p/>
 * Description:JAIN-SIP Audio/Video phone application
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * Organisation: LSIIT laboratory (http://lsiit.u-strasbg.fr)
 * </p>
 * <p/>
 * Network Research Team (http://www-r2.u-strasbg.fr))
 * </p>
 * <p/>
 * Louis Pasteur University - Strasbourg - France
 * </p>
 *
 * @author Emil Ivov (http://www.emcho.com)
 * @version 1.1
 */
public interface CommunicationsListener extends EventListener {

    public void callReceived(CallEvent evt);

    public void callEnded(CallEvent evt);

    public void callRejectedLocally(CallRejectedEvent evt);

    public void callRejectedRemotely(CallRejectedEvent evt);

    public void messageReceived(MessageEvent evt);

    public void receivedUnknownMessage(UnknownMessageEvent evt);

    public void communicationsErrorOccurred(CommunicationsErrorEvent evt);

    public void registered(RegistrationEvent evt);

    public void registering(RegistrationEvent evt);

    public void registrationFailed(RegistrationEvent evt);

    public void unregistering(RegistrationEvent evt);

    public void unregistered(RegistrationEvent evt);

}
