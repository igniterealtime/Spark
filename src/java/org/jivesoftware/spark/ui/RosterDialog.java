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
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Column;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.borders.ComponentTitledBorder;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.Gateway;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;


/**
 * The RosterDialog is used to add new users to the users XMPP Roster.
 */
public class RosterDialog implements ActionListener {
    private JPanel panel;
    private JTextField jidField;
    private JTextField nicknameField;
    private final Vector<String> groupModel = new Vector<String>();
    private final JPanel networkPanel = new JPanel(new GridBagLayout());

    private JComboBox groupBox;
    private JComboBox accounts;
    private JDialog dialog;
    private ContactList contactList;
    private JCheckBox publicBox;
    private JButton _searchForName ;
    private Collection<String> _usersearchservice;

    /**
     * Create a new instance of RosterDialog.
     */
    public RosterDialog() {
        contactList = SparkManager.getWorkspace().getContactList();
        
        _usersearchservice = SearchManager.getInstance().getSearchServicesAsString();

        panel = new JPanel();
        JLabel contactIDLabel = new JLabel();
        jidField = new JTextField();
        JLabel nicknameLabel = new JLabel();
        nicknameField = new JTextField();
        JLabel groupLabel = new JLabel();
        groupBox = new JComboBox(groupModel);

        JButton newGroupButton = new JButton();

        JLabel accountsLabel = new JLabel();
        accounts = new JComboBox();
        publicBox = new JCheckBox(Res.getString("label.user.on.public.network"));

        ResourceUtils.resLabel(accountsLabel, publicBox, Res.getString("label.network"));
        
        _searchForName = new JButton();
        _searchForName.setIcon(SparkRes.getImageIcon(SparkRes.TRANSFER_IMAGE_24x24));

	_searchForName.addMouseListener(new MouseListener() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		try {
		    searchForContact(jidField.getText(), e);
		} catch (XMPPException e1) {
		    Log.error("search contact", e1);
		}
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
	    }
	});

        dialog = null;
        panel.setLayout(new GridBagLayout());
        panel.add(contactIDLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(jidField	, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(_searchForName, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 5, 5, 5), 0, 0));

        
        panel.add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(nicknameField, new GridBagConstraints(1, 1, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));


        ComponentTitledBorder componentBorder = new ComponentTitledBorder(publicBox, networkPanel
                , BorderFactory.createEtchedBorder());


        networkPanel.add(accountsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        networkPanel.add(accounts, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 17, 2, new Insets(5, 5, 5, 5), 0, 0));

        networkPanel.setBorder(componentBorder);

        networkPanel.setVisible(false);
        accounts.setEnabled(false);


        panel.add(groupLabel, new GridBagConstraints(0, 4, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        
        panel.add(groupBox, new GridBagConstraints(1, 4, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        
        if(!Default.getBoolean("ADD_CONTACT_GROUP_DISABLED")){
        panel.add(newGroupButton, new GridBagConstraints(2, 4, 1, 1, 0.0D, 0.0D, 17, 1, new Insets(5, 5, 5, 5), 0, 0));
        }
        newGroupButton.addActionListener(this);

        panel.add(networkPanel, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


        ResourceUtils.resLabel(contactIDLabel, jidField, Res.getString("label.username") + ":");
        ResourceUtils.resLabel(nicknameLabel, nicknameField, Res.getString("label.nickname") + ":");
        ResourceUtils.resLabel(groupLabel, groupBox, Res.getString("label.group") + ":");
        ResourceUtils.resButton(newGroupButton, Res.getString("button.new"));

        accounts.setRenderer(new JPanelRenderer());

        for (ContactGroup group : contactList.getContactGroups()) {
            if (!group.isOfflineGroup() && !Res.getString("unfiled").equalsIgnoreCase(group.getGroupName()) && !group.isSharedGroup()) {
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
                String jid = getJID();
                String vcardNickname = null;

                if (!publicBox.isSelected()) {
                    // This is not a transport.
                    String fullJID = getJID();
                    if (fullJID.indexOf("@") == -1) {
                        fullJID = fullJID + "@" + SparkManager.getConnection().getServiceName();
                    }

                    vcardNickname = SparkManager.getUserManager().getNickname(fullJID);
                }

                String nickname = nicknameField.getText();
                if (!ModelUtil.hasLength(nickname) && ModelUtil.hasLength(jid)) {
                    nickname = StringUtils.parseName(jid);
                    if (!ModelUtil.hasLength(nickname)) {
                        nickname = jid;
                    }

                    nicknameField.setText(vcardNickname != null ? vcardNickname : nickname);
                }
            }
        });

        final List<AccountItem> accountCol = getAccounts();
        for (AccountItem item : accountCol) {
            accounts.addItem(item);
        }

        if (accountCol.size() > 0) {
            accountsLabel.setVisible(true);
            accounts.setVisible(true);
            publicBox.setVisible(true);
            networkPanel.setVisible(true);
        }

        publicBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                accounts.setEnabled(publicBox.isSelected());
            }
        });


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
        TitlePanel titlePanel = new TitlePanel(Res.getString("title.add.contact"), Res.getString("message.add.contact.to.list"), null, true);


	JPanel mainPanel = new JPanel() {
	    private static final long serialVersionUID = -7489967438182277375L;

	    public Dimension getPreferredSize() {
		final Dimension size = super.getPreferredSize();
		size.width = 450;
		return size;
	    }
	};

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
       
        JButton addbutton = new JButton(Res.getString("add"));
        
        addbutton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		addContactButton();
	    }
	});
        
        JButton cancelbutton = new JButton(Res.getString("cancel"));
        cancelbutton.addActionListener(new ActionListener() {    
	    @Override
	    public void actionPerformed(ActionEvent e) {
		dialog.dispose();
	    }
	});
        
        
        JPanel buttonpanel = new JPanel(new FlowLayout());
        buttonpanel.add(addbutton);
        buttonpanel.add(cancelbutton);
        mainPanel.add(buttonpanel, BorderLayout.SOUTH);
        
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
            if (jid.indexOf("@") == -1) {
                jid = jid + "@" + SparkManager.getConnection().getServiceName();
            }
            String nickname = nicknameField.getText();
            String group = (String)groupBox.getSelectedItem();

            jid = UserManager.escapeJID(jid);

            // Add as a new entry
            addRosterEntry(jid, nickname, group);
        }
        else {
            String jid = getJID();
            try {
                jid = Gateway.getJID(transport.getServiceName(), jid);
            }
            catch (XMPPException e) {
                Log.error(e);
            }

            String nickname = nicknameField.getText();
            String group = (String)groupBox.getSelectedItem();
            addRosterEntry(jid, nickname, group);
        }
    }

    /**
     * Returns the trimmed version of the JID.
     *
     * @return the trimmed version.
     */
    private String getJID() {
        return jidField.getText().trim();
    }

    private void addRosterEntry(final String jid, final String nickname, final String group) {
        final SwingWorker rosterEntryThread = new SwingWorker() {
            public Object construct() {
                return addEntry(jid, nickname, group);
            }

            public void finished() {
                if (get() == null) {
                    JOptionPane.showMessageDialog(dialog, Res.getString("label.unable.to.add.contact"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                }
            }

        };

        rosterEntryThread.start();
    }
    
    /**
     * Creates a Popupdialog above the Search Button displaying matching
     * Contacts
     * 
     * @param byname
     *            , the Searchname, atleast 5 Chars long
     * @param event
     *            , the MouseEvent which triggered it
     * @throws XMPPException
     */
    public void searchForContact(String byname, MouseEvent event)
	    throws XMPPException {

	if (byname.contains("@")) {
	    byname = byname.substring(0, byname.indexOf("@"));
	}

	if (byname.length() <= 4) {
	    JOptionPane.showMessageDialog(jidField,
		    Res.getString("message.search.input.short"),
		    Res.getString("title.notification"),
		    JOptionPane.ERROR_MESSAGE);

	} else {

	    JPopupMenu popup = new JPopupMenu();
	    JMenuItem header = new JMenuItem(
		    Res.getString("group.search.results") + ":");
	    header.setBackground(UIManager.getColor("List.selectionBackground"));
	    header.setForeground(Color.red);
	    popup.add(header);

	    for (String search : _usersearchservice) {

		ReportedData data;
		UserSearchManager usersearchManager = new UserSearchManager(
			SparkManager.getConnection());

		Form f = usersearchManager.getSearchForm(search);

		Form answer = f.createAnswerForm();
		answer.setAnswer("Name", true);
		answer.setAnswer("Email", true);
		answer.setAnswer("Username", true);
		answer.setAnswer("search", byname);

		data = usersearchManager.getSearchResults(answer, search);

		ArrayList<String> columnnames = new ArrayList<String>();
		Iterator<Column> columns = data.getColumns();
		while (columns.hasNext()) {
		    ReportedData.Column column = (ReportedData.Column) columns
			    .next();
		    String label = column.getLabel();
		    columnnames.add(label);
		}

		Iterator<Row> rows = data.getRows();
		while (rows.hasNext()) {
		    ReportedData.Row row = (ReportedData.Row) rows.next();
		    if (row.getValues(columnnames.get(0)).hasNext()) {
			String s = (String) row.getValues(columnnames.get(0))
				.next();
			final JMenuItem item = new JMenuItem(s);
			popup.add(item);
			item.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
				jidField.setText(item.getText());
				nicknameField.setText(StringUtils
					.parseName(item.getText()));
			    }
			});
		    }

		}
	    }

	    if (popup.getComponentCount() > 2) {
		popup.setVisible(true);
		popup.show(_searchForName, event.getX(), event.getY());
	    } else if (popup.getComponentCount() == 2) {
		jidField.setText(((JMenuItem) popup.getComponent(1)).getText());
		nicknameField.setText(StringUtils.parseName(((JMenuItem) popup
			.getComponent(1)).getText()));
	    } else {
		JOptionPane.showMessageDialog(jidField,
			Res.getString("message.no.results.found"),
			Res.getString("title.notification"),
			JOptionPane.ERROR_MESSAGE);
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

        for (Transport transport : TransportUtils.getTransports()) {
            if (TransportUtils.isRegistered(SparkManager.getConnection(), transport)) {
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
	String errorMessage = Res.getString("title.error");
	String jid = getJID();
	
	if(jid.length()==0)
	{
	    JOptionPane.showMessageDialog(dialog, Res.getString("message.invalid.jid.error"),
			Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
		return;
	}
	
	String contact = UserManager.escapeJID(jid);
	String nickname = nicknameField.getText();
	String group = (String) groupBox.getSelectedItem();

	Transport transport = null;
	if (publicBox.isSelected()) {
	    AccountItem item = (AccountItem) accounts.getSelectedItem();
	    transport = item.getTransport();
	}

	if (transport == null) {
	    if (!contact.contains("@")) {
		contact = contact + "@"
			+ SparkManager.getConnection().getServiceName();
	    }
	} else {
	    if (!contact.contains("@")) {
		contact = contact + "@" + transport.getServiceName();
	    }
	}

	if (!ModelUtil.hasLength(nickname) && ModelUtil.hasLength(contact)) {
	    // Try to load nickname from VCard
	    VCard vcard = new VCard();
	    try {
		vcard.load(SparkManager.getConnection(), contact);
		nickname = vcard.getNickName();
	    } catch (XMPPException e1) {
		Log.error(e1);
	    }
	    // If no nickname, use first name.
	    if (!ModelUtil.hasLength(nickname)) {
		nickname = StringUtils.parseName(contact);
	    }
	    nicknameField.setText(nickname);
	}

	ContactGroup contactGroup = contactList.getContactGroup(group);
	boolean isSharedGroup = contactGroup != null
		&& contactGroup.isSharedGroup();

	if (isSharedGroup) {
	    errorMessage = Res
		    .getString("message.cannot.add.contact.to.shared.group");
	} else if (!ModelUtil.hasLength(contact)) {
	    errorMessage = Res.getString("message.specify.contact.jid");
	} else if (!StringUtils.parseBareAddress(contact).contains("@")) {
	    errorMessage = Res.getString("message.invalid.jid.error");
	} else if (!ModelUtil.hasLength(group)) {
	    errorMessage = Res.getString("message.specify.group");
	} else if (ModelUtil.hasLength(contact) && ModelUtil.hasLength(group)
		&& !isSharedGroup) {
	    addEntry();
	    dialog.setVisible(false);
	    return;
	} else {

	    JOptionPane.showMessageDialog(dialog, errorMessage,
		    Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
	}
    }

    class AccountItem extends JPanel {
		private static final long serialVersionUID = -7657731912529801653L;
		private Transport transport;

        public AccountItem(Icon icon, String name, Transport transport) {
            setLayout(new GridBagLayout());
            this.transport = transport;

            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(icon);

            JLabel label = new JLabel();
            label.setText(name);
            label.setFont(new Font("Dialog", Font.PLAIN, 11));
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