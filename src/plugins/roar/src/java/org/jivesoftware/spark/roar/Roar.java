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
package org.jivesoftware.spark.roar;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;

/**
 * The Main Class of the Roar Plugin
 * 
 * @author wolf.posdorfer
 *
 */
public class Roar implements Plugin {

    private RoarMessageListener _listener;

    @Override
    public void initialize() {

        RoarPreference pref = new RoarPreference();
        SparkManager.getPreferenceManager().addPreference(pref);

        _listener = new RoarMessageListener();
        SparkManager.getChatManager().addGlobalMessageListener(_listener);

    }

    @Override
    public void shutdown() {
        SparkManager.getChatManager().removeGlobalMessageListener(_listener);
    }

    @Override
    public boolean canShutDown() {
        return true;
    }

    @Override
    public void uninstall() {

    }

}
