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
package org.jivesoftware.spark;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.component.JContactItemField;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.profile.VCardManager;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 * Handles all users in the agent application. Each user or chatting user can be referenced from the User
 * Manager. You would use the UserManager to get visitors in a chat room or secondary agents.
 */
public class UserManager {

    private Map<JFrame,Component> parents = new HashMap<JFrame,Component>();

    public UserManager() {
    }

    public String getNickname() {
        final VCardManager vCardManager = SparkManager.getVCardManager();
        VCard vcard = vCardManager.getVCard();
        if (vcard == null) {
            return SparkManager.getSessionManager().getUsername();
        }
        else {
            String nickname = vcard.getNickName();
            if (ModelUtil.hasLength(nickname)) {
                return nickname;
            }
            else {
                String firstName = vcard.getFirstName();
                if (ModelUtil.hasLength(firstName)) {
                    return firstName;
                }
            }
        }

        // Default to node if nothing.
        String username = SparkManager.getSessionManager().getUsername();
        username = StringUtils.unescapeNode(username);

        return username;
    }
    
    public String getNickname(String fullJID) {
    	String vcardNickname = null;
        VCard vCard = SparkManager.getVCardManager().getVCard(fullJID);
        if (vCard != null && vCard.getError() == null) {
            String firstName = vCard.getFirstName();
            String lastName = vCard.getLastName();
            String nickname = vCard.getNickName();
            if (ModelUtil.hasLength(nickname)) {
                vcardNickname = nickname;
            }
            else if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
                vcardNickname = firstName + " " + lastName;
            }
            else if (ModelUtil.hasLength(firstName)) {
                vcardNickname = firstName;
            }
        }
        
        return vcardNickname;
    }


    /**
     * Return a Collection of all user jids found in the specified room.
     *
     * @param room    the name of the chatroom
     * @param fullJID set to true if you wish to have the full jid with resource, otherwise false
     *                for the bare jid.
     * @return a Collection of jids found in the room.
     */
    public Collection<String> getUserJidsInRoom(String room, boolean fullJID) {
        return new ArrayList<String>();
    }

    /**
     * Checks to see if the user is an owner of the specified room.
     *
     * @param groupChatRoom the group chat room.
     * @param nickname      the user's nickname.
     * @return true if the user is an owner.
     */
    public boolean isOwner(GroupChatRoom groupChatRoom, String nickname) {
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null) {
            String affiliation = occupant.getAffiliation();
            if ("owner".equals(affiliation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the Occupant is the owner of the room.
     *
     * @param occupant the occupant of a room.
     * @return true if the user is an owner.
     */
    public boolean isOwner(Occupant occupant) {
        if (occupant != null) {
            String affiliation = occupant.getAffiliation();
            if ("owner".equals(affiliation)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if the Occupant is a Member in this Room<br>
     * <b>admins and owners are also members!!!</b>
     * @param occupant
     * @return true if member, else false
     */
    public boolean isMember(Occupant occupant) {
	if (occupant != null) {
	    String affiliation = occupant.getAffiliation();
	    if ("member".equals(affiliation) || affiliation.equals("owner")
		    || affiliation.equals("admin")) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Checks to see if the Occupant is a moderator.
     *
     * @param groupChatRoom the group chat room.
     * @param nickname      the nickname of the user.
     * @return true if the user is a moderator.
     */
    public boolean isModerator(GroupChatRoom groupChatRoom, String nickname) {
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null) {
            String role = occupant.getRole();
            if ("moderator".equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the Occupant is a moderator.
     *
     * @param occupant the Occupant of a room.
     * @return true if the user is a moderator.
     */
    public boolean isModerator(Occupant occupant) {
        if (occupant != null) {
            String role = occupant.getRole();
            if ("moderator".equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the user is either an owner or admin of a room.
     *
     * @param groupChatRoom the group chat room.
     * @param nickname      the user's nickname.
     * @return true if the user is either an owner or admin of the room.
     */
    public boolean isOwnerOrAdmin(GroupChatRoom groupChatRoom, String nickname) {
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null) {
            String affiliation = occupant.getAffiliation();
            if ("owner".equals(affiliation) || "admin".equals(affiliation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the user is either an owner or admin of the given room.
     *
     * @param occupant the <code>Occupant</code> to check.
     * @return true if the user is either an owner or admin of the room.
     */
    public boolean isOwnerOrAdmin(Occupant occupant) {
        if (occupant != null) {
            String affiliation = occupant.getAffiliation();
            if ("owner".equals(affiliation) || "admin".equals(affiliation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the occupant of the room identified by their nickname.
     *
     * @param groupChatRoom the GroupChatRoom.
     * @param nickname      the users nickname.
     * @return the Occupant found.
     */
    public Occupant getOccupant(GroupChatRoom groupChatRoom, String nickname) {
        String userJID = groupChatRoom.getRoomname() + "/" + nickname;
        Occupant occ = null;
        try {
            occ = groupChatRoom.getMultiUserChat().getOccupant(userJID);
        }
        catch (Exception e) {
            Log.error(e);
        }
        return occ;
    }

    /**
     * Checks the nickname of a user in a room and determines if they are an
     * administrator of the room.
     *
     * @param groupChatRoom the GroupChatRoom.
     * @param nickname      the nickname of the user. Note: In MultiUserChats, users nicknames
     *                      are defined by the resource(ex.theroom@conference.jivesoftware.com/derek) would have
     *                      derek as a nickname.
     * @return true if the user is an admin.
     */
    public boolean isAdmin(GroupChatRoom groupChatRoom, String nickname) {
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null) {
            String affiliation = occupant.getAffiliation();
            if ("admin".equals(affiliation)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVoice(GroupChatRoom groupChatRoom, String nickname) {
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null) {
            String role = occupant.getRole();
            if ("visitor".equals(role)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns a Collection of all <code>ChatUsers</code> in a ChatRoom.
     *
     * @param chatRoom the ChatRoom to inspect.
     * @return the Collection of all ChatUsers.
     * @see <code>ChatUser</code>
     */
    public Collection<String> getAllParticipantsInRoom(ChatRoom chatRoom) {
        return new ArrayList<String>();
    }


    public String getUserNicknameFromJID(String jid) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem item = contactList.getContactItemByJID(jid);
        if (item != null) {
            return item.getDisplayName();
        }

        return unescapeJID(jid);
    }

    /**
     * Escapes a complete JID by examing the Node itself and escaping
     * when neccessary.
     *
     * @param jid the users JID
     * @return the escaped JID.
     */
    public static String escapeJID(String jid) {
        if (jid == null) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();
        String node = StringUtils.parseName(jid);
        String restOfJID = jid.substring(node.length());
        builder.append(StringUtils.escapeNode(node));
        builder.append(restOfJID);
        return builder.toString();
    }

    /**
     * Unescapes a complete JID by examing the node itself and unescaping when necessary.
     *
     * @param jid the users jid.
     * @return the unescaped JID.
     */
    public static String unescapeJID(String jid) {
        if (jid == null) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();
        String node = StringUtils.parseName(jid);
        String restOfJID = jid.substring(node.length());
        builder.append(StringUtils.unescapeNode(node));
        builder.append(restOfJID);
        return builder.toString();
    }

    /**
     * Returns the full jid w/ resource of a user by their display name
     * in the ContactList.
     *
     * @param displayName the displayed name of the user.
     * @return the full jid w/ resource of the user.
     */
    public String getJIDFromDisplayName(String displayName) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem item = contactList.getContactItemByDisplayName(displayName);
        if (item != null) {
            return getFullJID(item.getJID());
        }

        return null;
    }

    /**
     * Returns the full jid (with resource) based on the user's jid.
     *
     * @param jid the users bare jid.
     * @return the full jid with resource.
     */
    public String getFullJID(String jid) {
        Presence presence = PresenceManager.getPresence(jid);
        return presence.getFrom();
    }


    public void searchContacts(String contact, final JFrame parent) {
        if (parents.get(parent) == null && parent.getGlassPane() !=null) {
        	parents.put(parent, parent.getGlassPane());
        }

        // Make sure we are using the default glass pane
        final Component glassPane = parents.get(parent);
        parent.setGlassPane(glassPane);

        final Map<String, ContactItem> contactMap = new HashMap<String, ContactItem>();
        final List<ContactItem> contacts = new ArrayList<ContactItem>();

        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        for (ContactGroup contactGroup : contactList.getContactGroups()) {
            contactGroup.clearSelection();
            for (ContactItem contactItem : contactGroup.getContactItems()) {
                if (!contactMap.containsKey(contactItem.getJID())) {
                    contacts.add(contactItem);
                    contactMap.put(contactItem.getJID(), contactItem);
                }
            }
        }

        // Sort
        Collections.sort(contacts, itemComparator);

        final JContactItemField contactField = new JContactItemField(new ArrayList<ContactItem>(contacts));


        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new GridBagLayout());
        JLabel enterLabel = new JLabel(Res.getString("label.contact.to.find"));
        enterLabel.setFont(new Font("dialog", Font.BOLD, 10));
        layoutPanel.add(enterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        layoutPanel.add(contactField, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        layoutPanel.setBorder(BorderFactory.createBevelBorder(0));
        
        contactField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (ModelUtil.hasLength(contactField.getText())) {
                        ContactItem item = contactMap.get(contactField.getText());
                        if (item == null) {
                            item = contactField.getSelectedContactItem();
                        }
                        if (item != null) {
                            parent.setGlassPane(glassPane);
                            parent.getGlassPane().setVisible(false);
                            contactField.dispose();
                            SparkManager.getChatManager().activateChat(item.getJID(), item.getDisplayName());
                        }
                    }

                }
                else if (keyEvent.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    parent.setGlassPane(glassPane);
                    parent.getGlassPane().setVisible(false);
                    contactField.dispose();
                }
            }
        });

        contactField.getList().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
        	if(SwingUtilities.isRightMouseButton(e))
        	{
        	    contactField.setSelectetIndex(e);
        	    ContactItem item = contactField.getSelectedContactItem();
        	    MouseEvent exx = new MouseEvent((Component) e.getSource(),e.getID(), e.getWhen(),e.getModifiers(),e.getX()+20, e.getY(), e.getClickCount(), false);
        	    SparkManager.getContactList().setSelectedUser(item.getJID());
        	    SparkManager.getContactList().showPopup(contactField.getPopup(),exx,item);
        	}
        	
                if (e.getClickCount() == 2) {
                    if (ModelUtil.hasLength(contactField.getText())) {
                        ContactItem item = contactMap.get(contactField.getText());
                        if (item == null) {
                            item = contactField.getSelectedContactItem();
                        }
                        if (item != null) {
                            parent.setGlassPane(glassPane);
                            parent.getGlassPane().setVisible(false);
                            contactField.dispose();
                            SparkManager.getChatManager().activateChat(item.getJID(), item.getDisplayName());
                        }
                    }
                }
            }
        });


        final JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(layoutPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 200, 0));
        mainPanel.setOpaque(false);

        contactField.setText(contact);
        parent.setGlassPane(mainPanel);
        parent.getGlassPane().setVisible(true);
        contactField.focus();

        mainPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                parent.setGlassPane(glassPane);
                parent.getGlassPane().setVisible(false);
                contactField.dispose();
            }
        });

        parent.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                parent.setGlassPane(glassPane);
                parent.getGlassPane().setVisible(false);
                contactField.dispose();
                parent.removeWindowListener(this);
            }

            public void windowDeactivated(final WindowEvent windowEvent) {
                TimerTask task = new SwingTimerTask() {
                    public void doRun() {
                        if (contactField.canClose()) {
                            windowClosing(windowEvent);
                        }
                    }
                };

                TaskEngine.getInstance().schedule(task, 250);
            }
        });
    }

    /**
     * Returns the correct JID based on the number of resources signed in.
     *
     * @param jid the users jid.
     * @return the valid jid to use.
     */
    public static String getValidJID(String jid) {
        Roster roster = SparkManager.getConnection().getRoster();
        Iterator<Presence> presences = roster.getPresences(jid);
        int count = 0;
        Presence p = null;
        if (presences.hasNext()) {
            p = presences.next();
            count++;
        }

        if (count == 1 && p != null) {
            return p.getFrom();
        }
        else {
            return jid;
        }
    }


    /**
     * Sorts ContactItems.
     */
    final Comparator<ContactItem> itemComparator = new Comparator<ContactItem>() {
        public int compare(ContactItem item1, ContactItem item2) {
            return item1.getDisplayName().toLowerCase().compareTo(item2.getDisplayName().toLowerCase());
        }
    };

}



