/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.plugins;

import org.jivesoftware.spark.plugin.Plugin;

/**
 *
 */
public class LinuxPlugin implements Plugin {

    public void initialize() {
        // Add System Tray
        new LinuxSystemTray();
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
    }
}
