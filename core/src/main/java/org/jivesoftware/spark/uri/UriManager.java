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

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.AccountCreationWizard;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smackx.commands.AdHocCommand;
import org.jivesoftware.smackx.commands.AdHocCommandManager;
import org.jivesoftware.smackx.commands.AdHocCommandNote;
import org.jivesoftware.smackx.commands.AdHocCommandResult;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.form.SubmitForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Class to handle URI-Mappings defined by
 * <a href="http://xmpp.org/extensions/xep-0147.html">XEP-0147: XMPP URI Scheme Query Components</a>
 *
 * @author Wolf Posdorfer
 */
public class UriManager {

    public enum uritypes {
        command,
        disco,
        invite,
        join,
        message,
        pubsub,
        recvfile,
        register,
        remove,
        roster,
        sendfile,
        subscribe,
        unregister,
        unsubscribe,
        vcard
    }

    private final LocalPreferences localPref = SettingsManager.getLocalPreferences();

    /**
     * Handles XMPP URI Mappings like <code>xmpp:chamber@shakespeare.lit?join;password=somesecret</code>.
     * See <a href="https://xmpp.org/registrar/querytypes.html">XMPP URI/IRI Query types</a>
     *
     * @param xmppUri the XMPP URI passed into Spark or received in a chat message.
     * @param isCommand the XMPP URI passed into Spark or received in a chat message.
     */
    public void handleURIMapping(String xmppUri, boolean isCommand) {
        if (xmppUri == null) {
            return;
        }
        if (!xmppUri.startsWith("xmpp:")) {
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
            openChat(jid);
        } else {
            // extract the command from the query string i.e. "join" from "?join;password=somesecret"
            String command = query;
            int cmdEndPos = query.indexOf(';');
            if (cmdEndPos > 0) {
                command = query.substring(0, cmdEndPos);
            }
            uritypes commandUriType;
            try {
                commandUriType = uritypes.valueOf(command.toLowerCase());
            } catch (IllegalArgumentException e) {
                Log.error("Unknown XMPP URI command " + xmppUri);
                return;
            }
            // For a URI received in a chat it is allowed only the ?join command
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
                case invite:
                    try {
                        handleConferenceInvite(uri);
                    } catch (Exception e) {
                        Log.error("error with ?invite URI", e);
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
                case register:
                    try {
                        handleRegister(uri);
                    } catch (Exception e) {
                        Log.error("error with ?register URI", e);
                    }
                    break;
                case vcard:
                    try {
                        handleVcard(uri);
                    } catch (Exception e) {
                        Log.error("error with ?vcard URI", e);
                    }
                    break;
                case command:
                    try {
                        handleCommand(uri);
                    } catch (Exception e) {
                        Log.error("error with ?command URI", e);
                    }
                    break;
                default:
                    Log.error("Unsupported XMPP URI command " + xmppUri);
                    break;
            }
        }
    }

    private static ChatRoom openChat(Jid jid) {
        EntityBareJid bareJid = jid.asEntityBareJidOrThrow();
        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(bareJid);
        ChatManager chatManager = SparkManager.getChatManager();
        ChatRoom chatRoom = chatManager.createChatRoom(bareJid, nickname, nickname);
        chatManager.getChatContainer().activateChatRoom(chatRoom);
        return chatRoom;
    }

    URI parseXmppUri(String xmppUri) throws URISyntaxException {
        URI uri;
        /*
        Java's URI class distinguishes between two types of URIs:
        * Hierarchical URIs: Like http://example.com/path?query. These have a / after the scheme and colon.
        * Opaque URIs: Like mailto:user@example.com or xmpp:user@domain. These do not have a / immediately following the scheme.
        For opaque URIs, the URI class does not automatically parse the string into components like host, path, or query.
        Instead, it treats everything after the colon as the Scheme-Specific Part.
        To reuse Java's built-in query parsing, we'll use the "Fake Hierarchical" Trick:
        We temporarily transform the "xmpp:" URI into a hierarchical one, i.e. "https:", just for parsing.
        */
        uri = new URI(xmppUri.replaceFirst("xmpp:", "https://"));
        return uri;
    }

    /**
     * Handles the "?message" URI to send a message to a chat room or conference.
     * <pre>
     * xmpp:romeo@montague.lit?message;body=Hello
     * </pre>
     */
    public void handleMessage(URI uri) throws Exception {
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        ChatRoom chatRoom = openChat(jid);
        String body = retrieveParam(uri, "body");
        String stanzaId = retrieveParam(uri, "id");
        String subject = retrieveParam(uri, "subject");
        String thread = retrieveParam(uri, "thread");
        String from = retrieveParam(uri, "from"); //FIXME will be overridden in chatRoom.sendMessage()
        String type = retrieveParam(uri, "type"); //FIXME will be overridden in chatRoom.sendMessage()
        if (body != null && !body.isEmpty()) {
            MessageBuilder messageBuilder = StanzaBuilder.buildMessage(stanzaId)
                .setBody(body);
            if (subject != null) {
                messageBuilder.setSubject(subject);
            }
            if (thread != null) {
                messageBuilder.setThread(thread);
            }
            if (from != null) {
                messageBuilder.from(from);
            }
            if (type != null) {
                Message.Type msgType = Message.Type.valueOf(type);
                messageBuilder.ofType(msgType);
            }
            chatRoom.sendMessage(messageBuilder);
        }
    }

    /**
     * Handles the "?join" URI to open a conference chat.
     * <pre>
     * xmpp:chamber@shakespeare.lit?join;password=secret
     * </pre>
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
     * Handles the "?invite" URI to invite contacts to an existing conference chat.
     * It doesn't follow the standard so may be changed in the future.
     * <pre>
     * xmpp:chamber@shakespeare.lit?invite;password=secret
     * # non standard: invite to multiple users e.g. romeo@montague.lit,juliet@capulet.lit
     * xmpp:chamber@shakespeare.lit?invite;invites=romeo%40montague.lit%2Cjuliet%40capulet.lit
     * </pre>
     */
    public void handleConferenceInvite(URI uri) {
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
//        String password = retrieveParam(uri, "password"); //TODO implement
        String invitesStr = retrieveParam(uri, "invites");

        List<EntityBareJid> invites = null;
        if (invitesStr != null) {
            String[] invitesJids = StringUtils.split(invitesStr, ",");
            invites = new ArrayList<>(invitesJids.length);
            for (String invitesJid : invitesJids) {
                try {
                    invites.add(JidCreate.entityBareFrom(invitesJid.trim()));
                } catch (XmppStringprepException e) {
                    Log.error("Error parsing invite JID: " + invitesJid + ": " + e.getMessage());
                }
            }
        }
        ConferenceUtils.inviteUsersToRoom(jid.asEntityBareJidIfPossible(), invites, false);
    }

    /**
     * Handles the "?subscribe" URI to send contact add request.
     * <pre>
     * xmpp:romeo@montague.lit?subscribe
     * </pre>
     */
    public void handleSubscribe(URI uri) throws Exception {
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        Presence response = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.subscribe)
            .to(jid)
            .build();
        SparkManager.getConnection().sendStanza(response);
    }

    /**
     * Handles the "?unsubscribe" URI to unsubscribe from the contact presence.
     */
    public void handleUnsubscribe(URI uri) throws Exception {
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        Presence response = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unsubscribe)
            .to(jid)
            .build();
        SparkManager.getConnection().sendStanza(response);
    }

    /***
     * Handles the "?roster" URI to add a group to a contact list.
     * <pre>
     * xmpp:romeo@montague.lit?roster
     * xmpp:romeo@montague.lit?roster;name=Romeo%20Montague
     * xmpp:romeo@montague.lit?roster;group=Friends
     * xmpp:romeo@montague.lit?roster;name=Romeo%20Montague;group=Friends
     * </pre>
     */
    public void handleRoster(URI uri) throws Exception {
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        BareJid bareJid = jid.asBareJid();

        String name = retrieveParam(uri, "name");
        String group = retrieveParam(uri, "group");

        Roster roster = SparkManager.getRoster();
        roster.preApproveAndCreateEntry(bareJid, name, new String[]{group});
        RosterGroup rosterGroup = roster.getGroup(group);
        if (rosterGroup == null) {
            rosterGroup = roster.createGroup(group);
        }

        RosterEntry userEntry = roster.getEntry(bareJid);
        if (userEntry == null) {
            roster.preApproveAndCreateEntry(bareJid, name, new String[]{group});
        } else {
            userEntry.setName(name);
            rosterGroup.addEntry(userEntry);
        }
    }

    /**
     * Handles the "?remove" URI to remove the contact from a contact list.
     * <pre>
     * xmpp:romeo@montague.lit?remove
     * </pre>
     */
    public void handleRemove(URI uri) throws Exception {
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
     * Handles the "?register" URI to start a registration process on the specified server.
     * See <a href="https://xmpp.org/extensions/xep-0077.html#registrar-querytypes-register">XEP-0077</a>
     * <pre>
     * xmpp:shakespeare.lit?register
     * </pre>
     */
    private void handleRegister(URI uri) throws Exception {
        DomainBareJid domain = JidCreate.domainBareFromOrNull(uri.getHost()) ;
        if (domain == null) {
            return;
        }
        AccountCreationWizard createAccountPanel = new AccountCreationWizard();
        createAccountPanel.setServer(domain);
        createAccountPanel.invoke(null);
        if (!createAccountPanel.isRegistered()) {
            return;
        }
        final int userChoice = JOptionPane.showConfirmDialog(createAccountPanel,
            Res.getString("message.restart.required"),
            Res.getString("title.alert"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
        if (userChoice == JOptionPane.YES_OPTION) {
            // Persist information
            Localpart registeredUsername = Localpart.fromUnescaped(createAccountPanel.getUsernameWithoutEscape());
            DomainBareJid registeredDomain = JidCreate.domainBareFromOrNull(createAccountPanel.getServer());
            BareJid bareJid = JidCreate.bareFrom(registeredUsername, registeredDomain);
            localPref.setLastUsername(registeredUsername.toString());
            try {
                localPref.setPasswordForUser(bareJid.toString(), createAccountPanel.getPassword());
            } catch (Exception e) {
                Log.error("Error storing encrypted password.", e);
            }
            localPref.setServer(registeredDomain.toString());
            SparkManager.getMainWindow().logout(false);
        }
    }

    /**
     * Handles the "?vcard" URI to show profile info for the JID.
     * <pre>
     * xmpp:romeo@montague.lit?vcard
     * </pre>
     */
    private void handleVcard(URI uri) {
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        SparkManager.getVCardManager().viewProfile(jid.asBareJid(), null);
    }

    /**
     * Handles the "?command" URI to send Ad-Hoc command to the specified server or entity.
     * It doesn't follow the standard so may be changed in the future.
     * <pre>
     * # ping command
     * xmpp:marlowe.shakespeare.lit?command;node=ping
     * # To create a user you need to encodeURIComponent(form)
     * xmpp:montague.lit?command;node=http%3A%2F%2Fjabber.org%2Fprotocol%2Fadmin%23add-user;form=%3Cx%20xmlns%3D%22jabber%3Ax%3Adata%22%20type%3D%22submit%22%3E%0A%3Cfield%20type%3D%22hidden%22%20var%3D%22FORM_TYPE%22%3E%0A%3Cvalue%3Ehttp%3A%2F%2Fjabber.org%2Fprotocol%2Fadmin%3C%2Fvalue%3E%0A%3C%2Ffield%3E%0A%3Cfield%20type%3D%22jid-single%22%20var%3D%22accountjid%22%3E%0A%3Cvalue%3Eromeo%40montague.lit%3C%2Fvalue%3E%0A%3C%2Ffield%3E%0A%3Cfield%20type%3D%22text-private%22%20var%3D%22password%22%3E%0A%3Cvalue%3E1234%3C%2Fvalue%3E%0A%3C%2Ffield%3E%0A%3Cfield%20type%3D%22text-private%22%20var%3D%22password-verify%22%3E%0A%3Cvalue%3E1234%3C%2Fvalue%3E%0A%3C%2Ffield%3E%0A%3C%2Fx%3E
     * # To delete the user you need to encodeURIComponent(form)
     * xmpp:montague.lit?command;node=http%3A%2F%2Fjabber.org%2Fprotocol%2Fadmin%23delete-user;form=%3Cx%20xmlns%3D%22jabber%3Ax%3Adata%22%20type%3D%22submit%22%3E%0A%3Cfield%20type%3D%22hidden%22%20var%3D%22FORM_TYPE%22%3E%0A%3Cvalue%3Ehttp%3A%2F%2Fjabber.org%2Fprotocol%2Fadmin%3C%2Fvalue%3E%0A%3C%2Ffield%3E%0A%3Cfield%20type%3D%22jid-multi%22%20var%3D%22accountjids%22%3E%0A%3Cvalue%3Eromeo%40montague.lit%3C%2Fvalue%3E%0A%3C%2Ffield%3E%0A%3C%2Fx%3E
     * </pre>
     */
    private void handleCommand(URI uri) throws Exception {
        Jid jid = retrieveJID(uri);
        if (jid == null) {
            return;
        }
        AbstractXMPPConnection connection = SparkManager.getConnection();
        AdHocCommandManager adHocCommandManager = AdHocCommandManager.getInstance(connection);
        String node = retrieveParam(uri, "node");
        if (node == null) {
            Log.error("No node specified in URI: " + uri);
            return;
        }
        String action = retrieveParam(uri, "action");
        if (action != null && !action.equals( "execute")) {
            Log.error("Only execute action is supported: " + uri);
            return;
        }
        String formXml = retrieveParam(uri, "form");
        DataForm dataForm = null;
        if (formXml != null) {
            try {
                //FIXME HACK we can't change a form type after deserialization. So replace it manually.
                String submitFormXmlToDataForm = formXml.replace("\"jabber:x:data\" type=\"submit\"", "\"jabber:x:data\" type=\"form\"");
                XmlPullParser parser = PacketParserUtils.getParserFor(submitFormXmlToDataForm);
                dataForm = DataFormProvider.INSTANCE.parse(parser);
            } catch (Exception e) {
                Log.error("Error parsing form: ", e);
                return;
            }
        }
        AdHocCommand remoteCommand = adHocCommandManager.getRemoteCommand(jid, node);
        AdHocCommandResult commandResult = remoteCommand.execute();
        if (dataForm != null) {
            DataForm respForm = commandResult.getResponse().getForm();
            FillableForm fillableForm = fillForm(respForm, dataForm);
            SubmitForm submitForm = fillableForm.getSubmitForm();
            commandResult = remoteCommand.complete(submitForm);
        }
        System.out.println("status: " + remoteCommand.getStatus());
        if (commandResult.getResponse().getNotes() != null) {
            System.out.println("notes:");
            for (AdHocCommandNote note : commandResult.getResponse().getNotes()) {
                System.out.println(note.getType() + " " + note.getValue());
            }
        }
        DataForm respForm = commandResult.getResponse().getForm();
        if (respForm != null) {
            CharSequence respXml = respForm.toXML();
            System.out.println("response:");
            System.out.println(respXml);
        }
        if (!commandResult.isCompleted()) {
            Log.error("Ad-hoc command execution did not complete successfully for URI: " + uri);
        } else {
            Log.debug("Ad-hoc command execution completed successfully for URI: " + uri);
        }
    }

    private static FillableForm fillForm(DataForm respForm, DataForm dataForm) {
        FillableForm fillableForm = new FillableForm(respForm);
        for (FormField field : dataForm.getFields()) {
            if (field.getFieldName().equals("FORM_TYPE")) {
                continue;
            }
            switch (field.getType()) {
                case list_multi:
                case text_multi:
                case jid_multi:
                    fillableForm.setAnswer(field.getFieldName(), field.getValues());
                    break;
                default:
                    fillableForm.setAnswer(field.getFieldName(), field.getValues().get(0));
            }
        }
        return fillableForm;
    }

    /**
     * Gets JID from URI. Returns the full jid including resource e.g. <code>romeo@montague.lit/orchard</code>
     */
    public Jid retrieveJID(URI uri) {
        if (uri.getHost() == null) {
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
     * Extracts a param from the URI.
     * E.g., to get a password:
     * <pre>
     * URI uri = parseXmppUri("xmpp:chamber@shakespeare.lit?join;password=secret");
     * String password = retrieveParam(uri, "password");
     * </pre>
     *
     * @return null if the param is not found or empty.
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
