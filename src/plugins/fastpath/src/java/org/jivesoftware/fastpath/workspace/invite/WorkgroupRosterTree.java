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

import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.JiveTreeCellRenderer;
import org.jivesoftware.spark.component.JiveTreeNode;
import org.jivesoftware.spark.component.Tree;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public final class WorkgroupRosterTree extends JPanel {
	private static final long serialVersionUID = -1159008318665938338L;
	private final JiveTreeNode rootNode = new JiveTreeNode(FpRes.getString("title.contact.list"));
    private final Tree rosterTree;
    private final Map<JiveTreeNode, String> addressMap = new HashMap<JiveTreeNode, String>();
    private boolean showUnavailableAgents = true;
    private final List workgroupList;
    private Collection exclusionList;

    /**
     * Creates a new Roster Tree.
     *
     * @param exclusionJIDs the collection of jids to be excluded from the roster.
     * @param showAgents    true if agents should be visible.
     * @param workgroupList the list of workgroups.
     */
    public WorkgroupRosterTree(Collection exclusionJIDs, boolean showAgents, List workgroupList) {
        this.workgroupList = workgroupList;
        showUnavailableAgents = showAgents;
        exclusionList = exclusionJIDs;

        rootNode.setAllowsChildren(true);
        rosterTree = new Tree(rootNode);
        rosterTree.setCellRenderer(new JiveTreeCellRenderer());
        buildFromRoster();
        setLayout(new BorderLayout());

        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.white);

        final JScrollPane treeScroller = new JScrollPane(rosterTree);
        treeScroller.setBorder(BorderFactory.createEmptyBorder());
        panel.add(treeScroller, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        add(panel, BorderLayout.CENTER);
        for (int i = 0; i < rosterTree.getRowCount(); i++) {
            rosterTree.expandRow(i);
        }
    }

    private void changePresence(String user, Presence presence) {
        final Iterator<JiveTreeNode> iter = addressMap.keySet().iterator();
        while (iter.hasNext()) {
            final JiveTreeNode node = (JiveTreeNode)iter.next();
            final String nodeUser = (String)addressMap.get(node);
            if (user.startsWith(nodeUser)) {
                if (!PresenceManager.isAvailable(presence)) {
                    node.setIcon(FastpathRes.getImageIcon(FastpathRes.RED_BALL));
                }
                else {
                    node.setIcon(FastpathRes.getImageIcon(FastpathRes.GREEN_BALL));
                }
            }
        }
    }

    private void buildFromRoster() {
        final XMPPConnection xmppCon = SparkManager.getConnection();
        final Roster roster = xmppCon.getRoster();


        roster.addRosterListener(new RosterListener() {
            public void rosterModified() {
            }

            public void entriesAdded(Collection collection) {

            }

            public void entriesUpdated(Collection collection) {

            }

            public void entriesDeleted(Collection collection) {

            }

            public void presenceChanged(Presence presence) {
                changePresence(presence.getFrom(), presence);
            }

        });

        for (RosterGroup group : roster.getGroups()) {
            if (workgroupList.contains(group.getName())) {
                continue;
            }

            final JiveTreeNode groupNode = new JiveTreeNode(group.getName(), true);
            groupNode.setAllowsChildren(true);
            if (group.getEntryCount() > 0) {
                rootNode.add(groupNode);
            }

            for (RosterEntry entry : group.getEntries()) {
                String name = entry.getName();
                if (name == null) {
                    name = entry.getUser();
                }

                if (exclusionList.contains(entry.getUser())) {
                    continue;
                }

                final JiveTreeNode entryNode = new JiveTreeNode(name, false);
                final Presence usersPresence = roster.getPresence(entry.getUser());
                addressMap.put(entryNode, entry.getUser());
                if (PresenceManager.isAvailable(usersPresence)) {
                    groupNode.add(entryNode);
                }
                else if (showUnavailableAgents) {
                    groupNode.add(entryNode);
                }

                changePresence(entry.getUser(), usersPresence);
                final DefaultTreeModel model = (DefaultTreeModel)rosterTree.getModel();
                model.nodeStructureChanged(groupNode);
            }
        }
    }

    /**
     * Returns the Tree representation of the Roster Tree.
     *
     * @return the tree representation of the Roster Tree.
     */
    public Tree getRosterTree() {
        return rosterTree;
    }

    /**
     * Returns the selected agent node userobject.
     *
     * @param node the JiveTreeNode.
     * @return the selected agent nodes userobject.
     */
    public String getAgentJID(JiveTreeNode node) {
        String address = (String)addressMap.get(node);
        return address;
    }

    public String toString() {
        return "Roster";
    }
}
