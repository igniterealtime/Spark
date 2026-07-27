package org.jivesoftware.spark;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserManagerTest {

    @Test
    public void escapeJidKeepsUsernameWithoutDomain() {
        assertEquals("alice", UserManager.escapeJID("alice"));
    }

    @Test
    public void escapeJidEscapesCompleteJidLocalpart() {
        assertEquals("alice\\20smith@example.org", UserManager.escapeJID("alice smith@example.org"));
    }

    @Test
    public void escapeJidKeepsNull() {
        assertNull(UserManager.escapeJID(null));
    }
}
