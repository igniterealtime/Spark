/**
 * $Revision: 22540 $
 * $Date: 2005-10-10 08:44:25 -0700 (Mon, 10 Oct 2005) $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */

package com.jivesoftware.spark.plugin.apple;


import org.jivesoftware.Spark;
import org.jivesoftware.spark.Alerter;

import java.awt.Window;

/**
 * When a message is received and the user does not have presence
 * the dock will bounce.
 *
 * @author Andrew Wright
 */
public class MacWindowAlerter implements Alerter {


    public void flashWindow(Window window) {
        AppleUtils.bounceDockIcon(false);
    }

    public void flashWindowStopWhenFocused(Window window) {
        AppleUtils.bounceDockIcon(false);
    }

    public void stopFlashing(Window window) {
        AppleUtils.resetDock();

    }

    public boolean handleNotification() {
        return Spark.isMac();
    }
}
