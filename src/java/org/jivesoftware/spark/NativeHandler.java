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

/**
 * Implementations of this interface define native mechanisms based on the Operating System
 * Spark is running on.
 *
 * @author Derek DeMoro
 */
public interface NativeHandler {

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
}
