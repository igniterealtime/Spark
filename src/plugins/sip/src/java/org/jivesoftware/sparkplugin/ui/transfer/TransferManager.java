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

package org.jivesoftware.sparkplugin.ui.transfer;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.sparkplugin.ui.TelephoneTextField;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.component.panes.CollapsiblePane;
import org.jivesoftware.spark.util.ModelUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * TransferManager is used to transfer a specific call to another user within your Contact List.
 *
 * @author Derek DeMoro
 */
public class TransferManager extends JPanel implements TransferListener {

    private static final long serialVersionUID = 2830745814617073226L;
    private TelephoneTextField callField;
    private RolloverButton callButton;
    private JDialog dialog;
    private String dialedNumber;

    private List<TransferGroupUI> groups = new ArrayList<TransferGroupUI>();

    public TransferManager() {
        setLayout(new GridBagLayout());
        setBackground(Color.white);

        Roster roster = SparkManager.getConnection().getRoster();

        callField = new TelephoneTextField();

        callButton = new RolloverButton("Transfer", PhoneRes.getImageIcon("TRANSFER_IMAGE"));
        callButton.setMargin(new Insets(0, 0, 0, 0));

        add(callField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        add(callButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));

        callField.getTextComponent().addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent caretEvent) {
                callButton.setEnabled(ModelUtil.hasLength(callField.getText()) && callField.isEdited());
                callField.validateTextField();
            }
        });

        callField.getTextComponent().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!callField.isEnabled() || !callField.isEdited() || !ModelUtil.hasLength(callField.getText())) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    transferCall();
                }
            }


            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    return;
                }

                // Go through groups and sort. :)
                for (TransferGroupUI group : groups) {
                    group.sort(callField.getText());
                }
            }
        });

        callButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transferCall();
            }
        });

        callButton.setEnabled(false);

        final JPanel groupsPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        groupsPanel.setBackground(Color.white);

        final List<String> groupNames = new ArrayList<String>();

        for (RosterGroup rosterGroup : roster.getGroups()) {
            groupNames.add(rosterGroup.getName());
        }

        // Sort to add groups alphabetically.
        Collections.sort(groupNames);

        for (String groupName : groupNames) {
            TransferGroupUI group = new TransferGroupUI(groupName);
            groups.add(group);
            group.addTransferListener(this);

            if (group.hasTelephoneContacts()) {
                CollapsiblePane pane = new CollapsiblePane(groupName);

                pane.setContentPane(group);
                groupsPanel.add(pane);
            }
        }

        // Add Scroll Pane to Panel
        final JScrollPane scrollPane = new JScrollPane(groupsPanel);
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        add(scrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
    }

    /**
     * Disposes of the modal dialog and places the call.
     */
    private void transferCall() {
        dialedNumber = callField.getText();
        dialog.dispose();
    }


    public void numberSelected(String number) {
        callField.setText(number);
    }


    /**
     * Displays the transfer dialog.
     *
     * @param parent the parent frame.
     * @return the number selected, if available. Otherwise null is returned.
     */
    public String getNumber(JFrame parent) {
        dialog = new JDialog(parent, "Transfer Call", true);
        dialog.setLocationRelativeTo(parent);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.pack();
        dialog.setSize(350, 400);
        dialog.setVisible(true);
        return dialedNumber;
    }
}
