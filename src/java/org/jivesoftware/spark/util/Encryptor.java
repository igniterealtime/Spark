/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.util;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;

/**
 * Encrypts and Decrypts text based on DESede keys.
 *
 * @author Derek DeMoro
 */
public class Encryptor {

    private static String secretKey = "ugfpV1dMC5jyJtqwVAfTpHkxqJ0+E0ae";

    private static Cipher ecipher;
    private static Cipher dcipher;

    static {
        try {
            SecretKey key = decodeKey();
            ecipher = Cipher.getInstance("DESede");
            dcipher = Cipher.getInstance("DESede");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    public static String encrypt(String string) throws Exception {
        byte[] utf8 = string.getBytes("UTF8");

        // Encrypt
        byte[] enc = ecipher.doFinal(utf8);
        return org.jivesoftware.smack.util.StringUtils.encodeBase64(enc);
    }

    public static String decrypt(String string) {
        byte[] dec = StringUtils.decodeBase64(string);

        try {
            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        }
        catch (IllegalBlockSizeException e) {
            Log.error(e);
        }
        catch (BadPaddingException e) {
            Log.error(e);
        }
        catch (UnsupportedEncodingException e) {
            Log.error(e);
        }
        return null;
    }

    private static SecretKey decodeKey() throws Exception {
        byte[] bytes = StringUtils.decodeBase64(secretKey);
        return new SecretKeySpec(bytes, "DESede");
    }

    public static void main(String[] args) throws Exception {
        String encoded = encrypt("How are you today");
        System.out.println(decrypt(encoded));
    }
}
