/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.sparklers;

import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.Sparkler;
import org.jivesoftware.spark.ui.SparklerDecorator;

public class SparklersPlugin implements Plugin {

    public void initialize() {
        new Sparkler() {
            public void decorateMessage(String message, SparklerDecorator decorator) {
                decorator.setURL("Spark", "http://www.test.com");
            }
        };

    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {

    }
}
