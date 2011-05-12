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
package org.jivesoftware;

/**
 * The <code>MainWindowListener</code> interface is one of the interfaces extension
 * writers use to add functionality to Spark.
 * <p/>
 * In general, you implement this interface in order to listen
 * for Window events that could otherwise not be listened to by attaching
 * to the MainWindow due to security restrictions.
 */
public interface MainWindowListener {

    /**
     * Invoked by the <code>MainWindow</code> when it is about the shutdown.
     * When invoked, the <code>MainWindowListener</code>
     * should do anything necessary to persist their current state.
     * <code>MainWindowListeners</code> authors should take care to ensure
     * that any extraneous processing is not performed on this method, as it would
     * cause a delay in the shutdown process.
     *
     * @see org.jivesoftware.MainWindow
     */
    void shutdown();

    /**
     * Invoked by the <code>MainWindow</code> when it has been activated, such
     * as when it is coming out of a minimized state.
     * When invoked, the <code>MainWindowListener</code>
     * should do anything necessary to smoothly transition back to the application.
     *
     * @see org.jivesoftware.MainWindow
     */
    void mainWindowActivated();

    /**
     * Invoked by the <code>MainWindow</code> when it has been minimized in the toolbar.
     *
     * @see org.jivesoftware.MainWindow
     */
    void mainWindowDeactivated();

}