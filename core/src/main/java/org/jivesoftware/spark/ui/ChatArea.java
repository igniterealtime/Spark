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

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.uri.UriManager;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.util.JidUtil;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The ChatArea class handles proper chat text formatting such as url handling. Use ChatArea for proper
 * formatting of bold, italics, underlined and urls.
 */
public class ChatArea extends JTextPane implements MouseListener, MouseMotionListener, ActionListener {

    /**
     * The default Hand cursor.
     */
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    /**
     * The default Text Cursor.
     */
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * The currently selected Font Size to use.
     */
    private final int fontSize = SettingsManager.getLocalPreferences().getChatRoomFontSize();
    private final CopyOnWriteArrayList<ContextMenuListener> contextMenuListeners = new CopyOnWriteArrayList<>();

    private JPopupMenu popup;

    private final JMenuItem cutMenu;
    private final JMenuItem copyMenu;
    private final JMenuItem pasteMenu;
    private final JMenuItem selectAll;

    private final CopyOnWriteArrayList<LinkInterceptor> interceptors = new CopyOnWriteArrayList<>();

    protected final EmoticonManager emoticonManager;

    protected Boolean forceEmoticons = false;
    
    protected Boolean emoticonsAvailable = true;

    /**
     * ChatArea Constructor.
     */
    public ChatArea() {
        emoticonManager = EmoticonManager.getInstance();
        
        Collection<String> emoticonPacks;
        emoticonPacks = emoticonManager.getEmoticonPacks();
        
        if(emoticonPacks == null) {
        	emoticonsAvailable = false;
        }

        cutMenu = new JMenuItem(Res.getString("action.cut"));
        cutMenu.addActionListener(this);

        copyMenu = new JMenuItem(Res.getString("action.copy"));
        copyMenu.addActionListener(this);

        pasteMenu = new JMenuItem(Res.getString("action.paste"));
        pasteMenu.addActionListener(this);

        selectAll = new JMenuItem(Res.getString("action.select.all"));
        selectAll.addActionListener(this);

        // Set Default Font
        setFont(new Font("Dialog", Font.PLAIN, fontSize));


        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl x"), "cut");

        getActionMap().put("cut", new AbstractAction("cut") {
			private static final long serialVersionUID = 9117190151545566922L;

			@Override
			public void actionPerformed(ActionEvent evt) {
                cutAction();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl c"), "copy");

        getActionMap().put("copy", new AbstractAction("copy") {
			private static final long serialVersionUID = 4949716854440264528L;

			@Override
			public void actionPerformed(ActionEvent evt) {
                SparkManager.setClipboard(getSelectedText());
            }
        });

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ctrl v"), "paste");

        getActionMap().put("paste", new AbstractAction("paste") {
			private static final long serialVersionUID = -8767763580660683678L;

			@Override
			public void actionPerformed(ActionEvent evt) {
                pasteAction();
            }
        });

        setEditorKit( new WrapEditorKit() ); // SPARK-1613 Ensure that long text wraps.
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
    }

    @Override
	public void mouseClicked(MouseEvent e) {
        try {
            final int pos = viewToModel(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);

            if (element != null) {
                final AttributeSet as = element.getAttributes();
                final String url = (String) as.getAttribute("link");

                if (url != null) {
                    try {
                        boolean handled = fireLinkInterceptors(e, url);
                        if (!handled) {
                            if(e.getButton() == MouseEvent.BUTTON1) {
                                if (url.startsWith("xmpp:") && url.contains("?join")) {
                                    // eg: xmpp:open_chat@conference.igniterealtime.org?join;password=somesecret
                                    URI uri = new URI(url);
                                    final String schemeSpecificPart = uri.getSchemeSpecificPart();
                                    final String roomAddress = schemeSpecificPart.substring(0, schemeSpecificPart.indexOf('?'));
                                    final String password = UriManager.retrievePassword(uri);
                                    final EntityBareJid roomJid = JidCreate.entityBareFrom(roomAddress);
                                    ConferenceUtils.joinConferenceOnSeperateThread(roomJid.getLocalpart().toString(), roomJid, null, password);
                                } else {
                                    BrowserLauncher.openURL(url);
                                }
                            }
			    else if (e.getButton() == MouseEvent.BUTTON3) {
				JPopupMenu popupmenu = new JPopupMenu();
				JMenuItem linkcopy = new JMenuItem(
					Res.getString("action.copy"));
				linkcopy.addActionListener( e1 -> SparkManager.setClipboard(url) );
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

    @Override
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
    @Override
	public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
        }


    }

    @Override
	public void mouseEntered(MouseEvent e) {
    }

    @Override
	public void mouseExited(MouseEvent e) {
    }

    @Override
	public void mouseDragged(MouseEvent e) {
    }

    /**
     * Checks to see if the mouse is located over a browseable
     * link.
     *
     * @param e - the current MouseEvent.
     */
    @Override
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
        contextMenuListeners.addIfAbsent(listener);
    }

    /**
     * Remove a <code>ContextMenuListener</code> to ChatArea.
     *
     * @param listener the ContextMenuListener.
     */
    public void removeContextMenuListener(ContextMenuListener listener) {
        contextMenuListeners.remove(listener);
    }

    private void fireContextMenuListeners()
    {
        for ( final ContextMenuListener listener : contextMenuListeners )
        {
            try
            {
                listener.poppingUp( this, popup );
            }
            catch ( Exception e )
            {
                Log.error( "A ContextMenuListener (" + listener + ") threw an exception while processing a 'poppingUp' event. ChatArea: '" + this + "', popup: '" + popup + "'.", e );
            }
        }
    }

    public void addLinkInterceptor(LinkInterceptor interceptor) {
        interceptors.addIfAbsent(interceptor);
    }

    public void removeLinkInterceptor(LinkInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public boolean fireLinkInterceptors( MouseEvent event, String link )
    {
        for ( final LinkInterceptor interceptor : interceptors )
        {
            try
            {
                final boolean handled = interceptor.handleLink( event, link );
                if ( handled )
                {
                    return true;
                }
            }
            catch ( Exception e )
            {
                Log.error( "A LinkInterceptor (" + interceptor + ") threw an exception while processing link: '" + link + "', event: '" + event + "'.", e );
            }
        }

        return false;
    }


    @Override
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
