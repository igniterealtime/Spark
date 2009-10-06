/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jdesktop.swingx.JXList;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.UserStatusListener;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.ImageTitlePanel;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;

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

/**
 * The <code>RoomInfo</code> class is used to display all room information, such
 * as agents and room information.
 */
public final class GroupChatParticipantList extends JPanel implements
		ChatRoomListener {

    	private static final long serialVersionUID = 3809155443119207342L;
	private GroupChatRoom groupChatRoom;
	private final ImageTitlePanel agentInfoPanel;
	private ChatManager chatManager;
	private MultiUserChat chat;

	private final Map<String, String> userMap = new HashMap<String, String>();

	private UserManager userManager = SparkManager.getUserManager();

	private DefaultListModel model = new DefaultListModel();

	private JXList participantsList;

	private PacketListener listener = null;

	private Map<String, String> invitees = new HashMap<String, String>();

	private boolean allowNicknameChange = true;

	private DiscoverInfo roomInformation;

	private List<JLabel> users = new ArrayList<JLabel>();

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

		// Speed up scrolling. It was way too slow.
		scroller.getVerticalScrollBar().setBlockIncrement(50);
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		scroller.setBackground(Color.white);
		scroller.getViewport().setBackground(Color.white);

		add(scroller, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
	}

	public void setChatRoom(final ChatRoom chatRoom) {
		this.groupChatRoom = (GroupChatRoom) chatRoom;

		chatManager.addChatRoomListener(this);

		chat = groupChatRoom.getMultiUserChat();

		chat.addInvitationRejectionListener(new InvitationRejectionListener() {
			public void invitationDeclined(String jid, String message) {
				String nickname = userManager.getUserNicknameFromJID(jid);

				userHasLeft(chatRoom, nickname);

				chatRoom.getTranscriptWindow().insertNotificationMessage(
						nickname + " has rejected the invitation.",
						ChatManager.NOTIFICATION_COLOR);
			}
		});

		listener = new PacketListener() {
			public void processPacket(final Packet packet) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Presence p = (Presence) packet;
						if (p.getError() != null) {
							if (p.getError().getCondition().equals(
									XMPPError.Condition.conflict.toString())) {
								return;
							}
						}
						final String userid = p.getFrom();

						String displayName = StringUtils.parseResource(userid);
						userMap.put(displayName, userid);

						if (p.getType() == Presence.Type.available) {
							addParticipant(userid, p);
							agentInfoPanel.setVisible(true);
						} else {
							removeUser(displayName);
						}
					}
				});

			}
		};

		chat.addParticipantListener(listener);

		ServiceDiscoveryManager disco = ServiceDiscoveryManager
				.getInstanceFor(SparkManager.getConnection());
		try {
			roomInformation = disco.discoverInfo(chat.getRoom());
		} catch (XMPPException e) {
			Log.debug("Unable to retrieve room informatino for "
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

	public void chatRoomLeft(ChatRoom room) {
		if (this.groupChatRoom == room) {
			chatManager.removeChatRoomListener(this);
			agentInfoPanel.setVisible(false);
		}
	}

	public void chatRoomClosed(ChatRoom room) {
		if (this.groupChatRoom == room) {
			chatManager.removeChatRoomListener(this);
			chat.removeParticipantListener(listener);
		}
	}

	public void chatRoomActivated(ChatRoom room) {
	}

	public void userHasJoined(ChatRoom room, String userid) {
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

	private ImageIcon getImageIcon(String participantJID) {
		String displayName = StringUtils.parseResource(participantJID);
		ImageIcon icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
		icon.setDescription(displayName);
		return icon;
	}

	private void addParticipant(String participantJID, Presence presence) {
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

		String nickname = StringUtils.parseResource(participantJID);

		if (!exists(nickname)) {
			Icon icon;

			icon = PresenceManager.getIconFromPresence(presence);
			if (icon == null) {
				icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
			}

			addUser(icon, nickname);
		} else {
			Icon icon = PresenceManager.getIconFromPresence(presence);
			if (icon == null) {
				icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
			}

			int index = getIndex(nickname);
			if (index != -1) {
				final JLabel userLabel = new JLabel(nickname, icon,
						JLabel.HORIZONTAL);
				model.setElementAt(userLabel, index);
			}
		}
	}

	public void userHasLeft(ChatRoom room, String userid) {
		if (room != groupChatRoom) {
			return;
		}

		int index = getIndex(userid);

		if (index != -1) {
			removeUser(userid);
			userMap.remove(userid);
		}
	}

	private boolean exists(String nickname) {
		for (int i = 0; i < model.getSize(); i++) {
			final JLabel userLabel = (JLabel) model.getElementAt(i);
			if (userLabel.getText().equals(nickname)) {
				return true;
			}
		}
		return false;
	}

	private String getSelectedUser() {
		JLabel label = (JLabel) participantsList.getSelectedValue();
		if (label != null) {
			return label.getText();
		}

		return null;
	}

	private void startChat(ChatRoom groupChat, String groupJID) {
		String userNickname = StringUtils.parseResource(groupJID);
		String roomTitle = userNickname + " - "
				+ StringUtils.parseName(groupChat.getRoomname());

		String nicknameOfUser = StringUtils.parseResource(groupJID);
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

	private void kickUser(String nickname) {
		try {
			chat.kickParticipant(nickname, Res
					.getString("message.you.have.been.kicked"));
		} catch (XMPPException e) {
			groupChatRoom.insertText(Res.getString("message.kicked.error",
					nickname));
		}
	}

	private void banUser(String displayName) {
		try {
			Occupant occupant = chat.getOccupant(userMap.get(displayName));
			if (occupant != null) {
				String bareJID = StringUtils
						.parseBareAddress(occupant.getJid());
				chat.banUser(bareJID, Res
						.getString("message.you.have.been.banned"));
			}
		} catch (XMPPException e) {
			Log.error(e);
		}
	}

	private void unbanUser(String jid) {
		try {
			chat.grantMembership(jid);
		} catch (XMPPException e) {
			Log.error(e);
		}
	}

	private void grantVoice(String nickname) {
		try {
			chat.grantVoice(nickname);
		} catch (XMPPException e) {
			Log.error(e);
		}
	}

	private void revokeVoice(String nickname) {
		try {
			chat.revokeVoice(nickname);
		} catch (XMPPException e) {
			Log.error(e);
		}
	}

	private void grantModerator(String nickname) {
		try {
			chat.grantModerator(nickname);
		} catch (XMPPException e) {
			Log.error(e);
		}
	}

	private void revokeModerator(String nickname) {
		try {
			chat.revokeModerator(nickname);
		} catch (XMPPException e) {
			Log.error(e);
		}
	}

	/**
	 * Let's make sure that the panel doesn't strech past the scrollpane view
	 * pane.
	 * 
	 * @return the preferred dimension
	 */
	public Dimension getPreferredSize() {
		final Dimension size = super.getPreferredSize();
		size.width = 150;
		return size;
	}

	private void checkPopup(MouseEvent evt) {
		Point p = evt.getPoint();
		final int index = participantsList.locationToIndex(p);

		final JPopupMenu popup = new JPopupMenu();

		if (index != -1) {
			participantsList.setSelectedIndex(index);
			final JLabel userLabel = (JLabel) model.getElementAt(index);
			final String selectedUser = userLabel.getText();
			final String groupJID = userMap.get(selectedUser);
			String groupJIDNickname = StringUtils.parseResource(groupJID);

			final String nickname = groupChatRoom.getNickname();
			final Occupant occupant = userManager.getOccupant(groupChatRoom,
					selectedUser);
			final boolean admin = SparkManager.getUserManager().isOwnerOrAdmin(
					groupChatRoom, chat.getNickname());
			final boolean moderator = SparkManager.getUserManager()
					.isModerator(groupChatRoom, chat.getNickname());

			final boolean userIsAdmin = userManager.isOwnerOrAdmin(occupant);
			final boolean userIsModerator = userManager.isModerator(occupant);
			boolean isMe = nickname.equals(groupJIDNickname);

			// Handle invites
			if (groupJIDNickname == null) {
				Action inviteAgainAction = new AbstractAction() {
					private static final long serialVersionUID = -1875073139356098243L;

					public void actionPerformed(ActionEvent actionEvent) {
						String message = invitees.get(selectedUser);
						String jid = userManager
								.getJIDFromDisplayName(selectedUser);
						chat.invite(jid, message);
					}
				};

				inviteAgainAction.putValue(Action.NAME, Res.getString("menuitem.inivite.again"));
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

				removeInvite.putValue(Action.NAME, Res.getString("menuitem.remove"));
				popup.add(removeInvite);

				popup.show(participantsList, evt.getX(), evt.getY());
				return;
			}

			if (isMe) {
				Action changeNicknameAction = new AbstractAction() {
					private static final long serialVersionUID = -7891803180672794112L;

					public void actionPerformed(ActionEvent actionEvent) {
						String newNickname = JOptionPane.showInputDialog(
								groupChatRoom, Res
										.getString("label.new.nickname")
										+ ":", Res
										.getString("title.change.nickname"),
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
								} catch (XMPPException e1) {
									newNickname = JOptionPane
											.showInputDialog(
													groupChatRoom,
													Res
															.getString("message.nickname.in.use")
															+ ":",
													Res
															.getString("title.change.nickname"),
													JOptionPane.QUESTION_MESSAGE);
									if (!ModelUtil.hasLength(newNickname)) {
										break;
									}
								}
							}
						}
					}
				};

				changeNicknameAction.putValue(Action.NAME, Res
						.getString("menuitem.change.nickname"));
				changeNicknameAction.putValue(Action.SMALL_ICON, SparkRes
						.getImageIcon(SparkRes.DESKTOP_IMAGE));

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

			chatAction.putValue(Action.NAME, Res
					.getString("menuitem.start.a.chat"));
			chatAction.putValue(Action.SMALL_ICON, SparkRes
					.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
			if (!isMe) {
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

			blockAction.putValue(Action.NAME, Res
					.getString("menuitem.block.user"));
			blockAction.putValue(Action.SMALL_ICON, SparkRes
					.getImageIcon(SparkRes.BRICKWALL_IMAGE));
			if (!isMe) {
				if (groupChatRoom.isBlocked(groupJID)) {
					blockAction.putValue(Action.NAME, Res
							.getString("menuitem.unblock.user"));
				}
				popup.add(blockAction);
			}

			Action kickAction = new AbstractAction() {
				private static final long serialVersionUID = 5769982955040961189L;

				public void actionPerformed(ActionEvent actionEvent) {
					kickUser(selectedUser);
				}
			};

			kickAction.putValue(Action.NAME, Res
					.getString("menuitem.kick.user"));
			kickAction.putValue(Action.SMALL_ICON, SparkRes
					.getImageIcon(SparkRes.SMALL_DELETE));
			if (moderator && !userIsAdmin && !isMe) {
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

				}
			};

			voiceAction.putValue(Action.NAME, Res.getString("menuitem.voice"));
			voiceAction.putValue(Action.SMALL_ICON, SparkRes
					.getImageIcon(SparkRes.MEGAPHONE_16x16));
			if (moderator && !userIsModerator && !isMe) {
				if (userManager.hasVoice(groupChatRoom, selectedUser)) {
					voiceAction.putValue(Action.NAME, Res
							.getString("menuitem.revoke.voice"));
				} else {
					voiceAction.putValue(Action.NAME, Res
							.getString("menuitem.grant.voice"));
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
			banAction.putValue(Action.SMALL_ICON, SparkRes
					.getImageIcon(SparkRes.RED_FLAG_16x16));
			if (admin && !userIsModerator && !isMe) {
				popup.add(banAction);
			}

			Action moderatorAction = new AbstractAction() {
				private static final long serialVersionUID = 8162535640460764896L;

				public void actionPerformed(ActionEvent actionEvent) {
					if (!userIsModerator) {
						grantModerator(selectedUser);
					} else {
						revokeModerator(selectedUser);
					}
				}
			};

			moderatorAction.putValue(Action.SMALL_ICON, SparkRes
					.getImageIcon(SparkRes.MODERATOR_IMAGE));
			if (admin && !userIsModerator) {
				moderatorAction.putValue(Action.NAME, Res
						.getString("menuitem.grant.moderator"));
				popup.add(moderatorAction);
			} else if (admin && userIsModerator && !isMe) {
				moderatorAction.putValue(Action.NAME, Res
						.getString("menuitem.revoke.moderator"));
				popup.add(moderatorAction);
			}

			// Handle Unbanning of users.
			Action unbanAction = new AbstractAction() {
				private static final long serialVersionUID = 3672121864443182872L;

				public void actionPerformed(ActionEvent actionEvent) {
					String jid = ((JMenuItem) actionEvent.getSource())
							.getText();
					unbanUser(jid);
				}
			};

			if (admin) {
				JMenu unbanMenu = new JMenu(Res.getString("menuitem.unban"));
				Iterator<Affiliate> bannedUsers = null;
				try {
					bannedUsers = chat.getOutcasts().iterator();
				} catch (XMPPException e) {
					Log.error("Error loading all banned users", e);
				}

				while (bannedUsers != null && bannedUsers.hasNext()) {
					Affiliate bannedUser = (Affiliate) bannedUsers.next();
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
				ConferenceUtils.inviteUsersToRoom(groupChatRoom
						.getConferenceService(), groupChatRoom.getRoomname(),
						null);
			}
		};

		inviteAction.putValue(Action.NAME, Res
				.getString("menuitem.invite.users"));
		inviteAction.putValue(Action.SMALL_ICON, SparkRes
				.getImageIcon(SparkRes.CONFERENCE_IMAGE_16x16));

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
			return item1.getText().toLowerCase().compareTo(
					item2.getText().toLowerCase());
		}
	};

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
}

