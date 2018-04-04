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
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.log.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.filter.*;
import org.jxmpp.util.XmppStringUtils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;

import org.jivesoftware.sparkimpl.updater.EasySSLProtocolSocketFactory;

import sun.misc.BASE64Decoder;

public class ChatRoomDecorator
{
    public RolloverButton fileuploadButton;
    public ChatRoom room;
    private StanzaListener uploadResponseListener;

    public ChatRoomDecorator(final ChatRoom room, final SparkFileUploadPlugin plugin)
    {
        this.room = room;

        try {
            BASE64Decoder decoder = new BASE64Decoder();
            String imageString = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAHYYAAB2GAV2iE4EAAAETSURBVDhPtZMxioUwEIbHFdFCEQVB7BSs7EUQvICtp7CRB95DCytP4BW8hIWFF7ARL6CN5ulsYFlffMU+9mvyzwz5k0wSjhzAB3zR8c+8NcjzHHRdhyiKaIbBeQQWTdOQYzLqNE2J7/uor9z2oK5rGMcR4jiGIAjANE2YpolWf7g9AsdxIIoiJEkC27aBIAi08humwePxAFVVUWuaBjzPo2bBNCjLEhRFodF7/u8a933HHkiShPFNr9nXeKb7vieO46A+sSwLxyvMHdi2Deu6QlVVuLLneVAUBa1e+PZ5RZZl4rou7iDLMpp95fYhLcsCbduCYRgQhiHNMkCbC/M8k2EYUB+PiHRdR46mYnzlw+8M8ASnHRlMzJ472gAAAABJRU5ErkJggg==";
            byte[] imageByte = decoder.decodeBuffer(imageString);
            ImageIcon fileuploadIcon = new ImageIcon(imageByte);
            fileuploadButton = new RolloverButton(fileuploadIcon);
            fileuploadButton.setToolTipText(GraphicUtils.createToolTip("Http File Upload"));
            final String roomId = getNode(room.getRoomname());
            final String sessionID = roomId + "-" + System.currentTimeMillis();
            final String nickname = getNode(XmppStringUtils.parseBareAddress(SparkManager.getSessionManager().getJID()));

            fileuploadButton.addActionListener( new ActionListener()
            {
                    public void actionPerformed(ActionEvent event)
                    {
                        String newRoomId;

                        if ("groupchat".equals(room.getChatType().toString()))
                        {
                            newRoomId = roomId;
                            getUploadUrl(room, Message.Type.groupchat);

                        } else {

                            newRoomId = sessionID;
                            getUploadUrl(room, Message.Type.chat);
                        }
                    }
            });
            room.getEditorBar().add(fileuploadButton);

        } catch (Exception e) {

            Log.error("cannot create file upload icon", e);
        }

    }

    public void finished()
    {
        Log.warning("ChatRoomDecorator: finished " + room.getRoomname());
    }

    private String getNode(String jid)
    {
        String node = jid;
        int pos = node.indexOf("@");

        if (pos > -1)
            node = jid.substring(0, pos);

        return node;
    }

    private void getUploadUrl(ChatRoom room, Message.Type type)
    {
        FileDialog fd = new FileDialog((Frame)null, "Choose a file to upload", FileDialog.LOAD);
        fd.setMultipleMode(true);
        fd.setVisible(true);
        File files[] = fd.getFiles();

        for (File file : files)
        {
            handleUpload(file, room, type);
        }
    }


    private void handleUpload(File file, ChatRoom room, Message.Type type)
    {
        Log.warning("Uploading file: " + file.getAbsolutePath());
        String fileName = null;
        try {
            fileName = URLEncoder.encode(file.getName(), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            // Can be safely ignored because UTF-8 is always supported
        }
        try {
            UploadRequest request = new UploadRequest(fileName, file.length());
            request.setTo("httpfileupload." + SparkManager.getSessionManager().getServerAddress());
            request.setType(IQ.Type.get);

            PacketCollector collector = SparkManager.getConnection().createPacketCollector(new PacketIDFilter(request.getPacketID()));

            SparkManager.getConnection().sendPacket(request);

            IQ result = (IQ) collector.nextResult(5000);
            collector.cancel();

            if (result == null) {
               Log.error("No response from the server.");
               broadcastUploadUrl(room.getRoomname(), file.getName() + " upload failed", type);
               return;
            }
            if (result.getType() == IQ.Type.error) {
               String errorMsg = result.getError().getConditionText();

               if (errorMsg == null)
               {
                    if (result.getError().getCondition() == XMPPError.Condition.not_acceptable)
                    {
                        errorMsg = "File too large.";
                    }
                    else errorMsg = result.getError().toString();
               }

               Log.error(errorMsg);
               broadcastUploadUrl(room.getRoomname(), file.getName() + " upload failed - " + errorMsg, type);
               return;
            }

            UploadRequest response = (UploadRequest) result;

            Log.warning("handleUpload response " + response.putUrl + " " + response.getUrl);

            if (response.putUrl != null)
            {
                uploadFile(file, response, room, type);
            }

        } catch (Exception e) {
            Log.error("uploadFile error", e);
            broadcastUploadUrl(room.getRoomname(), file.getName() + " upload failed", type);
        }
    }

    private void uploadFile(File file, UploadRequest response, ChatRoom room, Message.Type type)
    {
        Log.warning("uploadFile request " + room.getRoomname() + " " + response.putUrl);
        URLConnection urlconnection = null;

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

            Log.warning("uploadFile response " + statusCode + " " + responseBody);

            if ((statusCode >= 200) && (statusCode <= 202))
            {
                broadcastUploadUrl(room.getRoomname(), response.getUrl, type);
            }

        } catch (Exception e) {
            Log.error("uploadFile error", e);
        }
    }

    private void broadcastUploadUrl(String jid, String url, Message.Type type)
    {
        Message message2 = new Message();
        message2.setTo(jid);
        message2.setType(type);
        message2.setBody(url);
        room.sendMessage(message2);
    }


}
