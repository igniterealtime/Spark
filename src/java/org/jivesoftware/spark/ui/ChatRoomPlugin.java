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
