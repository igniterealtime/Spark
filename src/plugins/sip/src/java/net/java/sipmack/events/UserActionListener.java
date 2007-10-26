package net.java.sipmack.events;

import net.java.sipmack.media.MediaException;
import net.java.sipmack.sip.Interlocutor;
import net.java.sipmack.sip.InterlocutorUI;

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

public interface UserActionListener extends java.util.EventListener {

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
