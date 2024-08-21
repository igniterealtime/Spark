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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.ChatNotFoundException;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.RosterPickList;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class InvitationDialog extends JPanel {
    private static final long serialVersionUID = -8588678602429200581L;
    private JTextField textRoomsField;
    private JComboBox<ConferenceItem> comboRoomsField;

    private final JTextField messageField = new JTextField();


    private final DefaultListModel<String> invitedUsers = new DefaultListModel<>();
    private final JList<String> invitedUserList = new JList<>(invitedUsers);

    private JDialog dlg;

    public InvitationDialog(boolean adhoc) {
        JComponent roomsField;
        if (adhoc) {
            roomsField = new JTextField();
            textRoomsField = (JTextField) roomsField;
        } else {
            roomsField = new JComboBox<>();
            comboRoomsField = (JComboBox<ConferenceItem>) roomsField;
            comboRoomsField.setEditable(true);
            comboRoomsField.addActionListener( e -> {
                // get selected bookmark and persist it:
                BookmarkedConference bookmarkedConf;
                Object bookmarkedConfItem = comboRoomsField.getSelectedItem();
                if (bookmarkedConfItem instanceof ConferenceItem) {
                    bookmarkedConf = ((ConferenceItem) bookmarkedConfItem).getBookmarkedConf();
                    SettingsManager.getLocalPreferences().setDefaultBookmarkedConf(bookmarkedConf.getJid());
                    SettingsManager.saveSettings();
                }
            } );
        }
        GridBagLayout gridBagLayout1 = new GridBagLayout();
        setLayout( gridBagLayout1 );

        JLabel roomsLabel = new JLabel();
        add( roomsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add( roomsField, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


        JLabel messageLabel = new JLabel();
        add( messageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(messageField, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        JLabel jidLabel = new JLabel();
        final JTextField jidField = new JTextField();
        JButton addJIDButton = new JButton();
        JButton browseButton = new JButton();
        ResourceUtils.resButton(addJIDButton, Res.getString("button.add"));
        ResourceUtils.resButton(browseButton, Res.getString("button.roster"));
        ResourceUtils.resLabel(jidLabel, jidField, Res.getString("label.add.jid"));

        add(jidLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(jidField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(addJIDButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(browseButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        addJIDButton.addActionListener( actionEvent -> {
            String jid = jidField.getText();
            String server = XmppStringUtils.parseBareJid(jid);
            if (server == null || !server.contains( "@" ) ) {
                UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                JOptionPane.showMessageDialog(dlg, Res.getString("message.enter.valid.jid"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                jidField.setText("");
                jidField.requestFocus();
            }
            else {
                if (!invitedUsers.contains(jid)) {
                    invitedUsers.addElement(jid);
                }
                jidField.setText("");
            }
        } );

        browseButton.addActionListener( actionEvent -> {
            RosterPickList browser = new RosterPickList();
            Collection<String> col = browser.showRoster(dlg);

            for (String aCol : col) {
                if (!invitedUsers.contains(aCol)) {
                    invitedUsers.addElement(aCol);
                }
            }
        } );


        JLabel inviteLabel = new JLabel();
        add( inviteLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(new JScrollPane(invitedUserList), new GridBagConstraints(1, 3, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // Add Resource Utils
        ResourceUtils.resLabel( messageLabel, messageField, Res.getString("label.message") + ":");
        ResourceUtils.resLabel( roomsLabel, roomsField, Res.getString("label.room") + ":");
        inviteLabel.setText(Res.getString("label.invited.users"));

        messageField.setText(Res.getString("message.please.join.in.conference"));

        // Add Listener to list
        invitedUserList.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseReleased(MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger()) {
                    showPopup(mouseEvent);
                }
            }

            @Override
			public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger()) {
                    showPopup(mouseEvent);
                }
            }
        });
    }

    private void showPopup(MouseEvent e) {
        final JPopupMenu popup = new JPopupMenu();
        final int index = invitedUserList.locationToIndex(e.getPoint());

        Action removeAction = new AbstractAction() {
	    private static final long serialVersionUID = 7837533277115442942L;

	    @Override
		public void actionPerformed(ActionEvent e) {
                invitedUsers.remove(index);
            }
        };

        removeAction.putValue(Action.NAME, Res.getString("menuitem.remove"));
        removeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

        popup.add(removeAction);

        popup.show(invitedUserList, e.getX(), e.getY());

    }

    private void fillRoomsUI(Collection<BookmarkedConference> rooms, String adHocRoomName) {
        if (textRoomsField != null) {
            textRoomsField.setText(adHocRoomName);
        }
        if (comboRoomsField != null) {
            // comboRoomsField.setSelectedIndex(-1);
            ConferenceItem bookmarkedConf = null;
            final EntityBareJid bookmarkedConfJid = SettingsManager.getLocalPreferences().getDefaultBookmarkedConf();

            for (BookmarkedConference room : rooms) {
                final ConferenceItem ci = new ConferenceItem(room);
                if (bookmarkedConfJid != null && bookmarkedConfJid.equals(ci.getBookmarkedConf().getJid())) {
                    bookmarkedConf = ci;
                }
                comboRoomsField.addItem(ci);
            }
            if (bookmarkedConf != null) {
                comboRoomsField.setSelectedItem(bookmarkedConf);
            }
        }
    }

    private String getSelectedRoomName() {
        String roomTitle = null;

        if (textRoomsField != null) {
            roomTitle = textRoomsField.getText();
        }
        if (comboRoomsField != null) {
            roomTitle = comboRoomsField.getSelectedItem().toString();
        }

        return roomTitle;
    }

    private BookmarkedConference getSelectedBookmarkedConference() {
        BookmarkedConference bookmarkedConf = null;
        if (comboRoomsField != null) {
            Object bookmarkedConfItem = comboRoomsField.getSelectedItem();
            if (bookmarkedConfItem instanceof ConferenceItem) {
                bookmarkedConf = ((ConferenceItem) bookmarkedConfItem).getBookmarkedConf();
            }
        }
        return bookmarkedConf;
    }

    public void inviteUsersToRoom(final DomainBareJid serviceName, Collection<BookmarkedConference> rooms, String adHocRoomName, Collection<Jid> jids) {
        fillRoomsUI(rooms, adHocRoomName);


        JFrame parent = SparkManager.getChatManager().getChatContainer().getChatFrame();
        if (parent == null || !parent.isVisible()) {
            parent = SparkManager.getMainWindow();
        }

        // Add jids to user list
        if (jids != null) {
            for (Jid jid : jids) {
                invitedUsers.addElement(jid.toString());
            }
        }

        final JOptionPane pane;


        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.invite.to.conference"), Res.getString("message.invite.users.to.conference"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("invite"), Res.getString("cancel")};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        dlg = p.createDialog(parent, Res.getString("title.conference.rooms"));
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(500, 450);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        //dlg.setLocationRelativeTo(parent);
        dlg.setLocationRelativeTo(null);


        PropertyChangeListener changeListener = e -> {
            String value = (String)pane.getValue();
            if (Res.getString("cancel").equals(value)) {
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                dlg.dispose();
            }
            else if (Res.getString("invite").equals(value)) {
                final String roomTitle = getSelectedRoomName();
                final BookmarkedConference selectedBookmarkedConf = getSelectedBookmarkedConference();
                int size = invitedUserList.getModel().getSize();
                UIManager.put("OptionPane.okButtonText", Res.getString("ok"));

                if (size == 0) {
                    JOptionPane.showMessageDialog(dlg, Res.getString("message.specify.users.to.join.conference"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    return;
                }

                if (!ModelUtil.hasLength(roomTitle)) {
                    JOptionPane.showMessageDialog(dlg, Res.getString("message.no.room.to.join.error"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    return;
                }
                EntityBareJid roomName = null;

                // Add all rooms the user is in to list.
                ChatManager chatManager = SparkManager.getChatManager();
                for (ChatRoom chatRoom : chatManager.getChatContainer().getChatRooms()) {
                    if (chatRoom instanceof GroupChatRoom) {
                        GroupChatRoom groupRoom = (GroupChatRoom) chatRoom;
                        if (groupRoom.getBareJid().equals(roomTitle)) {
                            roomName = groupRoom.getMultiUserChat().getRoom();
                            break;
                        }
                    }
                }
                String message = messageField.getText();
                final String messageText = message != null ? message : Res.getString("message.please.join.in.conference");

                if (invitedUsers.getSize() > 0) {
                    invitedUserList.setSelectionInterval(0, invitedUsers.getSize() - 1);
                }


                GroupChatRoom chatRoom;
                try {
                    chatRoom = SparkManager.getChatManager().getGroupChat(roomName);
                }
                catch (ChatNotFoundException e1) {
                    dlg.setVisible(false);
                    final List<EntityBareJid> jidList = new ArrayList<>();
                    Object[] jids1 = invitedUserList.getSelectedValuesList().toArray();
                    for (Object o : jids1) {
                        try {
                            String entityBareJidString = (String) o;
                            EntityBareJid entityBareJid = JidCreate.entityBareFromUnescaped(entityBareJidString);
                            jidList.add(entityBareJid);
                        } catch (NullPointerException | XmppStringprepException ee) {
                            Log.error(ee);
                        }
                    }

                    SwingWorker worker = new SwingWorker() {
                        @Override
						public Object construct() {
                            try {
                                Thread.sleep(15);
                            }
                            catch (InterruptedException e2) {
                                Log.error(e2);
                            }
                            return "ok";
                        }

                        @Override
						public void finished() {
                            try {
                                if (selectedBookmarkedConf == null) {
                                    ConferenceUtils.createPrivateConference(serviceName, messageText, roomTitle,
                                            jidList);
                                } else {
                                    ConferenceUtils.joinConferenceOnSeperateThread(
                                            selectedBookmarkedConf.getName(), selectedBookmarkedConf.getJid(),
                                            selectedBookmarkedConf.getNickname(), selectedBookmarkedConf.getPassword(),
                                            messageText, jidList);
                                }
                            }
                            catch (SmackException | InterruptedException ex) {
                                UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                                JOptionPane.showMessageDialog(pane, "An error occurred.", Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    };

                    worker.start();
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    return;
                }

                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                dlg.dispose();

                Object[] values = invitedUserList.getSelectedValuesList().toArray();
                for (Object o : values) {
                    String jidString = (String) o;
                    try {
                        EntityBareJid jid = JidCreate.entityBareFromUnescaped(jidString);
                        chatRoom.getMultiUserChat().invite(jid, message != null ? message : Res.getString("message.please.join.in.conference"));
                        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
                        chatRoom.getTranscriptWindow().insertNotificationMessage("Invited " + nickname, ChatManager.NOTIFICATION_COLOR);
                    } catch (SmackException.NotConnectedException | XmppStringprepException | InterruptedException e1) {
                        Log.warning("Unable to send stanza to " + jidString, e1);
                    }
                }

            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
    }
}
