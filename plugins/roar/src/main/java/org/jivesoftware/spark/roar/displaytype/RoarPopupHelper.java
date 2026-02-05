package org.jivesoftware.spark.roar.displaytype;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;

/**
 * Utility methods for popups
 * 
 * @author w.posdorfer
 */
public final class RoarPopupHelper {

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
        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom().asBareJid());
        if (room.getChatType() == Message.Type.groupchat) {
            nickname = message.getFrom().getResourceOrEmpty().toString();
        }

        final JivePropertiesExtension extension = message.getExtension(JivePropertiesExtension.class);
        final boolean broadcast = extension != null && extension.getProperty( "broadcast" ) != null;

        if ((broadcast || message.getType() == Message.Type.normal || message.getType() == Message.Type.headline)
                && message.getBody() != null) {
            nickname = Res.getString("broadcast") + " - " + nickname;
        }
        return nickname;
    }
}
