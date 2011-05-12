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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.JiveTreeCellRenderer;
import org.jivesoftware.spark.component.JiveTreeNode;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.Tree;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

public class RoomBrowser extends JPanel {
    private static final long serialVersionUID = 8820670697089268423L;
    private JLabel descriptionLabel = new JLabel();
    private JLabel subjectLabel = new JLabel();
    private JLabel occupantsLabel = new JLabel();
    private JLabel roomNameLabel = new JLabel();

    private JLabel descriptionValue = new JLabel();
    private JLabel subjectValue = new JLabel();
    private JLabel occupantsValue = new JLabel();
    private JLabel roomNameValue = new JLabel();

    private JiveTreeNode rootNode;
    private Tree tree;

    public RoomBrowser() {
        descriptionLabel.setText(Res.getString("description") + ":");
        subjectLabel.setText(Res.getString("subject") + ":");
        occupantsLabel.setText(Res.getString("occupants") + ":");
        roomNameLabel.setText(Res.getString("room.name") + ":");

        // Add labels to UI
        setLayout(new GridBagLayout());
        add(descriptionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(descriptionValue, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        add(subjectLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(subjectValue, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        add(occupantsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(occupantsValue, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        add(roomNameLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(roomNameValue, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


        rootNode = new JiveTreeNode(Res.getString("tree.users.in.room"), true);
        tree = new Tree(rootNode);

        add(tree, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        setBackground(Color.white);
        tree.setCellRenderer(new JiveTreeCellRenderer());


    }

    public void displayRoomInformation(final String roomJID) {
        SwingWorker worker = new SwingWorker() {
            RoomInfo roomInfo = null;
            DiscoverItems items = null;

            public Object construct() {
                try {
                    roomInfo = MultiUserChat.getRoomInfo(SparkManager.getConnection(), roomJID);


                    ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());
                    items = manager.discoverItems(roomJID);
                }
                catch (XMPPException e) {
                    Log.error(e);
                }
                return "ok";
            }

            public void finished() {
                setupRoomInformationUI(roomJID, roomInfo, items);
            }
        };

        worker.start();
    }

    private void setupRoomInformationUI(String roomJID, final RoomInfo roomInfo, final DiscoverItems items) {
        descriptionValue.setText(Res.getString("message.no.description.available"));
        subjectValue.setText(Res.getString("message.no.subject.available"));
        occupantsValue.setText("n/a");
        roomNameValue.setText("n/a");
        try {
            descriptionValue.setText(roomInfo.getDescription());
            subjectValue.setText(roomInfo.getSubject());

            if (roomInfo.getOccupantsCount() == -1) {
                occupantsValue.setText("n/a");
            }
            else {
                occupantsValue.setText(Integer.toString(roomInfo.getOccupantsCount()));
            }
            roomNameValue.setText(roomInfo.getRoom());

            Iterator<DiscoverItems.Item> iter = items.getItems();
            while (iter.hasNext()) {
                DiscoverItems.Item item = iter.next();
                String jid = item.getEntityID();
                rootNode.add(new JiveTreeNode(jid, false, SparkRes.getImageIcon(SparkRes.SMALL_USER1_INFORMATION)));
            }
            tree.expandRow(0);
        }
        catch (Exception e) {
            Log.error(e);
        }

        final JOptionPane pane;

        // Create the title panel for this dialog
        TitlePanel titlePanel = new TitlePanel(Res.getString("title.view.room.information"), Res.getString("message.room.information.for", roomJID), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("close")};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        final JDialog dlg = p.createDialog(SparkManager.getMainWindow(), Res.getString("title.view.room.information"));
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(450, 400);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String)pane.getValue();
                if (Res.getString("close").equals(value)) {
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
    }


}
