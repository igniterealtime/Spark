/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.media.event;

import java.util.EventObject;

public class MediaEvent extends EventObject {

	private static final long serialVersionUID = 2025524719587153767L;
	private String user;

    public MediaEvent(Object source) {
        super(source);
    }

    public MediaEvent(Object source, String u) {
        super(source);
        user = u;
    }
    
    public String getUser() {
    	return user;
    }
}