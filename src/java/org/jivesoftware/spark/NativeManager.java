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
        final Iterator<NativeHandler> alertNotifier = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifier.hasNext()) {
            final NativeHandler alert = alertNotifier.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.flashWindow(window);
            }
        }
    }

    /**
     * Flash the given window, but stop flashing when the window takes focus.
     *
     * @param window the window to start flashing.
     */
    public void flashWindowStopOnFocus(Window window) {
        final Iterator<NativeHandler> alertNotifiers = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifiers.hasNext()) {
            final NativeHandler alert = alertNotifiers.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.flashWindowStopWhenFocused(window);
            }
        }
    }

    /**
     * Stop the flashing of the window.
     *
     * @param window the window to stop flashing.
     */
    public void stopFlashing(Window window) {
        final Iterator<NativeHandler> alertNotifiers = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifiers.hasNext()) {
            final NativeHandler alert = alertNotifiers.next();
            boolean handle = alert.handleNotification();
            if (handle) {
                alert.stopFlashing(window);
            }
        }
    }

}
