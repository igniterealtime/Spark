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

import java.util.EventObject;

public class RegistrationEvent extends EventObject {
	private static final long serialVersionUID = 6759871915588020048L;

	public enum Type {
        Normal, WrongPass, NotFound, Forbidden, WrongAuthUser, TimeOut
    };

    private Type type = Type.Normal;

    public RegistrationEvent(String registrationAddress) {
        super(registrationAddress);
    }

    public RegistrationEvent(String registrationAddress, Type type) {
        super(registrationAddress);
        this.type = type;
    }

    public String getReason() {
        return (String) getSource();
    }

    public Type getType() {
        return type;
    }

}
