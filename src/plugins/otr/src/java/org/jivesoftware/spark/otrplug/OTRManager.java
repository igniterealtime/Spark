package org.jivesoftware.spark.otrplug;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.java.otr4j.OtrKeyManager;
import net.java.otr4j.OtrKeyManagerImpl;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.MessageEventListener;
import org.jivesoftware.spark.ui.MyOtrKeyManager;
import org.jivesoftware.spark.ui.OTRSession;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;




public class OTRManager  extends ChatRoomListenerAdapter implements ContactItemHandler {

    private static OTRManager singleton;
    private static Object LOCK = new Object();
    private MessageListenerHandler _msgListener;
    private Map<String, OTRSession> _activeSessions = new HashMap<String,OTRSession>();
    final ChatManager chatManager = SparkManager.getChatManager();
    private static MyOtrKeyManager _keyManager;
    

    private OTRManager()
    {
        _msgListener = new MessageListenerHandler();
        chatManager.addChatRoomListener(this);
        chatManager.addContactItemHandler(this);
    }
    
    
    @Override
    public void chatRoomOpened(ChatRoom room) {
        super.chatRoomOpened(room);
        if (room instanceof ChatRoomImpl) {
            createOTRSession((ChatRoomImpl) room, ((ChatRoomImpl) room).getParticipantJID());
        }
    }
    
    @Override
    public void chatRoomClosed(ChatRoom room) {
        super.chatRoomClosed(room);
        if (OTRProperties.getInstance().getOTRCloseOnChatClose()) {
            if (room instanceof ChatRoomImpl) {
                ChatRoomImpl myroom = (ChatRoomImpl) room;
                if (_activeSessions.containsKey(myroom.getParticipantJID())) {
                    OTRSession searchedSession = _activeSessions.get(myroom.getParticipantJID());
                    searchedSession.stopSession();
                    _activeSessions.remove(myroom.getParticipantJID());
                }
            }
        }
    }
    
    
    public static OTRManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                OTRManager controller = new OTRManager();
                singleton = controller;
                try {
                    _keyManager = new MyOtrKeyManager(SparkManager.getUserDirectory().getPath()+"/otrkey.priv");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return controller;
            }
        }
        return singleton;
    }

    
    private void createOTRSession(ChatRoomImpl chatroom, String jid)
    {
        if (!_activeSessions.containsKey(jid))
        {
            _activeSessions.put(jid, startOTRSession(chatroom, jid));
        } else {
            _activeSessions.get(jid).updateChatRoom(chatroom);
        }
    }
    
    public MyOtrKeyManager getKeyManager()
    {
        return _keyManager;
    }
    
    private OTRSession startOTRSession(ChatRoomImpl chatroom, String jid)
    {
        return new OTRSession(chatroom, SparkManager.getConnection().getUser(), jid);
    }


    @Override
    public boolean handlePresence(ContactItem item, Presence presence) {
        if (OTRProperties.getInstance().getOTRCloseOnDisc())
        {
            if (!presence.isAvailable() && _activeSessions.containsKey(item.getJID()))
            {
                _activeSessions.get(item.getJID()).stopSession();
            }
        }
        return false;
    }


    @Override
    public Icon getIcon(String jid) {
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
