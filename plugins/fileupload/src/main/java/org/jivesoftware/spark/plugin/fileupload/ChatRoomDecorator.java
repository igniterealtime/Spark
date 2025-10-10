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

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.updater.AcceptAllCertsConnectionManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ChatRoomDecorator
{
    public RolloverButton fileuploadButton;
    public final ChatRoom room;

    public ChatRoomDecorator(final ChatRoom room)
    {
        this.room = room;

        try {
            fileuploadButton = new RolloverButton(SparkRes.getImageIcon("UPLOAD_ICON"));
            fileuploadButton.setToolTipText(GraphicUtils.createToolTip("Http File Upload"));

            fileuploadButton.addActionListener(event -> {
                if ("groupchat".equals(room.getChatType().toString()))
                {
                    getUploadUrl(room, Message.Type.groupchat);
                } else {
                    getUploadUrl(room, Message.Type.chat);
                }
            });
            room.getEditorBar().add(fileuploadButton);

        } catch (Exception e) {
            Log.error("Cannot create file upload icon for the ChatRoomDecorator", e);
        }

    }

    public void finished()
    {
        Log.debug("ChatRoomDecorator finished for room: " + room.getBareJid());
    }

    private void getUploadUrl(ChatRoom room, Message.Type type)
    {
        FileDialog fd = new FileDialog((Frame)null, "Choose a file to upload", FileDialog.LOAD);
        fd.setMultipleMode(true);
        fd.setVisible(true);
        File[] files = fd.getFiles();

        for (File file : files)
        {
            SwingUtilities.invokeLater( () -> new Thread(() -> handleUpload(file, room, type)).start());
        }
    }


    private void handleUpload(File file, ChatRoom room, Message.Type type)
    {
        Log.debug("Uploading file: " + file.getAbsolutePath());
        String fileName = null;
        try {
            fileName = URLEncoder.encode(file.getName(), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            // Can be safely ignored because UTF-8 is always supported
        }
        try {
            UploadRequest request = new UploadRequest(fileName, file.length(), UploadRequest.guessContentType(file));
            request.setTo(JidCreate.fromOrThrowUnchecked("httpfileupload." + SparkManager.getSessionManager().getServerAddress()));
            request.setType(IQ.Type.get);

            IQ result = SparkManager.getConnection().createStanzaCollectorAndSend(request).nextResultOrThrow();

            UploadRequest response = (UploadRequest) result;

            Log.debug("handleUpload response: putUrl=" + response.putUrl + " getUrl=" + response.getUrl);

            if (response.putUrl != null)
            {
                uploadFile(file, response, room, type);
            }

        } catch (Exception e) {
            Log.error("Error while attempting to uploading file", e);
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(room, "Upload failed: " + e.getMessage(),"Http File Upload Plugin", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void uploadFile(File file, UploadRequest response, ChatRoom room, Message.Type type)
    {
        Log.debug("About to upload file for room " + room.getBareJid() + " via HTTP PUT to URL " + response.putUrl);
        ContentType contentType = ContentType.create("application/binary");
        try {
            final String contentTypeName = UploadRequest.guessContentType(file);
            if (contentTypeName != null) {
                contentType = ContentType.create(contentTypeName);
            }
        } catch (Throwable t) {
            Log.warning("Unable to deduce content type of file " + file, t);
        }
        try (final CloseableHttpClient httpClient =
                 HttpClients.custom()
                    .setConnectionManager(AcceptAllCertsConnectionManager.getInstance())
                    .build()
        ) {
            final ClassicHttpRequest request = ClassicRequestBuilder.put(response.putUrl)
                .setEntity(new FileEntity(file, contentType))
                .setHeader("User-Agent", "Spark HttpFileUpload")
                .build();

            httpClient.execute(request, httpResponse -> {
                try {
                    final int statusCode = httpResponse.getCode();
                    final String reasonPhrase = httpResponse.getReasonPhrase();
                    if ((statusCode >= 200) && (statusCode <= 202)) {
                        Log.debug("Upload file success. HTTP response: " + statusCode + " " + reasonPhrase);
                        broadcastUploadUrl(room.getBareJid(), response.getUrl, type);
                    } else {
                        throw new IllegalStateException("Server responded to upload request with: " + statusCode + ": " + reasonPhrase);
                    }
                } catch (Exception e) {
                    Log.error("Error encountered whilst uploading the file", e);
                    UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                    JOptionPane.showMessageDialog(room, "Upload failed: " + e.getMessage(), "Http File Upload Plugin", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            });
        } catch (Exception e) {
            Log.error("Error encountered whilst uploading the file", e);
            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(room, "Upload failed: " + e.getMessage(), "Http File Upload Plugin", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void broadcastUploadUrl(EntityBareJid jid, String url, Message.Type type)
    {
        Message message2 = new Message();
        message2.setTo(jid);
        message2.setType(type);
        message2.setBody(url);
        message2.addExtension(
            StandardExtensionElement.builder("x", "jabber:x:oob")
                .addElement("url", url)
                .build()
        );
        room.sendMessage(message2);
    }

}
