package org.jivesoftware.spark.util;

import org.junit.Test;

import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Verifies that {@link StringUtils#keyStroke2String} decodes extended (DOWN) modifier masks.
 * Modifiers are emitted in order: shift, ctrl, meta, alt.
 */
public class StringUtilsKeyStrokeTest {

    @Test
    public void decodesCtrl() {
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        assertTrue(StringUtils.keyStroke2String(key).startsWith("ctrl "));
    }

    @Test
    public void decodesShiftAndCtrlInOrder() {
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
        assertTrue(StringUtils.keyStroke2String(key).startsWith("shift ctrl "));
    }

    @Test
    public void decodesAlt() {
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.ALT_DOWN_MASK);
        assertTrue(StringUtils.keyStroke2String(key).startsWith("alt "));
    }

    @Test
    public void noModifierHasNoModifierPrefix() {
        String result = StringUtils.keyStroke2String(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
        assertFalse(result.contains("ctrl "));
        assertFalse(result.contains("shift "));
        assertFalse(result.contains("alt "));
    }
}
