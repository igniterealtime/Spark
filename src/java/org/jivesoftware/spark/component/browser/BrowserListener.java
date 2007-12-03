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
 * @author Derek DeMoro
 */
public interface BrowserListener {

    /**
     * Called when a document/page has been fully loaded.
     *
     * @param documentURL URL of the document that was loaded.
     */
    void documentLoaded(String documentURL);
}
