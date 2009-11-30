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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.spark.util.ModelUtil;

/**
 * The AlertManager handles the delegation of Alerting based.
 *
 * @author Derek DeMoro
 */
public class NativeManager {

    private List<NativeHandler> nativeHandlers = new ArrayList<NativeHandler>();

    public NativeManager() {

    }

    /**
     * Adds an alert.
     *
     * @param nativeHandler the Alerter to add.
     */
    public void addNativeHandler(NativeHandler nativeHandler) {
        nativeHandlers.add(nativeHandler);
    }

    /**
     * Removes an alerter.
     *
     * @param nativeHandler the alerter to remove.
     */
    public void removeNativeHandler(NativeHandler nativeHandler) {
        nativeHandlers.remove(nativeHandler);
    }


    /**
     * Flash the given window.
     *
     * @param window the window to flash.
     */
    public void flashWindow(Window window) {
        final Iterator alertNotifier = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifier.hasNext()) {
            final NativeHandler alert = (NativeHandler)alertNotifier.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.flashWindow(window);
                break;
            }
        }
    }

    /**
     * Flash the given window, but stop flashing when the window takes focus.
     *
     * @param window the window to start flashing.
     */
    public void flashWindowStopOnFocus(Window window) {
        final Iterator alertNotifiers = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifiers.hasNext()) {
            final NativeHandler alert = (NativeHandler)alertNotifiers.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.flashWindowStopWhenFocused(window);
                break;
            }
        }
    }

    /**
     * Stop the flashing of the window.
     *
     * @param window the window to stop flashing.
     */
    public void stopFlashing(Window window) {
        final Iterator alertNotifiers = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifiers.hasNext()) {
            final NativeHandler alert = (NativeHandler)alertNotifiers.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.stopFlashing(window);
                break;
            }
        }
    }

}
