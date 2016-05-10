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
package org.jivesoftware.spark.ui.conferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.UIManager;


import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
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
import org.jivesoftware.spark.util.ImageCombiner;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

/**
 * A UI that handles all Group Rooms contained in an XMPP Messenger server. This
 * handles creation and joining of rooms for group chat discussions as well as
 * the listing of the creation times, number of occupants in a room, and the
 * room name itself.
 */
public class ConferenceRoomBrowser extends JPanel implements ActionListener,
	ComponentListener {
    private static final long serialVersionUID = -4483998189117467048L;
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

    private JMenuItem joinRoomItem;
    private JMenuItem addRoomItem;
    private JMenuItem createItem;
    private JMenuItem refreshItem;

    private ChatManager chatManager;

    private JDialog dlg;

    private BookmarksUI conferences;
    private String serviceName;

    private int allButtonWidth;
    private int threeButtonWidth;
    private int twoButtonWidth;
    private int oneButtonWidth;

    private boolean partialDiscovery = false;

    private JPopupMenu popup;

    final TableRowSorter<TableModel> sorter;

    /**
     * Creates a new instance of ConferenceRooms.
     *
     * @param conferences
     *            the conference ui.
     * @param serviceName
     *            the name of the conference service.
     *
     */
    public ConferenceRoomBrowser(BookmarksUI conferences,
	    final String serviceName) {

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
	final JPanel Hauptpanel = new JPanel(new BorderLayout());
	JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel pane_hiddenButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	toolbar.add(joinRoomButton);
	toolbar.add(addRoomButton);
	toolbar.add(createButton);
	toolbar.add(refreshButton);
	pane_hiddenButtons.add(showHiddenButtons);

	Hauptpanel.add(toolbar, BorderLayout.WEST);
	Hauptpanel.add(pane_hiddenButtons, BorderLayout.EAST);
	this.add(Hauptpanel, BorderLayout.NORTH);

        JLabel labelFilter = new JLabel( Res.getString( "label.search" ) );
        JTextField txtFilter = new JTextField( 20 );
    txtFilter.setMinimumSize(new Dimension(50,20));
        
    //add fields for filter
    final JPanel Filterpanel = new JPanel(new BorderLayout());
	JPanel toolbarFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
	toolbarFilter.add( labelFilter );
	toolbarFilter.add( txtFilter );

	Filterpanel.add(toolbarFilter);
	this.add(Filterpanel,BorderLayout.SOUTH);
        

	createButton.addActionListener(this);
	createItem.addActionListener(this);
	joinRoomButton.addActionListener(this);
	refreshButton.addActionListener(this);

	ResourceUtils.resButton(createButton,
		Res.getString("button.create.room"));
	ResourceUtils.resButton(joinRoomButton,
		Res.getString("button.join.room"));
	ResourceUtils.resButton(refreshButton, Res.getString("button.refresh"));
	ResourceUtils.resButton(addRoomButton,
		Res.getString("button.bookmark.room"));

	refreshButton.setToolTipText(Res.getString("message.update.room.list"));
	joinRoomButton.setToolTipText(Res
		.getString("message.join.conference.room"));
	createButton.setToolTipText(Res
		.getString("message.create.or.join.room"));

	// Add Group Chat Table
	roomsTable = new RoomList();

    //build model for roomsTable, ignoring the 1st column              
    sorter = new TableRowSorter<>( roomsTable.getModel() );
    roomsTable.setRowSorter(sorter);
    
	final JScrollPane pane = new JScrollPane(roomsTable);
	pane.setBackground(Color.white);
	pane.setForeground(Color.white);
	this.setBackground(Color.white);
	this.setForeground(Color.white);
	pane.getViewport().setBackground(Color.white);
	this.add(pane, BorderLayout.CENTER);

	chatManager = SparkManager.getChatManager();

        
    txtFilter.addKeyListener( new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            JTextField textField = (JTextField)e.getSource();
            String text = textField.getText();
            List<RowFilter<Object,Object>> filters = new ArrayList<>();
            filters.add(RowFilter.regexFilter(text, 1));
            filters.add(RowFilter.regexFilter(text, 2));
            filters.add(RowFilter.regexFilter(text, 3));
            RowFilter<Object,Object> af = RowFilter.orFilter(filters);
            sorter.setRowFilter(af);
        }
    });                   
	joinRoomButton.addActionListener( actionEvent -> joinSelectedRoom() );
	addRoomButton.addActionListener( actionEvent -> bookmarkRoom(serviceName) );

	refreshButton.addActionListener( actionEvent -> refreshRoomList(serviceName) );

	joinRoomItem.addActionListener( actionEvent -> joinSelectedRoom() );

	addRoomItem.addActionListener( actionEvent -> bookmarkRoom(serviceName) );

	refreshItem.addActionListener( actionEvent -> refreshRoomList(serviceName) );

	showHiddenButtons.addActionListener( actionEvent -> popup.show(showHiddenButtons, 0, showHiddenButtons.getHeight()) );

	joinRoomButton.setEnabled(false);
	addRoomButton.setEnabled(false);
	joinRoomItem.setEnabled(false);
	addRoomItem.setEnabled(false);

	addTableListener();
    }

   private void startLoadingImg(){
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

   private void stopLoadingImg(){
       SwingWorker stopLoading = new SwingWorker() {

           @Override
           public Object construct() {
               return null;
           }
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

   private void clearTable(){
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

    private void refreshRoomList(final String serviceName) {
        startLoadingImg();
        clearTable();

        TimerTask refreshTask = new TimerTask() {
            Collection<HostedRoom> rooms;

            @Override
            public void run() {
                try {
                    rooms = getRoomList(serviceName);
                    try { 
                        for (HostedRoom aResult : rooms) {
                            RoomObject room = getRoomsAndInfo(aResult);
                            addRoomToTable(room.getRoomJID(), room.getRoomName(),
                            room.getNumberOfOccupants());
                        }
                        stopLoadingImg();
                    } catch (Exception e) {
                        Log.error("Unable to retrieve room list and info.", e);
                    }
                } catch ( Exception e1 ) {
                    System.err.println(e1);
                }
            }
        };
        TaskEngine.getInstance().submit(refreshTask);
    }

    private RoomObject getRoomsAndInfo(final HostedRoom room) {
        boolean stillSearchForOccupants = true;
        RoomObject result = null;
        try {
            try {
                String roomName = room.getName();
                String roomJID = room.getJid();
                int numberOfOccupants = -1;
                if (stillSearchForOccupants) {
                RoomInfo roomInfo = null;
                try {
                    roomInfo = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getRoomInfo( roomJID );
                } catch (Exception e) {
                    // Nothing to do
                }

                if (roomInfo != null) {
                    numberOfOccupants = roomInfo.getOccupantsCount();
                    if (numberOfOccupants == -1) {
                    }
                } else {
                }
                }

                result = new RoomObject();
                result.setRoomJID(roomJID);
                result.setRoomName(roomName);
                result.setNumberOfOccupants(numberOfOccupants);
            } catch (Exception e) {
                Log.error("Error setting up GroupChatTable", e);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return result;
    }

    private void bookmarkRoom(String serviceName) {
        int selectedRow = roomsTable.getSelectedRow();
        
        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
        
        if (-1 == selectedRow) {
            JOptionPane.showMessageDialog(dlg, Res.getString("message.select.add.room.to.add"), Res.getString("title.group.chat"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final String roomJID = roomsTable.getValueAt(selectedRow, 2) + "@" + serviceName;
        final String roomName = roomsTable.getValueAt(selectedRow, 1).toString();

        // Check to see what type of room this is.
        boolean persistent = false;
        try {
            final RoomInfo roomInfo = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getRoomInfo( roomJID );
            persistent = roomInfo.isPersistent();
        } catch (Exception e) {
            // Do not return
            Log.error("This room does not exist. Probably this room was temporary and was closed");
        }
        if (!persistent) {
             JOptionPane.showMessageDialog(dlg, Res.getString("message.bookmark.temporary.room.error"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
             return;
        }

        Tree serviceTree = conferences.getTree();
        JiveTreeNode rootNode = (JiveTreeNode) serviceTree.getModel().getRoot();

        TreePath rootPath = serviceTree.findByName(serviceTree, new String[] { rootNode.toString(), serviceName });

        boolean isBookmarked = isBookmarked(roomJID);

        if (!isBookmarked) {
            JiveTreeNode node = (JiveTreeNode) serviceTree.getLastSelectedPathComponent();
            if (node == null) {
                TreePath path = serviceTree.findByName(serviceTree, new String[] { rootNode.toString(), ConferenceServices.getDefaultServiceName() });
                node = (JiveTreeNode) path.getLastPathComponent();
            }
            JiveTreeNode roomNode = new JiveTreeNode(roomName, false, SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
            roomNode.setAssociatedObject(roomJID);
            node.add(roomNode);
            final DefaultTreeModel model = (DefaultTreeModel) serviceTree.getModel();
            model.nodeStructureChanged(node);
            serviceTree.expandPath(rootPath);
            roomsTable.getTableModel().setValueAt(new JLabel(SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON)), selectedRow, 0);
            addBookmarkUI(false);

            conferences.addBookmark(roomName, roomJID, false);
        } else {
            // Remove bookmark
            TreePath path = serviceTree.findByName(serviceTree, new String[] { rootNode.toString(), serviceName, roomName });
            JiveTreeNode node = (JiveTreeNode) path.getLastPathComponent();
            final DefaultTreeModel model = (DefaultTreeModel) serviceTree.getModel();
            model.removeNodeFromParent(node);
            roomsTable.getTableModel().setValueAt(new JLabel(SparkRes.getImageIcon(SparkRes.BLANK_IMAGE)), selectedRow, 0);
            addBookmarkUI(true);

            String jid = (String) node.getAssociatedObject();
            conferences.removeBookmark(jid);
        }
    }

    private void joinSelectedRoom() {
	int selectedRow = roomsTable.getSelectedRow();
	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
	if (-1 == selectedRow) {
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
            if (e.getValueIsAdjusting())
                return;

            int selectedRow = roomsTable.getSelectedRow();
            if (selectedRow != -1) {
                joinRoomButton.setEnabled(true);
                joinRoomItem.setEnabled(true);
                String roomJID = roomsTable.getValueAt(selectedRow,
                    2) + "@" + serviceName;
                addRoomButton.setEnabled(true);
                addRoomItem.setEnabled(true);
                if (isBookmarked(roomJID)) {
                addBookmarkUI(false);
                } else {
                addBookmarkUI(true);
                }
            } else {
                joinRoomButton.setEnabled(false);
                addRoomButton.setEnabled(false);
                joinRoomItem.setEnabled(false);
                addRoomItem.setEnabled(false);
                addBookmarkUI(true);
            }
            } );
    }

    /**
     * Displays the ConferenceRoomBrowser.
     */
    public void invoke() {
        startLoadingImg();
        TimerTask invokeThread = new TimerTask() {
            Collection<HostedRoom> rooms;

            @Override
            public void run() {
                try {
                    rooms = getRoomList(serviceName);

                    if (rooms == null) {
                    	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                        JOptionPane.showMessageDialog(conferences,
                            Res.getString("message.conference.info.error"),
                            Res.getString("title.error"),
                            JOptionPane.ERROR_MESSAGE);
                        if (dlg != null) {
                        dlg.dispose();
                        }
                    }else{
                        try {
                            for (HostedRoom room : rooms) {

                            String roomName = room.getName();
                            String roomJID = room.getJid();

                            int numberOfOccupants = -1;

                            // Need to handle the case where the room info does not
                            // contain the number of occupants. If that is the case,
                            // we should not continue to request this info from the
                            // service.
                            if (!partialDiscovery) {
                                RoomInfo roomInfo = null;
                                try {
                                roomInfo = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getRoomInfo( roomJID );
                                } catch (Exception e) {
                                // Nothing to do
                                }

                                if (roomInfo != null) {
                                numberOfOccupants = roomInfo
                                    .getOccupantsCount();
                                }
                                if (roomInfo == null || numberOfOccupants == -1) {
                                partialDiscovery = true;
                                }
                            }
                            addRoomToTable(roomJID, roomName, numberOfOccupants);
                            }
                        } catch (Exception e) {
                            Log.error("Error setting up GroupChatTable", e);
                        }
                    }
                } catch (Exception e) {
                    Log.error("Unable to retrieve list of rooms.", e);
                }
                stopLoadingImg();
            }
        };

        final JOptionPane pane;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(
            Res.getString("title.create.or.bookmark.room"),
            Res.getString("message.add.favorite.room"),
            SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = { Res.getString("close") };
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        dlg = p.createDialog(SparkManager.getMainWindow(),
            Res.getString("title.browse.room.service", serviceName));
        dlg.setModal(false);
        dlg.pack();
        dlg.addComponentListener(this);

        /*
         * looking up which bundle is used to set the size of the Window (not
         * using Localpreferences getLanguage() because sometimes language is
         * not saved in the properties file and so the method only returns an
         * empty String)
         */
        if (Res.getBundle().getLocale().toString().equals("de"))
            dlg.setSize(700, 400);
        else
            dlg.setSize(500, 400);

        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

        PropertyChangeListener changeListener = new PropertyChangeListener() {
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
            public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                dlg.dispose();
            }
            }
        });

        // will need that, when the window is smaller then the buttons width...
        setButtonsWidth();

        showHiddenButtons.setVisible(false);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();  
        TaskEngine.getInstance().submit(invokeThread);
    }

    private final class RoomList extends Table {
	private static final long serialVersionUID = -731280190627042419L;

	public RoomList() {
	    super(new String[] { " ", Res.getString("title.name"),
		    Res.getString("title.address"),
		    Res.getString("title.occupants") });
	    getColumnModel().setColumnMargin(0);
	    getColumnModel().getColumn(0).setMaxWidth(30);
	    getColumnModel().getColumn(3).setMaxWidth(80);

	    setSelectionBackground(Table.SELECTION_COLOR);
	    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    setRowSelectionAllowed(true);
	    // setSortable(true);

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
                    private static final long serialVersionUID = 5142016247851363420L;

                    public void actionPerformed(ActionEvent actionEvent) {
                        int selectedRow = roomsTable.getSelectedRow();
                        if (selectedRow != -1) {
                            String roomJID = roomsTable.getValueAt(selectedRow, 2) + "@" + serviceName;
                            RoomBrowser roomBrowser = new RoomBrowser();
                            roomBrowser.displayRoomInformation(roomJID);
                        }
                    }
                };

                roomInfoAction.putValue(Action.NAME, Res.getString("menuitem.view.room.info"));
                roomInfoAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DATA_FIND_IMAGE));

                final int selectedRow = roomsTable.getSelectedRow();
                final String roomName = roomsTable.getValueAt(selectedRow, 1).toString();
                popupMenu.add(roomInfoAction);
                final JCheckBoxMenuItem autoJoin = new JCheckBoxMenuItem(Res.getString("menuitem.join.on.startup"));
                autoJoin.addActionListener( e1 -> {
                    String roomJID = roomsTable.getValueAt(selectedRow, 2) + "@" + serviceName;
                    conferences.removeBookmark(roomJID);
                    conferences.addBookmark(roomName, roomJID, autoJoin.isSelected());
                } );


                if (selectedRow != -1) {

                    for (BookmarkedConference bookmark :conferences.getBookmarks())
                    {
                        if (roomName.equals(bookmark.getName()))
                        {
                            autoJoin.setSelected(bookmark.isAutoJoin());
                            popupMenu.add(autoJoin);
                        }

                    }


                }

                popupMenu.show(roomsTable, e.getX(), e.getY());
            }
        }

    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == createButton || e.getSource() == createItem) {
	    createRoom();
	}
    }

    private void enterRoom() {
	int selectedRow = roomsTable.getSelectedRow();
	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
	if (-1 == selectedRow) {
	    JOptionPane.showMessageDialog(dlg,
		    Res.getString("message.select.room.to.enter"),
		    Res.getString("title.group.chat"),
		    JOptionPane.INFORMATION_MESSAGE);
	    return;
	}
	final String roomJID = roomsTable.getValueAt(selectedRow, 2) + "@"
		+ serviceName;
	final String roomDescription = (String) roomsTable.getValueAt(
		selectedRow, 1);

	try {
	    chatManager.getChatContainer().getChatRoom(roomJID);
	} catch (ChatRoomNotFoundException e1) {
	    ConferenceUtils.joinConferenceOnSeperateThread(roomDescription,
		    roomJID, null);
	}
    }

    /**
     * Returns a Collection of all rooms in the specified Conference Service.
     *
     * @param serviceName
     *            the name of the conference service.
     * @return a Collection of all rooms in the Conference service.
     * @throws Exception
     *             if a problem occurs while getting the room list
     */
    private static Collection<HostedRoom> getRoomList(String serviceName)
	    throws Exception {
        return MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getHostedRooms( serviceName );
    }

    /**
     * Create a new room based on room table selection.
     */
    private void createRoom() {
	RoomCreationDialog mucRoomDialog = new RoomCreationDialog();
	final MultiUserChat groupChat = mucRoomDialog.createGroupChat(
		SparkManager.getMainWindow(), serviceName);
	LocalPreferences pref = SettingsManager.getLocalPreferences();

	if (null != groupChat) {

	    // Join Room
	    try {
		GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(groupChat);

		groupChat.create(pref.getNickname());
		chatManager.getChatContainer().addChatRoom(room);
		chatManager.getChatContainer().activateChatRoom(room);

		// Send Form
		Form form = groupChat.getConfigurationForm().createAnswerForm();
		if (mucRoomDialog.isPasswordProtected()) {
		    String password = mucRoomDialog.getPassword();
		    room.setPassword(password);
		    form.setAnswer("muc#roomconfig_passwordprotectedroom", true);
		    form.setAnswer("muc#roomconfig_roomsecret", password);
		}
		form.setAnswer("muc#roomconfig_roomname",
			mucRoomDialog.getRoomName());
		form.setAnswer("muc#roomconfig_roomdesc",
			mucRoomDialog.getRoomTopic());

		if (mucRoomDialog.isPermanent()) {
		    form.setAnswer("muc#roomconfig_persistentroom", true);
		}

		List<String> owners = new ArrayList<>();
		owners.add(SparkManager.getSessionManager().getBareAddress());
		form.setAnswer("muc#roomconfig_roomowners", owners);

		// new DataFormDialog(groupChat, form);
		groupChat.sendConfigurationForm(form);
        addRoomToTable(groupChat.getRoom(), XmppStringUtils.parseLocalpart(groupChat.getRoom()), 1);
	    } catch (XMPPException | SmackException e1) {
		Log.error("Error creating new room.", e1);
		UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
		JOptionPane
			.showMessageDialog(this,
				Res.getString("message.room.creation.error"),
				Res.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    /**
     * Adds a room to the room table.
     *
     * @param jid
     *            the jid of the conference room.
     * @param roomName
     *            the name of the conference room.
     * @param numberOfOccupants
     *            the number of occupants in the conference room. If -1 is
     *            specified, the the occupant count will show as n/a.
     */
    private void addRoomToTable(final String jid, final String roomName,
	    final int numberOfOccupants) {
        SwingWorker addRoomThread = new SwingWorker() {

            @Override
            public Object construct() {
                JLabel iconLabel = new JLabel();
                iconLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
                boolean isbookmark = false;
                boolean ispassword = false;

                ImageIcon bookmarkicon = SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON);
                ImageIcon passwordicon = SparkRes.getImageIcon(SparkRes.LOCK_16x16);

                if (isBookmarked(jid)) {
                    isbookmark = true;
                    iconLabel.setIcon(SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
                }
                if (isPasswordProtected(jid)) {
                    ispassword = true;
                }

                if (isbookmark && ispassword) {
                    Image img = ImageCombiner.combine(bookmarkicon, passwordicon);
                    iconLabel.setIcon(new ImageIcon(img));
                } else if (isbookmark) {
                    iconLabel.setIcon(bookmarkicon);
                } else if (ispassword) {
                    Image img = ImageCombiner.returnTransparentImage(
                        passwordicon.getIconWidth(), passwordicon.getIconHeight());

                    Image combined = ImageCombiner.combine(new ImageIcon(img),
                        passwordicon);

                    iconLabel.setIcon(new ImageIcon(combined));
                }

                String occupants = Integer.toString(numberOfOccupants);
                if (numberOfOccupants == -1) {
                    occupants = "n/a";
                }

                final Object[] insertRoom = new Object[] { iconLabel, roomName,
                    XmppStringUtils.parseLocalpart(jid), occupants };
                return insertRoom;
            }
            
            @Override
            public void finished() {
                Object[] insertRoom = (Object[])get();
                roomsTable.getTableModel().addRow(insertRoom);
            }
        };
        addRoomThread.start();
    }

    /**
     * Returns true if the room specified is bookmarked.
     *
     * @param roomJID
     *            the jid of the room to check.
     * @return true if the room is bookmarked.
     */
    private boolean isBookmarked(String roomJID) {
	for (Object o : conferences.getBookmarks()) {
	    BookmarkedConference bk = (BookmarkedConference) o;
	    String jid = bk.getJid();
	    if (jid != null && roomJID.equals(jid)) {
		return true;
	    }
	}

	return false;

    }

    /**
     * Returns true if the room is password protected or Members only
     *
     * @param roomjid
     * @return
     */
    private boolean isPasswordProtected(String roomjid) {
	boolean result = false;
	try {

	    RoomInfo rif = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getRoomInfo( roomjid );

	    result = rif.isMembersOnly() || rif.isPasswordProtected();

	} catch (XMPPException | SmackException | NumberFormatException e) {

	}

        return result;
    }

    /**
     * Toggles the bookmark room button depending on it's state.
     *
     * @param addBookmark
     *            true if the button should display itself as bookmarkable :)
     */
    private void addBookmarkUI(boolean addBookmark) {
	if (!addBookmark) {
	    addRoomButton.setText(Res.getString("button.remove.bookmark"));
	    addRoomButton.setIcon(SparkRes
		    .getImageIcon(SparkRes.DELETE_BOOKMARK_ICON));
	} else {
	    ResourceUtils.resButton(addRoomButton,
		    Res.getString("button.bookmark.room"));
	    addRoomButton.setIcon(SparkRes
		    .getImageIcon(SparkRes.ADD_BOOKMARK_ICON));
	}
    }

    /*
     * * Center the text
     */
    static class CenterRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 105809683882744641L;

	public CenterRenderer() {
	    setHorizontalAlignment(CENTER);
	}

	public Component getTableCellRendererComponent(JTable table,
		Object value, boolean isSelected, boolean hasFocus, int row,
		int column) {
	    super.getTableCellRendererComponent(table, value, isSelected,
		    hasFocus, row, column);
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

    public void componentHidden(ComponentEvent e) {

    }

    public void componentMoved(ComponentEvent e) {

    }

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
	}

	else if (this.getWidth() <= (twoButtonWidth + 19)) {
	    joinRoomButton.setVisible(true);
	    addRoomButton.setVisible(false);
	    createButton.setVisible(false);
	    refreshButton.setVisible(false);

	    joinRoomItem.setVisible(false);
	    addRoomItem.setVisible(true);
	    createItem.setVisible(true);
	    refreshItem.setVisible(true);

	    showHiddenButtons.setVisible(true);
	}

	else if (this.getWidth() <= (threeButtonWidth + 19)) {
	    joinRoomButton.setVisible(true);
	    addRoomButton.setVisible(true);
	    createButton.setVisible(false);
	    refreshButton.setVisible(false);

	    joinRoomItem.setVisible(false);
	    addRoomItem.setVisible(false);
	    createItem.setVisible(true);
	    refreshItem.setVisible(true);

	    showHiddenButtons.setVisible(true);
	}

	else if (this.getWidth() <= (allButtonWidth + 19)) {
	    joinRoomButton.setVisible(true);
	    addRoomButton.setVisible(true);
	    createButton.setVisible(true);
	    refreshButton.setVisible(false);

	    joinRoomItem.setVisible(false);
	    addRoomItem.setVisible(false);
	    createItem.setVisible(false);
	    refreshItem.setVisible(true);

	    showHiddenButtons.setVisible(true);
	}

	else if (this.getWidth() > (allButtonWidth + 19)) {
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
