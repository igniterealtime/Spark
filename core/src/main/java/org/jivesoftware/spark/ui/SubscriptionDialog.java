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
package org.jivesoftware.spark.ui;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;
import org.jxmpp.jid.BareJid;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHEAST;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.SOUTH;
import static java.awt.GridBagConstraints.WEST;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jivesoftware.spark.util.GroupUtils.loadGroupBox;

/**
 * SubscriptionDialog handles all subscription requests.
 *
 * @author Derek DeMoro
 */
public class SubscriptionDialog {
    private final RolloverButton acceptButton = new RolloverButton();
    private final RolloverButton viewInfoButton = new RolloverButton();
    private final RolloverButton denyButton = new RolloverButton();
    private final JPanel mainPanel = new JPanel();
    private final JCheckBox rosterBox = new JCheckBox();
    private final JTextField nicknameField = new JTextField();
    private final JComboBox<String> groupBox = new JComboBox<>();
    private final JLabel usernameLabelValue = new JLabel();
    private JFrame dialog;
    private BareJid jid;

    public SubscriptionDialog() {
        mainPanel.setLayout(new GridBagLayout());
        // Add Roster Addition
        final JPanel rosterPanel = new JPanel();
        rosterPanel.setLayout(new GridBagLayout());
        // Add ResourceUtils
        JLabel usernameLabel = new JLabel();
        ResourceUtils.resLabel(usernameLabel, nicknameField, Res.getString("label.username") + ":");
        JLabel nicknameLabel = new JLabel();
        ResourceUtils.resLabel(nicknameLabel, nicknameField, Res.getString("label.nickname") + ":");
        JLabel groupLabel = new JLabel();
        ResourceUtils.resLabel(groupLabel, groupBox, Res.getString("label.group") + ":");
        rosterBox.setText(Res.getString("label.add.to.roster"));
        groupBox.setEditable(true);

        rosterBox.addActionListener( actionEvent -> {
            nicknameField.setEnabled(rosterBox.isSelected());
            groupBox.setEnabled(rosterBox.isSelected());
        } );

        rosterBox.setSelected(true);

        Insets insets = new Insets(5, 5, 5, 5);
        rosterPanel.add(usernameLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, NORTHWEST, NONE, insets, 0, 0));
        rosterPanel.add(usernameLabelValue, new GridBagConstraints(1, 1, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));

        rosterPanel.add(nicknameLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, NORTHWEST, NONE, insets, 0, 0));
        rosterPanel.add(nicknameField, new GridBagConstraints(1, 2, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));

        rosterPanel.add(groupLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, NORTHWEST, NONE, insets, 0, 0));
        rosterPanel.add(groupBox, new GridBagConstraints(1, 3, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        rosterPanel.add(new JLabel(), new GridBagConstraints(1, 4, 1, 1, 1, 1, SOUTH, BOTH, insets, 0, 0));
        // Add Roster Panel to mainPanel
        mainPanel.add(rosterPanel, new GridBagConstraints(2, 1, 5, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        // Add Buttons
        ResourceUtils.resButton(acceptButton, Res.getString("button.accept"));
        ResourceUtils.resButton(viewInfoButton, Res.getString("button.profile"));
        ResourceUtils.resButton(denyButton, Res.getString("button.deny"));

        mainPanel.add(acceptButton, new GridBagConstraints(3, 2, 1, 1, 1, 0, NORTHEAST, NONE, insets, 0, 5));
        mainPanel.add(viewInfoButton, new GridBagConstraints(4, 2, 1, 1, 0, 0, NORTHEAST, NONE, insets, 0, 5));
        mainPanel.add(denyButton, new GridBagConstraints(5, 2, 1, 1, 0, 1, NORTHEAST, NONE, insets, 0, 5));
        loadGroupBox(groupBox);
    }

    public void invoke(final BareJid jid) {
        this.jid = jid;
        final Roster roster = SparkManager.getRoster();
        // If User is already in roster, do not show.
        RosterEntry entry = roster.getEntry(jid);
        if (entry != null && entry.getType() == RosterPacket.ItemType.to) {
            Presence response = StanzaBuilder.buildPresence()
                .ofType(Presence.Type.subscribed)
                .to(jid)
                .build();
            try {
                SparkManager.getConnection().sendStanza(response);
            } catch (Exception e) {
                Log.error("Unable to process subscription request from: " + jid, e);
            }
            return;
        }

        String message = Res.getString("message.approve.subscription", jid.asUnescapedString());
        Transport transport = TransportUtils.getTransport( jid.asDomainBareJid() );
        Icon icon = null;
        if (transport != null) {
            icon = transport.getIcon();
        }

        TitlePanel messageLabel = new TitlePanel("", message, icon);

        // Add Message Label
        mainPanel.add(messageLabel, new GridBagConstraints(0, 0, 6, 1, 1, 0, WEST, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        UserManager userManager = SparkManager.getUserManager();

        String username = userManager.getNickname(jid);
        username = username == null ? XmppStringUtils.parseLocalpart(jid.asUnescapedString()) : username;
        usernameLabelValue.setText(jid.asUnescapedString());
        nicknameField.setText(username);

        acceptButton.addActionListener(e -> {
            if (!rosterBox.isSelected()) {
                Presence response = StanzaBuilder.buildPresence()
                    .ofType(Presence.Type.subscribed)
                    .to(jid)
                    .build();
                try {
                    SparkManager.getConnection().sendStanza(response);
                } catch (SmackException.NotConnectedException | InterruptedException e1) {
                    Log.warning("Unable to send stanza accepting subscription from " + jid, e1);
                }
                dialog.dispose();
                return;
            }

            boolean addEntry = addEntry();
            if (addEntry) {
                Presence response = StanzaBuilder.buildPresence()
                    .ofType(Presence.Type.subscribed)
                    .to(jid)
                    .build();
                try {
                    SparkManager.getConnection().sendStanza(response);
                } catch (SmackException.NotConnectedException | InterruptedException e1) {
                    Log.warning("Unable to send stanza accepting subscription from " + jid, e1);
                }
            } else {
                dialog.dispose();
            }
        });

        denyButton.addActionListener(e -> {
            // Send subscribed
            unsubscribeAndClose();
        });

        viewInfoButton.addActionListener( e -> SparkManager.getVCardManager().viewProfile(jid, mainPanel) );
        
        dialog = new JFrame(Res.getString("title.subscription.request")){
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = super.getPreferredSize();
                dim.width = 400;
                return dim;
            }
        };
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                unsubscribeAndClose();
            }
        });

        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        ActionListener action = e -> unsubscribeAndClose();
        dialog.getRootPane().registerKeyboardAction(action, key, JComponent.WHEN_FOCUSED);
        dialog.setIconImage(SparkManager.getApplicationImage().getImage());
        dialog.getContentPane().add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(SparkManager.getMainWindow());


        if (SparkManager.getMainWindow().isFocused()) {
            dialog.setState(Frame.NORMAL);
            dialog.setVisible(true);
        } else if (!SparkManager.getMainWindow().isVisible() || !SparkManager.getMainWindow().isFocused()) {
            if (Spark.isWindows()) {
                dialog.setFocusable(false);
                dialog.setState(Frame.ICONIFIED);
            }
            SparkManager.getNativeManager().flashWindowStopOnFocus(dialog);
            dialog.setVisible(true);
        }
    }


    private void unsubscribeAndClose() {
        Presence response = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unsubscribe)
            .to(jid)
            .build();
        try {
            SparkManager.getConnection().sendStanza(response);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            Log.error("Unable to send stanza unsubscribing from " + jid, e);
        }

        dialog.dispose();
    }

    private boolean addEntry() {
        String nickname = nicknameField.getText();
        String group = (String) groupBox.getSelectedItem();

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactGroup contactGroup = contactList.getContactGroup(group);
        boolean isSharedGroup = contactGroup != null && contactGroup.isSharedGroup();
        String errorMessage = null;
        if (isSharedGroup) {
            errorMessage = Res.getString("message.cannot.add.contact.to.shared.group");
        } else if (isBlank(nickname)) {
            errorMessage = Res.getString("message.specify.contact.jid");
        } else if (isBlank(group)) {
            errorMessage = Res.getString("message.specify.group");
        } else {
            try {
                addEntry(jid, nickname, group);
            } catch (XMPPException | SmackException e) {
                errorMessage = Res.getString("title.error") + ": " + e.getMessage();
            }
        }
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(dialog, errorMessage, Res.getString("title.error"), ERROR_MESSAGE);
            return false;
        }
        dialog.setVisible(false);
        return true;
    }


    /**
     * Adds a new entry to the user's Roster.
     */
    public void addEntry(BareJid jid, String displayName, String group) throws XMPPException.XMPPErrorException, SmackException {
        String[] groups = {group};
        Roster roster = SparkManager.getRoster();
        RosterEntry userEntry = roster.getEntry(jid);
        boolean isSubscribed = true;
        if (userEntry != null) {
            isSubscribed = userEntry.getGroups().isEmpty();
        }
        if (isSubscribed) {
            try {
                roster.preApproveAndCreateEntry(jid, displayName, groups);
            } catch (XMPPException | SmackException | InterruptedException e) {
                Log.error("Unable to add new entry " + jid, e);
            }
            roster.getEntry(jid);
            return;
        }
        try {
            RosterGroup rosterGroup = roster.getGroup(group);
            if (rosterGroup == null) {
                rosterGroup = roster.createGroup(group);
            }
            userEntry.setName(displayName);
            rosterGroup.addEntry(userEntry);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (XMPPException.XMPPErrorException | SmackException ex) {
            Log.error(ex);
            throw ex;
        }
    }
}
