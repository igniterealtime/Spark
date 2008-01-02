/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date:  $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package org.jivesoftware.spark.plugin;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.component.LinkLabel;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.BrowserLauncher;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GoogleDocument extends JPanel {

    private GoogleSearchResult result;

    public GoogleDocument(final ChatRoom room, final GoogleSearchResult result) {
        setLayout(new GridBagLayout());
        setBackground(Color.white);

        this.result = result;

        ClassLoader cl = getClass().getClassLoader();
        URL imageURL = cl.getResource("images/send_file_24x24.png");
        RolloverButton sendButton = new RolloverButton(new ImageIcon(imageURL));
        String url = result.getURL();
        String title = result.getSubject();

        Icon icon = result.getIcon();
        if (icon == null || icon.getIconWidth() == -1) {
            icon = SparkRes.getImageIcon(SparkRes.DOCUMENT_16x16);
        }

        LinkLabel documentLabel = new LinkLabel(title, url, Color.blue, Color.red);

        add(sendButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(icon), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(documentLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        documentLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                mouseEvent.consume();
                try {
                    File file = new File(result.getURL());
                    if (file.exists()) {
                        Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + result.getURL());
                    }
                    else {
                        // Assume it's .html
                        BrowserLauncher.openURL(result.getURL());
                    }
                }
                catch (IOException e1) {
                    // Nothing to do
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                SparkTransferManager transferManager = SparkManager.getTransferManager();
                ChatRoomImpl chatRoom = (ChatRoomImpl)room;
                File file = new File(result.getURL());
                if (file.exists()) {
                    UserManager userManager = SparkManager.getUserManager();
                    String fullJID = userManager.getFullJID(chatRoom.getParticipantJID());
                    if (fullJID != null)
                        transferManager.sendFile(file, fullJID);

                }
                else {
                    Message message = new Message();
                    message.setBody(result.getURL());
                    chatRoom.sendMessage(message);

                    chatRoom.getTranscriptWindow().insertNotificationMessage("Sent URL: " + result.getURL(), ChatManager.NOTIFICATION_COLOR);
                }

            }
        }

        );
    }

    public GoogleSearchResult getSearchResult() {
        return result;
    }

}
