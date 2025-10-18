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
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.packet.XmlElement;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

import javax.swing.Icon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        final Presence availablePresence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.available)
            .setStatus(Res.getString("status.online"))
            .setPriority(1)
            .setMode(Presence.Mode.available)
            .build();

        final Presence freeToChatPresence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.available)
            .setStatus(Res.getString("status.free.to.chat"))
            .setPriority(1)
            .setMode(Presence.Mode.chat)
            .build();

        final Presence awayPresence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.available)
            .setStatus(Res.getString("status.away"))
            .setPriority(0)
            .setMode(Presence.Mode.away)
            .build();

        final Presence phonePresence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.available)
            .setStatus(Res.getString("status.on.phone"))
            .setPriority(0)
            .setMode(Presence.Mode.away)
            .build();

        final Presence dndPresence = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.available)
            .setStatus(Res.getString("status.do.not.disturb"))
            .setPriority(0)
            .setMode(Presence.Mode.dnd)
            .build();

        final Presence extendedAway = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.available)
            .setStatus(Res.getString("status.extended.away"))
            .setPriority(0)
            .setMode(Presence.Mode.xa)
            .build();

        final Presence invisible = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unavailable)
            .setStatus(Res.getString("status.invisible"))
            .setPriority(0)
            .setMode(Presence.Mode.available)
            .build();

        PRESENCES.add(freeToChatPresence);
        PRESENCES.add(availablePresence);
        PRESENCES.add(awayPresence);
        PRESENCES.add(phonePresence);
        PRESENCES.add(extendedAway);
        PRESENCES.add(dndPresence);

        if (!Default.getBoolean(Default.HIDE_LOGIN_AS_INVISIBLE) && Enterprise.containsFeature(Enterprise.INVISIBLE_LOGIN_FEATURE)) PRESENCES.add(invisible);
    }

    /**
     * Building Presence related data.
     */
    private PresenceManager() {

    }

    /**
     * Returns true if the user is online.
     *
     * @param jid the JID of the user.
     * @return true if online.
     */
    public static boolean isOnline(BareJid jid) {
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
    public static boolean isAvailable(BareJid jid) {
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
     * @param jid the users JID.
     * @return the users presence.
     */
    public static Presence getPresence(BareJid jid) {
        if (jid.equals(SparkManager.getSessionManager().getUserBareAddress())) {
            return SparkManager.getWorkspace().getStatusBar().getPresence();
        } else {
            final Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
            return roster.getPresence(jid);
        }
    }

    /**
     * Returns the fully qualified jid of a user. May return {@code null}.
     *
     * @param jid the users bare JID (ex. derek@jivesoftware.com)
     * @return the fully qualified JID of a user (ex. derek@jivesoftware.com --> derek@jivesoftware.com/spark) or {@code null}.
     */
    public static EntityFullJid getFullyQualifiedJID(BareJid jid) {
        final Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
        Presence presence = roster.getPresence(jid);
        Jid result = presence.getFrom();
        return result.asEntityFullJidIfPossible();
    }

	public static String getJidFromMUCPresence(Presence presence) {		
		Collection<XmlElement> extensions = presence.getExtensions();
		for (XmlElement extension : extensions) {
			if (extension instanceof MUCUser) {
				final MUCUser mucUser = (MUCUser) extension;
				Jid fullJid = mucUser.getItem().getJid();
                if ( fullJid == null) {
                    return null;
                }
				return fullJid.asBareJid().toString();
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
            icon = SparkRes.getImageIcon(SparkRes.IM_XA);
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
        return presence.getStatus() != null &&
            presence.getStatus().contains(Res.getString("status.on.phone")) &&
            presenceMode.equals(Presence.Mode.away);
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

}
