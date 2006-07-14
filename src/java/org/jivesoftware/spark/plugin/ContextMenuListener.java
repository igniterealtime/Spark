/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.plugin;

import javax.swing.JPopupMenu;

import java.awt.event.MouseEvent;

/**
 * The ContextMenuListener allows implementors to add their own menu
 * items to the context menu associated with this listener.
 */
public interface ContextMenuListener {

    /**
     * Called just before the context menu is popping up.
     *
     * @param object the object the event was fired for.
     * @param popup  the PopupMenu to be displayed.
     */
    void poppingUp(Object object, JPopupMenu popup);

    /**
     * Called just before the context menu closed.
     *
     * @param popup the popup menu in the process of closing.
     */
    void poppingDown(JPopupMenu popup);

    /**
     * Called when the user double clicks on an item that has a popup menu.
     * Only one listener should return true from this menu.
     *
     * @param e the current mouse event
     * @return true if user handles the default action.
     */
    boolean handleDefaultAction(MouseEvent e);

}

 
