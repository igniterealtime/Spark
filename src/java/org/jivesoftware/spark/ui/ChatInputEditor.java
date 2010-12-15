/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoManager;

import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * This is implementation of ChatArea that should be used as the sendField
 * in any chat room implementation.
 */
public class ChatInputEditor extends ChatArea implements DocumentListener {
	private static final long serialVersionUID = -3085035737908538581L;
	private final UndoManager undoManager;
    private KeyStroke keyStroke;

    /**
     * Creates a new Default ChatSendField.
     */
    public ChatInputEditor() {
        undoManager = new UndoManager();

        this.setDragEnabled(true);
        this.getDocument().addUndoableEditListener(undoManager);
        Action undo = new AbstractAction() {
			private static final long serialVersionUID = -8897769620508545403L;
			public void actionPerformed(ActionEvent e) {
                undoManager.undo();
            }
        };

        keyStroke = KeyStroke.getKeyStroke('z', ActionEvent.CTRL_MASK);
        this.getInputMap().put(keyStroke, "undo");
        
        this.registerKeyboardAction(undo, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.getDocument().addDocumentListener(this);

        this.addMouseListener(this);
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
        this.getInputMap().remove(keyStroke);
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

    /**
     * Inserts text into the current document.
     *
     * @param text the text to insert
     * @throws BadLocationException if the location is not available for insertion.
     */
    @Override
    public void insertText(String text) throws BadLocationException {
        final Document doc = getDocument();
        styles.removeAttribute("link");
        doc.insertString(this.getCaret().getDot(), text, styles);
    }

    /**
     * Inserts text into the current document.
     *
     * @param text  the text to insert
     * @param color the color of the text
     * @throws BadLocationException if the location is not available for insertion.
     */
    @Override
    public void insertText(String text, Color color) throws BadLocationException {
        final Document doc = getDocument();
        StyleConstants.setForeground(styles, color);
        doc.insertString(this.getCaret().getDot(), text, styles);
    }

    /**
     * Inserts a link into the current document.
     *
     * @param link - the link to insert( ex. http://www.javasoft.com )
     * @throws BadLocationException if the location is not available for insertion.
     */
    @Override
    public void insertLink(String link) throws BadLocationException {
        final Document doc = getDocument();
        styles.addAttribute("link", link);

        StyleConstants.setForeground(styles, (Color)UIManager.get("Link.foreground"));
        StyleConstants.setUnderline(styles, true);
        doc.insertString(this.getCaret().getDot(), link, styles);
        StyleConstants.setUnderline(styles, false);
        StyleConstants.setForeground(styles, (Color)UIManager.get("TextPane.foreground"));
        styles.removeAttribute("link");
        setCharacterAttributes(styles, false);

    }
    
     /**
     * Inserts a network address into the current document. 
     *
     * @param address - the address to insert( ex. \superpc\etc\file\ OR http://localhost/ )
     * @throws BadLocationException if the location is not available for insertion.
     */
    @Override
    public void insertAddress(String address) throws BadLocationException {
        final Document doc = getDocument();
        styles.addAttribute("link", address);

        StyleConstants.setForeground(styles, (Color)UIManager.get("Address.foreground"));
        StyleConstants.setUnderline(styles, true);
        doc.insertString(this.getCaret().getDot(), address, styles);
        StyleConstants.setUnderline(styles, false);
        StyleConstants.setForeground(styles, (Color)UIManager.get("TextPane.foreground"));
        styles.removeAttribute("link");
        setCharacterAttributes(styles, false);

    }

    /**
     * Inserts an emotion icon into the current document.
     *
     * @param imageKey - the smiley representation of the image.( ex. :) )
     * @return true if the image was found, otherwise false.
     */
    @Override
    public boolean insertImage(String imageKey) {
    	
        if(!forceEmoticons && !SettingsManager.getLocalPreferences().areEmoticonsEnabled() || !emoticonsAvailable){
            return false;
        }
        final Document doc = getDocument();
        Icon emotion = emoticonManager.getEmoticonImage(imageKey.toLowerCase());
        if (emotion == null) {
            return false;
        }

        select(doc.getLength(), doc.getLength());
        insertIcon(emotion);

        return true;
    }
    
}