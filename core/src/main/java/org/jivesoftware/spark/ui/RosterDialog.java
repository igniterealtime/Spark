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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.borders.ComponentTitledBorder;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.packet.Gateway;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.util.Objects.requireNonNull;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jivesoftware.spark.util.GraphicUtils.addItemIfNotExists;
import static org.jivesoftware.spark.util.GroupUtils.loadGroupBox;


/**
 * The RosterDialog is used to add new users to the user's XMPP Roster.
 */
public class RosterDialog {
    private final JPanel panel = new JPanel();
    private final JTextField jidField = new JTextField();
    private final JTextField nicknameField = new JTextField();

    private final JComboBox<String> groupBox = new JComboBox<>();
    private final JComboBox<AccountItem> accounts = new JComboBox<>();
    private JDialog dialog;
    private final ContactList contactList;
    private final JCheckBox publicBox = new JCheckBox();
    private final JButton _searchForName = new JButton();
    private final Collection<DomainBareJid> _usersearchservice;

    /**
     * Create a new instance of RosterDialog.
     */
    public RosterDialog() {
        contactList = SparkManager.getWorkspace().getContactList();
        _usersearchservice = SearchManager.getInstance().getSearchServicesAsJid();

        JLabel contactIDLabel = new JLabel();
        JLabel nicknameLabel = new JLabel();
        JLabel groupLabel = new JLabel();

        JButton newGroupButton = new JButton();

        JLabel accountsLabel = new JLabel();
        publicBox.setText(Res.getString("label.user.on.public.network"));
        ResourceUtils.resLabel(accountsLabel, publicBox, Res.getString("label.network"));

        _searchForName.setIcon(SparkRes.getImageIcon(SparkRes.Icon.TRANSFER_IMAGE_24x24));
        _searchForName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    searchForContact(jidField.getText(), e);
                } catch (XMPPException | SmackException | InterruptedException e1) {
                    Log.error("search contact", e1);
                }
            }
        });

        panel.setLayout(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);
        panel.add(contactIDLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, 17, 2, insets, 0, 0));
        panel.add(jidField, new GridBagConstraints(1, 0, 1, 1, 1, 0, 17, 2, insets, 0, 0));
        panel.add(_searchForName, new GridBagConstraints(2, 0, 1, 1, 0, 0, 17, 0, insets, 0, 0));


        panel.add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, 17, 2, insets, 0, 0));
        panel.add(nicknameField, new GridBagConstraints(1, 1, 1, 1, 1, 0, 17, 2, insets, 0, 0));


        JPanel networkPanel = new JPanel( new GridBagLayout() );
        ComponentTitledBorder componentBorder = new ComponentTitledBorder(publicBox, networkPanel
            , BorderFactory.createEtchedBorder());


        networkPanel.add(accountsLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, 17, 2, insets, 0, 0));
        networkPanel.add(accounts, new GridBagConstraints(1, 0, 1, 1, 1, 0, 17, 2, insets, 0, 0));

        networkPanel.setBorder(componentBorder);

        networkPanel.setVisible(false);
        accounts.setEnabled(false);


        panel.add(groupLabel, new GridBagConstraints(0, 4, 1, 1, 0, 0, 17, 2, insets, 0, 0));

        panel.add(groupBox, new GridBagConstraints(1, 4, 1, 1, 1, 0, 17, 2, insets, 0, 0));

        if (!Default.getBoolean(Default.ADD_CONTACT_GROUP_DISABLED) && Enterprise.containsFeature(Enterprise.ADD_GROUPS_FEATURE)) {
            panel.add(newGroupButton, new GridBagConstraints(2, 4, 1, 1, 0, 0, 17, 1, insets, 0, 0));
        }
        newGroupButton.addActionListener(this::newGroupButtonOnClick);

        panel.add(networkPanel, new GridBagConstraints(0, 5, 2, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));


        ResourceUtils.resLabel(contactIDLabel, jidField, Res.getString("label.username") + ":");
        ResourceUtils.resLabel(nicknameLabel, nicknameField, Res.getString("label.nickname") + ":");
        ResourceUtils.resLabel(groupLabel, groupBox, Res.getString("label.group") + ":");
        ResourceUtils.resButton(newGroupButton, Res.getString("button.new"));

        accounts.setRenderer(new JPanelRenderer());

        loadGroupBox(groupBox);

        jidField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String jid = getJID();
                String vcardNickname = null;

                if (!publicBox.isSelected()) {
                    // This is not a transport.
                    String fullJID = getJID();
                    if ( !fullJID.contains( "@" ) ) {
                        fullJID = fullJID + "@" + SparkManager.getConnection().getXMPPServiceDomain();
                    }

                    if (!fullJID.startsWith( "@" ))
                    {
                        BareJid bareJid = JidCreate.bareFromOrThrowUnchecked(fullJID);
                        vcardNickname = SparkManager.getUserManager().getNickname( bareJid );
                    }
                }

                String nickname = nicknameField.getText();
                if (isBlank(nickname) && !isBlank(jid)) {
                    nickname = XmppStringUtils.parseLocalpart(jid);
                    if (isBlank(nickname)) {
                        nickname = jid;
                    }
                    nicknameField.setText(vcardNickname != null ? vcardNickname : nickname);
                }
            }
        });

        List<AccountItem> accountCol = getAccounts();
        if (!accountCol.isEmpty()) {
            for (AccountItem item : accountCol) {
                accounts.addItem(item);
            }
            accountsLabel.setVisible(true);
            accounts.setVisible(true);
            publicBox.setVisible(true);
            networkPanel.setVisible(true);
        }

        publicBox.addActionListener( actionEvent -> accounts.setEnabled(publicBox.isSelected()) );
    }

    /**
     * Sets the default <code>ContactGroup</code> to display in the combo box.
     */
    public void setDefaultGroup(ContactGroup contactGroup) {
        String groupName = contactGroup.getGroupName();
        addItemIfNotExists(groupBox, groupName);
        groupBox.setSelectedItem(groupName);
    }

    /**
     * Sets the default jid to show in the jid field.
     */
    public void setDefaultJID(String jid) {
        jidField.setText(jid);
    }

    /**
     * Sets the default nickname to show in the nickname field.
     */
    public void setDefaultNickname(String nickname) {
        nicknameField.setText(nickname);
    }


    public void newGroupButtonOnClick(ActionEvent e) {
        String group = JOptionPane.showInputDialog(dialog, Res.getString("label.enter.group.name") + ":", Res.getString("title.new.roster.group"), JOptionPane.QUESTION_MESSAGE);
        if (isBlank(group)) {
            return;
        }
        SparkManager.getRoster().createGroup(group);
        addItemIfNotExists(groupBox, group);
        int size = groupBox.getItemCount();
        groupBox.setSelectedIndex(size - 1);
    }

    /**
     * Display the RosterDialog using a parent container.
     */
    public void showRosterDialog(JFrame parent) {
        TitlePanel titlePanel = new TitlePanel(Res.getString("title.add.contact"), Res.getString("message.add.contact.to.list"), null, true);

        JPanel mainPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width = 450;
                return size;
            }
        };

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);

        JButton buttonAdd = new JButton(Res.getString("add"));

        buttonAdd.addActionListener( e -> addContactButton() );

        JButton buttonCancel = new JButton(Res.getString("cancel"));
        buttonCancel.addActionListener( e -> dialog.dispose() );


        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(buttonAdd);
        panelButtons.add(buttonCancel);
        mainPanel.add(panelButtons, BorderLayout.SOUTH);

        dialog = new JDialog(parent, Res.getString("title.add.contact"), false);
        dialog.setContentPane(mainPanel);
        dialog.pack();

        dialog.setLocationRelativeTo(parent);

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

    private void addEntry() {
        Transport transport = null;
        AccountItem item;
        if (publicBox.isSelected()) {
            item = (AccountItem)accounts.getSelectedItem();
            transport = item.getTransport();
        }
        if (transport == null) {
            String jid = getJID();
            if ( !jid.contains( "@" ) ) {
                jid = jid + "@" + SparkManager.getConnection().getXMPPServiceDomain();
            }
            String nickname = nicknameField.getText();
            String group = (String)groupBox.getSelectedItem();

            jid = UserManager.escapeJID(jid);
            BareJid bareJid = JidCreate.bareFromOrThrowUnchecked(jid);
            // Add as a new entry
            addRosterEntry(bareJid, nickname, group);
        } else {
            String jid = getJID();
            try {
                jid = Gateway.getJID(transport.getXMPPServiceDomain(), jid);
            } catch (Exception e) {
                Log.error(e);
            }

            String nickname = nicknameField.getText();
            String group = (String)groupBox.getSelectedItem();
            BareJid bareJid = JidCreate.bareFromOrThrowUnchecked(jid);
            addRosterEntry(bareJid, nickname, group);
        }
    }

    /**
     * Returns the trimmed version of the JID.
     */
    private String getJID() {
        return jidField.getText().trim();
    }

    private void addRosterEntry(final BareJid jid, final String nickname, final String group) {
        final SwingWorker rosterEntryThread = new SwingWorker() {
            @Override
            public Object construct() {
                return addEntry(jid, nickname, group);
            }

            @Override
            public void finished() {
                if (!(Boolean) get()) {
                    JOptionPane.showMessageDialog(dialog, Res.getString("label.unable.to.add.contact"), Res.getString("title.error"), ERROR_MESSAGE);
                }
            }
        };

        rosterEntryThread.start();
    }

    /**
     * Creates a PopupDialog above the Search Button displaying matching Contacts
     *
     * @param byname the Search name, at least 2 chars long
     */
    public void searchForContact(String byname, MouseEvent event)
        throws XMPPException, SmackException.NotConnectedException, SmackException.NoResponseException, InterruptedException {
        if (byname.contains("@")) {
            byname = byname.substring(0, byname.indexOf("@"));
        }

        if (byname.length() < 2) {
            JOptionPane.showMessageDialog(jidField,
                Res.getString("message.search.input.short"),
                Res.getString("title.notification"),
                ERROR_MESSAGE);
            return;
        }
        JPopupMenu popup = new JPopupMenu();
        JMenuItem header = new JMenuItem(Res.getString("group.search.results") + ":");
        header.setBackground(UIManager.getColor("List.selectionBackground"));
        header.setForeground(Color.red);
        popup.add(header);

        for (DomainBareJid search : _usersearchservice) {
            UserSearchManager usersearchManager = UserSearchManager.getInstanceFor(SparkManager.getConnection());

            DataForm f = usersearchManager.getSearchForm(search).getDataForm();
            FillableForm answer = new FillableForm(f);
            answer.setAnswer("Name", true);
            answer.setAnswer("Email", true);
            answer.setAnswer("Username", true);
            answer.setAnswer("search", byname);

            ReportedData data = usersearchManager.sendSimpleSearchForm(answer.getDataFormToSubmit(), search);

            ArrayList<String> columnNames = new ArrayList<>();
            for (ReportedData.Column column : data.getColumns()) {
                String label = column.getLabel();
                columnNames.add(label);
            }

            for (ReportedData.Row row : data.getRows()) {
                if (!row.getValues(columnNames.get(0)).isEmpty()) {
                    String s = row.getValues(columnNames.get(0)).get(0).toString();
                    JMenuItem item = new JMenuItem(s);
                    item.addActionListener(e -> {
                        String jid = item.getText();
                        String nick = XmppStringUtils.parseLocalpart(jid);
                        jidField.setText(jid);
                        nicknameField.setText(nick);
                    });
                    popup.add(item);
                }
            }
        }

        if (popup.getComponentCount() > 2) {
            popup.setVisible(true);
            popup.show(_searchForName, event.getX(), event.getY());
        } else if (popup.getComponentCount() == 2) {
            String jid = ((JMenuItem) popup.getComponent(1)).getText();
            String nick = XmppStringUtils.parseLocalpart(jid);
            jidField.setText(jid);
            nicknameField.setText(nick);
        } else {
            JOptionPane.showMessageDialog(jidField,
                Res.getString("message.no.results.found"),
                Res.getString("title.notification"),
                ERROR_MESSAGE);
        }
    }


    /**
     * Adds a new entry to the users Roster.
     * The contact is added asynchronously, the roaster will be updated accordingly.
     * If the account doesn't exist on a server we'll not see an error.
     *
     * @return true if the new contact added or updated RosterEntry.
     */
    public boolean addEntry(BareJid jid, String nickname, String group) {
        Roster roster = SparkManager.getRoster();
        RosterEntry userEntry = roster.getEntry(jid);
        boolean isNewContact = userEntry == null || userEntry.getGroups().isEmpty();
        if (isNewContact) {
            try {
                addContactToRoster(jid, nickname, new String[]{group}, roster);
                return true;
            } catch (XMPPException | SmackException | InterruptedException e) {
                Log.error("Unable to add contact to contact list " + jid, e);
                return false;
            }
        }
        // Already known contact, ensure that it has the specified nickname and added to the group
        try {
            userEntry.setName(nickname);
            RosterGroup rosterGroup = roster.createGroup(group);
            rosterGroup.addEntry(userEntry);
            return true;
        } catch (XMPPException | SmackException | InterruptedException ex) {
            Log.error("Unable to update contact nickname and group" + jid, ex);
            return false;
        }
    }

    private void addContactToRoster(BareJid contactJid, String displayName, String[] parentNames, Roster roster) throws SmackException, XMPPException.XMPPErrorException, InterruptedException {
        if (roster.isSubscriptionPreApprovalSupported()) {
            try {
                roster.preApproveAndCreateEntry(contactJid, displayName, parentNames);
                return;
            } catch (SmackException.FeatureNotSupportedException ignored) {
                // never happens because we checked isSubscriptionPreApprovalSupported
            }
        }
        roster.createItemAndRequestSubscription(contactJid, displayName, parentNames);
    }

    public List<AccountItem> getAccounts() {
        List<AccountItem> list = new ArrayList<>();
        for (Transport transport : TransportUtils.getTransports()) {
            if (TransportUtils.isRegistered(transport)) {
                AccountItem item = new AccountItem(transport.getIcon(), transport.getName(), transport);
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Method to handle the Add-Button
     */
    private void addContactButton() {
        String jid = getJID();
        if (jid.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, Res.getString("message.invalid.jid.error"),
                Res.getString("title.error"), ERROR_MESSAGE);
            return;
        }

        String contact = jid;
        String nickname = nicknameField.getText();
        String group = (String) groupBox.getSelectedItem();

        Transport transport = null;
        if (publicBox.isSelected()) {
            AccountItem item = (AccountItem) accounts.getSelectedItem();
            transport = item.getTransport();
        }

        if (transport == null) {
            if (!contact.contains("@")) {
                contact = contact + "@" + SparkManager.getConnection().getXMPPServiceDomain();
            }
        } else {
            if (!contact.contains("@")) {
                contact = contact + "@" + transport.getXMPPServiceDomain();
            }
        }

        // Escape only after a complete JID has been constructed. Escaping a bare
        // username first causes XmppStringUtils.parseLocalpart() to return null.
        contact = UserManager.escapeJID(contact);

        if (isBlank(nickname) && !isBlank(contact)) {
            // Try to load nickname from VCard
            try {
                EntityBareJid contactJid = JidCreate.entityBareFrom(contact);
                VCardManager vCardManager = VCardManager.getInstanceFor(SparkManager.getConnection());
                VCard vcard = vCardManager.loadVCard(contactJid);
                nickname = vcard.getNickName();
            } catch (XMPPException | SmackException | XmppStringprepException | InterruptedException e1) {
                Log.error(e1);
            }
            // If no nickname, use first name.
            if (isBlank(nickname)) {
                nickname = XmppStringUtils.parseLocalpart(contact);
            }
            nicknameField.setText(nickname);
        }

        ContactGroup contactGroup = contactList.getContactGroup(group);
        boolean isSharedGroup = contactGroup != null && contactGroup.isSharedGroup();
        String errorMessage = null;
        if (isSharedGroup) {
            errorMessage = Res.getString("message.cannot.add.contact.to.shared.group");
        } else if (isBlank(contact)) {
            errorMessage = Res.getString("message.specify.contact.jid");
        } else if (!XmppStringUtils.parseBareJid(contact).contains("@")) {
            errorMessage = Res.getString("message.invalid.jid.error");
        } else if (isBlank(group)) {
            errorMessage = Res.getString("message.specify.group");
        } else {
            addEntry();
        }
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(dialog, errorMessage, Res.getString("title.error"), ERROR_MESSAGE);
            return;
        }
        dialog.setVisible(false);
    }

    static class AccountItem extends JPanel {
        private final Transport transport;

        public AccountItem(Icon icon, String name, Transport transport) {
            requireNonNull(icon);
            requireNonNull(name);
            requireNonNull(transport);
            setLayout(new GridBagLayout());
            this.transport = transport;

            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(icon);

            JLabel label = new JLabel();
            label.setText(name);
            label.setFont(new Font("Dialog", Font.PLAIN, 11));
            label.setHorizontalTextPosition(JLabel.CENTER);

            add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
            add(label, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));

            setBackground(Color.white);
        }

        public Transport getTransport() {
            return transport;
        }
    }
}
