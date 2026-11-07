/**
 * Utility functions for DNS lookups.
 */
package org.jivesoftware.spark.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.jivesoftware.spark.util.log.Log;

public final class DnsUtils {

    private DnsUtils() {
    }

    /**
     * Resolve the Kerberos Key Distribution Center (KDC) hostname for a given Kerberos realm using DNS SRV records.
     * Makes DNS SRV record lookups (_kerberos._udp.<realm>) via JNDI.
     * Checks UDP first, falling back to TCP if not found.
     */
    public static String getDnsKdc(String realm) {
        if (realm == null || realm.isBlank()) {
            return null;
        }
        String kdc = lookupSrvRecord("_kerberos._udp." + realm);
        if (kdc == null) {
            kdc = lookupSrvRecord("_kerberos._tcp." + realm);
        }
        return kdc;
    }

    private static String lookupSrvRecord(String dnsQuery) {
        var env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

        DirContext context = null;
        try {
            context = new InitialDirContext(env);
            Attributes attrs = context.getAttributes(dnsQuery, new String[]{"SRV"});
            if (attrs == null) {
                return null;
            }
            // TreeMap keeps records sorted by priority ascending (lowest priority number chosen first)
            var recordsByPriority = new TreeMap<Integer, List<String>>();
            NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll();
            while (attrEnum != null && attrEnum.hasMoreElements()) {
                Attribute attr = attrEnum.nextElement();
                NamingEnumeration<?> valEnum = attr.getAll();
                while (valEnum != null && valEnum.hasMoreElements()) {
                    String record = String.valueOf(valEnum.nextElement());
                    parseSrvRecord(record, recordsByPriority);
                }
            }
            return getBestHost(recordsByPriority);
        } catch (NamingException e) {
            Log.debug("DNS SRV lookup failed for " + dnsQuery + ": " + e.getMessage());
            return null;
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException ignored) {
                }
            }
        }
    }

    static void parseSrvRecord(String record, Map<Integer, List<String>> recordsByPriority) {
        if (record == null) {
            return;
        }
        String[] parts = record.trim().split("\\s+");
        if (parts.length >= 4) {
            try {
                int priority = Integer.parseInt(parts[0]);
                recordsByPriority.computeIfAbsent(priority, k -> new ArrayList<>()).add(parts[3]);
            } catch (NumberFormatException ignored) {
                Log.debug("Invalid priority in DNS SRV record: " + record);
            }
        }
    }

    static String getBestHost(Map<Integer, List<String>> recordsByPriority) {
        if (recordsByPriority == null || recordsByPriority.isEmpty()) {
            return null;
        }
        // Lowest priority value is the highest precedence per RFC 2782
        int lowestPriority = recordsByPriority.keySet().iterator().next();
        List<String> hosts = recordsByPriority.get(lowestPriority);
        if (hosts == null || hosts.isEmpty()) {
            return null;
        }
        String targetHost = hosts.get(0);
        return targetHost.endsWith(".") ? targetHost.substring(0, targetHost.length() - 1) : targetHost;
    }
}
