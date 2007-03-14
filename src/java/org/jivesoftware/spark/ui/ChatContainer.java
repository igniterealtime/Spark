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

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPaneListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Contains all <code>ChatRoom</code> objects within Spark.
 *
 * @author Derek DeMoro
 */
public class ChatContainer extends SparkTabbedPane implements MessageListener, ChangeListener {
    /**
     * List of all ChatRoom Listeners.
     */
    private final List<ChatRoomListener> chatRoomListeners = new ArrayList<ChatRoomListener>();

    private final List<ChatRoom> chatRoomList = new ArrayList<ChatRoom>();

    private final Map<String, PacketListener> presenceMap = new HashMap<String, PacketListener>();

    private static final String WELCOME_TITLE = SparkRes.getString(SparkRes.WELCOME);


    private ChatFrame chatFrame;

    /**
     * Creates the ChatRooms to hold all ChatRooms.
     */
    public ChatContainer() {
        // Assign location
        super(SettingsManager.getLocalPreferences().isTabTopPosition() ? JTabbedPane.TOP : JTabbedPane.BOTTOM);

        // Set minimum size
        setMinimumSize(new Dimension(400, 200));
        // Don't allow tabs to shrink and allow scrolling.

        addSparkTabbedPaneListener(new SparkTabbedPaneListener() {
            public void tabRemoved(SparkTab tab, Component component, int index) {
                stateChanged(null);
                if (component instanceof ChatRoom) {
                    cleanupChatRoom((ChatRoom)component);
                }
                else if (component instanceof ContainerComponent) {
                    ((ContainerComponent)component).closing();
                }
            }

            public void tabAdded(SparkTab tab, Component component, int index) {
                stateChanged(null);
            }

            public void tabSelected(SparkTab tab, Component component, int index) {
                stateChanged(null);

                // Notify ChatRoomListeners that the tab has been activated.
                if (component instanceof ChatRoom) {
                    fireChatRoomActivated((ChatRoom)component);
                }
            }

            public void allTabsRemoved() {
                chatFrame.setTitle("");
                chatFrame.dispose();
            }


            public boolean canTabClose(SparkTab tab, Component component) {
                return true;
            }
        });

        setCloseButtonEnabled(true);

        // Add Key Navigation
        addKeyNavigation();

        this.setFocusable(false);

        setOpaque(true);

        setBackground(Color.white);
    }


    /**
     * Adds navigation capability to chat rooms. Users can navigate using the alt-left or right arrow keys.
     */
    private void addKeyNavigation() {
        KeyStroke leftStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        String leftStrokeString = org.jivesoftware.spark.util.StringUtils.keyStroke2String(leftStroke);

        // Handle Left Arrow
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt " + leftStrokeString + ""), "navigateLeft");
        this.getActionMap().put("navigateLeft", new AbstractAction("navigateLeft") {
            public void actionPerformed(ActionEvent evt) {
                int selectedIndex = getSelectedIndex();
                if (selectedIndex > 0) {
                    setSelectedIndex(selectedIndex - 1);
                }
                else {
                    setSelectedIndex(getTabCount() - 1);
                }
            }
        });

        KeyStroke rightStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        String rightStrokeString = org.jivesoftware.spark.util.StringUtils.keyStroke2String(rightStroke);

        // Handle Right Arrow
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt " + rightStrokeString + ""), "navigateRight");
        this.getActionMap().put("navigateRight", new AbstractAction("navigateRight") {
            public void actionPerformed(ActionEvent evt) {
                int selectedIndex = getSelectedIndex();
                if (selectedIndex > -1) {
                    int count = getTabCount();
                    if (selectedIndex == (count - 1)) {
                        setSelectedIndex(0);
                    }
                    else {
                        setSelectedIndex(selectedIndex + 1);
                    }
                }
            }
        });

        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("Ctrl W"), "escape");
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "escape");

        this.getActionMap().put("escape", new AbstractAction("escape") {
            public void actionPerformed(ActionEvent evt) {
                closeActiveRoom();
            }
        });

        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_MASK), "shiftCmdW");
        this.getActionMap().put("shiftCmdW", new AbstractAction("shiftCmdW") {
            public void actionPerformed(ActionEvent evt) {
                closeAllChatRooms();
            }
        });

        // Add KeyMappings
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "searchContacts");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control F"), "searchContacts");
        getActionMap().put("searchContacts", new AbstractAction("searchContacts") {
            public void actionPerformed(ActionEvent evt) {
                SparkManager.getUserManager().searchContacts("", SparkManager.getChatManager().getChatContainer().getChatFrame());
            }
        });
    }


    /**
     * Adds a new ChatRoom to Spark.
     *
     * @param room the ChatRoom to add.
     */
    public synchronized void addChatRoom(final ChatRoom room) {
        createFrameIfNeeded();

        room.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        AndFilter presenceFilter = new AndFilter(new PacketTypeFilter(Presence.class), new FromContainsFilter(room.getRoomname()));

        // Next, create a packet listener. We use an anonymous inner class for brevity.
        PacketListener myListener = new PacketListener() {
            public void processPacket(final Packet packet) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        handleRoomPresence((Presence)packet);
                    }
                });
            }
        };


        SparkManager.getConnection().addPacketListener(myListener, presenceFilter);

        // Add to PresenceMap
        presenceMap.put(room.getRoomname(), myListener);

        String tooltip = "";
        if (room instanceof ChatRoomImpl) {
            tooltip = ((ChatRoomImpl)room).getParticipantJID();
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(((ChatRoomImpl)room).getParticipantJID());

            tooltip = "<html><body><b>Contact:&nbsp;</b>" + nickname + "<br><b>JID:&nbsp;</b>" + tooltip;

            // Cancel typing notifications
            SparkManager.getChatManager().cancelledNotification(((ChatRoomImpl)room).getParticipantJID(), "");
        }
        else {
            tooltip = room.getRoomname();
        }

        // Create ChatRoom UI and dock
        SparkTab tab = addTab(room.getTabTitle(), room.getTabIcon(), room, tooltip);
        tab.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                checkTabPopup(e);
            }

            public void mousePressed(MouseEvent e) {
                checkTabPopup(e);
            }
        });

        room.addMessageListener(this);

        // Remove brand panel
        final String title = getTabAt(0).getActualText();
        if (title.equals(WELCOME_TITLE)) {
            chatFrame.setTitle(room.getRoomTitle());
        }

        final SwingWorker visibilityThread = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e1) {
                    Log.error(e1);
                }
                return true;
            }

            public void finished() {
                checkVisibility(room);
            }
        };

        visibilityThread.start();

        // Add to ChatRoomList
        chatRoomList.add(room);

        // Notify users that the chat room has been opened.
        fireChatRoomOpened(room);

        // Focus Chat
        focusChat();
    }

    public void addContainerComponent(ContainerComponent comp) {
        createFrameIfNeeded();

        SparkTab tab = addTab(comp.getTabTitle(), comp.getTabIcon(), comp.getGUI(), comp.getToolTipDescription());
        chatFrame.setTitle(comp.getFrameTitle());
        checkVisibility(comp.getGUI());

        if (getSelectedComponent() != comp) {
            // Notify Decorators
            SparkManager.getChatManager().notifySparkTabHandlers(comp.getGUI());
        }
    }

    /**
     * Handles the presence of a one to one chat room.
     *
     * @param p the presence to handle.
     */
    private void handleRoomPresence(final Presence p) {
        final String roomname = StringUtils.parseBareAddress(p.getFrom());
        ChatRoom chatRoom;
        try {
            chatRoom = getChatRoom(roomname);
        }
        catch (ChatRoomNotFoundException e1) {
            Log.debug("Could not locate chat room " + roomname);
            return;
        }

        final String userid = StringUtils.parseResource(p.getFrom());
        if (p.getType() == Presence.Type.unavailable) {
            fireUserHasLeft(chatRoom, userid);
        }
        else if (p.getType() == Presence.Type.available) {
            fireUserHasJoined(chatRoom, userid);
        }

        // Change tab icon
        if (chatRoom instanceof ChatRoomImpl) {
            // Notify state change.
            int tabLoc = indexOfComponent(chatRoom);
            if (tabLoc != -1) {
                SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
            }
        }
    }

    private void checkVisibility(Component component) {
        if (!chatFrame.isVisible() && SparkManager.getMainWindow().isFocused()) {
            chatFrame.setState(Frame.NORMAL);
            chatFrame.setVisible(true);
        }
        else if (chatFrame.isVisible() && !chatFrame.isInFocus()) {
            flashWindow(component);
        }
        else if (chatFrame.isVisible() && chatFrame.getState() == Frame.ICONIFIED) {
            // Set to new tab.
            int tabLocation = indexOfComponent(component);
            setSelectedIndex(tabLocation);

            // If the ContactList is in the tray, we need better notification by flashing
            // the chatframe.
            flashWindow(component);
        }

        // Handle when chat frame is visible but the Contact List is not.
        else if (chatFrame.isVisible() && !SparkManager.getMainWindow().isVisible()) {
            flashWindow(component);
        }
        else if (!chatFrame.isVisible()) {
            // Set to new tab.
            int tabLocation = indexOfComponent(component);
            setSelectedIndex(tabLocation);

            chatFrame.dispose();
            if (Spark.isWindows()) {
                chatFrame.setFocusableWindowState(false);
                chatFrame.setState(Frame.ICONIFIED);
            }
            chatFrame.setVisible(true);
            chatFrame.setFocusableWindowState(true);

            // If the ContactList is in the tray, we need better notification by flashing
            // the chatframe.
            if (!SparkManager.getMainWindow().isVisible()) {
                flashWindow(component);
            }
            else if (chatFrame.getState() == Frame.ICONIFIED) {
                flashWindow(component);
            }

            if (component instanceof ChatRoom) {
                chatFrame.setTitle(((ChatRoom)component).getRoomTitle());
            }
        }
    }

    /**
     * Removes the ChatRoom resources.
     *
     * @param room the room to remove.
     */
    private void cleanupChatRoom(ChatRoom room) {
        if (room.isActive()) {
            room.leaveChatRoom();
        }

        final PacketListener listener = presenceMap.get(room.getRoomname());
        if (listener != null) {
            SparkManager.getConnection().removePacketListener(listener);
        }

        fireChatRoomClosed(room);
        room.removeMessageListener(this);

        // Remove mappings
        presenceMap.remove(room.getRoomname());

        chatRoomList.remove(room);
    }

    /**
     * Close all chat rooms.
     */
    public void closeAllChatRooms() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();

        if (MainWindow.getInstance().isDocked() || !pref.isAutoCloseChatRoomsEnabled()) {
            return;
        }

        final Iterator rooms = new ArrayList<ChatRoom>(chatRoomList).iterator();
        while (rooms.hasNext()) {
            ChatRoom chatRoom = (ChatRoom)rooms.next();
            closeTab(chatRoom);
            chatRoom.closeChatRoom();
        }

        for (int i = 0; i < getTabCount(); i++) {
            Component comp = getComponentAt(i);
            if (comp instanceof ContainerComponent) {
                ((ContainerComponent)comp).closing();
            }

            closeTab(comp);
        }
    }

    /**
     * Leaves a ChatRoom. Leaving a chat room does everything but close the room itself.
     *
     * @param room the room to leave.
     */
    public void leaveChatRoom(ChatRoom room) {
        // Notify that the chatroom has been left.
        fireChatRoomLeft(room);
        room.leaveChatRoom();

        // Setting the tab to be "disabled". Will not actually disable the tab because
        // that doesn't allow for selection.
        final int location = indexOfComponent(room);
        if (location != -1) {
//            setBackgroundAt(location, Color.GRAY);
            //          setForegroundAt(location, Color.GRAY);
            //        setIconAt(location, null);
        }

        final PacketListener listener = (PacketListener)presenceMap.get(room.getRoomname());
        if (listener != null) {
            SparkManager.getConnection().removePacketListener(listener);
        }
    }

    /**
     * Returns a ChatRoom by name.
     *
     * @param roomName the name of the ChatRoom.
     * @return the ChatRoom
     * @throws ChatRoomNotFoundException
     */
    public ChatRoom getChatRoom(String roomName) throws ChatRoomNotFoundException {
        for (int i = 0; i < getTabCount(); i++) {
            ChatRoom room = null;
            try {
                room = getChatRoom(i);
            }
            catch (ChatRoomNotFoundException e1) {
                // Ignore
            }

            if (room != null && room.getRoomname().equalsIgnoreCase(roomName) && room.isActive()) {
                return room;
            }
        }
        throw new ChatRoomNotFoundException(roomName + " not found.");
    }

    /**
     * Returns a ChatRoom in the specified tab location.
     *
     * @param location the tab location.
     * @return the ChatRoom found.
     * @throws ChatRoomNotFoundException
     */
    public ChatRoom getChatRoom(int location) throws ChatRoomNotFoundException {
        if (getTabCount() < location) {
            return null;
        }
        try {
            Component comp = getComponentAt(location);
            if (comp != null && comp instanceof ChatRoom) {
                return (ChatRoom)comp;
            }
        }
        catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
            Log.error("Error getting Chat Room", outOfBoundsEx);
        }

        throw new ChatRoomNotFoundException();
    }

    /**
     * Returns the Active ChatRoom.
     *
     * @return the ChatRoom active in the tabbed pane.
     * @throws ChatRoomNotFoundException is thrown if no chat room is found.
     */
    public ChatRoom getActiveChatRoom() throws ChatRoomNotFoundException {
        int location = getSelectedIndex();
        if (location != -1) {
            return getChatRoom(location);
        }
        throw new ChatRoomNotFoundException();
    }

    /**
     * Returns the Active Component.
     *
     * @return the Component active in the tabbed pane.
     */
    public Component getActiveRoom() {
        int location = getSelectedIndex();
        if (location != -1) {
            return getComponentAt(location);
        }

        return null;
    }

    /**
     * Activates the specified ChatRoom.
     *
     * @param room the ChatRoom to activate.
     */
    public void activateChatRoom(ChatRoom room) {
        int tabLocation = indexOfComponent(room);
        setSelectedIndex(tabLocation);

        chatFrame.bringFrameIntoFocus();
        focusChat();
    }

    /**
     * Activates the component in tabbed pane.
     *
     * @param component the component contained within the tab to activate.
     */
    public void activateComponent(Component component) {
        int tabLocation = indexOfComponent(component);
        if (tabLocation != -1) {
            setSelectedIndex(tabLocation);
        }

        chatFrame.bringFrameIntoFocus();
        focusChat();
    }

    /**
     * Used for Tray Notifications.
     *
     * @param room    the ChatRoom where the message was received.
     * @param message the message received.
     */
    public void messageReceived(ChatRoom room, Message message) {
        room.increaseUnreadMessageCount();

        // Check to see if it's a room update.
        String from = message.getFrom();
        String insertMessage = message.getBody();
        if (room.getChatType() == Message.Type.chat) {
            from = StringUtils.parseName(from);
        }
        else {
            from = StringUtils.parseResource(from);
        }

        if (ModelUtil.hasLength(from)) {
            insertMessage = from + ": " + insertMessage;
        }

        fireNotifyOnMessage(room);
    }

    public void fireNotifyOnMessage(final ChatRoom chatRoom) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                handleMessageNotification(chatRoom);
            }
        });
    }

    private void handleMessageNotification(final ChatRoom chatRoom) {
        ChatRoom activeChatRoom = null;
        try {
            activeChatRoom = getActiveChatRoom();
        }
        catch (ChatRoomNotFoundException e1) {
            Log.error(e1);
        }

        if (chatFrame.isVisible() && (chatFrame.getState() == Frame.ICONIFIED || chatFrame.getInactiveTime() > 20000)) {
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);
            startFlashing(chatRoom);
            return;
        }

        if (!chatFrame.isVisible() && SparkManager.getMainWindow().isFocused()) {
            chatFrame.setState(Frame.NORMAL);
            chatFrame.setVisible(true);
        }
        else if (chatFrame.isVisible() && !chatFrame.isInFocus()) {
            startFlashing(chatRoom);
        }
        else if (chatFrame.isVisible() && chatFrame.getState() == Frame.ICONIFIED) {
            // Set to new tab.
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);

            // If the ContactList is in the tray, we need better notification by flashing
            // the chatframe.
            startFlashing(chatRoom);
        }

        // Handle when chat frame is visible but the Contact List is not.
        else if (chatFrame.isVisible() && !SparkManager.getMainWindow().isVisible() && !chatFrame.isInFocus()) {
            startFlashing(chatRoom);
        }
        else if (!chatFrame.isVisible()) {
            // Set to new tab.
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);

            if (Spark.isWindows()) {
                chatFrame.setFocusableWindowState(false);
                chatFrame.setState(Frame.ICONIFIED);
            }
            chatFrame.setVisible(true);
            chatFrame.setFocusableWindowState(true);

            // If the ContactList is in the tray, we need better notification by flashing
            // the chatframe.
            if (!SparkManager.getMainWindow().isVisible()) {
                startFlashing(chatRoom);
            }
            else if (chatFrame.getState() == Frame.ICONIFIED) {
                startFlashing(chatRoom);
            }

            chatFrame.setTitle(chatRoom.getRoomTitle());
        }
        else if (chatRoom != activeChatRoom) {
            startFlashing(chatRoom);
        }
    }

    public void messageSent(ChatRoom room, Message message) {
        fireChatRoomStateUpdated(room);
    }

    /**
     * Notification that the tab pane has been modified. Generally by changing of the tabs.
     *
     * @param e the ChangeEvent.
     */
    public void stateChanged(ChangeEvent e) {
        // Stop the flashing only if the chat frame is in focus.
        if (chatFrame.isInFocus()) {
            stopFlashing();
        }

        final Object o = getSelectedComponent();
        if (o instanceof ChatRoom) {
            final ChatRoom room = (ChatRoom)o;
            focusChat();

            // Set the title of the room.
            chatFrame.setTitle(room.getRoomTitle());
            chatFrame.setIconImage(SparkManager.getMainWindow().getIconImage());
        }
        else if (o instanceof ContainerComponent) {
            final ContainerComponent comp = (ContainerComponent)o;
            chatFrame.setTitle(comp.getFrameTitle());
            chatFrame.setIconImage(comp.getTabIcon().getImage());

            SparkManager.getChatManager().notifySparkTabHandlers(comp.getGUI());
        }
    }


    private void stopFlashing() {
        try {
            // Get current tab
            int selectedIndex = getSelectedIndex();
            if (selectedIndex != -1) {
                Component comp = getComponentAt(selectedIndex);
                if (comp != null) {
                    stopFlashing(comp);
                }
            }
        }
        catch (Exception e) {
            Log.error(e);
        }
    }


    /**
     * Closes a tab of a room.
     *
     * @param component the component inside of the tab to close.
     */
    public void closeTab(Component component) {
        int location = indexOfComponent(component);
        if (location == -1) {
            return;
        }

        if (getTabCount() == 0) {
            chatFrame.setTitle("");
            chatFrame.dispose();
        }

        this.removeTabAt(location);
    }

    public void closeActiveRoom() {
        ChatRoom room = null;
        try {
            room = getActiveChatRoom();
        }
        catch (ChatRoomNotFoundException e1) {
            Component comp = getActiveRoom();
            if (comp != null) {
                boolean canClose = ((ContainerComponent)comp).closing();
                if (canClose) {
                    closeTab(comp);
                }
                return;
            }
        }

        // Confirm end session
        boolean isGroupChat = room.getChatType() == Message.Type.groupchat;
        if (isGroupChat) {
            final int ok = JOptionPane.showConfirmDialog(chatFrame, Res.getString("message.end.conversation"),
                    Res.getString("title.confirmation"), JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                room.closeChatRoom();
                return;
            }
        }
        else {
            room.closeChatRoom();
            return;
        }


    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        Iterator iter = chatRoomList.iterator();
        while (iter.hasNext()) {
            ChatRoom room = (ChatRoom)iter.next();
            buf.append("Roomname=").append(room.getRoomname()).append("\n");
        }
        return buf.toString();
    }


    /**
     * Returns true if there are any Rooms present.
     *
     * @return true if Rooms are present, otherwise false.
     */
    public boolean hasRooms() {
        int count = getSelectedIndex();
        return count != -1;
    }

    /**
     * Adds a ChatRoom listener to ChatRooms. The
     * listener will be called when either a ChatRoom has been
     * added, removed, or activated.
     *
     * @param listener the <code>ChatRoomListener</code> to register
     */
    public void addChatRoomListener(ChatRoomListener listener) {
        if (!chatRoomListeners.contains(listener)) {
            chatRoomListeners.add(listener);
        }
    }

    /**
     * Removes the specified <code>ChatRoomListener</code>.
     *
     * @param listener the <code>ChatRoomListener</code> to remove
     */
    public void removeChatRoomListener(ChatRoomListener listener) {
        chatRoomListeners.remove(listener);
    }

    /**
     * Notifies users that a <code>ChatRoom</code> has been opened.
     *
     * @param room - the <code>ChatRoom</code> that has been opened.
     */
    protected void fireChatRoomOpened(ChatRoom room) {
        final Iterator iter = new ArrayList(chatRoomListeners).iterator();
        while (iter.hasNext()) {
            ((ChatRoomListener)iter.next()).chatRoomOpened(room);
        }
    }

    /**
     * Notifies users that a <code>ChatRoom</code> has been left.
     *
     * @param room - the <code>ChatRoom</code> that has been left
     */
    protected void fireChatRoomLeft(ChatRoom room) {
        final Iterator iter = new HashSet(chatRoomListeners).iterator();
        while (iter.hasNext()) {
            final Object chatRoomListener = iter.next();
            ((ChatRoomListener)chatRoomListener).chatRoomLeft(room);
        }
    }

    /**
     * Notifies users that a <code>ChatRoom</code> has been closed.
     *
     * @param room - the <code>ChatRoom</code> that has been closed.
     */
    protected void fireChatRoomClosed(ChatRoom room) {
        final Iterator iter = new HashSet(chatRoomListeners).iterator();
        while (iter.hasNext()) {
            final Object chatRoomListener = iter.next();
            ((ChatRoomListener)chatRoomListener).chatRoomClosed(room);
        }
    }

    /**
     * Notifies users that a <code>ChatRoom</code> has been activated.
     *
     * @param room - the <code>ChatRoom</code> that has been activated.
     */
    protected void fireChatRoomActivated(ChatRoom room) {
        final Iterator iter = new HashSet(chatRoomListeners).iterator();
        while (iter.hasNext()) {
            ((ChatRoomListener)iter.next()).chatRoomActivated(room);
        }
    }

    /**
     * Notifies users that a user has joined a <code>ChatRoom</code>.
     *
     * @param room   - the <code>ChatRoom</code> that a user has joined.
     * @param userid - the userid of the person.
     */
    protected void fireUserHasJoined(final ChatRoom room, final String userid) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final Iterator iter = new HashSet(chatRoomListeners).iterator();
                while (iter.hasNext()) {
                    ((ChatRoomListener)iter.next()).userHasJoined(room, userid);
                }
            }
        });

    }

    /**
     * Notifies users that a user has left a <code>ChatRoom</code>.
     *
     * @param room   - the <code>ChatRoom</code> that a user has left.
     * @param userid - the userid of the person.
     */
    protected void fireUserHasLeft(final ChatRoom room, final String userid) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final Iterator iter = new HashSet(chatRoomListeners).iterator();
                while (iter.hasNext()) {
                    ((ChatRoomListener)iter.next()).userHasLeft(room, userid);
                }
            }
        });

    }

    /**
     * Starts flashing of MainWindow.
     *
     * @param room the ChatRoom to check if a message has been inserted
     *             but the room is not the selected room.
     */
    public void startFlashing(final ChatRoom room) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    final int index = indexOfComponent(room);
                    if (index != -1) {
                        // Check notifications.
                        if (SettingsManager.getLocalPreferences().isChatRoomNotificationsOn() || !(room instanceof GroupChatRoom)) {
                            checkNotificationPreferences(room);
                        }

                        // Notify decorators
                        SparkManager.getChatManager().notifySparkTabHandlers(room);
                    }

                    boolean shouldFlash = SettingsManager.getLocalPreferences().isChatRoomNotificationsOn() || !(room instanceof GroupChatRoom);

                    if (!chatFrame.isFocused() && shouldFlash) {
                        SparkManager.getAlertManager().flashWindow(chatFrame);
                    }
                }
                catch (Exception ex) {
                    Log.error("Issue in ChatRooms with tab location.", ex);
                }
            }
        });
    }

    public void flashWindow(final Component component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    boolean invokeFlash = SettingsManager.getLocalPreferences().isChatRoomNotificationsOn() || !(component instanceof GroupChatRoom);

                    if (!chatFrame.isFocused() && invokeFlash) {
                        chatFrame.setFocusableWindowState(true);
                        SparkManager.getAlertManager().flashWindow(chatFrame);
                    }
                }
                catch (Exception ex) {
                    Log.error("Issue in ChatRooms with tab location.", ex);
                }
            }
        });
    }


    public void fireChatRoomStateUpdated(final ChatRoom room) {
        final int index = indexOfComponent(room);
        if (index != -1) {
            SparkTab tab = getTabAt(index);
            SparkManager.getChatManager().notifySparkTabHandlers(room);
        }
    }

    /**
     * Checks to see if the <code>ChatFrame</code> should stop flashing.
     *
     * @param component the component that should be notified.
     */
    public void stopFlashing(final Component component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Stop the flashing
                    SparkManager.getAlertManager().stopFlashing(chatFrame);

                    // Notify decorators
                    SparkManager.getChatManager().notifySparkTabHandlers(component);
                }
                catch (Exception ex) {
                    Log.error("Could not stop flashing because " + ex.getMessage(), ex);
                }


            }
        });
    }

    /**
     * Handles Notification preferences for incoming messages and rooms.
     *
     * @param room the chat room.
     */
    private void checkNotificationPreferences(final ChatRoom room) {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if (pref.getWindowTakesFocus()) {
            chatFrame.setState(Frame.NORMAL);
            chatFrame.setVisible(true);
        }

        if (pref.getShowToasterPopup()) {
            SparkToaster toaster = new SparkToaster();
            toaster.setCustomAction(new AbstractAction() {
                public void actionPerformed(ActionEvent actionEvent) {
                    chatFrame.setState(Frame.NORMAL);
                    chatFrame.setVisible(true);
                    int tabLocation = indexOfComponent(room);
                    if (tabLocation != -1) {
                        setSelectedIndex(tabLocation);
                    }
                }
            });

            toaster.setDisplayTime(5000);
            toaster.setBorder(BorderFactory.createBevelBorder(0));

            String nickname = nickname = room.getRoomTitle();
            toaster.setTitle(nickname);
            toaster.setToasterHeight(150);
            toaster.setToasterWidth(200);


            int size = room.getTranscripts().size();
            if (size > 0) {
                Message message = (Message)room.getTranscripts().get(size - 1);

                toaster.showToaster(room.getTabIcon(), message.getBody());
            }
        }
    }

    public void setChatRoomTitle(ChatRoom room, String title) {
        int index = indexOfComponent(room);
        if (index != -1) {
            SparkTab tab = getTabAt(index);
            fireChatRoomStateUpdated(room);
            tab.setTabTitle(room.getTabTitle());
        }
    }

    private void createFrameIfNeeded() {
        if (chatFrame != null) {
            return;
        }
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if (pref.isDockingEnabled()) {
            chatFrame = MainWindow.getInstance();
        }
        else {
            chatFrame = new ChatFrame();
        }

        // The ultimate workground for 1.6
        chatFrame.dispose();
        chatFrame.setFocusableWindowState(false);
        chatFrame.setFocusableWindowState(true);
        chatFrame.dispose();


        chatFrame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent windowEvent) {
                stopFlashing();
                int sel = getSelectedIndex();
                if (sel == -1) {
                    return;
                }
                final ChatRoom room;
                try {
                    room = getChatRoom(sel);
                    focusChat();

                    // Set the title of the room.
                    chatFrame.setTitle(room.getRoomTitle());
                }
                catch (ChatRoomNotFoundException e1) {
                }

            }

            public void windowDeactivated(WindowEvent windowEvent) {
            }


            public void windowClosing(WindowEvent windowEvent) {
                // Save layout
                chatFrame.saveLayout();

                SparkManager.getChatManager().getChatContainer().closeAllChatRooms();
            }
        });

        // Start timer
        handleStaleChats();
    }


    public void focusChat() {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e1) {
                    Log.error(e1);
                }
                return "ok";
            }

            public void finished() {
                try {
                    //chatFrame.requestFocus();
                    ChatRoom chatRoom = getActiveChatRoom();
                    chatRoom.requestFocusInWindow();
                    chatRoom.getChatInputEditor().requestFocusInWindow();
                }
                catch (ChatRoomNotFoundException e1) {
                    // Ignore. There may legitamtly not be a chat room.
                }
            }
        };
        worker.start();

    }

    public Collection<ChatRoom> getChatRooms() {
        return new ArrayList<ChatRoom>(chatRoomList);
    }

    public ChatFrame getChatFrame() {
        return chatFrame;
    }

    public void blinkFrameIfNecessary(final JFrame frame) {

        final MainWindow mainWindow = SparkManager.getMainWindow();

        if (mainWindow.isFocused()) {
            frame.setVisible(true);
            return;
        }
        else {
            // Set to new tab.
            if (Spark.isWindows()) {
                frame.setState(Frame.ICONIFIED);
                chatFrame.setFocusableWindowState(true);
                SparkManager.getAlertManager().flashWindow(frame);

                frame.setVisible(true);
                frame.addWindowListener(new WindowAdapter() {
                    public void windowActivated(WindowEvent e) {
                        SparkManager.getAlertManager().stopFlashing(frame);
                    }
                });
            }
        }
    }

    /**
     * Returns a Collection of stale chat rooms.
     *
     * @return a collection of stale chat rooms.
     */
    public Collection<ChatRoom> getStaleChatRooms() {
        final List<ChatRoom> staleRooms = new ArrayList<ChatRoom>();
        for (ChatRoom chatRoom : getChatRooms()) {
            long lastActivity = chatRoom.getLastActivity();
            long currentTime = System.currentTimeMillis();

            long diff = currentTime - lastActivity;
            int minutes = (int)(diff / (60 * 1000F));

            LocalPreferences pref = SettingsManager.getLocalPreferences();
            int timeoutMinutes = pref.getChatLengthDefaultTimeout();

            int unreadCount = chatRoom.getUnreadMessageCount();

            if (timeoutMinutes <= minutes && unreadCount == 0) {
                staleRooms.add(chatRoom);
            }
        }

        return staleRooms;
    }

    private void checkTabPopup(MouseEvent e) {
        final SparkTab tab = (SparkTab)e.getSource();
        if (!e.isPopupTrigger()) {
            return;
        }

        final JPopupMenu popup = new JPopupMenu();

        // Handle closing this room.
        Action closeThisAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ChatRoom chatRoom = (ChatRoom)getComponentInTab(tab);
                if (chatRoom != null) {
                    closeTab(chatRoom);
                }
            }
        };
        closeThisAction.putValue(Action.NAME, Res.getString("message.close.this.chat"));
        popup.add(closeThisAction);


        if (getChatRooms().size() > 1) {
            // Handle closing other rooms.
            Action closeOthersAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    ChatRoom chatRoom = (ChatRoom)getComponentInTab(tab);
                    if (chatRoom != null) {
                        for (ChatRoom cRoom : getChatRooms()) {
                            if (chatRoom != cRoom) {
                                closeTab(cRoom);
                            }
                        }
                    }
                }
            };

            closeOthersAction.putValue(Action.NAME, Res.getString("message.close.other.chats"));
            popup.add(closeOthersAction);

            Action closeOldAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    for (ChatRoom rooms : getStaleChatRooms()) {
                        closeTab(rooms);
                    }
                }
            };
            closeOldAction.putValue(Action.NAME, Res.getString("message.close.stale.chats"));
            popup.add(closeOldAction);

        }


        popup.show(tab, e.getX(), e.getY());

    }

    /**
     * Checks every room every 30 seconds to see if it's timed out.
     */
    private void handleStaleChats() {
        int delay = 1000;   // delay for 1 minute
        int period = 60000;  // repeat every 30 seconds.
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (ChatRoom chatRoom : getStaleChatRooms()) {
                    // Notify decorators
                    SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
                }
            }
        }, delay, period);
    }

}
