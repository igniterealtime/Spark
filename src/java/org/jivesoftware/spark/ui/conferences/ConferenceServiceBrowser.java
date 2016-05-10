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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConferenceServiceBrowser {

    public String getSelectedService() {
        final JLabel serverAddressLabel = new JLabel();
        final JTextField serverAddress = new JTextField();
        final RolloverButton findButton = new RolloverButton();

        ResourceUtils.resLabel(serverAddressLabel, serverAddress, Res.getString("label.server.address"));
        ResourceUtils.resButton(findButton, Res.getString("button.find"));

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        mainPanel.add(serverAddressLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(serverAddress, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        mainPanel.add(findButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(new JLabel("ex. jivesoftware.com"), new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

        // Add Empty CheckBox List
        final DefaultListModel model = new DefaultListModel();
        final JList list = new JList(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createTitledBorder(Res.getString("group.conferences.found")));
        mainPanel.add(scrollPane, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // Add Listener to find button
        findButton.addActionListener( e -> {
            String address = serverAddress.getText();
            if (ModelUtil.hasLength(address)) {
                try {
                    Collection<String> col = getConferenceServices(address);
                    for (String aCol : col) {
                        String service = aCol;
                        model.addElement(service);
                    }
                }
                catch (Exception e1) {
                    Log.error(e1);
                }

            }
        } );

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("ok"), Res.getString("close")};
        final JOptionPane pane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        TitlePanel titlePanel = new TitlePanel(Res.getString("title.browse.conference.services"), Res.getString("message.find.conference.services"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        final JDialog dlg = p.createDialog(SparkManager.getMainWindow(), Res.getString("title.find.conference.service"));
        dlg.setModal(true);

        dlg.pack();
        dlg.setSize(600, 400);
        dlg.setResizable(true);
        dlg.setContentPane(topPanel);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String)pane.getValue();
                if ("Close".equals(value)) {
                    list.clearSelection();
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
                else if ("Ok".equals(value)) {
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);
        serverAddress.requestFocusInWindow();
        dlg.setVisible(true);
        dlg.toFront();


        return (String)list.getSelectedValue();
    }

    public Collection<String> getConferenceServices(String server) throws Exception {
        List<String> answer = new ArrayList<>();
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());
        DiscoverItems items = discoManager.discoverItems(server);
        for (DiscoverItems.Item item : items.getItems() ) {
            if (item.getEntityID().startsWith("conference") || item.getEntityID().startsWith("private")) {
                answer.add(item.getEntityID());
            }
            else {
                try {
                    DiscoverInfo info = discoManager.discoverInfo(item.getEntityID());
                    if (info.containsFeature("http://jabber.org/protocol/muc")) {
                        answer.add(item.getEntityID());
                    }
                }
                catch (XMPPException | SmackException e) {
                    // Nothing to do
                }
            }
        }
        return answer;
    }
}
