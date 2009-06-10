/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.sip.security;

/**
 * This exception is used by SipSecurityManager to indicate failure to provide
 * valid credentials for a given request.
 *
 * @author Emil Ivov <emcho@dev.java.net>
 * @version 1.0
 */
public class SipSecurityException extends Exception {
	private static final long serialVersionUID = 3158525577631441256L;

	public SipSecurityException() {

        this("SipSecurityException");
    }

    public SipSecurityException(String message) {
        super(message);
    }

}
