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
	private static final long serialVersionUID = -5389246902135069702L;
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
