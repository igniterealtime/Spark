/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.tabbedPane;

import java.awt.Component;

/**
 *
 */
public interface SparkTabbedPaneListener {

    void tabRemoved(SparkTab tab, Component component, int index);

    void tabAdded(SparkTab tab, Component component, int index);

    void tabSelected(SparkTab tab, Component component, int index);

    void allTabsRemoved();


}
