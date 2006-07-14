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


/**
 * Implementation of <code>BrowserListener</code> allows for handling when documents
 * have been downloaded via a <code>BrowserViewer</code> implementation.
 *
 * @authro Derek DeMoro
 */
public interface BrowserListener {

    /**
     * Called when a document/page has been fully loaded.
     *
     * @param documentURL
     */
    void documentLoaded(String documentURL);
}
