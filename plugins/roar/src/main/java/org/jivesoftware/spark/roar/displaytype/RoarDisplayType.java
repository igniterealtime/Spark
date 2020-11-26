package org.jivesoftware.spark.roar.displaytype;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ui.ChatRoom;

public interface RoarDisplayType {

    /**
     * Do stuff when a message is received
     */
    void messageReceived(ChatRoom room, Message message, PropertyBundle bundle);

    /**
     * Do stuff when a message is sent
     */
    void messageSent(ChatRoom room, Message message);

    /**
     * Do stuff when the Popup closes
     */
    void closingRoarPanel(int x, int y);

    String getName();

    String getLocalizedName();
    
    String getWarningMessage();

    /**
     * Checks if the display type is supported on this platform.
     *
     * @return true if supported, otherwise false.
     */
    default boolean isSupported() {
        return true;
    }
}
