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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.muc.MucFeature;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static org.jivesoftware.smackx.muc.MucConfigFormManager.*;

/**
 * ConferenceUtils allow for basic joining and inviting of users.
 */
public class ConferenceUtils {

    private ConferenceUtils() {
    }

    /**
     * Return the number of occupants in a room.
     *
     * @param roomJID the full JID of the conference room. (ex. dev@conference.jivesoftware.com)
     * @return the number of occupants in the room if available.
     * @throws XMPPException thrown if an error occured during retrieval of the information.
     * @throws InterruptedException 
     */
    public static int getNumberOfOccupants(EntityBareJid roomJID) throws SmackException, XMPPException, InterruptedException {
        final RoomInfo roomInfo = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getRoomInfo( roomJID );
        return roomInfo.getOccupantsCount();
    }

    /**
     * Retrieve the date (in yyyyMMdd) format of the time the room was created.
     *
     * @param roomJID the jid of the room.
     * @return the formatted date.
     * @throws Exception throws an exception if we are unable to retrieve the date.
     */
    public static String getCreationDate(EntityBareJid roomJID) throws Exception {
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());

        final DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        DiscoverInfo infoResult = discoManager.discoverInfo(roomJID);
        DataForm dataForm = infoResult.getExtension(DataForm.class);
        if (dataForm == null) {
            return "Not available";
        }
        String creationDate = "";
        for ( final FormField field : dataForm.getFields() ) {
            String label = field.getLabel();


            if ("Creation date".equalsIgnoreCase(label)) {
                for ( CharSequence value : field.getValues() ) {
                    creationDate = value.toString();
                    Date date = dateFormatter.parse(creationDate);
                    creationDate = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.MEDIUM).format(date);
                }
            }
        }
        return creationDate;
    }

    /**
     * Enters a GroupChatRoom on the event thread.
     *
     * @param roomName the name of the room.
     * @param roomJID  the jid of the room.
     * @param password the rooms password (if any).
     * @return the GroupChatRoom created.
     */
    public static GroupChatRoom enterRoomOnSameThread(final CharSequence roomName, EntityBareJid roomJID, String password )
    {
        final JoinRoomSwingWorker worker = new JoinRoomSwingWorker( roomJID, null, password, roomName.toString() );
        worker.start();
        return (GroupChatRoom) worker.get(); // blocks until completed.
    }

    public static void joinConferenceOnSeperateThread(final CharSequence roomName, EntityBareJid roomJID, final Resourcepart nickname, String password) {
        joinConferenceOnSeperateThread(roomName, roomJID, nickname, password, null, null);
    }

    /**
     * Joins a conference room using another thread. This allows for a smoother opening of rooms.
     *
     * @param roomName the name of the room.
     * @param roomJID  the jid of the room.
     * @param nickname THe nickname that the user joining will be using (optional).
     * @param password the rooms password if required.
     */
    public static void joinConferenceOnSeperateThread(final CharSequence roomName, EntityBareJid roomJID, final Resourcepart nickname, String password, final String inviteMessage, final Collection<EntityBareJid> invites) {

        final JoinRoomSwingWorker worker = new JoinRoomSwingWorker( roomJID, nickname, password, roomName.toString() );

        if ( invites != null && !invites.isEmpty() )
        {
            final InviteSwingWorker inviteSwingWorker = new InviteSwingWorker( roomJID, invites, inviteMessage );
            worker.setFollowUp( inviteSwingWorker );
        }

        worker.start();
    }

    /**
     * Presents the user with a dialog pre-filled with the room name and the jid.
     *
     * @param roomName the name of the room.
     * @param roomJID  the rooms jid.
     */
    public static void joinConferenceRoom(final String roomName, EntityBareJid roomJID) {
        JoinConferenceRoomDialog joinDialog = new JoinConferenceRoomDialog();
        joinDialog.joinRoom(roomJID, roomName);
        changePresenceToAvailableIfInvisible();
    }

    /**
     * Invites users to an existing room.
     *
     * @param chat The room to invite people into.
     * @param jids a collection of the users to invite.
     */
    public static void inviteUsersToRoom(MultiUserChat chat, Collection<Jid> jids, boolean randomName ) {
        inviteUsersToRoom(chat.getRoom().asDomainBareJid(), chat.getRoom().toString(), jids, randomName );
    }

    /**
     * Invites users to an existing room.
     *
     * @param serviceName the service name to use.
     * @param roomName    the name of the room.
     * @param jids        a collection of the users to invite.
     */
    public static void inviteUsersToRoom(DomainBareJid serviceName, String roomName, Collection<Jid> jids, boolean randomName) {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        boolean useTextField = pref.isUseAdHocRoom();
        Collection<BookmarkedConference> rooms = null;
        if (!useTextField) {
            try {
                rooms = retrieveBookmarkedConferences();
            } catch (Exception ex) {
                Log.error(ex);
            }
            useTextField = !randomName || (rooms == null || rooms.size() == 0);
        }
        InvitationDialog inviteDialog = new InvitationDialog(useTextField);
        inviteDialog.inviteUsersToRoom(serviceName, rooms, roomName, jids);
    }

    public static Collection<BookmarkedConference> retrieveBookmarkedConferences() throws XMPPException, SmackException, InterruptedException
    {
        BookmarkManager manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());
        return manager.getBookmarkedConferences();
    }

    /**
     * Returns true if the room requires a password.
     *
     * @param roomJID the JID of the room.
     * @return true if the room requires a password.
     */
    public static boolean isPasswordRequired(EntityBareJid roomJID) {
        // Check to see if the room is password protected
    	ServiceDiscoveryManager discover = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());

        try {
            DiscoverInfo info = discover.discoverInfo(roomJID);
            return info.containsFeature(MucFeature.PasswordProtected.getName());
        }
        catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }
        return false;
    }

    /**
     * Creates a private conference.
     *
     * @param serviceName the service name to use for the private conference.
     * @param message     the message sent to each individual invitee.
     * @param roomName    the name of the room to create.
     * @param jids        a collection of the user JIDs to invite.
     * @throws SmackException thrown if an error occurs during room creation.
     * @throws InterruptedException 
     */
    public static void createPrivateConference(DomainBareJid serviceName, String message, String roomName, Collection<EntityBareJid> jids) throws SmackException, InterruptedException
    {
        final EntityBareJid roomJID = JidCreate.entityBareFromUnescapedOrThrowUnchecked(roomName + "@" + serviceName);
        final MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getMultiUserChat( roomJID );
        final LocalPreferences pref = SettingsManager.getLocalPreferences();


        final GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(multiUserChat);
        try {
            // Attempt to create room.
            multiUserChat.create(pref.getNickname());
        }
        catch (XMPPException e) {
            // TODO: Simply let this method throw XMPPException, instead of wrapping it here.
            throw new SmackException.SmackWrappedException(e);
        }

        try {
            // Since this is a private room, make the room not public and set user as owner of the room.
            FillableForm submitForm = multiUserChat.getConfigurationForm().getFillableForm();
            submitForm.setAnswer(MUC_ROOMCONFIG_PUBLICLYSEARCHABLEROOM, false);
            submitForm.setAnswer(MUC_ROOMCONFIG_ROOMNAME, roomName);

            multiUserChat.sendConfigurationForm(submitForm);
        }
        catch (XMPPException | SmackException e1) {
            Log.error("Unable to send conference room chat configuration form.", e1);
        }


        ChatManager chatManager = SparkManager.getChatManager();

        // Check if room already is open
        try {
            chatManager.getChatContainer().getChatRoom(room.getBareJid());
        }
        catch (ChatRoomNotFoundException e) {
            chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);
        }

        for (EntityBareJid jid : jids) {
            multiUserChat.invite(jid, message);
            room.getTranscriptWindow().insertNotificationMessage(Res.getString("message.waiting.for.user.to.join",jid), ChatManager.NOTIFICATION_COLOR);
        }
    }

    /**
     * Returns an explanation for the exception.
     *
     * @param error The XMPP error
     * @return the reason for the exception.
     * @see <a href="http://xmpp.org/extensions/xep-0045.html#enter-errorcodes">XEP-0045 Error Conditions</a>
     */
    public static String getReason(StanzaError error) {
        if (error == null) {
            return Res.getString("message.error.no.response");
        }

        switch ( error.getCondition() )
        {
            case conflict:
                return Res.getString("message.error.nickname.in.use");
            case forbidden:
                return Res.getString("message.you.have.been.banned");
            case remote_server_not_found:
            case item_not_found:
                return Res.getString("message.error.room.not.exist");
            case not_acceptable:
                return Res.getString("message.error.must.use.reserved.nick");
            case not_allowed:
                return Res.getString("message.error.no.permission.create.room");
            case not_authorized:
                return Res.getString("message.error.room.password.incorrect");
            case registration_required:
                return Res.getString("message.error.not.member");
            default:
                String reason = error.getDescriptiveText() == null ? error.getCondition().toString() : error.getDescriptiveText();
                return Res.getString("message.default.error") + ": " + reason;
        }

    }

    final static List<EntityBareJid> unclosableChatRooms = new ArrayList<>();
	public synchronized static void addUnclosableChatRoom(EntityBareJid jid) {
		unclosableChatRooms.add(jid);
	}

	public static void addUnclosableChatRoom(String jidString) {
	    EntityBareJid jid;
        try {
            jid = JidCreate.entityBareFrom(jidString);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
	    synchronized (ConferenceUtils.class) {
	        unclosableChatRooms.add(jid);
	    }
	}

    public static boolean isChatRoomClosable(Component c) {
        if (c instanceof GroupChatRoom) {
            GroupChatRoom groupChatRoom = (GroupChatRoom) c;
            EntityBareJid roomName = groupChatRoom.getChatRoom().getBareJid();
            return !unclosableChatRooms.contains(roomName);
        } else {
            return true;
        }
    }

	public static boolean confirmToRevealVisibility() {
		Presence currentPresence = SparkManager.getWorkspace().getStatusBar().getPresence();
		
		if (!PresenceManager.isInvisible(currentPresence))
			return true;

		int reply = JOptionPane.showConfirmDialog(null,
				Res.getString("dialog.confirm.to.reveal.visibility.msg"),
				Res.getString("dialog.confirm.to.reveal.visibility.title"),
				JOptionPane.YES_NO_OPTION);
		return reply == JOptionPane.YES_OPTION;
	}
	    
    /**
	 * If the current present is 'invisible' then this method change it to
	 * 'Online'. Decided 'invisibility' is a kind of private thing. So if user
	 * would like to go to the conference then the user reveal him/herself. i.e.
	 * if current presence is invisible then we should go to visible. Otherwise
	 * all 'invisible' users will be shown as 'Offline' on the conference
	 * participant list which is confusing. This method mostly is used in
	 * ConferenceUtils. Usually it is called after user is joined to a room.
	 */
	public static void changePresenceToAvailableIfInvisible() {
		Presence currentPresence = SparkManager.getWorkspace().getStatusBar().getPresence();
		if (PresenceManager.isInvisible(currentPresence)) {
			PrivacyManager.getInstance().goToVisible();
			SparkManager.getSessionManager().changePresence(PresenceManager.getAvailablePresence());
		}
	}

}

