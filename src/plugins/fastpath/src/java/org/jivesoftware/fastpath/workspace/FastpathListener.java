/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package org.jivesoftware.fastpath.workspace;

import org.jivesoftware.fastpath.workspace.util.RequestUtils;
import org.jivesoftware.spark.ui.ChatRoom;

import javax.swing.JTabbedPane;

/**
 *
 */
public interface FastpathListener {

    void fastpathRoomOpened(ChatRoom chatRoom, String sessionID, RequestUtils map, JTabbedPane assistantPane);
}
