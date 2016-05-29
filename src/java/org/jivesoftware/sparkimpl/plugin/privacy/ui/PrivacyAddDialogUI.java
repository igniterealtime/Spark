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
package org.jivesoftware.sparkimpl.plugin.privacy.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;


import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.log.Log;

/**
 * @author Bergunde Holger
 */
public class PrivacyAddDialogUI extends JPanel {

    private JCheckBox _showOffCheckbox = new JCheckBox();
    private static final long serialVersionUID = -7725304880236329893L;
    private DefaultListModel<ContactItem> model = new DefaultListModel<>();
    private JList<ContactItem> rosterList = new JList<>( model );
    private boolean _showGroups = false;
    private List<ContactItem> _userList = new ArrayList<>();
    private JCheckBox _blockPIn;
    private JCheckBox _blockPOout;
    private JCheckBox _blockMsg;
    private JCheckBox _blockIQ;

    /**
     * Creates a new instance of the RosterBrowser.
     */
    public PrivacyAddDialogUI() {
        setLayout(new GridBagLayout());

        _showOffCheckbox.setText(Res.getString("menuitem.show.offline.users"));
        _showOffCheckbox.addActionListener( e -> createList() );

        JPanel checkBoxPanel = createCheckBoxes();
        this.add(checkBoxPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.add(_showOffCheckbox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(new JScrollPane(rosterList), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));


    }

    private JPanel createCheckBoxes() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(Res.getString("privacy.border.block")));
        _blockPIn = new JCheckBox(Res.getString("privacy.label.pin.desc"));
        _blockPIn.setIcon(SparkRes.getImageIcon("PRIVACY_PIN_ALLOW"));
        _blockPIn.setSelectedIcon(SparkRes.getImageIcon("PRIVACY_PIN_DENY"));
        _blockPIn.setRolloverEnabled(false);
        _blockPOout = new JCheckBox(Res.getString("privacy.label.pout.desc"));
        _blockPOout.setIcon(SparkRes.getImageIcon("PRIVACY_POUT_ALLOW"));
        _blockPOout.setSelectedIcon(SparkRes.getImageIcon("PRIVACY_POUT_DENY"));
        _blockPOout.setRolloverEnabled(false);
        _blockMsg = new JCheckBox(Res.getString("privacy.label.msg.desc"));
        _blockMsg.setSelectedIcon(SparkRes.getImageIcon("PRIVACY_MSG_DENY"));
        _blockMsg.setIcon(SparkRes.getImageIcon("PRIVACY_MSG_ALLOW"));
        _blockMsg.setRolloverEnabled(false);
        _blockIQ = new JCheckBox(Res.getString("privacy.label.iq.desc"));
        _blockIQ.setSelectedIcon(SparkRes.getImageIcon("PRIVACY_QUERY_DENY"));
        _blockIQ.setIcon(SparkRes.getImageIcon("PRIVACY_QUERY_ALLOW"));
        _blockIQ.setRolloverEnabled(false);
        panel.add(_blockPIn, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(_blockPOout, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(_blockMsg, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(_blockIQ, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        return panel;
    }

    private void createList() {
        _userList.clear();
        final Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
        if (_showGroups) {
            for (RosterGroup group : roster.getGroups()) {
                _showOffCheckbox.setVisible(false);
                ContactItem item = new ContactItem(group.getName(), null, group.getName());
                _userList.add(item);
            }
        } else {
            for (RosterEntry entry : roster.getEntries()) {
                Presence presence = PresenceManager.getPresence(entry.getUser());

                if (presence.isAvailable()) {
                    ContactItem item = new ContactItem(entry.getName(), null, entry.getUser());
                    item.setPresence(presence);
                    _userList.add(item);
                } else if (_showOffCheckbox.isSelected()) {
                    ContactItem item = new ContactItem(entry.getName(), null, entry.getUser());
                    item.setPresence(presence);
                    _userList.add(item);
                }
            }
        }

        Collections.sort(_userList, itemComparator);
        model.clear();
        for (ContactItem item : _userList) {

            model.addElement(item);
        }


    }

    /**
     * Displays a pick list of available users within their roster.
     * 
     * @param parent
     *            the parent container.
     * @return all items chosen in the pick list.
     */
    public Collection<PrivacyItem> showRoster(Component parent, boolean showGroups) {
        _showGroups = showGroups;
        // Populate Invite Panel with Available users.

        createList();

        // Sort Users

        final JOptionPane pane;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("privacy.title.add.picker"), Res.getString("privacy.pick.one.or.more"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("ok"), Res.getString("cancel")};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        final JDialog dlg = p.createDialog(parent, Res.getString("privacy.title.add.picker"));
        dlg.setModal(true);

        dlg.pack();
        dlg.setSize(350, 450);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = e -> {
            String value = (String) pane.getValue();
            if (Res.getString("cancel").equals(value)) {
                rosterList.clearSelection();
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                dlg.dispose();
            } else if (Res.getString("ok").equals(value)) {
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                dlg.dispose();
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        List<PrivacyItem> selectedContacts = new ArrayList<>();



        Object[] values = rosterList.getSelectedValues();
        final int no = values != null ? values.length : 0;
        for (int i = 0; i < no; i++) {
            try {
                ContactItem item = (ContactItem) values[i];

                PrivacyItem.Type type = _showGroups ? PrivacyItem.Type.group : PrivacyItem.Type.jid;
                PrivacyItem pitem = new PrivacyItem(type, item.getJID(), false, 999);
                pitem.setFilterIQ(_blockIQ.isSelected());
                pitem.setFilterMessage(_blockMsg.isSelected());
                pitem.setFilterPresenceIn(_blockPIn.isSelected());
                pitem.setFilterPresenceOut(_blockPOout.isSelected());

                selectedContacts.add(pitem);
            } catch (NullPointerException e) {
                Log.error(e);
            }
        }

        return selectedContacts;
    }
    /**
     * Sorts ContactItems.
     */
    final Comparator<ContactItem> itemComparator = ( item1, item2 ) -> {
        String nickname1 = item1.getDisplayName();
        String nickname2 = item2.getDisplayName();
        if (nickname1 == null || nickname2 == null) {
            return 0;
        }

        return nickname1.toLowerCase().compareTo(nickname2.toLowerCase());

    };
}
