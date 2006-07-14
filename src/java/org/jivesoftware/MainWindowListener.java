/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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