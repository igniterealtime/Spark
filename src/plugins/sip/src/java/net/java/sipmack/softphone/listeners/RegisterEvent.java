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

import net.java.sipmack.sip.SipRegisterStatus;

import java.util.EventObject;

public class RegisterEvent extends EventObject {

	private static final long serialVersionUID = 6673006341746488777L;

	private SipRegisterStatus status = SipRegisterStatus.Unregistered;

    private String reason = "";

    public RegisterEvent(Object source) {
        super(source);
    }

    public RegisterEvent(Object source, SipRegisterStatus status, String reason) {
        super(source);
        this.setStatus(status);
        this.setReason(reason);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SipRegisterStatus getStatus() {
        return status;
    }

    public void setStatus(SipRegisterStatus status) {
        this.status = status;
    }

}
