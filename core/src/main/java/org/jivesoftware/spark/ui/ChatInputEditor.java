/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.jivesoftware.spark.SparkManager;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_Z;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;

/**
 * This is an implementation of ChatArea that should be used as the sendField
 * in any chat room implementation.
 */
public class ChatInputEditor extends ChatArea implements DocumentListener {
    private final UndoManager undoManager = new UndoManager();

    public ChatInputEditor() {
        setDragEnabled(true);
        getDocument().addUndoableEditListener(undoManager);

        Action undoAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.undo();
                } catch (CannotUndoException ignored) {
                }
            }
        };

        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SparkManager.getChatManager().getChatContainer().closeActiveRoom();
            }
        };

        Action ctrlBackspaceAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCtrlBackspace();
            }
        };

        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(VK_Z, CTRL_DOWN_MASK);
        KeyStroke ctrlBackspaceKeyStroke = KeyStroke.getKeyStroke(VK_BACK_SPACE, CTRL_DOWN_MASK);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(VK_ESCAPE, 0);
        KeyStroke ctrlWKeyStroke = KeyStroke.getKeyStroke(VK_W, CTRL_DOWN_MASK);

        getInputMap().put(ctrlBackspaceKeyStroke, "ctrlbackspace");
        getInputMap().put(undoKeyStroke, "undo");
        getInputMap().put(escapeKeyStroke, "escape");
        getInputMap().put(ctrlWKeyStroke, "escape");

        registerKeyboardAction(undoAction, undoKeyStroke, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        registerKeyboardAction(ctrlBackspaceAction, ctrlBackspaceKeyStroke, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        registerKeyboardAction(escapeAction, escapeKeyStroke, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        getDocument().addDocumentListener(this);
        addMouseListener(this);
    }

    private void handleCtrlBackspace() {
        String selected = getSelectedText();
        if (selected != null && !selected.isEmpty()) {
            removeWordInBetween(getSelectionStart(), getSelectionEnd());
            return;
        }

        String text = getText();
        int caretPos = getCaretPosition();
        if (caretPos < text.length()) {
            String preCaret = text.substring(0, caretPos);
            int lastSpace = preCaret.lastIndexOf(' ');
            if (lastSpace > 0) {
                for (int i = lastSpace; i >= 0 && text.charAt(i) == ' '; --i) {
                    lastSpace--;
                }
                lastSpace++;
            } else {
                lastSpace = 0;
            }
            removeWordInBetween(lastSpace, caretPos);
            setCaretPosition(Math.min(lastSpace, getText().length()));
            return;
        }

        if (text.contains(" ")) {
            int untilHere = text.lastIndexOf(' ');
            for (int i = untilHere; i >= 0 && text.charAt(i) == ' '; --i) {
                untilHere--;
            }
            untilHere++;
            removeLastWord(text.substring(untilHere));
        } else {
            removeLastWord(text);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        requestFocusInWindow();
    }

    /**
     * Appends the text at the end.
     */
    public void appendText(String str) {
        setText(getText() + str);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    /**
     * Remove dependencies when no longer in use.
     */
    public void close() {
        getDocument().removeDocumentListener(this);
        getDocument().removeUndoableEditListener(undoManager);
        removeMouseListener(this);
    }

    /**
     * Disables the chat editor, rendering it to the system default color.
     */
    public void showAsDisabled() {
        setEditable(false);
        setEnabled(false);
        clear();
    }

    /**
     * Inserts text into the current document at the current caret position.
     *
     * @param text the text to insert
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertText(String text) throws BadLocationException {
        Document doc = getDocument();
        doc.insertString(getCaret().getDot(), text, null);
    }
}
