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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * The ChatArea class handles proper chat text formatting such as url handling. Use ChatArea for proper
 * formatting of bold, italics, underlined and urls.
 */
public class ChatArea extends JTextPane implements MouseListener, MouseMotionListener, ActionListener {
	private static final long serialVersionUID = -2155445968040220072L;

	/**
     * The SimpleAttributeSet used within this instance of JTextPane.
     */
    public final SimpleAttributeSet styles = new SimpleAttributeSet();

    /**
     * The default Hand cursor.
     */
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    /**
     * The default Text Cursor.
     */
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * The currently selected Font Family to use.
     */
    private String fontFamily;

    /**
     * The currently selected Font Size to use.
     */
    private int fontSize;

    private List<ContextMenuListener> contextMenuListener = new ArrayList<ContextMenuListener>();

    private JPopupMenu popup;


    private JMenuItem cutMenu;
    private JMenuItem copyMenu;
    private JMenuItem pasteMenu;
    private JMenuItem selectAll;

    private List<LinkInterceptor> interceptors = new ArrayList<LinkInterceptor>();

    protected EmoticonManager emoticonManager;

    protected Boolean forceEmoticons = false;
    
    protected Boolean emoticonsAvailable = true;

    /**
     * ChatArea Constructor.
     */
    public ChatArea() {
        emoticonManager = EmoticonManager.getInstance();
        
        Collection<String> emoticonPacks = null; 
        emoticonPacks = emoticonManager.getEmoticonPacks();
        
        if(emoticonPacks == null) {
        	emoticonsAvailable = false;
        }

        // Set Default Font
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        int fs = pref.getChatRoomFontSize();
        fontSize = fs;
        setFontSize(fs);


        cutMenu = new JMenuItem(Res.getString("action.cut"));
        cutMenu.addActionListener(this);

        copyMenu = new JMenuItem(Res.getString("action.copy"));
        copyMenu.addActionListener(this);

        pasteMenu = new JMenuItem(Res.getString("action.paste"));
        pasteMenu.addActionListener(this);

        selectAll = new JMenuItem(Res.getString("action.select.all"));
        selectAll.addActionListener(this);

        // Set Default Font
        setFont(new Font("Dialog", Font.PLAIN, 12));


        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl x"), "cut");

        getActionMap().put("cut", new AbstractAction("cut") {
			private static final long serialVersionUID = 9117190151545566922L;

			public void actionPerformed(ActionEvent evt) {
                cutAction();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl c"), "copy");

        getActionMap().put("copy", new AbstractAction("copy") {
			private static final long serialVersionUID = 4949716854440264528L;

			public void actionPerformed(ActionEvent evt) {
                SparkManager.setClipboard(getSelectedText());
            }
        });

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("Ctrl v"), "paste");

        getActionMap().put("paste", new AbstractAction("paste") {
			private static final long serialVersionUID = -8767763580660683678L;

			public void actionPerformed(ActionEvent evt) {
                pasteAction();
            }
        });

    }

    /**
     * Set the current text of the ChatArea.
     *
     * @param message inserts the text directly into the ChatArea
     */
    public void setText(String message) {
        // By default, use the hand cursor for link selection
        // and scrolling.
      //  setCursor(HAND_CURSOR);

        // Make sure the message is not null.
        //  message = message.trim();
        // Why?
//        message = message.replaceAll("/\"", "");
        if (ModelUtil.hasLength(message)) {
            try {
                insert(message);
            }
            catch (BadLocationException e) {
                Log.error(e);
            }
        }
    }
    /**
     * setText is a core JTextPane method that can beused to inject a different Document type
     * for instance HTMLDocument (setText("<HTML></HTML>")
     * We should keep the functionality - it is useful when we want to inject a different Document type
     * instead of StyleDocument 
     * @param content
     */
    public void setInitialContent(String content) {
        super.setText(content);
    } 
    
   
    /**
     * Removes the last appearance of word from the TextArea
     * @param word
     */
    public void removeLastWord(String word)
    {
	select(getText().lastIndexOf(word),getText().length());	
	replaceSelection("");
    }
    
    /**
     * Removes everything in between <b>begin</b> and <b>end</b>
     * @param begin
     * @param end
     */
    public void removeWordInBetween(int begin, int end){
	select(begin, end);
	replaceSelection("");
    }

    /**
     * Clear the current document. This will remove all text and element
     * attributes such as bold, italics, and underlining. Note that the font family  and
     * font size will be persisted.
     */
    public void clear() {
        super.setText("");
        if (fontFamily != null) {
            setFont(fontFamily);
        }

        if (fontSize != 0) {
            setFontSize(fontSize);
        }

        StyleConstants.setUnderline(styles, false);
        StyleConstants.setBold(styles, false);
        StyleConstants.setItalic(styles, false);
        setCharacterAttributes(styles, false);
    }


    /**
     * Does the actual insertion of text, adhering to the styles
     * specified during message creation in either the thin or thick client.
     *
     * @param text - the text to insert.
     * @throws BadLocationException if location is not available to insert into.
     */
    public void insert(String text) throws BadLocationException {
        boolean bold = false;
        boolean italic = false;
        boolean underlined = false;

        final StringTokenizer tokenizer = new StringTokenizer(text, " \n \t", true);
        while (tokenizer.hasMoreTokens()) {
            String textFound = tokenizer.nextToken();
            if ((textFound.startsWith("http://") || textFound.startsWith("ftp://")
                    || textFound.startsWith("https://") || textFound.startsWith("www.")) &&
                    textFound.indexOf(".") > 1) {
                insertLink(textFound);
            }
            else if ( textFound.startsWith("\\\\")  || (textFound.indexOf("://") > 0 && textFound.indexOf(".") < 1) ) {
                insertAddress(textFound);
            }     
            else if (!insertImage(textFound)) {
                insertText(textFound);
            }
        }

        // By default, always have decorations off.
        StyleConstants.setBold(styles, bold);
        StyleConstants.setItalic(styles, italic);
        StyleConstants.setUnderline(styles, underlined);
    }

    /**
     * Inserts text into the current document.
     *
     * @param text the text to insert
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertText(String text) throws BadLocationException {
        final Document doc = getDocument();
        styles.removeAttribute("link");
        doc.insertString(doc.getLength(), text, styles);
        setCaretPosition(doc.getLength());
    }

    /**
     * Inserts text into the current document.
     *
     * @param text  the text to insert
     * @param color the color of the text
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertText(String text, Color color) throws BadLocationException {
        final Document doc = getDocument();
        StyleConstants.setForeground(styles, color);
        doc.insertString(doc.getLength(), text, styles);
        setCaretPosition(doc.getLength());
    }

    /**
     * Inserts a link into the current document.
     *
     * @param link - the link to insert( ex. http://www.javasoft.com )
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertLink(String link) throws BadLocationException {
        final Document doc = getDocument();
        styles.addAttribute("link", link);

        StyleConstants.setForeground(styles, (Color)UIManager.get("Link.foreground"));
        StyleConstants.setUnderline(styles, true);
        doc.insertString(doc.getLength(), link, styles);
        StyleConstants.setUnderline(styles, false);
        StyleConstants.setForeground(styles, (Color)UIManager.get("TextPane.foreground"));
        styles.removeAttribute("link");
        setCharacterAttributes(styles, false);
        setCaretPosition(doc.getLength());

    }
    
     /**
     * Inserts a network address into the current document. 
     *
     * @param address - the address to insert( ex. \superpc\etc\file\ OR http://localhost/ )
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertAddress(String address) throws BadLocationException {
        final Document doc = getDocument();
        styles.addAttribute("link", address);

        StyleConstants.setForeground(styles, (Color)UIManager.get("Address.foreground"));
        StyleConstants.setUnderline(styles, true);
        doc.insertString(doc.getLength(), address, styles);
        StyleConstants.setUnderline(styles, false);
        StyleConstants.setForeground(styles, (Color)UIManager.get("TextPane.foreground"));
        styles.removeAttribute("link");
        setCharacterAttributes(styles, false);
        setCaretPosition(doc.getLength());

    }

    /**
     * Inserts an emotion icon into the current document.
     *
     * @param imageKey - the smiley representation of the image.( ex. :) )
     * @return true if the image was found, otherwise false.
     */
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
        setCaretPosition(doc.getLength());

        return true;
    }

    /**
     * Inserts horizontal line
     */
    public void insertHorizontalLine() {
        try {
            insertComponent( new JSeparator() );
            insertText("\n");
        }
        catch (BadLocationException e) {
            Log.error("Error message.", e);
        }
    }

    /**     
     * Sets the current element to be either bold or not depending
     * on the current state. If the element is currently set as bold,
     * it will be set to false, and vice-versa.
     */
    public void setBold() {
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null) {
            AttributeSet as = element.getAttributes();
            boolean isBold = StyleConstants.isBold(as);
            StyleConstants.setBold(styles, !isBold);
            try {
                setCharacterAttributes(styles, true);
            }
            catch (Exception ex) {
                Log.error("Error settings bold:", ex);
            }
        }
    }

    /**
     * Sets the current element to be either italicized or not depending
     * on the current state. If the element is currently set as italic,
     * it will be set to false, and vice-versa.
     */
    public void setItalics() {
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null) {
            AttributeSet as = element.getAttributes();
            boolean isItalic = StyleConstants.isItalic(as);
            StyleConstants.setItalic(styles, !isItalic);
            try {
                setCharacterAttributes(styles, true);
            }
            catch (Exception fontException) {
                Log.error("Error settings italics:", fontException);
            }
        }
    }

    /**
     * Sets the current document to be either underlined or not depending
     * on the current state. If the element is currently set as underlined,
     * it will be set to false, and vice-versa.
     */
    public void setUnderlined() {
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null) {
            AttributeSet as = element.getAttributes();
            boolean isUnderlined = StyleConstants.isUnderline(as);
            StyleConstants.setUnderline(styles, !isUnderlined);
            try {
                setCharacterAttributes(styles, true);
            }
            catch (Exception underlineException) {
                Log.error("Error settings underline:", underlineException);
            }
        }
    }

    /**
     * Set the font on the current element.
     *
     * @param font the font to use with the current element
     */
    public void setFont(String font) {
        StyleConstants.setFontFamily(styles, font);
        try {
            setCharacterAttributes(styles, false);
        }
        catch (Exception fontException) {
            Log.error("Error settings font:", fontException);
        }

        fontFamily = font;
    }

    /**
     * Set the current font size.
     *
     * @param size the current font size.
     */
    public void setFontSize(int size) {
        StyleConstants.setFontSize(styles, size);
        try {
            setCharacterAttributes(styles, false);
        }
        catch (Exception fontException) {
            Log.error("Error settings font:", fontException);
        }

        fontSize = size;
    }

    public void mouseClicked(MouseEvent e) {
        try {
            final int pos = viewToModel(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);

            if (element != null) {
                final AttributeSet as = element.getAttributes();
                final Object o = as.getAttribute("link");

                if (o != null) {
                    try {
                        final String url = (String)o;
                        boolean handled = fireLinkInterceptors(e, url);
                        if (!handled) {
                            if(e.getButton() == MouseEvent.BUTTON1)
                        	BrowserLauncher.openURL(url);
			    else if (e.getButton() == MouseEvent.BUTTON3) {
				JPopupMenu popupmenu = new JPopupMenu();
				JMenuItem linkcopy = new JMenuItem(
					Res.getString("action.copy"));
				linkcopy.addActionListener(new ActionListener() {

				    @Override
				    public void actionPerformed(ActionEvent e) {
					SparkManager.setClipboard(url);

				    }
				});
				linkcopy.setEnabled(true);
				popupmenu.add(linkcopy);
				popupmenu.show(this, e.getX(), e.getY());
			    }
                        }
                    }
                    catch (Exception ioe) {
                        Log.error("Error launching browser:", ioe);
                    }
                }
            }
        }
        catch (Exception ex) {
            Log.error("Visible Error", ex);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
        }
    }

    /**
     * This launches the <code>BrowserLauncher</code> with the URL
     * located in <code>ChatArea</code>. Note that the url will
     * automatically be clickable when added to <code>ChatArea</code>
     *
     * @param e - the MouseReleased event
     */
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
        }


    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Checks to see if the mouse is located over a browseable
     * link.
     *
     * @param e - the current MouseEvent.
     */
    public void mouseMoved(MouseEvent e) {
        checkForLink(e);
    }

    /**
     * Checks to see if the mouse is located over a browseable
     * link.
     *
     * @param e - the current MouseEvent.
     */
    private void checkForLink(MouseEvent e) {
        try {
            final int pos = viewToModel(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);

            if (element != null) {
                final AttributeSet as = element.getAttributes();
                final Object o = as.getAttribute("link");

                if (o != null) {
                    setCursor(HAND_CURSOR);
                }
                else {
                    setCursor(DEFAULT_CURSOR);
                }
            }
        }
        catch (Exception ex) {
            Log.error("Error in CheckLink:", ex);
        }
    }

    /**
     * Examines the chatInput text pane, and returns a string containing the text with any markup
     * (jive markup in our case). This will strip any terminating new line from the input.
     *
     * @return a string of marked up text.
     */
    public String getMarkup() {
        final StringBuffer buf = new StringBuffer();
        final String text = getText();
        final StyledDocument doc = getStyledDocument();
        final Element rootElem = doc.getDefaultRootElement();

        // MAY RETURN THIS BLOCK
        if (text.trim().length() <= 0) {
            return null;
        }

        boolean endsInNewline = text.charAt(text.length() - 1) == '\n';
        for (int j = 0; j < rootElem.getElementCount(); j++) {
            final Element pElem = rootElem.getElement(j);

            for (int i = 0; i < pElem.getElementCount(); i++) {
                final Element e = pElem.getElement(i);
                final AttributeSet as = e.getAttributes();
                final boolean bold = StyleConstants.isBold(as);
                final boolean italic = StyleConstants.isItalic(as);
                final boolean underline = StyleConstants.isUnderline(as);
                int end = e.getEndOffset();

                if (end > text.length()) {
                    end = text.length();
                }

                if (endsInNewline && end >= text.length() - 1) {
                    end--;
                }

                // swing text.. :-/
                if (j == rootElem.getElementCount() - 1
                        && i == pElem.getElementCount() - 1) {
                    end = text.length();
                }

                final String current = text.substring(e.getStartOffset(), end);
                if (bold) {
                    buf.append("[b]");
                }
                if (italic) {
                    buf.append("[i]");
                }
                if (underline) {
                    buf.append("[u]");
                }
                //buf.append( "[font face=/\"" + fontFamily + "/\" size=/\"" + fontSize + "/\"/]" );

                // Iterator over current string to find url tokens
                final StringTokenizer tkn = new StringTokenizer(current, " ", true);
                while (tkn.hasMoreTokens()) {
                    final String token = tkn.nextToken();
                    if (token.startsWith("http://") || token.startsWith("ftp://")
                            || token.startsWith("https://")) {
                        buf.append("[url]").append(token).append("[/url]");
                    }
                    else if (token.startsWith("www")) {
                        buf.append("[url ");
                        buf.append("http://").append(token);
                        buf.append("]");
                        buf.append(token);
                        buf.append("[/url]");
                    }
                    else {
                        buf.append(token);
                    }
                }

                // Always add end tags for markup
                if (underline) {
                    buf.append("[/u]");
                }
                if (italic) {
                    buf.append("[/i]");
                }
                if (bold) {
                    buf.append("[/b]");
                }
                //  buf.append( "[/font]" );
            }
        }

        return buf.toString();
    }

    private void handlePopup(MouseEvent e) {
        popup = new JPopupMenu();
        popup.add(cutMenu);
        popup.add(copyMenu);
        popup.add(pasteMenu);
        fireContextMenuListeners();
        popup.addSeparator();
        popup.add(selectAll);

        // Handle enable
        boolean textSelected = ModelUtil.hasLength(getSelectedText());
        String clipboard = SparkManager.getClipboard();
        cutMenu.setEnabled(textSelected && isEditable());
        copyMenu.setEnabled(textSelected);
        pasteMenu.setEnabled(ModelUtil.hasLength(clipboard) && isEditable());

        popup.show(this, e.getX(), e.getY());
    }

    /**
     * Adds a <code>ContextMenuListener</code> to ChatArea.
     *
     * @param listener the ContextMenuListener.
     */
    public void addContextMenuListener(ContextMenuListener listener) {
        contextMenuListener.add(listener);
    }

    /**
     * Remove a <code>ContextMenuListener</code> to ChatArea.
     *
     * @param listener the ContextMenuListener.
     */
    public void removeContextMenuListener(ContextMenuListener listener) {
        contextMenuListener.remove(listener);
    }

    private void fireContextMenuListeners() {
        for (ContextMenuListener listener : new ArrayList<ContextMenuListener>(contextMenuListener)) {
            listener.poppingUp(this, popup);
        }
    }

    public void addLinkInterceptor(LinkInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void removeLinkInterceptor(LinkInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public boolean fireLinkInterceptors(MouseEvent event, String link) {
        for (LinkInterceptor linkInterceptor : new ArrayList<LinkInterceptor>(interceptors)) {
            boolean handled = linkInterceptor.handleLink(event, link);
            if (handled) {
                return true;
            }
        }

        return false;
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cutMenu) {
            cutAction();
        }
        else if (e.getSource() == copyMenu) {
            SparkManager.setClipboard(getSelectedText());
        }
        else if (e.getSource() == pasteMenu) {
            pasteAction();
        }
        else if (e.getSource() == selectAll) {
            requestFocus();
            selectAll();
        }
    }

    private void cutAction() {
        String selectedText = getSelectedText();
        replaceSelection("");
        SparkManager.setClipboard(selectedText);
    }

    private void pasteAction() {
        String text = SparkManager.getClipboard();
        if (text != null) {
            replaceSelection(text);
        }
    }

    protected void releaseResources() {
        getActionMap().remove("copy");
        getActionMap().remove("cut");
        getActionMap().remove("paste");
    }

    public Boolean getForceEmoticons() {
        return forceEmoticons;
    }

    public void setForceEmoticons(Boolean forceEmoticons) {
        this.forceEmoticons = forceEmoticons;
    }
}