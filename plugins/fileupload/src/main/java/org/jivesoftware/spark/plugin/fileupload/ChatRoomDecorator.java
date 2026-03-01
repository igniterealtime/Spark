/**
 * Copyright (C) 2004-2010 Jive Software. 2023 Ignite Realtime Foundation. All rights reserved.
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

package org.jivesoftware.spark.plugin.fileupload;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import static org.jivesoftware.smack.packet.Message.Type.groupchat;
import static org.jivesoftware.smack.packet.Message.Type.chat;

public class ChatRoomDecorator {
    private final HttpFileUploadManager httpFileUploadManager;
    private final RolloverButton fileuploadButton;
    private final ChatRoom room;

    public ChatRoomDecorator(HttpFileUploadManager httpFileUploadManager, final ChatRoom room) {
        this.httpFileUploadManager = httpFileUploadManager;
        this.room = room;
        // Adds file upload button to chat room
        fileuploadButton = new RolloverButton(SparkRes.getImageIcon("UPLOAD_ICON"));
        try {
            fileuploadButton.setToolTipText(GraphicUtils.createToolTip("Http File Upload"));
            fileuploadButton.addActionListener(event -> {
                getUploadUrl(room, room.getChatType() == groupchat ? groupchat : chat);
            });
            room.getEditorBar().add(fileuploadButton);
        } catch (Exception e) {
            Log.error("Cannot create file upload icon for the ChatRoomDecorator", e);
        }
    }

    private static String guessContentType(final File file) {
        String result = URLConnection.guessContentTypeFromName(file.getName());
        if (result != null && !result.isEmpty()) {
            return result;
        }
        try {
            result = Files.probeContentType(file.toPath());
            if (result != null && !result.isEmpty()) {
                return result;
            }
            try (final InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
                result = URLConnection.guessContentTypeFromStream(is);
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    public void finished() {
        Log.debug("ChatRoomDecorator finished for room: " + room.getBareJid());
    }

    private void getUploadUrl(ChatRoom room, Message.Type type) {
        FileDialog fd = new FileDialog((Frame) null, "Choose a file to upload", FileDialog.LOAD);
        fd.setMultipleMode(true);
        fd.setVisible(true);
        File[] files = fd.getFiles();

        for (File file : files) {
            SwingUtilities.invokeLater(() -> handleUpload(file, room, type));
        }
    }

    private void handleUpload(File file, ChatRoom room, Message.Type type) {
        Log.debug("Uploading file: " + file.getAbsolutePath());
        try {
            URL uploadedFile = httpFileUploadManager.uploadFile(file);
            broadcastUploadUrl(uploadedFile.toString(), type);
        } catch (Exception e) {
            Log.error("Error while attempting to uploading file", e);
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(room,
                "Upload failed: " + e.getMessage(),
                "Http File Upload Plugin",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void broadcastUploadUrl(String url, Message.Type type) {
        MessageBuilder messageBuilder = StanzaBuilder.buildMessage()
            .ofType(type)
            .setBody(url)
            .addExtension(
                StandardExtensionElement.builder("x", "jabber:x:oob")
                    .addElement("url", url)
                    .build()
            );
        room.sendMessage(messageBuilder);
    }

}
