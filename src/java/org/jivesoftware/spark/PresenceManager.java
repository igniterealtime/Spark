/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;

import javax.swing.Icon;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the most common presence checks.
 *
 * @author Derek DeMoro
 */
public class PresenceManager {

    private static final List<Presence> PRESENCES = new ArrayList<Presence>();


    static {
        // Add Available Presence
        final Presence availablePresence = new Presence(Presence.Type.available, Res.getString("status.online"), 1, Presence.Mode.available);
        final Presence freeToChatPresence = new Presence(Presence.Type.available, Res.getString("status.free.to.chat"), 1, Presence.Mode.chat);
        final Presence awayPresence = new Presence(Presence.Type.available, Res.getString("status.away"), 0, Presence.Mode.away);
        final Presence phonePresence = new Presence(Presence.Type.available, Res.getString("status.on.phone"), 0, Presence.Mode.away);
        final Presence dndPresence = new Presence(Presence.Type.available, Res.getString("status.do.not.disturb"), 0, Presence.Mode.dnd);
        final Presence extendedAway = new Presence(Presence.Type.available, Res.getString("status.extended.away"), 0, Presence.Mode.xa);

        PRESENCES.add(freeToChatPresence);
        PRESENCES.add(availablePresence);
        PRESENCES.add(awayPresence);
        PRESENCES.add(extendedAway);
        PRESENCES.add(phonePresence);
        PRESENCES.add(dndPresence);
    }

    /**
     * Building Presence related data.
     */
    private PresenceManager() {

    }

    /**
     * Returns true if the user is online.
     *
     * @param jid the jid of the user.
     * @return true if online.
     */
    public static boolean isOnline(String jid) {
        final Roster roster = SparkManager.getConnection().getRoster();
        Presence presence = roster.getPresence(jid);
        return presence.isAvailable();
    }

    /**
     * Returns true if the user is online and their status is available or free to chat.
     *
     * @param jid the jid of the user.
     * @return true if the user is online and available.
     */
    public static boolean isAvailable(String jid) {
        final Roster roster = SparkManager.getConnection().getRoster();
        Presence presence = roster.getPresence(jid);
        return presence.isAvailable() && !presence.isAway();
    }

    /**
     * Returns true if the user is online and their mode is available or free to chat.
     *
     * @param presence the users presence.
     * @return true if the user is online and their mode is available or free to chat.
     */
    public static boolean isAvailable(Presence presence) {
        return presence.isAvailable() && !presence.isAway();
    }

    /**
     * Returns the presence of a user.
     *
     * @param jid the users jid.
     * @return the users presence.
     */
    public static Presence getPresence(String jid) {
        final Roster roster = SparkManager.getConnection().getRoster();
        return roster.getPresence(jid);
    }

    /**
     * Returns the fully qualified jid of a user.
     *
     * @param jid the users bare jid (ex. derek@jivesoftware.com)
     * @return the fully qualified jid of a user (ex. derek@jivesoftware.com --> derek@jivesoftware.com/spark)
     */
    public static String getFullyQualifiedJID(String jid) {
        final Roster roster = SparkManager.getConnection().getRoster();
        Presence presence = roster.getPresence(jid);
        return presence.getFrom();
    }

    /**
     * Returns the icon associated with a users presence.
     *
     * @param presence the users presence.
     * @return the icon associated with it.
     */
    public static Icon getIconFromPresence(Presence presence) {
        // Handle offline presence
        if (!presence.isAvailable()) {
            return SparkRes.getImageIcon(SparkRes.CLEAR_BALL_ICON);
        }

        Presence.Mode presenceMode = presence.getMode();
        if (presenceMode == null) {
            presenceMode = Presence.Mode.available;
        }

        Icon icon = null;

        if (presenceMode.equals(Presence.Mode.available)) {
            icon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
        }
        else if (presenceMode.equals(Presence.Mode.chat)) {
            icon = SparkRes.getImageIcon(SparkRes.FREE_TO_CHAT_IMAGE);
        }
        else if (presence.getStatus() != null && presence.getStatus().toLowerCase().contains("phone")) {
            icon = SparkRes.getImageIcon(SparkRes.ON_PHONE_IMAGE);
        }
        else if (presenceMode.equals(Presence.Mode.away)) {
            icon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
        }
        else if (presenceMode.equals(Presence.Mode.dnd)) {
            icon = SparkRes.getImageIcon(SparkRes.IM_DND);
        }
        else if (presenceMode.equals(Presence.Mode.xa)) {
            icon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
        }

        // Check For ContactItem handlers
        Icon handlerIcon = SparkManager.getChatManager().getTabIconForContactHandler(presence);
        if (handlerIcon != null) {
            icon = handlerIcon;
        }


        return icon;
    }

    /**
     * Returns the Presence Map.
     *
     * @return the Presence Map.
     */
    public static List<Presence> getPresences() {
        return PRESENCES;
    }
}
