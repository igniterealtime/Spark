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
import java.util.Comparator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.profile.ext.VCardUpdateExtension;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * Represent a single contact within the <code>ContactList</code>.
 */
public class ContactItem extends JPanel {
    public final static Comparator<ContactItem> CONTACT_ITEM_COMPARATOR = Comparator.comparing(ContactItem::getDisplayName, String.CASE_INSENSITIVE_ORDER);

	private final JLabel imageLabel = new JLabel();
    private final JLabel displayNameLabel = new JLabel();
    private final JLabel descriptionLabel = new JLabel();
    private final JLabel specialImageLabel = new JLabel();
    private final JLabel sideIcon = new JLabel();

    private String nickname;
    private String alias;
    private final BareJid jid;
    private Icon icon;
    private String status;
    private String groupName;
    private boolean available;
    private Presence presence;
    private String hash;
    private final int fontSize;
    private final int iconSize;
    private final boolean avatarsShowing;

    private static final Color COLOR_USER_ONLINE_NICKNAME = new Color(255, 128, 0);

	public ContactItem(String alias, String nickname, BareJid jid) {
        this.alias = trimToEmpty(alias);
        this.nickname = trimToEmpty(nickname);
        this.jid = requireNonNull(jid);
        setLayout(new GridBagLayout());

        // Set Default Font
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        fontSize = pref.getContactListFontSize();
        iconSize = pref.getContactListIconSize();
        avatarsShowing = pref.areAvatarsVisible();

        // Set default presence
        presence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unavailable)
            .build();

            if (avatarsShowing) {
                sideIcon.setMinimumSize(new Dimension(iconSize, iconSize));
                sideIcon.setMaximumSize(new Dimension(iconSize, iconSize));
                sideIcon.setPreferredSize(new Dimension(iconSize, iconSize));
            }

            displayNameLabel.setHorizontalTextPosition(JLabel.LEFT);
            displayNameLabel.setHorizontalAlignment(JLabel.LEFT);

            descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, fontSize));
            descriptionLabel.setForeground((Color) UIManager.get("ContactItemDescription.foreground"));
            descriptionLabel.setHorizontalTextPosition(JLabel.LEFT);
            descriptionLabel.setHorizontalAlignment(JLabel.LEFT);

            this.setOpaque(true);

            add(imageLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 0), 0, 0));
            add(displayNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
            add(descriptionLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 0), 0, 0));
            add(specialImageLabel, new GridBagConstraints(3, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
            add(sideIcon, new GridBagConstraints(4, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

        updateDisplayName();
    }

	/**
	 * Returns the name that should be displayed to represent the contact.
	 * If an alias has been set, this alias will be returned. If no alias has
	 * been set, the nickname will be returned. If that hasn't been set either,
	 * the JID will be returned.
	 */
    public String getDisplayName() {
        return displayNameLabel.getText();
	}

    /**
	 * Returns the nickname of the contact from vCard. Note that for typical user-interface
	 * related tasks, you probably should use {@link #getDisplayName()} instead.
	 */
	public String getNickname() {
		return nickname;
	}

    public void setNickname(String nickname) {
        this.nickname = trimToEmpty(nickname);
        updateDisplayName();
    }

    /**
	 * Returns the alias of the contact. Note that for typical user-interface
	 * related tasks, you probably should use {@link #getDisplayName()} instead.
	 */
	public String getAlias() {
		return alias;
	}

    public void setAlias(String alias) {
        this.alias = trimToEmpty(alias);
        updateDisplayName();
    }

    /**
	 * Updates the displayed name for the contact. This method tries to use an
	 * alias first. If that's not set, the nickname will be used instead. If
	 * that's not set either, the JID of the user will be used.
	 */
    protected void updateDisplayName() {
        String displayName = calcDisplayName();
        displayNameLabel.setText(displayName);
    }

    private String calcDisplayName() {
        if (!alias.isBlank()) {
            return alias;
        }
        if (!nickname.isBlank()) {
            return nickname;
        }
        return getJid().asUnescapedString();
    }

    /**
     * Return the XMPP address, aka. JID of this contact item.
     */
    public BareJid getJid() {
        return jid;
    }

    /**
     * Returns the icon showing the contacts current state or presence.
     */
    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        imageLabel.setIcon(icon);
    }

    /**
     * Returns the contacts current status based on their presence.
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the name of the <code>ContactGroup</code> that this contact belongs to.
     */
    public String getGroupName() {
        return groupName;
    }

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
     */
    public JLabel getNicknameLabel() {
        return displayNameLabel;
    }

    /**
     * Returns the current presence of the contact.
     */
    public Presence getPresence() {
        return presence;
    }

    /**
     * Sets the current presence on this contact item.
     */
    public void setPresence(Presence presence) {
        this.presence = presence;
        final VCardUpdateExtension extension = presence.getExtension(VCardUpdateExtension.class);
        // Handle vCard update packet.
        if (extension != null) {
            String hash = extension.getPhotoHash();
            // if hash was changed. Note: old Tigase sends "null"
            if (hash != null && !hash.equals(this.hash) && !hash.equals("null")) {
                if (this.hash != null) {
                    //TODO delete old avatar
                }
                this.hash = hash;
                if (!hashExists(hash)) {
                    // Persists the avatar locally based on the new hash.
                    SparkManager.getVCardManager().reloadVCard(getJid().asEntityBareJidIfPossible());
                    updateAvatarInSideIcon();
                }
            }
        }
        updatePresenceIcon(presence);
    }

    /**
     * Checks to see if the hash already exists.
     */
    private boolean hashExists(String hash) {
        final File imageFile = new File(SparkManager.getContactsDir(), hash);
        return imageFile.exists();
    }

    /**
     * Returns the url of the avatar belonging to this contact.
     */
    public URL getAvatarURL()  {
        if (ModelUtil.hasLength(hash)) {
            File imageFile = new File(SparkManager.getContactsDir(), hash);
            if (imageFile.exists()) {
                try {
                    return imageFile.toURI().toURL();
                } catch (MalformedURLException ignored) {
                    // never happens
                }
            }
        }

        return SparkManager.getVCardManager().getAvatarURLIfAvailable(getJid());
    }

    public int getFontSize() {
        return fontSize;
    }

    @Override
	public String toString() {
        return displayNameLabel.getText();
    }


    /**
     * Updates the icon of the user based on their presence.
     */
    public void updatePresenceIcon(Presence presence) {
        ChatManager chatManager = SparkManager.getChatManager();
        boolean handled = chatManager.fireContactItemPresenceChanged(this, presence);
        if (handled) {
            return;
        }

        String status = presence.getStatus();
        Icon statusIcon = SparkRes.getImageIcon(SparkRes.Icon.GREEN_BALL);
        boolean isAvailable = false;
        if (status == null && presence.isAvailable()) {
            Presence.Mode mode = presence.getMode();
            if (mode == Presence.Mode.available) {
                status = Res.getString("status.online");
                isAvailable = true;
            }
            else if (mode == Presence.Mode.away) {
                status = Res.getString("status.away");
                statusIcon = SparkRes.getImageIcon(SparkRes.Icon.IM_AWAY);
            }
            else if (mode == Presence.Mode.chat) {
                status = Res.getString("status.free.to.chat");
            }
            else if (mode == Presence.Mode.dnd) {
                status = Res.getString("status.do.not.disturb");
                statusIcon = SparkRes.getImageIcon(SparkRes.Icon.IM_AWAY);
            }
            else if (mode == Presence.Mode.xa) {
                status = Res.getString("status.extended.away");
                statusIcon = SparkRes.getImageIcon(SparkRes.Icon.IM_XA);
            }
        }

        // Sets status icon and text based on presence
        if (presence.isAway()) {
            statusIcon = SparkRes.getImageIcon(SparkRes.Icon.IM_AWAY);
        }
        else if (presence.isAvailable()) {
            isAvailable = true;
        }
        else {
            getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, fontSize));
            getNicknameLabel().setForeground((Color)UIManager.get("ContactItemOffline.color"));

            Roster roster = SparkManager.getRoster();
            RosterEntry entry = roster.getEntry(getJid());
            if (entry != null && (entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from)
                    && entry.isSubscriptionPending()) {
                // Do not move out of group.
                setIcon(SparkRes.getImageIcon(SparkRes.Icon.SMALL_QUESTION));
                getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, fontSize));
                setStatusText(Res.getString("status.pending"));
            }
            else {
            	//We should keep the offline bullet (not available) instead of putting icon null.
            	setIcon(SparkRes.getImageIcon(SparkRes.Icon.CLEAR_BALL_ICON));
                setFont(new Font("Dialog", Font.PLAIN, fontSize));
                getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, fontSize));
                setAvailable(false);
                setStatusText(!isBlank(status) ? status : "");
            }

            sideIcon.setIcon(null);
            setAvailable(false);
            return;
        }

        Icon sIcon = PresenceManager.getIconFromPresence(presence);
        setIcon(sIcon != null ? sIcon : statusIcon);
        if (status != null) {
            setStatus(status);
        }

        if (PresenceManager.isOnPhone(presence)) {
            statusIcon = SparkRes.getImageIcon(SparkRes.Icon.ON_PHONE_IMAGE);
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
       	  	LocalPreferences pref = SettingsManager.getLocalPreferences();
       	  	if(pref.isGrayingOutEnabled())
       	  	{
       	  		getNicknameLabel().setFont(new Font("Dialog", Font.ITALIC, fontSize));
                getNicknameLabel().setForeground(Color.gray);	
       	  	}
       	  	else
       	  	{
       	  		getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, fontSize));
                getNicknameLabel().setForeground(Color.black);
       	  	}
            if (status != null) {
                setStatusText(status);
            }
        }

        setAvailable(true);
    }

    /**
     * Sets the status label text based on the users status.
     */
    public void setStatusText(String status) {
        setStatus(status);
        descriptionLabel.setText(!isBlank(status) ? " - " + status : "");
    }

    /**
     * The icon should only be used to display avatars in contact list. if you want to add an icon
     * to indicated that this contact is a transport e.g you should use setSpecialIcon()
     */
    public void setSideIcon(Icon icon) {
        sideIcon.setIcon(icon);
    }

    /**
     * The icon to use to show extra information about this contact. An example would be to
     * represent that this user is from a 3rd party transport.
     */
    public Icon getSpecialIcon() {
        return specialImageLabel.getIcon();
    }

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
        getNicknameLabel().setForeground(COLOR_USER_ONLINE_NICKNAME);
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
        if (!avatarsShowing) {
            setSideIcon(null);
            return;
        }
		try {
			final URL url = getAvatarURL();
			if (url != null) {
					ImageIcon icon = new ImageIcon(url);
					icon = GraphicUtils.scale(icon, iconSize, iconSize);
					setSideIcon(icon);
			}
		} catch (Exception e) {
			Log.warning("Unable to update avatar in side icon", e);
		}
	}

}
