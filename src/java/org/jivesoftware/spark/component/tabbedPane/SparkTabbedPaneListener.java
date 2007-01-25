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
 * The listener that is notified when state is changed within the <code>SparkTabbedPane</code>
 *
 * @author Derek DeMoro
 */
public interface SparkTabbedPaneListener {

    /**
     * Called when a <code>SparkTab</code> is removed from the tab pane.
     *
     * @param tab       the tab that is being removed.
     * @param component the child component of the tab.
     * @param index     the index of the tab.
     */
    void tabRemoved(SparkTab tab, Component component, int index);

    /**
     * Called when a new <code>SparkTab</code> has been added.
     *
     * @param tab       the new SparkTab added.
     * @param component the child component of the tab.
     * @param index     the index of the tab.
     */
    void tabAdded(SparkTab tab, Component component, int index);

    /**
     * Called when the tab is selected by the user.
     *
     * @param tab       the SparkTab selected.
     * @param component the child component of the tab.
     * @param index     the index of the tab.
     */
    void tabSelected(SparkTab tab, Component component, int index);

    /**
     * Called when all tabs are closed.
     */
    void allTabsRemoved();

    /**
     * Implementations of this method allow users to have more fine grained control
     * on closing of individual tabs depending on component state.
     *
     * @param tab       the SparkTab that will be closing.
     * @param component the child component of the tab.
     * @return true to allow closing, otherwise returning false will stop closing of this tab.
     */
    boolean canTabClose(SparkTab tab, Component component);


}
