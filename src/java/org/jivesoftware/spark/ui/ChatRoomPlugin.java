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

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Provides a mechanism for users to register themselves as ChatRoomPlugin objects. This allows
 * users to initialize their own UI's and attach themselves to all ChatRooms for added feature sets.
 */
public interface ChatRoomPlugin {

    /**
     * Sets the ChatRoom to attach to.
     *
     * @param room the ChatRoom that the ChatRoomPlugin will be attaching to.
     */
    void setChatRoom(ChatRoom room);

    /**
     * Called whenever the tab containing the ChatRoomPlugin is clicked.
     */
    void tabSelected();

    /**
     * Return the name of the title you wish to identify this ChatRoomPlugin by.
     *
     * @return the title of the tab panel containing the
     */
    String getTabTitle();

    /**
     * Return the Icon to use on the tab containing the ChatRoomPlugin.
     *
     * @return the icon to show on the ChatRoomPlugin tab.
     */
    Icon getTabIcon();

    /**
     * Return the tooltip to use on a mouseover of the tab.
     *
     * @return the tooltip to use on a mouseover of the tab.
     */
    String getTabToolTip();

    /**
     * Return's the GUI for the ChatRoomPlugin.
     *
     * @return the GUI of the ChatRoomPlugin.
     */
    JComponent getGUI();
}
