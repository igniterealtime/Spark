package org.jivesoftware.spark.uri;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for the retrievePassword method of UriManager.
 * This method extracts the password from a given URI if present.
 * xmpp:open_chat@conference.igniterealtime.org?join;password=somesecret
 */
public class UriManagerTest {

    @Test
    public void testRetrieveParam() throws URISyntaxException {
        UriManager uriManager = new UriManager();
        // URI with a password present as a semicolon-delimited param
        URI testUri = uriManager.parseXmppUri("xmpp:conference@example.com?join;password=secret123%20%40%23%24%25%5E%26");
        String password = uriManager.retrieveParam(testUri, "password");
        assertEquals("secret123 @#$%^&", password);

        // URI with a password and additional parameters
        testUri = uriManager.parseXmppUri("xmpp:conference@example.com?join;password=secret123&name=john");
        password = uriManager.retrieveParam(testUri, "password");
        assertEquals("secret123", password);

        // URI with an empty password parameter
        testUri = uriManager.parseXmppUri("xmpp:conference@example.com?join;password=");
        password = uriManager.retrieveParam(testUri, "password");
        assertNull(password);

        // URI without a password parameter
        testUri = uriManager.parseXmppUri("xmpp:conference@example.com?join;");
        password = uriManager.retrieveParam(testUri, "password");
        assertNull(password);

        // URI with no query string
        testUri = uriManager.parseXmppUri("xmpp:conference@example.com");
        password = uriManager.retrieveParam(testUri, "password");
        assertNull(password);
    }
}
