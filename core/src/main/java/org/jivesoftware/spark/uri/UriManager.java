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
package org.jivesoftware.spark.uri;

import java.net.URI;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import static org.jivesoftware.spark.util.StringUtils.unescapeFromXML;

/**
 * Class to handle URI-Mappings defined by <br>
 * <a href="http://xmpp.org/extensions/xep-0147.html">XEP-0147: XMPP URI Scheme Query Components</a>
 *
 * @author wolf.posdorfer
 */
public class UriManager {

    public enum uritypes {
        message("message"),
        join("join"),
        unsubscribe("unsubscribe"),
        subscribe("subscribe"),
        roster("roster"),
        remove("remove");

        private final String _xml;

        uritypes(String s) {
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
        Jid jid = retrieveJID(uri);
        String body = null;
        // Find body
        if (bodyIndex != -1) {
            body = query.substring(bodyIndex + 5);
        }
        body = unescapeFromXML(body);

        UserManager userManager = SparkManager.getUserManager();
        String nickname = userManager.getUserNicknameFromJID(jid.asBareJid());
        ChatManager chatManager = SparkManager.getChatManager();
        ChatRoom chatRoom = chatManager.createChatRoom(jid.asEntityJidOrThrow(), nickname, nickname);
        if (body != null) {
            MessageBuilder messageBuilder = StanzaBuilder.buildMessage()
                .setBody(body);
            chatRoom.sendMessage(messageBuilder);
        }

        chatManager.getChatContainer().activateChatRoom(chatRoom);
    }

    /**
     * Handles the ?join URI
     *
     * @param uri
     *            the decoded uri
     */
    public void handleConference(URI uri) {
        Jid jid = retrieveJID(uri);
        String password = retrievePassword(uri);
        ConferenceUtils.joinConferenceOnSeperateThread(jid, jid.asEntityBareJidOrThrow(), null, password);
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
        Jid jid = retrieveJID(uri);
        XMPPConnection connection = SparkManager.getConnection();
        Presence response = connection.getStanzaFactory()
            .buildPresenceStanza()
            .ofType(Presence.Type.subscribe)
            .to(jid)
            .build();
        connection.sendStanza(response);
    }

    /**
     * Handles the ?unsubscribe URI
     *
     * @param uri
     *            the decoded uri
     */
    public void handleUnsubscribe(URI uri) throws SmackException.NotConnectedException {
        Jid jid;
        try {
            jid = JidCreate.from(retrieveJID(uri));
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }

        Presence response = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unsubscribe)
            .build();
        response.setTo(jid);
        try {
            SparkManager.getConnection().sendStanza(response);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
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
        BareJid jid;
        try {
            jid = JidCreate.bareFrom(retrieveJID(uri));
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }

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

        Roster roster = Roster.getInstanceFor(SparkManager.getConnection());
        RosterEntry userEntry = roster.getEntry(jid);

        roster.preApproveAndCreateEntry(jid, name, new String[]{group});

        RosterGroup rosterGroup = roster.getGroup(group);
        if (rosterGroup == null) {
            rosterGroup = roster.createGroup(group);
        }

        if (userEntry == null) {
            roster.preApproveAndCreateEntry(jid, name, new String[]{group});
        } else {
            userEntry.setName(name);
            rosterGroup.addEntry(userEntry);
        }
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
        BareJid jid;
        try {
            jid = JidCreate.bareFrom(retrieveJID(uri));
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }

        Roster roster = Roster.getInstanceFor(SparkManager.getConnection());
        RosterEntry entry = roster.getEntry(jid);
        roster.removeEntry(entry);
    }

    /**
     * Gets JID from URI. Returns the full jid including resource romeo@montague.net/balcony
     */
    public Jid retrieveJID(URI uri) {
        String jidString = "";
        String user = uri.getUserInfo();
        if (user != null) {
            jidString += user;
            jidString += '@';
        }
        jidString += uri.getHost();
        // Resource contains the leading /
        String resource = uri.getPath();
        if (resource != null && !resource.isEmpty() && !resource.equals("/")) {
            jidString += resource;
        }
        return JidCreate.fromOrThrowUnchecked(jidString);
    }

    /**
     * Extracts password from URI if present
     */
    public static String retrievePassword(URI uri) {
        int index = uri.toString().indexOf("password=");
        if (index == -1) {
            return null;
        }
        String result = uri.toString().substring(index + "password=".length());
        if (result.indexOf('&') != -1) {
            result = result.substring(0, result.indexOf('&'));
        }
        if (result.indexOf(';') != -1) {
            result = result.substring(0, result.indexOf(';'));
        }
        return !result.isEmpty() ? result : null;
    }
}
