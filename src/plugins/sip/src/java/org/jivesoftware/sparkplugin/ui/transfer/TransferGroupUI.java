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
package org.jivesoftware.sparkplugin.ui.transfer;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.sparkplugin.callhistory.TelephoneUtils;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ModelUtil;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

/**
 * The UI that represents one group within the users Roster.
 */
public class TransferGroupUI extends JPanel {

    private static final long serialVersionUID = 1L;

    private List<TransferListener> listeners = new ArrayList<TransferListener>();

    private List<UserEntry> userEntries = new ArrayList<UserEntry>();

    private boolean containsNumbers;



    public TransferGroupUI(String groupName) {
        setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        setBackground(Color.white);

        final Roster roster = SparkManager.getConnection().getRoster();
        final RosterGroup rosterGroup = roster.getGroup(groupName);

        final List<RosterEntry> entries = new ArrayList<RosterEntry>(rosterGroup.getEntries());

        Collections.sort(entries, entryComparator);

        for (RosterEntry entry : entries) {
            final UserEntry userEntry = new UserEntry(entry);
            userEntries.add(userEntry);
            add(userEntry);
        }
    }

    /**
     * Individual UI for one single user.
     */
    private class UserEntry extends JPanel {

        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
        private String workNumber;
        private String homeNumber;
        private String mobileNumber;

        public UserEntry(RosterEntry entry) {
            setLayout(new GridBagLayout());
            final Roster roster = SparkManager.getConnection().getRoster();
            Presence presence = roster.getPresence(entry.getUser());

            Icon icon = PresenceManager.getIconFromPresence(presence);
            String status = presence.getStatus() != null ? presence.getStatus() : "";

            if ("Online".equals(status) || Res.getString("available").equalsIgnoreCase(status)) {
                status = "";
            }

            String nickname = entry.getName();
            this.name = nickname;

            setOpaque(false);

            final JLabel contactLabel = new JLabel(nickname, icon, JLabel.LEFT);


            final JLabel descriptionLabel = new JLabel();
            descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
            descriptionLabel.setForeground((Color)UIManager.get("ContactItemDescription.foreground"));
            descriptionLabel.setHorizontalTextPosition(JLabel.LEFT);
            descriptionLabel.setHorizontalAlignment(JLabel.LEFT);
            if (ModelUtil.hasLength(status)) {
                descriptionLabel.setText(" - " + status);
            }

            final RolloverButton transferButton = new RolloverButton(PhoneRes.getImageIcon("TRANSFER_IMAGE"));
            transferButton.setMargin(new Insets(0, 0, 0, 0));

            VCard vcard = SparkManager.getVCardManager().getVCardFromMemory(entry.getUser());
            homeNumber = vcard.getPhoneHome("VOICE");
            workNumber = vcard.getPhoneWork("VOICE");
            mobileNumber = vcard.getPhoneWork("CELL");

            final List<Action> actions = new ArrayList<Action>();


            if (ModelUtil.hasLength(homeNumber)) {
                Action homeAction = new NumberAction("Home:", homeNumber, PhoneRes.getImageIcon("HOME_IMAGE"));
                actions.add(homeAction);
            }

            if (ModelUtil.hasLength(workNumber)) {
                Action workAction = new NumberAction("Work:", workNumber, PhoneRes.getImageIcon("WORK_IMAGE"));
                actions.add(workAction);
            }

            if (ModelUtil.hasLength(mobileNumber)) {
                Action mobileAction = new NumberAction("Cell:  ", mobileNumber, PhoneRes.getImageIcon("MOBILE_IMAGE"));
                actions.add(mobileAction);
            }

            if (actions.size() > 0) {
                containsNumbers = true;
                add(contactLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
                add(descriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                add(transferButton, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));

                transferButton.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent mouseEvent) {
                        if (actions.size() > 1) {
                            JPopupMenu popupMenu = new JPopupMenu();
                            for (Action action : actions) {
                                popupMenu.add(action);
                            }
                            popupMenu.show(transferButton, mouseEvent.getX(), mouseEvent.getY());
                        }
                        else {
                            Action action = actions.get(0);
                            action.actionPerformed(null);
                        }
                    }
                });
            }


        }

        @SuppressWarnings("unused")
	public String getNickname() {
            return name;
        }

        public String getWorkNumber() {
            return workNumber;
        }

        public String getHomeNumber() {
            return homeNumber;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }
    }


    /**
     * Simple Action to handle selection of numbers.
     */
    private class NumberAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private String number;

        public NumberAction(String label, String number, Icon icon) {
            this.number = number;
            putValue(Action.NAME, "<html><b>" + label + "</b>&nbsp;&nbsp;" + number + "</html>");
            putValue(Action.SMALL_ICON, icon);
        }

        public void actionPerformed(ActionEvent e) {
            fireTransferListeners(number);
        }
    }

    public void sort(String text) {
        text = TelephoneUtils.removeInvalidChars(text);
        for (UserEntry entry : userEntries) {
            String home = TelephoneUtils.removeInvalidChars(entry.getHomeNumber());
            if (home == null) {
                home = "";
            }

            String work = TelephoneUtils.removeInvalidChars(entry.getWorkNumber());
            if (work == null) {
                work = "";
            }

            String mobileNumber = TelephoneUtils.removeInvalidChars(entry.getMobileNumber());
            if (mobileNumber == null) {
                mobileNumber = "";
            }

            boolean match = false;
            if (home.startsWith(text) || work.startsWith(text) || mobileNumber.startsWith(text)) {
                match = true;
            }
            entry.setVisible(match);
        }

        invalidate();
        validate();
        repaint();
    }

    /**
     * Add a TransferListener.
     *
     * @param listener the listener.
     */
    public void addTransferListener(TransferListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a TransferListener.
     *
     * @param listener the listener.
     */
    public void removeTransferListener(TransferListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all TransferListeners that a number has been selected.
     */
    public void fireTransferListeners(String number) {
        for (TransferListener listener : listeners) {
            listener.numberSelected(number);
        }
    }

    /**
     * Sorts RosterEntries
     */
    final Comparator<RosterEntry> entryComparator = new Comparator<RosterEntry>() {
        public int compare(RosterEntry one, RosterEntry two) {
            final RosterEntry entryOne = one;
            final RosterEntry entryTwo = two;
            return entryOne.getName().toLowerCase().compareTo(entryTwo.getName().toLowerCase());

        }
    };

    public boolean hasTelephoneContacts(){
        return containsNumbers;
    }
}
