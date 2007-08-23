/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware;

import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;

/**
 * Uses the Windows registry to perform URI XMPP mappings.
 *
 * @author Derek DeMoro
 */
public class SparkStartupListener implements com.install4j.api.launcher.StartupNotification.Listener {

    public void startupPerformed(String arguments) {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.handleURIMapping(arguments);
    }


}
