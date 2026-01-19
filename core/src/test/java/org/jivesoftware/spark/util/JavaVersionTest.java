package org.jivesoftware.spark.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaVersionTest
{
    @Test
    public void testJava7() {
        // Setup fixture.
        final String value = "1.7.0";

        // Execute system under test.
        final int result = StringUtils.getJavaMajorVersion( value );

        // Verify results.
        assertEquals( 7, result );
    }


    @Test
    public void testJava9() {
        // Setup fixture.
        final String value = "9.0.0.15";

        // Execute system under test.
        final int result = StringUtils.getJavaMajorVersion( value );

        // Verify results.
        assertEquals( 9, result );
    }

    @Test
    public void testJava11() {
        // Setup fixture.
        final String value = "11.0.1";

        // Execute system under test.
        final int result = StringUtils.getJavaMajorVersion( value );

        // Verify results.
        assertEquals( 11, result );
    }

    @Test
    public void testJava26ea() {
        // Setup fixture.
        final String value = "26-ea";

        // Execute system under test.
        final int result = StringUtils.getJavaMajorVersion( value );

        // Verify results.
        assertEquals( 26, result );
    }
}
