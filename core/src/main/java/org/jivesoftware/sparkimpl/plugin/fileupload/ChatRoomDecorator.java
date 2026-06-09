/**
 * Copyright (C) 2004-2010 Jive Software. 2026 Ignite Realtime Foundation. All rights reserved.
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

package org.jivesoftware.sparkimpl.plugin.fileupload;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager;
import org.jivesoftware.smackx.httpfileupload.UploadService;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui.TransferUtils;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.net.URL;

import static org.jivesoftware.smack.XMPPException.XMPPErrorException;
import static org.jivesoftware.smack.packet.StanzaError.Condition.not_acceptable;
import static org.jivesoftware.smack.packet.StanzaError.Condition.resource_constraint;
import static org.jivesoftware.spark.ChatManager.ERROR_COLOR;

public class ChatRoomDecorator {
    private final HttpFileUploadManager httpFileUploadManager;
    private final RolloverButton fileuploadButton;
    private final ChatRoom room;

    public ChatRoomDecorator(HttpFileUploadManager httpFileUploadManager, final ChatRoom room) {
        this.httpFileUploadManager = httpFileUploadManager;
        this.room = room;
        // Adds file upload button to chat room
        fileuploadButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.Icon.UPLOAD_ICON));
        try {
            fileuploadButton.setToolTipText(GraphicUtils.createToolTip(Res.getString("title.file.upload")));
            fileuploadButton.addActionListener(event -> {
                getUploadUrl(room);
            });
            room.getEditorBar().add(fileuploadButton);
        } catch (Exception e) {
            Log.error("Cannot create file upload icon for the ChatRoomDecorator", e);
        }
    }

    public void finished() {
    }

    private void getUploadUrl(ChatRoom room) {
        FileDialog fd = new FileDialog((Frame) null, "Choose a file to upload", FileDialog.LOAD);
        fd.setMultipleMode(true);
        fd.setVisible(true);
        File[] files = fd.getFiles();

        SwingWorker worker = new SwingWorker() {
            @Override
            public Object construct() {
               for (File file : files) {
                    handleUpload(file, room);
               }
               return null;
            }
        };
        worker.start();
    }

    private void handleUpload(File file, ChatRoom room) {
        Log.debug("Uploading file: " + file.getAbsolutePath());
        long fileSize = file.length();
        if (fileSize == 0) {
            return;
        }
        UploadService uploadService = httpFileUploadManager.getDefaultUploadService();
        Long maxSize = uploadService.getMaxFileSize();
        if (maxSize != null) {
            if (fileSize > maxSize) {
                String maxsizeString = TransferUtils.getAppropriateByteWithSuffix(maxSize);
                String yoursizeString = TransferUtils.getAppropriateByteWithSuffix(fileSize);
                String fileMaxSizeMsg = Res.getString("message.file.transfer.file.too.big.error", maxsizeString, yoursizeString);
                room.getTranscriptWindow().insertNotificationMessage(fileMaxSizeMsg, ERROR_COLOR);
                return;
            }
        }
        try {
            URL uploadedFile = httpFileUploadManager.uploadFile(file);
            broadcastUploadUrl(uploadedFile.toString());
        } catch (Exception e) {
            String errMsg = e.getMessage();
            if (e instanceof XMPPErrorException) {
                Condition condition = ((XMPPErrorException) e).getStanzaError().getCondition();
                if (condition == resource_constraint) {
                    errMsg += "\n" + Res.getString("message.file.transfer.history.send.quota");
                } else if (condition == not_acceptable) {
                    errMsg += "\n" + Res.getString("message.file.transfer.history.send.notAllowed");
                }
            } else {
                Log.error("Error while attempting to uploading file", e);
            }
            String fileSendErrorMsg = Res.getString("message.file.transfer.history.send.error",
                file.getAbsolutePath(), room.getTabTitle()) + ":\n" + errMsg;
            room.getTranscriptWindow().insertNotificationMessage(fileSendErrorMsg, ERROR_COLOR);
        }
    }

    private void broadcastUploadUrl(String url) {
        MessageBuilder messageBuilder = StanzaBuilder.buildMessage()
            .setBody(url)
            .addExtension(
                StandardExtensionElement.builder("x", "jabber:x:oob")
                    .addElement("url", url)
                    .build()
            );
        room.sendMessage(messageBuilder);
    }

}
