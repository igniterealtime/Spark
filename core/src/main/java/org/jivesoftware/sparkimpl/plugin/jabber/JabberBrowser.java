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
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * Jabber Browser.
 * Discovering items on an XMPP server
 *
 * @author Derek DeMoro
 */
public class JabberBrowser implements Plugin {

    /**
     * Displays Jabber browser in the modal dialog
     */
    public void display() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        JPanel browsePanel = new JPanel();
        JLabel addressLabel = new JLabel();
        JComboBox<String> addressField = new JComboBox<>();
        addressField.setEditable(true);
        addressField.addItem(SparkManager.getConnection().getXMPPServiceDomain().toString());
        // Setup resource
        ResourceUtils.resLabel(addressLabel, addressField, Res.getString("label.jabber.address") + ":");

        RolloverButton backButton = new RolloverButton();
        backButton.setIcon(SparkRes.getImageIcon(SparkRes.Icon.LEFT_ARROW_IMAGE));
        backButton.addActionListener(e -> {
            int selectedItem = addressField.getSelectedIndex();
            if (selectedItem > 0) {
                String historyItem = addressField.getItemAt(selectedItem - 1);
                browse(historyItem, browsePanel, addressField);
            }
        });

        Insets insets = new Insets(5, 5, 5, 5);
        mainPanel.add(backButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));
        mainPanel.add(addressLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));
        mainPanel.add(addressField, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));

        JButton browseButton = new JButton("");
        ResourceUtils.resButton(browseButton, Res.getString("button.browse"));
        browseButton.addActionListener(e -> {
            startBrowsing(addressField, browsePanel);
        });
        mainPanel.add(addressField, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        mainPanel.add(browseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));

        browsePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        browsePanel.setBackground(Color.white);

        JScrollPane pane = new JScrollPane(browsePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        browsePanel.setPreferredSize(new Dimension(0, 0));
        mainPanel.add(pane, new GridBagConstraints(0, 1, 4, 1, 1.0, 1.0, WEST, BOTH, insets, 0, 0));

        JDialog dialog = new JDialog(SparkManager.getMainWindow(), Res.getString("title.jabber.browser"));
        dialog.setIconImage(SparkRes.getImageIcon(SparkRes.Icon.FIND_IMAGE).getImage());
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(mainPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(SparkManager.getMainWindow());
        dialog.setVisible(true);
        startBrowsing(addressField, browsePanel);
    }

    private void startBrowsing(JComboBox<String> addressField, JPanel browsePanel) {
        String serviceName = (String) addressField.getSelectedItem();
        if (isBlank(serviceName)) {
            return;
        }
        browse(serviceName, browsePanel, addressField);
    }

    /**
     * Add address; discover items; updates the browser panel
     */
    private void browse(String serviceNameString, JPanel browsePanel, JComboBox<String> addressField) {
        Jid serviceName = JidCreate.fromOrThrowUnchecked(serviceNameString);
        browsePanel.removeAll();
        SwingWorker swingWorker = new SwingWorker() {
            DiscoverItems discoItems;
            DiscoverInfo discoFeatures;

            @Override
            public Object construct() {
                ServiceDiscoveryManager discoManager = SparkManager.getDiscoManager();
                try {
                    discoItems = discoManager.discoverItems(serviceName);
                    discoFeatures = discoManager.discoverInfo(serviceName);
                } catch (XMPPException | SmackException | InterruptedException e) {
                    Log.error(e);
                }
                return null;
            }

            @Override
            public void finished() {
                if (discoItems != null && discoFeatures != null) {
                    addAddress(serviceName.toString(), addressField);
                    List<DiscoverItems.Item> resultItems = discoItems.getItems();
                    List<Entity> list = new ArrayList<>(resultItems.size());
                    for (DiscoverItems.Item item : resultItems) {
                        Entity entity = new Entity(item, discoFeatures, browsePanel, addressField);
                        browsePanel.add(entity);
                        list.add(entity);
                    }
                    GraphicUtils.makeSameSize(list.toArray(new JComponent[0]));
                }
                browsePanel.invalidate();
                browsePanel.validate();
                browsePanel.repaint();
            }
        };
        swingWorker.start();
    }

    public class Entity extends RolloverButton {
        private final DiscoverItems.Item item;

        public Entity(final DiscoverItems.Item item, DiscoverInfo discoFeatures, JPanel browsePanel, JComboBox<String> addressField) {
            this.item = item;
            setVerticalTextPosition(JLabel.BOTTOM);
            setHorizontalTextPosition(JLabel.CENTER);
            setText(item.getName() != null ? item.getName() : item.getEntityID().toString());
            setIcon(SparkRes.getImageIcon(SparkRes.Icon.USER1_MESSAGE_24x24));
            String tipText = "Identities:\n";
            for (DiscoverInfo.Identity identity : discoFeatures.getIdentities()) {
                tipText += "Name: " + trimToEmpty(identity.getName()) + ", Category: " + trimToEmpty(identity.getCategory()) + ", Lang: " + trimToEmpty(identity.getLanguage()) + "\n";
            }
            tipText += "Features:\n";
            for (DiscoverInfo.Feature feature : discoFeatures.getFeatures()) {
                tipText += feature.getVar() + "\n";
            }
            setToolTipText(tipText);

            addActionListener(e -> browse(item.getEntityID().toString(), browsePanel, addressField));
        }

        public DiscoverItems.Item getItem() {
            return item;
        }
    }

    private void addAddress(String address, JComboBox<String> addressField) {
        addressField.addItem(address);
        addressField.setSelectedItem(address);
    }

    @Override
    public void initialize() {
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
        JMenuItem menuShowXmppBrowser = new JMenuItem(Res.getString("title.jabber.browser"), SparkRes.getImageIcon(SparkRes.Icon.FIND_IMAGE));
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
