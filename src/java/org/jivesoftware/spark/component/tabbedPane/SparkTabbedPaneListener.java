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
