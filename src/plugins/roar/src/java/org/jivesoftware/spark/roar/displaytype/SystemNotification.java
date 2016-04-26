package org.jivesoftware.spark.roar.displaytype;

import org.jivesoftware.Spark;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.roar.RoarResources;
import org.jivesoftware.spark.ui.ChatRoom;

/**
 * Provides OS-based notifications
 * 
 * @author wolf.posdorfer
 */
public class SystemNotification implements RoarDisplayType {

    @Override
    public void messageReceived(ChatRoom room, Message message, PropertyBundle bundle) {

        String nickname = RoarPopupHelper.getNickname(room, message);
        if (Spark.isMac()) {
            MacNotificationCenter.sendNotification(nickname, message.getBody());
        } else {
            WindowsNotification.sendNotification(nickname, message.getBody());
        }
    }
    
    @Override
    public void messageSent(ChatRoom room, Message message) {
        // doesn't apply
    }

    @Override
    public void closingRoarPanel(int x, int y) {
        // doesn't apply
    }
    
    @Override
    public String toString() {
        return "SystemNotification";
    }

    public String getName() {
        return "SystemNotification";
    }

    public String getLocalizedName() {
        return RoarResources.getString("roar.display.system");
    }
    
    @Override
    public String getWarningMessage() {
        if (Spark.isMac()) {
            return RoarResources.getString("roar.warning.system.mac");
        } else {
            return RoarResources.getString("roar.warning.system");
        }
    }

}
