/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.util;

import java.awt.EventQueue;

/**
 * Allows for handling of TimerTask operations inside of the SwingEventThread.
 */
public abstract class SwingTimerTask extends java.util.TimerTask {
    public abstract void doRun();

    public void run() {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(this);
        }
        else {
            doRun();
        }
    }
}
