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
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.form.FillableForm;
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
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static org.jivesoftware.smackx.muc.MucConfigFormManager.*;

/**
 * ConferenceUtils allow for basic joining and inviting of users.
 */
public class ConferenceUtils {
    private static final LocalPreferences pref = SettingsManager.getLocalPreferences();

    private ConferenceUtils() {
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

    public static void joinConferenceOnSeparateThread(final CharSequence roomName, EntityBareJid roomJID, final Resourcepart nickname, String password) {
        joinConferenceOnSeparateThread(roomName, roomJID, nickname, password, null, null);
    }

    /**
     * Joins a conference room using another thread. This allows for a smoother opening of rooms.
     *
     * @param roomName the name of the room.
     * @param roomJID  the jid of the room.
     * @param nickname THe nickname that the user joining will be using (optional).
     * @param password the rooms password if required.
     */
    public static void joinConferenceOnSeparateThread(final CharSequence roomName, EntityBareJid roomJID, final Resourcepart nickname, String password, final String inviteMessage, final Collection<EntityBareJid> invites) {
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
     * @param roomInfo the info of the room.
     * @param roomJID  the rooms jid.
     */
    public static void joinConferenceRoom(RoomInfo roomInfo, EntityBareJid roomJID) {
        JoinConferenceRoomDialog joinDialog = new JoinConferenceRoomDialog();
        joinDialog.joinRoom(roomJID, roomInfo);
        changePresenceToAvailableIfInvisible();
    }

    /**
     * Invites users to an existing room.
     *
     * @param chat The room to invite people into.
     * @param jids a collection of the users to invite.
     */
    public static void inviteUsersToRoom(MultiUserChat chat, Collection<Jid> jids, boolean randomName ) {
        inviteUsersToRoom(chat.getRoom().asDomainBareJid(), chat.getRoom(), jids, randomName );
    }

    /**
     * Invites users to an existing room.
     *
     * @param serviceName the service name to use.
     * @param roomName    the name of the room.
     * @param jids        a collection of the users to invite.
     */
    public static void inviteUsersToRoom(DomainBareJid serviceName, EntityBareJid roomName, Collection<Jid> jids, boolean randomName) {
        boolean useTextField = pref.isUseAdHocRoom();
        List<BookmarkedConference> rooms = null;
        if (!useTextField) {
            try {
                rooms = retrieveBookmarkedConferences();
            } catch (Exception ex) {
                Log.error(ex);
            }
            useTextField = !randomName || (rooms == null || rooms.isEmpty());
        }
        InvitationDialog inviteDialog = new InvitationDialog(useTextField);
        inviteDialog.inviteUsersToRoom(serviceName, rooms, roomName, jids);
    }

    public static List<BookmarkedConference> retrieveBookmarkedConferences() throws XMPPException, SmackException, InterruptedException
    {
        BookmarkManager manager = BookmarkManager.getBookmarkManager(SparkManager.getConnection());
        return manager.getBookmarkedConferences();
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
        Localpart roomNamePart = Localpart.fromUnescapedOrThrowUnchecked(roomName);
        EntityBareJid roomJID = JidCreate.entityBareFrom(roomNamePart, serviceName);
        MultiUserChatManager mucManager = SparkManager.getMucManager();
        MultiUserChat multiUserChat = mucManager.getMultiUserChat(roomJID);
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
        // Check if the room already is opened
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

