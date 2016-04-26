package org.jivesoftware.spark.roar.displaytype;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;

public interface RoarDisplayType {

    /**
     * Do stuff when a message is received
     */
    public void messageReceived(ChatRoom room, Message message, PropertyBundle bundle);

    /**
     * Do stuff when a message is sent
     */
    public void messageSent(ChatRoom room, Message message);

    /**
     * Do stuff when the Popup closes
     * 
     * @param type
     *            , the owner of the Popup
     */
    public void closingRoarPanel(int x, int y);

    public String getName();

    public String getLocalizedName();
    
    public String getWarningMessage();

    /**
     * Returns the Nickname of the person sending the message
     * 
     * @param room
     *            the ChatRoom the message was sent in
     * @param message
     *            the actual message
     * @return nickname
     */
    public static String getNickname(ChatRoom room, Message message) {
        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom());
        if (room.getChatType() == Message.Type.groupchat) {
            nickname = StringUtils.parseResource(nickname);
        }

        boolean broadcast = message.getProperty("broadcast") != null;

        if ((broadcast || message.getType() == Message.Type.normal || message.getType() == Message.Type.headline)
                && message.getBody() != null) {
            nickname = Res.getString("broadcast") + " - " + nickname;
        }
        return nickname;
    }
}
