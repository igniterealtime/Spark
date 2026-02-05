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
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.muc.MultiUserChatConstants;
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
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BookmarkedConferences is used to display the UI for all bookmarked conference rooms.
 */
public class BookmarksUI extends JPanel {
    /**
     * Tree used to display bookmarks.
     */
    private Tree tree;
    private JiveTreeNode rootNode;
    private List<DomainBareJid> mucServices;
    private final Set<EntityBareJid> autoJoinRooms = new HashSet<>();
    private final CopyOnWriteArrayList<ContextMenuListener> listeners = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<BookmarksListener> bookmarkListeners = new CopyOnWriteArrayList<>();

    private BookmarkManager manager;

    public BookmarksUI() {
    }

    /**
     * Initialize Conference UI.
     */
    public void loadUI() {
        EventQueue.invokeLater(() -> {
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            setLayout(new GridBagLayout());

            add(getServicePanel(), new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            rootNode = new JiveTreeNode("Conference Services");
            tree = new Tree(rootNode) {
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
            tree.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        // Handle double click event to join conference room
                        if (mouseEvent.getClickCount() != 2) {
                            return;
                        }
                        TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                        if (path == null) {
                            return;
                        }
                        JiveTreeNode node = (JiveTreeNode) path.getLastPathComponent();
                        if (node != null && node.getAllowsChildren()) {
                            browseRooms((String) node.getUserObject());
                        } else if (node != null) {
                            final String roomName = node.getUserObject().toString();
                            final Jid roomJid = (Jid) node.getAssociatedObject(); // Has a resource part if a nickname is desired.
                            ConferenceUtils.joinConferenceOnSeparateThread(roomName, roomJid.asEntityBareJidOrThrow(), roomJid.getResourceOrNull(), null);
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

            manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());

            final TimerTask bookmarkTask = new TimerTask() {
                @Override
                public void run() {
                    Collection<BookmarkedConference> bc = null;
                    try {
                        while (bc == null) {
                            bc = manager.getBookmarkedConferences();
                        }
                        setBookmarks(bc);
                    } catch (XMPPException | SmackException | InterruptedException error) {
                        Log.error(error);
                    }
                }
            };

            TaskEngine.getInstance().schedule(bookmarkTask, 5000);
        });
    }

    private void checkPopup(MouseEvent mouseEvent) {
        // Handle no path for x y coordinates
        if (tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY()) == null) {
            return;
        }

        final JiveTreeNode node = (JiveTreeNode) tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY()).getLastPathComponent();

        if (mouseEvent.isPopupTrigger() && node != null) {
            JPopupMenu popupMenu = new JPopupMenu();

            // Define service actions
            Action browseAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    browseRooms(node.toString());
                }
            };
            browseAction.putValue(Action.NAME, Res.getString("menuitem.browse.service"));
            browseAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DATA_FIND_IMAGE));

            Action removeServiceAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                    treeModel.removeNodeFromParent(node);
                }
            };
            removeServiceAction.putValue(Action.NAME, Res.getString("menuitem.remove.service"));
            removeServiceAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

            JMenuItem browseServiceMenu = new JMenuItem(browseAction);
            JMenuItem removeServiceMenu = new JMenuItem(removeServiceAction);

            // Define room actions
            Action joinRoomAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final String roomName = node.getUserObject().toString();
                    final Jid roomJid = (Jid) node.getAssociatedObject(); // Has a resource part if a nickname is desired.
                    ConferenceUtils.joinConferenceOnSeparateThread(roomName, roomJid.asEntityBareJidOrThrow(), roomJid.getResourceOrNull(), null);
                }
            };

            joinRoomAction.putValue(Action.NAME, Res.getString("menuitem.join.room"));
            joinRoomAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_USER_ENTER));

            Action removeRoomAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                    treeModel.removeNodeFromParent(node);
                    final Jid roomJid = (Jid) node.getAssociatedObject();
                    autoJoinRooms.remove(roomJid.asEntityBareJidOrThrow());
                    removeBookmark(roomJid.asEntityBareJidOrThrow());
                }
            };
            removeRoomAction.putValue(Action.NAME, Res.getString("menuitem.remove.bookmark"));
            removeRoomAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DELETE_BOOKMARK_ICON));


            JMenuItem joinRoomMenu = new JMenuItem(joinRoomAction);
            JMenuItem removeRoomMenu = new JMenuItem(removeRoomAction);

            // Add context menu items based on node type
            if (node.getAllowsChildren()) {
                popupMenu.add(browseServiceMenu);
                popupMenu.add(removeServiceMenu);
            } else {
                popupMenu.add(joinRoomMenu);
                popupMenu.add(removeRoomMenu);
                popupMenu.addSeparator();

                Action autoJoin = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final EntityBareJid roomJID = ((Jid) node.getAssociatedObject()).asEntityBareJidOrThrow();
                        if (autoJoinRooms.contains(roomJID)) {
                            autoJoinRooms.remove(roomJID);
                        } else {
                            autoJoinRooms.add(roomJID);
                        }

                        String name = node.getUserObject().toString();
                        addBookmark(name, roomJID, autoJoinRooms.contains(roomJID));
                    }
                };

                autoJoin.putValue(Action.NAME, Res.getString("menuitem.join.on.startup"));

                JCheckBoxMenuItem item = new JCheckBoxMenuItem(autoJoin);
                EntityBareJid roomJID = ((Jid) node.getAssociatedObject()).asEntityBareJidOrThrow();
                item.setSelected(autoJoinRooms.contains(roomJID));
                popupMenu.add(item);

                // Define service actions
                Action roomInfoAction = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        EntityBareJid roomJID = ((Jid) node.getAssociatedObject()).asEntityBareJidOrThrow();
                        RoomBrowser roomBrowser = new RoomBrowser();
                        roomBrowser.displayRoomInformation(roomJID);
                    }
                };

                roomInfoAction.putValue(Action.NAME, Res.getString("menuitem.view.room.info"));
                roomInfoAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DATA_FIND_IMAGE));
                popupMenu.add(roomInfoAction);

                Action copyURIgroupChat = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final String roomName = node.getAssociatedObject().toString();
                        SparkManager.setClipboard("xmpp:" + roomName + "?join");
                    }
                };

                copyURIgroupChat.putValue(Action.NAME, Res.getString("button.copy.to.clipboard"));
                copyURIgroupChat.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.COPY_16x16));
                popupMenu.add(copyURIgroupChat);

            }
            // Fire menu listeners
            fireContextMenuListeners(popupMenu, node);
            // Display popup menu.
            popupMenu.show(tree, mouseEvent.getX(), mouseEvent.getY());
        }
    }

    public void browseRooms(String serviceNameString) {
        DomainBareJid serviceName;
        try {
            serviceName = JidCreate.domainBareFrom(serviceNameString);
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }
        browseRooms(serviceName);
    }

    public void browseRooms(DomainBareJid serviceName) {
        ConferenceRoomBrowser rooms = new ConferenceRoomBrowser(this, serviceName);
        rooms.invoke();
    }

    private void addRegisteredServices() {
        SwingWorker worker = new SwingWorker() {

            @Override
            public Object construct() {
                try {
                    if (SparkManager.getConnection().isConnected()) {
                        mucServices = SparkManager.getMucManager().getMucServiceDomains();
                    }
                } catch (XMPPException | SmackException | InterruptedException e) {
                    Log.error("Unable to load MUC Service Names.", e);
                }
                return mucServices;
            }

            @Override
            public void finished() {
                if (mucServices == null) {
                    return;
                }
                for (DomainBareJid service : mucServices) {
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
    public JiveTreeNode addServiceToList(DomainBareJid service) {
        final JiveTreeNode serviceNode = new JiveTreeNode(service.toString(), true, SparkRes.getImageIcon(SparkRes.SERVER_ICON));
        rootNode.add(serviceNode);
        final DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
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
     * @param roomJidNick the jid of the room. Optionally contains a nickname in the resource
     * @return the new bookmark created.
     */
    private JiveTreeNode addBookmark(JiveTreeNode serviceNode, String roomName, Jid roomJidNick) {
        JiveTreeNode roomNode = new JiveTreeNode(roomName, false, SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
        roomNode.setAssociatedObject(roomJidNick);
        serviceNode.add(roomNode);
        final DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.nodeStructureChanged(serviceNode);
        return roomNode;
    }

    public void addBookmark(String roomName, EntityBareJid roomJID, boolean autoJoin) {
        try {
            if (autoJoin) {
                autoJoinRooms.add(roomJID);
            }
            manager.addBookmarkedConference(roomName, roomJID, autoJoin, null, null);
            fireBookmarksAdded(roomJID);  //fire bookmark event
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }
    }

    public void removeBookmark(EntityBareJid roomJID) {
        try {
            autoJoinRooms.remove(roomJID);
            manager.removeBookmarkedConference(roomJID);
            fireBookmarksRemoved(roomJID); // fire bookmark remove event
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }
    }


    /**
     * Build panel for adding conference service
     */
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
            @Override
            public void actionPerformed(ActionEvent e) {
                final String conferenceService = serviceField.getText();
                if (hasService(conferenceService)) {
                    UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                    JOptionPane.showMessageDialog(null, Res.getString("message.service.already.exists"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    serviceField.setText("");
                } else {
                    final List<DomainBareJid> serviceList = new ArrayList<>();
                    serviceField.setText(Res.getString("message.searching.please.wait"));
                    serviceField.setEnabled(false);
                    addButton.setEnabled(false);
                    SwingWorker worker = new SwingWorker() {
                        DiscoverInfo discoInfo;

                        @Override
                        public Object construct() {
                            XMPPConnection connection = SparkManager.getConnection();
                            ServiceDiscoveryManager discoManager = SparkManager.getDiscoManager();
                            try {
                                DomainBareJid conferenceServiceJid = JidCreate.domainBareFrom(conferenceService);
                                discoInfo = discoManager.discoverInfo(conferenceServiceJid);
                                for (DiscoverInfo.Identity identity : discoInfo.getIdentities()) {
                                    if ("conference".equals(identity.getCategory())) {
                                        serviceList.add(conferenceServiceJid);
                                        break;
                                    } else if ("server".equals(identity.getCategory())) {
                                        try {
                                            Collection<DomainBareJid> services = getConferenceServices(conferenceServiceJid);
                                            serviceList.addAll(services);
                                        } catch (Exception e1) {
                                            Log.error("Unable to load conference services in server.", e1);
                                        }
                                    }
                                }
                            } catch (XMPPException | SmackException | XmppStringprepException | InterruptedException e1) {
                                Log.error("Error in conferences discovery.", e1);
                            }
                            return true;
                        }

                        @Override
                        public void finished() {
                            if (discoInfo != null) {
                                for (DomainBareJid aServiceList : serviceList) {
                                    if (!hasService(aServiceList)) {
                                        addServiceToList(aServiceList);
                                    }
                                }
                            } else {
                                UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                                JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.conference.service.error"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                            }
                            serviceField.setText("");
                            serviceField.setEnabled(true);
                            addButton.setEnabled(true);
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

    private Collection<DomainBareJid> getConferenceServices(DomainBareJid server) throws Exception {
        List<DomainBareJid> answer = new ArrayList<>();
        ServiceDiscoveryManager discoManager = SparkManager.getDiscoManager();
        DiscoverItems items = discoManager.discoverItems(server);
        for (DiscoverItems.Item item : items.getItems()) {
            DomainBareJid entityID = item.getEntityID().asDomainBareJid();
            // TODO: We should not simply assume that this is MUC service just because it starts with a given prefix.
            if (entityID.toString().startsWith("conference") || entityID.toString().startsWith("private")) {
                answer.add(entityID);
            } else {
                try {
                    DiscoverInfo info = discoManager.discoverInfo(item.getEntityID());
                    if (info.containsFeature(MultiUserChatConstants.NAMESPACE)) {
                        answer.add(entityID);
                    }
                } catch (XMPPException | SmackException e) {
                    Log.error("Problem when loading conference service.", e);
                }
            }
        }
        return answer;
    }

    private boolean hasService(CharSequence service) {
        TreePath path = tree.findByName(tree, new String[]{rootNode.getUserObject().toString(), service.toString()});
        return path != null;
    }

    /**
     * Sets the current bookmarks used with this account.
     */
    public void setBookmarks(Collection<BookmarkedConference> bookmarks) {
        // Iterate bookmarks; join if auotojoin and add to the tree
        for (BookmarkedConference bookmark : bookmarks) {
            DomainBareJid serviceName = bookmark.getJid().asDomainBareJid();
            String roomName = bookmark.getName() != null && !bookmark.getName().isEmpty() ? bookmark.getName() : bookmark.getJid().getLocalpart().asUnescapedString();

            if (bookmark.isAutoJoin()) {
                ConferenceUtils.joinConferenceOnSeparateThread(roomName, bookmark.getJid(), bookmark.getNickname(), bookmark.getPassword());
                ConferenceUtils.addUnclosableChatRoom(bookmark.getJid());
                autoJoinRooms.add(bookmark.getJid());
            }

            // Get Service Node
            String rootNodeStr = rootNode.getUserObject().toString();
            TreePath path = tree.findByName(tree, new String[]{rootNodeStr, serviceName.toString()});
            JiveTreeNode serviceNode;
            if (path == null) {
                serviceNode = addServiceToList(serviceName);
                path = tree.findByName(tree, new String[]{rootNodeStr, serviceName.toString()});
            } else {
                serviceNode = (JiveTreeNode) path.getLastPathComponent();
            }

            Jid roomJidNick = bookmark.getNickname() != null ? JidCreate.fullFrom(bookmark.getJid(), bookmark.getNickname()) : bookmark.getJid();
            addBookmark(serviceNode, roomName, roomJidNick);
            tree.expandPath(path);
        }
    }

    /**
     * Returns all MUC services available.
     */
    public List<DomainBareJid> getMucServices() {
        return mucServices;
    }

    /**
     * Adds a new ContextMenuListener.
     *
     * @param listener the listener.
     */
    public void addContextMenuListener(ContextMenuListener listener) {
        listeners.addIfAbsent(listener);
    }

    /**
     * Removes a ContextMenuListener.
     */
    public void removeContextMenuListener(ContextMenuListener listener) {
        listeners.remove(listener);
    }

    private void fireContextMenuListeners(JPopupMenu popup, JiveTreeNode node) {
        for (final ContextMenuListener listener : listeners) {
            try {
                listener.poppingUp(node, popup);
            } catch (Exception e) {
                Log.error("A ContextMenuListener (" + listener + ") threw an exception while processing a 'poppingUp' event for node: " + node, e);
            }
        }
    }

    /**
     * Adds a new BookmarksListener.
     */
    public void addBookmarksListener(BookmarksListener bookmarkListener) {
        bookmarkListeners.addIfAbsent(bookmarkListener);
    }

    /**
     * Removes a BookmarksListener.
     */
    public void removeBookmarksListener(BookmarksListener bookmarkListener) {
        bookmarkListeners.remove(bookmarkListener);
    }

    private void fireBookmarksAdded(EntityBareJid roomJID) {
        for (final BookmarksListener listener : bookmarkListeners) {
            try {
                listener.bookmarkAdded(roomJID.toString());
            } catch (Exception e) {
                Log.error("A BookmarksListener (" + listener + ") threw an exception while processing a 'bookmarkAdded' event for: " + roomJID, e);
            }
        }
    }

    private void fireBookmarksRemoved(EntityBareJid roomJID) {
        for (final BookmarksListener listener : bookmarkListeners) {
            try {
                listener.bookmarkRemoved(roomJID.toString());
            } catch (Exception e) {
                Log.error("A BookmarksListener (" + listener + ") threw an exception while processing a 'bookmarkRemoved' event for: " + roomJID, e);
            }
        }
    }

    /**
     * Returns a list of bookmarks.
     */
    public List<BookmarkedConference> getBookmarks() {
        try {
            return manager.getBookmarkedConferences();
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }
        return List.of();
    }

    void addOrRemoveNode(DomainBareJid serviceName, boolean isBookmarked, String roomName, EntityBareJid roomJID) {
        JiveTreeNode rootNode = (JiveTreeNode) tree.getModel().getRoot();
        TreePath rootPath = tree.findByName(tree, new String[]{rootNode.toString(), serviceName.toString()});
        if (!isBookmarked) {
            JiveTreeNode node = (JiveTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                String defaultServiceName = ConferenceServices.getDefaultServiceName().toString();
                TreePath path = tree.findByName(tree, new String[]{rootNode.toString(), defaultServiceName});
                node = (JiveTreeNode) path.getLastPathComponent();
            }
            JiveTreeNode roomNode = new JiveTreeNode(roomName, false, SparkRes.getImageIcon(SparkRes.BOOKMARK_ICON));
            roomNode.setAssociatedObject(roomJID);
            node.add(roomNode);
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.nodeStructureChanged(node);
            tree.expandPath(rootPath);
            addBookmark(roomName, roomJID, false);
        } else {
            // Remove bookmark
            TreePath path = tree.findByName(tree, new String[]{rootNode.toString(), serviceName.toString(), roomName});
            JiveTreeNode node = (JiveTreeNode) path.getLastPathComponent();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.removeNodeFromParent(node);
            EntityBareJid jid = ((Jid) node.getAssociatedObject()).asEntityBareJidOrThrow();
            removeBookmark(jid);
        }
    }
}
