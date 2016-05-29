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

import java.net.URI;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
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
	message("message"),
	join("join"),
	unsubscribe("unsubscribe"),
	subscribe("subscribe"),
	roster("roster"),
	remove("remove");

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
     * @param uri
     *            the decoded uri
     */
    public void handleMessage(URI uri) {

	String query = uri.getQuery();
	int bodyIndex = query.indexOf("body=");

	String jid = retrieveJID(uri);
	String body = null;

	// Find body
	if (bodyIndex != -1) {
	    body = query.substring(bodyIndex + 5);
	}

	body = org.jivesoftware.spark.util.StringUtils.unescapeFromXML(body);

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
     * @param uri
     *            the decoded uri
     * @throws Exception
     *             thrown if the conference cannot be joined.
     */
    public void handleConference(URI uri) throws Exception {
	String jid = retrieveJID(uri);
	ConferenceUtils.joinConferenceOnSeperateThread(jid, jid, null);
    }

    /**
     * Handles the ?subscribe URI
     * 
     * @param uri
     *            the decoded uri
     * @throws Exception
     */
    public void handleSubscribe(URI uri) throws Exception {
	// xmpp:romeo@montague.net?subscribe
	// Send contact add request
	String jid = retrieveJID(uri);

	Presence response = new Presence(Presence.Type.subscribe);
	response.setTo(jid);
	SparkManager.getConnection().sendStanza(response);
    }

    /**
     * Handles the ?unsubscribe URI
     * 
     * @param uri
     *            the decoded uri
     */
    public void handleUnsubscribe(URI uri) throws SmackException.NotConnectedException
	{
	String jid = retrieveJID(uri);

	Presence response = new Presence(Presence.Type.unsubscribe);
	response.setTo(jid);
	SparkManager.getConnection().sendStanza(response);
    }

    /***
     * Handles the ?roster URI<br>
     * with name= and group=
     * 
     * @param uri
     *            the decoded uri
     * @throws Exception
     */
    public void handleRoster(URI uri) throws Exception {
	// xmpp:romeo@montague.net?roster
	// xmpp:romeo@montague.net?roster;name=Romeo%20Montague
	// xmpp:romeo@montague.net?roster;group=Friends
	// xmpp:romeo@montague.net?roster;name=Romeo%20Montague;group=Friends
	String jid = retrieveJID(uri);

	String name = "";
	String query = uri.getQuery();
	if (query.contains("name=")) {
	    StringBuilder buf = new StringBuilder();
	    int x = query.indexOf("name=") + 5;
	    while (x < query.length() && query.charAt(x) != ';') {
		buf.append(query.charAt(x));
		x++;
	    }
	}
	String group = "";
	if (query.contains("group=")) {
	    StringBuilder buf = new StringBuilder();
	    int x = query.indexOf("group=") + 6;
	    while (x < query.length() && query.charAt(x) != ';') {
		buf.append(query.charAt(x));
		x++;
	    }
	}

	Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
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
     * 
     * @param uri
     *            the decoded uri
     * @throws Exception
     */
    public void handleRemove(URI uri) throws Exception {
	// xmpp:romeo@montague.net?remove

	String jid = retrieveJID(uri);
	Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
	RosterEntry entry = roster.getEntry(jid);
	roster.removeEntry(entry);
    }

    /**
     * Gets JID from URI. Returns the full jid including resource
     * 
     * @param uri
     *            the URI
     * @return romeo@montague.net
     */
    public String retrieveJID(URI uri) {
	StringBuilder sb = new StringBuilder(32);
	String user = uri.getUserInfo();
	if (user != null) {
	    sb.append(user);
	    sb.append('@');
	}
	sb.append(uri.getHost());
	// Resource contains the leading /
	String resource = uri.getPath();
	if (resource != null && resource.length() > 0 && !resource.equals("/")) {
	    sb.append(resource);
	}
	return sb.toString();
    }
}
