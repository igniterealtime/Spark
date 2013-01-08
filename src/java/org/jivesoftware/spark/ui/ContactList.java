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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jivesoftware.MainWindowListener;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.LastActivityManager;
import org.jivesoftware.smackx.SharedGroupManager;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.InputDialog;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;


public class ContactList extends JPanel implements ActionListener,
	ContactGroupListener, Plugin, RosterListener, ConnectionListener {

    private static final long serialVersionUID = -4391111935248627078L;
    private JPanel mainPanel = new JPanel();
    private JScrollPane contactListScrollPane;
    private final List<ContactGroup> groupList = new ArrayList<ContactGroup>();
    private final RolloverButton addingGroupButton;

    private ContactItem activeItem;
    private ContactGroup activeGroup;
    private ContactGroup unfiledGroup;

    
    // Create Menus
    private JMenuItem addContactMenu;
    private JMenuItem addContactGroupMenu;
    private JMenuItem removeContactFromGroupMenu;
    private JMenuItem chatMenu;
    private JMenuItem renameMenu;

    private ContactGroup offlineGroup;
    private final JCheckBoxMenuItem showHideMenu = new JCheckBoxMenuItem();
    private final JCheckBoxMenuItem showOfflineGroupMenu = new JCheckBoxMenuItem();
    private final JCheckBoxMenuItem showOfflineUsersMenu = new JCheckBoxMenuItem();
    
    private List<String> sharedGroups = new ArrayList<String>();

    private final List<ContextMenuListener> contextListeners = new ArrayList<ContextMenuListener>();

    private List<Presence> initialPresences = new ArrayList<Presence>();
    private final Timer presenceTimer = new Timer();
    private final List<FileDropListener> dndListeners = new ArrayList<FileDropListener>();
    private final List<ContactListListener> contactListListeners = new ArrayList<ContactListListener>();
    private Properties props;
    private File propertiesFile;

    private LocalPreferences localPreferences;
    
    private ContactItem contactItem;
    
    private String name, user;


    public static final String RETRY_PANEL = "RETRY_PANEL";


    private ReconnectPanel _reconnectPanel;
    private ReconnectPanelSmall _reconnectpanelsmall;
    private ReconnectPanelIcon _reconnectpanelicon;

    private Workspace workspace;

    public static KeyEvent activeKeyEvent;

    /**
     * Creates a new instance of ContactList.
     */
    public ContactList() {
        // Load Local Preferences
        localPreferences = SettingsManager.getLocalPreferences();

        offlineGroup = UIComponentRegistry.createContactGroup(Res.getString("group.offline"));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        addContactMenu = new JMenuItem(Res.getString("menuitem.add.contact"), SparkRes.getImageIcon(SparkRes.USER1_ADD_16x16));
        addContactGroupMenu = new JMenuItem(Res.getString("menuitem.add.contact.group"), SparkRes.getImageIcon(SparkRes.SMALL_ADD_IMAGE));

        removeContactFromGroupMenu = new JMenuItem(Res.getString("menuitem.remove.from.group"), SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        chatMenu = new JMenuItem(Res.getString("menuitem.start.a.chat"), SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
        renameMenu = new JMenuItem(Res.getString("menuitem.rename"), SparkRes.getImageIcon(SparkRes.DESKTOP_IMAGE));

        addContactMenu.addActionListener(this);
        removeContactFromGroupMenu.addActionListener(this);
        chatMenu.addActionListener(this);
        renameMenu.addActionListener(this);
        

        setLayout(new BorderLayout());

        addingGroupButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.ADD_CONTACT_IMAGE));

        RolloverButton groupChatButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.JOIN_GROUPCHAT_IMAGE));
        toolbar.add(addingGroupButton);
        toolbar.add(groupChatButton);

        addingGroupButton.addActionListener(this);

        mainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        mainPanel.setBackground((Color)UIManager.get("ContactItem.background"));
        contactListScrollPane = new JScrollPane(mainPanel);
        contactListScrollPane.setAutoscrolls(true);

        contactListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contactListScrollPane.getVerticalScrollBar().setBlockIncrement(200);
        contactListScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        _reconnectPanel = new ReconnectPanel();
        
        _reconnectpanelsmall = new ReconnectPanelSmall(Res.getString("button.reconnect2"));
        
        _reconnectpanelicon = new ReconnectPanelIcon();

        workspace = SparkManager.getWorkspace();

        workspace.getCardPanel().add(RETRY_PANEL, _reconnectPanel);


        add(contactListScrollPane, BorderLayout.CENTER);
              

        // Load Properties file
        props = new Properties();
        // Save to properties file.
        propertiesFile = new File(Spark.getSparkUserHome() + "/groups.properties");
        try {
            props.load(new FileInputStream(propertiesFile));
        }
        catch (IOException e) {
            // File does not exist.
        }

        // Add ActionListener(s) to menus
//        addContactGroup(unfiledGroup);
        addContactGroup(offlineGroup);

        showHideMenu.setSelected(false);

        // Add KeyMappings
        SparkManager.getMainWindow().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F"), "searchContacts");
        SparkManager.getMainWindow().getRootPane().getActionMap().put("searchContacts", new AbstractAction("searchContacts") {
			private static final long serialVersionUID = -5956142123453578689L;

			public void actionPerformed(ActionEvent evt) {
                SparkManager.getUserManager().searchContacts("", SparkManager.getMainWindow());
            }
        });

        // Handle Command-F on Macs
        SparkManager.getMainWindow().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "appleStrokeF");
        SparkManager.getMainWindow().getRootPane().getActionMap().put("appleStrokeF", new AbstractAction("appleStrokeF") {
			private static final long serialVersionUID = 7883006402414136652L;

			public void actionPerformed(ActionEvent evt) {
                SparkManager.getUserManager().searchContacts("", SparkManager.getMainWindow());
            }
        });

        // Save state on shutdown.
        SparkManager.getMainWindow().addMainWindowListener(new MainWindowListener() {
            public void shutdown() {
                saveState();
            }

            public void mainWindowActivated() {

            }

            public void mainWindowDeactivated() {

            }
        });

        SparkManager.getConnection().addConnectionListener(this);

        // Get command panel and add View Online/Offline, Add Contact
//        StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();

//        final JPanel commandPanel = SparkManager.getWorkspace().getCommandPanel(); 
//
//
//        final RolloverButton addContactButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.USER1_ADD_16x16));
//        if (!Default.getBoolean(Default.ADD_CONTACT_DISABLED)) {
//        	commandPanel.add(addContactButton);
//        }
//        addContactButton.setToolTipText(Res.getString("message.add.a.contact"));
//        addContactButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                new RosterDialog().showRosterDialog();
//            }
//        });
        
    }
    
    /**
     * Switches all users to Offline and Creates a Reconnection Group
     */
    private synchronized void switchAllUserOffline(final boolean onError)
    {
	SwingWorker worker = new SwingWorker() {
	    
	    @Override
	    public Object construct() { 
		mainPanel.add(_reconnectpanelsmall,0);	
		_reconnectpanelsmall.setClosedOnError(onError);
		final Collection<RosterEntry> roster = SparkManager.getConnection().getRoster().getEntries();
		
		for(RosterEntry r : roster)
		{
		    Presence p = new Presence(Presence.Type.unavailable);
		    moveToOfflineGroup(p, r.getUser());
		}
		return true;
	    }
	};	
	worker.start();
    }

    /**
     * Switches all Users to Offline and Creates an Icon in the CommandBar
     */
    private synchronized void switchAllUserOfflineNoGroupEntry(final boolean onError) {
	SwingWorker worker = new SwingWorker() {
	    @Override
	    public Object construct() {
		_reconnectpanelicon.getPanel().add(_reconnectpanelicon.getButton(), 0);
		_reconnectpanelicon.getPanel().revalidate();
		_reconnectpanelicon.setClosedOnError(onError);
		final Collection<RosterEntry> roster = SparkManager
			.getConnection().getRoster().getEntries();

		for (RosterEntry r : roster) {
		    Presence p = new Presence(Presence.Type.unavailable);
		    moveToOfflineGroup(p, r.getUser());
		}
		return true;
	    }
	};
	worker.start();
    }

    /**
     * Updates the users presence.
     *
     * @param presence the user to update.
     * @throws Exception if there is a problem while updating the user's presence.
     */
    private synchronized void updateUserPresence(Presence presence) throws Exception {
        if (presence.getError() != null) {
            // We ignore this.
            return;
        }

        final Roster roster = SparkManager.getConnection().getRoster();

        final String bareJID = StringUtils.parseBareAddress(presence.getFrom());

        RosterEntry entry = roster.getEntry(bareJID);
        boolean isPending = entry != null && (entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from)
            && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus();

        // If online, check to see if they are in the offline group.
        // If so, remove from offline group and add to all groups they
        // belong to.

        if (presence.getType() == Presence.Type.available && offlineGroup.getContactItemByJID(bareJID) != null || (presence.getFrom().indexOf("workgroup.") != -1)) {
            changeOfflineToOnline(bareJID, entry, presence);
        }
        else if (presence.getType() == Presence.Type.available) {
            updateContactItemsPresence(presence, entry, bareJID);
        }
        else if (presence.getType() == Presence.Type.unavailable && !isPending) {
            // If not available, move to offline group.
            Presence rosterPresence = PresenceManager.getPresence(bareJID);
            if (!rosterPresence.isAvailable()) {
                moveToOfflineGroup(presence, bareJID);
            }
            else {
                updateContactItemsPresence(rosterPresence, entry, bareJID);
            }
        }
        
    }

    /**
     * Updates the presence of one individual based on their JID.
     *
     * @param presence the users presence.
     * @param entry    the roster entry being updated.
     * @param bareJID  the bare jid of the user.
     */
    private void updateContactItemsPresence(Presence presence, RosterEntry entry, String bareJID) {
        for (ContactGroup group : groupList) {
            ContactItem item = group.getContactItemByJID(bareJID);
            if (item != null) {
                if (group == offlineGroup) {
                    changeOfflineToOnline(bareJID, entry, presence);
                    continue;
                }
                item.setPresence(presence);
                group.fireContactGroupUpdated();
            }
        }
    }

    /**
     * Moves every <code>ContactItem</code> associated with the given bareJID to offline.
     *
     * @param presence the users presence.
     * @param bareJID  the bareJID of the user.
     */
    private void moveToOfflineGroup(final Presence presence, final String bareJID) {
        for (ContactGroup grpItem : new ArrayList<ContactGroup>(groupList)) {
            final ContactGroup group = grpItem;
            final ContactItem item = group.getContactItemByJID(bareJID);
            if (item != null) {
                int numberOfMillisecondsInTheFuture = 3000;
                Date timeToRun = new Date(System.currentTimeMillis() + numberOfMillisecondsInTheFuture);

                // Only run through if the users presence was online before.
                if (item.getPresence().isAvailable()) {
                    item.showUserGoingOfflineOnline();
                    item.setIcon(SparkRes.getImageIcon(SparkRes.CLEAR_BALL_ICON));
                    group.fireContactGroupUpdated();

                    final Timer offlineTimer = new Timer();
                    offlineTimer.schedule(new TimerTask() {
                        public void run() {
                            // Check to see if the user is offline, if so, move them to the offline group.
                            Presence userPresence = PresenceManager.getPresence(bareJID);
                            if (userPresence.isAvailable()) {
                                return;
                            }

                            item.setPresence(presence);

                            // Check for ContactItemHandler.
                            group.removeContactItem(item);
                            checkGroup(group);

                            if (offlineGroup.getContactItemByJID(item.getJID()) == null) {
                                moveToOffline(item);
                                offlineGroup.fireContactGroupUpdated();
                            }
                        }
                    }, timeToRun);
                }
            } else {
                final ContactItem offlineItem = offlineGroup.getContactItemByJID(bareJID);
                if (offlineItem != null) {
                    offlineItem.setPresence(presence);
                }
            }
        }
    }

    /**
     * Moves a user to each group they belong to.
     *
     * @param bareJID  the bareJID of the user to show as online.
     * @param entry    the <code>RosterEntry</code> of the user.
     * @param presence the users presence.
     */
    private void changeOfflineToOnline(String bareJID, final RosterEntry entry, Presence presence) {
        // Move out of offline group. Add to all groups.
        final ContactItem offlineItem = offlineGroup.getContactItemByJID(bareJID);

        if (offlineItem == null) {
            return;
        }
        offlineGroup.removeContactItem(offlineItem);

        // Add To all groups it belongs to.
        boolean isFiled = false;

        for (RosterGroup rosterGroup : entry.getGroups()) {
            isFiled = true;
            ContactGroup contactGroup = getContactGroup(rosterGroup.getName());
            if (contactGroup == null) {
                // Create Contact Group
                contactGroup = addContactGroup(rosterGroup.getName());
            }

            if (contactGroup != null) {
                ContactItem changeContactItem = null;
                if (contactGroup.getContactItemByJID(entry.getUser()) == null) {
                	ContactItem offlineCurrentItem = contactGroup.getOfflineContactItemByJID(bareJID);
                	//prevents from duplicating roster contacts when users going offline and online with Offline Group invisible
                	contactGroup.removeContactItem(offlineCurrentItem);

                    // If we are reconnecting we have to check if we are on the
                    // dispatch thread
                    if (EventQueue.isDispatchThread()) {

                        changeContactItem = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
                        contactGroup.addContactItem(changeContactItem);
                        changeContactItem.setAvailable(true);
                        changeContactItem.setPresence(presence);                        
                        changeContactItem.updateAvatarInSideIcon();
                        changeContactItem.showUserComingOnline();                       
                        changeContactItem.setSpecialIcon(offlineItem.getSpecialImageLabel().getIcon());
                        //contactItem.updatePresenceIcon(contactItem.getPresence());
                        toggleGroupVisibility(contactGroup.getGroupName(), true);
                        //contactGroup.fireContactGroupUpdated();
                        
                        int numberOfMillisecondsInTheFuture = 5000;
                        Date timeToRun = new Date(System.currentTimeMillis()
                                + numberOfMillisecondsInTheFuture);
                        Timer timer = new Timer();

                        final ContactItem staticItem = changeContactItem;
                        final ContactGroup staticGroup = contactGroup;
                        timer.schedule(new TimerTask() {
                            public void run() {
                                staticItem.updatePresenceIcon(staticItem.getPresence());
                                staticGroup.fireContactGroupUpdated();
                            }
                        }, timeToRun);

                    } else {

                        final ContactGroup staticContactGroup = contactGroup;
                        final Presence staticItemPrecense = presence;
                        
                        //Reconnection and not in dispatch Thread -> Add to EVentQueue
                        EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {

                                ContactItem changeContact = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
                                staticContactGroup.addContactItem(changeContact);
                                changeContact.setPresence(staticItemPrecense);
                                changeContact.setAvailable(true);
                                changeContact.updateAvatarInSideIcon();
                                changeContact.showUserComingOnline();
                                changeContact.setSpecialIcon(offlineItem.getSpecialImageLabel().getIcon());
                                changeContact.updatePresenceIcon(changeContact.getPresence());
                                toggleGroupVisibility(staticContactGroup.getGroupName(), true);
                                staticContactGroup.fireContactGroupUpdated();
                                
                            }

                      });
                    }
                }
           }
        }

        if (!isFiled) {

            
            if (unfiledGroup.getContactItemByJID(entry.getUser()) == null) {
                // If we are reconnecting we have to check if we are on the
                // dispatch thread
                if (EventQueue.isDispatchThread()) {

                    contactItem = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
                    ContactGroup unfiledGrp = getUnfiledGroup();
                    unfiledGrp.addContactItem(contactItem);
                    contactItem.setPresence(presence);
                    contactItem.setAvailable(true);
                    unfiledGrp.setVisible(true);
                    unfiledGrp.fireContactGroupUpdated();



                    

                } else {
                    final Presence staticItemPrecense = presence;
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            contactItem = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
                            ContactGroup unfiledGrp = getUnfiledGroup();
                            
                            contactItem.setPresence(staticItemPrecense);
                            contactItem.setAvailable(true);
                            unfiledGrp.addContactItem(contactItem);
                            contactItem.updatePresenceIcon(contactItem.getPresence());
                            unfiledGrp.fireContactGroupUpdated();


                        }
                    });

                }

            }
        }
    }

    /**
     * Called to build the initial ContactList.
     */
    private void buildContactList() {
        XMPPConnection con = SparkManager.getConnection();
        final Roster roster = con.getRoster();


        roster.addRosterListener(this);

        // Add All Groups to List
        for (RosterGroup group : roster.getGroups()) {
            addContactGroup(group.getName());
        }

        for (RosterGroup group : roster.getGroups()) {
        	
        	if(group.getName() == null || group.getName() == ""){
        		for(RosterEntry entry : group.getEntries()){
        			
				ContactItem buildContactItem = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
                    moveToOffline(buildContactItem);
        		}
        	}else{
        		
	            ContactGroup contactGroup = getContactGroup(group.getName());
	            if (contactGroup == null) {
	                contactGroup = getUnfiledGroup();
	            }

	            for (RosterEntry entry : group.getEntries()) {
	            	contactItem = null;
	            	name = entry.getName();
	            	user = entry.getUser();
	            	// in case of connection lost, the creation must be done in eventqueue
	            	if(EventQueue.isDispatchThread()) {
				contactItem = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
	            	}
	            	else {
	            		try {
	            			EventQueue.invokeAndWait(new Runnable(){
	            				public void run() {
							contactItem = UIComponentRegistry.createContactItem(name, null, user);
	            				}
	            			});
	            		} catch(Exception ex) {
	            			ex.printStackTrace();
	            		}
	            	}
	            		
	            	// if there was something wrong, try an other
	            	if(contactItem == null)
	            		continue;
	            	
	                contactItem.setPresence(new Presence(Presence.Type.unavailable));
	                if ((entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from)
	                    && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus()) {
	                    // Add to contact group.
	                    contactGroup.addContactItem(contactItem);
	                    contactGroup.setVisible(true);
	                }
	                else {
	                    if (offlineGroup.getContactItemByJID(entry.getUser()) == null) {
	                        moveToOffline(contactItem);
	                    }
	                }
            	}
            }
        }

        if (EventQueue.isDispatchThread()) {
            // Add Unfiled Group
            for (RosterEntry entry : roster.getUnfiledEntries()) {
                ContactItem moveToOfflineContactItem = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
                moveToOffline(moveToOfflineContactItem);
            }
        }
        else
        {
        	try {
        	EventQueue.invokeAndWait(new Runnable() {

				@Override
				public void run() {
		            for (RosterEntry entry : roster.getUnfiledEntries()) {
		                ContactItem moveToOfflineContactItem = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());
		                moveToOffline(moveToOfflineContactItem);
		            }
				}
        		
        	});
        	} catch (Exception e) {
        		Log.error("moveToOffilne",e);
        	}
        }

    }

    /**
     * Called when NEW entries are added.
     *
     * @param addresses the address added.
     */
    public void entriesAdded(final Collection<String> addresses) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Roster roster = SparkManager.getConnection().getRoster();

                for (String jid : addresses) {
                    RosterEntry entry = roster.getEntry(jid);
                    addUser(entry);
                }
            }
        });
    }

    /**
     * Adds a single user to the ContactList.
     *
     * @param entry the <code>RosterEntry</code> of the the user.
     */
    private void addUser(RosterEntry entry) {
        ContactItem newContactItem = UIComponentRegistry.createContactItem(entry.getName(), null, entry.getUser());

        if (entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from) {
            // Ignore, since the new user is pending to be added.
            for (RosterGroup group : entry.getGroups()) {
                ContactGroup contactGroup = getContactGroup(group.getName());
                if (contactGroup == null) {
                    contactGroup = addContactGroup(group.getName());
                }

                boolean isPending = entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from
                    && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus();
                if (isPending) {
                    contactGroup.setVisible(true);
                }
                contactGroup.addContactItem(newContactItem);

            }
            return;
        }
        else {
            moveToOffline(newContactItem);
        }

        // Update users icon
        Presence presence = SparkManager.getConnection().getRoster().getPresence(entry.getUser());
        try {
            updateUserPresence(presence);
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Handle when the Roster changes based on subscription notices.
     *
     * @param addresses List of entries that were updated.
     */
    public void entriesUpdated(final Collection<String> addresses) {
        handleEntriesUpdated(addresses);
    }

    /**
     * Called when users are removed from the roster.
     *
     * @param addresses the addresses removed from the roster.
     */
    public void entriesDeleted(final Collection<String> addresses) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (String jid : addresses) {
                    removeContactItem(jid);
                }
            }
        });

    }

    /**
     * Handles any presence modifications of a user(s).
     *
     * @param addresses the Collection of addresses that have been modified within the Roster.
     */
    private synchronized void handleEntriesUpdated(final Collection<String> addresses) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Roster roster = SparkManager.getConnection().getRoster();

                Iterator<String> jids = addresses.iterator();
                while (jids.hasNext()) {
                    String jid = jids.next();
                    RosterEntry rosterEntry = roster.getEntry(jid);
                    if (rosterEntry != null) {
                        // Check for new Roster Groups and add them if they do not exist.
                        boolean isUnfiled = true;
                        for (RosterGroup group : rosterEntry.getGroups()) {
                            isUnfiled = false;

                            // Handle if this is a new Entry in a new Group.
                            if (getContactGroup(group.getName()) == null) {
                                // Create group.
                                ContactGroup contactGroup = addContactGroup(group.getName());
                                contactGroup.setVisible(false);
                                contactGroup = getContactGroup(group.getName());
                                ContactItem contactItem = UIComponentRegistry.createContactItem(rosterEntry.getName(), null, rosterEntry.getUser());
                                contactGroup.addContactItem(contactItem);
                                Presence presence = PresenceManager.getPresence(jid);
                                contactItem.setPresence(presence);
                                if (presence.isAvailable()) {
                                    contactGroup.setVisible(true);
                                }
                            }
                            else {
                                ContactGroup contactGroup = getContactGroup(group.getName());
                                ContactItem item = offlineGroup.getContactItemByJID(jid);
                                if (item == null) {
                                    item = contactGroup.getContactItemByJID(jid);
                                }
                                // Check to see if this entry is new to a pre-existing group.
                                if (item == null) {
                                    item = UIComponentRegistry.createContactItem(rosterEntry.getName(), null, rosterEntry.getUser());
                                    Presence presence = PresenceManager.getPresence(jid);
                                    item.setPresence(presence);
                                    if (presence.isAvailable()) {
                                        contactGroup.addContactItem(item);
                                        contactGroup.fireContactGroupUpdated();
                                    }
                                    else {
                                        moveToOffline(item);
                                        offlineGroup.fireContactGroupUpdated();
                                    }
                                }

                                // If not, just update their presence.
                                else {
                                    RosterEntry entry = roster.getEntry(jid);
                                    Presence presence = PresenceManager.getPresence(jid);
                                    item.setPresence(presence);
                                    try {
                                        updateUserPresence(presence);
                                    }
                                    catch (Exception e) {
                                        Log.error(e);
                                    }

                                    if (entry != null && (entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from)
                                        && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus()) {
                                        contactGroup.setVisible(true);

                                    }
                                    contactGroup.fireContactGroupUpdated();
                                }
                            }
                        }

                        // Now check to see if groups have been modified or removed. This is used
                        // to check if Contact Groups have been renamed or removed.
                        final Set<String> userGroupSet = new HashSet<String>();
                        jids = addresses.iterator();
                        while (jids.hasNext()) {
                            jid = (String)jids.next();
                            rosterEntry = roster.getEntry(jid);

                            boolean unfiled = true;
                            for (RosterGroup g : rosterEntry.getGroups()) {
                                userGroupSet.add(g.getName());
                                unfiled = false;
                            }

                            for (ContactGroup group : new ArrayList<ContactGroup>(getContactGroups())) {
                                ContactItem itemFound = group.getContactItemByJID(jid);
                                if (itemFound != null && !unfiled && group != getUnfiledGroup() && group != offlineGroup) {
                                    if (!userGroupSet.contains(group.getGroupName())) {
                                    	if (group.getContactItems().isEmpty()) {
                                    		removeContactGroup(group);
                                    	} else {
                                    		group.removeContactItem(itemFound);
                                    	}
                                    }
                                }

                            }
                        }


                        if (!isUnfiled) {
                            return;
                        }
                        
                        ContactGroup unfiledGrp = getUnfiledGroup();
                        ContactItem unfiledItem = unfiledGrp.getContactItemByJID(jid);
                        if (unfiledItem != null) {

                        }
                        else {
                            ContactItem offlineItem = offlineGroup.getContactItemByJID(jid);
                            if (offlineItem != null) {
                                if ((rosterEntry.getType() == RosterPacket.ItemType.none || rosterEntry.getType() == RosterPacket.ItemType.from)
                                    && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == rosterEntry.getStatus()) {
                                    // Remove from offlineItem and add to unfiledItem.
                                    offlineGroup.removeContactItem(offlineItem);
                                    unfiledGrp.addContactItem(offlineItem);
                                    unfiledGrp.fireContactGroupUpdated();
                                    unfiledGrp.setVisible(true);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public void presenceChanged(Presence presence) {

    }

    /**
     * Retrieve the ContactItem by it's jid.
     *
     * @param jid the JID of the user.
     * @return the "first" contact item found.
     */
    public ContactItem getContactItemByJID(String jid) {
        for (ContactGroup group : getContactGroups()) {
            ContactItem item = group.getContactItemByJID(StringUtils.parseBareAddress(jid));
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a Collection of ContactItems in a ContactList.
     *
     * @param jid the users JID.
     * @return a Collection of <code>ContactItem</code> items.
     */
    public Collection<ContactItem> getContactItemsByJID(String jid) {
        final List<ContactItem> list = new ArrayList<ContactItem>();
        for (ContactGroup group : getContactGroups()) {
            ContactItem item = group.getContactItemByJID(StringUtils.parseBareAddress(jid));
            if (item != null) {
                list.add(item);
            }
        }
        /**
         * We have to search ContactItems into offline contacts.
         * Standart getContactItemByJID() method search ContactItems only in OfflineGroup or into inline cantacts
         */
        for( ContactGroup group : getContactGroups() ) {
            for (ContactItem offlineItem : group.getOfflineContacts() ) {
                if ( offlineItem != null && offlineItem.getJID().equalsIgnoreCase(StringUtils.parseBareAddress(jid)) ) {
                    if ( !list.contains(offlineItem) ) {
                        list.add(offlineItem);
                    }
                }
            }
        }

        return list;
    }

    /**
     * Set an Icon for all ContactItems that match the given jid.
     *
     * @param jid  the users jid.
     * @param icon the icon to use.
     */
    public void setIconFor(String jid, Icon icon) {
        for (ContactGroup group : getContactGroups()) {
            ContactItem item = group.getContactItemByJID(StringUtils.parseBareAddress(jid));
            if (item != null) {
                item.setIcon(icon);
                group.fireContactGroupUpdated();
            }
        }
    }

    /**
     * Sets the default settings for a ContactItem.
     *
     * @param jid the users jid.
     */
    public void useDefaults(String jid) {
        for (ContactGroup group : getContactGroups()) {
            ContactItem item = group.getContactItemByJID(StringUtils.parseBareAddress(jid));
            if (item != null) {
                item.updatePresenceIcon(item.getPresence());
                group.fireContactGroupUpdated();
            }
        }
    }


    /**
     * Retrieve the ContactItem by their displayed name (either alias, nickname or username).
     *
     * @param displayName the users nickname in the contact list.
     * @return the "first" contact item found.
     */
    public ContactItem getContactItemByDisplayName(String displayName) {
        for (ContactGroup contactGroup : getContactGroups()) {
            ContactItem item = contactGroup.getContactItemByDisplayName(displayName);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    /**
     * Adds a new ContactGroup to the ContactList.
     *
     * @param group the group to add.
     */
    private void addContactGroup(ContactGroup group) {
        groupList.add(group);

        Collections.sort(groupList, GROUP_COMPARATOR);

        try {
            mainPanel.add(group, groupList.indexOf(group));
        }
        catch (Exception e) {
            Log.error(e);
        }

        group.addContactGroupListener(this);

        fireContactGroupAdded(group);

        // Check state
        String prop = props.getProperty(group.getGroupName());
        if (prop != null) {
            boolean isCollapsed = Boolean.valueOf(prop);
            group.setCollapsed(isCollapsed);
        }
    }

    /**
     * Creates and adds a new ContactGroup to the ContactList.
     *
     * @param groupName the name of the ContactGroup to add.
     * @return the newly created ContactGroup.
     */
    private ContactGroup addContactGroup(String groupName) {
        StringTokenizer tkn = new StringTokenizer(groupName, "::");

        ContactGroup rootGroup = null;
        ContactGroup lastGroup = null;
        StringBuffer buf = new StringBuffer();

        boolean groupAdded = false;
        while (tkn.hasMoreTokens()) {
            String group = tkn.nextToken();
            buf.append(group);
            if (tkn.hasMoreTokens()) {
                buf.append("::");
            }

            String name = buf.toString();
            if (name.endsWith("::")) {
                name = name.substring(0, name.length() - 2);
            }

            ContactGroup newContactGroup = getContactGroup(name);


            if (newContactGroup == null) {
		newContactGroup = UIComponentRegistry.createContactGroup(group);

                String realGroupName = buf.toString();
                if (realGroupName.endsWith("::")) {
                    realGroupName = realGroupName.substring(0, realGroupName.length() - 2);
                }

                newContactGroup.setGroupName(realGroupName);
            }
            else {
                if (newContactGroup != offlineGroup && newContactGroup != getUnfiledGroup()) {
                    rootGroup = newContactGroup;
                    continue;
                }
            }


            if (lastGroup != null) {
                lastGroup.addContactGroup(newContactGroup);
                groupList.add(newContactGroup);
            }
            else if (rootGroup != null) {
                rootGroup.addContactGroup(newContactGroup);
                groupList.add(newContactGroup);
            }
            else {
                rootGroup = newContactGroup;
            }

            lastGroup = newContactGroup;


            newContactGroup.addContactGroupListener(this);

            if (sharedGroups != null) {
                boolean isSharedGroup = sharedGroups.contains(newContactGroup.getGroupName());
                newContactGroup.setSharedGroup(isSharedGroup);
            }

            fireContactGroupAdded(newContactGroup);

            // Check state

            String prop = props.getProperty(newContactGroup.getGroupName());
            if (prop != null) {
                boolean isCollapsed = Boolean.valueOf(prop);
                newContactGroup.setCollapsed(isCollapsed);
            }


            groupAdded = true;
        }

        if (!groupAdded) {
            return getContactGroup(groupName);
        }


        final List<ContactGroup> tempList = new ArrayList<ContactGroup>();
        final Component[] comps = mainPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof ContactGroup && c != offlineGroup) {
                tempList.add((ContactGroup)c);
            }
        }
        tempList.add(rootGroup);


        groupList.add(rootGroup);

        Collections.sort(tempList, GROUP_COMPARATOR);

        int loc = tempList.indexOf(rootGroup);


        try {
            mainPanel.add(rootGroup, loc);
        }
        catch (Exception e) {
            Log.error(e);
        }

        //Check if i should show groups with no users online
        if (null != getContactGroup(groupName) && !getContactGroup(groupName).hasAvailableContacts())
        {
            showEmptyGroups(localPreferences.isEmptyGroupsShown());
        }

        
        return getContactGroup(groupName);

    }

    /**
     * Removes a ContactGroup from the group model and ContactList.
     *
     * @param contactGroup the ContactGroup to remove.
     */
    private void removeContactGroup(ContactGroup contactGroup) {
        contactGroup.removeContactGroupListener(this);
        groupList.remove(contactGroup);
        mainPanel.remove(contactGroup);

        ContactGroup parent = getParentGroup(contactGroup.getGroupName());
        if (parent != null) {
            parent.removeContactGroup(contactGroup);
        }

        contactListScrollPane.validate();
        mainPanel.invalidate();
        mainPanel.repaint();

        fireContactGroupRemoved(contactGroup);
    }


    /**
     * Returns a ContactGroup based on its name.
     *
     * @param groupName the name of the ContactGroup.
     * @return the ContactGroup. If no ContactGroup is found, null is returned.
     */
    public ContactGroup getContactGroup(String groupName) {
        ContactGroup cGroup = null;

        for (ContactGroup contactGroup : groupList) {
            if (contactGroup.getGroupName().equals(groupName)) {
                cGroup = contactGroup;
                break;
            }
            else {
                cGroup = getSubContactGroup(contactGroup, groupName);
                if (cGroup != null) {
                    break;
                }
            }
        }

        return cGroup;
    }

    /**
     * For traversing of a nested group. Allows users to find the owning parent of a given contact group.
     *
     * @param groupName the name of the nested contact group.
     * @return the parent ContactGroup. If no parent, null will be returned.
     */
    public ContactGroup getParentGroup(String groupName) {
        // Check if there is even a parent group
        if (!groupName.contains("::")) {
            return null;
        }

        final ContactGroup group = getContactGroup(groupName);
        if (group == null) {
            return null;
        }

        // Otherwise, find parent
        int index = groupName.lastIndexOf("::");
        String parentGroupName = groupName.substring(0, index);
        return getContactGroup(parentGroupName);
    }

    /**
     * Returns the nested ContactGroup of a given ContactGroup with associated name.
     *
     * @param group     the parent ContactGroup.
     * @param groupName the name of the nested group.
     * @return the nested ContactGroup. If not found, null will be returned.
     */
    private ContactGroup getSubContactGroup(ContactGroup group, String groupName) {
        final Iterator<ContactGroup> contactGroups = group.getContactGroups().iterator();
        ContactGroup grp = null;

        while (contactGroups.hasNext()) {
            ContactGroup contactGroup = contactGroups.next();
            if (contactGroup.getGroupName().equals(groupName)) {
                grp = contactGroup;
                break;
            }
            else if (contactGroup.getContactGroups().size() > 0) {
                grp = getSubContactGroup(contactGroup, groupName);
                if (grp != null) {
                    break;
                }
            }

        }
        return grp;
    }

    /**
     * Toggles the visiblity of a ContactGroup.
     *
     * @param groupName the name of the ContactGroup.
     * @param visible   true to show, otherwise false.
     */
    public void toggleGroupVisibility(String groupName, boolean visible) {
        StringTokenizer tkn = new StringTokenizer(groupName, "::");
        while (tkn.hasMoreTokens()) {
            String group = tkn.nextToken();
            ContactGroup contactGroup = getContactGroup(group);
            if (contactGroup != null) {
                contactGroup.setVisible(visible);
            }
        }

        ContactGroup group = getContactGroup(groupName);
        if (group != null) {
            group.setVisible(visible);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addingGroupButton) {
            new RosterDialog().showRosterDialog();
        }
        else if (e.getSource() == chatMenu) {
            if (activeItem != null) {
                SparkManager.getChatManager().activateChat(activeItem.getJID(), activeItem.getDisplayName());
            }
        }
        else if (e.getSource() == addContactMenu) {
            RosterDialog rosterDialog = new RosterDialog();
            if (activeGroup != null) {
                rosterDialog.setDefaultGroup(activeGroup);
            }
            rosterDialog.showRosterDialog();
        }
        else if (e.getSource() == removeContactFromGroupMenu) {
            if (activeItem != null) {
                removeContactFromGroup(activeItem);
            }
        }
        else if (e.getSource() == renameMenu) {
            if (activeItem == null) {
                return;
            }

            String oldAlias = activeItem.getAlias();
            String newAlias = JOptionPane.showInputDialog(this, Res.getString("label.rename.to") + ":", oldAlias);
            
            // if user pressed 'cancel', output will be null.
        	// if user removed alias, output will be an empty String.
            if (newAlias != null) {
	            if (!ModelUtil.hasLength(newAlias)) {
	            	newAlias = null; // allows you to remove an alias.
	            }
	            
	            String address = activeItem.getJID();
	            ContactGroup contactGroup = getContactGroup(activeItem.getGroupName());
	            ContactItem contactItem = contactGroup.getContactItemByDisplayName(activeItem.getDisplayName());
	            contactItem.setAlias(newAlias);
	
	            final Roster roster = SparkManager.getConnection().getRoster();
	            RosterEntry entry = roster.getEntry(address);
	            entry.setName(newAlias);
	
	
	            final Iterator<ContactGroup> contactGroups = groupList.iterator();
	            String user = StringUtils.parseBareAddress(address);
	            while (contactGroups.hasNext()) {
	                ContactGroup cg = contactGroups.next();
	                ContactItem ci = cg.getContactItemByJID(user);
	                if (ci != null) {
	                    ci.setAlias(newAlias);
	                }
	            }
            }
        }
    }


    /**
     * Removes a contact item from the group.
     *
     * @param item the ContactItem to remove.
     */
    private void removeContactFromGroup(ContactItem item) {
        String groupName = item.getGroupName();
        ContactGroup contactGroup = getContactGroup(groupName);
        Roster roster = SparkManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());
        if (entry != null && contactGroup != offlineGroup) {
            try {
                RosterGroup rosterGroup = roster.getGroup(groupName);
                if (rosterGroup != null) {
                    RosterEntry rosterEntry = rosterGroup.getEntry(entry.getUser());
                    if (rosterEntry != null) {
                        rosterGroup.removeEntry(rosterEntry);
                    }
                }
                contactGroup.removeContactItem(contactGroup.getContactItemByJID(item.getJID()));
                checkGroup(contactGroup);
            }
            catch (Exception e) {
                Log.error("Error removing user from contact list.", e);
            }
        }
    }

    private void removeContactFromRoster(ContactItem item) {
        Roster roster = SparkManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());
        if (entry != null) {
            try {
                roster.removeEntry(entry);
            }
            catch (XMPPException e) {
                Log.warning("Unable to remove roster entry.", e);
            }
        }
    }

    private void removeContactItem(String jid) {
        for (ContactGroup group : new ArrayList<ContactGroup>(getContactGroups())) {
            ContactItem item = group.getContactItemByJID(jid);
            group.removeOfflineContactItem(jid);
            if (item != null) {
                group.removeContactItem(item);
                checkGroup(group);
            }
        }
    }

    public void contactItemClicked(ContactItem item) {
        activeItem = item;

        if (activeKeyEvent == null || ((activeKeyEvent.getModifiers() & KeyEvent.CTRL_MASK) == 0)) {
            clearSelectionList(item);
        }


        fireContactItemClicked(item);
        activeKeyEvent = null;
    }

    public void contactItemDoubleClicked(ContactItem item) {
        activeItem = item;

        ChatManager chatManager = SparkManager.getChatManager();
        boolean handled = chatManager.fireContactItemDoubleClicked(item);

        if (!handled) {
            chatManager.activateChat(item.getJID(), item.getDisplayName());
        }

        clearSelectionList(item);

        fireContactItemDoubleClicked(item);
    }

    public void contactGroupPopup(MouseEvent e, final ContactGroup group) {
   	  // Do nothing with offline group
        if (group == offlineGroup || group == getUnfiledGroup()) {
            return;
        }


        final JPopupMenu popup = new JPopupMenu();
        if (!Default.getBoolean(Default.ADD_CONTACT_DISABLED)) {
        	popup.add(addContactMenu);
        }

        if(!Default.getBoolean("ADD_CONTACT_GROUP_DISABLED")){
            popup.add(addContactGroupMenu);
        }
        popup.addSeparator();

        fireContextMenuListenerPopup(popup, group);

        JMenuItem delete = new JMenuItem(Res.getString("menuitem.delete"));
        JMenuItem rename = new JMenuItem(Res.getString("menuitem.rename"));
        JMenuItem expand = new JMenuItem(Res.getString("menuitem.expand.all.groups"));
        JMenuItem collapse = new JMenuItem(Res.getString("menuitem.collapse.all.groups"));
        
        if (!group.isSharedGroup()) {
            popup.addSeparator();
            popup.add(delete);
            popup.add(rename);
        }
        
        popup.addSeparator();
        popup.add(expand);
        popup.add(collapse);

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int ok = JOptionPane.showConfirmDialog(group, Res.getString("message.delete.confirmation", group.getGroupName()), Res.getString("title.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (ok == JOptionPane.YES_OPTION) {
                    String groupName = group.getGroupName();
                    Roster roster = SparkManager.getConnection().getRoster();

                    RosterGroup rosterGroup = roster.getGroup(groupName);
                    if (rosterGroup != null) {
                        for (RosterEntry entry : rosterGroup.getEntries()) {
                            try {
                                rosterGroup.removeEntry(entry);
                            }
                            catch (XMPPException e1) {
                                Log.error("Error removing entry", e1);
                            }
                        }
                    }

                    // Remove from UI
                    removeContactGroup(group);
                    invalidate();
                    repaint();
                }

            }
        });

     
      rename.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newName = JOptionPane.showInputDialog(group, Res.getString("label.rename.to") + ":", Res.getString("title.rename.roster.group"), JOptionPane.QUESTION_MESSAGE);
                if (!ModelUtil.hasLength(newName)) {
                    return;
                }
                String groupName = group.getGroupName();
                Roster roster = SparkManager.getConnection().getRoster();

                RosterGroup rosterGroup = roster.getGroup(groupName);
                //Do not remove ContactGroup if the name entered was the same 
                if (rosterGroup != null && !groupName.equals(newName)) {
                   removeContactGroup(group);
                    rosterGroup.setName(newName);
                    addContactGroup(newName);
                    toggleGroupVisibility(newName, true);        
                    getContactGroup(newName).setCollapsed( group.isCollapsed());
                }

            }
        });
        expand.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
         	Collection<ContactGroup> groups = getContactGroups();  
         	for (ContactGroup group : groups) {
               group.setCollapsed(false);
           }  
           }
        });
        
        collapse.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
         	Collection<ContactGroup> groups = getContactGroups();  
         	for (ContactGroup group : groups) {
               group.setCollapsed(true);
           }  
           }
        });

        // popup.add(inviteFirstAcceptor);
        popup.show(group, e.getX(), e.getY());

        activeGroup = group;
    }

    
    public void showPopup(MouseEvent e, final ContactItem item)
    {
	showPopup(null,e,item);
    }
    /**
     * Shows popup for right-clicking of ContactItem.
     *
     * @param e    the MouseEvent
     * @param item the ContactItem
     * @param component the owning component
     */
    public void showPopup(Component component, MouseEvent e, final ContactItem item) {
        if (item.getJID() == null) {
            return;
        }

        activeItem = item;

        final JPopupMenu popup = new JPopupMenu();

        // Add Start Chat Menu
        popup.add(chatMenu);

        // Add Send File Action
        Action sendAction = new AbstractAction() {
			private static final long serialVersionUID = -7519717310558205566L;

			public void actionPerformed(ActionEvent actionEvent) {
                SparkManager.getTransferManager().sendFileTo(item);
            }
        };

        sendAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DOCUMENT_16x16));
        sendAction.putValue(Action.NAME, Res.getString("menuitem.send.a.file"));

        if (item.getPresence() != null) {
            popup.add(sendAction);
        }

        popup.addSeparator();


        String groupName = item.getGroupName();
        ContactGroup contactGroup = getContactGroup(groupName);

        // Only show "Remove Contact From Group" if the user belongs to more than one group.
        if (!contactGroup.isSharedGroup() && !contactGroup.isOfflineGroup() && contactGroup != getUnfiledGroup()) {
            Roster roster = SparkManager.getConnection().getRoster();
            RosterEntry entry = roster.getEntry(item.getJID());
            if (entry != null) {
                int groupCount = entry.getGroups().size();

                //todo: It should be possible to remove a user from the only group they're in
                // which would put them into the unfiled group.
                if (groupCount > 1) {
                    popup.add(removeContactFromGroupMenu);
                }

            }
        }

        // Define remove entry action
        Action removeAction = new AbstractAction() {
			private static final long serialVersionUID = -2565914214685979320L;

			public void actionPerformed(ActionEvent e) {
                removeContactFromRoster(item);
            }
        };

        removeAction.putValue(Action.NAME, Res.getString("menuitem.remove.from.roster"));
        removeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_CIRCLE_DELETE));

        // Check if user is in shared group.
        boolean isInSharedGroup = false;
        for (ContactGroup cGroup : new ArrayList<ContactGroup>(getContactGroups())) {
            if (cGroup.isSharedGroup()) {
                ContactItem it = cGroup.getContactItemByJID(item.getJID());
                if (it != null) {
                    isInSharedGroup = true;
                }
            }
        }


        if (!contactGroup.isSharedGroup() && !isInSharedGroup) {
            popup.add(removeAction);
        }

        popup.add(renameMenu);


        Action viewProfile = new AbstractAction() {
			private static final long serialVersionUID = -2562731455090634805L;

			public void actionPerformed(ActionEvent e) {
                VCardManager vcardSupport = SparkManager.getVCardManager();
                String jid = item.getJID();
                vcardSupport.viewProfile(jid, SparkManager.getWorkspace());
            }
        };
        viewProfile.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.PROFILE_IMAGE_16x16));
        viewProfile.putValue(Action.NAME, Res.getString("menuitem.view.profile"));

        popup.add(viewProfile);


        popup.addSeparator();

        Action lastActivityAction = new AbstractAction() {
			private static final long serialVersionUID = -4884230635430933060L;

			public void actionPerformed(ActionEvent actionEvent) {
	            try {
					String client = "";
					if (item.getPresence().getType() != Presence.Type.unavailable) {
						client = item.getPresence().getFrom();
						if ((client != null) && (client.lastIndexOf("/") != -1)) {
							client = client.substring(client.lastIndexOf("/"));
						} else client = "/";
					}
	
	                LastActivity activity = LastActivityManager.getLastActivity(SparkManager.getConnection(), item.getJID()+client);
                    long idleTime = (activity.getIdleTime() * 1000);
                    String time = ModelUtil.getTimeFromLong(idleTime);
                    JOptionPane.showMessageDialog(getGUI(), Res.getString("message.idle.for", time), Res.getString("title.last.activity"), JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception e1) {
                    JOptionPane.showMessageDialog(getGUI(), Res.getString("message.unable.to.retrieve.last.activity", item.getJID()), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                }

            }
        };

        lastActivityAction.putValue(Action.NAME, Res.getString("menuitem.view.last.activity"));
        lastActivityAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_USER1_STOPWATCH));

        if (contactGroup == offlineGroup || item.getPresence().isAway() || (item.getPresence().getType() == Presence.Type.unavailable) || (item.getPresence().getType() == null)) {
            popup.add(lastActivityAction);
        }

        Action subscribeAction = new AbstractAction() {
			private static final long serialVersionUID = -7754905015338902300L;

			public void actionPerformed(ActionEvent e) {
                String jid = item.getJID();
                Presence response = new Presence(Presence.Type.subscribe);
                response.setTo(jid);

                SparkManager.getConnection().sendPacket(response);
            }
        };

        subscribeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_USER1_INFORMATION));
        subscribeAction.putValue(Action.NAME, Res.getString("menuitem.subscribe.to"));

        Roster roster = SparkManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());
        if (entry != null && entry.getType() == RosterPacket.ItemType.from) {
            popup.add(subscribeAction);
        }       
        else if( entry!=null && entry.getType() != RosterPacket.ItemType.both && entry.getStatus() == RosterPacket.ItemStatus.SUBSCRIPTION_PENDING)
        {
            popup.add(subscribeAction);
        }

        // Fire Context Menu Listener
        fireContextMenuListenerPopup(popup, item);

        ContactGroup group = getContactGroup(item.getGroupName());
	if (component == null) {
	    popup.show(group.getList(), e.getX(), e.getY());
	} else {
	    popup.show(component, e.getX(), e.getY());
	    popup.requestFocus();
	}
    }

    public void showPopup(MouseEvent e, final Collection<ContactItem> items) {
        ContactGroup group = null;
        for (ContactItem item : items) {
            group = getContactGroup(item.getGroupName());
            break;
        }


        final JPopupMenu popup = new JPopupMenu();
        final JMenuItem sendMessagesMenu = new JMenuItem(Res.getString("menuitem.send.a.message"), SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));


        fireContextMenuListenerPopup(popup, items);

        popup.add(sendMessagesMenu);

        sendMessagesMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessages(items);
            }
        });

        try {
            popup.show(group.getList(), e.getX(), e.getY());
        }
        catch (NullPointerException ee) {
            // Nothing we can do here
        }
    }

    private void clearSelectionList(ContactItem selectedItem) {
        // Check for null. In certain cases the event triggering the model might
        // not find the selected object.
        if (selectedItem == null) {
            return;
        }

        final ContactGroup owner = getContactGroup(selectedItem.getGroupName());
        for (ContactGroup contactGroup : new ArrayList<ContactGroup>(groupList)) {
            if (owner != contactGroup) {
                contactGroup.clearSelection();
            }
        }
    }


    private void sendMessages(Collection<ContactItem> items) {
        StringBuffer buf = new StringBuffer();
        InputDialog dialog = new InputDialog();
        final String messageText = dialog.getInput(Res.getString("title.broadcast.message"), Res.getString("message.enter.broadcast.message"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), SparkManager.getMainWindow());
        if (ModelUtil.hasLength(messageText)) {

            final Map<String, Message> broadcastMessages = new HashMap<String, Message>();
            for (ContactItem item : items) {
                final Message message = new Message();
                message.setTo(item.getJID());
                message.setProperty("broadcast", true);
                message.setBody(messageText);
                if (!broadcastMessages.containsKey(item.getJID())) {
                    buf.append(item.getDisplayName()).append("\n");
                    broadcastMessages.put(item.getJID(), message);
                }
            }

            for (Message message : broadcastMessages.values()) {
                SparkManager.getConnection().sendPacket(message);
            }

            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.broadcasted.to", buf.toString()), Res.getString("title.notification"), JOptionPane.INFORMATION_MESSAGE);
        }


    }

    // For plugin use only

    public void initialize() {
	this.setBorder(BorderFactory.createEmptyBorder());

        // Add Contact List
        addContactListToWorkspace();

        // Hide top toolbar
        SparkManager.getMainWindow().getTopToolBar().setVisible(false);

        final Runnable sharedGroupLoader = new Runnable() {
	    public void run() {
                // Retrieve shared group list.
                try {
                    sharedGroups = SharedGroupManager.getSharedGroups(SparkManager.getConnection());
                }
                catch (XMPPException e) {
                    Log.error("Unable to contact shared group info.", e);
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        loadContactList();
                    }
                });

            }
        };

        TaskEngine.getInstance().submit(sharedGroupLoader);
    }

    private void loadContactList() {

	// Build the initial contact list.
	buildContactList();

	boolean show = localPreferences.isEmptyGroupsShown();

	// Hide all groups initially
	showEmptyGroups(show);

	// Hide all Offline Users
	showOfflineUsers(localPreferences.isOfflineUsersShown());

	// Add a subscription listener.
	addSubscriptionListener();

	// Load all plugins
	SparkManager.getWorkspace().loadPlugins();

    }

    public void addSubscriptionListener() {
        // Add subscription listener
        PacketFilter packetFilter = new PacketTypeFilter(Presence.class);
        PacketListener subscribeListener = new PacketListener() {
            public void processPacket(Packet packet) {
                final Presence presence = (Presence)packet;
                if (presence.getType() == Presence.Type.subscribe) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            subscriptionRequest(presence.getFrom());
                        }
                    });
                }
                else if (presence.getType() == Presence.Type.unsubscribe) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Roster roster = SparkManager.getConnection().getRoster();
                            RosterEntry entry = roster.getEntry(presence.getFrom());
                            if (entry != null) {
                                try {
                                    removeContactItem(presence.getFrom());
                                    roster.removeEntry(entry);
                                }
                                catch (XMPPException e) {
                                    Presence unsub = new Presence(Presence.Type.unsubscribed);
                                    unsub.setTo(presence.getFrom());
                                    SparkManager.getConnection().sendPacket(unsub);
                                    Log.error(e);
                                }
                            }
                        }
                    });


                }
                else if (presence.getType() == Presence.Type.subscribe) {
                    // Find Contact in Contact List
                    String jid = StringUtils.parseBareAddress(presence.getFrom());
                    ContactItem item = getContactItemByJID(jid);

                    // If item is not in the Contact List, add them.
                    if (item == null) {
                        final Roster roster = SparkManager.getConnection().getRoster();
                        RosterEntry entry = roster.getEntry(jid);
                        if (entry != null) {
                            item = UIComponentRegistry.createContactItem(entry.getName(), null, jid);
                            moveToOffline(item);
                            offlineGroup.fireContactGroupUpdated();
                        }
                    }
                }
                else if (presence.getType() == Presence.Type.unsubscribed) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Roster roster = SparkManager.getConnection().getRoster();
                            RosterEntry entry = roster.getEntry(presence.getFrom());
                            if (entry != null) {
                                try {
                                    removeContactItem(presence.getFrom());
                                    roster.removeEntry(entry);
                                }
                                catch (XMPPException e) {
                                    Log.error(e);
                                }
                            }
                            String jid = StringUtils.parseBareAddress(presence.getFrom());
                            removeContactItem(jid);
                        }
                    });
                }
                else {

                    try {
                        initialPresences.add(presence);
                    }
                    catch (Exception e) {
                        Log.error(e);
                    }

                    int numberOfMillisecondsInTheFuture = 1000;

                    presenceTimer.schedule(new TimerTask() {
                        public void run() {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    for (Presence userToUpdate : new ArrayList<Presence>(initialPresences)) {
                                        initialPresences.remove(userToUpdate);
                                        try {
                                            updateUserPresence(userToUpdate);
                                        }
                                        catch (Exception e) {
                                            Log.error(e);
                                        }

                                    }
                                }
                            });
                        }
                    }, numberOfMillisecondsInTheFuture);
                }
            }
        };

        SparkManager.getConnection().addPacketListener(subscribeListener, packetFilter);
    }


    public void shutdown() {
        saveState();
    }

    public boolean canShutDown() {
        return true;
    }

    private void addContactListToWorkspace() {
        Workspace workspace = SparkManager.getWorkspace();
        workspace.getWorkspacePane().addTab(Res.getString("tab.contacts"), SparkRes.getImageIcon(SparkRes.SMALL_ALL_CHATS_IMAGE), this);
        // Add To Contacts Menu
        final JMenu contactsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.contacts"));
        JMenuItem addContactsMenu = new JMenuItem("", SparkRes.getImageIcon(SparkRes.USER1_ADD_16x16));
        ResourceUtils.resButton(addContactsMenu, Res.getString("menuitem.add.contact"));
        ResourceUtils.resButton(addContactGroupMenu, Res.getString("menuitem.add.contact.group"));

        if (!Default.getBoolean(Default.ADD_CONTACT_DISABLED)) {
        	contactsMenu.add(addContactsMenu);
        }
       if(!Default.getBoolean("ADD_CONTACT_GROUP_DISABLED")){      
	       contactsMenu.add(addContactGroupMenu);
       }
       
        addContactsMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RosterDialog().showRosterDialog();
            }
        });

        addContactGroupMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String groupName = JOptionPane.showInputDialog(getGUI(), Res.getString("message.name.of.group") + ":", Res.getString("title.add.new.group"), JOptionPane.QUESTION_MESSAGE);
                if (ModelUtil.hasLength(groupName)) {
                    ContactGroup contactGroup = getContactGroup(groupName);
                    if (contactGroup == null) {
                        contactGroup = addContactGroup(groupName);
                        contactGroup.setVisible(true);
                        //validateTree();
                        repaint();
                    }
                }
            }
        });

        // Add Toggle Contacts Menu
        ResourceUtils.resButton(showHideMenu, Res.getString("menuitem.show.empty.groups"));
        contactsMenu.add(showHideMenu);

        showHideMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEmptyGroups(showHideMenu.isSelected());
            }
        });

        ResourceUtils.resButton(showOfflineGroupMenu, Res.getString("menuitem.show.offline.group"));
        contactsMenu.add(showOfflineGroupMenu);
        
        showOfflineGroupMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                showOfflineGroup(showOfflineGroupMenu.isSelected());
            }
        });
        
        ResourceUtils.resButton(showOfflineUsersMenu, Res.getString("menuitem.show.offline.users"));
        contactsMenu.add(showOfflineUsersMenu);
        
        showOfflineUsersMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                showOfflineUsers(showOfflineUsersMenu.isSelected());
            }
        });
        
        // Show or Hide Offline Group
        showOfflineGroupMenu.setSelected(localPreferences.isOfflineGroupVisible());
        showOfflineGroup(localPreferences.isOfflineGroupVisible());

        // sets showOfflineUsersMenu selected or not selected
        showOfflineUsersMenu.setSelected(localPreferences.isOfflineUsersShown());
        
        // Initialize vcard support
        SparkManager.getVCardManager();
    }

    /**
     * Toggles the visibility of empty groups.
     *
     * @param show true to display empty contact groups within the ContactList, otherwise false.
     */
    private void showEmptyGroups(boolean show) {
        for (ContactGroup group : getContactGroups()) {
            if (group != offlineGroup) {
                if (show) {
                    group.setVisible(true);
                } else {
                    // Never hide offline group.
                    group.setVisible(group.hasAvailableContacts());
                }
            }
        }

        localPreferences.setEmptyGroupsShown(show);
        showHideMenu.setSelected(show);
        SettingsManager.saveSettings();
    }
    
    private void showOfflineUsers(boolean show) {
       for (ContactGroup group : getContactGroups()) 
       {
      	  if(group != offlineGroup)
      	  {
      		  group.toggleOfflineVisibility(show);
      	  }
      	  
      	  if (group == offlineGroup) {
               if (show) 
               {
                   group.setVisible(true);
                   showOfflineGroupMenu.setEnabled(true);
                   showOfflineGroupMenu.setSelected(localPreferences.isOfflineGroupVisible());
                   showOfflineGroup(showOfflineGroupMenu.isSelected());
               } 
               else 
               {
                   group.setVisible(false);
                   showOfflineGroupMenu.setEnabled(false);
               }
           }
       }
       localPreferences.setOfflineUsersShown(show);
       SettingsManager.saveSettings();
   }

    /**
     * Toggles the visiblity of the Offline Group.
     *
     * @param show true to display the offline group, otherwise false.
     */
    private void showOfflineGroup(boolean show) {
        // Save in preferences
        localPreferences.setOfflineGroupVisible(show);
        SettingsManager.saveSettings();

        // Toggle Visibility of Offline Group.
        offlineGroup.setVisible(show);

        if (show) {
            // Remove offline items from all groups.
            for (ContactGroup group : getContactGroups()) {
                group.toggleOfflineVisibility(false);
            }
        }
        else {
            // Remove offline items from all groups.
            for (ContactGroup group : getContactGroups()) {
                group.toggleOfflineVisibility(true);
            }
        }
    }


    /**
     * Sorts ContactGroups
     */
    public static final Comparator<ContactGroup> GROUP_COMPARATOR = new Comparator<ContactGroup>() {
        public int compare(ContactGroup group1, ContactGroup group2) {
            // Make sure that offline group is always on bottom.
            if (group2.isOfflineGroup()) {
                return -1;
            }

            return group1.getGroupName().trim().toLowerCase().compareTo(group2.getGroupName().trim().toLowerCase());
        }
    };

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public List<ContactGroup> getContactGroups() {
        final List<ContactGroup> gList = new ArrayList<ContactGroup>(groupList);
        Collections.sort(gList, GROUP_COMPARATOR);
        return gList;
    }

    private void subscriptionRequest(final String jid) {
        final SubscriptionDialog subscriptionDialog = new SubscriptionDialog();
        subscriptionDialog.invoke(jid);
    }

    public void addContextMenuListener(ContextMenuListener listener) {
        contextListeners.add(listener);
    }

    public void removeContextMenuListener(ContextMenuListener listener) {
        contextListeners.remove(listener);
    }

    public void fireContextMenuListenerPopup(JPopupMenu popup, Object object) {
        for (ContextMenuListener listener : new ArrayList<ContextMenuListener>(contextListeners)) {
            listener.poppingUp(object, popup);
        }
    }

    public JComponent getGUI() {
        return this;
    }

    public ContactGroup getActiveGroup() {
        return activeGroup;
    }

    public Collection<ContactItem> getSelectedUsers() {
        final List<ContactItem> list = new ArrayList<ContactItem>();

        for (ContactGroup group : getContactGroups()) {
            for (ContactItem item : group.getSelectedContacts()) {
                list.add(item);
            }
        }
        return list;
    }
    
  /**
   * Selects the first user found with specified jid
   * @param jid, the Users JID
   */
    public void setSelectedUser(String jid) {
	for (ContactGroup group : getContactGroups()) {
	    if (group.getContactItemByJID(jid) != null) {

		ContactItem item = group.getContactItemByJID(jid);
		group.getList().setSelectedValue(item, false);
		return;
	    }
	}
    }

    private void checkGroup(final ContactGroup group) {
    	try {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
			        if (!group.hasAvailableContacts() && group != offlineGroup && group != getUnfiledGroup() && !showHideMenu.isSelected()) {
			            group.setVisible(false);
			        }	
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    public void addFileDropListener(FileDropListener listener) {
        dndListeners.add(listener);
    }

    public void removeFileDropListener(FileDropListener listener) {
        dndListeners.remove(listener);
    }

    public void fireFilesDropped(Collection<File> files, ContactItem item) {
        for (FileDropListener fileDropListener : new ArrayList<FileDropListener>(dndListeners)) {
            fileDropListener.filesDropped(files, item);
        }
    }

    public void contactItemAdded(ContactItem item) {
        fireContactItemAdded(item);
    }

    public void contactItemRemoved(ContactItem item) {
        fireContactItemRemoved(item);
    }

    /*
        Adding ContactListListener support.
    */

    public void addContactListListener(ContactListListener listener) {
        contactListListeners.add(listener);
    }

    public void removeContactListListener(ContactListListener listener) {
        contactListListeners.remove(listener);
    }

    public void fireContactItemAdded(ContactItem item) {
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) {
            contactListListener.contactItemAdded(item);
        }
    }

    public void fireContactItemRemoved(ContactItem item) {
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) {
            contactListListener.contactItemRemoved(item);
        }
    }

    public void fireContactGroupAdded(ContactGroup group) {
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) {
            contactListListener.contactGroupAdded(group);
        }
    }

    public void fireContactGroupRemoved(ContactGroup group) {
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) {
            contactListListener.contactGroupRemoved(group);
        }
    }

    public void fireContactItemClicked(ContactItem contactItem) {
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) {
            contactListListener.contactItemClicked(contactItem);
        }
    }

    public void fireContactItemDoubleClicked(ContactItem contactItem) {
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) {
            contactListListener.contactItemDoubleClicked(contactItem);
        }
    }


    public void uninstall() {
        // Do nothing.
    }

    public void saveState() {
        if (props == null) {
            return;
        }
        for (ContactGroup contactGroup : getContactGroups()) { 
      	  props.put(contactGroup.getGroupName(), Boolean.toString(contactGroup.isCollapsed()));
        }

        try {
            props.store(new FileOutputStream(propertiesFile), "Tracks the state of groups.");
        }
        catch (IOException e) {
            Log.error("Unable to save group properties.", e);
        }

    }


    public void connectionClosed() {
	// No reason to reconnect.

	// Show MainWindow
	SparkManager.getMainWindow().setVisible(true);

	// Flash That Window.
	SparkManager.getNativeManager().flashWindowStopOnFocus(
		SparkManager.getMainWindow());

	String errorMessage = Res.getString("message.disconnected.error");

	switch (localPreferences.getReconnectPanelType()) {
	case 0:
	    _reconnectPanel.setClosedOnError(false);
	    _reconnectPanel.setDisconnectReason(errorMessage);
	    removeAllUsers();
	    workspace.changeCardLayout(RETRY_PANEL);
	    break;
	case 1:
	    switchAllUserOffline(false);
	    _reconnectpanelsmall.setReconnectText(errorMessage);
	    break;

	case 2:
	    switchAllUserOfflineNoGroupEntry(false);
	    _reconnectpanelicon.setReconnectText(errorMessage);
	    break;
	}

    }
    


    /**
     * Reconnect using the Panel with Message
     * @param message
     */
    private void reconnect(final String message) {
	// Show MainWindow
	SparkManager.getMainWindow().setVisible(true);

	// Flash That Window.
	SparkManager.getNativeManager().flashWindowStopOnFocus(
		SparkManager.getMainWindow());

	switch (localPreferences.getReconnectPanelType()) {
	case 0:
	    workspace.changeCardLayout(RETRY_PANEL);
	    _reconnectPanel.setDisconnectReason(message);
	    break;
	case 1:
	    switchAllUserOffline(true);
	    _reconnectpanelsmall.startReconnecting();
	    break;

	case 2:
	    switchAllUserOfflineNoGroupEntry(true);
	    _reconnectpanelicon.startReconnecting();
	    break;
	default:
	    workspace.changeCardLayout(RETRY_PANEL);
	}

	removeAllUsers();
    }
   
    public void clientReconnected() {

	switch (localPreferences.getReconnectPanelType()) {
	case 0:
	    workspace.changeCardLayout(Workspace.WORKSPACE_PANE);
	    break;
	case 1:
	    mainPanel.remove(_reconnectpanelsmall);
	    break;

	case 2:
	    SwingWorker sw = new SwingWorker() {
		@Override
		public Object construct() {
		    _reconnectpanelicon.remove();
		    _reconnectpanelicon.getPanel().revalidate();
		    return 42;
		}
	    };
	    sw.start();
	    break;
	}

	offlineGroup.fireContactGroupUpdated();
	buildContactList();

	final Presence myPresence = SparkManager.getWorkspace().getStatusBar()
		.getPresence();
	SparkManager.getSessionManager().changePresence(myPresence);

    }

    public void connectionClosedOnError(final Exception ex) {
	String errorMessage = Res.getString("message.disconnected.error");

	if (ex != null && ex instanceof XMPPException) {
	    XMPPException xmppEx = (XMPPException) ex;
	    StreamError error = xmppEx.getStreamError();
	    String reason = error.getCode();

	    if ("conflict".equals(reason)) {
		errorMessage = Res
			.getString("message.disconnected.conflict.error");
	    } else if ("system-shutdown".equals(reason)) {
		errorMessage = Res.getString("message.disconnected.shutdown");
	    } else {
		errorMessage = Res.getString("message.general.error", reason);
	    }
	}

	switch (localPreferences.getReconnectPanelType()) {
	case 0:
	    final String message = errorMessage;
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    _reconnectPanel.setClosedOnError(true);
		    reconnect(message);
		}
	    });
	    break;
	case 1:
	    switchAllUserOffline(true);
	    _reconnectpanelsmall.setReconnectText(errorMessage);
	    break;

	case 2:
	    switchAllUserOfflineNoGroupEntry(true);
	    _reconnectpanelicon.setReconnectText(errorMessage);
	    break;

	}
    }

    private void removeAllUsers() {
        // Behind the scenes, move everyone to the offline group.
        for (ContactGroup contactGroup : new ArrayList<ContactGroup>(getContactGroups())) {
            contactGroup.removeAllContacts();
        }

    }

    public void reconnectingIn(int i) {
	
	switch (localPreferences.getReconnectPanelType()) {
	case 0:
	    if (i == 0) {
		_reconnectPanel.setReconnectText(Res
			.getString("message.reconnect.attempting"));
	    } else {
		_reconnectPanel.setReconnectText(Res.getString(
			"message.reconnect.wait", i));
	    }
	    break;

	case 1:
	    if (i == 0) {
		_reconnectpanelsmall.setReconnectText(Res
			.getString("message.reconnect.attempting"));
	    } else {
		_reconnectpanelsmall.setReconnectText(Res.getString(
			"message.reconnect.wait", i));
	    }
	    break;

	case 2:
	    if (i == 0) {
		_reconnectpanelicon.setReconnectText(Res
			.getString("message.reconnect.attempting"));
	    } else {
		_reconnectpanelicon.setReconnectText(Res.getString(
			"message.reconnect.wait", i));
	    }
	    break;
	}

    }

    public void reconnectionSuccessful() {
        clientReconnected();
    }

    public void reconnectionFailed(Exception exception) {

	switch (localPreferences.getReconnectPanelType()) {

	case 0:
	    _reconnectPanel.setReconnectText(Res
		    .getString("message.reconnect.failed"));
	    break;
	case 1:
	    _reconnectpanelsmall.setReconnectText(Res
		    .getString("message.reconnect.failed"));
	    break;
	case 2:
	    _reconnectpanelicon.setReconnectText(Res
		    .getString("message.reconnect.failed"));
	    break;
	}

    }

    /**
     * Moves a <code>ContactItem</code> to an offline state.
     *
     * @param contactItem the ContactItem.
     */
    private void moveToOffline(ContactItem contactItem) {
        offlineGroup.addContactItem(contactItem);
        	  
        String jid = contactItem.getJID();
        Boolean isFiled = false;

        final Roster roster = SparkManager.getConnection().getRoster();
        for (RosterGroup group : roster.getEntry(jid).getGroups()) {
            ContactGroup contactGroup = getContactGroup(group.getName());
            if(contactGroup == null && group.getName() != ""){
            	contactGroup = addContactGroup(group.getName());
            }
            if (contactGroup != null) {
                isFiled = true;
                contactGroup.addOfflineContactItem(contactItem.getAlias(), contactItem.getNickname(), contactItem.getJID(), contactItem.getStatus());
            }
        }
        if (!isFiled) {
            getUnfiledGroup().addOfflineContactItem(contactItem.getAlias(), contactItem.getNickname(), contactItem.getJID(), contactItem.getStatus());
        }
        
        if(localPreferences.isOfflineUsersShown() == false)
        {
	        for (ContactGroup group : getContactGroups()) 
	        {
	       	  if(group != offlineGroup)
	       	  {
	       		  group.toggleOfflineVisibility(false);
	       	  }
	        }
        }
    }

    private ContactGroup getUnfiledGroup() {
        if (unfiledGroup == null) {
            // Add Unfiled Group
        	if(EventQueue.isDispatchThread()) {
			unfiledGroup = UIComponentRegistry.createContactGroup(Res.getString("unfiled"));
                addContactGroup(unfiledGroup);
        	}
        	else {
        		try {
	        		EventQueue.invokeAndWait(new Runnable(){
	        			public void run() {
						unfiledGroup = UIComponentRegistry.createContactGroup(Res.getString("unfiled"));
	        	            addContactGroup(unfiledGroup);
	        			}
	        		});
        		}catch(Exception ex) {
        			ex.printStackTrace();
        		}
        	}
        }
        return unfiledGroup;
    }

    /**
     * Sorts ContactItems.
     */
    public final static Comparator<ContactItem> ContactItemComparator = new Comparator<ContactItem>() {
        public int compare(ContactItem item1, ContactItem item2) {
            return item1.getDisplayName().toLowerCase().compareTo(item2.getDisplayName().toLowerCase());
        }
    };
    public void showAddContact(String contact)
    {
	addContactMenu.doClick();
    };
    
    public ContactItem getActiveItem() {
	return activeItem;
    }
}