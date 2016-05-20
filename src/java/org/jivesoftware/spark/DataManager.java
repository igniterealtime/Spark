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
package org.jivesoftware.spark;

import org.jivesoftware.spark.plugin.MetadataListener;
import org.jivesoftware.spark.ui.ChatRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Allows a mechanism to associated data with chat rooms.
 */
public class DataManager {
    private List<MetadataListener> metadataListeners = new ArrayList<>();

    private static DataManager singleton;
    private static final Object LOCK = new Object();


    /**
     * Returns the singleton instance of <CODE>DataManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>DataManager</CODE>
     */
    public static DataManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                DataManager controller = new DataManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }


    /**
     * Create a new instance of DataManager.
     */
    private DataManager() {

    }

    // Allows for more associated data to individual chat rooms.
    public void addMetadataListener(MetadataListener listener) {
        metadataListeners.add(listener);
    }

    public void removeMetadataListener(MetadataListener listener) {
        metadataListeners.remove(listener);
    }

    public void setMetadataForRoom(ChatRoom room, Map<?, ?> map) {
        for (MetadataListener listener : metadataListeners) {
            listener.metadataAssociatedWithRoom(room, map);
        }
    }
}
