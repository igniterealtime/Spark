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

import org.jivesoftware.spark.component.browser.BrowserViewer;

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
     * Setup system tray.
     */
    void setupSystemTray();

    /**
     * Start Idle Process.
     */
    void startIdleProcess();

    /**
     * Returns the Browser to use for this platform.
     *
     * @return the <code>BrowserViewer</code> to use.
     */
    BrowserViewer getBrowser();

    /**
     * Instructs Spark to open up a file.
     *
     * @param file the file or directory to open.
     */
    void openFile(File file);

    /**
     * Return true if this <code>Alerter</code> should handle the alert request.
     *
     * @return true to handle.
     */
    boolean handleNotification();

}
