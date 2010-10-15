/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
package net.java.sipmack.sip.security;

public class Credentials {
    private static String userName = null;
    private static char[] password = null;
    private static String authUserName = null;
    private static String displayName = null;

    /**
     * Sets the user name.
     *
     * @param userName The user name to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Sets the user name.
     *
     * @param userName The user name to set in Authentication challenge.
     */
    public void setAuthUserName(String userName) {
        this.authUserName = userName;
    }

    public static void clean() {
        userName = null;
        password = null;
        authUserName = null;
    }

    public static String getUserDisplay() {
        return displayName == null ? userName : displayName;
    }

    /**
     * Returns the user name.
     *
     * @return the user name.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Returns the user name.
     *
     * @return the user name.
     */
    public String getAuthUserName() {
        return this.authUserName != null && !this.authUserName.equals("") ? this.authUserName
                : this.userName;
    }

    /**
     * Sets the user password.
     *
     * @param passwd The password associated with username
     */
    public void setPassword(char[] passwd) {
        this.password = passwd;
    }

    /**
     * Returns these credentials' password
     *
     * @return these credentials' password
     */
    public char[] getPassword() {
        return password;
	}

}