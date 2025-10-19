/**
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

import net.suuft.libretranslate.Language;
import net.suuft.libretranslate.Translator;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.MessageFilter;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

/**
 * A plugin that uses google's translation service to translate instant messages between two users.
 *
 * @author Jive Software
 */
public class TranslatorPlugin implements Plugin {

    /**
     * Called after Spark is loaded to initialize the new plugin.
     */
    @Override
    public void initialize() {
        TranslatorPreference pref = new TranslatorPreference();
        SparkManager.getPreferenceManager().addPreference(pref);

        // Retrieve ChatManager from the SparkManager
        final ChatManager chatManager = SparkManager.getChatManager();
        // Add to a new ChatRoom when the ChatRoom opens.
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                // only do the translation for single chat
                if (room instanceof ChatRoomImpl && TranslatorProperties.getInstance().getEnabledTranslator()) {
                    final ChatRoomImpl roomImpl = (ChatRoomImpl)room;
                    final TranscriptWindow transcriptWindow = roomImpl.getTranscriptWindow();

                    //Set server LibreTranslate API
                    if(TranslatorProperties.getInstance().getUseCustomUrl() && !StringUtils.isBlank(TranslatorProperties.getInstance().getUrl())){
                        Translator.setUrlApi(TranslatorProperties.getInstance().getUrl());
                    } else {
                        Translator.setUrlApi(TranslatorUtil.getDefaultUrl());
                    }

                    // Create a new ChatRoomButton.
                    final JComboBox<Object> translatorBox = new JComboBox<>(TranslatorUtil.getLanguage());

                    translatorBox.addActionListener( e -> {
                        // Set the focus back to the message box.
                        roomImpl.getChatInputEditor().requestFocusInWindow();
                    } );

                    roomImpl.addChatRoomComponent(translatorBox);

                    // do the translation for outgoing messages.
                    final MessageFilter messageFilter = new MessageFilter() {
                        @Override
                        public void filterOutgoing(ChatRoom room, MessageBuilder messageBuilder) {
                            String currentBody = messageBuilder.getBody();
                            Language lang = (Language) translatorBox.getSelectedItem();
                            if (lang != null && lang != Language.NONE) {
                                try {
                                    currentBody = TranslatorUtil.translate(currentBody, lang);
                                    transcriptWindow.insertNotificationMessage("-> "+currentBody, Color.gray);
                                    messageBuilder.setBody(currentBody);
                                } catch (Exception e){
                                    transcriptWindow.insertNotificationMessage(TranslatorResource.getString("translator.error"), ChatManager.ERROR_COLOR);
                                }
                            }
                        }

                        @Override
                        public void filterIncoming(ChatRoom room, Message message) {
                            // do nothing
                        }
                    };
                    chatManager.addMessageFilter(messageFilter);
                }
            }
        });
    }

    /**
     * Called when Spark is shutting down to allow for persistence of information
     * or releasing of resources.
     */
    @Override
    public void shutdown() {

    }

    /**
     * Return true if the Spark can shutdown on users request.
     *
     * @return true if Spark can shutdown on users request.
     */
    @Override
    public boolean canShutDown() {
        return true;
    }

    /**
     * Is called when a user explicitly asked to uninstall this plugin.
     * The plugin owner is responsible to clean up any resources and
     * remove any components install in Spark.
     */
    @Override
    public void uninstall() {
        // Remove all resources belonging to this plugin.
    }
}
