package org.jivesoftware.spark.otrplug;



import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.OTRSession;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;




public class OTRManager   {

    private static OTRManager singleton;
    private static Object LOCK = new Object();
    private MessageListenerHandler _msgListener;
    
    
    private OTRManager()
    {
        _msgListener = new MessageListenerHandler();
    }
    
    
    public static OTRManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                OTRManager controller = new OTRManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    public void startOTRSession(ChatRoomImpl chatroom, String jid)
    {
        new OTRSession(chatroom, SparkManager.getConnection().getUser(), jid);
    }
   
    

}
