/**
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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

import java.io.*;
import java.net.*;

import java.awt.*;
import javax.swing.*;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.log.*;
import org.jivesoftware.smack.packet.*;
import org.jxmpp.jid.EntityBareJid;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;

import org.jivesoftware.sparkimpl.updater.EasySSLProtocolSocketFactory;

import org.jxmpp.jid.impl.JidCreate;
import javax.xml.bind.DatatypeConverter;
import org.jivesoftware.resource.SparkRes;

public class ChatRoomDecorator
{
    public RolloverButton fileuploadButton;
    public final ChatRoom room;

    public ChatRoomDecorator(final ChatRoom room, final SparkFileUploadPlugin plugin)
    {
        this.room = room;

        try {
            String imageString = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAHYYAAB2GAV2iE4EAAAETSURBVDhPtZMxioUwEIbHFdFCEQVB7BSs7EUQvICtp7CRB95DCytP4BW8hIWFF7ARL6CN5ulsYFlffMU+9mvyzwz5k0wSjhzAB3zR8c+8NcjzHHRdhyiKaIbBeQQWTdOQYzLqNE2J7/uor9z2oK5rGMcR4jiGIAjANE2YpolWf7g9AsdxIIoiJEkC27aBIAi08humwePxAFVVUWuaBjzPo2bBNCjLEhRFodF7/u8a933HHkiShPFNr9nXeKb7vieO46A+sSwLxyvMHdi2Deu6QlVVuLLneVAUBa1e+PZ5RZZl4rou7iDLMpp95fYhLcsCbduCYRgQhiHNMkCbC/M8k2EYUB+PiHRdR46mYnzlw+8M8ASnHRlMzJ472gAAAABJRU5ErkJggg==";
            byte[] imageByte = DatatypeConverter.parseBase64Binary(imageString);
            ImageIcon fileuploadIcon = new ImageIcon(imageByte);
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
            UploadRequest request = new UploadRequest(fileName, file.length());
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

        try {
            PutMethod put = new PutMethod(response.putUrl);
            int port = put.getURI().getPort();
            if (port > 0)
            {
                Protocol.registerProtocol( "https", new Protocol( "https", new EasySSLProtocolSocketFactory(), port ) );
            }

            HttpClient client = new HttpClient();
            RequestEntity entity = new FileRequestEntity(file, "application/binary");
            put.setRequestEntity(entity);
            put.setRequestHeader("User-Agent", "Spark HttpFileUpload");
            client.executeMethod(put);

            int statusCode = put.getStatusCode();
            String responseBody = put.getResponseBodyAsString();

            if ((statusCode >= 200) && (statusCode <= 202))
            {
                Log.debug("Upload file success. HTTP response: " + statusCode + " " + responseBody);
                broadcastUploadUrl(room.getBareJid(), response.getUrl, type);
            } else {
                Log.error("Failed to upload file. HTTP response: " + statusCode + " " + responseBody);
            }

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
        room.sendMessage(message2);
    }

}
