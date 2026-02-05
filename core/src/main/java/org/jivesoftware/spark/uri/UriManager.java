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
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import static org.jivesoftware.spark.util.StringUtils.unescapeFromXML;

/**
 * Class to handle URI-Mappings defined by <br>
 * <a href="http://xmpp.org/extensions/xep-0147.html">XEP-0147: XMPP URI Scheme Query Components</a>
 *
 * @author wolf.posdorfer
 */
public class UriManager {

    public enum uritypes {
        message,
        join,
        unsubscribe,
        subscribe,
        roster,
        remove;
    }

    /**
     * Handles XMPP URI Mappings.
     * E.g.: xmpp:open_chat@conference.igniterealtime.org?join;password=somesecret
     * See <a href="https://xmpp.org/registrar/querytypes.html">XMPP URI/IRI Querytypes</a>
     *
     * @param xmppUri the XMPP URI passed into Spark or received in a chat message.
     * @param isCommand the XMPP URI passed into Spark or received in a chat message.
     */
    public void handleURIMapping(String xmppUri, boolean isCommand) {
        if (xmppUri == null) {
            return;
        }
        if (!xmppUri.startsWith("xmpp")) {
            return;
        }

        Log.debug("Handling URI mapping for: " + xmppUri);
        URI uri;
        try {
            uri = parseXmppUri(xmppUri);
        } catch (URISyntaxException e) {
            Log.error("error parsing uri: " + xmppUri, e);
            return;
        }

        String query = uri.getQuery();
        if (query == null) {
            // No query string, so assume the URI is xmpp:JID
            Jid jid = retrieveJID(uri);
            if (jid == null) {
                return;
            }
            EntityBareJid bareJid = jid.asEntityBareJidOrThrow();
            UserManager userManager = SparkManager.getUserManager();
            String nickname = userManager.getUserNicknameFromJID(bareJid);
            ChatManager chatManager = SparkManager.getChatManager();
            ChatRoom chatRoom = chatManager.createChatRoom(bareJid, nickname, nickname);
            chatManager.getChatContainer().activateChatRoom(chatRoom);
        } else {
            // extract the command from the query string i.e. "join" from "?join;password=somesecret"
            String command = query;
            int cmdEndPos = query.indexOf(';');
            if (cmdEndPos > 0) {
                command = query.substring(0, cmdEndPos);
            }
            UriManager.uritypes commandUriType;
            try {
                commandUriType = UriManager.uritypes.valueOf(command.toLowerCase());
            } catch (IllegalArgumentException e) {
                Log.error("Unknown XMPP URI command " + xmppUri);
                return;
            }
            // For URI received in a chat it's allowed only the ?join command
            if (!isCommand && commandUriType != uritypes.join) {
                return;
            }

            // Route URI to handler based on command
            switch (commandUriType) {
                case message:
                    try {
                        handleMessage(uri);
                    } catch (Exception e) {
                        Log.error("error with ?message URI", e);
                    }
                    break;
                case join:
                    try {
                        handleConference(uri);
                    } catch (Exception e) {
                        Log.error("error with ?join URI", e);
                    }
                    break;
                case subscribe:
                    try {
                        handleSubscribe(uri);
                    } catch (Exception e) {
                        Log.error("error with ?subscribe URI", e);
                    }
                    break;
                case unsubscribe:
                    try {
                        handleUnsubscribe(uri);
                    } catch (Exception e) {
                        Log.error("error with ?unsubscribe URI", e);
                    }
                    break;
                case roster:
                    try {
                        handleRoster(uri);
                    } catch (Exception e) {
                        Log.error("error with ?roster URI", e);
                    }
                    break;
                case remove:
                    try {
                        handleRemove(uri);
                    } catch (Exception e) {
                        Log.error("error with ?remove URI", e);
                    }
                    break;
            }
        }
    }

    URI parseXmppUri(String xmppUri) throws URISyntaxException {
        URI uri;
        /*
        Java's URI class distinguishes between two types of URIs:
        * Hierarchical URIs: Like http://example.com/path?query. These have a / after the scheme and colon.
        * Opaque URIs: Like mailto:user@example.com or xmpp:user@domain. These do not have a / immediately following the scheme.
        For opaque URIs, the URI class does not automatically parse the string into components like host, path, or query.
        Instead, it treats everything after the colon as the Scheme Specific Part.
        To reuse Java's built-in query parsing, we'll use the "Fake Hierarchical" Trick:
        We temporarily transform the xmpp: URI into a hierarchical one (i.e. http:) just for parsing.
        */
        uri = new URI(xmppUri.replaceFirst("xmpp:", "http://"));
        return uri;
    }

    /**
     * handles the ?message URI
     *
     * @param uri
     *            the decoded uri
     */
    public void handleMessage(URI uri) {
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        // Find body
        String body = retrieveParam(uri, "body");
        body = unescapeFromXML(body);

        UserManager userManager = SparkManager.getUserManager();
        String nickname = userManager.getUserNicknameFromJID(jid.asBareJid());
        ChatManager chatManager = SparkManager.getChatManager();
        ChatRoom chatRoom = chatManager.createChatRoom(jid.asEntityJidOrThrow(), nickname, nickname);
        if (body != null && !body.isEmpty()) {
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
        if (jid == null) {
            return;
        }
        String password = retrieveParam(uri, "password");
        ConferenceUtils.joinConferenceOnSeparateThread(jid, jid.asEntityBareJidOrThrow(), null, password);
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
        if (jid == null) {
            return;
        }
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
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
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
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        BareJid bareJid = jid.asBareJid();

        String name = retrieveParam(uri, "name");
        String group = retrieveParam(uri, "group");

        Roster roster = SparkManager.getRoster();
        RosterEntry userEntry = roster.getEntry(bareJid);

        roster.preApproveAndCreateEntry(bareJid, name, new String[]{group});

        RosterGroup rosterGroup = roster.getGroup(group);
        if (rosterGroup == null) {
            rosterGroup = roster.createGroup(group);
        }

        if (userEntry == null) {
            roster.preApproveAndCreateEntry(bareJid, name, new String[]{group});
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
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        BareJid bareJid = jid.asBareJid();

        Roster roster = SparkManager.getRoster();
        RosterEntry entry = roster.getEntry(bareJid);
        roster.removeEntry(entry);
    }

    /**
     * Gets JID from URI. Returns the full jid including resource romeo@montague.net/balcony
     */
    public Jid retrieveJID(URI uri) {
        if (uri.getHost() == null || uri.getUserInfo() == null) {
            return null;
        }
        String jidString = "";
        if (uri.getUserInfo() != null) {
            jidString += uri.getUserInfo();
            jidString += '@';
        }
        jidString += uri.getHost();
        // Resource contains the leading /
        String resource = uri.getPath();
        if (resource != null && !resource.isEmpty() && !resource.equals("/")) {
            jidString += resource;
        }
        return JidCreate.fromOrNull(jidString);
    }

    /**
     * Extracts password from URI if present e.g., xmpp:open_chat@conference.igniterealtime.org?join;password=somesecret
     */
    public String retrieveParam(URI uri, String param) {
        String query = uri.getRawSchemeSpecificPart();
        int index = query.indexOf(";" + param + "=");
        if (index == -1) {
            return null;
        }
        int paramStart = index + (";" + param + "=").length();
        int paramEndPos = query.indexOf('&', paramStart);
        if (paramEndPos == -1) {
            paramEndPos = query.indexOf(';', paramStart);
            if (paramEndPos == -1) {
                paramEndPos = query.length();
            }
        }
        String result = query.substring(paramStart, paramEndPos);
        if (result.isEmpty()) {
            return null;
        }
        return URLDecoder.decode(result, StandardCharsets.UTF_8);
    }
}
