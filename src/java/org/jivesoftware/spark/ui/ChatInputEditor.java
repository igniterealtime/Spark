/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * This is implementation of ChatArea that should be used as the sendField
 * in any chat room implementation.
 */
public class ChatInputEditor extends ChatArea implements DocumentListener {
	private static final long serialVersionUID = -3085035737908538581L;
	private final UndoManager undoManager;
	private KeyStroke undoKeyStroke;
	private KeyStroke ctrlbackspaceKeyStroke;
	private KeyStroke escapeKeyStroke;

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
		try {
		    undoManager.undo();
		} catch (CannotUndoException cue) {
		    // no more undoing for you
		}
            }
        };
        
        Action escape = new AbstractAction() {
	    private static final long serialVersionUID = -2973535045376312313L;
	    @Override
	    public void actionPerformed(ActionEvent e) {
		SparkManager.getChatManager().getChatContainer().closeActiveRoom();	
	    }
	};
        
	Action ctrlbackspace = new AbstractAction() {
	    private static final long serialVersionUID = -2973535045376312313L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		
		// We have Text selected, remove it
		if (getSelectedText() != null && getSelectedText().length() > 0) {
		   ChatInputEditor.this.removeWordInBetween(getSelectionStart(),
			    getSelectionEnd());

		    // We are somewhere in betwee 0 and str.length
		} else if (getCaretPosition() < getText().length()) {

		    String preCaret = getText()
			    .substring(0, getCaretPosition());

		    int lastSpace = preCaret.lastIndexOf(" ") != -1 ? preCaret
			    .lastIndexOf(" ") : 0;

		    if (lastSpace != -1 && lastSpace!=0)
		    {	
			// Do we have anymore spaces before the current one?
			for (int i = lastSpace; getText().charAt(i) == ' '; --i) {
			    lastSpace--;
			}
			lastSpace++;
		    }
		    ChatInputEditor.this.removeWordInBetween(lastSpace,
			    getCaretPosition());

		    if (lastSpace <= getText().length()) {
			setCaretPosition(lastSpace);
		    } else {
			setCaretPosition(getText().length());
		    }

		    // We are at the end and will remove until the next SPACE
		} else if (getText().contains(" ")) {
		    int untilhere = getText().lastIndexOf(" ");

		    // Do we have anymore spaces before the last one?
		    for (int i = untilhere; getText().charAt(i) == ' '; --i) {
			untilhere--;
		    }
		    untilhere++;
		    ChatInputEditor.this.removeLastWord(getText().substring(
			    untilhere));
		} else {
		    ChatInputEditor.this.removeLastWord(getText());
		}
	    }
	};

        undoKeyStroke = KeyStroke.getKeyStroke('z', ActionEvent.CTRL_MASK);       
        ctrlbackspaceKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_MASK);
        escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        
        getInputMap().put(ctrlbackspaceKeyStroke, "ctrlbackspace");
        getInputMap().put(undoKeyStroke, "undo");
        getInputMap().put(escapeKeyStroke, "escape");
        getInputMap().put(KeyStroke.getKeyStroke("Ctrl W"), "escape");
        
        registerKeyboardAction(undo, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        registerKeyboardAction(ctrlbackspace, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        registerKeyboardAction(escape, KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        
        getDocument().addDocumentListener(this);

        addMouseListener(this);
    }

    public void insertUpdate(DocumentEvent e) {
        //    this.setCaretPosition(e.getOffset());
        this.requestFocusInWindow();
    }

    /**
     * Appends the Text at the end
     */
    public void setText(String str) {
        super.setText(str);
    }

    public void removeUpdate(DocumentEvent e) {
    }

    public void changedUpdate(DocumentEvent e) {
    }

    /**
     * Remove dependices when no longer in use.
     */
    public void close() {
        getDocument().removeDocumentListener(this);
        getDocument().removeUndoableEditListener(undoManager);
        removeMouseListener(this);
        getInputMap().remove(undoKeyStroke);
        getInputMap().remove(ctrlbackspaceKeyStroke);
        getInputMap().remove(escapeKeyStroke);
        getInputMap().remove(KeyStroke.getKeyStroke("Ctrl W"));
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