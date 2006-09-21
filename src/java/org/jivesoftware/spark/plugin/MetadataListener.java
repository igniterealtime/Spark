/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package org.jivesoftware.spark.plugin;

import org.jivesoftware.spark.ui.ChatRoom;

import java.util.Map;

/**
 *
 */
public interface MetadataListener {

    void metadataAssociatedWithRoom(ChatRoom room, Map metadata);
}
