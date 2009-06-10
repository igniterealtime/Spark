/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.softphone;

import net.java.sipmack.sip.security.SecurityAuthority;
import net.java.sipmack.sip.security.Credentials;

public class SoftPhoneSecurity implements SecurityAuthority {

    static String userName = "";

    static String authUserName = "";

    static char password[] = "".toCharArray();

    /**
     * Implements obtainCredentials from SecurityAuthority.
     *
     * @param realm the realm that credentials are needed for
     * @return the credentials for the specified realm or null if no credentials
     *         could be obtained
     */
    public Credentials obtainCredentials(String realm,
                                             Credentials defaultValues) {
        try {
            if (defaultValues == null || defaultValues.getUserName() == null
                    || defaultValues.getPassword() == null) {
                Credentials credentials = new Credentials();
                try {
                    credentials.setUserName(userName);
                    credentials.setAuthUserName(authUserName);
                    credentials.setPassword(password);
                }
                catch (Exception e) {
                }
                return credentials;
            }
            else {
                userName = defaultValues.getUserName();
                authUserName = defaultValues.getAuthUserName();
                password = defaultValues.getPassword();
                return defaultValues;
            }
        }
        finally {
        }
    }
}
