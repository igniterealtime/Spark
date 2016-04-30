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
package org.jivesoftware.sparkimpl.settings;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;

import java.util.Map;

public class UserSettings {
    public static final String NAMESPACE = "jive:user:settings";
    public static final String ELEMENT_NAME = "personal_settings";
    private PrivateDataManager privateDataManager;
    private SettingsData settingsData;
    private static UserSettings singleton;
    private static final Object LOCK = new Object();

    public static UserSettings getInstance() {
        synchronized (LOCK) {
            if (null == singleton) {
                UserSettings controller = new UserSettings();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private UserSettings() {
        privateDataManager = PrivateDataManager.getInstanceFor(SparkManager.getConnection());
        PrivateDataManager.addPrivateDataProvider("personal_settings", "jive:user:settings", new SettingsDataProvider());

        try {
            settingsData = (SettingsData)privateDataManager.getPrivateData("personal_settings", "jive:user:settings");
        }
        catch (XMPPException | SmackException e) {
            Log.error("Error in User Settings", e);
        }
    }

    public Map<String,String> getSettings() {
        try {
            return settingsData.getMap();
        }
        catch (Exception ex) {
            Log.error("Error in User Settings.", ex);
        }
        return null;
    }

    public void setProperty(String name, String value) {
        getSettings().put(name, value);
    }

    public void setProperty(String name, boolean showtime) {
        getSettings().put(name, Boolean.toString(showtime));
    }

    public void setProperty(String name, int value) {
        getSettings().put(name, Integer.toString(value));
    }

    public String getProperty(String name) {
        return getSettings().get(name);
    }

    public String getEmptyPropertyIfNull(String name) {
        return ModelUtil.nullifyIfEmpty(getSettings().get(name));
    }

    public void save() {
        try {
            privateDataManager.setPrivateData(settingsData);
        }
        catch (XMPPException | SmackException e) {
            Log.error("Error in User Settings.", e);
        }
    }


}