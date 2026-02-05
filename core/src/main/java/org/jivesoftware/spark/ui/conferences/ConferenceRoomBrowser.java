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

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ImageCombiner;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;

import static java.awt.BorderLayout.*;
import static org.jivesoftware.smackx.muc.MucConfigFormManager.*;

/**
 * A UI that handles all Group Rooms contained in an XMPP Messenger server. This
 * handles creation and joining of rooms for group chat discussions as well as
 * the listing of the creation times, number of occupants in a room, and the
 * room name itself.
 */
public class ConferenceRoomBrowser extends JPanel implements ActionListener, ComponentListener {
    private final RoomList roomsTable;
    private final RolloverButton createButton = new RolloverButton("",
        SparkRes.getImageIcon(SparkRes.SMALL_USER1_NEW));
    private final RolloverButton joinRoomButton = new RolloverButton("",
        SparkRes.getImageIcon(SparkRes.DOOR_IMAGE));
    private final RolloverButton refreshButton = new RolloverButton("",
        SparkRes.getImageIcon(SparkRes.REFRESH_IMAGE));
    private final RolloverButton addRoomButton = new RolloverButton("",
        SparkRes.getImageIcon(SparkRes.ADD_BOOKMARK_ICON));

    private final RolloverButton showHiddenButtons = new RolloverButton(
        SparkRes.getImageIcon(SparkRes.PANE_UP_ARROW_IMAGE));

    private final JMenuItem joinRoomItem;
    private final JMenuItem addRoomItem;
    private final JMenuItem createItem;
    private final JMenuItem refreshItem;

    private final ChatManager chatManager;

    private JDialog dlg;

    private final BookmarksUI conferences;
    private final DomainBareJid serviceName;

    private int allButtonWidth;
    private int threeButtonWidth;
    private int twoButtonWidth;
    private int oneButtonWidth;

    private final JPopupMenu popup;

    final TableRowSorter<TableModel> sorter;
    private final Map<EntityBareJid, RoomInfo> roomInfos = new HashMap<>();
    private final LocalPreferences pref = SettingsManager.getLocalPreferences();

    /**
     * Creates a new instance of ConferenceRooms.
     *
     * @param conferences the conference ui.
     * @param serviceName the name of the conference service.
     */
    public ConferenceRoomBrowser(BookmarksUI conferences, DomainBareJid serviceName) {
        this.setLayout(new BorderLayout());

        this.conferences = conferences;
        this.serviceName = serviceName;

        popup = new JPopupMenu();

        joinRoomItem = new JMenuItem(Res.getString("menuitem.join.room"));
        addRoomItem = new JMenuItem(Res.getString("menuitem.bookmark.room"));
        createItem = new JMenuItem(Res.getString("menuitem.create.room"));
        refreshItem = new JMenuItem(Res.getString("menuitem.refresh"));

        joinRoomItem.setIcon(SparkRes.getImageIcon(SparkRes.DOOR_IMAGE));
        addRoomItem.setIcon(SparkRes.getImageIcon(SparkRes.ADD_BOOKMARK_ICON));
        createItem.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_USER1_NEW));
        refreshItem.setIcon(SparkRes.getImageIcon(SparkRes.REFRESH_IMAGE));

        popup.add(joinRoomItem);
        popup.add(addRoomItem);
        popup.add(createItem);
        popup.add(refreshItem);

        // Add Toolbar
        final JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pane_hiddenButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.add(joinRoomButton);
        toolbar.add(addRoomButton);
        toolbar.add(createButton);
        toolbar.add(refreshButton);
        pane_hiddenButtons.add(showHiddenButtons);

        mainPanel.add(toolbar, WEST);
        mainPanel.add(pane_hiddenButtons, EAST);
        this.add(mainPanel, NORTH);

        JLabel labelFilter = new JLabel(Res.getString("label.search"));
        JTextField txtFilter = new JTextField(20);
        txtFilter.setMinimumSize(new Dimension(50, 20));

        //add fields for filter
        final JPanel filterPanel = new JPanel(new BorderLayout());
        JPanel toolbarFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbarFilter.add(labelFilter);
        toolbarFilter.add(txtFilter);

        filterPanel.add(toolbarFilter);
        this.add(filterPanel, SOUTH);

        createButton.addActionListener(this);
        createItem.addActionListener(this);
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
        roomsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    enterRoom();
                }
            }
        });

        //build model for roomsTable, ignoring the 1st column
        sorter = new TableRowSorter<>(roomsTable.getModel());
        sorter.setComparator(3, Comparator.comparing((String o) -> !o.isEmpty() ? Long.parseLong(o) : 0));
        roomsTable.setRowSorter(sorter);
        roomsTable.setComponentPopupMenu(roomEntryPopupMenu());

        final JScrollPane pane = new JScrollPane(roomsTable);
        pane.setBackground(Color.white);
        pane.setForeground(Color.white);
        this.setBackground(Color.white);
        this.setForeground(Color.white);
        pane.getViewport().setBackground(Color.white);
        this.add(pane, CENTER);

        chatManager = SparkManager.getChatManager();

        txtFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();
                List<RowFilter<Object, Object>> filters = new ArrayList<>(3);
                filters.add(RowFilter.regexFilter(text, 1));
                filters.add(RowFilter.regexFilter(text, 2));
                filters.add(RowFilter.regexFilter(text, 3));
                RowFilter<Object, Object> af = RowFilter.orFilter(filters);
                sorter.setRowFilter(af);
            }
        });
        joinRoomButton.addActionListener(actionEvent -> joinSelectedRoom());
        addRoomButton.addActionListener(actionEvent -> bookmarkRoom(serviceName));
        refreshButton.addActionListener(actionEvent -> refreshRoomList(serviceName));
        joinRoomItem.addActionListener(actionEvent -> joinSelectedRoom());
        addRoomItem.addActionListener(actionEvent -> bookmarkRoom(serviceName));
        refreshItem.addActionListener(actionEvent -> refreshRoomList(serviceName));
        showHiddenButtons.addActionListener(actionEvent -> popup.show(showHiddenButtons, 0, showHiddenButtons.getHeight()));

        joinRoomButton.setEnabled(false);
        addRoomButton.setEnabled(false);
        joinRoomItem.setEnabled(false);
        addRoomItem.setEnabled(false);

        addTableListener();
    }

    private RoomInfo selectedRoomInfo() {
        final int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        String roomJIDString = roomsTable.getValueAt(selectedRow, 2) + "@" + serviceName;
        EntityBareJid roomJID = JidCreate.entityBareFromOrNull(roomJIDString);
        RoomInfo roomInfo = roomInfos.get(roomJID);
        return roomInfo;
    }

    private JPopupMenu roomEntryPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem itemRoomInfo = new JMenuItem(Res.getString("menuitem.view.room.info"));
        itemRoomInfo.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_DATA_FIND_IMAGE));
        itemRoomInfo.addActionListener(e -> {
            RoomInfo roomInfo = selectedRoomInfo();
            if (roomInfo == null) {
                return;
            }
            RoomBrowser roomBrowser = new RoomBrowser();
            roomBrowser.displayRoomInformation(roomInfo.getRoom());
        });
        popupMenu.add(itemRoomInfo);

        JMenuItem itemCopyUri = new JMenuItem(Res.getString("button.copy.to.clipboard"));
        itemCopyUri.setIcon(SparkRes.getImageIcon(SparkRes.COPY_16x16));
        itemCopyUri.addActionListener(e -> {
            RoomInfo roomInfo = selectedRoomInfo();
            if (roomInfo == null) {
                return;
            }
            String roomJIDString = roomInfo.getRoom().toString();
            SparkManager.setClipboard("xmpp:" + roomJIDString + "?join");
        });
        popupMenu.add(itemCopyUri);

        // Select the row where the use made their right-clicked
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Point point = SwingUtilities.convertPoint(popupMenu, new Point(0, 0), roomsTable);
                    int rowAtPoint = roomsTable.rowAtPoint(point);
                    if (rowAtPoint <= -1) {
                        return;
                    }
                    roomsTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        return popupMenu;
    }

    private void startLoadingImg() {
        SwingWorker startLoading = new SwingWorker() {

            @Override
            public Object construct() {
                return null;
            }

            @Override
            public void finished() {
                refreshButton.setIcon(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));
                refreshButton.validate();
                refreshButton.repaint();
                refreshItem.setIcon(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));
                refreshItem.validate();
                refreshItem.repaint();
            }
        };
        startLoading.start();
    }

    private void stopLoadingImg() {
        SwingWorker stopLoading = new SwingWorker() {

            @Override
            public Object construct() {
                return null;
            }

            @Override
            public void finished() {
                refreshButton.setIcon(SparkRes.getImageIcon(SparkRes.REFRESH_IMAGE));
                refreshButton.validate();
                refreshButton.repaint();
                refreshItem.setIcon(SparkRes.getImageIcon(SparkRes.REFRESH_IMAGE));
                refreshItem.validate();
                refreshItem.repaint();
            }
        };
        stopLoading.start();
    }

    private void clearTable() {
        SwingWorker clearTable = new SwingWorker() {

            @Override
            public Object construct() {
                return null;
            }

            @Override
            public void finished() {
                roomsTable.clearTable();
            }
        };
        clearTable.start();
    }

    private void refreshRoomList(final DomainBareJid serviceName) {
        TimerTask refreshTask = new TimerTask() {

            @Override
            public void run() {
                roomInfos.clear();
                clearTable();
                startLoadingImg();
                String errorMsg = null;
                try {
                    MultiUserChatManager mucManager = SparkManager.getMucManager();
                    Map<EntityBareJid, HostedRoom> rooms = mucManager.getRoomsHostedBy(serviceName);
                    for (Map.Entry<EntityBareJid, HostedRoom> entry : rooms.entrySet()) {
                        HostedRoom room = entry.getValue();
                        try {
                            RoomInfo roomInfo = mucManager.getRoomInfo(room.getJid());
                            addRoomToTable(roomInfo);
                        } catch (Exception e) {
                            Log.warning("Unable to get room info " + room.getName() + ": " + e.getMessage());
                        }
                    }
                } catch (XMPPException.XMPPErrorException e) {
                    StanzaError.Condition condition = e.getStanzaError().getCondition();
                    if (condition == StanzaError.Condition.feature_not_implemented || condition == StanzaError.Condition.service_unavailable) {
                        errorMsg = Res.getString("message.conference.rooms.unsupported");
                    } else {
                        Log.error("Unable to retrieve list of rooms from " + serviceName + ": " + e);
                        errorMsg = e.getMessage();
                    }
                } catch (Exception e) {
                    Log.error("Unable to retrieve list of rooms from " + serviceName, e);
                    errorMsg = e.getMessage();
                } finally {
                    stopLoadingImg();
                }
                // If there was an error, show it to a user
                if (errorMsg != null) {
                    UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                    JOptionPane.showMessageDialog(conferences,
                        Res.getString("message.conference.info.error") + "\n" + errorMsg,
                        Res.getString("title.error"),
                        JOptionPane.ERROR_MESSAGE);
                    if (dlg != null) {
                        dlg.dispose();
                    }
                    return;
                }
                roomsTable.setSortable(true);
            }
        };
        TaskEngine.getInstance().submit(refreshTask);
    }

    private void bookmarkRoom(DomainBareJid serviceName) {
        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
        RoomInfo roomInfo = selectedRoomInfo();
        if (roomInfo == null) {
            JOptionPane.showMessageDialog(dlg, Res.getString("message.select.add.room.to.add"), Res.getString("title.group.chat"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Check to see what type of room this is.
        if (!roomInfo.isPersistent()) {
            JOptionPane.showMessageDialog(dlg, Res.getString("message.bookmark.temporary.room.error"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isBookmarked = isBookmarked(roomInfo.getRoom());
        conferences.addOrRemoveNode(serviceName, isBookmarked, roomInfo.getName(), roomInfo.getRoom());
        int selectedRow = roomsTable.getSelectedRow();
        ImageIcon bookmarkIcon = isBookmarked ? SparkRes.getImageIcon(SparkRes.BLANK_IMAGE) : SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON);
        roomsTable.getTableModel().setValueAt(new JLabel(bookmarkIcon), selectedRow, 0);
        addBookmarkUI(!isBookmarked);
    }


    private void joinSelectedRoom() {
        RoomInfo roomInfo = selectedRoomInfo();
        if (roomInfo == null) {
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(dlg,
                Res.getString("message.select.room.to.join"),
                Res.getString("title.group.chat"),
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        enterRoom();
    }

    private void addTableListener() {
        roomsTable.getSelectionModel().addListSelectionListener(
            e -> {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                RoomInfo roomInfo = selectedRoomInfo();
                if (roomInfo != null) {
                    joinRoomButton.setEnabled(true);
                    joinRoomItem.setEnabled(true);
                    addRoomButton.setEnabled(true);
                    addRoomItem.setEnabled(true);
                    addBookmarkUI(!isBookmarked(roomInfo.getRoom()));
                } else {
                    joinRoomButton.setEnabled(false);
                    addRoomButton.setEnabled(false);
                    joinRoomItem.setEnabled(false);
                    addRoomItem.setEnabled(false);
                    addBookmarkUI(true);
                }
            });
    }

    /**
     * Displays the ConferenceRoomBrowser.
     */
    public void invoke() {
        // Create the title panel for this dialog
        TitlePanel titlePanel = new TitlePanel(
            Res.getString("title.create.or.bookmark.room"),
            Res.getString("message.add.favorite.room"),
            SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("close")};
        final JOptionPane pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
        mainPanel.add(pane, CENTER);
        final JOptionPane p = new JOptionPane();

        dlg = p.createDialog(SparkManager.getMainWindow(),
            Res.getString("title.browse.room.service", serviceName));
        dlg.setModal(false);
        dlg.addComponentListener(this);

        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String) pane.getValue();
                if (Res.getString("close").equals(value)) {
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                } else if (Res.getString("close").equals(value)) {
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    dlg.dispose();
                }
            }
        });

        // will need that when the window is smaller than the buttons width...
        setButtonsWidth();

        showHiddenButtons.setVisible(false);
        dlg.pack();

        final Rectangle bounds = LayoutSettingsManager.getLayoutSettings().getConferenceRoomBrowserBounds();
        if (bounds == null || bounds.width <= 0 || bounds.height <= 0) {
            // Use default settings.
            dlg.setSize(700, 400);
        } else {
            dlg.setBounds(bounds);
        }

        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LayoutSettingsManager.getLayoutSettings().setConferenceRoomBrowserBounds(dlg.getBounds());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                LayoutSettingsManager.getLayoutSettings().setConferenceRoomBrowserBounds(dlg.getBounds());
            }
        });

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
        refreshRoomList(serviceName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton || e.getSource() == createItem) {
            createRoom();
        }
    }

    private void enterRoom() {
        RoomInfo roomInfo = selectedRoomInfo();
        if (roomInfo == null) {
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(dlg,
                Res.getString("message.select.room.to.enter"),
                Res.getString("title.group.chat"),
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            chatManager.getChatContainer().getChatRoom(roomInfo.getRoom());
        } catch (ChatRoomNotFoundException e1) {
            ConferenceUtils.joinConferenceOnSeparateThread(roomInfo.getName(),
                roomInfo.getRoom(), null, null);
        }
    }

    /**
     * Create a new room based on room table selection.
     */
    private void createRoom() {
        RoomCreationDialog mucRoomDialog = new RoomCreationDialog();
        final MultiUserChat groupChat = mucRoomDialog.createGroupChat(SparkManager.getMainWindow(), serviceName);
        if (groupChat == null) {
            return;
        }
        // Join Room
        try {
            GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(groupChat);
            Resourcepart nickname = pref.getNickname();
            groupChat.create(nickname);
            chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);

            // Send Form
            FillableForm form = groupChat.getConfigurationForm().getFillableForm();
            if (mucRoomDialog.isPasswordProtected()) {
                String password = mucRoomDialog.getPassword();
                room.setPassword(password);
                if (form.hasField(MUC_ROOMCONFIG_PASSWORDPROTECTEDROOM)) {
                    form.setAnswer(MUC_ROOMCONFIG_PASSWORDPROTECTEDROOM, true);
                }
                form.setAnswer(MUC_ROOMCONFIG_ROOMSECRET, password);
            }
            form.setAnswer(MUC_ROOMCONFIG_ROOMNAME, mucRoomDialog.getRoomName());
            form.setAnswer("muc#roomconfig_roomdesc", mucRoomDialog.getRoomTopic());

            if (mucRoomDialog.isPublicRoom()) {
                if (form.hasField(MUC_ROOMCONFIG_PUBLICLYSEARCHABLEROOM)) {
                    form.setAnswer(MUC_ROOMCONFIG_PUBLICLYSEARCHABLEROOM, true);
                }
            }
            if (mucRoomDialog.isPermanent()) {
                form.setAnswer("muc#roomconfig_persistentroom", true);
            }
            groupChat.sendConfigurationForm(form);
            MultiUserChatManager mucManager = SparkManager.getMucManager();
            RoomInfo roomInfo = mucManager.getRoomInfo(groupChat.getRoom());
            addRoomToTable(roomInfo);
        } catch (XMPPException | SmackException | InterruptedException e1) {
            Log.error("Error creating new room.", e1);
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(this,
                Res.getString("message.room.creation.error"),
                Res.getString("title.error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a room to the room table.
     */
    private void addRoomToTable(RoomInfo room) {
        roomInfos.put(room.getRoom(), room);
        SwingWorker addRoomThread = new SwingWorker() {

            @Override
            public Object construct() {
                JLabel iconLabel = new JLabel();
                iconLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
                boolean isBookmark = false;
                boolean isPassword = false;

                ImageIcon bookmarkIcon = SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON);
                ImageIcon passwordIcon = SparkRes.getImageIcon(SparkRes.LOCK_16x16);

                if (isBookmarked(room.getRoom())) {
                    isBookmark = true;
                    iconLabel.setIcon(SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
                }
                if (room.isPasswordProtected() || room.isMembersOnly()) {
                    isPassword = true;
                }

                // Set icon based on bookmark and password status
                if (isBookmark && isPassword) {
                    try {
                        Image img = ImageCombiner.combine(bookmarkIcon, passwordIcon);
                        if (img != null) {
                            iconLabel.setIcon(new ImageIcon(img));
                        }
                    } catch (Exception e) {
                        Log.warning("Unable to set icon for bookmarked & password-protected room " + room.getRoom(), e);
                    }
                } else if (isBookmark) {
                    iconLabel.setIcon(bookmarkIcon);
                } else if (isPassword) {
                    try {
                        if (passwordIcon != null) {
                            Image img = ImageCombiner.returnTransparentImage(
                                passwordIcon.getIconWidth(), passwordIcon.getIconHeight());
                            Image combined = ImageCombiner.combine(new ImageIcon(img), passwordIcon);
                            if (combined != null) {
                                iconLabel.setIcon(new ImageIcon(combined));
                            }
                        }
                    } catch (Exception e) {
                        Log.warning("Unable to set icon for password-protected room " + room.getRoom(), e);
                    }
                }

                String occupants = room.getOccupantsCount() != -1 ? String.valueOf(room.getOccupantsCount()) : "";
                String roomAddress = room.getRoom().getLocalpart().toString();
                String roomTitle = room.getName() != null ? room.getName() : roomAddress;
                return new Object[]{iconLabel, roomTitle, roomAddress, occupants, room.getLang(), room.getDescription()};
            }

            @Override
            public void finished() {
                Object[] insertRoom = (Object[]) get();
                roomsTable.getTableModel().addRow(insertRoom);
            }
        };
        addRoomThread.start();
    }

    /**
     * Returns true if the room specified is bookmarked.
     *
     * @param roomJID the jid of the room to check.
     * @return true if the room is bookmarked.
     */
    private boolean isBookmarked(EntityBareJid roomJID) {
        for (BookmarkedConference bk : conferences.getBookmarks()) {
            if (roomJID.equals(bk.getJid())) {
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
        } else {
            ResourceUtils.resButton(addRoomButton, Res.getString("button.bookmark.room"));
            addRoomButton.setIcon(SparkRes.getImageIcon(SparkRes.ADD_BOOKMARK_ICON));
        }
    }

    /**
     * Center the text
     */
    static class CenterRenderer extends DefaultTableCellRenderer {

        public CenterRenderer() {
            setHorizontalAlignment(CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus, int row,
                                                       int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
            return this;
        }
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (this.getWidth() <= (oneButtonWidth + 19)) {
            joinRoomButton.setVisible(false);
            addRoomButton.setVisible(false);
            createButton.setVisible(false);
            refreshButton.setVisible(false);

            joinRoomItem.setVisible(true);
            addRoomItem.setVisible(true);
            createItem.setVisible(true);
            refreshItem.setVisible(true);

            showHiddenButtons.setVisible(true);
        } else if (this.getWidth() <= (twoButtonWidth + 19)) {
            joinRoomButton.setVisible(true);
            addRoomButton.setVisible(false);
            createButton.setVisible(false);
            refreshButton.setVisible(false);

            joinRoomItem.setVisible(false);
            addRoomItem.setVisible(true);
            createItem.setVisible(true);
            refreshItem.setVisible(true);

            showHiddenButtons.setVisible(true);
        } else if (this.getWidth() <= (threeButtonWidth + 19)) {
            joinRoomButton.setVisible(true);
            addRoomButton.setVisible(true);
            createButton.setVisible(false);
            refreshButton.setVisible(false);

            joinRoomItem.setVisible(false);
            addRoomItem.setVisible(false);
            createItem.setVisible(true);
            refreshItem.setVisible(true);

            showHiddenButtons.setVisible(true);
        } else if (this.getWidth() <= (allButtonWidth + 19)) {
            joinRoomButton.setVisible(true);
            addRoomButton.setVisible(true);
            createButton.setVisible(true);
            refreshButton.setVisible(false);

            joinRoomItem.setVisible(false);
            addRoomItem.setVisible(false);
            createItem.setVisible(false);
            refreshItem.setVisible(true);

            showHiddenButtons.setVisible(true);
        } else if (this.getWidth() > (allButtonWidth + 19)) {
            joinRoomButton.setVisible(true);
            addRoomButton.setVisible(true);
            createButton.setVisible(true);
            refreshButton.setVisible(true);

            joinRoomItem.setVisible(false);
            addRoomItem.setVisible(false);
            createItem.setVisible(false);
            refreshItem.setVisible(false);

            showHiddenButtons.setVisible(false);
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    // will need that, when the window is smaller than the buttons width...
    private void setButtonsWidth() {
        allButtonWidth = createButton.getWidth() + refreshButton.getWidth()
            + addRoomButton.getWidth() + joinRoomButton.getWidth();
        threeButtonWidth = createButton.getWidth() + addRoomButton.getWidth()
            + joinRoomButton.getWidth() + showHiddenButtons.getWidth();
        twoButtonWidth = addRoomButton.getWidth() + joinRoomButton.getWidth()
            + showHiddenButtons.getWidth();
        oneButtonWidth = joinRoomButton.getWidth()
            + showHiddenButtons.getWidth();
    }

}
