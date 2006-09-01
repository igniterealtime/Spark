/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.JiveTreeNode;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.Table;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.Tree;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * A UI that handles all Group Rooms contained in an XMPP Messenger server.  This handles
 * creation and joining of rooms for group chat discussions as well as the listing
 * of the creation times, number of occupants in a room, and the room name itself.
 */
public class ConferenceRooms extends JPanel implements ActionListener {
    private final RoomList roomsTable;
    private final RolloverButton createButton = new RolloverButton("", SparkRes.getImageIcon(SparkRes.SMALL_USER1_NEW));
    private final RolloverButton joinRoomButton = new RolloverButton("", SparkRes.getImageIcon(SparkRes.DOOR_IMAGE));
    private final RolloverButton refreshButton = new RolloverButton("", SparkRes.getImageIcon(SparkRes.REFRESH_IMAGE));
    private final RolloverButton addRoomButton = new RolloverButton("", SparkRes.getImageIcon(SparkRes.ADD_BOOKMARK_ICON));

    private ChatManager chatManager;

    private JDialog dlg;

    private Tree serviceTree;
    private String serviceName;

    private boolean partialDiscovery = false;

    /**
     * Creates a new instance of ConferenceRooms.
     *
     * @param serviceTree the service tree.
     * @param serviceName the name of the conference service.
     *                    //TODO This needs to be refactored.
     */
    public ConferenceRooms(final Tree serviceTree, final String serviceName) {

        this.setLayout(new BorderLayout());

        this.serviceTree = serviceTree;
        this.serviceName = serviceName;

        // Add Toolbar
        final JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(joinRoomButton);
        toolbar.add(addRoomButton);
        toolbar.add(createButton);
        toolbar.add(refreshButton);

        this.add(toolbar, BorderLayout.NORTH);
        createButton.addActionListener(this);
        joinRoomButton.addActionListener(this);
        refreshButton.addActionListener(this);

        ResourceUtils.resButton(createButton, Res.getString("button.create.room"));
        ResourceUtils.resButton(joinRoomButton, Res.getString("button.join.room"));
        ResourceUtils.resButton(refreshButton, Res.getString("button.refresh"));
        ResourceUtils.resButton(addRoomButton, Res.getString("button.bookmark.room"));

        refreshButton.setToolTipText(Res.getString("message.update.room.list"));
        joinRoomButton.setToolTipText(Res.getString("message.join.conference.room"));
        createButton.setToolTipText(Res.getString("message.create.or.join.room"));

        // Add Group Chat Table
        roomsTable = new RoomList();

        final JScrollPane pane = new JScrollPane(roomsTable);
        pane.setBackground(Color.white);
        pane.setForeground(Color.white);
        this.setBackground(Color.white);
        this.setForeground(Color.white);
        pane.getViewport().setBackground(Color.white);
        this.add(pane, BorderLayout.CENTER);

        chatManager = SparkManager.getChatManager();

        joinRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                joinSelectedRoom();
            }
        });

        addRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                bookmarkRoom(serviceName, serviceTree);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                refreshRoomList(serviceName);
            }
        });

        joinRoomButton.setEnabled(false);
        addRoomButton.setEnabled(false);

        addTableListener();
    }

    private void refreshRoomList(final String serviceName) {
        roomsTable.clearTable();
        SwingWorker worker = new SwingWorker() {
            Collection result;

            public Object construct() {
                result = getRoomsAndInfo(serviceName);
                return result;
            }

            public void finished() {
                try {


                    Iterator rooms = result.iterator();
                    while (rooms.hasNext()) {
                        RoomObject obj = (RoomObject)rooms.next();
                        addRoomToTable(obj.getRoomJID(), obj.getRoomName(), obj.getNumberOfOccupants());
                    }
                }
                catch (Exception e) {
                    Log.error("Unable to retrieve room list and info.", e);
                }
            }
        };

        worker.start();


    }

    private Collection getRoomsAndInfo(final String serviceName) {
        List roomList = new ArrayList();
        boolean stillSearchForOccupants = true;
        try {
            Collection result = getRoomList(serviceName);
            try {
                Iterator rooms = result.iterator();
                while (rooms.hasNext()) {
                    HostedRoom hostedRoom = (HostedRoom)rooms.next();
                    String roomName = hostedRoom.getName();
                    String roomJID = hostedRoom.getJid();
                    int numberOfOccupants = -1;
                    if (stillSearchForOccupants) {
                        RoomInfo roomInfo = null;
                        try {
                            roomInfo = MultiUserChat.getRoomInfo(SparkManager.getConnection(), roomJID);
                        }
                        catch (Exception e) {
                        }

                        if (roomInfo != null) {
                            numberOfOccupants = roomInfo.getOccupantsCount();
                            if (numberOfOccupants == -1) {
                                stillSearchForOccupants = false;
                            }
                        }
                        else {
                            stillSearchForOccupants = false;
                        }
                    }

                    RoomObject obj = new RoomObject();
                    obj.setRoomJID(roomJID);
                    obj.setRoomName(roomName);
                    obj.setNumberOfOccupants(numberOfOccupants);
                    roomList.add(obj);
                }
            }
            catch (Exception e) {
                Log.error("Error setting up GroupChatTable", e);
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
        return roomList;
    }

    private void bookmarkRoom(String serviceName, Tree serviceTree) {
        int selectedRow = roomsTable.getSelectedRow();
        if (-1 == selectedRow) {
            JOptionPane.showMessageDialog(dlg, Res.getString("message.select.add.room.to.add"), Res.getString("title.group.chat"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final String roomJID = (String)roomsTable.getValueAt(selectedRow, 2) + "@" + serviceName;
        final String roomName = (String)roomsTable.getValueAt(selectedRow, 1);

        // Check to see what type of room this is.
        try {
            final RoomInfo roomInfo = MultiUserChat.getRoomInfo(SparkManager.getConnection(), roomJID);
            if (!roomInfo.isPersistent()) {
                JOptionPane.showMessageDialog(dlg, Res.getString("message.bookmark.temporary.room.error"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        catch (XMPPException e) {
            return;
        }

        JiveTreeNode rootNode = (JiveTreeNode)serviceTree.getModel().getRoot();

        TreePath rootPath = serviceTree.findByName(serviceTree, new String[]{rootNode.toString(), serviceName});


        boolean isBookmarked = isBookmarked(roomJID);


        if (!isBookmarked) {
            JiveTreeNode node = (JiveTreeNode)serviceTree.getLastSelectedPathComponent();
            if (node == null) {
                TreePath path = serviceTree.findByName(serviceTree, new String[]{rootNode.toString(), Conferences.getDefaultServiceName()});
                node = (JiveTreeNode)path.getLastPathComponent();
            }
            JiveTreeNode roomNode = new JiveTreeNode(roomName, false, SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
            roomNode.setAssociatedObject(roomJID);
            node.add(roomNode);
            final DefaultTreeModel model = (DefaultTreeModel)serviceTree.getModel();
            model.nodeStructureChanged(node);
            serviceTree.expandPath(rootPath);
            roomsTable.setValueAt(new JLabel(SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON)), selectedRow, 0);
            addBookmarkUI(false);
        }
        else {
            // Remove bookmark
            TreePath path = serviceTree.findByName(serviceTree, new String[]{rootNode.toString(), serviceName, roomName});
            JiveTreeNode node = (JiveTreeNode)path.getLastPathComponent();
            final DefaultTreeModel model = (DefaultTreeModel)serviceTree.getModel();
            model.removeNodeFromParent(node);
            roomsTable.setValueAt(new JLabel(SparkRes.getImageIcon(SparkRes.BLANK_IMAGE)), selectedRow, 0);
            addBookmarkUI(true);
        }
    }

    private void joinSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (-1 == selectedRow) {
            JOptionPane.showMessageDialog(dlg, Res.getString("message.select.room.to.join"), Res.getString("title.group.chat"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        enterRoom();
    }

    private void addTableListener() {
        roomsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;

                int selectedRow = roomsTable.getSelectedRow();
                if (selectedRow != -1) {
                    joinRoomButton.setEnabled(true);

                    String roomName = (String)roomsTable.getValueAt(selectedRow, 1);
                    String roomJID = (String)roomsTable.getValueAt(selectedRow, 2) + "@" + serviceName;
                    addRoomButton.setEnabled(true);
                    if (isBookmarked(roomJID)) {
                        addBookmarkUI(false);
                    }
                    else {
                        addBookmarkUI(true);
                    }
                }
                else {
                    joinRoomButton.setEnabled(false);
                    addRoomButton.setEnabled(false);
                    addBookmarkUI(true);
                }
            }
        });
    }

    /**
     * Displays the ConferenceRoomBrowser.
     */
    public void invoke() {
        SwingWorker worker = new SwingWorker() {
            Collection rooms;

            public Object construct() {
                try {
                    rooms = getRoomList(serviceName);
                }
                catch (Exception e) {
                    Log.error("Unable to retrieve list of rooms.", e);
                }

                return "OK";
            }

            public void finished() {
                if (rooms == null) {
                    JOptionPane.showMessageDialog(serviceTree, Res.getString("message.conference.info.error"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    if (dlg != null) {
                        dlg.dispose();
                    }
                }
                try {
                    Iterator iter = rooms.iterator();
                    while (iter.hasNext()) {
                        HostedRoom hostedRoom = (HostedRoom)iter.next();
                        String roomName = hostedRoom.getName();
                        String roomJID = hostedRoom.getJid();

                        int numberOfOccupants = -1;

                        // Need to handle the case where the room info does not contain the number of occupants. If that is the case,
                        // we should not continue to request this info from the service.
                        if (!partialDiscovery) {
                            RoomInfo roomInfo = null;
                            try {
                                roomInfo = MultiUserChat.getRoomInfo(SparkManager.getConnection(), roomJID);
                            }
                            catch (Exception e) {
                            }

                            if (roomInfo != null) {
                                numberOfOccupants = roomInfo.getOccupantsCount();
                            }
                            if (roomInfo == null || numberOfOccupants == -1) {
                                partialDiscovery = true;
                            }
                        }
                        addRoomToTable(roomJID, roomName, numberOfOccupants);
                    }
                }
                catch (Exception e) {
                    Log.error("Error setting up GroupChatTable", e);
                }
            }
        };

        worker.start();
        // Find Initial Rooms


        final JOptionPane pane;


        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.create.or.bookmark.room"), Res.getString("message.add.favorite.room"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("close")};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        dlg = p.createDialog(SparkManager.getMainWindow(), Res.getString("title.browse.room.service ", serviceName));
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(500, 400);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String)pane.getValue();
                if (Res.getString("close").equals(value)) {
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
                else if (Res.getString("close").equals(value)) {
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    dlg.dispose();
                }
            }
        });

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
    }

    private final class RoomList extends Table {
        public RoomList() {
            super(new String[]{" ", Res.getString("title.name"), Res.getString("title.address"), Res.getString("title.occupants")});
            getColumnModel().setColumnMargin(0);
            getColumnModel().getColumn(0).setMaxWidth(30);
            getColumnModel().getColumn(3).setMaxWidth(80);

            setSelectionBackground(Table.SELECTION_COLOR);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setRowSelectionAllowed(true);
//            setSortable(true);

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        enterRoom();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    checkPopup(e);
                }

                public void mousePressed(MouseEvent e) {
                    checkPopup(e);
                }
            });

        }

        // Handle image rendering correctly
        public TableCellRenderer getCellRenderer(int row, int column) {
            Object o = getValueAt(row, column);
            if (o != null) {
                if (o instanceof JLabel) {
                    return new JLabelRenderer(false);
                }
            }

            if (column == 3) {
                return new CenterRenderer();
            }

            return super.getCellRenderer(row, column);
        }

        private void checkPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                final JPopupMenu popupMenu = new JPopupMenu();

                Action roomInfoAction = new AbstractAction() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        int selectedRow = roomsTable.getSelectedRow();
                        if (selectedRow != -1) {
                            String roomJID = (String)roomsTable.getValueAt(selectedRow, 2) + "@" + serviceName;
                            RoomBrowser roomBrowser = new RoomBrowser();
                            roomBrowser.displayRoomInformation(roomJID);
                        }
                    }
                };

                roomInfoAction.putValue(Action.NAME, Res.getString("menuitem.view.room.info"));
                roomInfoAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DATA_FIND_IMAGE));

                popupMenu.add(roomInfoAction);
                popupMenu.show(roomsTable, e.getX(), e.getY());
            }
        }

    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            createRoom();
        }
    }

    private void enterRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (-1 == selectedRow) {
            JOptionPane.showMessageDialog(dlg, Res.getString("message.select.room.to.enter"), Res.getString("title.group.chat"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        final String roomJID = (String)roomsTable.getValueAt(selectedRow, 2) + "@" + serviceName;
        final String roomDescription = (String)roomsTable.getValueAt(selectedRow, 1);

        try {
            chatManager.getChatContainer().getChatRoom(roomJID);
        }
        catch (ChatRoomNotFoundException e1) {
            ConferenceUtils.autoJoinConferenceRoom(roomDescription, roomJID, null);
        }
    }

    /**
     * Returns a Collection of all rooms in the specified Conference Service.
     *
     * @param serviceName the name of the conference service.
     * @return a Collection of all rooms in the Conference service.
     * @throws Exception
     */
    private static Collection getRoomList(String serviceName) throws Exception {
        return MultiUserChat.getHostedRooms(SparkManager.getConnection(), serviceName);
    }


    /**
     * Create a new room based on room table selection.
     */
    private void createRoom() {
        ConferenceCreator mucRoomDialog = new ConferenceCreator();
        final MultiUserChat groupChat = mucRoomDialog.createGroupChat(SparkManager.getMainWindow(), serviceName);
        LocalPreferences pref = SettingsManager.getLocalPreferences();

        if (null != groupChat) {

            // Join Room
            try {
                GroupChatRoom room = new GroupChatRoom(groupChat);

                chatManager.getChatContainer().addChatRoom(room);
                chatManager.getChatContainer().activateChatRoom(room);
                groupChat.create(pref.getNickname());

                // Send Form
                Form form = groupChat.getConfigurationForm().createAnswerForm();
                if (mucRoomDialog.isPasswordProtected()) {
                    String password = mucRoomDialog.getPassword();
                    form.setAnswer("muc#roomconfig_passwordprotectedroom", true);
                    form.setAnswer("muc#roomconfig_roomsecret", password);
                }
                form.setAnswer("muc#roomconfig_roomname", mucRoomDialog.getRoomName());

                if (mucRoomDialog.isPermanent()) {
                    form.setAnswer("muc#roomconfig_persistentroom", true);
                }

                List owners = new ArrayList();
                owners.add(SparkManager.getSessionManager().getBareAddress());
                form.setAnswer("muc#roomconfig_roomowners", owners);

                // new DataFormDialog(groupChat, form);
                groupChat.sendConfigurationForm(form);


            }
            catch (XMPPException e1) {
                Log.error("Error creating new room.", e1);
            }

            addRoomToTable(groupChat.getRoom(), StringUtils.parseName(groupChat.getRoom()), 1);
        }
    }

    /**
     * Adds a room to the room table.
     *
     * @param jid               the jid of the conference room.
     * @param roomName          the name of the conference room.
     * @param numberOfOccupants the number of occupants in the conference room. If -1 is specified,
     *                          the the occupant count will show as n/a.
     */
    private void addRoomToTable(String jid, String roomName, int numberOfOccupants) {
        JLabel bookmarkedLabel = new JLabel();
        if (isBookmarked(jid)) {
            bookmarkedLabel.setIcon(SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
        }

        String occupants = Integer.toString(numberOfOccupants);
        if (numberOfOccupants == -1) {
            occupants = "n/a";
        }

        final Object[] insertRoom = new Object[]{bookmarkedLabel, roomName, StringUtils.parseName(jid), occupants};
        roomsTable.getTableModel().addRow(insertRoom);
    }

    /**
     * Returns true if the room specified is bookmarked.
     *
     * @param roomJID the jid of the room to check.
     * @return true if the room is bookmarked.
     */
    private boolean isBookmarked(String roomJID) {
        JiveTreeNode rootNode = (JiveTreeNode)serviceTree.getModel().getRoot();
        TreePath path = serviceTree.findByName(serviceTree, new String[]{rootNode.toString(), serviceName});

        JiveTreeNode serviceNode = (JiveTreeNode)path.getLastPathComponent();

        // Otherwise, traverse through the path.
        int childCount = serviceNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            JiveTreeNode node = (JiveTreeNode)serviceNode.getChildAt(i);

            // If the jid (nodeObject) of the node equals the roomJID specified, then return true.
            if (node.getAssociatedObject() != null && node.getAssociatedObject().equals(roomJID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Toggles the bookmark room button depending on it's state.
     *
     * @param addBookmark true if the button should display itself as bookmarkable :)
     */
    private void addBookmarkUI(boolean addBookmark) {
        if (!addBookmark) {
            addRoomButton.setText(Res.getString("button.remove.bookmark"));
            addRoomButton.setIcon(SparkRes.getImageIcon(SparkRes.DELETE_BOOKMARK_ICON));
        }
        else {
            addRoomButton.setText(Res.getString("button.bookmark.room"));
            addRoomButton.setIcon(SparkRes.getImageIcon(SparkRes.ADD_BOOKMARK_ICON));
        }
    }

    /*
    **  Center the text
    */
    static class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() {
            setHorizontalAlignment(CENTER);
        }

        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }

    private class RoomObject {
        private String roomName;
        private String roomJID;

        int numberOfOccupants;

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRoomJID() {
            return roomJID;
        }

        public void setRoomJID(String roomJID) {
            this.roomJID = roomJID;
        }

        public int getNumberOfOccupants() {
            return numberOfOccupants;
        }

        public void setNumberOfOccupants(int numberOfOccupants) {
            this.numberOfOccupants = numberOfOccupants;
        }
    }


}

