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
package org.jivesoftware.fastpath;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.Workpane;
import org.jivesoftware.fastpath.workspace.panes.BackgroundPane;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.workgroup.agent.Agent;
import org.jivesoftware.smackx.workgroup.agent.AgentSession;
import org.jivesoftware.smackx.workgroup.user.Workgroup;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;

public class FastpathPlugin implements Plugin, ConnectionListener {
    private static Workgroup wgroup;
    private static AgentSession agentSession;
    private static Workpane litWorkspace;
    private String componentAddress;
    private static BackgroundPane mainPanel;
    private static FastpathContainer container;
    private JLabel workgroupLabel;
    private JComboBox comboBox;
    private JButton joinButton;
    private RolloverButton logoutButton;
    private static boolean wasConnected;

    private FastpathTabHandler fastpathTabHandler;

    public void initialize() {

   	 EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				  container = new FastpathContainer();
				  workgroupLabel = new JLabel(FpRes.getString("workgroup"));
				  comboBox = new JComboBox();
				  joinButton = new JButton(FpRes.getString("join"), null);
				  logoutButton = new RolloverButton(FpRes.getString("logout"), null);
		        // Initialize tab handler for Fastpath chats.
		        fastpathTabHandler = new FastpathTabHandler();
		        mainPanel = new BackgroundPane();
			}
   		 
   	 });
   	 




			
			
        try {
            DiscoverItems items = SparkManager.getSessionManager().getDiscoveredItems();
            Iterator<DiscoverItems.Item> iter = items.getItems();
            while (iter.hasNext()) {
                DiscoverItems.Item item = iter.next();
                String entityID = item.getEntityID() != null ? item.getEntityID() : "";
                if (entityID.startsWith("workgroup")) {
                    // Log into workgroup
                    final String workgroupService = "workgroup." + SparkManager.getSessionManager().getServerAddress();
                    final String jid = SparkManager.getSessionManager().getJID();


                    SwingWorker worker = new SwingWorker() {
                        public Object construct() {
                            try {
                                return Agent.getWorkgroups(workgroupService, jid, SparkManager.getConnection());
                            }
                            catch (XMPPException e1) {
                                return Collections.emptyList();
                            }
                        }

                        public void finished() {
                            Collection<String> agents = (Collection<String>)get();
                            if (agents.size() == 0) {
                                return;
                            }
                            showSelection(agents);
                        }
                    };

                    worker.start();

                }

            }
        }
        catch (Exception e) {
            Log.error(e);
        }

        SparkManager.getConnection().addConnectionListener(this);
    }

    private void showSelection(Collection<String> col) {
        if (col.size() == 0) {
            return;
        }

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));


        workgroupLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        logoutButton.setVisible(false);

        // Add workgroups to combobox
        Iterator<String> workgroups = col.iterator();
        while (workgroups.hasNext()) {
            String workgroup = (String)workgroups.next();
            String componentAddress = StringUtils.parseServer(workgroup);
            setComponentAddress(componentAddress);
            comboBox.addItem(StringUtils.parseName(workgroup));
        }

        mainPanel.add(workgroupLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(logoutButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        mainPanel.add(comboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(joinButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        container.getTopPanel().add(mainPanel, BorderLayout.CENTER);


        final Workspace workspace = SparkManager.getWorkspace();
        workspace.getWorkspacePane().addTab(FpRes.getString("tab.fastpath"), FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16), container);

        Action joinAction = new AbstractAction() {
			private static final long serialVersionUID = 4476966137732930493L;

			public void actionPerformed(ActionEvent actionEvent) {
                joinWorkgroup();
            }
        };

        Action leaveAction = new AbstractAction() {
			private static final long serialVersionUID = -264964340889335732L;

			public void actionPerformed(ActionEvent actionEvent) {
                leaveWorkgroup();
            }
        };

        if (col.size() == 1) {
            joinAction.actionPerformed(null);
        }

        logoutButton.addActionListener(leaveAction);
        joinButton.addActionListener(joinAction);

        // Load services immeditaly.
        Thread loadServicesThread = new Thread(new Runnable() {
            public void run() {
                SparkManager.getChatManager().getDefaultConferenceService();
            }
        });

        loadServicesThread.start();
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {

    }

    public static Workgroup getWorkgroup() {
        return wgroup;
    }

    public static AgentSession getAgentSession() {
        return agentSession;
    }

    public static Workpane getLitWorkspace() {
        return litWorkspace;
    }

    public String getComponentAddress() {
        return componentAddress;
    }

    public void setComponentAddress(String componentAddress) {
        this.componentAddress = componentAddress;
    }


    public static JPanel getMainPanel() {
        return mainPanel;
    }

    public static FastpathContainer getUI() {
        return container;
    }


    public void connectionClosed() {
        lostConnection();
    }

    public void connectionClosedOnError(Exception e) {
        lostConnection();
    }

    public void reconnectingIn(int seconds) {
    }

    public void reconnectionSuccessful() {
        // Rejoin the workgroup after 15 seconds.
        final TimerTask rejoinTask = new SwingTimerTask() {
            public void doRun() {
                if (wasConnected) {
                    joinWorkgroup();
                }
            }
        };

        TaskEngine.getInstance().schedule(rejoinTask, 15000);
    }

    public void reconnectionFailed(Exception e) {
    }

    private void lostConnection() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (agentSession != null) {
                    resetWorkgroups();
                    wasConnected = true;
                    agentSession = null;
                }
            }
        });

    }

    private void joinWorkgroup() {
        wasConnected = false;
        joinButton.setEnabled(false);
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                    Log.error(e);
                }
                return null;
            }

            public void finished() {
                XMPPConnection con = SparkManager.getConnection();
                String workgroupName = org.jivesoftware.spark.util.StringUtils.makeFirstWordCaptial((String)comboBox.getSelectedItem());
                workgroupLabel.setText(FpRes.getString("message.workgroup.logged.into", workgroupName));
                logoutButton.setVisible(true);
                joinButton.setVisible(false);
                comboBox.setVisible(false);
                String workgroup = comboBox.getSelectedItem() + "@" + getComponentAddress();
                if (agentSession != null && agentSession.isOnline()) {
                    try {
                        agentSession.setOnline(false);
                        agentSession.setOnline(true);
                    }
                    catch (XMPPException e) {
                        Log.error(e);
                        leaveWorkgroup();
                        joinButton.setEnabled(true);
                        return;
                    }
                }
                else {
                    agentSession = new AgentSession(workgroup, con);
                    try {
                        agentSession.setOnline(true);
                    }
                    catch (XMPPException e1) {
                        Log.error(e1);
                        leaveWorkgroup();
                        joinButton.setEnabled(true);
                        return;
                    }
                }

                // Send actual presence to workgroup.
                final Presence actualPresence = SparkManager.getWorkspace().getStatusBar().getPresence();
                Presence toWorkgroupPresence = new Presence(actualPresence.getType(), actualPresence.getStatus(), actualPresence.getPriority(), actualPresence.getMode());
                toWorkgroupPresence.setTo(workgroup);
                con.sendPacket(toWorkgroupPresence);

                try {
                    wgroup = new Workgroup(workgroup, con);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    joinButton.setEnabled(true);
                    return;
                }

                // Initialize Workspace
                litWorkspace = new Workpane();
                litWorkspace.listenForOffers();
                joinButton.setEnabled(true);

                // Register tab handler
                SparkManager.getChatManager().addSparkTabHandler(fastpathTabHandler);
            }
        };
        worker.start();
    }

    private void leaveWorkgroup() {
        workgroupLabel.setText(FpRes.getString("workgroup") + ":");
        logoutButton.setVisible(false);
        joinButton.setVisible(true);
        comboBox.setVisible(true);

        comboBox.removeAllItems();
        // Log into workgroup
        String workgroupService = "workgroup." + SparkManager.getSessionManager().getServerAddress();
        String jid = SparkManager.getSessionManager().getJID();

        try {
            Collection<String> col = Agent.getWorkgroups(workgroupService, jid, SparkManager.getConnection());
            // Add workgroups to combobox
            Iterator<String> workgroups = col.iterator();
            while (workgroups.hasNext()) {
                String workgroup = workgroups.next();
                String componentAddress = StringUtils.parseServer(workgroup);
                setComponentAddress(componentAddress);
                comboBox.addItem(StringUtils.parseName(workgroup));
            }
        }
        catch (XMPPException ee) {
            // If the user does not belong to a workgroup, then don't initialize the rest of the plugin.
            return;
        }

        try {
            agentSession.setOnline(false);
        }
        catch (XMPPException e1) {
            Log.error(e1);
        }
        litWorkspace.unload();
        wgroup = null;

        // UnRegister tab handler
        SparkManager.getChatManager().removeSparkTabHandler(fastpathTabHandler);
    }

    private void resetWorkgroups() {
        litWorkspace.unload();
        workgroupLabel.setText(FpRes.getString("workgroup") + ":");
        logoutButton.setVisible(false);
        joinButton.setVisible(true);
        joinButton.setEnabled(true);
        comboBox.setVisible(true);
        wgroup = null;
    }
}
