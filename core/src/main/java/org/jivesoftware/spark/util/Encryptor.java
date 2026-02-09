/**
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
package org.jivesoftware.spark.util;

import org.jivesoftware.spark.util.log.Log;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Encrypts and Decrypts text based on DESede keys.
 *
 * @author Derek DeMoro
 */
public class Encryptor {

    // In Base 64: ugfpV1dMC5jyJtqwVAfTpHkxqJ0+E0ae
    private static final byte[] secretKey = new byte[]{-70, 7, -23, 87, 87, 76, 11, -104, -14, 38, -38, -80, 84, 7, -45, -92, 121, 49, -88, -99, 62, 19, 70, -98};
    private static Cipher ecipher;
    private static Cipher dcipher;

    static {
        try {
            SecretKey key = new SecretKeySpec(secretKey, "DESede");
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
        byte[] utf8 = string.getBytes(StandardCharsets.UTF_8);

        // Encrypt
        byte[] enc = ecipher.doFinal(utf8);
        return Base64.getEncoder().encodeToString(enc);
    }

    public static String decrypt(String string) {
        try {
            return decryptOrThrow(string);
        }
        catch (IllegalBlockSizeException | BadPaddingException e) {
            Log.error(e);
            return null;
        }
    }

    public static String decryptOrThrow(String string) throws BadPaddingException, IllegalBlockSizeException {
        byte[] dec = Base64.getDecoder().decode(string);

        // Decrypt
        byte[] utf8 = dcipher.doFinal(dec);

        // Decode using utf-8
        return new String(utf8, StandardCharsets.UTF_8);
    }
}
