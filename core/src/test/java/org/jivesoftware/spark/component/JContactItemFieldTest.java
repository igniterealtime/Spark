package org.jivesoftware.spark.component;

import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JContactItemFieldTest {

    @Test
    public void acceptsContactNameCharacters() {
        assertTrue(JContactItemField.validateChar('a'));
        assertTrue(JContactItemField.validateChar('Z'));
        assertTrue(JContactItemField.validateChar('7'));
        assertTrue(JContactItemField.validateChar('@'));
        assertTrue(JContactItemField.validateChar('.'));
        assertTrue(JContactItemField.validateChar(' '));
        assertTrue(JContactItemField.validateChar((char) KeyEvent.VK_BACK_SPACE));
    }

    @Test
    public void rejectsUnrelatedCharacters() {
        assertFalse(JContactItemField.validateChar('#'));
        assertFalse(JContactItemField.validateChar('/'));
        // Former bug: modifier-mask values (CTRL_MASK=2, CTRL_DOWN_MASK=128) were treated as valid chars.
        assertFalse(JContactItemField.validateChar((char) 2));
        assertFalse(JContactItemField.validateChar((char) 128));
    }
}
