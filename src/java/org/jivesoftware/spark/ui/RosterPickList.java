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

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * The <code>RosterPickList</code> is used as a pick list of users within ones Roster.
 */
public class RosterPickList extends JPanel {
	private static final long serialVersionUID = -7725304880236329893L;
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
    public Collection<String> showRoster(JDialog parent) {
        final List<ContactItem> userList = new ArrayList<ContactItem>();

        // Populate Invite Panel with Available users.
        final Roster roster = SparkManager.getConnection().getRoster();
        for (RosterEntry entry : roster.getEntries()) {
            Presence presence = PresenceManager.getPresence(entry.getUser());
            if (presence.isAvailable()) {
                ContactItem item = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
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

        List<String> selectedContacts = new ArrayList<String>();

        Object[] values = rosterList.getSelectedValues();
        final int no = values != null ? values.length : 0;
        for (int i = 0; i < no; i++) {
            try {
                ContactItem item = (ContactItem)values[i];
                selectedContacts.add(item.getJID());
            }
            catch (NullPointerException e) {
                Log.error(e);
            }
        }

        return selectedContacts;
    }


    /**
     * Sorts ContactItems.
     */
    final Comparator<ContactItem> itemComparator = new Comparator<ContactItem>() {
        public int compare(ContactItem item1, ContactItem item2) {
            String nickname1 = item1.getDisplayName();
            String nickname2 = item2.getDisplayName();
            if (nickname1 == null || nickname2 == null) {
                return 0;
            }

            return nickname1.toLowerCase().compareTo(nickname2.toLowerCase());

        }
    };

}
