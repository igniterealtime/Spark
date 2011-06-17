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
package org.jivesoftware.spark.uri;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;

/**
 * Class to handle URI-Mappings defined by <br>
 * 
 * <a href="http://xmpp.org/extensions/xep-0147.html">XEP-0147: XMPP URI Scheme
 * Query Components</a>
 * 
 * @author wolf.posdorfer
 * 
 */
public class UriManager {

    public enum uritypes {
	message("?message"),
	join("?join"), 
	unsubscribe("?unsubscribe"),
	subscribe("?subscribe"),
	roster("?roster"),
	remove("?remove");

	private String _xml;

	private uritypes(String s) {
	    _xml = s;
	}
	public String getXML() {
	    return _xml;
	}
    }

    /**
     * handles the ?message URI
     * 
     * @param uriMapping
     *            the uri mapping string.
     */
    public void handleMessage(String uriMapping) {

	int bodyIndex = uriMapping.indexOf("body=");

	String jid = retrieveJID(uriMapping, uritypes.message);//uriMapping.substring(index + 5, messageIndex);
	String body = null;

	// Find body
	if (bodyIndex != -1) {
	    body = uriMapping.substring(bodyIndex + 5);
	}

	body = org.jivesoftware.spark.util.StringUtils.unescapeFromXML(body);
	body = org.jivesoftware.spark.util.StringUtils
		.replace(body, "%20", " ");

	UserManager userManager = SparkManager.getUserManager();
	String nickname = userManager.getUserNicknameFromJID(jid);
	if (nickname == null) {
	    nickname = jid;
	}

	ChatManager chatManager = SparkManager.getChatManager();
	ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname, nickname);
	if (body != null) {
	    Message message = new Message();
	    message.setBody(body);
	    chatRoom.sendMessage(message);
	}

	chatManager.getChatContainer().activateChatRoom(chatRoom);
    }

    /**
     * Handles the ?join URI
     * 
     * @param uriMapping
     *            the uri mapping.
     * @throws Exception
     *             thrown if the conference cannot be joined.
     */
    public void handleConference(String uriMapping) throws Exception {
	String jid = retrieveJID(uriMapping, uritypes.join);

	ConferenceUtils.joinConferenceOnSeperateThread(jid, jid,null);
    }

    /**
     * Handles the ?subscribe URI
     * @param uriMapping
     * @throws Exception
     */
    public void handleSubscribe(String uriMapping) throws Exception {
	// xmpp:romeo@montague.net?subscribe
	// Send contact add request
	String jid = retrieveJID(uriMapping, uritypes.subscribe);

	Presence response = new Presence(Presence.Type.subscribe);
	response.setTo(jid);
	SparkManager.getConnection().sendPacket(response);

    }
    
    /**
     * Handles the ?unsubscribe URI
     * @param arguments
     */
    public void handleUnsubscribe(String arguments) {
	String jid = retrieveJID(arguments, uritypes.unsubscribe);

	Presence response = new Presence(Presence.Type.unsubscribe);
	response.setTo(jid);
	SparkManager.getConnection().sendPacket(response);
    }

    /***
     * Handles the ?roster URI<br>
     * with name= and group=
     * 
     * @param arguments
     * @throws Exception
     */
    public void handleRoster(String arguments) throws Exception {
	// xmpp:romeo@montague.net?roster
	// xmpp:romeo@montague.net?roster;name=Romeo%20Montague
	// xmpp:romeo@montague.net?roster;group=Friends
	// xmpp:romeo@montague.net?roster;name=Romeo%20Montague;group=Friends
	String jid = retrieveJID(arguments, uritypes.roster);

	String name = "";

	if (arguments.contains("name=")) {
	    StringBuffer buf = new StringBuffer();
	    int x = arguments.indexOf("name=") + 5;
	    while (x < arguments.length() && arguments.charAt(x) != ';') {
		buf.append(arguments.charAt(x));
		x++;
	    }
	    name = buf.toString().replace("%20", " ");
	}
	String group = "";
	if (arguments.contains("group=")) {
	    StringBuffer buf = new StringBuffer();
	    int x = arguments.indexOf("group=") + 6;
	    while (x < arguments.length() && arguments.charAt(x) != ';') {
		buf.append(arguments.charAt(x));
		x++;
	    }
	    group = buf.toString().replace("%20", " ");
	}

	Roster roster = SparkManager.getConnection().getRoster();
	RosterEntry userEntry = roster.getEntry(jid);

	roster.createEntry(jid, name, new String[] { group });

	RosterGroup rosterGroup = roster.getGroup(group);
	if (rosterGroup == null) {
	    rosterGroup = roster.createGroup(group);
	}

	if (userEntry == null) {
	    roster.createEntry(jid, name, new String[] { group });
	    userEntry = roster.getEntry(jid);
	} else {
	    userEntry.setName(name);
	    rosterGroup.addEntry(userEntry);
	}

	userEntry = roster.getEntry(jid);

    }

    /**
     * Handles the ?remove URI
     * @param arguments
     * @throws Exception
     */
    public void handleRemove(String arguments) throws Exception {
	//xmpp:romeo@montague.net?remove
	
	String jid = retrieveJID(arguments, uritypes.remove);
	Roster roster = SparkManager.getConnection().getRoster();
	RosterEntry entry = roster.getEntry(jid);
	roster.removeEntry(entry);

    }
    /**
     * Parses Jid from xmpp:romeo@montague.net?xyxyx
     * @param stanza, the complete stanza
     * @param mapping, the uri type
     * @return romeo@montague.net
     */
    private String retrieveJID(String stanza, uritypes mapping)
    {
	int xmpp = stanza.indexOf("xmpp:") + 5;
	int cmd = stanza.indexOf(mapping.getXML());
	return stanza.substring(xmpp, cmd);
    }
    
}
