package org.jivesoftware.spark.uri;

import org.jivesoftware.Spark;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

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
    @ClassRule
    public static final TemporaryFolder tmpHomeFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty("user.home", tmpHomeFolder.getRoot().getAbsolutePath());
        Spark.initializeFolders(System.getProperties());
    }

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

    @Test
    public void testRetrieveJID() throws URISyntaxException, XmppStringprepException {
        UriManager uriManager = new UriManager();
        // Case with a user and host
        URI testUri = uriManager.parseXmppUri("xmpp:user@domain.com");
        assertEquals(JidCreate.entityBareFrom( "user@domain.com"),  uriManager.retrieveJID(testUri));

        // Case with a user, host, and a resource
        testUri = uriManager.parseXmppUri("xmpp:user@domain.com/resource");
        assertEquals(JidCreate.fullFrom( "user@domain.com/resource"), uriManager.retrieveJID(testUri));

        // Case with only a host
        testUri = uriManager.parseXmppUri("xmpp:domain.com");
        assertEquals(JidCreate.domainBareFrom("domain.com"), uriManager.retrieveJID(testUri));

        // Case with a host and a resource
        testUri = uriManager.parseXmppUri("xmpp:domain.com/resource");
        assertEquals(JidCreate.domainFullFrom("domain.com/resource"), uriManager.retrieveJID(testUri));

        // Case with a malformed URI
        testUri = uriManager.parseXmppUri("xmpp://@");
        assertNull(uriManager.retrieveJID(testUri));
    }
}
