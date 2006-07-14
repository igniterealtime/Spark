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

import org.jivesoftware.Spark;
import org.jivesoftware.spark.component.HTMLViewer;

/**
 * Responsible for determining what type of Browser to return. On windows,
 * either IE or Mozilla will be returned. Otherwise, we will return a simple
 * HTMLViewer using JDK 1.4+ HTMLEditor.
 */
public class BrowserFactory {

    /**
     * Empty Constructor.
     */
    private BrowserFactory() {

    }

    /**
     * Returns the Browser UI to use for system Spark is currently running on.
     *
     * @return the BrowserViewer.
     * @see NativeBrowserViewer
     * @see HTMLViewer
     */
    public static BrowserViewer getBrowser() {
        BrowserViewer browserViewer = new NativeBrowserViewer();
        if (Spark.isWindows()) {

        }
        else {

        }
        browserViewer.initializeBrowser();
        return browserViewer;
    }
}
