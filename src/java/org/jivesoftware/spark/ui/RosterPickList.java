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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The <code>RosterPickList</code> is used as a pick list of users within ones Roster.
 */
public class RosterPickList extends JPanel {
    private DefaultListModel model = new DefaultListModel();
    private JList rosterList = new JList(model);

    /**
     * Creates a new instance of the RosterBrowser.
     */
    public RosterPickList() {
        setLayout(new GridBagLayout());

        rosterList.setCellRenderer(new JPanelRenderer());

        JLabel rosterLabel = new JLabel();
        this.add(rosterLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(new JScrollPane(rosterList), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        ResourceUtils.resLabel(rosterLabel, rosterList, Res.getString("label.available.users.in.roster"));
    }

    /**
     * Displays a pick list of available users within their roster.
     *
     * @param parent the parent container.
     * @return all items choosen in the pick list.
     */
    public Collection showRoster(JDialog parent) {
        final List<ContactItem> userList = new ArrayList<ContactItem>();

        // Populate Invite Panel with Available users.
        final Roster roster = SparkManager.getConnection().getRoster();
        for (RosterEntry entry : roster.getEntries()) {
            Presence presence = PresenceManager.getPresence(entry.getUser());
            if (presence.isAvailable()) {
                ContactItem item = new ContactItem(entry.getName(), entry.getUser());
                item.setPresence(presence);
                userList.add(item);
            }
        }

        // Sort Users
        Collections.sort(userList, itemComparator);

        for (ContactItem item : userList) {
            model.addElement(item);
        }

        final JOptionPane pane;


        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.roster"), Res.getString("message.select.one.or.more"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("ok"), Res.getString("cancel")};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        final JDialog dlg = p.createDialog(parent, Res.getString("title.roster"));
        dlg.setModal(true);

        dlg.pack();
        dlg.setSize(350, 450);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String)pane.getValue();
                if (Res.getString("cancel").equals(value)) {
                    rosterList.clearSelection();
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                }
                else if (Res.getString("ok").equals(value)) {
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        List selectedContacts = new ArrayList();

        Object[] values = rosterList.getSelectedValues();
        final int no = values != null ? values.length : 0;
        for (int i = 0; i < no; i++) {
            ContactItem item = (ContactItem)values[i];
            selectedContacts.add(item.getFullJID());
        }

        return selectedContacts;
    }


    /**
     * Sorts ContactItems.
     */
    final Comparator<ContactItem> itemComparator = new Comparator() {
        public int compare(Object contactItemOne, Object contactItemTwo) {
            final ContactItem item1 = (ContactItem)contactItemOne;
            final ContactItem item2 = (ContactItem)contactItemTwo;
            return item1.getNickname().toLowerCase().compareTo(item2.getNickname().toLowerCase());

        }
    };

}
