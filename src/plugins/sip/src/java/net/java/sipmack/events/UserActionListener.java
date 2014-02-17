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
