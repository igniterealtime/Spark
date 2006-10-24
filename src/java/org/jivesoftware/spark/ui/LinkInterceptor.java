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

import java.awt.event.MouseEvent;

/**
 * Implementors of this interface wish to interecept link clicked events within
 * an active chat.
 */
public interface LinkInterceptor {

    /**
     * Returns true if you wish to handle this link, otherwise, will default to Spark.
     *
     * @param mouseEvent the MouseEvent.
     * @param link the link that was clicked.
     * @return true if the user wishes to handle the link.
     */
    public boolean handleLink(MouseEvent mouseEvent, String link);
}
