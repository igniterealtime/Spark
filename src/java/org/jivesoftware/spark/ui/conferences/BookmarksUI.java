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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bookmark.BookmarkManager;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.JiveTreeCellRenderer;
import org.jivesoftware.spark.component.JiveTreeNode;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.Tree;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;

/**
 * BookmarkedConferences is used to display the UI for all bookmarked conference rooms.
 */
public class BookmarksUI extends JPanel {
	private static final long serialVersionUID = -315974309284551232L;

	private Tree tree;

    private JiveTreeNode rootNode;

    private Collection<String> mucServices;

    private Set<String> autoJoinRooms = new HashSet<String>();

    private List<ContextMenuListener> listeners = new ArrayList<ContextMenuListener>();

    /**
     * Bookmarks listeners
     */
    private List<BookmarksListener> bookmarkListeners = new ArrayList<BookmarksListener>();

    private BookmarkManager manager;

    /**
     * 
     */
    public BookmarksUI() {
        
    }

    /**
     * Initialize Conference UI.
     */
    public void loadUI() {
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setLayout(new GridBagLayout());

        add(getServicePanel(), new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        rootNode = new JiveTreeNode("Conference Services");
        tree = new Tree(rootNode) {
			private static final long serialVersionUID = -8445572224948613446L;

            @Override
			protected void setExpandedState(TreePath path, boolean state) {
                // Ignore all collapse requests; collapse events will not be fired
                if (state) {
                    super.setExpandedState(path, state);
                }
            }
        };

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                tree.setCursor(GraphicUtils.HAND_CURSOR);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                tree.setCursor(GraphicUtils.DEFAULT_CURSOR);
            }
        });


        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // Add all registered services.
        addRegisteredServices();


        tree.setCellRenderer(new JiveTreeCellRenderer());
        tree.putClientProperty("JTree.lineStyle", "None");
        tree.setRootVisible(false);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                    if (path == null) {
                        return;
                    }
                    JiveTreeNode node = (JiveTreeNode)path.getLastPathComponent();
                    if (node != null && node.getAllowsChildren()) {
                        browseRooms((String)node.getUserObject());
                    }
                    else if (node != null) {
                        String roomJID = node.getAssociatedObject().toString();

                        ConferenceUtils.joinConferenceOnSeperateThread(node.getUserObject().toString(), roomJID, null);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                checkPopup(mouseEvent);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                checkPopup(mouseEvent);
            }
        }
        );
        setBackground(Color.white);

        try {
            manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());
        }
        catch (XMPPException e) {
            Log.error(e);
        }
        
        final TimerTask bookmarkTask = new TimerTask() {
            @Override
            public void run() {
                Collection<BookmarkedConference> bc = null;
        		while(bc == null){
        			try {
        				bc = manager.getBookmarkedConferences();
        			}catch (XMPPException error) {
                        Log.error(error);
                    }
        		}
                setBookmarks(bc);
            }
        };
        
        TaskEngine.getInstance().schedule(bookmarkTask, 5000);
    }

    private void checkPopup(MouseEvent mouseEvent) {
        // Handle no path for x y coordinates
        if (tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY()) == null) {
            return;
        }

        final JiveTreeNode node = (JiveTreeNode)tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY()).getLastPathComponent();

        if (mouseEvent.isPopupTrigger() && node != null) {
            JPopupMenu popupMenu = new JPopupMenu();

            // Define service actions
            Action browseAction = new AbstractAction() {
				private static final long serialVersionUID = -8866708581713789939L;

                @Override
				public void actionPerformed(ActionEvent actionEvent) {
                    browseRooms(node.toString());
                }
            };
            browseAction.putValue(Action.NAME, Res.getString("menuitem.browse.service"));
            browseAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DATA_FIND_IMAGE));

            Action removeServiceAction = new AbstractAction() {
				private static final long serialVersionUID = -5276754429117462223L;

                @Override
				public void actionPerformed(ActionEvent actionEvent) {
                    DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
                    treeModel.removeNodeFromParent(node);
                }
            };
            removeServiceAction.putValue(Action.NAME, Res.getString("menuitem.remove.service"));
            removeServiceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

            JMenuItem browseServiceMenu = new JMenuItem(browseAction);
            JMenuItem removeServiceMenu = new JMenuItem(removeServiceAction);

            // Define room actions
            Action joinRoomAction = new AbstractAction() {
				private static final long serialVersionUID = -356016505214728244L;

                @Override
				public void actionPerformed(ActionEvent actionEvent) {
                    String roomName = node.getUserObject().toString();
                    String roomJID = node.getAssociatedObject().toString();
                    ConferenceUtils.joinConferenceOnSeperateThread(roomName, roomJID, null);
                }
            };

            joinRoomAction.putValue(Action.NAME, Res.getString("menuitem.join.room"));
            joinRoomAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_USER_ENTER));

            Action removeRoomAction = new AbstractAction() {
				private static final long serialVersionUID = -7560090091884746914L;

                @Override
				public void actionPerformed(ActionEvent actionEvent) {
                    DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
                    treeModel.removeNodeFromParent(node);
                    String roomJID = node.getAssociatedObject().toString();
                    autoJoinRooms.remove(roomJID);
                    removeBookmark(roomJID);
                }
            };
            removeRoomAction.putValue(Action.NAME, Res.getString("menuitem.remove.bookmark"));
            removeRoomAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DELETE_BOOKMARK_ICON));


            JMenuItem joinRoomMenu = new JMenuItem(joinRoomAction);
            JMenuItem removeRoomMenu = new JMenuItem(removeRoomAction);


            if (node.getAllowsChildren()) {
                popupMenu.add(browseServiceMenu);
                popupMenu.add(removeServiceMenu);
            }
            else {
                popupMenu.add(joinRoomMenu);
                popupMenu.add(removeRoomMenu);
                popupMenu.addSeparator();

                Action autoJoin = new AbstractAction() {
					private static final long serialVersionUID = 7857469398581933449L;

                    @Override
					public void actionPerformed(ActionEvent e) {
                        String roomJID = node.getAssociatedObject().toString();
                        if (autoJoinRooms.contains(roomJID)) {
                            autoJoinRooms.remove(roomJID);
                        }
                        else {
                            autoJoinRooms.add(roomJID);
                        }

                        String name = node.getUserObject().toString();
                        addBookmark(name, roomJID, autoJoinRooms.contains(roomJID));
                    }
                };

                autoJoin.putValue(Action.NAME, Res.getString("menuitem.join.on.startup"));

                JCheckBoxMenuItem item = new JCheckBoxMenuItem(autoJoin);
                String roomJID = node.getAssociatedObject().toString();
                item.setSelected(autoJoinRooms.contains(roomJID));
                popupMenu.add(item);

                // Define service actions
                Action roomInfoAction = new AbstractAction() {
					private static final long serialVersionUID = -8336773839944003744L;

                    @Override
					public void actionPerformed(ActionEvent actionEvent) {
                        String roomJID = node.getAssociatedObject().toString();
                        RoomBrowser roomBrowser = new RoomBrowser();
                        roomBrowser.displayRoomInformation(roomJID);
                    }
                };

                roomInfoAction.putValue(Action.NAME, Res.getString("menuitem.view.room.info"));
                roomInfoAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DATA_FIND_IMAGE));
                popupMenu.add(roomInfoAction);
            }

            // Fire menu listeners
            fireContextMenuListeners(popupMenu, node);

            // Display popup menu.
            popupMenu.show(tree, mouseEvent.getX(), mouseEvent.getY());
        }
    }

    public void browseRooms(String serviceName) {
        ConferenceRoomBrowser rooms = new ConferenceRoomBrowser(this, serviceName);
        rooms.invoke();
    }

    private void addRegisteredServices() {
        SwingWorker worker = new SwingWorker() {

            @Override
            public Object construct() {
                try {
                    if (SparkManager.getConnection().isConnected()) {
                        mucServices = MultiUserChat.getServiceNames(SparkManager.getConnection());
                    }
                }
                catch (XMPPException e) {
                    Log.error("Unable to load MUC Service Names.", e);
                }
                return mucServices;
            }

            @Override
            public void finished() {
                if (mucServices == null) {
                    return;
                }

                for (Object mucService : mucServices) {
                    String service = (String) mucService;
                    if (!hasService(service)) {
                        addServiceToList(service);
                    }
                }
            }
        };

        worker.start();
    }

    /**
     * Adds a new service (ex. conferences@jabber.org) to the services list.
     *
     * @param service the new service.
     * @return the new service node created.
     */
    public JiveTreeNode addServiceToList(String service) {
        final JiveTreeNode serviceNode = new JiveTreeNode(service, true, SparkRes.getImageIcon(SparkRes.SERVER_ICON));
        rootNode.add(serviceNode);
        final DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.nodeStructureChanged(rootNode);
        // expand the tree for displaying
        for (int i = 0; i <= tree.getRowCount(); i++) {
            tree.expandPath(tree.getPathForRow(i));
        }
        return serviceNode;
    }

    /**
     * Adds a new bookmark to a particular service node.
     *
     * @param serviceNode the service node.
     * @param roomName    the name of the room to bookmark.
     * @param roomJID     the jid of the room.
     * @return the new bookmark created.
     */
    public JiveTreeNode addBookmark(JiveTreeNode serviceNode, String roomName, String roomJID) {
        JiveTreeNode roomNode = new JiveTreeNode(roomName, false, SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
        roomNode.setAssociatedObject(roomJID);
        serviceNode.add(roomNode);
        final DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.nodeStructureChanged(serviceNode);
        return roomNode;
    }

    public void addBookmark(String roomName, String roomJID, boolean autoJoin) {
        try {
            if (autoJoin)
            {
                autoJoinRooms.add(roomJID);
            } 
            manager.addBookmarkedConference(roomName, roomJID, autoJoin, null, null);
            fireBookmarksAdded(roomJID);  //fire bookmark event
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }

    public void removeBookmark(String roomJID) {
        try {
            if (autoJoinRooms.contains(roomJID))
            {
                autoJoinRooms.remove(roomJID);
            }
            manager.removeBookmarkedConference(roomJID);
            fireBookmarksRemoved(roomJID); // fire bookmark remove event
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }


    private JPanel getServicePanel() {
        final JPanel servicePanel = new JPanel();
        servicePanel.setOpaque(false);
        servicePanel.setLayout(new GridBagLayout());

        final JLabel serviceLabel = new JLabel();
        final RolloverButton addButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.SMALL_ADD_IMAGE));
        addButton.setToolTipText(Res.getString("message.add.conference.service"));

        final JTextField serviceField = new JTextField();
        servicePanel.add(serviceLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 5), 0, 0));
        servicePanel.add(serviceField, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 5), 0, 0));
        servicePanel.add(addButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));

        // Add resource utils
        ResourceUtils.resLabel(serviceLabel, serviceField, Res.getString("label.add.conference.service"));

        final Action conferenceAction = new AbstractAction() {
			private static final long serialVersionUID = 7973928300442518496L;

            @Override
			public void actionPerformed(ActionEvent e) {
                final String conferenceService = serviceField.getText();
                if (hasService(conferenceService)) {
                    JOptionPane.showMessageDialog(null, Res.getString("message.service.already.exists"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    serviceField.setText("");
                }
                else {
                    final List<String> serviceList = new ArrayList<String>();
                    serviceField.setText(Res.getString("message.searching.please.wait"));
                    serviceField.setEnabled(false);
                    addButton.setEnabled(false);
                    SwingWorker worker = new SwingWorker() {
                        DiscoverInfo discoInfo;

                        @Override
                        public Object construct() {
                            ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());

                            try {
                                discoInfo = discoManager.discoverInfo(conferenceService);
                                Iterator<Identity> iter = discoInfo.getIdentities();
                                while (iter.hasNext()) {
                                    DiscoverInfo.Identity identity = (DiscoverInfo.Identity)iter.next();
                                    if ("conference".equals(identity.getCategory())) {
                                        serviceList.add(conferenceService);
                                        break;
                                    }
                                    else if ("server".equals(identity.getCategory())) {
                                        try {
                                            Collection<String> services = getConferenceServices(conferenceService);
                                            for (String service : services) {
                                                serviceList.add(service);
                                            }
                                        }
                                        catch (Exception e1) {
                                            Log.error("Unable to load conference services in server.", e1);
                                        }

                                    }
                                }
                            }
                            catch (XMPPException e1) {
                                Log.error("Error in disco discovery.", e1);
                            }
                            return true;
                        }

                        @Override
                        public void finished() {
                            if (discoInfo != null) {
                                for (String aServiceList : serviceList) {
                                	if (!hasService(aServiceList)) {
                                		addServiceToList(aServiceList);
                                	}
                                }
                                serviceField.setText("");
                                serviceField.setEnabled(true);
                                addButton.setEnabled(true);
                            }
                            else {
                                JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.conference.service.error"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                                serviceField.setText("");
                                serviceField.setEnabled(true);
                                addButton.setEnabled(true);
                            }
                        }
                    };
                    worker.start();
                }
            }
        };

        addButton.addActionListener(conferenceAction);


        serviceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    conferenceAction.actionPerformed(null);
                }
            }
        });

        return servicePanel;
    }

    private Collection<String> getConferenceServices(String server) throws Exception {
        List<String> answer = new ArrayList<String>();
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());
        DiscoverItems items = discoManager.discoverItems(server);
        for (Iterator<DiscoverItems.Item> it = items.getItems(); it.hasNext();) {
            DiscoverItems.Item item = (DiscoverItems.Item)it.next();
            if (item.getEntityID().startsWith("conference") || item.getEntityID().startsWith("private")) {
                answer.add(item.getEntityID());
            }
            else {
                try {
                    DiscoverInfo info = discoManager.discoverInfo(item.getEntityID());
                    if (info.containsFeature("http://jabber.org/protocol/muc")) {
                        answer.add(item.getEntityID());
                    }
                }
                catch (XMPPException e) {
                    Log.error("Problem when loading conference service.", e);
                }
            }
        }
        return answer;
    }

    private boolean hasService(String service) {
        TreePath path = tree.findByName(tree, new String[]{rootNode.getUserObject().toString(), service});
        return path != null;
    }

    /**
     * Returns the Tree used to display bookmarks.
     *
     * @return Tree used to display bookmarks.
     */
    public Tree getTree() {
        return tree;
    }

    /**
     * Sets the current bookmarks used with this account.
     *
     * @param bookmarks the current bookmarks used with this account.
     */
    public void setBookmarks(Collection<BookmarkedConference> bookmarks) {

        for (BookmarkedConference bookmark : bookmarks) {
            String serviceName = StringUtils.parseServer(bookmark.getJid());
            String roomJID = bookmark.getJid();
            String roomName = bookmark.getName();

            if (bookmark.isAutoJoin()) {
                ConferenceUtils.joinConferenceOnSeperateThread(bookmark.getName(), bookmark.getJid(), bookmark.getPassword());
                ConferenceUtils.addUnclosableChatRoom(roomJID);
                autoJoinRooms.add(bookmark.getJid());
            }

            // Get Service Node
            TreePath path = tree.findByName(tree, new String[]{rootNode.getUserObject().toString(), serviceName});
            JiveTreeNode serviceNode;
            if (path == null) {
                serviceNode = addServiceToList(serviceName);
                path = tree.findByName(tree, new String[]{rootNode.getUserObject().toString(), serviceName});
            }
            else {
                serviceNode = (JiveTreeNode)path.getLastPathComponent();
            }

            addBookmark(serviceNode, roomName, roomJID);

            tree.expandPath(path);
        }
    }

    /**
     * Returns all MUC services available.
     *
     * @return a collection of MUC services.
     */
    public Collection<String> getMucServices() {
        return mucServices;
    }

    /**
     * Adds a new ContextMenuListener.
     *
     * @param listener the listener.
     */
    public void addContextMenuListener(ContextMenuListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a ContextMenuListener.
     *
     * @param listener the listener.
     */
    public void removeContextMenuListener(ContextMenuListener listener) {
        listeners.remove(listener);
    }

    private void fireContextMenuListeners(JPopupMenu popup, JiveTreeNode node) {
        for (ContextMenuListener listener : new ArrayList<ContextMenuListener>(listeners)) {
            listener.poppingUp(node, popup);
        }
    }

    /**
     * Adds a new BookmarksListener.
     *
     * @param bookmarkListener the bookmarkListener.
     */
    public void addBookmarksListener(BookmarksListener bookmarkListener) {
        bookmarkListeners.add(bookmarkListener);
    }

    /**
     * Removes a BookmarksListener.
     *
     * @param bookmarkListener the bookmarkListener.
     */
    public void removeBookmarksListener(BookmarksListener bookmarkListener) {
        bookmarkListeners.remove(bookmarkListener);
    }

    private void fireBookmarksAdded(String roomJID) {
        for (BookmarksListener bookmarkListener : new ArrayList<BookmarksListener>(bookmarkListeners)) {
            bookmarkListener.bookmarkAdded(roomJID);
        }
    }

    private void fireBookmarksRemoved(String roomJID) {
        for (BookmarksListener bookmarkListener : new ArrayList<BookmarksListener>(bookmarkListeners)) {
            bookmarkListener.bookmarkRemoved(roomJID);
        }
    }

    /**
     * Returns a list of bookmarks.
     * @return a Collection of bookmarks.
     */
    public Collection<BookmarkedConference> getBookmarks() {
        try {
            return manager.getBookmarkedConferences();
        }
        catch (XMPPException e) {
            Log.error(e);
        }
        return Collections.emptyList();
    }

}
