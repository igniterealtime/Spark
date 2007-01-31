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

import org.jdesktop.jdic.browser.BrowserEngineManager;
import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementation of BrowserViewer using Native Browsers (IE / Mozilla)
 *
 * @author Derek DeMoro
 */
class NativeBrowserViewer extends BrowserViewer implements WebBrowserListener {
    private WebBrowser browser;

    public void initializeBrowser() {
        BrowserEngineManager bem = BrowserEngineManager.instance();
        if (Spark.isWindows()) {
            bem.setActiveEngine(BrowserEngineManager.IE);
        }
        else {
            bem.setActiveEngine(BrowserEngineManager.MOZILLA);
        }
        browser = new WebBrowser();


        this.setLayout(new BorderLayout());

        this.add(browser, BorderLayout.CENTER);

        browser.addWebBrowserListener(this);
    }

    public void loadURL(String url) {
        try {
            browser.setURL(new URL(url));
        }
        catch (MalformedURLException e) {
            Log.error(e);
        }
    }

    public void goBack() {
        browser.back();
    }

    public void downloadStarted(WebBrowserEvent event) {
    }

    public void downloadCompleted(WebBrowserEvent event) {
        if (browser == null || browser.getURL() == null) {
            return;
        }

        String url = browser.getURL().toExternalForm();
        documentLoaded(url);
    }

    public void downloadProgress(WebBrowserEvent event) {

    }

    public void downloadError(WebBrowserEvent event) {

    }

    public void documentCompleted(WebBrowserEvent event) {

    }

    public void titleChange(WebBrowserEvent event) {

    }

    public void statusTextChange(WebBrowserEvent event) {

    }


    public void initializationCompleted(WebBrowserEvent webBrowserEvent) {
    }
}
