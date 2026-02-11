/**
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
package org.jivesoftware.sparkimpl.plugin.jabber;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

/**
 * Jabber Browser.
 * Discovering items on an XMPP server
 *
 * @author Derek DeMoro
 */
public class JabberBrowser implements Plugin {
    private JLabel addressLabel;
    private JComboBox<String> addressField;
    private XMPPConnection con;
    private JPanel browsePanel;

    /**
     * Displays Jabber browser in the modal dialog
     */
    public void display() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        // Setup resource
        ResourceUtils.resLabel(addressLabel, addressField, Res.getString("label.jabber.address") + ":");

        RolloverButton backButton = new RolloverButton();
        backButton.setIcon(SparkRes.getImageIcon(SparkRes.LEFT_ARROW_IMAGE));
        backButton.addActionListener(e -> {
            int selectedItem = addressField.getSelectedIndex();
            if (selectedItem > 0) {
                String historyItem = addressField.getItemAt(selectedItem - 1);
                browse(historyItem);
            }
        });

        mainPanel.add(backButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(addressLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(addressField, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


        JButton browseButton = new JButton("");
        ResourceUtils.resButton(browseButton, Res.getString("button.browse"));
        browseButton.addActionListener(e -> {
            String serviceName = (String) addressField.getSelectedItem();
            if (!ModelUtil.hasLength(serviceName)) {
                return;
            }
            browse(serviceName);
        });
        mainPanel.add(addressField, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(browseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        browsePanel = new JPanel();
        browsePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        browsePanel.setBackground(Color.white);

        JScrollPane pane = new JScrollPane(browsePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        browsePanel.setPreferredSize(new Dimension(0, 0));
        mainPanel.add(pane, new GridBagConstraints(0, 1, 4, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        JFrame frame = new JFrame();
        frame.setIconImage(SparkRes.getImageIcon(SparkRes.FIND_IMAGE).getImage());

        JDialog dialog = new JDialog(frame, Res.getString("title.jabber.browser"));
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(mainPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(SparkManager.getMainWindow());
        dialog.setVisible(true);
    }

    private void browse(String serviceNameString) {
        Jid serviceName;
        try {
            serviceName = JidCreate.from(serviceNameString);
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }

        browsePanel.removeAll();

        ServiceDiscoveryManager discoManager = SparkManager.getDiscoManager();
        DiscoverItems result;
        try {
            result = discoManager.discoverItems(serviceName);
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
            return;
        }

        addAddress(serviceName.toString());

        for (DiscoverItems.Item item : result.getItems()) {
            Entity entity = new Entity(item);
            browsePanel.add(entity);
        }

        browsePanel.invalidate();
        browsePanel.validate();
        browsePanel.repaint();
    }

    /**
     * Add address; discover items; updates the browser panel
     */
    private void browseItem(DiscoverItems.Item discoveredItem) {
        addAddress(discoveredItem.getEntityID().toString());
        browsePanel.removeAll();
        ServiceDiscoveryManager discoManager = SparkManager.getDiscoManager();
        DiscoverItems result;
        try {
            result = discoManager.discoverItems(discoveredItem.getEntityID());
        } catch (XMPPException | SmackException | InterruptedException e) {
            browsePanel.invalidate();
            browsePanel.validate();
            browsePanel.repaint();
            return;
        }

        List<DiscoverItems.Item> resultItems = result.getItems();
        List<Entity> list = new ArrayList<>();
        for (DiscoverItems.Item item : resultItems) {
            Entity entity = new Entity(item);
            browsePanel.add(entity);
            list.add(entity);
        }

        GraphicUtils.makeSameSize(list.toArray(new JComponent[0]));

        browsePanel.invalidate();
        browsePanel.validate();
        browsePanel.repaint();
    }

    public class Entity extends RolloverButton {
        private final DiscoverItems.Item item;

        public Entity(final DiscoverItems.Item item) {
            this.item = item;
            setVerticalTextPosition(JLabel.BOTTOM);
            setHorizontalTextPosition(JLabel.CENTER);
            setText(item.getName() != null ? item.getName() : item.getEntityID().toString());
            setIcon(SparkRes.getImageIcon(SparkRes.USER1_MESSAGE_24x24));

            addActionListener(e -> browseItem(item));
        }

        public DiscoverItems.Item getItem() {
            return item;
        }
    }

    private void addAddress(String address) {
        addressField.addItem(address);
        addressField.setSelectedItem(address);
    }

    @Override
    public void initialize() {
        this.con = SparkManager.getConnection();
        EventQueue.invokeLater(() -> {
            addressLabel = new JLabel();
            addressField = new JComboBox<>();
            addressField.setEditable(true);
            addressField.addItem(con.getHost());
        });
        SparkManager.getWorkspace().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F8"), "showBrowser");
        AbstractAction actionShowXmppBrowser = new AbstractAction("showBrowser") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                display();
            }
        };
        SparkManager.getWorkspace().getActionMap().put("showBrowser", actionShowXmppBrowser);
        addMainMenuItem(actionShowXmppBrowser);
    }

    private void addMainMenuItem(AbstractAction actionShowXmppBrowser) {
        JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        if (actionsMenu == null) {
            return;
        }
        JMenuItem menuShowXmppBrowser = new JMenuItem(Res.getString("title.jabber.browser"), SparkRes.getImageIcon(SparkRes.FIND_IMAGE));
        menuShowXmppBrowser.addActionListener(actionShowXmppBrowser);
        actionsMenu.add(menuShowXmppBrowser);
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public void uninstall() {
    }

}
