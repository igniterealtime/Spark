/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;

/**
 * Handles the most common presence checks.
 *
 * @author Derek DeMoro
 */
public class PresenceManager {

    /**
     * Empty private constructor.
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
}
