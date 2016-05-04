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
package org.jivesoftware.sparkplugin;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.TimerTask;

import javax.sip.message.Request;
import javax.swing.UIManager;

import net.java.sipmack.sip.Call;
import net.java.sipmack.sip.SipRegisterStatus;
import net.java.sipmack.sip.event.CallRejectedEvent;
import net.java.sipmack.sip.event.CallStateEvent;
import net.java.sipmack.sip.event.MessageEvent;
import net.java.sipmack.sip.event.UnknownMessageEvent;
import net.java.sipmack.softphone.SoftPhoneManager;
import net.java.sipmack.softphone.VoiceMail;
import net.java.sipmack.softphone.listeners.RegisterEvent;
import net.java.sipmack.softphone.listeners.SoftPhoneListener;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.sparkplugin.preferences.SipCodecsPreference;
import org.jivesoftware.sparkplugin.ui.ContactDialControl;
import org.jivesoftware.sparkplugin.ui.RegistrationStatusPanel;
import org.jivesoftware.sparkplugin.ui.TelephoneTextField;
import org.jivesoftware.sparkplugin.ui.call.SoftPhoneTabHandler;

/**
 * Title: Spark Phone
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>SoftPhonePlugin</code> class build and implements a Spark SIP
 *         plugin
 * @version 1.0, 20/07/2006
 */

public class SoftPhonePlugin implements Plugin, SoftPhoneListener {

    private SoftPhoneManager softPhone;

    private ContactDialControl dialControl;

    private RegistrationStatusPanel loadingPanel;

    private boolean isRegistered;

    /**
     * Called after Spark is loaded to initialize the new plugin. Load
     * Configuration from VCard and Instantiate a SoftPhoneManager
     */
    public void initialize() {
   	 final SwingWorker initializeThread = new SwingWorker() {
            public Object construct() {
  					 PhoneManager.getInstance();
                softPhone = SoftPhoneManager.getInstance();
                
                return true;
            }

            public void finished() {
                if (softPhone.isPhoneEnabled()) {
                    // Add TabHandler
                    SparkManager.getChatManager().addSparkTabHandler(new SoftPhoneTabHandler());
                    initializeUI();
                }
                SipCodecsPreference preference = new SipCodecsPreference();
                SparkManager.getPreferenceManager().addPreference(preference);
                preference.load();
            }
        };
        initializeThread.start();
    }

    private void initializeUI() {
        dialControl = new ContactDialControl();
        dialControl.setVisible(false);

        // Add Dial control to main window
        SparkManager.getWorkspace().add(dialControl, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 4, 4), 0, 0));

        // Add Loading Panel
        loadingPanel = new RegistrationStatusPanel();
        SparkManager.getWorkspace().add(loadingPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 4, 4), 0, 0));
        loadingPanel.setVisible(false);


        softPhone.addSoftPhoneListener(this);

        SparkManager.getConnection().addConnectionListener(new ConnectionListener() {

            @Override
            public void connected( XMPPConnection xmppConnection ) {
            }

            @Override
            public void authenticated( XMPPConnection xmppConnection, boolean b ) {
                // Wait a bit before registering
                TimerTask registerTask = new TimerTask() {
                    public void run() {
                        softPhone.register();
                    }
                };

                TaskEngine.getInstance().schedule(registerTask, 15000);
            }

            public void connectionClosed() {
                //softPhone.handleUnregisterRequest();
            }

            public void connectionClosedOnError(Exception exception) {
                softPhone.handleUnregisterRequest();
            }

            public void reconnectingIn(int i) {
                // Wait a bit before registering
                TimerTask registerTask = new TimerTask() {
                    public void run() {
                        softPhone.register();
                    }
                };

                TaskEngine.getInstance().schedule(registerTask, 15000);
            }

            public void reconnectionSuccessful() {
            }

            public void reconnectionFailed(Exception exception) {

            }

        });
    }

    /**
     * Called when Spark is shutting down to allow for persistence of
     * information or releasing of resources. Unregister from SIP Server
     */
    public void shutdown() {
        if (softPhone.isPhoneEnabled()) {
            softPhone.getLogManager().commit();
            softPhone.handleExitRequest();
            SparkManager.getWorkspace().remove(loadingPanel);
            SparkManager.getWorkspace().remove(dialControl);
        }
    }

    /**
     * Return true if the Spark can shutdown on users request.
     *
     * @return true if Spark can shutdown on users request.
     */
    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
    }


    /**
     * Fired when a message is received
     *
     * @param evt MessageEvent
     */
    public void messageReceived(MessageEvent evt) {
        if (evt.getSource() instanceof Request) {
            Request request = (Request)evt.getSource();
            if (request.getMethod().equals(Request.NOTIFY)) {
                VoiceMail vm = new VoiceMail(evt);
                dialControl.setVoiceMailLabel(vm.getUnread());
                dialControl.setVoiceMailDescription("You have " + vm.getUnread() + " new voice mails.");
            }
        }
    }

    /**
     * Fired when an Unknow message is received
     *
     * @param evt UnknownMessageEvent
     */
    public void receivedUnknownMessage(UnknownMessageEvent evt) {

    }

    /**
     * Fired when global status of SIP Registering changed
     *
     * @param evt RegisterEvent
     */
    public void registerStatusChanged(RegisterEvent evt) {
        if (evt.getStatus() == SipRegisterStatus.Registered) {
            dialControl.setVisible(true);
            loadingPanel.setVisible(false);
            SparkManager.getWorkspace().getStatusBar().setDescriptiveText(softPhone.getSipAccount().getDisplayName());
            if (!isRegistered) {
                loadVCards();
                isRegistered = true;
            }
        }
        else if (evt.getStatus() == SipRegisterStatus.Unregistered) {
            dialControl.setVoiceMailLabel("");
            dialControl.setVisible(false);
            loadingPanel.setVisible(false);
            SparkManager.getWorkspace().getStatusBar().setDescriptiveText("");
        }
        else if (evt.getStatus() == SipRegisterStatus.Registering) {
            if (!loadingPanel.isVisible()) {
                dialControl.setVisible(false);
                loadingPanel.setVisible(true);
                loadingPanel.showRegistrationProgress();
            }
        }
        else if (evt.getStatus() == SipRegisterStatus.RegistrationFailed) {
            loadingPanel.setVisible(true);
            dialControl.setVisible(false);
            loadingPanel.showRegistrationFailed(evt);
        }
    }

    /**
     * Perform some GUI Changes when call State Changes
     */
    public void callStateChanged(CallStateEvent evt) {

    }

    /**
     * Handle invalid dialed numbers or NOT Found numbers
     *
     * @param evt the rejection Event
     */
    public void callRejectedRemotely(CallRejectedEvent evt) {
        Call call = evt.getCall();

        TelephoneTextField telephoneTextField = dialControl.getCallField();
        telephoneTextField.setForeground((Color)UIManager.get("TextField.lightforeground"));
        telephoneTextField.setText(PhoneRes.getIString("phone.invalidnumber")+":" + call.getNumber());

    }

    private void loadVCards() {
        // Load vCard information.
        final Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
        for (RosterEntry entry : roster.getEntries()) {
            SparkManager.getVCardManager().getVCardFromMemory(entry.getUser());
        }
    }

}