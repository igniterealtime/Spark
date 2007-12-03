/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.filetransfer;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.FileDropListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Collection;

/**
 * Enables encapsulation of transfer capabilities within a ChatRoom.
 */
public class ChatRoomTransferDecorator implements KeyListener, FileDropListener, ChatRoomClosingListener, ActionListener {

    private ChatRoom chatRoom;
    private ChatRoomButton sendFileButton;
    private ChatRoomButton sendScreenShotButton;

    public ChatRoomTransferDecorator(final ChatRoom chatRoom) {
        this.chatRoom = chatRoom;

        chatRoom.addFileDropListener(this);
        chatRoom.getChatInputEditor().addKeyListener(this);
        chatRoom.addClosingListener(this);


        sendFileButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.SEND_FILE_24x24));
        sendFileButton.setToolTipText(Res.getString("message.send.file.to.user"));

        chatRoom.getToolBar().addChatRoomButton(sendFileButton);

        sendScreenShotButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.PHOTO_IMAGE));
        sendScreenShotButton.setToolTipText(Res.getString("message.send.picture"));
        chatRoom.getToolBar().addChatRoomButton(sendScreenShotButton);

        sendFileButton.addActionListener(this);
        sendScreenShotButton.addActionListener(this);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_V) {
            int i = ke.getModifiers();
            if ((i & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
                Clipboard clb = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable contents = clb.getContents(ke.getSource());
                if (contents != null && contents.getTransferDataFlavors().length == 1) {
                    if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                        SparkManager.getTransferManager().sendImage(SparkTransferManager.getClipboard(), chatRoom);
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void filesDropped(Collection files, Component component) {
        if (component instanceof ChatRoomImpl) {
            ChatRoomImpl roomImpl = (ChatRoomImpl)component;


            for (Object file : files) {
                SparkManager.getTransferManager().sendFile((File) file, roomImpl.getParticipantJID());
            }

            SparkManager.getChatManager().getChatContainer().activateChatRoom(roomImpl);
        }
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendScreenShotButton) {
            SparkManager.getTransferManager().sendScreenshot(sendScreenShotButton, chatRoom);
        }
        else {
            showFilePicker();
        }
    }

    private void showFilePicker() {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e1) {
                    Log.error(e1);
                }
                return true;
            }

            public void finished() {
                FileDialog fileChooser = SparkManager.getTransferManager().getFileChooser(SparkManager.getChatManager().getChatContainer().getChatFrame(), Res.getString("title.select.file.to.send"));
                fileChooser.setVisible(true);

                if (fileChooser.getDirectory() == null || fileChooser.getFile() == null) {
                    return;
                }

                File file = new File(fileChooser.getDirectory(), fileChooser.getFile());

                if (file.exists()) {
                    SparkManager.getTransferManager().setDefaultDirectory(file.getParentFile());
                    SparkManager.getTransferManager().sendFile(file, ((ChatRoomImpl)chatRoom).getParticipantJID());
                }

            }
        };
        worker.start();
    }

    public void closing() {
        chatRoom.removeFileDropListener(this);
        chatRoom.getChatInputEditor().removeKeyListener(this);
        chatRoom.removeClosingListener(this);
        sendFileButton.removeActionListener(this);
        sendScreenShotButton.removeActionListener(this);
    }
}
