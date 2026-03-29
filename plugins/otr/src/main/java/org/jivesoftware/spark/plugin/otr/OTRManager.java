package org.jivesoftware.spark.plugin.otr;

import java.awt.*;
import java.io.IOException;

import java.util.HashMap;

import java.util.Map;

import net.java.otr4j.OtrException;
import net.java.otr4j.OtrKeyManager;
import net.java.otr4j.OtrKeyManagerImpl.DefaultPropertiesStore;
import net.java.otr4j.session.Session;
import net.java.otr4j.session.SessionID;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;

import org.jivesoftware.spark.plugin.otr.impl.OTRSession;
import org.jivesoftware.spark.plugin.otr.util.OTRProperties;
import org.jivesoftware.spark.plugin.otr.util.OTRResources;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;

import javax.swing.*;

/**
 * OTRManager controls the whole OTR process.
 * It checks if a new chat window is opened and creates an OTR session if there is no available.
 * 
 * @author Bergunde Holger
 */
public class OTRManager {

    private static OTRManager singleton;
    private static final Object LOCK = new Object();
    private final Map<String, OTRSession> _activeSessions = new HashMap<>();
    private static OtrKeyManager _keyManager;

    private OTRManager() {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(new ChatRoomListener() {
            @Override
            public void chatRoomOpened(ChatRoom room) {
                if (!(room instanceof ChatRoomImpl)) {
                    return;
                }
                ChatRoomImpl chatRoom = (ChatRoomImpl) room;
                createOTRSession(chatRoom, chatRoom.getParticipantJID().toString());
            }

            @Override
            public void chatRoomClosed(ChatRoom room) {
                if (!(room instanceof ChatRoomImpl)) {
                    return;
                }
                if (!OTRProperties.getInstance().getOTRCloseOnChatClose()) {
                    return;
                }
                ChatRoomImpl myroom = (ChatRoomImpl) room;
                OTRSession searchedSession = getOtrSession(myroom.getParticipantJID().toString());
                if (searchedSession == null) {
                    return;
                }
                searchedSession.stopSession();
                _activeSessions.remove(myroom.getParticipantJID().toString());
            }
        });
        chatManager.addContactItemHandler(new ContactItemHandler() {
            @Override
            public boolean handlePresence(ContactItem item, Presence presence) {
                // check if the buddy went offline and close the session
                if (presence.isAvailable()) {
                    return false;
                }
                if (OTRProperties.getInstance().getOTRCloseOnDisconnect()) {
                    OTRSession otrSession = getOtrSession(item.getJid().toString());
                    if (otrSession != null) {
                        otrSession.stopSession();
                    }
                }
                return false;
            }
        });
    }

    /**
     * OTRManager is a singleton. Use this method to get the instance.
     */
    public static OTRManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating two singletons.
        synchronized (LOCK) {
            if (singleton == null) {
                singleton = new OTRManager();
                try {
                    DefaultPropertiesStore store = new DefaultPropertiesStore(SparkManager.getUserDirectory().getPath() + "/otrkey.priv");
                    _keyManager = new OtrKeyManagerImpl(store);
                    // We should generate a local key if there is no available
                    EntityFullJid userJid = SparkManager.getConnection().getUser();
                    String key = _keyManager.getLocalFingerprint(new SessionID(userJid.toString(), "none", "prpl-jabber"));
                    if (key == null) {
                        _keyManager.generateLocalKeyPair(new SessionID(userJid.toString(), "none", "prpl-jabber"));
                    }
                } catch (IOException e) {
                    Log.error(e);
                }
            }
        }
        return singleton;
    }

    /**
     * Starts the OTR session with a buddy.
     */
    public void startOtrWithUser(EntityBareJid participantJid) {
        OTRSession otrSession = getOtrSession(participantJid.toString());
        if (otrSession != null) {
            otrSession.startSession();
        }
    }


    public void authenticateUser(EntityBareJid participantJid) {
        OTRSession otrSession = getOtrSession(participantJid.toString());
        if (otrSession != null) {
            EventQueue.invokeLater(() -> {
                String dlgTitle = OTRResources.getString("otr.authenticate");
                String dlgMsg = OTRResources.getString("otr.authenticate.question");
                String secret = JOptionPane.showInputDialog(null, dlgMsg, dlgTitle, JOptionPane.QUESTION_MESSAGE);
                String question = null; // TODO implement an ability to set a question
                try {
                    otrSession.getMySession().initSmp(question, secret);
                } catch (OtrException e) {
                    Log.error(e);
                }
            });
        }
    }

    private void createOTRSession(ChatRoomImpl chatroom, String participantJid) {
        OTRSession otrSession = getOtrSession(participantJid);
        if (otrSession == null) {
            EntityFullJid myJid = SparkManager.getConnection().getUser();
            otrSession = new OTRSession(chatroom, myJid.toString(), participantJid);
            _activeSessions.put(participantJid, otrSession);
        } else {
            otrSession.updateChatRoom(chatroom);
        }
    }

    public OTRSession getOtrSession(String participantJid) {
        return _activeSessions.get(participantJid);
    }

    /**
     * Returns the OtrKeyManager to store and load keys
     */
    public OtrKeyManager getKeyManager() {
        return _keyManager;
    }

}
