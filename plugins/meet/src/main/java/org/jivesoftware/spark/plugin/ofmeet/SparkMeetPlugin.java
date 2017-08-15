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
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;


import org.jivesoftware.Spark;
import org.jivesoftware.spark.*;
import org.jivesoftware.spark.component.*;
import org.jivesoftware.spark.component.browser.*;
import org.jivesoftware.spark.plugin.*;
import org.jivesoftware.spark.ui.rooms.*;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.log.*;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;


public class SparkMeetPlugin implements Plugin, ChatRoomListener, GlobalMessageListener
{
    private org.jivesoftware.spark.ChatManager chatManager;

    private String protocol = "https";
    private String server = null;
    private String port = "7443";
    private String url = null;
    private int width = 1024;
    private int height = 768;
    private String path = "ofmeet";

    private static File pluginsettings = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Spark" + System.getProperty("file.separator") + "ofmeet.properties");
    private Map<String, ChatRoomDecorator> decorators = new HashMap<String, ChatRoomDecorator>();

    private Browser browser = null;
    private JFrame frame = null;
    private JPanel inviteAlert;


    public SparkMeetPlugin()
    {

    }

    public void initialize()
    {
        chatManager = SparkManager.getChatManager();

        server = SparkManager.getSessionManager().getServerAddress();

        Properties props = new Properties();

        if (pluginsettings.exists())
        {
            Log.warning("ofmeet-info: Properties-file does exist= " + pluginsettings.getPath());

            try {
                props.load(new FileInputStream(pluginsettings));

                if (props.getProperty("port") != null)
                {
                    port = props.getProperty("port");
                    Log.warning("ofmeet-info: ofmeet-port from properties-file is= " + port);
                }

                if (props.getProperty("protocol") != null)
                {
                    protocol = props.getProperty("protocol");
                    Log.warning("ofmeet-info: ofmeet-protocol from properties-file is= " + protocol);
                }

                if (props.getProperty("server") != null)
                {
                    server = props.getProperty("server");
                    Log.warning("ofmeet-info: ofmeet-server from properties-file is= " + server);
                }

                if (props.getProperty("path") != null)
                {
                    path = props.getProperty("path");
                    Log.warning("ofmeet-info: ofmeet-path from properties-file is= " + path);
                }

                if (props.getProperty("width") != null)
                {
                    width = Integer.parseInt(props.getProperty("width"));
                    Log.warning("ofmeet-info: ofmeet-width from properties-file is= " + width);
                }

                if (props.getProperty("height") != null)
                {
                    height = Integer.parseInt(props.getProperty("height"));
                    Log.warning("ofmeet-info: ofmeet-height from properties-file is= " + height);
                }


            } catch (Exception e) {
                System.err.println(e);
            }

        } else {

            Log.warning("ofmeet-Error: Properties-file does not exist= " + pluginsettings.getPath() + ", using default " + url);
        }

        url = "https://" + server + ":" + port + "/" + path;

        chatManager.addChatRoomListener(this);
        chatManager.addGlobalMessageListener(this);

    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {

        try {
            String roomId = getNode(room.getRoomname());
            String body = message.getBody();

            if ( body.startsWith("https://") && body.endsWith("/" + roomId) ) {
                showInvitationAlert(message.getBody(), room, roomId);
            }


        } catch (Exception e) {
            // i dont care
        }

    }

    @Override
    public void messageSent(ChatRoom room, Message message) {

    }

    private String getNode(String jid)
    {
        String node = jid;
        int pos = node.indexOf("@");

        if (pos > -1)
            node = jid.substring(0, pos);

        return node;
    }

    public void shutdown()
    {
        try
        {
            Log.warning("shutdown");

            chatManager.removeChatRoomListener(this);
            chatManager.removeGlobalMessageListener(this);
        }
        catch(Exception e)
        {
            Log.warning("shutdown ", e);
        }
    }

    public boolean canShutDown()
    {
        return true;
    }

    public void uninstall()
    {

    }

    // openUrl keep only one ofmeet window opened at any time
    // to close window, send hangup command and wait for 1 sec.
    // before disposing browser and jframe window


    public void openUrl(String url, String roomId)
    {
        String meetUrl = url;

        try {
            String username = URLEncoder.encode(SparkManager.getSessionManager().getUsername(), "UTF-8");
            String password = URLEncoder.encode(SparkManager.getSessionManager().getPassword(), "UTF-8");

            if (meetUrl.startsWith("https://"))
            {
                meetUrl = "https://" + username + ":" + password + "@" + url.substring(8);
                openRoom(meetUrl, roomId);

            } else Log.warning("openUrl:  unexpected url " + meetUrl);

        } catch (Exception e) {
            Log.warning("Error with username/password:  " + meetUrl);
            openRoom(meetUrl, roomId);
        }
    }

    private void openRoom(String roomUrl, String roomId)
    {
        try {
            if (browser != null)
            {
                ActionListener taskPerformer = new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        if (browser != null) browser.dispose();
                        if (frame != null)   frame.dispose();

                        open(roomUrl, roomId);
                    }
                };

                browser.executeJavaScript("APP.conference.hangup();");

                javax.swing.Timer timer = new javax.swing.Timer(1000 ,taskPerformer);
                timer.setRepeats(false);
                timer.start();

            } else open(roomUrl, roomId);

        } catch (Exception t) {

            Log.warning("openRoom " + roomUrl, t);
        }
    }

    private void open(String roomUrl, String roomId)
    {
        browser = new Browser();
        BrowserView view = new BrowserView(browser);

        frame = new JFrame();
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setTitle("Openfire Meetings - " + roomId);

        frame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                close();
            }
        });

        browser.loadURL(roomUrl);
    }

    private void close()
    {
        ActionListener taskPerformer = new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                if (browser != null)
                {
                    browser.dispose();
                    browser = null;
                }

                if (frame != null)
                {
                    frame.dispose();
                    frame = null;
                }
            }
        };

        browser.executeJavaScript("APP.conference.hangup();");

        javax.swing.Timer timer = new javax.swing.Timer(1000 ,taskPerformer);
        timer.setRepeats(false);
        timer.start();
    }

    public void chatRoomLeft(ChatRoom chatroom)
    {

    }

    public void chatRoomClosed(ChatRoom chatroom)
    {
        String roomId = chatroom.getRoomname();

        Log.warning("chatRoomClosed:  " + roomId);

        if (decorators.containsKey(roomId))
        {
            ChatRoomDecorator decorator = decorators.remove(roomId);
            decorator.finished();
            decorator = null;
        }

        if (browser != null)
        {
            browser.dispose();
            browser = null;
        }
    }

    public void chatRoomActivated(ChatRoom chatroom)
    {
        String roomId = chatroom.getRoomname();

        Log.warning("chatRoomActivated:  " + roomId);
    }

    public void userHasJoined(ChatRoom room, String s)
    {
        String roomId = room.getRoomname();

        Log.warning("userHasJoined:  " + roomId + " " + s);
    }

    public void userHasLeft(ChatRoom room, String s)
    {
        String roomId = room.getRoomname();

        Log.warning("userHasLeft:  " + roomId + " " + s);
    }

    public void chatRoomOpened(final ChatRoom room)
    {
        String roomId = room.getRoomname();

        Log.warning("chatRoomOpened:  " + roomId);

        if (roomId.indexOf('/') == -1 && decorators.containsKey(roomId) == false)
        {
            decorators.put(roomId, new ChatRoomDecorator(room, url, this));
        }
    }

    /**
     * Display an alert that allows the user to accept or reject a meet
     * invitation.
     *
     * @param invitation
     *            the meet invitation.
     */
    private void showInvitationAlert(final String meetUrl, final ChatRoom room, final String roomId)
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

        acceptButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Hide the response panel. TODO: make this work.
                room.getTranscriptWindow().remove(inviteAlert);
                inviteAlert.remove(1);
                inviteAlert.add(new JLabel("Joining audio/conference conference ..."), BorderLayout.CENTER);
                declineButton.setEnabled(false);
                acceptButton.setEnabled(false);

                openUrl(meetUrl, roomId);
            }
        });
        buttonPanel.add(acceptButton);

        // The decline button. When clicked, reject the meet offer.

        declineButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Hide the response panel. TODO: make this work.
                room.getTranscriptWindow().remove(inviteAlert);
                declineButton.setVisible(false);
                acceptButton.setVisible(false);
            }
        });
        buttonPanel.add(declineButton);
        content.add(buttonPanel, BorderLayout.SOUTH);
        inviteAlert.add(content, BorderLayout.CENTER);

        // Add the response panel to the transcript window.
        room.getTranscriptWindow().addComponent(inviteAlert);
    }
}
