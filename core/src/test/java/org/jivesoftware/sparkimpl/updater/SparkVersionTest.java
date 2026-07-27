package org.jivesoftware.sparkimpl.updater;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SparkVersionTest {

    @Test
    public void normalizesSupportedArchitectures() {
        String original = System.getProperty("os.arch");
        try {
            System.setProperty("os.arch", "i686");
            assertEquals("x86", SparkVersion.currentArchitecture());

            System.setProperty("os.arch", "amd64");
            assertEquals("x64", SparkVersion.currentArchitecture());

            System.setProperty("os.arch", "aarch64");
            assertEquals("arm64", SparkVersion.currentArchitecture());
        } finally {
            if (original == null) {
                System.clearProperty("os.arch");
            } else {
                System.setProperty("os.arch", original);
            }
        }
    }
}
