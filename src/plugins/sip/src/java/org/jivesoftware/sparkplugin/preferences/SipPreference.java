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
package org.jivesoftware.sparkplugin.preferences;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import net.java.sipmack.softphone.SoftPhoneManager;
import com.thoughtworks.xstream.XStream;
import net.java.sipmack.common.Log;
import net.java.sipmack.sip.SIPConfig;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.SwingWorker;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>SipPreference</code> class manage the Spark Preferences
 * @version 1.0, 28/09/2006
 */

public class SipPreference implements Preference {
    private SipPreferencePanel panel;

    private boolean showGUI = true;

    private boolean commitSettings = true;

    private XStream xstream;

    private SipPreferences preferences;

    public SipPreference() {
   	 try {
				EventQueue.invokeAndWait(new Runnable() {
		 				@Override
		 				public void run() {
		 					loadFromFile();
		 		         panel = new SipPreferencePanel();
		 				}
		 			});
 	    }
 	    catch(Exception e){
 	   	 e.printStackTrace();
 	    }
        
    }

    public void setData(SipPreferences sipPreferences) {
        this.preferences = sipPreferences;
    }

    public Object getData() {
        return preferences;
    }

    public String getErrorMessage() {
        return "Invalid SIP Settings";
    }

    public JComponent getGUI() {
        return panel;
    }

    public Icon getIcon() {
        return PhoneRes.getImageIcon("ANSWER_PHONE_IMAGE");
    }

    public String getListName() {
        return "Phone";
    }

    public String getNamespace() {
        return "SIP";
    }

    public String getTitle() {
        return "Phone Settings";
    }

    public String getTooltip() {
        return "Phone Settings";
    }

    public boolean isDataValid() {
        return true;
    }

    public void loadFromFile() {
        if (preferences != null) {
            return;
        }

        if (!getSipSettingsFile().exists()) {
            preferences = new SipPreferences();
        }
        else {

            // Do Initial Load from FileSystem.
            File settingsFile = getSipSettingsFile();
            try {
                FileReader reader = new FileReader(settingsFile);
                preferences = (SipPreferences)getXStream().fromXML(reader);
            }
            catch (Exception e) {
                Log.error("Error loading Sound Preferences.", e);
                preferences = new SipPreferences();
            }
        }
    }

    public void load() {
        SwingWorker worker = new SwingWorker() {

            public Object construct() {
                loadFromFile();
                return preferences;
            }

            public void finished() {

                panel
                    .setUserName(preferences.getUserName() != null ? preferences
                        .getUserName()
                        : "");
                panel
                    .setAuthUserName(preferences.getAuthUserName() != null ? preferences
                        .getAuthUserName()
                        : "");
                panel.setServer(preferences.getServer() != null ? preferences
                    .getServer() : "");

                panel
                    .setPassword(preferences.getPassword() != null ? preferences
                        .getPassword()
                        : "");

                panel.setRegister(preferences.isRegisterAtStart());

                panel.setStunServer(preferences.getStunServer());

                panel.setStunPort(preferences.getStunPort());

                panel.setUseStun(preferences.isUseStun());
            }
        };

        worker.start();

    }

    public void commit() {
        if (commitSettings) {
            preferences.setUserName(panel.getUserName());
            preferences.setAuthUserName(panel.getAuthUserName());
            preferences.setServer(panel.getServer());
            preferences.setPassword(panel.getPassword());
            preferences.setRegisterAtStart(panel.getRegister());
            preferences.setPreferredAddress(SIPConfig
                .getPreferredNetworkAddress());

            preferences.setUseStun(panel.getUseStun());
            preferences.setStunServer(panel.getStunServer());
            preferences.setStunPort(panel.getStunPort());

            saveSipFile();
            SoftPhoneManager.getInstance().handleUnregisterRequest();
            SoftPhoneManager.getInstance().register();
        }


    }

    public void shutdown() {
        preferences.setPreferredAddress(SIPConfig.getPreferredNetworkAddress());
        saveSipFile();
    }

    public boolean isShowGUI() {
        return showGUI;
    }

    public void setShowGUI(boolean showGUI) {
        this.showGUI = showGUI;
    }

    public boolean isCommitSettings() {
        return commitSettings;
    }

    public void setCommitSettings(boolean commitSettings) {
        this.commitSettings = commitSettings;
    }

    private File getSipSettingsFile() {
        File file = new File(Spark.getSparkUserHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "sip-settings.xml");
    }

    public void saveSipFile() {
        try {
            FileWriter writer = new FileWriter(getSipSettingsFile());
            getXStream().toXML(preferences, writer);
        }
        catch (Exception e) {
            Log.error("Error saving sound settings.", e);
        }
    }

    private XStream getXStream() {
        if (xstream == null) {
            xstream = new XStream();
            xstream.alias("sip", SipPreferences.class);
        }
        return xstream;
    }

}
