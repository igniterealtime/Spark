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
    // The passwords aren't really encrypted because the key is always the same.
    // It's easy to decrypt them. This encryption is just to hide the password from inexperienced user.
    // We should use a system keychain or a master password, see https://igniterealtime.atlassian.net/browse/SPARK-1601
    // In Base 64: ugfpV1dMC5jyJtqwVAfTpHkxqJ0+E0ae
    private static final byte[] secretKey = new byte[]{-70, 7, -23, 87, 87, 76, 11, -104, -14, 38, -38, -80, 84, 7, -45, -92, 121, 49, -88, -99, 62, 19, 70, -98};
    private static final ThreadLocal<Cipher> ecipher = ThreadLocal.withInitial(() -> createCipher(Cipher.ENCRYPT_MODE));
    private static final ThreadLocal<Cipher> dcipher = ThreadLocal.withInitial(() -> createCipher(Cipher.DECRYPT_MODE));

    private static Cipher createCipher(int mode) {
        try {
            SecretKey key = new SecretKeySpec(secretKey, "DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(mode, key);
            return cipher;
        }
        catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static String encrypt(String string) {
        try {
            byte[] utf8 = string.getBytes(StandardCharsets.UTF_8);
            byte[] enc = ecipher.get().doFinal(utf8);
            return Base64.getEncoder().encodeToString(enc);
        }
        catch (Exception ignored) {
            return null;
        }
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
        byte[] utf8 = dcipher.get().doFinal(dec);
        return new String(utf8, StandardCharsets.UTF_8);
    }
}
