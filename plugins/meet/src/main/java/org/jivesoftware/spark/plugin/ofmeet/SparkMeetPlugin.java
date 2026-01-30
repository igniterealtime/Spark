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


package org.jivesoftware.spark.plugin.ofmeet;

import java.awt.*;
import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;


import org.jivesoftware.Spark;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.spark.*;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.packet.IQ;

import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;

import org.jivesoftware.spark.plugin.*;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.util.log.*;

import org.jitsi.util.OSUtils;
import de.mxro.process.*;
import org.jxmpp.jid.parts.*;
import org.jxmpp.jid.impl.JidCreate;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SparkMeetPlugin implements Plugin, ChatRoomListener, GlobalMessageListener
{
	public Properties props = new Properties();
    public String url = null;
	
    private org.jivesoftware.spark.ChatManager chatManager;
    private final File pluginsettings = new File( Spark.getLogDirectory().getParentFile() + System.getProperty("file.separator") + "ofmeet.properties");

    private final Map<String, ChatRoomDecorator> decorators = new HashMap<>();
    private String electronExePath = null;
    private String electronHomePath = null;
    private XProcess electronThread = null;
    private JPanel inviteAlert;

    public void initialize()
    {
        ProviderManager.addIQProvider("query", QueryRequest.NAMESPACE, new QueryRequest.Provider());
        checkNatives();

        chatManager = SparkManager.getChatManager();		

        String server = SparkManager.getSessionManager().getServerAddress().toString();
        String port = "7443";
        url = "https://" + server + ":" + port + "/ofmeet/";

        if (pluginsettings.exists())
        {
            Log.debug("ofmeet-info: Properties-file does exist= " + pluginsettings.getPath());

            try {
                props.load(new FileInputStream(pluginsettings));

                if (props.getProperty("url") != null)
                {
                    url = props.getProperty("url");
                    Log.debug("ofmeet-info: ofmeet url from properties-file is= " + url);
                }

            } catch (IOException ioe) {
                 Log.error("ofmeet-Error:", ioe);
            }

        } else {
            Log.warning("ofmeet-Error: Properties-file does not exist= " + pluginsettings.getPath() + ", using default " + url);
        }

        chatManager.addChatRoomListener(this);
        chatManager.addGlobalMessageListener(this);
		
		SparkMeetPreference preference = new SparkMeetPreference(this);
		SparkManager.getPreferenceManager().addPreference(preference);

        initializeOnlineMeetings();
    }

    private void initializeOnlineMeetings() {
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());

        DiscoverInfo discoverInfo;
        String serverJid = SparkManager.getSessionManager().getServerAddress().toString();

        try {
            discoverInfo = discoManager.discoverInfo(JidCreate.domainBareFrom(serverJid));
        } catch (Exception e) {
            Log.debug("Unable to disco " + serverJid);
            return;
        }
        if (discoverInfo == null) {
            return;
        }

        boolean jitsiAvailable = discoverInfo.containsFeature("urn:xmpp:http:online-meetings#jitsi");
        boolean galeneAvailable = discoverInfo.containsFeature("urn:xmpp:http:online-meetings#galene");
        boolean ohunAvailable = discoverInfo.containsFeature("urn:xmpp:http:online-meetings#ohun");

        String sUrl = null;
        if (jitsiAvailable) {
            sUrl = getServerUrl("jitsi");
        } else if (galeneAvailable) {
            sUrl = getServerUrl("galene");
        } else if (ohunAvailable) {
            sUrl = getServerUrl("ohun");
        }

        if (sUrl != null) {
            url = sUrl;
        }
    }

    private String getServerUrl(String app) {
		String serverUrl = null;
        try {
            QueryRequest request = new QueryRequest(app);
            request.setTo(JidCreate.fromOrThrowUnchecked(SparkManager.getSessionManager().getServerAddress()));
            request.setType(IQ.Type.get);
            IQ result = SparkManager.getConnection().createStanzaCollectorAndSend(request).nextResultOrThrow();
            QueryRequest response = (QueryRequest) result;

            Log.debug("SparkMeet response: url=" + response.url);
            if (response.url != null) serverUrl = response.url + "/";

        } catch (Exception e) {
            Log.error("Unable to get meet url from server for app type " + app);
        }
		return serverUrl;
	}

	public void commit(String url) {
		this.url = url;
		props.setProperty("url", url);
		try {
			FileOutputStream outputStream = new FileOutputStream(pluginsettings);
			props.store(outputStream, "Properties");
			outputStream.close();
		} catch (Exception e) {
			 Log.error("ofmeet-Error:", e);
		}			
	}

    public void shutdown()
    {
        try
        {
            Log.debug("shutdown");
            chatManager.removeChatRoomListener(this);
            ProviderManager.removeIQProvider("query", QueryRequest.NAMESPACE);

            if (electronThread != null) electronThread.destory();
            electronThread = null;
        }
        catch(Exception e)
        {
            Log.error("shutdown ", e);
        }
    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {
        try {
            Localpart roomId = room.getJid().getLocalpart();
            String body = message.getBody();
            int pos = body.indexOf("https://");
            if (pos > -1 && (body.contains("/" + roomId + "-") || body.contains("meeting"))) {
                showInvitationAlert(message.getBody().substring(pos), room, roomId);
            }
        } catch (Exception ignored) {
        }
    }

    private void showInvitationAlert(final String meetUrl, final ChatRoom room, final CharSequence roomId)
    {
        // Got an offer to start a new meet. So, make sure that a chat is
        // started with the other
        // user and show an invite panel.

        inviteAlert = new JPanel();
        inviteAlert.setLayout(new BorderLayout());

        JPanel invitePanel = new JPanel();
        invitePanel.setPreferredSize(new Dimension(24, 24));
        inviteAlert.add(invitePanel, BorderLayout.WEST);
        JPanel content = new JPanel(new BorderLayout());

        content.add(new JLabel("Join audio/conference conference ..."), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();

        // The accept button. When clicked, accept the meet offer.
        final JButton acceptButton = new JButton("Accept");
        final JButton declineButton = new JButton("Decline");

        acceptButton.addActionListener(e -> {
            // Hide the response panel. TODO: make this work.
            room.getTranscriptWindow().remove(inviteAlert);
            inviteAlert.remove(1);
            inviteAlert.add(new JLabel("Meeting at " + meetUrl), BorderLayout.CENTER);
            declineButton.setEnabled(false);
            acceptButton.setEnabled(false);

            openURL(meetUrl);
        });
        buttonPanel.add(acceptButton);

        // The decline button. When clicked, reject the meet offer.

        declineButton.addActionListener(e -> {
            // Hide the response panel. TODO: make this work.
            room.getTranscriptWindow().remove(inviteAlert);
            declineButton.setVisible(false);
            acceptButton.setVisible(false);
        });
        buttonPanel.add(declineButton);
        content.add(buttonPanel, BorderLayout.SOUTH);
        inviteAlert.add(content, BorderLayout.CENTER);

        // Add the response panel to the transcript window.
        room.getTranscriptWindow().addComponent(inviteAlert);
    }

    @Override
    public void messageSent(ChatRoom room, Message message) {

    }

    public boolean canShutDown()
    {
        return true;
    }

    public void uninstall()
    {

    }

    public void handleClick(String newUrl, ChatRoom room, String url, Message.Type type)
    {
        if (electronThread != null)
        {
            electronThread.destory();
            electronThread = null;
            return;
        }

        sendInvite(room, url, type);
        openURL(newUrl);
    }

    public void openURL(String newUrl)
    {
        try {
            String username = URLEncoder.encode(SparkManager.getSessionManager().getUsername(), UTF_8);
            String password = URLEncoder.encode(SparkManager.getSessionManager().getPassword(), UTF_8);

            electronThread = Spawn.startProcess(electronExePath + " --ignore-certificate-errors " + newUrl, new File(electronHomePath), new ProcessListener() {

                public void onOutputLine(final String line) {
                    System.out.println(line);
                }

                public void onProcessQuit(int code) {
                    electronThread = null;
                }

                public void onOutputClosed() {
                    System.out.println("process completed");
                }

                public void onErrorLine(final String line) {

                    if (!line.contains("Corrupt JPEG data"))
                    {
                        Log.warning("Electron error " + line);
                    }
                }

                public void onError(final Throwable t) {
                    Log.warning("Electron error", t);
                }
            });
        } catch (Exception t) {
            Log.error("Error opening url " + newUrl, t);
        }
    }


    public void chatRoomLeft(ChatRoom chatroom)
    {
    }

    public void chatRoomClosed(ChatRoom chatroom)
    {
        String roomId = chatroom.getBareJid().toString();
        Log.debug("chatRoomClosed:  " + roomId);

        if (decorators.containsKey(roomId))
        {
            ChatRoomDecorator decorator = decorators.remove(roomId);
            decorator.finished();
        }

        if (electronThread != null)
        {
            electronThread.destory();
            electronThread = null;
        }
    }

    public void chatRoomActivated(ChatRoom chatroom)
    {
        String roomId = chatroom.getBareJid().toString();
        Log.debug("chatRoomActivated:  " + roomId);
    }

    public void userHasJoined(ChatRoom room, String s)
    {
        String roomId = room.getBareJid().toString();
        Log.debug("userHasJoined:  " + roomId + " " + s);
    }

    public void userHasLeft(ChatRoom room, String s)
    {
        String roomId = room.getBareJid().toString();
        Log.debug("userHasLeft:  " + roomId + " " + s);
    }

    public void chatRoomOpened(final ChatRoom room)
    {
        String roomId = room.getBareJid().toString();
        Log.debug("chatRoomOpened:  " + roomId);

        if (roomId.indexOf('/') == -1)
        {
            decorators.put(roomId, new ChatRoomDecorator(room, this));
        }
    }

    private void checkNatives()
    {
        Log.debug("checkNatives");

        new Thread()
        {
            @Override public void run()
            {
                try
                {
                    String nativeLibsJarPath = Spark.getSparkUserHome() + File.separator + "plugins" + File.separator + "meet" + File.separator + "lib";
                    File nativeLibFolder = new File(nativeLibsJarPath, "native");

                    electronHomePath = nativeLibsJarPath + File.separator + "native";
                    electronExePath = electronHomePath + File.separator + "electron";

                    if(!nativeLibFolder.exists())
                    {
                        nativeLibFolder.mkdir();

                        String jarFileSuffix = null;

                        if(OSUtils.IS_LINUX32)
                        {
                            jarFileSuffix = "-linux-ia32.zip";
                        }
                        else if(OSUtils.IS_LINUX64)
                        {
                            jarFileSuffix = "-linux-x64.zip";
                        }
                        else if(OSUtils.IS_WINDOWS32)
                        {
                            jarFileSuffix = "-win32-ia32.zip";
                        }
                        else if(OSUtils.IS_WINDOWS64)
                        {
                            jarFileSuffix = "-win32-x64.zip";
                        }
                        else if(OSUtils.IS_MAC)
                        {
                            jarFileSuffix = "-darwin-x64.zip";
                        }

                        InputStream inputStream = new URL("https://github.com/electron/electron/releases/download/v37.2.3/electron-v37.2.3" + jarFileSuffix).openStream();
                        ZipInputStream zipIn = new ZipInputStream(inputStream);
                        ZipEntry entry = zipIn.getNextEntry();

                        while (entry != null)
                        {
                            try
                            {
                                String filePath = electronHomePath + File.separator + entry.getName();

                                Log.debug("writing file..." + filePath);

                                if (!entry.isDirectory())
                                {
                                    File file = new File(filePath);
                                    file.setReadable(true, true);
                                    file.setWritable(true, true);
                                    file.setExecutable(true, true);

                                    new File(file.getParent()).mkdirs();

                                    extractFile(zipIn, filePath);
                                }
                                zipIn.closeEntry();
                                entry = zipIn.getNextEntry();
                            }
                            catch(Exception e) {
                                Log.error("Error", e);
                            }
                        }
                        zipIn.close();

                        Log.debug("Native lib folder created and natives extracted");
                    }
                    else {
                        Log.debug("Native lib folder already exist.");
                    }


                    String libPath = nativeLibFolder.getCanonicalPath();

                    if (!System.getProperty("java.library.path").contains(libPath))
                    {
                        String newLibPath = libPath + File.pathSeparator + System.getProperty("java.library.path");
                        System.setProperty("java.library.path", newLibPath);

                        // this will reload the new setting
                        Log.warning("Unable to modify 'java.library.path' dynamically. Please ensure the library path includes: " + libPath);
                    }
                }
                catch (Exception e)
                {
                    Log.error(e.getMessage(), e);
                }
            }

        }.start();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException
    {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;

        while ((read = zipIn.read(bytesIn)) != -1)
        {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private void sendInvite(ChatRoom room, String url, Message.Type type)
    {
        MessageBuilder messageBuilder = StanzaBuilder.buildMessage()
            .ofType(type)
            .setBody(url);
        room.sendMessage(messageBuilder);
    }
}
