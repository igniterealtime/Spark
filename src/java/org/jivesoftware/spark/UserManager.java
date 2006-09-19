/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.profile.VCardManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;

/**
 * Handles all users in the agent application. Each user or chatting user can be referenced from the User
 * Manager. You would use the UserManager to get visitors in a chat room or secondary agents.
 */
public class UserManager {

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


    /**
     * Return a Collection of all user jids found in the specified room.
     *
     * @param room    the name of the chatroom
     * @param fullJID set to true if you wish to have the full jid with resource, otherwise false
     *                for the bare jid.
     * @return a Collection of jids found in the room.
     */
    public Collection getUserJidsInRoom(String room, boolean fullJID) {
        final List returnList = new ArrayList();


        return returnList;
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
    public Collection getAllParticipantsInRoom(ChatRoom chatRoom) {
        final String room = chatRoom.getRoomname();
        final List returnList = new ArrayList();


        return returnList;
    }


    public String getUserNicknameFromJID(String jid) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem item = contactList.getContactItemByJID(jid);
        if (item != null) {
            return item.getNickname();
        }

        return unescapeJID(jid);
    }

    /**
     * Escapes a complete JID by examing the Node itself and escaping
     * when neccessary.
     * @param jid the users JID
     * @return the escaped JID.
     */
    public static String escapeJID(String jid){
        if(jid == null){
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
     * @param jid the users jid.
     * @return the unescaped JID.
     */
    public static String unescapeJID(String jid){
        if(jid == null){
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
     * Returns the full jid w/ resource of a user by their nickname
     * in the ContactList.
     *
     * @param nickname the nickname of the user.
     * @return the full jid w/ resource of the user.
     */
    public String getJIDFromNickname(String nickname) {
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem item = contactList.getContactItemByNickname(nickname);
        if (item != null) {
            return getFullJID(item.getFullJID());
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
        Roster roster = SparkManager.getConnection().getRoster();
        Presence presence = roster.getPresence(jid);
        if (presence != null) {
            return presence.getFrom();
        }

        return null;

    }

    /**
     * Returns the Icon associated with the presence.
     *
     * @param presence the presence.
     * @return the icon.
     */
    public Icon getIconFromPresence(Presence presence) {
        StatusItem statusItem = SparkManager.getWorkspace().getStatusBar().getItemFromPresence(presence);
        Icon tabIcon = null;

        if (statusItem == null) {
            tabIcon = SparkRes.getImageIcon(SparkRes.CLEAR_BALL_ICON);
        }
        else {
            String status = presence.getStatus();
            if (status != null && status.indexOf("phone") != -1) {
                tabIcon = SparkRes.getImageIcon(SparkRes.ON_PHONE_IMAGE);
            }
            else {
                tabIcon = statusItem.getIcon();
            }
        }

        Icon icon = SparkManager.getChatManager().getPresenceIconForContactHandler(presence);
        if (icon != null) {
            tabIcon = icon;
        }

        return tabIcon;
    }
}