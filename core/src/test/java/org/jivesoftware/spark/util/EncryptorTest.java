package org.jivesoftware.spark.util;

import org.jasypt.properties.EncryptableProperties;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EncryptorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder(new File("target"));


    @Test
    public void encryptAndDecryptProperties() throws IOException
    {
        final String testString = "How are you today? This is test string!";
        final String masterPasswd = "MyEncryptionPasswordForTestOnly";
        Encryptor.setMasterPassword(masterPasswd);
        final String encryptedMessage = PropertyValueEncryptionUtils.encrypt(testString, Encryptor.AES256_INSTANCE);
        // System.out.println("Encrypted: " + encryptedMessage);
        final EncryptableProperties encProps = new EncryptableProperties(Encryptor.AES256_INSTANCE);
        encProps.setProperty("encrypted_property1", encryptedMessage);
        encProps.setProperty("unencrypted_property2", testString);
        // Save the properties to file
        final Path propertiesFilePath = tempFolder.newFile("spark.properties").toPath();
        encProps.store(Files.newOutputStream(propertiesFilePath), "Spark Settings (test)");
        // System.out.println("Properties saved: ");
        // System.out.println(Files.readString(propertiesFilePath.toAbsolutePath()));

        // Try to reload properties and decrypt
        final EncryptableProperties encPropsAfterReload = new EncryptableProperties(Encryptor.AES256_INSTANCE);
        encPropsAfterReload.load(Files.newInputStream(propertiesFilePath));

        Assert.assertEquals(testString, encPropsAfterReload.getProperty("encrypted_property1"));
        Assert.assertEquals(testString, encPropsAfterReload.getProperty("unencrypted_property2"));
    }

}
