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
