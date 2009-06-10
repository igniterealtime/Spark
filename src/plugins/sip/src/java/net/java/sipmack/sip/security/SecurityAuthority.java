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
 * SecurityAuthority is used by SipSecurityManager as an interface to an entity
 * capable of providing user credentials (password);
 *
 * @author Emil Ivov <emcho@dev.java.net>
 * @version 1.0
 */

public interface SecurityAuthority {
    /**
     * Returns a Credentials object associated with the specified realm.
     *
     * @param realm         The realm that the credentials are needed for.
     * @param defaultValues the values to propose the user by default
     * @return The credentials associated with the specified realm or null if
     *         none could be provided.
     */
    public Credentials obtainCredentials(String realm,
                                             Credentials defaultValues);
}