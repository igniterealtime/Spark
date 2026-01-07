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
package org.jivesoftware.spark;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MUCRole;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
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
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import static org.jivesoftware.spark.ui.ContactItem.CONTACT_ITEM_COMPARATOR;

/**
 * Handles all users in the agent application. Each user or chatting user can be referenced from the User
 * Manager. You would use the UserManager to get visitors in a chat room or secondary agents.
 */
public class UserManager {

    private final Map<JFrame,Component> parents = new HashMap<>();

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
        username = XmppStringUtils.unescapeLocalpart(username);

        return username;
    }
    
    public String getNickname(BareJid jid) {
    	String vcardNickname = null;
        VCard vCard = SparkManager.getVCardManager().getVCard(jid);
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
    public Collection<String> getUserJidsInRoom(EntityBareJid room, boolean fullJID) {
        return new ArrayList<>();
    }

    /**
     * Checks to see if the user is an owner of the specified room.
     *
     * @param groupChatRoom the group chat room.
     * @param nickname      the user's nickname.
     * @return true if the user is an owner.
     */
    public boolean isOwner(GroupChatRoom groupChatRoom, Resourcepart nickname)
    {
        return isOwner( getOccupant( groupChatRoom, nickname ) );
    }

    /**
     * Checks to see if the Occupant is the owner of the room.
     *
     * @param occupant the occupant of a room.
     * @return true if the user is an owner.
     */
    public boolean isOwner(Occupant occupant)
    {
        return occupant != null && occupant.getAffiliation() == MUCAffiliation.owner;
    }

    /**
     * Checks if the Occupant is a Member in this Room<br>
     * <b>admins and owners are also members!!!</b>
     * @param occupant
     * @return true if member, else false
     */
    public boolean isMember(Occupant occupant)
    {
        return occupant != null && ( occupant.getAffiliation() == MUCAffiliation.owner || occupant.getAffiliation() == MUCAffiliation.member || occupant.getAffiliation() == MUCAffiliation.admin );
    }

    /**
     * Checks to see if the Occupant is a moderator.
     *
     * @param groupChatRoom the group chat room.
     * @param nickname      the nickname of the user.
     * @return true if the user is a moderator.
     */
    public boolean isModerator(GroupChatRoom groupChatRoom, Resourcepart nickname)
    {
        return isModerator( getOccupant( groupChatRoom, nickname ) );
    }

    /**
     * Checks to see if the Occupant is a moderator.
     *
     * @param occupant the Occupant of a room.
     * @return true if the user is a moderator.
     */
    public boolean isModerator(Occupant occupant) {
        return occupant != null && occupant.getRole() == MUCRole.moderator;
    }

    /**
     * Checks to see if the user is either an owner or admin of a room.
     *
     * @param groupChatRoom the group chat room.
     * @param nickname      the user's nickname.
     * @return true if the user is either an owner or admin of the room.
     */
    public boolean isOwnerOrAdmin(GroupChatRoom groupChatRoom, Resourcepart nickname) {
        return isOwnerOrAdmin( getOccupant(groupChatRoom, nickname) );
    }

    /**
     * Checks to see if the user is either an owner or admin of the given room.
     *
     * @param occupant the <code>Occupant</code> to check.
     * @return true if the user is either an owner or admin of the room.
     */
    public boolean isOwnerOrAdmin(Occupant occupant) {
        return isOwner( occupant ) || isAdmin( occupant );
    }

    /**
     * Returns the occupant of the room identified by their nickname.
     *
     * @param groupChatRoom the GroupChatRoom.
     * @param nickname      the users nickname.
     * @return the Occupant found.
     */
    public Occupant getOccupant(GroupChatRoom groupChatRoom, Resourcepart nickname) {
        EntityFullJid userJID = JidCreate.entityFullFrom(groupChatRoom.getBareJid(), nickname);
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
    public boolean isAdmin(GroupChatRoom groupChatRoom, Resourcepart nickname) {
        return isAdmin( getOccupant(groupChatRoom, nickname) );
    }

    /**
     * Checks to see if the Occupant is an admin.
     *
     * @param occupant the occupant of a room.
     * @return true if the user is an admin.
     */
    public boolean isAdmin(Occupant occupant)
    {
        return occupant != null && occupant.getAffiliation() == MUCAffiliation.admin;
    }

    public boolean hasVoice(GroupChatRoom groupChatRoom, Resourcepart nickname) {
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null) {
            return MUCRole.visitor != occupant.getRole();
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
        return new ArrayList<>();
    }

    public String getUserNicknameFromJID(BareJid jid) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem item = contactList.getContactItemByJID(jid);
        if (item != null) {
            return item.getDisplayName();
        }

        return unescapeJID(jid);
    }

    public Resourcepart getUserNicknameAsResourcepartFromJID(BareJid jid) {
        return Resourcepart.fromOrThrowUnchecked(getUserNicknameFromJID(jid));
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
        String node = XmppStringUtils.parseLocalpart(jid);
        String restOfJID = jid.substring(node.length());
        builder.append(XmppStringUtils.escapeLocalpart(node));
        builder.append(restOfJID);
        return builder.toString();
    }

    public static String unescapeJID(CharSequence jid) {
        BareJid bareJid;
        try {
            bareJid = JidCreate.bareFrom(jid);
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }
        return unescapeJID(bareJid);
    }

    /**
     * Unescapes a complete JID by examining the node itself and unescaping when necessary.
     *
     * @param jid the users jid.
     * @return the unescaped JID.
     */
    public static String unescapeJID(BareJid jid) {
        if (jid == null) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();
        Localpart node = jid.getLocalpartOrNull();
        Domainpart restOfJID = jid.getDomain();
        if (node != null) {
            builder.append(XmppStringUtils.unescapeLocalpart(node.toString()));
            builder.append('@');
        }
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
    public EntityFullJid getJIDFromDisplayName(CharSequence displayName) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem item = contactList.getContactItemByDisplayName(displayName);
        if (item != null) {
            return getFullJID(item.getJid());
        }

        return null;
    }

    /**
     * Returns the full jid (with resource) based on the user's jid.
     *
     * @param bareJid the users bare JID.
     * @return the full JID with resource.
     */
    public EntityFullJid getFullJID(BareJid bareJid) {
        Presence presence = PresenceManager.getPresence(bareJid);
        Jid jid =  presence.getFrom();
        return jid.asEntityFullJidIfPossible();
    }


    public void searchContacts(String contact, final JFrame parent) {
        if (parents.get(parent) == null && parent.getGlassPane() !=null) {
        	parents.put(parent, parent.getGlassPane());
        }

        // Make sure we are using the default glass pane
        final Component glassPane = parents.get(parent);
        parent.setGlassPane(glassPane);

        final Map<String, ContactItem> contactMap = new HashMap<>();
        final List<ContactItem> contacts = new ArrayList<>();

        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        for (ContactGroup contactGroup : contactList.getContactGroups()) {
            contactGroup.clearSelection();
            for (ContactItem contactItem : contactGroup.getContactItems()) {
                if (contactMap.putIfAbsent(contactItem.getJid().toString(), contactItem) == null) {
                    contacts.add(contactItem);
                }
            }
        }

        // Sort
        contacts.sort(CONTACT_ITEM_COMPARATOR);

        final JContactItemField contactField = new JContactItemField( new ArrayList<>( contacts ));


        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new GridBagLayout());
        JLabel enterLabel = new JLabel(Res.getString("label.contact.to.find"));
        enterLabel.setFont(new Font("dialog", Font.BOLD, 10));
        layoutPanel.add(enterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        layoutPanel.add(contactField, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        layoutPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
        
        contactField.addKeyListener(new KeyAdapter() {
            @Override
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
                            SparkManager.getChatManager().activateChat(item.getJid(), item.getDisplayName());
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
            @Override
			public void mouseClicked(MouseEvent e) {
        	if(SwingUtilities.isRightMouseButton(e))
        	{
        	    contactField.setSelectetIndex(e);
        	    ContactItem item = contactField.getSelectedContactItem();
        	    MouseEvent exx = new MouseEvent((Component) e.getSource(),e.getID(), e.getWhen(),e.getModifiers(),e.getX()+20, e.getY(), e.getClickCount(), false);
        	    SparkManager.getContactList().setSelectedUser(item.getJid().asBareJid());
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
                            SparkManager.getChatManager().activateChat(item.getJid(), item.getDisplayName());
                        }
                    }
                }
            }
        });


        final JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(layoutPanel, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(SparkManager.getMainWindow().getTopToolBar().getHeight()+SparkManager.getWorkspace().getStatusBar().getHeight() + 47, 1, 5, 1), 200, 0));
        mainPanel.setOpaque(false);

        contactField.setText(contact);
        parent.setGlassPane(mainPanel);
        parent.getGlassPane().setVisible(true);
        contactField.focus();

        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent mouseEvent) {
                parent.setGlassPane(glassPane);
                parent.getGlassPane().setVisible(false);
                contactField.dispose();
            }
        });

        parent.addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent windowEvent) {
                parent.setGlassPane(glassPane);
                parent.getGlassPane().setVisible(false);
                contactField.dispose();
                parent.removeWindowListener(this);
            }

            @Override
			public void windowDeactivated(final WindowEvent windowEvent) {
                TimerTask task = new SwingTimerTask() {
                    @Override
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

}



