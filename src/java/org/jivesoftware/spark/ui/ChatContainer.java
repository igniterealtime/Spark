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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.regex.Pattern;

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
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPaneListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * Contains all <code>ChatRoom</code> objects within Spark.
 *
 * @author Derek DeMoro
 */
public class ChatContainer extends SparkTabbedPane implements MessageListener, ChangeListener, KeyListener {
	private static final long serialVersionUID = 3725711237490056136L;

	/**
     * List of all ChatRoom Listeners.
     */
    private final List<ChatRoomListener> chatRoomListeners = new ArrayList<ChatRoomListener>();
    private final List<ChatRoom> chatRoomList = new ArrayList<ChatRoom>();
    private final Map<String, PacketListener> presenceMap = new HashMap<String, PacketListener>();
    private static final String WELCOME_TITLE = SparkRes.getString(SparkRes.WELCOME);
    private ChatFrame chatFrame;
    private LocalPreferences localPref;
    private final TimerTask focusTask;


    /**
     * Creates the ChatContainer to hold all ChatRooms.
     */
    public ChatContainer() {
        // Assign location
        super(SettingsManager.getLocalPreferences().isTabTopPosition() ? JTabbedPane.TOP : JTabbedPane.BOTTOM);

        // Set minimum size
        setMinimumSize(new Dimension(400, 200));
        // Don't allow tabs to shrink and allow scrolling.

        enableDragAndDrop();
        
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

		if (chatFrame != null) {
		    chatFrame.setTitle("");
		    chatFrame.setVisible(false);
		    chatFrame = null;
		}
               
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

        // Create task for focusing chat.
        focusTask = new SwingTimerTask() {
            public void doRun() {
                try {
                    //chatFrame.requestFocus();
                    ChatRoom chatRoom = getActiveChatRoom();                    
                    chatRoom.getChatInputEditor().requestFocusInWindow();
                    updateActiveTab();
                  
                }
                catch (ChatRoomNotFoundException e1) {
                    // Ignore. There may legitamtly not be a chat room.
                }
            }
        };

    }


    
   
    /**
     * Adds navigation capability to chat rooms. Users can navigate using the alt-left or right arrow keys.
     */
    private void addKeyNavigation() {
        KeyStroke leftStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        String leftStrokeString = org.jivesoftware.spark.util.StringUtils.keyStroke2String(leftStroke);

        // Handle Left Arrow
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt " + leftStrokeString + ""), "navigateLeft");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("alt " + leftStrokeString + ""), "navigateLeft");
        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("alt " + leftStrokeString + ""), "navigateLeft");
        this.getActionMap().put("navigateLeft", new AbstractAction("navigateLeft") {
			private static final long serialVersionUID = -8677467560602512074L;

			public void actionPerformed(ActionEvent evt) {
                navigateLeft();
            }
        });

        KeyStroke rightStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        String rightStrokeString = org.jivesoftware.spark.util.StringUtils.keyStroke2String(rightStroke);

        // Handle Right Arrow
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt " + rightStrokeString + ""), "navigateRight");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("alt " + rightStrokeString + ""), "navigateRight");
        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("alt " + rightStrokeString + ""), "navigateRight");


        this.getActionMap().put("navigateRight", new AbstractAction("navigateRight") {
			private static final long serialVersionUID = -7676330627598261416L;

			public void actionPerformed(ActionEvent evt) {
                navigateRight();
            }
        });

        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");

        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("Ctrl W"), "escape");
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "escape");

        this.getActionMap().put("escape", new AbstractAction("escape") {
			private static final long serialVersionUID = 5165074248488666495L;

			public void actionPerformed(ActionEvent evt) {
                closeActiveRoom();
            }
        });

        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_MASK), "shiftCmdW");
        this.getActionMap().put("shiftCmdW", new AbstractAction("shiftCmdW") {
			private static final long serialVersionUID = -1179625099164632251L;

			public void actionPerformed(ActionEvent evt) {
                closeAllChatRooms();
            }
        });

        // Add KeyMappings
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "searchContacts");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control F"), "searchContacts");
        getActionMap().put("searchContacts", new AbstractAction("searchContacts") {
			private static final long serialVersionUID = -6904085783599775675L;

			public void actionPerformed(ActionEvent evt) {
			    SwingWorker worker = new SwingWorker() {
			        
			        @Override
			        public Object construct() {
			    	return 42;
			        }
			        @Override
			        public void finished() {
			            SparkManager.getUserManager()
			            .searchContacts("", SparkManager.getChatManager()
			        	    .getChatContainer().getChatFrame());
			        }
			    };
			    worker.start();
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

        room.registeredToFrame(chatFrame);
        
        SparkManager.getConnection().addPacketListener(myListener, presenceFilter);

        // Add to PresenceMap
        presenceMap.put(room.getRoomname(), myListener);

        String tooltip;
        if (room instanceof ChatRoomImpl) {
            tooltip = ((ChatRoomImpl)room).getParticipantJID();
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(((ChatRoomImpl)room).getParticipantJID());

            tooltip = "<html><body><b>Contact:&nbsp;</b>" + nickname + "<br><b>JID:&nbsp;</b>" + tooltip;
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


        final TimerTask visibleTask = new SwingTimerTask() {
            public void doRun() {
                checkVisibility(room);
            }
        };

        TaskEngine.getInstance().schedule(visibleTask, 100);

        // Add to ChatRoomList
        chatRoomList.add(room);

        // Notify users that the chat room has been opened.
        fireChatRoomOpened(room);

        // Focus Chat
        focusChat();

        // Add Room listeners to override issue with input maps and keybinding on the mac.
        if (Spark.isMac()) {
            room.getChatInputEditor().addKeyListener(this);
        }
    }

    public void addContainerComponent(ContainerComponent comp) {
        createFrameIfNeeded();

        addTab(comp.getTabTitle(), comp.getTabIcon(), comp.getGUI(), comp.getToolTipDescription());
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
        if (!chatFrame.isVisible() && SparkManager.getMainWindow().isFocusOwner()) {
            chatFrame.setState(Frame.NORMAL);
            chatFrame.setVisible(true);
        }
        else if (chatFrame.isVisible() && !chatFrame.isInFocus()) {
            startFlashing(component, false, null, null);
        }
        else if (chatFrame.isVisible() && chatFrame.getState() == Frame.ICONIFIED) {
            // Set to new tab.
            int tabLocation = indexOfComponent(component);
            setSelectedIndex(tabLocation);

            // If the ContactList is in the tray, we need better notification by flashing
            // the chatframe.
            startFlashing(component, false, null, null);
        }

        // Handle when chat frame is visible but the Contact List is not.
        else if (chatFrame.isVisible() && !SparkManager.getMainWindow().isVisible()) {
            startFlashing(component, false, null, null);
        }
        else if (!chatFrame.isVisible()) {
            // Set to new tab.
            int tabLocation = indexOfComponent(component);
            setSelectedIndex(tabLocation);

            if (Spark.isWindows()) {
                chatFrame.setFocusableWindowState(false);
                chatFrame.setState(Frame.ICONIFIED);
            }
            chatFrame.setFocusableWindowState(true);

            // If the ContactList is in the tray, we need better notification by flashing
            // the chatframe.
            if (!SparkManager.getMainWindow().isVisible()) {
                startFlashing(component, false, null, null);
            }
            else if (chatFrame.getState() == Frame.ICONIFIED) {
                startFlashing(component, false, null, null);
            }

            if (component instanceof ChatRoom) {
                chatFrame.setTitle(((ChatRoom)component).getRoomTitle());
            }
        }
    }

 
    
    private void handleMessageNotification(final ChatRoom chatRoom, boolean customMsg, String customMsgText, String customMsgTitle) {
        ChatRoom activeChatRoom = null;        
        boolean groupMessageChecked = false;
        try {
            activeChatRoom = getActiveChatRoom();
        }
        catch (ChatRoomNotFoundException e1) {
            Log.error(e1);
        }
                
        if (chatFrame.isVisible() && (chatFrame.getState() == Frame.ICONIFIED || chatFrame.getInactiveTime() > 20000)) {
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);
            groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
            return;
        } 
        if(chatFrame.isVisible() && chatFrame.getState()== Frame.NORMAL)
        {
            groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
            groupMessageChecked = true;
        }

        if (!chatFrame.isVisible() && SparkManager.getMainWindow().isFocusOwner()) {
            chatFrame.setState(Frame.NORMAL);
            chatFrame.setVisible(true);
        }
        else if (chatFrame.isVisible() && !chatFrame.isInFocus()) {
        	if (!groupMessageChecked) {
        		groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
        		groupMessageChecked = true;
        	}
        }
        else if (chatFrame.isVisible() && chatFrame.getState() == Frame.ICONIFIED) {
            // Set to new tab.
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);

            // If the ContactList is in the tray, we need better notification by flashing
            // the chatframe.
        	if (!groupMessageChecked) {
        		groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
        		groupMessageChecked = true;
        	}
        }

        // Handle when chat frame is visible but the Contact List is not.
        else if (chatFrame.isVisible() && !SparkManager.getMainWindow().isVisible() && !chatFrame.isInFocus()) {
        	if (!groupMessageChecked) {
        		groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
        		groupMessageChecked = true;
        	}
        }
        else if (!chatFrame.isVisible()) {
            // Set to new tab.
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);
    
            if (Spark.isWindows()) {
            	chatFrame.setExtendedState(Frame.ICONIFIED);
            }
            chatFrame.setVisible(true);
            
            // If the ContactList is in the tray, we need better notification by flashing
            // the chatframe.
            if (!SparkManager.getMainWindow().isVisible()) {
            	if (!groupMessageChecked) {
            		groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
            		groupMessageChecked = true;
            	}
            }
            else if (chatFrame.getState() == Frame.ICONIFIED) {
            	if (!groupMessageChecked) {
            		groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
            		groupMessageChecked = true;
            	}
            }

            chatFrame.setTitle(chatRoom.getRoomTitle());
        }
        else if (chatRoom != activeChatRoom) {
        	if (!groupMessageChecked) {
        		groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
        		groupMessageChecked = true;
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
            room.closeChatRoom();
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

        room.getChatInputEditor().removeKeyListener(this);

        // Clear all Text :)
        room.getTranscriptWindow().cleanup();
    }

    /**
     * Close all chat rooms.
     */
    public void closeAllChatRooms() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();

        if (MainWindow.getInstance().isDocked() || !pref.isAutoCloseChatRoomsEnabled()) {
            return;
        }

        for (ChatRoom chatRoom : new ArrayList<ChatRoom>(chatRoomList)) {
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

        final PacketListener listener = presenceMap.get(room.getRoomname());
        if (listener != null && SparkManager.getConnection().isConnected()) {
            SparkManager.getConnection().removePacketListener(listener);
        }
    }

    /**
     * Returns a ChatRoom by name.
     *
     * @param roomName the name of the ChatRoom.
     * @return the ChatRoom
     * @throws ChatRoomNotFoundException if the room was not found.
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
     * @throws ChatRoomNotFoundException thrown if the room is not found.
     */
    public ChatRoom getChatRoom(int location) throws ChatRoomNotFoundException {
        if (getTabCount() < location) {
        	throw new ChatRoomNotFoundException();
        }
        try {
            Component comp = getComponentAt(location);
            if (comp instanceof ChatRoom) {
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
        SparkManager.getWorkspace().getTranscriptPlugin().persistChatRoom(room);
        fireNotifyOnMessage(room, false, null, null);
        ChatManager.getInstance().fireMessageReceived(message);
    }
    
    /**
     * Used for Tray Notifications.
     *
     * @param chatRoom the ChatRoom where the message was received.
     * @param customMsg
     * @param customMsgText
     * @param customMsgTitle
     */    
    public void fireNotifyOnMessage(final ChatRoom chatRoom, final boolean customMsg, final String customMsgText, final String customMsgTitle) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                handleMessageNotification(chatRoom, customMsg, customMsgText, customMsgTitle);
            }
        });
    }

    /***
     *
     * @param room
     * @param message
     */
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
        }

        this.close(this.getTabAt(location));
    }

    public void closeActiveRoom() {
        ChatRoom room;
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
            }

            return;
        }

        // Confirm end session
        boolean isGroupChat = room.getChatType() == Message.Type.groupchat;
        if (isGroupChat) {
            final int ok = JOptionPane.showConfirmDialog(chatFrame, Res.getString("message.end.conversation"),
                    Res.getString("title.confirmation"), JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                room.closeChatRoom();
            }
        }
        else {
            room.closeChatRoom();
        }


    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (ChatRoom room : chatRoomList) {
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
        for (ChatRoomListener chatRoomListener : new ArrayList<ChatRoomListener>(chatRoomListeners)) {
            chatRoomListener.chatRoomOpened(room);
        }
    }

    /**
     * Notifies users that a <code>ChatRoom</code> has been left.
     *
     * @param room - the <code>ChatRoom</code> that has been left
     */
    protected void fireChatRoomLeft(ChatRoom room) {
        for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners)) {
            chatRoomListener.chatRoomLeft(room);
        }
    }

    /**
     * Notifies users that a <code>ChatRoom</code> has been closed.
     *
     * @param room - the <code>ChatRoom</code> that has been closed.
     */
    protected void fireChatRoomClosed(ChatRoom room) {
        for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners)) {
            chatRoomListener.chatRoomClosed(room);
        }
    }

    /**
     * Notifies users that a <code>ChatRoom</code> has been activated.
     *
     * @param room - the <code>ChatRoom</code> that has been activated.
     */
    protected void fireChatRoomActivated(ChatRoom room) {
        for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners)) {
            chatRoomListener.chatRoomActivated(room);
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
                for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners)) {
                    chatRoomListener.userHasJoined(room, userid);
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
                for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners)) {
                    chatRoomListener.userHasLeft(room, userid);
                }
            }
        });

    }

    /**
     * Starts flashing of MainWindow.
     *
     * @param comp the Component to check if a message has been inserted
     *             but the room is not the selected room.
     */
    public void startFlashing(final Component comp, final boolean customMsg,
	    final String customMsgText, final String customMsgTitle) {

	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    try {
			final int index = indexOfComponent(comp);
			if (index != -1) {
			    // Check notifications.
			    if (SettingsManager.getLocalPreferences().isChatRoomNotificationsOn()|| !(comp instanceof GroupChatRoom)) {
				if (comp instanceof ChatRoom) {
				    if (comp instanceof GroupChatRoom) {
					if (((GroupChatRoom) comp).getLastMessage() != null)
					    if (!((GroupChatRoom) comp).isBlocked(((GroupChatRoom) comp).getLastMessage().getFrom()))
						checkNotificationPreferences((ChatRoom) comp,customMsg,customMsgText,customMsgTitle);
				    } else {
					checkNotificationPreferences((ChatRoom) comp, customMsg,customMsgText, customMsgTitle);
				    }
				}
			    }
			    // Notify decorators
			    SparkManager.getChatManager().notifySparkTabHandlers(comp);
			}

			boolean flashAllowed = SettingsManager.getLocalPreferences().isChatRoomNotificationsOn() 
						|| !(comp instanceof GroupChatRoom);

			if (!chatFrame.isInFocus() && flashAllowed) {
			    SparkManager.getNativeManager().flashWindow(chatFrame);
			}
		    } catch (Exception ex) {
			Log.error("Issue in ChatRooms with tab location.", ex);
		    }
		}
	    });
    }


    public void fireChatRoomStateUpdated(final ChatRoom room) {
        final int index = indexOfComponent(room);
        if (index != -1) {
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
                    SparkManager.getNativeManager().stopFlashing(chatFrame);

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
     * @param fileTransfer if it is a file transfer then true
     * @param fileTName the file name being transfered (if fileTransfer applies)
     *
     */
    private void checkNotificationPreferences(final ChatRoom room, boolean customMsg, String customMsgText, String customMsgTitle) {
        LocalPreferences pref = SettingsManager.getLocalPreferences();

        if (pref.getWindowTakesFocus()) {
            chatFrame.setState(Frame.NORMAL);
            chatFrame.setVisible(true);
        }

        if (pref.getShowToasterPopup()) {
            SparkToaster toaster = new SparkToaster();
            toaster.setCustomAction(new AbstractAction() {
				private static final long serialVersionUID = -2759404307378067515L;

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
            
            String nickname = room.getRoomTitle();
            toaster.setToasterHeight(150);
            toaster.setToasterWidth(200);


            int size = room.getTranscripts().size();
            if(customMsg) {
                toaster.setTitle(customMsgTitle);
                toaster.showToaster(room.getTabIcon(), customMsgText);
            } else {
                toaster.setTitle(nickname);
                if (size > 0) {
                    Message message = room.getTranscripts().get(size - 1);
                    toaster.showToaster(room.getTabIcon(), message.getBody());
                }
            }
        }
    }

     /**
     * Performs several group chat checks
     *
     * chatRoom the chat room that needs to be passed
     * customMsg whether or not this is a custom message
     * customMsgText if any custom message should appear in the popup
     * customMsgTitle whether or not the toaster should have any popup
     *
     **/
    private void groupChatMessageCheck(ChatRoom chatRoom, boolean customMsg, String customMsgText, String customMsgTitle) {
        // predefine if this is a group chat message or not
        localPref = SettingsManager.getLocalPreferences();
        boolean isGroupChat = chatRoom.getChatType() == Message.Type.groupchat;
        int size = chatRoom.getTranscripts().size();
        
        // is this a group chat message and is my name in it?
        if (isGroupChat) {
            // is a group chat, perform some functions
            String fromNickName="";
            Message lastChatMessage= new Message();
            String mucNickNameT="";
            String finalRoomName ="";
            if(size>0)
            {
                lastChatMessage = chatRoom.getTranscripts().get(size - 1);
                mucNickNameT = lastChatMessage.getFrom();
                String[] mucNickName = mucNickNameT.split("/");    
                finalRoomName = chatRoom.getRoomTitle();
                if (mucNickName.length < 2) { // We have no name after "/" in mucNickNameT (must be like: test@conference.jabber.kg/kos)
                    fromNickName = finalRoomName; //Res.getString("label.message");
                } else {
                    fromNickName = mucNickName[1];
                }
            }
            if (localPref.isMucHighToastEnabled()) {
                // allowed to check for new messages containing name
                String myNickName = chatRoom.getNickname();
                String myUserName = SparkManager.getSessionManager().getUsername();
                Pattern usernameMatch = Pattern.compile(myUserName, Pattern.CASE_INSENSITIVE);
                Pattern nicknameMatch = Pattern.compile(myNickName, Pattern.CASE_INSENSITIVE);
                
                if (usernameMatch.matcher(lastChatMessage.getBody()).find() || nicknameMatch.matcher(lastChatMessage.getBody()).find()) {
                    // match, send new message
                    boolean customMsgS = true;
                    String customMsgTextS = Res.getString("group.chat.name.match") + " " + finalRoomName + " by " + fromNickName + " (" + lastChatMessage.getBody() + ")";
                    String customMsgTitleS = Res.getString("group.chat.name.notification");
                       
                    startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
                    return;
                } else {
                    // regular group message
                    boolean customMsgS = true;
                    String customMsgTextS = fromNickName + " says: " + lastChatMessage.getBody();
                    String customMsgTitleS = finalRoomName;
                    
                    startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
                    return;
                }
            } else {
                // regular group message
                boolean customMsgS = true;
                String customMsgTextS = fromNickName + " says: " + lastChatMessage.getBody();
                String customMsgTitleS = finalRoomName;
                
                startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
                return;
            }
        } else if (customMsg) {
            // probablt a file transfer request
            boolean customMsgS = customMsg;
            String customMsgTextS = customMsgText;
            String customMsgTitleS = customMsgTitle;
            
            startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
            return;
        } else {
            // normal personal chat
        	Message lastChatMessage = null;
        	if(size > 0)
        		lastChatMessage = chatRoom.getTranscripts().get(size - 1);
            String finalRoomName = chatRoom.getRoomTitle();
            
            String customMsgTextS = "";
            boolean customMsgS = true;
            String customMsgTitleS = finalRoomName;
            
            if(lastChatMessage != null) {
            	customMsgTextS = lastChatMessage.getBody();
            }

            startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
            return;
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
	} else {
	    chatFrame = new ChatFrame();
	}
	
        
	if (SparkManager.getMainWindow().isActive() || pref.getWindowTakesFocus()) {
	    chatFrame.setState(Frame.NORMAL);

	} else {
	    if (System.getProperty("java.version").startsWith("1.7.")) {
		try {
		    //	TODO UPDATE ON JAVA 1.7.0 release
		    // chatFrame.setAutoRequestFocus(false);
		    
		    // This can be removed once java 1.7 is mainstream  
		    Class<?> c = ClassLoader.getSystemClassLoader().loadClass(
			    JFrame.class.getCanonicalName());
		    Method m = c
			    .getMethod("setAutoRequestFocus", boolean.class);
		    m.invoke(chatFrame, false);
		} catch (Exception e) {
		    Log.error(e);
		}
	    }
	    chatFrame.setState(Frame.ICONIFIED);
	}
      
        
        chatFrame.setVisible(true);
        
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
                    // Nothing to do
                }

            }

            public void windowDeactivated(WindowEvent windowEvent) {
            }


	    public void windowClosing(WindowEvent windowEvent) {
		// Save layout
		    chatFrame.saveLayout();
		    SparkManager.getChatManager().getChatContainer()
			    .closeAllChatRooms();

		}
        });

        // Start timer
        handleStaleChats();
    }


    /**
     * Brings the chat into focus.
     */
    public void focusChat() {


        TaskEngine.getInstance().schedule(focusTask, 50);
    }

    public Collection<ChatRoom> getChatRooms() {
        return new ArrayList<ChatRoom>(chatRoomList);
    }

    public ChatFrame getChatFrame() {
        return chatFrame;
    }

    public void blinkFrameIfNecessary(final JFrame frame) {

        final MainWindow mainWindow = SparkManager.getMainWindow();

        if (mainWindow.isFocusOwner()) {
            frame.setVisible(true);
        }
        else {
            // Set to new tab.
            if (Spark.isWindows()) {
                frame.setState(Frame.ICONIFIED);
                SparkManager.getNativeManager().flashWindow(frame);

                frame.setVisible(true);
                frame.addWindowListener(new WindowAdapter() {
                    public void windowActivated(WindowEvent e) {
                        SparkManager.getNativeManager().stopFlashing(frame);
                    }
                });
            }
        }
    }


    private void checkTabPopup(MouseEvent e) {
        final SparkTab tab = (SparkTab)e.getSource();
        if (!e.isPopupTrigger()) {
            return;
        }

        final JPopupMenu popup = new JPopupMenu();

        // Handle closing this room.
        Action closeThisAction = new AbstractAction() {
			private static final long serialVersionUID = 5002889397735856123L;

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
				private static final long serialVersionUID = 1869236917427431585L;

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
				private static final long serialVersionUID = 1L;

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

    /**
     * Checks every room every 30 seconds to see if it's timed out.
     */
    private void handleStaleChats() {
        int delay = 1000;   // delay for 1 second.
        int period = 60000;  // repeat every minute.

        final TimerTask task = new SwingTimerTask() {
            public void doRun() {
                for (ChatRoom chatRoom : getStaleChatRooms()) {
                    // Notify decorators
                    SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
                }
            }
        };


        TaskEngine.getInstance().scheduleAtFixedRate(task, delay, period);
    }

    private void navigateRight() {
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

    private void navigateLeft() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex > 0) {
            setSelectedIndex(selectedIndex - 1);
        }
        else {
            setSelectedIndex(getTabCount() - 1);
        }
    }

    // Handle key listener events for mac only :)
    public void keyTyped(KeyEvent keyEvent) {
        // Nothing to do.
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isMetaDown()) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
                navigateRight();
            }
            else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
                navigateLeft();
            }

        }
    }

    public void keyReleased(KeyEvent keyEvent) {
        // Nothing to do.
    }

    /**
     * Returns the total number of unread messages in Spark.
     *
     * @return the total number of unread messages in Spark.
     */
    public int getTotalNumberOfUnreadMessages() {
        int messageCount = 0;
        for (ChatRoom chatRoom : chatRoomList) {
            messageCount += chatRoom.getUnreadMessageCount();
        }

        return messageCount;
    }
}

