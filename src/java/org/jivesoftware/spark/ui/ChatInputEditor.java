/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

/**
 * This is implementation of ChatArea that should be used as the sendField
 * in any chat room implementation.
 */
public class ChatInputEditor extends ChatArea implements DocumentListener {

    private final UndoManager undoManager = new UndoManager();

    /**
     * Creates a new Default ChatSendField.
     */
    public ChatInputEditor() {
        this.setDragEnabled(true);
        this.getDocument().addUndoableEditListener(undoManager);
        Action undo = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                undoManager.undo();
            }
        };

        this.getInputMap().put(KeyStroke.getKeyStroke('z', ActionEvent.CTRL_MASK), "undo");
        this.registerKeyboardAction(undo, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.getDocument().addDocumentListener(this);

        addMouseListener(this);
    }

    public void insertUpdate(DocumentEvent e) {
        //    this.setCaretPosition(e.getOffset());
        this.requestFocusInWindow();
    }

    public void setText(String str) {
        // Do nothing.
    }

    public void removeUpdate(DocumentEvent e) {
    }

    public void changedUpdate(DocumentEvent e) {
    }

    /**
     * Remove dependices when no longer in use.
     */
    public void close() {
        this.getDocument().removeDocumentListener(this);
        this.getDocument().removeUndoableEditListener(undoManager);
        this.removeMouseListener(this);
    }

    /**
     * Disables the Chat Editor, rendering it to the system default
     * color.
     */
    public void showAsDisabled() {
        this.setEditable(false);
        this.setEnabled(false);

        clear();

        final Color disabledColor = (Color)UIManager.get("Button.disabled");

        this.setBackground(disabledColor);
    }

    /**
     * Enable the Chat Editor.
     */
    public void showEnabled() {
        this.setEditable(true);
        this.setEnabled(true);

        this.setBackground(Color.white);
    }

}