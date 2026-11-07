package org.jivesoftware.spark.util;

import java.util.List;
import java.util.TreeMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DnsUtilsTest {

    @Test
    public void testGetDnsKdcNullOrBlank() {
        assertNull(DnsUtils.getDnsKdc(null));
        assertNull(DnsUtils.getDnsKdc(""));
        assertNull(DnsUtils.getDnsKdc("   "));
    }

    @Test
    public void testParseSrvRecordAndGetBestHost() {
        var records = new TreeMap<Integer, List<String>>();
        DnsUtils.parseSrvRecord("10 50 88 kdc2.example.com.", records);
        DnsUtils.parseSrvRecord("0 100 88 kdc1.example.com.", records);
        DnsUtils.parseSrvRecord("20 10 88 kdc3.example.com", records);

        assertEquals("kdc1.example.com", DnsUtils.getBestHost(records));
    }

    @Test
    public void testParseSrvRecordInvalidFormat() {
        var records = new TreeMap<Integer, List<String>>();
        DnsUtils.parseSrvRecord(null, records);
        DnsUtils.parseSrvRecord("", records);
        DnsUtils.parseSrvRecord("invalid record format", records);
        DnsUtils.parseSrvRecord("abc 100 88 kdc.example.com.", records);

        assertNull(DnsUtils.getBestHost(records));
    }
}
