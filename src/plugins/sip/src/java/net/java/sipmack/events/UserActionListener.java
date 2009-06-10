/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.events;

import java.util.EventListener;

import net.java.sipmack.media.MediaException;
import net.java.sipmack.sip.Interlocutor;
import net.java.sipmack.sip.InterlocutorUI;

public interface UserActionListener extends EventListener {

    public void createSoftPhone(String server) throws MediaException;

    public void destroySoftPhone();

    public void handleDialRequest(String callee);

    public boolean handleHangupRequest(Interlocutor interlocutor);

    public boolean handleAnswerRequest(Interlocutor interlocutor);

    public void handleRegisterRequest(String u, String p);

    public void handleRegisterRequest(String u, String au, String p);

    public void handleUnregisterRequest();
    
    public void handleDTMF(InterlocutorUI iui, String digit);

    public void handleHold(InterlocutorUI iui, boolean mic, boolean cam);

    public void handleMute(InterlocutorUI iui, boolean mic);

    public void handleExitRequest();

}
