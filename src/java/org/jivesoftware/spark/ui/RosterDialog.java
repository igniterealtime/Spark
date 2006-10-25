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

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportManager;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * The RosterDialog is used to add new users to the users XMPP Roster.
 */
public class RosterDialog implements PropertyChangeListener, ActionListener {
    private JPanel panel;
    private JTextField jidField;
    private JTextField nicknameField;
    private final Vector<String> groupModel = new Vector<String>();
    private JComboBox groupBox;
    private JComboBox accounts;
    private JOptionPane pane;
    private JDialog dialog;
    private ContactList contactList;

    /**
     * Create a new instance of RosterDialog.
     */
    public RosterDialog() {
        contactList = SparkManager.getWorkspace().getContactList();

        panel = new JPanel();
        JLabel contactIDLabel = new JLabel();
        jidField = new JTextField();
        JLabel nicknameLabel = new JLabel();
        nicknameField = new JTextField();
        JLabel groupLabel = new JLabel();
        groupBox = new JComboBox(groupModel);

        JButton newGroupButton = new JButton();

        JLabel accountsLabel = new JLabel("Account:");
        accounts = new JComboBox();


        pane = null;
        dialog = null;
        panel.setLayout(new GridBagLayout());
        panel.add(contactIDLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(jidField, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(nicknameField, new GridBagConstraints(1, 1, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));

        panel.add(accountsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(accounts, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, 17, 2, new Insets(5, 5, 5, 5), 0, 0));


        panel.add(groupLabel, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(groupBox, new GridBagConstraints(1, 3, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(newGroupButton, new GridBagConstraints(2, 3, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        newGroupButton.addActionListener(this);

        ResourceUtils.resLabel(contactIDLabel, jidField, Res.getString("label.username") + ":");
        ResourceUtils.resLabel(nicknameLabel, nicknameField, Res.getString("label.nickname") + ":");
        ResourceUtils.resLabel(groupLabel, groupBox, Res.getString("label.group") + ":");
        ResourceUtils.resButton(newGroupButton, Res.getString("button.new"));

        accounts.setRenderer(new JPanelRenderer());

        for (ContactGroup group : contactList.getContactGroups()) {
            if (!group.isOfflineGroup() && !"Unfiled".equalsIgnoreCase(group.getGroupName()) && !group.isSharedGroup()) {
                groupModel.add(group.getGroupName());
            }
        }


        groupBox.setEditable(true);

        if (groupModel.size() == 0) {
            groupBox.addItem("Friends");
        }

        if (groupModel.size() > 0) {
            groupBox.setSelectedIndex(0);
        }

        jidField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {

            }

            public void focusLost(FocusEvent e) {
                String jid = jidField.getText();
                AccountItem item = (AccountItem)accounts.getSelectedItem();
                Transport transport = item.getTransport();

                if (ModelUtil.hasLength(jid) && jid.indexOf('@') == -1) {
                    // Append server address
                    if (transport == null) {
                        jidField.setText(jid + "@" + SparkManager.getConnection().getServiceName());
                    }
                    else {
                        jidField.setText(jid + "@" + transport.getServiceName());
                    }
                }

                String nickname = nicknameField.getText();
                if (!ModelUtil.hasLength(nickname) && ModelUtil.hasLength(jid)) {
                    nicknameField.setText(StringUtils.parseName(jidField.getText()));
                }
            }
        });

        for (AccountItem item : getAccounts()) {
            accounts.addItem(item);
        }

    }

    /**
     * Sets the default <code>ContactGroup</code> to display in the combo box.
     *
     * @param contactGroup the default ContactGroup.
     */
    public void setDefaultGroup(ContactGroup contactGroup) {
        String groupName = contactGroup.getGroupName();
        if (groupModel.contains(groupName)) {
            groupBox.setSelectedItem(groupName);
        }
        else if (groupModel.size() > 0) {
            groupBox.addItem(groupName);
            groupBox.setSelectedItem(groupName);
        }
    }

    /**
     * Sets the default jid to show in the jid field.
     *
     * @param jid the jid.
     */
    public void setDefaultJID(String jid) {
        jidField.setText(jid);
    }

    /**
     * Sets the default nickname to show in the nickname field.
     *
     * @param nickname the nickname.
     */
    public void setDefaultNickname(String nickname) {
        nicknameField.setText(nickname);
    }


    public void actionPerformed(ActionEvent e) {
        String group = JOptionPane.showInputDialog(dialog, Res.getString("label.enter.group.name") + ":", Res.getString("title.new.roster.group"), 3);
        if (group != null && group.length() > 0 && !groupModel.contains(group)) {
            SparkManager.getConnection().getRoster().createGroup(group);
            groupModel.add(group);
            int size = groupModel.size();
            groupBox.setSelectedIndex(size - 1);
        }
    }

    /**
     * Display the RosterDialog using a parent container.
     *
     * @param parent the parent Frame.
     */
    public void showRosterDialog(JFrame parent) {
        TitlePanel titlePanel = new TitlePanel(Res.getString("title.add.contact"), Res.getString("message.add.contact.to.list"), SparkRes.getImageIcon(SparkRes.USER1_32x32), true);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        Object[] options = {
                Res.getString("add"), Res.getString("cancel")
        };
        pane = new JOptionPane(panel, -1, 2, null, options, options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);
        dialog = new JDialog(parent, Res.getString("title.add.contact"), true);
        dialog.pack();
        dialog.setContentPane(mainPanel);
        dialog.setSize(350, 250);

        dialog.setLocationRelativeTo(parent);
        pane.addPropertyChangeListener(this);


        dialog.setVisible(true);
        dialog.toFront();
        dialog.requestFocus();

        jidField.requestFocus();
    }

    /**
     * Display the RosterDialog using the MainWindow as the parent.
     */
    public void showRosterDialog() {
        showRosterDialog(SparkManager.getMainWindow());
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (pane != null && pane.getValue() instanceof Integer) {
            pane.removePropertyChangeListener(this);
            dialog.dispose();
            return;
        }

        String value = (String)pane.getValue();
        String errorMessage = Res.getString("title.error");
        if (Res.getString("cancel").equals(value)) {
            dialog.setVisible(false);
        }
        else if (Res.getString("add").equals(value)) {
            String contact = UserManager.escapeJID(jidField.getText());
            String nickname = nicknameField.getText();
            String group = (String)groupBox.getSelectedItem();

            if (!ModelUtil.hasLength(nickname) && ModelUtil.hasLength(contact)) {
                // Try to load nickname from VCard
                VCard vcard = new VCard();
                try {
                    vcard.load(SparkManager.getConnection(), contact);
                    nickname = vcard.getNickName();
                }
                catch (XMPPException e1) {
                    Log.error(e1);
                }
                // If no nickname, use first name.
                if (!ModelUtil.hasLength(nickname)) {
                    nickname = StringUtils.parseName(contact);
                }
                nicknameField.setText(nickname);
            }

            ContactGroup contactGroup = contactList.getContactGroup(group);
            boolean isSharedGroup = contactGroup != null && contactGroup.isSharedGroup();


            if (isSharedGroup) {
                errorMessage = Res.getString("message.cannot.add.contact.to.shared.group");
            }
            else if (!ModelUtil.hasLength(contact)) {
                errorMessage = Res.getString("message.specify.contact.jid");
            }
            else if (StringUtils.parseBareAddress(contact).indexOf("@") == -1) {
                errorMessage = Res.getString("message.invalid.jid.error");
            }
            else if (!ModelUtil.hasLength(group)) {
                errorMessage = Res.getString("message.specify.group");
            }
            else if (ModelUtil.hasLength(contact) && ModelUtil.hasLength(group) && !isSharedGroup) {
                addEntry();
                dialog.setVisible(false);
                return;
            }

            JOptionPane.showMessageDialog(dialog, errorMessage, Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
        }
    }

    private void addEntry() {
        AccountItem item = (AccountItem)accounts.getSelectedItem();
        if (item.getTransport() == null) {
            String jid = jidField.getText();
            if (jid.indexOf("@") == -1) {
                jid = jid + "@" + SparkManager.getConnection().getHost();
            }
            String nickname = nicknameField.getText();
            String group = (String)groupBox.getSelectedItem();

            jid = UserManager.escapeJID(jid);

            // Add as a new entry
            addEntry(jid, nickname, group);
        }
        else {
            String jid = jidField.getText();
            if (jid.indexOf("@") == -1) {
                jid = jid + "@" + item.getTransport().getServiceName();
                String nickname = nicknameField.getText();
                String group = (String)groupBox.getSelectedItem();
                addEntry(jid, nickname, group);
            }
        }
    }

    /**
     * Adds a new entry to the users Roster.
     *
     * @param jid      the jid.
     * @param nickname the nickname.
     * @param group    the contact group.
     * @return the new RosterEntry.
     */
    public RosterEntry addEntry(String jid, String nickname, String group) {
        String[] groups = {group};

        Roster roster = SparkManager.getConnection().getRoster();
        RosterEntry userEntry = roster.getEntry(jid);

        boolean isSubscribed = true;
        if (userEntry != null) {
            isSubscribed = userEntry.getGroups().size() == 0;
        }

        if (isSubscribed) {
            try {
                roster.createEntry(jid, nickname, new String[]{group});
            }
            catch (XMPPException e) {
                Log.error("Unable to add new entry " + jid, e);
            }
            return roster.getEntry(jid);
        }


        try {
            RosterGroup rosterGroup = roster.getGroup(group);
            if (rosterGroup == null) {
                rosterGroup = roster.createGroup(group);
            }

            if (userEntry == null) {
                roster.createEntry(jid, nickname, groups);
                userEntry = roster.getEntry(jid);
            }
            else {
                userEntry.setName(nickname);
                rosterGroup.addEntry(userEntry);
            }

            userEntry = roster.getEntry(jid);
        }
        catch (XMPPException ex) {
            Log.error(ex);
        }
        return userEntry;
    }

    public List<AccountItem> getAccounts() {
        List<AccountItem> list = new ArrayList<AccountItem>();

        // Create Jabber Account
        AccountItem account = new AccountItem(SparkRes.getImageIcon(SparkRes.ADDRESS_BOOK_16x16), "Jabber", null);
        list.add(account);

        for (Transport transport : TransportManager.getTransports()) {
            if (TransportManager.isRegistered(SparkManager.getConnection(), transport)) {
                AccountItem item = new AccountItem(transport.getIcon(), transport.getName(), transport);
                list.add(item);
            }
        }

        return list;
    }

    class AccountItem extends JPanel {

        private String name;
        private Transport transport;

        public AccountItem(Icon icon, String name, Transport transport) {
            setLayout(new GridBagLayout());
            this.name = name;
            this.transport = transport;

            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(icon);

            JLabel label = new JLabel();
            label.setText(name);
            label.setFont(new Font("Dialog", Font.BOLD, 11));
            label.setHorizontalTextPosition(JLabel.CENTER);

            add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            add(label, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));

            setBackground(Color.white);
        }

        public Transport getTransport() {
            return transport;
        }
    }
}