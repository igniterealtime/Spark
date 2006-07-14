/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;


import org.jivesoftware.spark.util.log.Log;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Creates a new CoBrowser component. The CoBrowser is ChatRoom specific and is used
 * to control the end users browser.  Using the CoBrowser allows you to assist end customers
 * by directing them to the appropriate site.
 */
public class HTMLViewer extends JPanel {
    private JEditorPane browser;


    /**
     * Creates a new CoBrowser object to be used with the specifid ChatRoom.
     */
    public HTMLViewer() {
        final JPanel mainPanel = new JPanel();
        browser = new JEditorPane();
        browser.setEditorKit(new HTMLEditorKit());

        setLayout(new GridBagLayout());

        this.add(mainPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * Sets the HTML content of the viewer.
     *
     * @param text the html content.
     */
    public void setHTMLContent(String text) {
        browser.setText(text);
    }

    /**
     * Loads a URL into the Viewer.
     *
     * @param url the url.
     */
    public void loadURL(String url) {
        try {
            if (url.startsWith("www")) {
                url = "http://" + url;
            }
            browser.setPage(url);
        }
        catch (Exception ex) {
            Log.error(ex);
        }
    }


    /**
     * Returns the selected text contained in this TextComponent. If the selection is null or the document empty, returns null.
     *
     * @return the text.
     */
    public String getSelectedText() {
        return browser.getSelectedText();
    }


    /**
     * Let's make sure that the panel doesn't strech past the
     * scrollpane view pane.
     *
     * @return the preferred dimension
     */
    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }

    /**
     * Adds a hyperlink listener for notification of any changes, for example when a link is selected and entered.
     *
     * @param listener the listener
     */
    public void setHyperlinkListener(HyperlinkListener listener) {
        browser.addHyperlinkListener(listener);
    }
}
