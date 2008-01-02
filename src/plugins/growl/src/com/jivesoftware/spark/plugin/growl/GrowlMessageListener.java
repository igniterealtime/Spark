/**
 * $Revision: 22540 $
 * $Date: 2005-10-10 08:44:25 -0700 (Mon, 10 Oct 2005) $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */

package com.jivesoftware.spark.plugin.growl;

import com.apple.cocoa.application.NSImage;
import com.apple.cocoa.foundation.NSData;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.GlobalMessageListener;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.SwingUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andrew Wright
 */
public class GrowlMessageListener implements GlobalMessageListener {

    private Growl growl;

    public GrowlMessageListener() {
        String[] notes = {"Message Received"};
        growl = new Growl("Spark", notes, notes);
        growl.register();
    }

    public void messageReceived(final ChatRoom chatRoom, final Message message) {
        final ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();

        if (!chatFrame.isVisible() || !chatFrame.isInFocus()) {
            showGrowlNotification(message);
        }

    }

    private void showGrowlNotification(final Message message) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    String name = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom());

                    // Since it looks the method can return null do this in case
                    if (name == null) {
                        name = StringUtils.parseName(message.getFrom());
                    }

                    VCard vCard = null;

                    try {
                        vCard = SparkManager.getVCardManager().getVCard(
                                StringUtils.parseBareAddress(message.getFrom()));
                    }
                    catch (Exception e) {
                        // vcard can time out so ignore
                    }

                    NSImage image = null;
                    if (vCard != null) {
                        byte[] bytes = vCard.getAvatar();
                        if (bytes != null) {
                            try {
                                NSData data = new NSData(bytes);
                                image = new NSImage(data);
                            }
                            catch (Exception e) {
                                // just incase there is an error i didn't intend
                            }
                        }
                    }

                    if (image == null) {
                        image = getImage("/images/message-32x32.png");
                    }

                    growl.notifyGrowlOf("Message Received", image, name, message.getBody(), null);

                }
                catch (Exception e) {
                    Log.error(e.getMessage(), e);
                }

            }
        });
    }

    public void messageSent(ChatRoom room, Message message) {
        // Ignore
    }

    /**
     * Creates a {@link com.apple.cocoa.application.NSImage} from a string that points to an image in the class
     *
     * @param image classpath path of an image
     * @return an cocoa image object
     */
    private NSImage getImage(String image) {
        InputStream in = this.getClass().getResourceAsStream(image);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buff = new byte[10 * 1024];
        int len;
        try {
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            in.close();
            out.close();
        }
        catch (IOException e) {
            Log.error(e.getMessage(), e);
        }

        NSData data = new NSData(out.toByteArray());
        return new NSImage(data);
    }
}
