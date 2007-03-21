/**
 * $Revision: 22540 $
 * $Date: 2005-10-10 08:44:25 -0700 (Mon, 10 Oct 2005) $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package com.jivesoftware.spark.plugin.growl;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;


/**
 * Provides support for Growl Notifications on Mac OS X. Growl can be acquired at
 * http://growl.info
 *
 *
 * @author Andrew Wright
 */
public class GrowlPlugin implements Plugin {

    private GrowlMessageListener growlListener;


    public void initialize() {
        growlListener = new GrowlMessageListener();
        SparkManager.getChatManager().addGlobalMessageListener(growlListener);
    }

    public void shutdown() {
        SparkManager.getChatManager().removeGlobalMessageListener(growlListener);
    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
        SparkManager.getChatManager().removeGlobalMessageListener(growlListener);
    }

}
