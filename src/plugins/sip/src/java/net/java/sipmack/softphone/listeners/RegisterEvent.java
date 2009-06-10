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
