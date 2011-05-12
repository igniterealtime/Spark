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
