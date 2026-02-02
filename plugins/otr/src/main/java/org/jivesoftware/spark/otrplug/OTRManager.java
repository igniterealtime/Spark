package org.jivesoftware.spark.otrplug;

import java.io.IOException;

import java.util.HashMap;

import java.util.Map;

import javax.swing.Icon;

import net.java.otr4j.OtrKeyManager;
import net.java.otr4j.OtrKeyManagerImpl;
import net.java.otr4j.session.SessionID;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;

import org.jivesoftware.spark.otrplug.impl.OTRSession;
import org.jivesoftware.spark.otrplug.util.OTRProperties;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityFullJid;

/**
 * OTRManager controls the whole OTR process.
 * It checks if a new chat window is opened and creates an OTR session if there is no available.
 * 
 * @author Bergunde Holger
 */
public class OTRManager extends ChatRoomListenerAdapter implements ContactItemHandler {

    private static OTRManager singleton;
    private static final Object LOCK = new Object();
    private final Map<String, OTRSession> _activeSessions = new HashMap<>();
    private static OtrKeyManagerImpl _keyManager;

    private OTRManager() {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(this);
        chatManager.addContactItemHandler(this);
    }

    @Override
    public void chatRoomOpened(ChatRoom room) {
        super.chatRoomOpened(room);
        if (room instanceof ChatRoomImpl) {
            createOTRSession((ChatRoomImpl) room, ((ChatRoomImpl) room).getParticipantJID().toString());
        }
    }

    @Override
    public void chatRoomClosed(ChatRoom room) {
        super.chatRoomClosed(room);
        if (OTRProperties.getInstance().getOTRCloseOnChatClose()) {
            if (room instanceof ChatRoomImpl) {
                ChatRoomImpl myroom = (ChatRoomImpl) room;
                OTRSession searchedSession = _activeSessions.get(myroom.getParticipantJID().toString());
                if (searchedSession != null) {
                    searchedSession.stopSession();
                    _activeSessions.remove(myroom.getParticipantJID().toString());
                }
            }
        }
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
                    _keyManager = new OtrKeyManagerImpl(SparkManager.getUserDirectory().getPath() + "/otrkey.priv");
                    // We should generate a local key if there is no available
                    EntityFullJid userJid = SparkManager.getConnection().getUser();
                    String key = _keyManager.getLocalFingerprint(new SessionID(userJid.toString(), "none", "Scytale"));
                    if (key == null) {
                        _keyManager.generateLocalKeyPair(new SessionID(userJid.toString(), "none", "Scytale"));
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return singleton;
    }

    /**
     * Starts the OTR session with specified JID.
     * 
     * @param jid participant
     */
    public void startOtrWithUser(String jid) {
        OTRSession otrSession = _activeSessions.get(jid);
        if (otrSession != null) {
            otrSession.startSession();
        }
    }

    private void createOTRSession(ChatRoomImpl chatroom, String jid) {
        OTRSession otrSession = _activeSessions.get(jid);
        if (otrSession == null) {
            otrSession = startOTRSession(chatroom, jid);
            _activeSessions.put(jid, otrSession);
        } else {
            otrSession.updateChatRoom(chatroom);
        }
    }

    /**
     * Returns the OtrKeyManager to store and load keys
     */
    public OtrKeyManager getKeyManager() {
        return _keyManager;
    }

    private OTRSession startOTRSession(ChatRoomImpl chatroom, String jid) {
        EntityFullJid userJid = SparkManager.getConnection().getUser();
        return new OTRSession(chatroom, userJid.toString(), jid);
    }

    @Override
    public boolean handlePresence(ContactItem item, Presence presence) {
        if (presence.isAvailable()) {
            return false;
        }
        if (OTRProperties.getInstance().getOTRCloseOnDisc()) {
            OTRSession otrSession = _activeSessions.get(item.getJid().toString());
            if (otrSession != null) {
                otrSession.stopSession();
            }
        }
        return false;
    }

    @Override
    public Icon getIcon(BareJid jid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Icon getTabIcon(Presence presence) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean handleDoubleClick(ContactItem item) {
        // TODO Auto-generated method stub
        return false;
    }

}
