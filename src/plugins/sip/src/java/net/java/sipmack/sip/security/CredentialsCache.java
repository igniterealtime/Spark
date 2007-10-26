/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */

package net.java.sipmack.sip.security;

import java.util.Hashtable;

/**
 * The class is used to cache all realms that a certain call has been authorized
 * against and all credentials that have been used for each realm. Note that
 * rfc3261 suggests keeping callId->credentials mapping where as we map
 * realm->credentials. This is done to avoid asking the user for a password
 * before each call.
 *
 * @author Emil Ivov <emcho@dev.java.net>
 * @version 1.0
 */

class CredentialsCache {
    // Contains call->realms mappings
    private Hashtable authenticatedRealms = new Hashtable();

    /**
     * Cache credentials for the specified call and realm
     *
     * @param realm      the realm that the specify credentials apply to
     * @param cacheEntry the credentials
     */
    void cacheEntry(String realm, CredentialsCacheEntry cacheEntry) {
        authenticatedRealms.put(realm, cacheEntry);
    }

    /**
     * Returns the credentials corresponding to the specified realm or null if
     * none could be found.
     *
     * @param realm the realm that the credentials apply to
     * @return the credentials corresponding to the specified realm or null if
     *         none could be found.
     */
    CredentialsCacheEntry get(String realm) {
        return (CredentialsCacheEntry)this.authenticatedRealms.get(realm);
    }

    /**
     * Returns the credentials corresponding to the specified realm or null if
     * none could be found and removes the entry from the cache.
     *
     * @param realm
     *            the realm that the credentials apply to
     * @return the credentials corresponding to the specified realm or null if
     *         none could be found.
     */
    CredentialsCacheEntry remove(String realm) {
        return (CredentialsCacheEntry)this.authenticatedRealms.remove(realm);
	}

}