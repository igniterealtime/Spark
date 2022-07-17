package org.jivesoftware.spark.util;

import org.junit.Test;

import static org.jivesoftware.spark.util.Encryptor.*;
import static org.junit.Assert.assertEquals;

public class EncryptorTest {

    @Test
    public void encryptAndDecryptString() {
        final String testString = "How are you today? This is test string!";
        String encodedString;
        try {
            encodedString = decrypt(encrypt(testString));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(testString,encodedString);
    }

}
