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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXList;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.*;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.muc.packet.MUCItem;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.ImageTitlePanel;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

/**
 * The <code>RoomInfo</code> class is used to display all room information, such
 * as agents and room information.
 */
public class GroupChatParticipantList extends JPanel {

    	private static final long serialVersionUID = 3809155443119207342L;
	private GroupChatRoom groupChatRoom;
	private final ImageTitlePanel agentInfoPanel;
	private ChatManager chatManager;
	private MultiUserChat chat;
	private LocalPreferences _localPreferences = SettingsManager.getLocalPreferences();

	private final Map<String, String> userMap = new HashMap<>();

	private UserManager userManager = SparkManager.getUserManager();

	private DefaultListModel model = new DefaultListModel();

	private JXList participantsList;

	private PresenceListener listener = null;

	private Map<String, String> invitees = new HashMap<>();

	private boolean allowNicknameChange = true;

	private DiscoverInfo roomInformation;

	private List<JLabel> users = new ArrayList<>();

	private Map<String,MUCRole> usersToRoles = new HashMap<>();
	private Map<String,MUCAffiliation> usersToAffiliation = new HashMap<>();

	/**
	 * Creates a new RoomInfo instance using the specified ChatRoom. The
	 * RoomInfo component is responsible for monitoring all activity in the
	 * ChatRoom.
	 */
	public GroupChatParticipantList() {
		setLayout(new GridBagLayout());
		chatManager = SparkManager.getChatManager();

		agentInfoPanel = new ImageTitlePanel(Res
				.getString("message.participants.in.room"));
		participantsList = new JXList(model);
		participantsList.setCellRenderer(new ParticipantRenderer());

		// Set the room to track
		this.setOpaque(false);
		this.setBackground(Color.white);

		// Respond to Double-Click in Agent List to start a chat
		participantsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					String selectedUser = getSelectedUser();
					startChat(groupChatRoom, userMap.get(selectedUser));
				}
			}

			public void mouseReleased(final MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					checkPopup(evt);
				}
			}

			public void mousePressed(final MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					checkPopup(evt);
				}
			}
		});

		JScrollPane scroller = new JScrollPane(participantsList);
		scroller.setPreferredSize( new Dimension( 200, getHeight() ) );

		// Speed up scrolling. It was way too slow.
		scroller.getVerticalScrollBar().setBlockIncrement(200);
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		scroller.setBackground(Color.white);
		scroller.getViewport().setBackground(Color.white);

		add(scroller, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
	}

    public void setChatRoom(final ChatRoom chatRoom) {
	this.groupChatRoom = (GroupChatRoom) chatRoom;

	chat = groupChatRoom.getMultiUserChat();

	chat.addInvitationRejectionListener( ( jid1, message ) -> {
    String nickname = userManager.getUserNicknameFromJID( jid1 );

    userHasLeft(nickname);

    chatRoom.getTranscriptWindow().insertNotificationMessage(
        nickname + " has rejected the invitation.",
        ChatManager.NOTIFICATION_COLOR);
    } );

	listener = p -> SwingUtilities.invokeLater( () -> {
if (p.getError() != null) {
if (p.getError()
.getCondition()
.equals(XMPPError.Condition.conflict
.toString())) {
return;
}
}
final String userid = p.getFrom();

String displayName = XmppStringUtils.parseResource(userid);
userMap.put(displayName, userid);

if (p.getType() == Presence.Type.available) {
addParticipant(userid, p);
agentInfoPanel.setVisible(true);
	groupChatRoom.validate();
} else {
removeUser(displayName);
}

// When joining a room, check if the current user is an owner/admin. If so, the UI should allow the current
// user to change settings of this MUC.
final MUCUser mucUserEx = p.getExtension( MUCUser.ELEMENT, MUCUser.NAMESPACE );
if (mucUserEx != null && mucUserEx.getStatus().contains( MUCUser.Status.create( 110 ) ) ) // 110 = Inform user that presence refers to itself
{
final MUCItem item = mucUserEx.getItem();
if ( item != null )
{
if ( item.getAffiliation() == MUCAffiliation.admin || item.getAffiliation() == MUCAffiliation.owner )
{
groupChatRoom.notifySettingsAccessRight();
}
}
}
} );

	chat.addParticipantListener(listener);

	ServiceDiscoveryManager disco = ServiceDiscoveryManager
		.getInstanceFor(SparkManager.getConnection());
	try {
	    roomInformation = disco.discoverInfo(chat.getRoom());
	} catch (XMPPException | SmackException e) {
	    Log.debug("Unable to retrieve room information for "
		    + chat.getRoom());
	}
    }

	public void chatRoomOpened(ChatRoom room) {
		if (room != groupChatRoom) {
			return;
		}

		chat.addUserStatusListener(new UserStatusListener() {
			public void kicked(String actor, String reason) {

			}

			public void voiceGranted() {

			}

			public void voiceRevoked() {

			}

			public void banned(String actor, String reason) {

			}

			public void membershipGranted() {

			}

			public void membershipRevoked() {

			}

			public void moderatorGranted() {

			}

			public void moderatorRevoked() {

			}

			public void ownershipGranted() {
			}

			public void ownershipRevoked() {

			}

			public void adminGranted() {

			}

			public void adminRevoked() {

			}
		});
	}

	public void addInvitee(String jid, String message) {
		// So the problem with this is that I have no idea what the users actual
		// jid is in most cases.
		final UserManager userManager = SparkManager.getUserManager();

		String displayName = userManager.getUserNicknameFromJID(jid);

		groupChatRoom.getTranscriptWindow().insertNotificationMessage(
				displayName + " has been invited to join this room.",
				ChatManager.NOTIFICATION_COLOR);

		if (roomInformation != null
				&& !roomInformation.containsFeature("muc_nonanonymous")) {
			return;
		}

		final ImageIcon inviteIcon = SparkRes
				.getImageIcon(SparkRes.USER1_BACK_16x16);

		addUser(inviteIcon, displayName);

		invitees.put(displayName, message);
	}

	protected ImageIcon getImageIcon(String participantJID) {
		String displayName = XmppStringUtils.parseResource(participantJID);
		ImageIcon icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
		icon.setDescription(displayName);
		return icon;
	}	

    protected void addParticipant(final String participantJID, Presence presence) {
	// Remove reference to invitees

	for (String displayName : invitees.keySet()) {
	    String jid = SparkManager.getUserManager().getJIDFromDisplayName(
		    displayName);

	    Occupant occ = chat.getOccupant(participantJID);
	    if (occ != null) {
		String actualJID = occ.getJid();
		if (actualJID.equals(jid)) {
		    removeUser(displayName);
		}
	    }
	}

	String nickname = XmppStringUtils.parseResource(participantJID);

	MUCAffiliation affiliation = null;
	MUCRole role = null;
	final MUCUser extension = (MUCUser) presence.getExtension( MUCUser.NAMESPACE );
	if ( extension != null && extension.getItem() != null )
	{
		affiliation = extension.getItem().getAffiliation();
		role = extension.getItem().getRole();
	}

	if ( affiliation == null ) {
		affiliation = MUCAffiliation.none;
	}
	if ( role == null ) {
		role = MUCRole.none;
	}

	usersToRoles.put(participantJID, role);
	usersToAffiliation.put(participantJID, affiliation);

	Icon icon;
	if (_localPreferences.isShowingRoleIcons()) {
	    icon = getIconForRole(role, affiliation);
	} else {
	    icon = PresenceManager.getIconFromPresence(presence);
	    if (icon == null) {
			icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
		}
	}

	if (!exists(nickname)) {
	    addUser(icon, nickname);
	} else {
	    int index = getIndex(nickname);
	    if (index != -1) {
		final JLabel userLabel = new JLabel(nickname, icon,
			JLabel.HORIZONTAL);
		model.setElementAt(userLabel, index);
	    }
	}
    }

	/**
	 * Returns corresponding Icons for each MUC-Role
	 * icons are: <br>
	 * Owner = Gold Star <br>
	 * Admin = Silver Star <br>
	 * Moderator = Bronze Star <br>
	 * Member = Yellow <br>
	 * Participant = Green <br>
	 * Visitor = Blue <br>
	 * N/A = Grey <br>
	 * @param role
	 * @return {@link Icon}
	 */
    private Icon getIconForRole(MUCRole role, MUCAffiliation affiliation)
	{
		switch ( affiliation )
		{
			case owner:
				return SparkRes.getImageIcon(SparkRes.STAR_OWNER);

			case admin:
				return SparkRes.getImageIcon(SparkRes.STAR_ADMIN);

			case member:
				if ( role == MUCRole.moderator )
				{
					return SparkRes.getImageIcon(SparkRes.STAR_MODERATOR);
				}
				else
				{
					return SparkRes.getImageIcon(SparkRes.STAR_YELLOW_IMAGE);
				}

			default:
				switch ( role )
				{
					case participant:
						return SparkRes.getImageIcon(SparkRes.STAR_GREEN_IMAGE);

					case moderator:
						return SparkRes.getImageIcon(SparkRes.STAR_MODERATOR);

					case visitor:
						return SparkRes.getImageIcon(SparkRes.STAR_BLUE_IMAGE);

					default:
						return SparkRes.getImageIcon(SparkRes.STAR_GREY_IMAGE);
				}
		}
    }

	public void userHasLeft(String userid) {
		int index = getIndex(userid);

		if (index != -1) {
			removeUser(userid);
			userMap.remove(userid);
		}
	}

	protected boolean exists(String nickname) {
		for (int i = 0; i < model.getSize(); i++) {
			final JLabel userLabel = (JLabel) model.getElementAt(i);
			if (userLabel.getText().equals(nickname)) {
				return true;
			}
		}
		return false;
	}

	protected String getSelectedUser() {
		JLabel label = (JLabel) participantsList.getSelectedValue();
		if (label != null) {
			return label.getText();
		}

		return null;
	}

	protected void startChat(ChatRoom groupChat, String groupJID) {
		String userNickname = XmppStringUtils.parseResource(groupJID);
		String roomTitle = userNickname + " - "
				+ XmppStringUtils.parseLocalpart(groupChat.getRoomname());

		String nicknameOfUser = XmppStringUtils.parseResource(groupJID);
		String nickname = groupChat.getNickname();

		if (nicknameOfUser.equals(nickname)) {
			return;
		}

		ChatRoom chatRoom;
		try {
			chatRoom = chatManager.getChatContainer().getChatRoom(groupJID);
		} catch (ChatRoomNotFoundException e) {
			Log.debug("Could not find chat room - " + groupJID);

			// Create new room
			chatRoom = new ChatRoomImpl(groupJID, nicknameOfUser, roomTitle);
			chatManager.getChatContainer().addChatRoom(chatRoom);
		}

		chatManager.getChatContainer().activateChatRoom(chatRoom);
	}

	public void tabSelected() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	public String getTabTitle() {
		return Res.getString("title.room.information");
	}

	public Icon getTabIcon() {
		return SparkRes.getImageIcon(SparkRes.SMALL_BUSINESS_MAN_VIEW);
	}

	public String getTabToolTip() {
		return Res.getString("title.room.information");
	}

	public JComponent getGUI() {
		return this;
	}

	protected void kickUser(String nickname) {
		try {
			chat.kickParticipant(nickname, Res
					.getString("message.you.have.been.kicked"));
		} catch (XMPPException | SmackException e) {
			groupChatRoom.insertText(Res.getString("message.kicked.error",
					nickname));
		}
	}

	protected void banUser(String displayName) {
		try {
			Occupant occupant = chat.getOccupant(userMap.get(displayName));
			if (occupant != null) {
				String bareJID = XmppStringUtils
						.parseBareJid(occupant.getJid());
				chat.banUser(bareJID, Res
						.getString("message.you.have.been.banned"));
			}
		} catch (XMPPException | SmackException e) {
		    groupChatRoom.getTranscriptWindow().
		    insertNotificationMessage("No can do "+e.getMessage(), ChatManager.ERROR_COLOR);
		}
	}

	protected void unbanUser(String jid) {
		try {
			chat.grantMembership(jid);
		} catch (XMPPException | SmackException e) {
		    groupChatRoom.getTranscriptWindow().
		    insertNotificationMessage("No can do "+e.getMessage(), ChatManager.ERROR_COLOR);
		}
	}

	protected void grantVoice(String nickname) {
		try {
			chat.grantVoice(nickname);
		} catch (XMPPException | SmackException e) {
		    groupChatRoom.getTranscriptWindow().
		    insertNotificationMessage("No can do "+e.getMessage(), ChatManager.ERROR_COLOR);
		}
	}

	protected void revokeVoice(String nickname) {
		try {
			chat.revokeVoice(nickname);
		} catch (XMPPException | SmackException e) {
		    groupChatRoom.getTranscriptWindow().
		    insertNotificationMessage("No can do "+e.getMessage(), ChatManager.ERROR_COLOR);
		}
	}

	protected void grantModerator(String nickname) {
	try {
	    chat.grantModerator(nickname);
	} catch (XMPPException | SmackException e) {
	    groupChatRoom.getTranscriptWindow().insertNotificationMessage(
		    "No can do " + e.getMessage(), ChatManager.ERROR_COLOR);
	}
    }

	protected void revokeModerator(String nickname) {
	try {
	    chat.revokeModerator(nickname);
	} catch (XMPPException | SmackException e) {
	    groupChatRoom.getTranscriptWindow().insertNotificationMessage(
		    "No can do " + e.getMessage(), ChatManager.ERROR_COLOR);
	}
    }

	protected void grantMember(String nickname) {
	try {
	    Occupant o = userManager.getOccupant(groupChatRoom,nickname);
	    nickname = XmppStringUtils.parseBareJid(o.getJid());
	    chat.grantMembership(nickname);

	} catch (XMPPException | SmackException e) {
	    groupChatRoom.getTranscriptWindow().insertNotificationMessage(
		    "No can do " + e.getMessage(), ChatManager.ERROR_COLOR);
	}
    }
	protected void revokeMember(String nickname) {
	try {
	    Occupant o = userManager.getOccupant(groupChatRoom,nickname);
	    nickname = XmppStringUtils.parseBareJid(o.getJid());
	    chat.revokeMembership(nickname);
	} catch (XMPPException | SmackException e) {
	    groupChatRoom.getTranscriptWindow().insertNotificationMessage(
		    "No can do " + e.getMessage(), ChatManager.ERROR_COLOR);
	}
    }

	protected void grantAdmin(String nickname) {
	try {
	    Occupant o = userManager.getOccupant(groupChatRoom,nickname);
	    nickname = XmppStringUtils.parseBareJid(o.getJid());
	    chat.grantAdmin(nickname);
	} catch (XMPPException | SmackException e) {
	    groupChatRoom.getTranscriptWindow().insertNotificationMessage(
		    "No can do " + e.getMessage(), ChatManager.ERROR_COLOR);
	}
    }
	protected void revokeAdmin(String nickname) {
	try {
	    Occupant o = userManager.getOccupant(groupChatRoom,nickname);
	    nickname = XmppStringUtils.parseBareJid(o.getJid());
	    chat.revokeAdmin(nickname);
	} catch (XMPPException | SmackException e) {
	    groupChatRoom.getTranscriptWindow().insertNotificationMessage(
		    "No can do " + e.getMessage(), ChatManager.ERROR_COLOR);
	}
    }

	protected void grantOwner(String nickname) {
	try {
	    Occupant o = userManager.getOccupant(groupChatRoom,nickname);
	    nickname = XmppStringUtils.parseBareJid(o.getJid());
	    chat.grantOwnership(nickname);
	} catch (XMPPException | SmackException e) {
	    groupChatRoom.getTranscriptWindow().insertNotificationMessage(
		    "No can do " + e.getMessage(), ChatManager.ERROR_COLOR);
	}
    }
	protected void revokeOwner(String nickname) {
	try {
	    Occupant o = userManager.getOccupant(groupChatRoom,nickname);
	    nickname = XmppStringUtils.parseBareJid(o.getJid());
	    chat.revokeOwnership(nickname);
	} catch (XMPPException | SmackException e) {
	    groupChatRoom.getTranscriptWindow().insertNotificationMessage(
		    "No can do " + e.getMessage(), ChatManager.ERROR_COLOR);
	}
    }

	protected void checkPopup(MouseEvent evt) {
	Point p = evt.getPoint();
	final int index = participantsList.locationToIndex(p);

	final JPopupMenu popup = new JPopupMenu();

	if (index != -1) {
	    participantsList.setSelectedIndex(index);
	    final JLabel userLabel = (JLabel) model.getElementAt(index);
	    final String selectedUser = userLabel.getText();
	    final String groupJID = userMap.get(selectedUser);
	    String groupJIDNickname = XmppStringUtils.parseResource(groupJID);

	    final String nickname = groupChatRoom.getNickname();
	    final Occupant occupant = userManager.getOccupant(groupChatRoom,
		    selectedUser);
	    final boolean iamAdmin = SparkManager.getUserManager().isAdmin(
		    groupChatRoom, chat.getNickname());
	    final boolean iamOwner = SparkManager.getUserManager().isOwner(groupChatRoom, chat.getNickname());

	    final boolean iamAdminOrOwner = iamAdmin || iamOwner;

	    final boolean iamModerator = SparkManager.getUserManager()
		    .isModerator(groupChatRoom, chat.getNickname());

	    final boolean userIsMember = SparkManager.getUserManager().isMember(occupant);

	    final boolean userIsAdmin = userManager.isAdmin(groupChatRoom, occupant.getNick());
	    final boolean userIsOwner = userManager.isOwner(occupant);
	    final boolean userIsModerator = userManager.isModerator(occupant);
	    boolean selectedMyself = nickname.equals(groupJIDNickname);

	    // Handle invites
	    if (groupJIDNickname == null) {
		Action inviteAgainAction = new AbstractAction() {
		    private static final long serialVersionUID = -1875073139356098243L;

		    public void actionPerformed(ActionEvent actionEvent) {
			String message = invitees.get(selectedUser);
			String jid = userManager.getJIDFromDisplayName(selectedUser);
			try
			{
				chat.invite(jid, message);
			}
			catch ( SmackException.NotConnectedException e )
			{
				Log.warning( "Unable to send stanza to " + jid, e );
			}
			}
		};

		inviteAgainAction.putValue(Action.NAME,
			Res.getString("menuitem.inivite.again"));
		popup.add(inviteAgainAction);

		Action removeInvite = new AbstractAction() {
		    private static final long serialVersionUID = -3647279452501661970L;

		    public void actionPerformed(ActionEvent actionEvent) {
			int index = getIndex(selectedUser);

			if (index != -1) {
			    model.removeElementAt(index);
			}
		    }
		};

		removeInvite.putValue(Action.NAME,
			Res.getString("menuitem.remove"));
		popup.add(removeInvite);

		popup.show(participantsList, evt.getX(), evt.getY());
		return;
	    }

	    if (selectedMyself) {
		Action changeNicknameAction = new AbstractAction() {
		    private static final long serialVersionUID = -7891803180672794112L;

		    public void actionPerformed(ActionEvent actionEvent) {
			String newNickname = JOptionPane.showInputDialog(
				groupChatRoom,
				Res.getString("label.new.nickname") + ":",
				Res.getString("title.change.nickname"),
				JOptionPane.QUESTION_MESSAGE);
			if (ModelUtil.hasLength(newNickname)) {
			    while (true) {
				newNickname = newNickname.trim();
				String nick = chat.getNickname();
				if (newNickname.equals(nick)) {
				    // return;
				}
				try {
				    chat.changeNickname(newNickname);
				    break;
				} catch (XMPPException | SmackException e1) {
					if ( e1 instanceof XMPPException.XMPPErrorException && (( XMPPException.XMPPErrorException ) e1).getXMPPError().getCondition() == XMPPError.Condition.not_acceptable )
					{
						// handle deny changing nick.
				    UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
					JOptionPane
						.showMessageDialog(
							groupChatRoom,
							Res.getString("message.nickname.not.acceptable"),
							Res.getString("title.change.nickname"),
							JOptionPane.ERROR_MESSAGE);
					break;
				    }
				    newNickname = JOptionPane
					    .showInputDialog(
						    groupChatRoom,
						    Res.getString("message.nickname.in.use")
							    + ":",
						    Res.getString("title.change.nickname"),
						    JOptionPane.QUESTION_MESSAGE);
				    if (!ModelUtil.hasLength(newNickname)) {
					break;
				    }
				}
			    }
			}
		    }
		};

		changeNicknameAction.putValue(Action.NAME,
			Res.getString("menuitem.change.nickname"));
		changeNicknameAction.putValue(Action.SMALL_ICON,
			SparkRes.getImageIcon(SparkRes.DESKTOP_IMAGE));

		if (allowNicknameChange) {
		    popup.add(changeNicknameAction);
		}
	    }

	    Action chatAction = new AbstractAction() {
		private static final long serialVersionUID = -2739549054781928195L;

		public void actionPerformed(ActionEvent actionEvent) {
		    String selectedUser = getSelectedUser();
		    startChat(groupChatRoom, userMap.get(selectedUser));
		}
	    };

	    chatAction.putValue(Action.NAME,
		    Res.getString("menuitem.start.a.chat"));
	    chatAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
	    if (!selectedMyself) {
		popup.add(chatAction);
	    }

	    Action blockAction = new AbstractAction() {
		private static final long serialVersionUID = 8771362206105723776L;

		public void actionPerformed(ActionEvent e) {
		    String user = getSelectedUser();
		    ImageIcon icon;
		    if (groupChatRoom.isBlocked(groupJID)) {
			groupChatRoom.removeBlockedUser(groupJID);
			icon = getImageIcon(groupJID);
		    } else {
			groupChatRoom.addBlockedUser(groupJID);
			icon = SparkRes.getImageIcon(SparkRes.BRICKWALL_IMAGE);
		    }

		    JLabel label = new JLabel(user, icon, JLabel.HORIZONTAL);
		    model.setElementAt(label, index);
		}
	    };

	    blockAction.putValue(Action.NAME,
		    Res.getString("menuitem.block.user"));
	    blockAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.BRICKWALL_IMAGE));
	    if (!selectedMyself) {
		if (groupChatRoom.isBlocked(groupJID)) {
		    blockAction.putValue(Action.NAME,
			    Res.getString("menuitem.unblock.user"));
		}
		popup.add(blockAction);
	    }

	    Action kickAction = new AbstractAction() {
		private static final long serialVersionUID = 5769982955040961189L;

		public void actionPerformed(ActionEvent actionEvent) {
		    kickUser(selectedUser);
		}
	    };

	    kickAction.putValue(Action.NAME,
		    Res.getString("menuitem.kick.user"));
	    kickAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
	    if (iamModerator && !userIsAdmin && !selectedMyself) {
		popup.add(kickAction);
	    }

	    // Handle Voice Operations
	    Action voiceAction = new AbstractAction() {
		private static final long serialVersionUID = 7628207942009369329L;

		public void actionPerformed(ActionEvent actionEvent) {
		    if (userManager.hasVoice(groupChatRoom, selectedUser)) {
			revokeVoice(selectedUser);
		    } else {
			grantVoice(selectedUser);
		    }
		    Collections.sort(users, labelComp);

		}
	    };

	    voiceAction.putValue(Action.NAME, Res.getString("menuitem.voice"));
	    voiceAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
	    if (iamModerator && !userIsModerator && !selectedMyself) {
		if (userManager.hasVoice(groupChatRoom, selectedUser)) {
		    voiceAction.putValue(Action.NAME,
			    Res.getString("menuitem.revoke.voice"));
		} else {
		    voiceAction.putValue(Action.NAME,
			    Res.getString("menuitem.grant.voice"));
		}
		popup.add(voiceAction);
	    }

	    Action banAction = new AbstractAction() {
		private static final long serialVersionUID = 4290194898356641253L;

		public void actionPerformed(ActionEvent actionEvent) {
		    banUser(selectedUser);
		}
	    };
	    banAction.putValue(Action.NAME, Res.getString("menuitem.ban.user"));
	    banAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.RED_FLAG_16x16));
	    if (iamAdminOrOwner && !userIsModerator && !selectedMyself) {
		popup.add(banAction);
	    }


	    JMenu affiliationMenu = new JMenu(Res.getString("menuitem.affiliation"));
	    affiliationMenu.setIcon(SparkRes.getImageIcon(SparkRes.MODERATOR_IMAGE));

	    Action memberAction = new AbstractAction() {
		private static final long serialVersionUID = -2528887841227305432L;

		@Override
	        public void actionPerformed(ActionEvent e) {
	            if (!userIsMember) {
			grantMember(selectedUser);
		    } else {
			revokeMember(selectedUser);
		    }
		    Collections.sort(users, labelComp);
	        }
	    };
	    memberAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.STAR_YELLOW_IMAGE));
	    if(iamAdminOrOwner && !userIsMember)
	    {
		memberAction.putValue(Action.NAME,Res.getString("menuitem.grant.member"));
		affiliationMenu.add(memberAction);
	    }
	    else if(iamAdminOrOwner && userIsMember && !selectedMyself)
	    {
		memberAction.putValue(Action.NAME,Res.getString("menuitem.revoke.member"));
		affiliationMenu.add(memberAction);
	    }


	    Action moderatorAction = new AbstractAction() {
		private static final long serialVersionUID = 8162535640460764896L;

		public void actionPerformed(ActionEvent actionEvent) {
		    if (!userIsModerator) {
			grantModerator(selectedUser);
		    } else {
			revokeModerator(selectedUser);
		    }
		    Collections.sort(users, labelComp);

		}
	    };

	    moderatorAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.STAR_MODERATOR));

	    if (iamAdminOrOwner && !userIsModerator && !userIsAdmin && !userIsOwner) {
		moderatorAction.putValue(Action.NAME,
			Res.getString("menuitem.grant.moderator"));
		affiliationMenu.add(moderatorAction);
	    } else if (iamAdminOrOwner && userIsModerator && !selectedMyself) {
		moderatorAction.putValue(Action.NAME,
			Res.getString("menuitem.revoke.moderator"));
		affiliationMenu.add(moderatorAction);
	    }

	    Action adminAction = new AbstractAction() {
		private static final long serialVersionUID = 3672121864443182872L;

		@Override
	        public void actionPerformed(ActionEvent e) {
		    if (!userIsAdmin) {
			grantAdmin(selectedUser);
		    } else {
			revokeAdmin(selectedUser);
		    }
		    Collections.sort(users, labelComp);

	        }
	    };
	    adminAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.STAR_ADMIN));
	    if(iamAdminOrOwner && !userIsAdmin && !userIsOwner)
	    {
		adminAction.putValue(Action.NAME,
			Res.getString("menuitem.grant.admin"));
		affiliationMenu.add(adminAction);
	    }
	    else if(iamAdminOrOwner && !selectedMyself )
	    {
		adminAction.putValue(Action.NAME,
			Res.getString("menuitem.revoke.admin"));
		affiliationMenu.add(adminAction);
	    }


	    Action ownerAction = new AbstractAction() {
		private static final long serialVersionUID = 3672121864443182872L;

		@Override
	        public void actionPerformed(ActionEvent e) {
		    if (!userIsOwner) {
			grantOwner(selectedUser);
		    } else {
			revokeOwner(selectedUser);
		    }
		    Collections.sort(users, labelComp);

	        }
	    };
	    ownerAction.putValue(Action.SMALL_ICON,
		    SparkRes.getImageIcon(SparkRes.STAR_OWNER));

	    if( iamOwner && !userIsOwner)
	    {
		ownerAction.putValue(Action.NAME,
			Res.getString("menuitem.grant.owner"));
		affiliationMenu.add(ownerAction);
	    }
	    else if(iamOwner && !selectedMyself )
	    {
		ownerAction.putValue(Action.NAME,
			Res.getString("menuitem.revoke.owner"));
		affiliationMenu.add(ownerAction);
	    }

	    if(affiliationMenu.getItemCount()>0)
		popup.add(affiliationMenu);


	    // Handle Unbanning of users.
	    Action unbanAction = new AbstractAction() {
		private static final long serialVersionUID = 3672121864443182872L;

		public void actionPerformed(ActionEvent actionEvent) {
		    String jid = ((JMenuItem) actionEvent.getSource())
			    .getText();
		    unbanUser(jid);
		}
	    };

	    if (iamAdmin || iamOwner) {
		JMenu unbanMenu = new JMenu(Res.getString("menuitem.unban"));
		Iterator<Affiliate> bannedUsers = null;
		try {
		    bannedUsers = chat.getOutcasts().iterator();
		} catch (XMPPException | SmackException e) {
		    Log.error("Error loading all banned users", e);
		}

		while (bannedUsers != null && bannedUsers.hasNext()) {
		    Affiliate bannedUser = bannedUsers.next();
		    ImageIcon icon = SparkRes.getImageIcon(SparkRes.RED_BALL);
		    JMenuItem bannedItem = new JMenuItem(bannedUser.getJid(),
			    icon);
		    unbanMenu.add(bannedItem);
		    bannedItem.addActionListener(unbanAction);
		}

		if (unbanMenu.getMenuComponentCount() > 0) {
		    popup.add(unbanMenu);
		}
	    }
	}

	Action inviteAction = new AbstractAction() {
	    private static final long serialVersionUID = 2240864466141501086L;

	    public void actionPerformed(ActionEvent actionEvent) {
		ConferenceUtils.inviteUsersToRoom( groupChatRoom.getMultiUserChat(), null, false);
	    }
	};

	inviteAction.putValue(Action.NAME,
		Res.getString("menuitem.invite.users"));
	inviteAction.putValue(Action.SMALL_ICON,
		SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));

	if (index != -1) {
	    popup.addSeparator();
	}
	popup.add(inviteAction);

	popup.show(participantsList, evt.getX(), evt.getY());
    }

	public void setNicknameChangeAllowed(boolean allowed) {
		allowNicknameChange = allowed;
	}

	public int getIndex(String name) {
		for (int i = 0; i < model.getSize(); i++) {
			JLabel label = (JLabel) model.getElementAt(i);
			if (label.getText().equals(name)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes a user from the participant list based on their displayed name.
	 *
	 * @param displayName
	 *            the users displayed name to remove.
	 */
	public synchronized void removeUser(String displayName) {
		try {
			for (int i = 0; i < users.size(); i++) {
				JLabel label = users.get(i);
				if (label.getText().equals(displayName)) {
					users.remove(label);
					model.removeElement(label);
				}
			}

			for (int i = 0; i < model.size(); i++) {
				JLabel label = (JLabel) model.getElementAt(i);
				if (label.getText().equals(displayName)) {
					users.remove(label);
					model.removeElement(label);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
	}

	/**
	 * Adds a new user to the participant list.
	 *
	 * @param userIcon
	 *            the icon to use initially.
	 * @param nickname
	 *            the users nickname.
	 */
	public synchronized void addUser(Icon userIcon, String nickname) {
		try {
			final JLabel user = new JLabel(nickname, userIcon,
					JLabel.HORIZONTAL);
			users.add(user);

			// Sort users alpha.
			Collections.sort(users, labelComp);

			// Add to the correct position in the model.
			final int index = users.indexOf(user);
			model.insertElementAt(user, index);
		} catch (Exception e) {
			Log.error(e);
		}
	}

    /**
     * Sorts ContactItems.
     */
    final Comparator<JLabel> labelComp = new Comparator<JLabel>() {

	public int compare(JLabel item1, JLabel item2) {
	    if (_localPreferences.isShowingRoleIcons()) {
		return compareWithRole(item1, item2);
	    } else {
		return compareWithoutRole(item1.getText(), item2.getText());
	    }

	}

	private int compareWithoutRole(String s1, String s2) {
	    return (s1.toLowerCase().compareTo(s2.toLowerCase()));
	}

	/**
	 * Comparaes 2 items by their Role and Affiliation<br>
	 * affiliation > role<br>
	 * owner > admin > moderator > member > participant > visitor
	 * @param item1
	 * @param item2
	 * @return -1, 0 or 1
	 */
	private int compareWithRole(JLabel item1, JLabel item2) {

	   int user1 = 100;
	    int user2 = 100;
	    try {
		// append Room-JID to UserLabel
		String jid1 = chat.getRoom() + "/" + item1.getText();
		String jid2 = chat.getRoom() + "/" + item2.getText();

		user1 = getCompareValue(jid1);
		user2 = getCompareValue(jid2);

	    } catch (Exception e) {
		// Sometimes theres no Occupant with that jid, dunno why
	    }

	    int result = 0;
	    if (user1 == user2) {
		result = compareWithoutRole(item1.getText(), item2.getText());

	    } else {
		// a=owner,b=admin, m=moderator, n=member , p=participant, v=visitor
		// a < b < m < n < p < v
		if (user1 < user2)
		    result = -1;
		if (user1 > user2)
		    result = 1;
	    }
	    return result;
	}
    };

    /**
     * check if we have an affiliation to this room<br>
     * and map it to an integer<br>
     * 0=owner,1=admin.....5=visitor<br>
     */
    private int getCompareValue(String jid)
	{
		MUCRole role = usersToRoles.get( jid );
		MUCAffiliation affiliation = usersToAffiliation.get( jid );
		switch ( affiliation )
		{
			case owner:
				return 0;
			case admin:
				return 1;
			case member:
				return ( role == MUCRole.moderator ? 2 : 3 );
			case none:
				switch ( role )
				{
					case moderator:
						return 2;
					case participant:
						return 4;
					case visitor:
						return 5;
					default:
						return 100;
				}
			default:
				return 100;
		}
	}

	/**
	 * The <code>JLabelIconRenderer</code> is the an implementation of
	 * ListCellRenderer to add icons w/ associated text in JComboBox and JList.
	 *
	 * @author Derek DeMoro
	 */
	public class ParticipantRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = -7509947975798079141L;

		/**
		 * Construct Default JLabelIconRenderer.
		 */
		public ParticipantRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			JLabel label = (JLabel) value;
			setText(label.getText());
			setIcon(label.getIcon());
			return this;
		}
	}
	
    protected GroupChatRoom getGroupChatRoom() {
        return groupChatRoom;
    }

    protected ImageTitlePanel getAgentInfoPanel() {
        return agentInfoPanel;
    }

    protected MultiUserChat getChat() {
        return chat;
    }

    protected Map<String, String> getUserMap() {
        return userMap;
    }

    protected DefaultListModel getModel() {
        return model;
    }

    protected JXList getParticipantsList() {

        return participantsList;

    }

    protected PresenceListener getListener() {
        return listener;
    }

    protected Map<String, String> getInvitees() {
        return invitees;
    }

    protected boolean isAllowNicknameChange() {
        return allowNicknameChange;
    }

    protected DiscoverInfo getRoomInformation() {
        return roomInformation;
    }

    protected List<JLabel> getUsers() {
        return users;
    }

    protected Comparator<JLabel> getLabelComp() {
        return labelComp;
    }
}

