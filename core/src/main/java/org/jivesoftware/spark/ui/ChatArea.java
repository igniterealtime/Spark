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
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jivesoftware.spark.util.GraphicUtils.DEFAULT_CURSOR;
import static org.jivesoftware.spark.util.GraphicUtils.HAND_CURSOR;

/**
 * The ChatArea class handles proper chat text formatting such as url handling.
 * Use ChatArea for proper formatting of bold, italics, underlined and urls.
 */
public class ChatArea extends JTextPane implements MouseListener, MouseMotionListener {
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

    protected boolean forceEmoticons = false;

    /**
     * ChatArea Constructor.
     */
    public ChatArea() {
        cutMenu = new JMenuItem(Res.getString("action.cut"));
        cutMenu.addActionListener(e -> cutAction());

        copyMenu = new JMenuItem(Res.getString("action.copy"));
        copyMenu.addActionListener(e -> SparkManager.setClipboard(getSelectedText()));

        pasteMenu = new JMenuItem(Res.getString("action.paste"));
        pasteMenu.addActionListener(e -> pasteAction());

        selectAll = new JMenuItem(Res.getString("action.select.all"));
        selectAll.addActionListener(e -> {
            requestFocus();
            selectAll();
        });

        // Set Default Font
        setFont(new Font("Dialog", Font.PLAIN, fontSize));

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(VK_X, CTRL_DOWN_MASK), "cut");
        getActionMap().put("cut", new AbstractAction("cut") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                cutAction();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(VK_C, CTRL_DOWN_MASK), "copy");
        getActionMap().put("copy", new AbstractAction("copy") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SparkManager.setClipboard(getSelectedText());
            }
        });

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(VK_V, CTRL_DOWN_MASK), "paste");
        getActionMap().put("paste", new AbstractAction("paste") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                pasteAction();
            }
        });

        setEditorKit(new WrapEditorKit()); // SPARK-1613 Ensure that long text wraps.
    }

    /**
     * setText is a core JTextPane method that can be used to inject a different Document type
     * for instance HTMLDocument (setText("<HTML></HTML>")
     * We should keep the functionality - it is useful when we want to inject a different Document type
     * instead of StyleDocument
     */
    public void setInitialContent(String content) {
        super.setText(content);
    }

    /**
     * Removes the last appearance of the word from the TextArea
     */
    public void removeLastWord(String word) {
        select(getText().lastIndexOf(word), getText().length());
        replaceSelection("");
    }

    /**
     * Removes everything in between <b>begin</b> and <b>end</b>
     */
    public void removeWordInBetween(int begin, int end) {
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
            final int pos = viewToModel2D(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);
            if (element != null) {
                final AttributeSet as = element.getAttributes();
                final String url = (String) as.getAttribute("link");
                if (url != null) {
                    boolean handled = fireLinkInterceptors(e, url);
                    if (!handled) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (url.startsWith("xmpp:")) {
                                // eg: xmpp:open_chat@conference.igniterealtime.org?join;password=somesecret
                                SparkManager.getUriManager().handleURIMapping(url, false);
                            } else {
                                BrowserLauncher.openURL(url);
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            JPopupMenu popupmenu = new JPopupMenu();
                            JMenuItem linkcopy = new JMenuItem(Res.getString("action.copy"));
                            linkcopy.addActionListener(e1 -> SparkManager.setClipboard(url));
                            linkcopy.setEnabled(true);
                            popupmenu.add(linkcopy);
                            popupmenu.show(this, e.getX(), e.getY());
                        }
                    }
                }
            }
        } catch (Exception ex) {
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
     * Checks to see if the mouse is located over a browseable link.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        checkForLink(e);
    }

    /**
     * Checks to see if the mouse is located over a browseable link.
     */
    private void checkForLink(MouseEvent e) {
        try {
            final int pos = viewToModel2D(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);
            if (element != null) {
                final AttributeSet as = element.getAttributes();
                final Object o = as.getAttribute("link");
                setCursor(o != null ? HAND_CURSOR : DEFAULT_CURSOR);
            }
        } catch (Exception ex) {
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
        boolean textSelected = !isBlank(getSelectedText());
        String clipboard = SparkManager.getClipboard();
        cutMenu.setEnabled(textSelected && isEditable());
        copyMenu.setEnabled(textSelected);
        pasteMenu.setEnabled(!isBlank(clipboard) && isEditable());

        popup.show(this, e.getX(), e.getY());
    }

    public void addContextMenuListener(ContextMenuListener listener) {
        contextMenuListeners.addIfAbsent(listener);
    }

    public void removeContextMenuListener(ContextMenuListener listener) {
        contextMenuListeners.remove(listener);
    }

    private void fireContextMenuListeners() {
        for (final ContextMenuListener listener : contextMenuListeners) {
            try {
                listener.poppingUp(this, popup);
            } catch (Exception e) {
                Log.error("A ContextMenuListener (" + listener + ") threw an exception while processing a 'poppingUp' event. ChatArea: '" + this + "', popup: '" + popup + "'.", e);
            }
        }
    }

    public void addLinkInterceptor(LinkInterceptor interceptor) {
        interceptors.addIfAbsent(interceptor);
    }

    public void removeLinkInterceptor(LinkInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public boolean fireLinkInterceptors(MouseEvent event, String link) {
        for (final LinkInterceptor interceptor : interceptors) {
            try {
                final boolean handled = interceptor.handleLink(event, link);
                if (handled) {
                    return true;
                }
            } catch (Exception e) {
                Log.error("A LinkInterceptor (" + interceptor + ") threw an exception while processing link: '" + link + "', event: '" + event + "'.", e);
            }
        }
        return false;
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

    public boolean getForceEmoticons() {
        return forceEmoticons;
    }

    public void setForceEmoticons(boolean forceEmoticons) {
        this.forceEmoticons = forceEmoticons;
    }
}
