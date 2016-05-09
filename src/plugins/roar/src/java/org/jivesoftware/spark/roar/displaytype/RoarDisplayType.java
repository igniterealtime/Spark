package org.jivesoftware.spark.roar.displaytype;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ui.ChatRoom;

public interface RoarDisplayType {
    
    /**
     * Do stuff when a message is received
     */
    public void messageReceived(ChatRoom room, Message message);
    
    /**
     * Do stuff when a message is sent
     */
    public void messageSent(ChatRoom room, Message message);
    
    
    /**
     * Do stuff when the Popup closes
     * @param type, the owner of the Popup
     */
    void closingRoarPanel(int x, int y);
    
    
}
