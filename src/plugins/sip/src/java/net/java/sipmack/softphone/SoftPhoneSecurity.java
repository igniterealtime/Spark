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
