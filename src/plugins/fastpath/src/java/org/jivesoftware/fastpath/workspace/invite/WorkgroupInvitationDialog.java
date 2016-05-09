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
package org.jivesoftware.fastpath.workspace.invite;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.workgroup.agent.Agent;
import org.jivesoftware.smackx.workgroup.agent.AgentRoster;
import org.jivesoftware.smackx.workgroup.agent.WorkgroupQueue;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.JiveTreeCellRenderer;
import org.jivesoftware.spark.component.JiveTreeNode;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.Tree;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;


/**
 * UI and Dialog to show all available agents that can be invited into a ChatRoom.
 */
public class WorkgroupInvitationDialog implements PropertyChangeListener {
    private Tree tree;
    private Tree rosterTree;
    private WorkgroupRosterTree roster;
    private JiveTreeNode rootNode;

    private JOptionPane pane;
    private JDialog dlg;

    private boolean isValid;
    private TitlePanel titlePanel;

    private JTextArea messageField;
    private JTextField jidField = new JTextField();

    private List workgroups = new ArrayList();
    private JLabel inviteLabel;

    /**
     * Empty Constructor.
     */
    public WorkgroupInvitationDialog() {
    }

    /**
     * Returns the agent selected.
     *
     * @param chatRoom the room this roster will be used with.
     * @param transfer true if this will be a transfer.
     * @return true if a valid agent has been selected.
     */
    public boolean hasSelectedAgent(ChatRoom chatRoom, boolean transfer) {
        final JiveTreeNode workgroupsNode = new JiveTreeNode("Workgroups", true);
        final JiveTreeNode queueNode = new JiveTreeNode("Queues", true);

        final String workgroupService = "workgroup." + SparkManager.getSessionManager().getServerAddress();
        final String jid = SparkManager.getSessionManager().getJID();

        String room = chatRoom.getRoomname();
        Collection agents = getAvailableAgents(FastpathPlugin.getAgentSession().getAgentRoster(), room);

        // Clear jid field
        jidField.setText("");

        String title = FpRes.getString("invite.agent");
        String acceptButton = FpRes.getString("invite");
        if (transfer) {
            title = FpRes.getString("transfer.to.agent");
            acceptButton = FpRes.getString("transfer");
        }

        String stockMessage = FpRes.getString("message.transfering.to.user");
        if (!transfer) {
            stockMessage = FpRes.getString("message.join.me.in.chat");
        }

        rootNode = new JiveTreeNode("Ask For Assistance", true);
        tree = new Tree(rootNode);
        tree.setCellRenderer(new JiveTreeCellRenderer());

        // This will remove lines from the tree and setup initial l&f
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRowHeight(16);
        tree.setExpandsSelectedPaths(true);


        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                Object o = path.getLastPathComponent();
                if (o instanceof JiveTreeNode) {
                    JiveTreeNode node = (JiveTreeNode)o;
                    JiveTreeNode parentNode = (JiveTreeNode)node.getParent();
                    if (parentNode == workgroupsNode) {
                        jidField.setText(node.getUserObject().toString() + "@" + workgroupService);
                    }
                    else if (parentNode == queueNode) {
                        jidField.setText(FastpathPlugin.getWorkgroup().getWorkgroupJID() + "/" + node.getUserObject().toString());
                    }
                    else {
                        String agent = getAgent();
                        jidField.setText(agent);
                    }
                }
            }
        });

        // Create message field
        messageField = new JTextArea();
        messageField.setWrapStyleWord(true);

        inviteLabel = new JLabel();
        if (transfer) {
            inviteLabel.setText(FpRes.getString("transfer.to") + ":");
        }
        else {
            inviteLabel.setText(FpRes.getString("invite") + ":");
        }


        if (transfer) {
            // Create the title panel for this dialog
            titlePanel = new TitlePanel(FpRes.getString("title.transfer"), FpRes.getString("message.transfer.to.another.agent"),
                FastpathRes.getImageIcon(FastpathRes.CHATTING_AGENT_IMAGE), true);
        }
        else {
            // Create the title panel for this dialog
            titlePanel = new TitlePanel(FpRes.getString("title.invitation"), FpRes.getString("message.invite.another.agent"),
                FastpathRes.getImageIcon(FastpathRes.CHATTING_AGENT_IMAGE), true);
        }

        // Build Tree
        String joinedWorkgroupName = StringUtils.parseName(FastpathPlugin.getWorkgroup().getWorkgroupJID());
        final JiveTreeNode workgroupNode = new JiveTreeNode(joinedWorkgroupName, true);

        final Iterator agentIter = agents.iterator();
        while (agentIter.hasNext()) {
            while (agentIter.hasNext()) {
                String agentName = UserManager.unescapeJID((String)agentIter.next());
                final JiveTreeNode agentNode = new JiveTreeNode(agentName, false, FastpathRes.getImageIcon(FastpathRes.GREEN_BALL));
                workgroupNode.add(agentNode);
            }
            if (workgroupNode.getChildCount() > 0) {
                rootNode.add(workgroupNode);
            }
        }


        Collection workgroupAgents;
        try {
            workgroupAgents = Agent.getWorkgroups(workgroupService, jid, SparkManager.getConnection());
        }
        catch (XMPPException e) {
            Log.error(e);
            workgroupAgents = Collections.EMPTY_LIST;
        }
        if (workgroupAgents.size() > 0) {
            // Add workgroups to combobox
            Iterator<String> workgroups = workgroupAgents.iterator();
            while (workgroups.hasNext()) {
                String workgroup = workgroups.next();

                String workgroupName = StringUtils.parseName(workgroup);
                final JiveTreeNode wgNode = new JiveTreeNode(workgroupName, false, FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));
                workgroupsNode.add(wgNode);
            }
            rootNode.add(workgroupsNode);
        }


        Iterator iter = FastpathPlugin.getAgentSession().getQueues();
        while (iter.hasNext()) {
            final WorkgroupQueue queue = (WorkgroupQueue)iter.next();
            if (queue.getStatus() == WorkgroupQueue.Status.OPEN) {
                final JiveTreeNode qNode = new JiveTreeNode(queue.getName(), false, FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));
                queueNode.add(qNode);
            }
        }

        rootNode.add(queueNode);

        // New Roster tree. Do not show agents at all.
        final UserManager userManager = SparkManager.getUserManager();
        Collection jids = userManager.getUserJidsInRoom(room, false);

        roster = new WorkgroupRosterTree(jids, false, workgroups);

        try {
            rosterTree = roster.getRosterTree();
            JiveTreeNode node = (JiveTreeNode)rosterTree.getModel().getRoot();
            rootNode.add(node);
        }
        catch (Exception e) {
            Log.error("Error checking for selected agent", e);
        }

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {acceptButton, FpRes.getString("cancel")};

        // Create objects to add to mainPanel.
        final JPanel parentPanel = new JPanel();
        final JLabel messageLabel = new JLabel();
        final JLabel agentLabel = new JLabel();
        parentPanel.setLayout(new GridBagLayout());

        // Update Labels using ResourceUtils
        ResourceUtils.resLabel(messageLabel, messageField, FpRes.getString("label.message.to.agent"));
        ResourceUtils.resLabel(agentLabel, tree, FpRes.getString("label.select.agent"));

        // Add Components to Parent Panel
        parentPanel.add(agentLabel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        parentPanel.add(new JScrollPane(tree), new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 100));

        parentPanel.add(messageLabel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        parentPanel.add(new JScrollPane(messageField), new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 25));
        parentPanel.add(inviteLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        parentPanel.add(jidField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Use line wrap.
        messageField.setLineWrap(true);

        // Update MessageField with stock message
        messageField.setText(stockMessage);

        // Add Parent Object to master OptionPane
        pane = new JOptionPane(parentPanel, JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        dlg = new JDialog(SparkManager.getChatManager().getChatContainer().getChatFrame(), title, true);
        dlg.pack();
        dlg.setSize(500, 500);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(chatRoom);
        pane.addPropertyChangeListener(this);

        // expand the tree for displaying
        for (int i = 0; i <= tree.getRowCount(); i++) {
            tree.expandPath(tree.getPathForRow(i));
        }

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        if (!isValid) {
            return false;
        }
        return true;
    }


    public void propertyChange(PropertyChangeEvent e) {
        String value = (String)pane.getValue();
        if (FpRes.getString("cancel").equals(value)) {
            isValid = false;
            dlg.setVisible(false);
        }
        else if (FpRes.getString("invite").equals(value) || FpRes.getString("transfer").equals(value)) {
            String agent = jidField.getText();
            boolean isValidJID = agent.indexOf("@") != -1;

            if (!ModelUtil.hasLength(agent)) {
                JOptionPane.showMessageDialog(dlg, FpRes.getString("message.no.agent.selected.error"),
                    FpRes.getString("error"), JOptionPane.ERROR_MESSAGE);
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
            else if (!isValidJID) {
                JOptionPane.showMessageDialog(dlg, FpRes.getString("message.jid.invalid.error"),
                    FpRes.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
            else if (!ModelUtil.hasLength(getMessage())) {
                JOptionPane.showMessageDialog(dlg, FpRes.getString("message.message.required.error"),
                    FpRes.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
            else {
                isValid = true;
                dlg.setVisible(false);
            }
        }
    }

    /**
     * Return the message to send to the agent.
     *
     * @return the message to send to the agent.
     */
    public String getMessage() {
        return messageField.getText();
    }

    /**
     * Returns the selected jid to transfer to.
     *
     * @return the selected jid.
     */
    public String getSelectedJID() {
        return jidField.getText();
    }

    /**
     * Handles tree selection of an agent.
     *
     * @return the agents jid.
     */
    private String getAgent() {
        String agentJID;

        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if (node == null || node.getAllowsChildren()) {
            return "";
        }

        agentJID = roster.getAgentJID((JiveTreeNode)node);
        if (agentJID == null) {
            final Object nodeInfo = node.getUserObject();
            if (!node.isLeaf()) {
                return "";
            }
            agentJID = nodeInfo.toString();
        }
        return agentJID;
    }

    /**
     * Returns a Collection of agents available to receive either an invitation or transfer request.
     *
     * @param roster   the AgentRoster of the workgroup.
     * @param roomName the name of the room to check.
     * @return collection of available agents.
     */
    private Collection<String> getAvailableAgents(AgentRoster roster, String roomName) {
        final Set<String> availableAgents = new HashSet<String>();

        final Iterator<String> agents = roster.getAgents().iterator();
        while (agents.hasNext()) {
            String agent = agents.next();
            if (PresenceManager.isAvailable(agent)) {
                final Iterator agentsInRoom = SparkManager.getUserManager().getUserJidsInRoom(roomName, false).iterator();
                boolean alreadyExists = false;

                while (agentsInRoom.hasNext()) {
                    String userid = (String)agentsInRoom.next();

                    if (agent.equalsIgnoreCase(userid)) {
                        alreadyExists = true;
                    }
                }

                if (!alreadyExists) {
                    availableAgents.add(agent);
                }

            }
        }


        return availableAgents;
    }
}
