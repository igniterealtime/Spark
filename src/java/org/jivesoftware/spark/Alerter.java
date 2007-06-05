/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;

import java.awt.Window;
import java.io.File;

/**
 * Implementations of this interface define alert mechanisms based on the Operating System
 * Spark is running on.
 *
 * @author Derek DeMoro
 */
public interface Alerter {

    /**
     * Flash the window.
     *
     * @param window the window to flash.
     */
    void flashWindow(Window window);

    /**
     * Start the flashing of the given window, but stop flashing when the window takes focus.
     *
     * @param window the window to start flashing.
     */
    void flashWindowStopWhenFocused(Window window);

    /**
     * Stop the flashing of the given window.
     *
     * @param window the window to stop flashing.
     */
    void stopFlashing(Window window);

    /**
     * Return true if this <code>Alerter</code> should handle the alert request.
     *
     * @return true to handle.
     */
    boolean handleNotification();

    /**
     * Open File using native operation.
     *
     * @param file the name of the file.
     * @return true if the file was executed.
     */
    boolean openFile(File file);

    /**
     * Launches email client.
     *
     * @param to      who the message should go to.
     * @param subject the subject.
     * @return true if the email client was launched.
     */

    boolean launchEmailClient(String to, String subject);

    /**
     * Launches browser with specified url string.
     *
     * @param url the url string.
     * @return true if the browser was launched.
     */
    boolean launchBrowser(String url);

}
