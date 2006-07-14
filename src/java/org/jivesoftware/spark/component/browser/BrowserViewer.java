/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.browser;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class describing a particular type of component capable of rendering html.
 *
 * @author Derek DeMoro
 * @see NativeBrowserViewer
 */
public abstract class BrowserViewer extends JPanel {
    private List<BrowserListener> listeners = new ArrayList<BrowserListener>();

    /**
     * Add a BrowserListener.
     *
     * @param listener the listener.
     */
    public void addBrowserListener(BrowserListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a BrowserListener.
     *
     * @param listener the listener.
     */
    public void removeBrowserListener(BrowserListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire all BrowserListeners.
     *
     * @param document the document that has been downloaded.
     */
    public void fireBrowserListeners(String document) {
        for (BrowserListener listener : listeners) {
            listener.documentLoaded(document);
        }
    }


    public void documentLoaded(String document) {
        fireBrowserListeners(document);
    }

    /**
     * Should create the UI necessary to display html.
     */
    public abstract void initializeBrowser();

    /**
     * Should load the given url.
     *
     * @param url the url to load.
     */
    public abstract void loadURL(String url);

    /**
     * Should go back in history one page.
     */
    public abstract void goBack();

}
