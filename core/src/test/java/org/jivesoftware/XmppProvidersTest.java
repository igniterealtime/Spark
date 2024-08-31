package org.jivesoftware;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class XmppProvidersTest {

    @Test
    public void testParseProvidersJson() {
        List<String> providers = XmppProviders.parseProvidersJson("[\n" +
            "    \"07f.de\",\n" +
            "    \"404.city\",\n" +
            "    \"5222.de\"" +
            "]");
        assertEquals(asList("07f.de", "404.city", "5222.de"), providers);
    }

}
