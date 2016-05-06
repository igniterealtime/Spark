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
package org.jivesoftware.spark.translator;

import java.awt.Color;

import javax.swing.JComboBox;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.MessageEventListener;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jxmpp.util.XmppStringUtils;

/**
 * A plugin that uses google's translation service to translate instant messages between two users.
 *
 * @author Jive Software
 */
public class TranslatorPlugin implements Plugin {

    /**
     * Called after Spark is loaded to initialize the new plugin.
     */
    public void initialize() {
        // Retrieve ChatManager from the SparkManager
        final ChatManager chatManager = SparkManager.getChatManager();

        // Add to a new ChatRoom when the ChatRoom opens.
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                // only do the translation for single chat
                if (room instanceof ChatRoomImpl) {
                    final ChatRoomImpl roomImpl = (ChatRoomImpl)room;

                    // Create a new ChatRoomButton.
                    final JComboBox<TranslatorUtil.TranslationType> translatorBox = new JComboBox<>(TranslatorUtil.TranslationType.getTypes());

                    translatorBox.addActionListener( e -> {
                        // Set the focus back to the message box.
                        roomImpl.getChatInputEditor().requestFocusInWindow();
                    } );

                    roomImpl.addChatRoomComponent(translatorBox);

                    // do the translation for outgoing messages.
                    final MessageEventListener messageListener = new MessageEventListener() {
                        public void sendingMessage(Message message) {
                            String currentBody = message.getBody();
                            String oldBody = message.getBody();
                            TranslatorUtil.TranslationType type =
                                    (TranslatorUtil.TranslationType)translatorBox.getSelectedItem();
                            if (type != null && type != TranslatorUtil.TranslationType.None) {
                            	message.setBody(null);
                            	currentBody = TranslatorUtil.translate(currentBody, type);
                                TranscriptWindow transcriptWindow = chatManager.getChatRoom( XmppStringUtils.parseBareJid( message.getTo() ) ).getTranscriptWindow();
                                if(oldBody.equals(currentBody.substring(0,currentBody.length()-1)))
                                {
                                	transcriptWindow.insertNotificationMessage("Could not translate: "+currentBody, ChatManager.ERROR_COLOR);
                                }
                                else
                                {
                                    transcriptWindow.insertNotificationMessage("-> "+currentBody, Color.gray);
                                	message.setBody(currentBody); 
                                }
                            }
                        }
                        

                        public void receivingMessage(Message message) {
                            // do nothing
                        }
                    };
                    roomImpl.addMessageEventListener(messageListener);
                }
            }
        });
    }

    /**
     * Called when Spark is shutting down to allow for persistence of information
     * or releasing of resources.
     */
    public void shutdown() {

    }

    /**
     * Return true if the Spark can shutdown on users request.
     *
     * @return true if Spark can shutdown on users request.
     */
    public boolean canShutDown() {
        return true;
    }

    /**
     * Is called when a user explicitly asked to uninstall this plugin.
     * The plugin owner is responsible to clean up any resources and
     * remove any components install in Spark.
     */
    public void uninstall() {
        // Remove all resources belonging to this plugin.
    }
}
