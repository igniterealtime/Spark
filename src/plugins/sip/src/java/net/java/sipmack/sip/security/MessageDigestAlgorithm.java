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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The class takes standard Http Authentication details and returns a response
 * according to the MD5 algorithm
 *
 * @author Emil Ivov < emcho@dev.java.net >
 * @version 1.0
 */

public class MessageDigestAlgorithm {
    /**
     * Calculates a response an http authentication response in accordance with
     * rfc2617.
     *
     * @param algorithm        MD5 or MD5-sess)
     * @param username_value   username_value (see rfc2617)
     * @param realm_value      realm_value
     * @param passwd           passwd
     * @param nonce_value      nonce_value
     * @param cnonce_value     cnonce_value
     * @param Method           method
     * @param digest_uri_value uri_value
     * @param entity_body      entity_body
     * @param qop_value        qop
     * @return a digest response as defined in rfc2617
     * @throws NullPointerException in case of incorrectly null parameters.
     */
    static String calculateResponse(String algorithm, String username_value,
                                    String realm_value, String passwd, String nonce_value,
                                    String nc_value, String cnonce_value, String Method,
                                    String digest_uri_value, String entity_body, String qop_value) {
        if (username_value == null || realm_value == null || passwd == null
                || Method == null || digest_uri_value == null
                || nonce_value == null)
            throw new NullPointerException(
                    "Null parameter to MessageDigestAlgorithm.calculateResponse()");

        // The following follows closely the algorithm for generating a response
        // digest as specified by rfc2617
        String A1 = null;

        if (algorithm == null || algorithm.trim().length() == 0
                || algorithm.trim().equalsIgnoreCase("MD5")) {
            A1 = username_value + ":" + realm_value + ":" + passwd;
        }
        else {
            if (cnonce_value == null || cnonce_value.length() == 0)
                throw new NullPointerException(
                        "cnonce_value may not be absent for MD5-Sess algorithm.");

            A1 = H(username_value + ":" + realm_value + ":" + passwd) + ":"
                    + nonce_value + ":" + cnonce_value;
        }

        String A2 = null;
        if (qop_value == null || qop_value.trim().length() == 0
                || qop_value.trim().equalsIgnoreCase("auth")) {
            A2 = Method + ":" + digest_uri_value;
        }
        else {
            if (entity_body == null)
                entity_body = "";
            A2 = Method + ":" + digest_uri_value + ":" + H(entity_body);
        }

        String request_digest = null;
        if (cnonce_value != null && qop_value != null
                && (qop_value.equals("auth") || (qop_value.equals("auth-int")))) {
            request_digest = KD(H(A1), nonce_value + ":" + nc_value + ":"
                    + cnonce_value + ":" + qop_value + ":" + H(A2));

        }
        else {
            request_digest = KD(H(A1), nonce_value + ":" + H(A2));
        }

        return request_digest;
    }

    /**
     * Defined in rfc 2617 as H(data) = MD5(data);
     *
     * @param data data
     * @return MD5(data)
     */
    private static String H(String data) {
        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");

            return toHexString(digest.digest(data.getBytes()));
        }
        catch (NoSuchAlgorithmException ex) {
            // shouldn't happen

            return null;
        }
        finally {

        }
    }

    /**
     * Defined in rfc 2617 as KD(secret, data) = H(concat(secret, ":", data))
     *
     * @param data   data
     * @param secret secret
     * @return H(concat(secret, ":", data));
     */
    private static String KD(String secret, String data) {
        try {

            return H(secret + ":" + data);
        }
        finally {

        }
    }

    // the following code was copied from the NIST-SIP instant
    // messenger (its author is Olivier Deruelle). Thanks for making it public!
    /**
     * to hex converter
     */
    private static final char[] toHex = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Converts b[] to hex string.
     *
     * @param b
     *            the bte array to convert
     * @return a Hex representation of b.
     */
    private static String toHexString(byte b[]) {
        int pos = 0;
        char[] c = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            c[pos++] = toHex[(b[i] >> 4) & 0x0F];
            c[pos++] = toHex[b[i] & 0x0f];
        }
        return new String(c);
	}

}