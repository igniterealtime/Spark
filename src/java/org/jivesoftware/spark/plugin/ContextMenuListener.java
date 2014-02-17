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

 
