/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.spark.util.GraphicUtils;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;

public class NewRoster extends JPanel implements RosterListener {

    private final RosterNode rootNode;
    private JTree tree;
    private DefaultTreeModel model;

    private JPanel mainPanel;
    private JScrollPane treeScroller;

    public static XMPPConnection con;

    public NewRoster() {
        setLayout(new BorderLayout());

        rootNode = new RosterNode();
        tree = new JTree(rootNode) {
            /**
             * Lets make sure that the panel doesn't stretch past the
             * scrollpane view pane.
             *
             * @return the preferred dimension
             */
            public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                size.width = 0;
                return size;
            }
        };

        tree.setCellRenderer(new RosterTreeCellRenderer());
        model = (DefaultTreeModel)tree.getModel();

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.white);

        treeScroller = new JScrollPane(tree);
        mainPanel.add(treeScroller, BorderLayout.CENTER);

        this.add(mainPanel, BorderLayout.CENTER);
    }

    private void buildContactList(XMPPConnection conn) {
        con = conn;
        final Roster roster = con.getRoster();


        roster.addRosterListener(this);

        final Iterator rosterGroups = roster.getGroups();
        while (rosterGroups.hasNext()) {
            RosterGroup group = (RosterGroup)rosterGroups.next();

            // Create Group node.
            final RosterNode groupNode = new RosterNode(group.getName(), true);
            if (group.getEntryCount() > 0) {
                rootNode.add(groupNode);
            }


            Iterator entries = group.getEntries();
            while (entries.hasNext()) {
                RosterEntry entry = (RosterEntry)entries.next();
                String nickname = entry.getName();
                if (nickname == null) {
                    nickname = entry.getUser();
                }

                RosterNode contact = new RosterNode(nickname, entry.getUser());

                // Add to contact group.
                groupNode.add(contact);

            }

        }

        RosterNode unfiledGroup = new RosterNode("Unfiled", true);
        rootNode.add(unfiledGroup);

        // Add Unfiled Group
        final Iterator unfiledEntries = roster.getUnfiledEntries();
        while (unfiledEntries.hasNext()) {
            RosterEntry entry = (RosterEntry)unfiledEntries.next();
            String name = entry.getName();
            if (name == null) {
                name = entry.getUser();
            }

            RosterNode contact = new RosterNode(name, entry.getUser());
            unfiledGroup.add(contact);
        }

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        tree.setRootVisible(false);

    }

    public void entriesAdded(Collection addresses) {
    }

    public void entriesUpdated(Collection addresses) {
    }

    public void entriesDeleted(Collection addresses) {
    }

    public void presenceChanged(String XMPPAddress) {
    }

    public static void main(String args[]) throws Exception {
        final XMPPConnection con = new XMPPConnection("jivesoftware.com", 5222);
        con.login("agent", "agent");

        final JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        NewRoster roster = new NewRoster();
        frame.getContentPane().add(roster);
        frame.pack();
        frame.setSize(600, 400);
        GraphicUtils.centerWindowOnScreen(frame);
        frame.setVisible(true);
        roster.buildContactList(con);
    }
}