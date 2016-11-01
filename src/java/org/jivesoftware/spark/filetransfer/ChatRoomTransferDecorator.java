/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.filetransfer;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.FileDropListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.UIComponentRegistry;
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

        // See if we should disable the ability to transfer files
        if (!Default.getBoolean("DISABLE_FILE_XFER")) {
        	sendFileButton = UIComponentRegistry.getButtonFactory().createSendFileButton();
        	sendFileButton.setToolTipText(Res.getString("message.send.file.to.user"));

        	chatRoom.addChatRoomButton(sendFileButton);
        	sendFileButton.addActionListener(this);
        }

        // See if we should disable the ability to take screenshots
        if (!Default.getBoolean("DISABLE_SCREENSHOTS")) {
        	sendScreenShotButton = UIComponentRegistry.getButtonFactory().createScreenshotButton();
        	sendScreenShotButton.setToolTipText(Res.getString("message.send.picture"));

        	chatRoom.addChatRoomButton(sendScreenShotButton);
        	sendScreenShotButton.addActionListener(this);
        }
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

    @Override
    public void filesDropped(Collection<File> files, Component component) {
	if (component instanceof ChatRoomImpl) {
	    ChatRoomImpl roomImpl = (ChatRoomImpl) component;

	    for (File file : files) {
		SparkManager.getTransferManager().sendFile(file,
			roomImpl.getParticipantJID());
	    }

	    SparkManager.getChatManager().getChatContainer()
		    .activateChatRoom(roomImpl);
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
                if (SparkManager.getTransferManager().getDefaultDirectory() != null)
                {
                    fileChooser.setDirectory(SparkManager.getTransferManager().getDefaultDirectory().getAbsolutePath());
                }
                fileChooser.setVisible(true);

                final File[] files = fileChooser.getFiles();
                if ( files.length == 0) {
                    // no selection
                    return;
                }

                File file = files[0]; // Single-file selection is used. Using the first array item is safe.

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
