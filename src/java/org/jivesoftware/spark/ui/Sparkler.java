/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

public interface Sparkler {

    /**
     * @param message Message for sparkler.
     * @param decorator Decorator to handler sparkler.
     */
    void decorateMessage(String message, SparklerDecorator decorator);
}
