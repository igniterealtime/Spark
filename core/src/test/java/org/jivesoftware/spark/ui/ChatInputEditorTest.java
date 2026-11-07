package org.jivesoftware.spark.ui;

import org.junit.Test;

import javax.swing.text.BadLocationException;

import static org.junit.Assert.assertEquals;

public class ChatInputEditorTest {

    @Test
    public void testAppendText() {
        ChatInputEditor editor = new ChatInputEditor();
        editor.setText("Hello");
        editor.appendText(" World");
        assertEquals("Hello World", editor.getText());
    }

    @Test
    public void testInsertText() throws BadLocationException {
        ChatInputEditor editor = new ChatInputEditor();
        editor.setText("HelloWorld");
        editor.setCaretPosition(5);
        editor.insertText(" ");
        assertEquals("Hello World", editor.getText());
    }

    @Test
    public void testClose() {
        ChatInputEditor editor = new ChatInputEditor();
        editor.close();
    }
}
