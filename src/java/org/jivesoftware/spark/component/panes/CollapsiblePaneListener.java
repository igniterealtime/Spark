/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.panes;

/**
 * Implementation of this interface is used for detecting when the <code>CollapsiblePane</code>
 * expands and collapses.
 *
 * @author Derek DeMoro
 */
public interface CollapsiblePaneListener {

    void paneExpanded();

    void paneCollapsed();
}
