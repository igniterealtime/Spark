package org.jivesoftware.spark.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TelephoneUtilsTest {

    @Test
    public void testGetNumbersFromPhone() {
        assertEquals("503[972]7215", TelephoneUtils.getNumbersFromPhone("1503-([972])-7215 "));
    }

    @Test
    public void testRemoveInvalidChars() {
        assertEquals("15039727215", TelephoneUtils.removeInvalidChars("1503-([972])-7215"));
    }

    @Test
    public void testFormatPattern() {
        assertEquals("0(34)3255-223478", TelephoneUtils.formatPattern("0(34-325-)5223478", "x(xx)xxxx-xxxx"));
        assertEquals("(503)972-7215", TelephoneUtils.formatPattern("503-([972])-7215", "(xxx)xxx-xxxx"));
    }

    @Test
    public void testFormatPhoneNumber() {
        assertEquals("(503)  972-7215", TelephoneUtils.formatPhoneNumber("5039727215"));
        assertEquals("12345678901", TelephoneUtils.formatPhoneNumber(" 12345678901 "));
    }
}
