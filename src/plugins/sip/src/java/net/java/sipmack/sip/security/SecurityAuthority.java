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