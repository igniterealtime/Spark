package org.jivesoftware.spark.otrplug;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.java.otr4j.OtrKeyManager;
import net.java.otr4j.OtrKeyManagerImpl;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.MessageEventListener;
import org.jivesoftware.spark.ui.MyOtrKeyManager;
import org.jivesoftware.spark.ui.OTRSession;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;




public class OTRManager  extends ChatRoomListenerAdapter {

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
    }
    
    
    @Override
    public void chatRoomOpened(ChatRoom room) {
        super.chatRoomOpened(room);
        if (room instanceof ChatRoomImpl) {
            createOTRSession((ChatRoomImpl) room, ((ChatRoomImpl) room).getParticipantJID());
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
   
    public ImageIcon getIcon(String fileName)
    {
        final ClassLoader cl = getClass().getClassLoader();
        ImageIcon icon = new ImageIcon(cl.getResource(fileName));
        return icon;
    }

}
