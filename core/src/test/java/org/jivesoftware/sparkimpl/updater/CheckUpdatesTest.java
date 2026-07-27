package org.jivesoftware.sparkimpl.updater;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CheckUpdatesTest {

    @Test
    public void comparesNumericVersionParts() {
        assertTrue(CheckUpdates.isGreater("3.10.0", "3.2.9"));
        assertFalse(CheckUpdates.isGreater("3.2.9", "3.10.0"));
    }

    @Test
    public void releaseIsNewerThanPrerelease() {
        assertTrue(CheckUpdates.isGreater("3.1.1", "3.1.1-SNAPSHOT"));
        assertTrue(CheckUpdates.isGreater("3.1.1", "3.1.1-rc1"));
    }

    @Test
    public void parsesLegacyServerFilenames() {
        assertTrue(CheckUpdates.isGreater("spark_3_1_1_x86.zip", "3.1.0"));
    }

    @Test
    public void ordersPrereleaseQualifiers() {
        assertTrue(CheckUpdates.isGreater("3.1.1-rc2", "3.1.1-rc1"));
        assertTrue(CheckUpdates.isGreater("3.1.1-rc1", "3.1.1-beta2"));
        assertTrue(CheckUpdates.isGreater("3.1.1-beta2", "3.1.1-alpha1"));
        assertFalse(CheckUpdates.isGreater("3.1.1-SNAPSHOT", "3.1.1-alpha1"));
    }

    @Test
    public void normalizesPrereleaseWithoutNumericSuffix() {
        assertEquals("3.1.1-rc", CheckUpdates.getVersion("spark_3_1_1_rc.exe"));
        assertEquals("3.1.1-beta2", CheckUpdates.getVersion("spark_3_1_1_beta2.exe"));
    }

    @Test
    public void determinesFilenameFromDescriptor() {
        SparkVersion version = new SparkVersion();
        version.setFileName("spark_3_1_0-with-jre.exe");
        version.setDownloadURL("https://example.org/download?client=ignored.exe");

        assertEquals("spark_3_1_0-with-jre.exe", CheckUpdates.determineFileName(version));
    }

    @Test
    public void determinesFilenameFromEncodedDownloadUrl() {
        SparkVersion version = new SparkVersion();
        version.setDownloadURL("https://example.org/download?client=spark_3_1_0-with-jre%20x86.exe");

        assertEquals("spark_3_1_0-with-jre x86.exe", CheckUpdates.determineFileName(version));
    }

    @Test
    public void verifiesSha256() throws Exception {
        Path file = Files.createTempFile("spark-update", ".bin");
        try {
            Files.write(file, "Spark".getBytes(StandardCharsets.UTF_8));
            CheckUpdates.verifyChecksum(file, "529bc3b07127ecb7e53a4dcf1991d9152c24537d919178022b2c42657f79a26b");
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void rejectsInvalidSha256() throws Exception {
        Path file = Files.createTempFile("spark-update", ".bin");
        try {
            Files.write(file, "Spark".getBytes(StandardCharsets.UTF_8));
            try {
                CheckUpdates.verifyChecksum(file, "0000000000000000000000000000000000000000000000000000000000000000");
                fail("Expected checksum validation to fail");
            } catch (IOException expected) {
                assertTrue(expected.getMessage().contains("SHA-256 mismatch"));
            }
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
