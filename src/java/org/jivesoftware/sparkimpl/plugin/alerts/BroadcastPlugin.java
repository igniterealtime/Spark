/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.InputDialog;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * Handles broadcasts from server and allows for roster wide broadcasts.
 */
public class BroadcastPlugin implements Plugin, PacketListener {

    public void initialize() {
        boolean enabled = Enterprise.containsFeature(Enterprise.BROADCAST_FEATURE);
        if (!enabled) {
            return;
        }
        PacketFilter serverFilter = new PacketTypeFilter(Message.class);
        SparkManager.getConnection().addPacketListener(this, serverFilter);

        // Register with action menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName("Actions");
        JMenuItem broadcastMenu = new JMenuItem("Broadcast Message", SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
        ResourceUtils.resButton(broadcastMenu, "&Broadcast Message");
        actionsMenu.add(broadcastMenu);
        broadcastMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                broadcastToRoster();
            }
        });

        // Register with action menu
        JMenuItem startConversationtMenu = new JMenuItem("", SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
        ResourceUtils.resButton(startConversationtMenu, "&Start Chat");
        actionsMenu.add(startConversationtMenu);
        startConversationtMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContactList contactList = SparkManager.getWorkspace().getContactList();
                Collection selectedUsers = contactList.getSelectedUsers();
                String selectedUser = "";
                Iterator selectedUsersIterator = selectedUsers.iterator();
                if (selectedUsersIterator.hasNext()) {
                    ContactItem contactItem = (ContactItem)selectedUsersIterator.next();
                    selectedUser = contactItem.getFullJID();
                }

                String jid = (String)JOptionPane.showInputDialog(SparkManager.getMainWindow(), "Enter Address", "Start Chat", JOptionPane.QUESTION_MESSAGE, null, null, selectedUser);
                if (ModelUtil.hasLength(jid) && ModelUtil.hasLength(StringUtils.parseServer(jid))) {
                    if (ModelUtil.hasLength(jid) && jid.indexOf('@') == -1) {
                        // Append server address
                        jid = jid + "@" + SparkManager.getConnection().getServiceName();
                    }

                    ChatRoom chatRoom = SparkManager.getChatManager().createChatRoom(jid, jid, jid);
                    SparkManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);
                }
            }
        });

        // Add send to selected users.
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object component, JPopupMenu popup) {
                if (component instanceof ContactGroup) {
                    final ContactGroup group = (ContactGroup)component;
                    Action broadcastMessageAction = new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            broadcastToGroup(group);
                        }
                    };

                    broadcastMessageAction.putValue(Action.NAME, "Broadcast message to group");
                    broadcastMessageAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
                    popup.add(broadcastMessageAction);
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        // Add Broadcast to roster
        StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
        JPanel commandPanel = statusBar.getCommandPanel();

        RolloverButton broadcastToRosterButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
        broadcastToRosterButton.setToolTipText("Send a broadcast");
        commandPanel.add(broadcastToRosterButton);
        statusBar.invalidate();
        statusBar.validate();
        statusBar.repaint();

        broadcastToRosterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                broadcastToRoster();
            }
        });
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    public void processPacket(final Packet packet) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final Message message = (Message)packet;
                boolean broadcast = message.getProperty("broadcast") != null;

                if ((broadcast || (message.getType() == Message.Type.NORMAL) || message.getType() == Message.Type.HEADLINE) && message.getBody() != null) {
                    showAlert((Message)packet);
                }
                else {
                    String host = SparkManager.getSessionManager().getServerAddress();
                    String from = packet.getFrom() != null ? packet.getFrom() : "";
                    if (host.equalsIgnoreCase(from) || !ModelUtil.hasLength(from)) {
                        showAlert((Message)packet);
                    }
                }
            }
        });

    }

    /**
     * Show Server Alert.
     *
     * @param message the message to show.
     */
    private void showAlert(Message message) {
        final String body = message.getBody();
        String subject = message.getSubject();

        StringBuffer buf = new StringBuffer();
        if (subject != null) {
            buf.append("Subject: ").append(subject);
            buf.append("\n\n");
        }

        buf.append(body);

        String host = SparkManager.getSessionManager().getServerAddress();
        String from = message.getFrom() != null ? message.getFrom() : "";


        final TranscriptWindow window = new TranscriptWindow();
        window.insertCustomMessage(null, buf.toString());

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(window, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createLineBorder(Color.lightGray));

        if (host.equalsIgnoreCase(from) || message.getFrom() == null) {
            SparkToaster toaster = new SparkToaster();
            toaster.setDisplayTime(30000);
            toaster.setTitle(host);
            toaster.setBorder(BorderFactory.createBevelBorder(0));
            toaster.showToaster(SparkRes.getImageIcon(SparkRes.INFORMATION_IMAGE), buf.toString());
        }
        else {
            VCard vcard = SparkManager.getVCardManager().getVCard(StringUtils.parseBareAddress(message.getFrom()));
            ImageIcon icon = null;
            if (vcard != null && vcard.getAvatar() != null) {
                icon = new ImageIcon(vcard.getAvatar());
                icon = GraphicUtils.scaleImageIcon(icon, 48, 48);
            }
            else if (icon == null || icon.getIconWidth() == -1) {
                icon = SparkRes.getImageIcon(SparkRes.USER_HEADSET_24x24);
            }
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom());
            MessageDialog.showComponent("Broadcast from " + nickname, "", icon, p, SparkManager.getMainWindow(), 400, 400, false);
        }

    }

    /**
     * Broadcasts a message to all in the roster.
     */
    private void broadcastToRoster() {
        InputDialog dialog = new InputDialog();
        final String messageText = dialog.getInput("Broadcast Message", "Enter message to broadcast to your entire roster list.", SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), SparkManager.getMainWindow());
        if (ModelUtil.hasLength(messageText)) {
            ContactList contactList = SparkManager.getWorkspace().getContactList();
            for (ContactGroup contactGroup : contactList.getContactGroups()) {
                Iterator items = contactGroup.getContactItems().iterator();
                for (ContactItem item : contactGroup.getContactItems()) {
                    if (item != null && item.getFullJID() != null) {
                        final Message message = new Message();
                        message.setTo(item.getFullJID());
                        message.setBody(messageText);
                        message.setProperty("broadcast", true);
                        SparkManager.getConnection().sendPacket(message);
                    }
                }

            }
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "The broadcast message has been sent.", "Message Broadcasted", JOptionPane.INFORMATION_MESSAGE);

        }

    }

    /**
     * Broadcasts a message to all selected users.
     *
     * @param group the Contact Group to send the messages to.
     */
    private void broadcastToGroup(ContactGroup group) {
        StringBuffer buf = new StringBuffer();
        InputDialog dialog = new InputDialog();
        final String messageText = dialog.getInput("Broadcast Message", "Enter message to broadcast to " + group.getGroupName(), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), SparkManager.getMainWindow());
        if (ModelUtil.hasLength(messageText)) {
            for(ContactItem item : group.getContactItems()){
                final Message message = new Message();
                message.setTo(item.getFullJID());
                message.setProperty("broadcast", true);
                message.setBody(messageText);
                buf.append(item.getNickname()).append("\n");
                SparkManager.getConnection().sendPacket(message);
            }

            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "The message has been broadcasted to the following users:\n" + buf.toString(), "Message Broadcasted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void uninstall() {
        // Do nothing.
    }


}
