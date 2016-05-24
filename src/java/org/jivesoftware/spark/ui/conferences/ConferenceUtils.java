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

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.PasswordDialog;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

/**
 * ConferenceUtils allow for basic joining and inviting of users.
 */
public class ConferenceUtils {

    private ConferenceUtils() {
    }

    /**
     * Return a list of available Conference rooms from the server
     * based on the service name.
     *
     * @param serviceName the service name (ex. conference@jivesoftware.com)
     * @return a collection of rooms.
     * @throws Exception if an error occured during fetch.
     */
    public static Collection<HostedRoom> getRoomList(String serviceName) throws Exception {
        return MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getHostedRooms( serviceName );
    }

    /**
     * Return the number of occupants in a room.
     *
     * @param roomJID the full JID of the conference room. (ex. dev@conference.jivesoftware.com)
     * @return the number of occupants in the room if available.
     * @throws XMPPException thrown if an error occured during retrieval of the information.
     */
    public static int getNumberOfOccupants(String roomJID) throws SmackException, XMPPException {
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
    public static String getCreationDate(String roomJID) throws Exception {
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());

        final DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        DiscoverInfo infoResult = discoManager.discoverInfo(roomJID);
        DataForm dataForm = infoResult.getExtension("x", "jabber:x:data");
        if (dataForm == null) {
            return "Not available";
        }
        String creationDate = "";
        for ( final FormField field : dataForm.getFields() ) {
            String label = field.getLabel();


            if (label != null && "Creation date".equalsIgnoreCase(label)) {
                for ( String value : field.getValues() ) {
                    creationDate = value;
                    Date date = dateFormatter.parse(creationDate);
                    creationDate = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.MEDIUM).format(date);
                }
            }
        }
        return creationDate;
    }

    public static void joinConferenceOnSeperateThread(final String roomName, String roomJID, String password) {
        joinConferenceOnSeperateThread(roomName, roomJID, password, null, null);
    }

    /**
     * Joins a conference room using another thread. This allows for a smoother opening of rooms.
     *
     * @param roomName the name of the room.
     * @param roomJID  the jid of the room.
     * @param password the rooms password if required.
     */
    public static void joinConferenceOnSeperateThread(final String roomName, String roomJID, String password, final String message, final Collection<String> jids) {
        ChatManager chatManager = SparkManager.getChatManager();
        LocalPreferences pref = SettingsManager.getRelodLocalPreferences();

        final MultiUserChat groupChat = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getMultiUserChat( roomJID );
        final String nickname = pref.getNickname().trim();

        // Check if room already exists. If so, join room again.
        try {
            GroupChatRoom chatRoom = (GroupChatRoom)chatManager.getChatContainer().getChatRoom(roomJID);
            MultiUserChat muc = chatRoom.getMultiUserChat();
            chatRoom.setPassword(password);
            if (!muc.isJoined()) {
                joinRoom(muc, nickname, password);
            }

            chatManager.getChatContainer().activateChatRoom(chatRoom);
            invite(groupChat, chatRoom, jids, message);
            return;
        }
        catch (ChatRoomNotFoundException e) {
            // Nothing to do
        }


        final GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(groupChat);
        room.setTabTitle(roomName);
        room.setPassword(password);

        if (isPasswordRequired(roomJID) && password == null) {
            final PasswordDialog passwordDialog = new PasswordDialog();
            passwordDialog.setPasswordField(pref.getGroupChatPassword(roomJID));
            password = passwordDialog.getPassword(Res.getString("title.password.required"), Res.getString("message.groupchat.require.password"), SparkRes.getImageIcon(SparkRes.LOCK_16x16), SparkManager.getFocusedComponent());
            if(passwordDialog.isCheckboxSelected() == true && password!=null)
            {
            	passwordDialog.savePassword(room.getRoomname(),password);
            }
            room.setPassword(password);
            if (!ModelUtil.hasLength(password)) {
                return;
            }
        }


        final List<String> errors = new ArrayList<>();
        final String userPassword = password;

        final SwingWorker startChat = new SwingWorker() {
            public Object construct() {
                if (!groupChat.isJoined()) {
                    int groupChatCounter = 0;
                    while (true) {
                        groupChatCounter++;
                        String joinName = nickname;
                        if (groupChatCounter > 1) {
                            joinName = joinName + groupChatCounter;
                        }
                        if (groupChatCounter < 10) {
                            try {
                            	if (!confirmToRevealVisibility())
                            		return null;

                                if (ModelUtil.hasLength(userPassword)) {
                                    groupChat.join(joinName, userPassword);
                                }
                                else {
                                    groupChat.join(joinName);
                                }
                                break;
                            }
                            catch (XMPPException | SmackException ex) {
                                XMPPError error = null;
                                if ( ex instanceof XMPPException.XMPPErrorException) {
                                    error = (( XMPPException.XMPPErrorException ) ex).getXMPPError();
                                }

                                final String errorText = ConferenceUtils.getReason( error );

                                errors.add( errorText );
                                if ( error != null && error.getCondition() != XMPPError.Condition.conflict )
                                {
                                    break;
                                }
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
                invite(groupChat, room, jids, message);
                return "ok";
            }

            public void finished() {
            	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                if (errors.size() > 0) {
                    String error = errors.get(0);
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), error, "Unable to join the room at this time.", JOptionPane.ERROR_MESSAGE);
                }
                else if (groupChat.isJoined()) {
                	changePresenceToAvailableIfInvisible();
                    ChatManager chatManager = SparkManager.getChatManager();
                    chatManager.getChatContainer().addChatRoom(room);
                    chatManager.getChatContainer().activateChatRoom(room);
                }
                else {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Unable to join the room.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        startChat.start();
    }

    private static void invite(MultiUserChat groupChat, GroupChatRoom room, Collection<String> jids, String message)
    {
        if (jids != null && message != null) {
            for (String jid : jids) {
                try
                {
                    groupChat.invite(jid, message);
                    room.getTranscriptWindow().insertNotificationMessage(
                            Res.getString("message.waiting.for.user.to.join", jid), ChatManager.NOTIFICATION_COLOR);
                }
                catch ( SmackException.NotConnectedException e )
                {
                    Log.warning( "Unable to invite " + jid + " to " + room, e );
                }
            }
        }
    }

    /**
     * Presents the user with a dialog pre-filled with the room name and the jid.
     *
     * @param roomName the name of the room.
     * @param roomJID  the rooms jid.
     */
    public static void joinConferenceRoom(final String roomName, String roomJID) {
        JoinConferenceRoomDialog joinDialog = new JoinConferenceRoomDialog();
        joinDialog.joinRoom(roomJID, roomName);
        changePresenceToAvailableIfInvisible();
    }


    /**
     * Joins a chat room without using the UI.
     *
     * @param groupChat the <code>MultiUserChat</code>
     * @param nickname  the nickname of the user.
     * @param password  the password to join the room with.
     * @return a List of errors, if any.
     */
    public static List<String> joinRoom(MultiUserChat groupChat, String nickname, String password) {
        final List<String> errors = new ArrayList<>();
        if (!groupChat.isJoined()) {
            int groupChatCounter = 0;
            while (true) {
                groupChatCounter++;
                String joinName = nickname;
                if (groupChatCounter > 1) {
                    joinName = joinName + groupChatCounter;
                }
                if (groupChatCounter < 10) {
                    try {
                        if (ModelUtil.hasLength(password)) {
                            groupChat.join(joinName, password);
                        }
                        else {
                            groupChat.join(joinName);
                        }
                        changePresenceToAvailableIfInvisible();
                        break;
                    }
                    catch (XMPPException | SmackException ex) {
                        XMPPError error = null;
                        if ( ex instanceof XMPPException.XMPPErrorException) {
                            error = (( XMPPException.XMPPErrorException ) ex).getXMPPError();
                        }

                        final String errorText = ConferenceUtils.getReason( error );

                        errors.add( errorText );
                        if ( error.getCondition() != XMPPError.Condition.conflict )
                        {
                            break;
                        }
                    }
                }
                else {
                    break;
                }
            }
        }

        return errors;
    }

    /**
     * Invites users to an existing room.
     *
     * @param chat The room to invite people into.
     * @param jids a collection of the users to invite.
     */
    public static void inviteUsersToRoom(MultiUserChat chat, Collection<String> jids, boolean randomName ) {
        inviteUsersToRoom( XmppStringUtils.parseDomain( chat.getRoom() ), XmppStringUtils.parseLocalpart( chat.getRoom() ), jids, randomName );
    }

    /**
     * Invites users to an existing room.
     *
     * @param serviceName the service name to use.
     * @param roomName    the name of the room.
     * @param jids        a collection of the users to invite.
     */
    public static void inviteUsersToRoom(String serviceName, String roomName, Collection<String> jids, boolean randomName) {
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

    public static Collection<BookmarkedConference> retrieveBookmarkedConferences() throws XMPPException, SmackException
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
    public static boolean isPasswordRequired(String roomJID) {
        // Check to see if the room is password protected
    	ServiceDiscoveryManager discover = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());


        try {
            DiscoverInfo info = discover.discoverInfo(roomJID);
            return info.containsFeature("muc_passwordprotected");
        }
        catch (XMPPException | SmackException e) {
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
     * @throws XMPPException thrown if an error occurs during room creation.
     */
    public static void createPrivateConference(String serviceName, String message, String roomName, Collection<String> jids) throws SmackException
    {
        final String roomJID = XmppStringUtils.escapeLocalpart(roomName) + "@" + serviceName;
        final MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getMultiUserChat( roomJID );
        final LocalPreferences pref = SettingsManager.getLocalPreferences();


        final GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(multiUserChat);
        try {
            // Attempt to create room.
            multiUserChat.create(pref.getNickname());
        }
        catch (XMPPException | SmackException e) {
            throw new SmackException(e);
        }

        try {
            // Since this is a private room, make the room not public and set user as owner of the room.
            Form submitForm = multiUserChat.getConfigurationForm().createAnswerForm();
            submitForm.setAnswer("muc#roomconfig_publicroom", false);
            submitForm.setAnswer("muc#roomconfig_roomname", roomName);

            final List<String> owners = new ArrayList<>();
            owners.add(SparkManager.getSessionManager().getBareAddress());
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);

            multiUserChat.sendConfigurationForm(submitForm);
        }
        catch (XMPPException | SmackException e1) {
            Log.error("Unable to send conference room chat configuration form.", e1);
        }


        ChatManager chatManager = SparkManager.getChatManager();

        // Check if room already is open
        try {
            chatManager.getChatContainer().getChatRoom(room.getRoomname());
        }
        catch (ChatRoomNotFoundException e) {
            chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);
        }

        for (String jid : jids) {
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
    public static String getReason(XMPPError error) {
        if (error == null) {
            return "No response from server.";
        }

        switch ( error.getCondition() )
        {
            case conflict:
                return "Your desired nickname is in use or reserved by someone else.";
            case forbidden:
                return "You have been banned from this room.";
            case item_not_found:
                return "The room you are trying to enter does not exist.";
            case not_acceptable:
                return "You must use your reserved room nick.";
            case not_allowed:
                return "You do not have permission to create a room.";
            case not_authorized:
                return "The password did not match the room's password.";
            case registration_required:
                return "You are not a member of this room.\nThis room requires you to be a member to join.";
            default:
                return "An error has occurred: " + error.getConditionText();
        }

    }

    /**
     * Enters a GroupChatRoom on the event thread.
     *
     * @param roomName the name of the room.
     * @param roomJID  the rooms jid.
     * @param password the rooms password (if any).
     * @return the GroupChatRoom created.
     */
    public static GroupChatRoom enterRoomOnSameThread(final String roomName, String roomJID, String password) {
        ChatManager chatManager = SparkManager.getChatManager();


        final LocalPreferences pref = SettingsManager.getLocalPreferences();

        final String nickname = pref.getNickname().trim();


        try {
            GroupChatRoom chatRoom = (GroupChatRoom)chatManager.getChatContainer().getChatRoom(roomJID);
            MultiUserChat muc = chatRoom.getMultiUserChat();
            if (!muc.isJoined()) {
                joinRoom(muc, nickname, password);
            }
            chatManager.getChatContainer().activateChatRoom(chatRoom);
            return chatRoom;
        }
        catch (ChatRoomNotFoundException e) {
            // Nothing to do
        }

        final MultiUserChat groupChat = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getMultiUserChat( roomJID );


        final GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(groupChat);
        room.setTabTitle(roomName);


        if (isPasswordRequired(roomJID) && password == null) {
            password = JOptionPane.showInputDialog(null, "Enter Room Password", "Need Password", JOptionPane.QUESTION_MESSAGE);
            if (!ModelUtil.hasLength(password)) {
                return null;
            }
        }


        final List<String> errors = new ArrayList<>();
        final String userPassword = password;


        if (!groupChat.isJoined()) {
            int groupChatCounter = 0;
            while (true) {
                groupChatCounter++;
                String joinName = nickname;
                if (groupChatCounter > 1) {
                    joinName = joinName + groupChatCounter;
                }
                if (groupChatCounter < 10) {
                    try {
                    	if (!confirmToRevealVisibility())
                    		return null;

                        if (ModelUtil.hasLength(userPassword)) {
                            groupChat.join(joinName, userPassword);
                        }
                        else {
                            groupChat.join(joinName);
                        }
                        break;
                    }
                    catch (XMPPException | SmackException ex) {
                        XMPPError error = null;
                        if ( ex instanceof XMPPException.XMPPErrorException) {
                            error = (( XMPPException.XMPPErrorException ) ex).getXMPPError();
                        }

                        final String errorText = ConferenceUtils.getReason( error );

                        errors.add( errorText );
                        if ( error != null && error.getCondition() != XMPPError.Condition.conflict )
                        {
                            break;
                        }
                    }
                }
                else {
                    break;
                }
            }

        }


        if (errors.size() > 0) {
            String error = errors.get(0);
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), error, "Unable to join the room at this time.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        else if (groupChat.isJoined()) {
        	changePresenceToAvailableIfInvisible();
            chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);
        }
        else {
        	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Unable to join the room.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return room;
    }

    public static void enterRoom(final MultiUserChat groupChat, String tabTitle, final String nickname, final String password) {
        final GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(groupChat);
        room.setTabTitle(tabTitle);

        final List<String> errors = new ArrayList<>();

        if (!groupChat.isJoined()) {
            int groupChatCounter = 0;
            while (true) {
                groupChatCounter++;
                String joinName = nickname;
                if (groupChatCounter > 1) {
                    joinName = joinName + groupChatCounter;
                }
                if (groupChatCounter < 10) {
                    try {
                    	if (!confirmToRevealVisibility())
                    		return;
                    	
                        if (ModelUtil.hasLength(password)) {
                            groupChat.join(joinName, password);
                        }
                        else {
                            groupChat.join(joinName);
                        }
                        break;
                    }
                    catch (XMPPException | SmackException ex) {
                        XMPPError error = null;
                        if ( ex instanceof XMPPException.XMPPErrorException) {
                            error = (( XMPPException.XMPPErrorException ) ex).getXMPPError();
                        }

                        final String errorText = ConferenceUtils.getReason( error );

                        errors.add( errorText );
                        if ( error != null && error.getCondition() != XMPPError.Condition.conflict )
                        {
                            break;
                        }
                    }
                }
                else {
                    break;
                }
            }
        }

        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
        
        if (errors.size() > 0) {
            String error = errors.get(0);
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), error, "Could Not Join Room", JOptionPane.ERROR_MESSAGE);
        }
        else if (groupChat.isJoined()) {
            changePresenceToAvailableIfInvisible();
            ChatManager chatManager = SparkManager.getChatManager();
            chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);
        }
        else {
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Unable to join room.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    final static List<String> unclosableChatRooms = new ArrayList<>();
	public synchronized static void addUnclosableChatRoom(String jid) {
		unclosableChatRooms.add(jid);
	}

	public static boolean isChatRoomClosable(Component c) {
		if(c instanceof GroupChatRoom ) {
			GroupChatRoom groupChatRoom = (GroupChatRoom) c;
    		String roomName = groupChatRoom.getChatRoom().getRoomname();

    		if(unclosableChatRooms.contains(roomName)){
    			return false;
    		}
		}
		return true;

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

