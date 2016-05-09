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
package org.jivesoftware.spellchecker;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;

/**
 * This Plugin provides realtime typo detection and suggestions
 */
public class SpellcheckerPlugin implements Plugin {
    private SpellcheckChatRoomListener listener;
    private SpellcheckerPreference preference;

    public boolean canShutDown() {
	return true;
    }

    public void initialize() {

	try {
	    preference = SpellcheckManager.getInstance()
		    .getSpellcheckerPreference();
	    SparkManager.getPreferenceManager().addPreference(preference);

	    listener = new SpellcheckChatRoomListener();
	    SparkManager.getChatManager().addChatRoomListener(listener);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void shutdown() {

    }

    public void uninstall() {
	SparkManager.getChatManager().removeChatRoomListener(listener);
    }

}
