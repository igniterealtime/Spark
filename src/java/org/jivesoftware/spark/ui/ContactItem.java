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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettings;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * Represent a single contact within the <code>ContactList</code>.
 */
public class ContactItem extends JPanel {

	private static final long serialVersionUID = 1514044406550293152L;
	private JLabel imageLabel;
    private JLabel displayNameLabel;
    private JLabel descriptionLabel;
    private String nickname;
    private String alias;
    private final String fullyQualifiedJID;
	private JLabel specialImageLabel;
    private Icon icon;

    private String status;
    private String groupName;

    boolean available;

    private Presence presence;

    private String hash = "";

    private File contactsDir;

    private JLabel sideIcon;

    private int fontSize;

    private int iconSize;

    private boolean avatarsShowing;

	public ContactItem(String alias, String nickname, String fullyQualifiedJID) {
		this(alias, nickname, fullyQualifiedJID, true);
	}

    /**
     * Creates a new instance of a contact.
     *
     * @param alias             the alias of the contact
     * @param nickname          the nickname of the contact.
     * @param fullyQualifiedJID the fully-qualified jid of the contact (ex. derek@jivesoftware.com)
     */
    public ContactItem(String alias, String nickname, String fullyQualifiedJID, boolean initUi) {
        setLayout(new GridBagLayout());

        // Set Default Font
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        fontSize = pref.getContactListFontSize();
        iconSize = pref.getContactListIconSize();
        avatarsShowing = pref.areAvatarsVisible();

        // Set default presence
        presence = new Presence(Presence.Type.unavailable);

        contactsDir = new File(SparkManager.getUserDirectory(), "contacts");

        this.alias = alias;
        this.nickname = nickname;
        this.fullyQualifiedJID = fullyQualifiedJID;

        if (initUi) {
		displayNameLabel = new JLabel();
		descriptionLabel = new JLabel();
		imageLabel = new JLabel();
		specialImageLabel = new JLabel();
		sideIcon = new JLabel();
		if (avatarsShowing) {
			sideIcon.setMinimumSize(new Dimension(iconSize, iconSize));
			sideIcon.setMaximumSize(new Dimension(iconSize, iconSize));
			sideIcon.setPreferredSize(new Dimension(iconSize, iconSize));
		}

		displayNameLabel.setHorizontalTextPosition(JLabel.LEFT);
		displayNameLabel.setHorizontalAlignment(JLabel.LEFT);
		//displayNameLabel.setText(nickname);


		descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, fontSize));
		descriptionLabel.setForeground((Color)UIManager.get("ContactItemDescription.foreground"));
		descriptionLabel.setHorizontalTextPosition(JLabel.LEFT);
		descriptionLabel.setHorizontalAlignment(JLabel.LEFT);


		this.setOpaque(true);

		add(imageLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 0), 0, 0));
		add(displayNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(descriptionLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 0), 0, 0));
		add(specialImageLabel, new GridBagConstraints(3, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(sideIcon, new GridBagConstraints(4, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		setDisplayName();
        }
    }

	/**
	 * Returns the name that should be displayed to represent the the contact.
	 * If an alias has been set, this alias will be returned. If no alias has
	 * been set, the nickname will be returned. If that hasn't been set either,
	 * the JID will be returned.
	 *
	 * @return a name suitable to be displayed
	 */
    public String getDisplayName() {
    	final String displayName;
		if (alias != null) {
			displayName = alias;
		} else if (nickname != null) {
			displayName = nickname;
		} else {
			displayName = getJID();
		}

		if (displayName != null) {
		return displayName;
		} else {
			return ""; // weird, but happens.
		}
	}

    /**
	 * Returns the nickname of the contact. Note that for typical user-interface
	 * related tasks, you probably should use {@link #getDisplayName()} instead.
	 *
	 * @return the contact nickname.
	 */
	public String getNickname() {
		return nickname;
	}

    /**
     * Sets the nickname of the contact.
     *
     * @param nickname the contact nickname.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
        if (alias == null) {
        	setDisplayName();
        }
    }

    /**
	 * Returns the alias of the contact. Note that for typical user-interface
	 * related tasks, you probably should use {@link #getDisplayName()} instead.
	 *
	 * @return the contact alias.
	 */
	public String getAlias() {
		return alias;
	}

    /**
     * Sets the alias of the contact.
     *
     * @param alias the contact alias.
     */
    public void setAlias(String alias) {
    	this.alias = alias;
    	setDisplayName();
    }

    /**
	 * Updates the displayed name for the contact. This method tries to use an
	 * alias first. If that's not set, the nickname will be used instead. If
	 * that's not set either, the JID of the user will be used.
	 */
    protected void setDisplayName() {
    	final String displayName = getDisplayName();

        int nickLength = displayName.length();

        LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();
        int windowWidth = settings.getMainWindowWidth();

        if (nickLength > windowWidth) {
            displayNameLabel.setText(StringUtils.unescapeNode(displayName).substring(0, windowWidth) + "...");
        } else {
            displayNameLabel.setText(StringUtils.unescapeNode(displayName));
        }
    }

    /**
     * Returns the fully qualified JID of the contact. (If available). Otherwise will
     * return the bare jid.
     *
     * @return the fully qualified jid (ex. derek@jivesoftware.com).
     */
    public String getJID() {
        return fullyQualifiedJID;
    }

    /**
     * Returns the icon showing the contacts current state or presence.
     *
     * @return the icon.
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Sets the current icon to use.
     *
     * @param icon the current icon to use.
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
        imageLabel.setIcon(icon);
    }

    /**
     * Returns the contacts current status based on their presence.
     *
     * @return the contacts current status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the contacts current status.
     *
     * @param status the contacts current status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the name of the <code>ContactGroup</code> that this contact belongs to.
     *
     * @return the name of the <code>ContactGroup</code>.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the name of the <code>ContactGrouop</code> that this contact belongs to.
     *
     * @param groupName the name of the ContactGroup.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Returns the <code>JLabel</code> showing the users nickname.
     *
     * @return the nickname label.
     */
    public JLabel getNicknameLabel() {
        return displayNameLabel;
    }

    /**
     * Returns the <code>JLabel</code> representing the description.
     *
     * @return the description label.
     */
    public JLabel getDescriptionLabel() {
        return descriptionLabel;
    }

    /**
     * Returns the current presence of the contact.
     *
     * @return the users current presence.
     */
    public Presence getPresence() {
        return presence;
    }

    /**
     * Sets the current presence on this contact item.
     *
     * @param presence the presence.
     */
    public void setPresence(Presence presence) {

        this.presence = presence;

        final PacketExtension packetExtension = presence.getExtension("x", "vcard-temp:x:update");

        // Handle vCard update packet.
        if (packetExtension != null && packetExtension instanceof DefaultPacketExtension) {
            DefaultPacketExtension o = (DefaultPacketExtension)packetExtension;
            String hash = o.getValue("photo");
            if (hash != null) {
                this.hash = hash;

                if (!hashExists(hash)) {
                    updateAvatar();
                    updateAvatarInSideIcon();
                }
            }
        }

        
        updatePresenceIcon(presence);
    }

    /**
     * Checks to see if the hash already exists.
     *
     * @param hash the hash.
     * @return true if the hash exists, otherwise false.
     */
    private boolean hashExists(String hash) {
        contactsDir.mkdirs();

        final File imageFile = new File(contactsDir, hash);
        return imageFile.exists();
    }

    /**
     * Returns the url of the avatar belonging to this contact.
     *
     * @return the url of the avatar.
     * @throws MalformedURLException thrown if the address is invalid.
     */
    public URL getAvatarURL() throws MalformedURLException {
        contactsDir.mkdirs();

        if (ModelUtil.hasLength(hash)) {
            final File imageFile = new File(contactsDir, hash);
            if (imageFile.exists()) {
                return imageFile.toURI().toURL();
            }
        }

        return SparkManager.getVCardManager().getAvatarURLIfAvailable(getJID());
    }

    /**
     * Persists the avatar locally based on the new hash.
     */
    private void updateAvatar() {
    	SparkManager.getVCardManager().addToQueue(getJID());
    }

    public String toString() {
        return displayNameLabel.getText();
    }


    /**
     * Updates the icon of the user based on their presence.
     *
     * @param presence the users presence.
     */
    public void updatePresenceIcon(Presence presence) {
        ChatManager chatManager = SparkManager.getChatManager();
        boolean handled = chatManager.fireContactItemPresenceChanged(this, presence);
        if (handled) {
            return;
        }

        String status = presence.getStatus();
        Icon statusIcon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
        boolean isAvailable = false;
        if (status == null && presence.isAvailable()) {
            Presence.Mode mode = presence.getMode();
            if (mode == Presence.Mode.available) {
                status = Res.getString("status.online");
                isAvailable = true;
            }
            else if (mode == Presence.Mode.away) {
                status = Res.getString("status.away");
                statusIcon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
            }
            else if (mode == Presence.Mode.chat) {
                status = Res.getString("status.free.to.chat");
            }
            else if (mode == Presence.Mode.dnd) {
                status = Res.getString("status.do.not.disturb");
                statusIcon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
            }
            else if (mode == Presence.Mode.xa) {
                status = Res.getString("status.extended.away");
                statusIcon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
            }
        }

        if (presence.isAvailable() && (presence.getMode() == Presence.Mode.dnd || presence.getMode() == Presence.Mode.away || presence.getMode() == Presence.Mode.xa)) {
            statusIcon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
        }
        else if (presence.isAvailable()) {
            isAvailable = true;
        }
        else if (!presence.isAvailable()) {
            getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, fontSize));
            getNicknameLabel().setForeground((Color)UIManager.get("ContactItemOffline.color"));

            RosterEntry entry = SparkManager.getConnection().getRoster().getEntry(getJID());
            if (entry != null && (entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from)
                    && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus()) {
                // Do not move out of group.
                setIcon(SparkRes.getImageIcon(SparkRes.SMALL_QUESTION));
                getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, fontSize));
                setStatusText(Res.getString("status.pending"));
            }
            else {
                setIcon(null);
                setFont(new Font("Dialog", Font.PLAIN, fontSize));
                getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, fontSize));
                setAvailable(false);
                if (ModelUtil.hasLength(status)) {
                    setStatusText(status);
                }
                else {
                    setStatusText("");
                }
            }

            sideIcon.setIcon(null);
            setAvailable(false);
            return;
        }

        Icon sIcon = PresenceManager.getIconFromPresence(presence);
        if (sIcon != null) {
            setIcon(sIcon);
        }
        else {
            setIcon(statusIcon);
        }
        if (status != null) {
            setStatus(status);
        }

        if (PresenceManager.isOnPhone(presence)) {
            statusIcon = SparkRes.getImageIcon(SparkRes.ON_PHONE_IMAGE);
            setIcon(statusIcon);
        }

        // Always change nickname label to black.
        getNicknameLabel().setForeground((Color)UIManager.get("ContactItemNickname.foreground"));


        if (isAvailable) {
            getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, fontSize));
            if (Res.getString("status.online").equals(status) || Res.getString("available").equalsIgnoreCase(status)) {
                setStatusText("");
            }
            else {
                setStatusText(status);
            }
        }
        else if (presence.isAvailable()) {
            getNicknameLabel().setFont(new Font("Dialog", Font.ITALIC, fontSize));
            getNicknameLabel().setForeground(Color.gray);
            if (status != null) {
                setStatusText(status);
            }
        }

        setAvailable(true);
    }

    /**
     * Sets the status label text based on the users status.
     *
     * @param status the users status.
     */
    public void setStatusText(String status) {
        setStatus(status);

        if (ModelUtil.hasLength(status)) {
            getDescriptionLabel().setText(" - " + status);
        }
        else {
            getDescriptionLabel().setText("");
        }
    }

    /**
     * The icon should only be used to display avatars in contact list. if you want to add an icon
     * to indicated that this contact is a transport e.g you should use setSpecialIcon()
     *
     * @param icon the icon to use.
     */
    public void setSideIcon(Icon icon) {
        sideIcon.setIcon(icon);
    }


    /**
     * The icon to use to show extra information about this contact. An example would be to
     * represent that this user is from a 3rd party transport.
     *
     * @param icon the icon to use.
     */
    public void setSpecialIcon(Icon icon)
    {
        specialImageLabel.setIcon(icon);
    }

    /**
     * Shows that the user is coming online.
     */
    public void showUserComingOnline() {
        // Change Font
        getNicknameLabel().setFont(new Font("Dialog", Font.BOLD, fontSize));
        getNicknameLabel().setForeground(new Color(255, 128, 0));
    }

    /**
     * Shows that the user is going offline.
     */
    public void showUserGoingOfflineOnline() {
        // Change Font
        getNicknameLabel().setFont(new Font("Dialog", Font.BOLD, fontSize));
        getNicknameLabel().setForeground(Color.red);
    }

    /**
     * Update avatar icon.
     */
	public void updateAvatarInSideIcon() {
		try {
			final URL url = getAvatarURL();
			if (url != null) {
				if (!avatarsShowing) {
					setSideIcon(null);
				} else {
					ImageIcon icon = new ImageIcon(url);
					icon = GraphicUtils.scale(icon, iconSize, iconSize);
					setSideIcon(icon);
				}
			}
		} catch (MalformedURLException e) {
			Log.error(e);
		}
	}

    protected JLabel getDisplayNameLabel() {
        return displayNameLabel;
    }

    protected String getFullyQualifiedJID() {
        return fullyQualifiedJID;
    }

    protected void setDisplayNameLabel(JLabel displayNameLabel) {
        this.displayNameLabel = displayNameLabel;
    }

    protected void setDescriptionLabel(JLabel descriptionLabel) {
        this.descriptionLabel = descriptionLabel;
    }

    protected JLabel getSpecialImageLabel() {
        return specialImageLabel;
    }

    protected void setSpecialImageLabel(JLabel specialImageLabel) {
        this.specialImageLabel = specialImageLabel;
    }


}
