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
package org.jivesoftware.spark;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.Icon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.jivesoftware.smack.util.StringUtils.isEmpty;
import static org.jivesoftware.smack.util.StringUtils.isNullOrEmpty;

/**
 * Handles the most common presence checks.
 *
 * @author Derek DeMoro
 */
public class PresenceManager {

    private static final List<Presence> PRESENCES = new ArrayList<>();


    static {
        // Add Available Presence
        final Presence availablePresence = new Presence(Presence.Type.available, Res.getString("status.online"), 1, Presence.Mode.available);
        final Presence freeToChatPresence = new Presence(Presence.Type.available, Res.getString("status.free.to.chat"), 1, Presence.Mode.chat);
        final Presence awayPresence = new Presence(Presence.Type.available, Res.getString("status.away"), 0, Presence.Mode.away);
        final Presence phonePresence = new Presence(Presence.Type.available, Res.getString("status.on.phone"), 0, Presence.Mode.away);
        final Presence dndPresence = new Presence(Presence.Type.available, Res.getString("status.do.not.disturb"), 0, Presence.Mode.dnd);
        final Presence extendedAway = new Presence(Presence.Type.available, Res.getString("status.extended.away"), 0, Presence.Mode.xa);
	final Presence invisible = new Presence(Presence.Type.unavailable, Res.getString("status.invisible"), 0, Presence.Mode.available);

        PRESENCES.add(freeToChatPresence);
        PRESENCES.add(availablePresence);
        PRESENCES.add(awayPresence);
        PRESENCES.add(extendedAway);
        PRESENCES.add(phonePresence);
        PRESENCES.add(dndPresence);
	PRESENCES.add(invisible);
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
        final Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
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
        final Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
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
        if ( jid == null ) {
            Log.error( "Unable to get the presence of a null jid!" );
            return null;
        }
		if (jid.equals(SparkManager.getSessionManager().getBareAddress())) {
			return SparkManager.getWorkspace().getStatusBar().getPresence();
		} else {
			final Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
			return roster.getPresence(jid);
		}
    }

    /**
     * Returns the fully qualified jid of a user.
     *
     * @param jid the users bare jid (ex. derek@jivesoftware.com)
     * @return the fully qualified jid of a user (ex. derek@jivesoftware.com --> derek@jivesoftware.com/spark)
     */
    public static String getFullyQualifiedJID(String jid) {
        final Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
        Presence presence = roster.getPresence(jid);
        return presence.getFrom();
    }
    
	public static String getJidFromMUCPresence(Presence presence) {		
		Collection<ExtensionElement> extensions = presence.getExtensions();
		for (ExtensionElement extension : extensions) {
			if (extension instanceof MUCUser) {
				final MUCUser mucUser = (MUCUser) extension;
				String fullJid = mucUser.getItem().getJid();
                if ( fullJid == null) {
                    return null;
                }
 				String userJid = XmppStringUtils.parseBareJid(fullJid);
				return userJid;
			}
		}
		return null;
	}    

    /**
     * Returns the icon associated with a users presence.
     *
     * @param presence the users presence.
     * @return the icon associated with it.
     */
    public static Icon getIconFromPresence(Presence presence) {
	if (isInvisible(presence)) {
            return SparkRes.getImageIcon(SparkRes.CLEAR_BALL_ICON);
        }

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
        else if (isOnPhone(presence)) {
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
    
    public static boolean isOnPhone(Presence presence) {
    	Presence.Mode presenceMode = presence.getMode();
    	 if (presenceMode == null) {
        	 presenceMode = Presence.Mode.available;
        }
    	if (presence.getStatus() != null && 
    		presence.getStatus().contains(Res.getString("status.on.phone")) && 
    		presenceMode.equals(Presence.Mode.away)) {
    		return true;
    	}
    	return false;
    }

    public static boolean isInvisible(Presence presence) {
        return presence != null && presence.getType() == Presence.Type.unavailable 
                && (Res.getString("status.invisible").equalsIgnoreCase(presence.getStatus())
                		|| isNullOrEmpty(presence.getStatus()))
                && Presence.Mode.available == presence.getMode();
    }

    public static Presence getAvailablePresence() {
        return PRESENCES.get(1);
    }

    public static Presence getUnavailablePresence() {
        return PRESENCES.get(6);
    }

    public static boolean areEqual(Presence p1, Presence p2) {
        if (p1 == p2)
           return true;
        
        if (p1 == null || p2 == null)
            return false;
        
       return p1.getType() == p2.getType() && p1.getMode() == p2.getMode()
               && p1.getStatus().equals(p2.getStatus());
    }

    public static Presence copy(Presence presence) {
	if (presence == null)
		return null;
	Presence copy = new Presence(presence.getType());
	copy.setMode(presence.getMode());
	copy.setStatus(presence.getStatus());
	copy.setPriority(presence.getPriority());
	return copy;
    }
}
