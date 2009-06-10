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

import net.java.sipmack.sip.InterlocutorUI;

public class UserCallControlEvent extends java.util.EventObject {
	private static final long serialVersionUID = -7959924232399983785L;

	public UserCallControlEvent() {
        super(null);
    }

    public UserCallControlEvent(Object source) {
        super(source);
    }

    public InterlocutorUI getAssociatedInterlocutor() {
        return (InterlocutorUI)source;
    }
}