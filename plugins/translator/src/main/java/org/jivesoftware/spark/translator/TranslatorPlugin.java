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

import java.awt.*;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.jivesoftware.spark.util.log.Log;
import space.dynomake.libretranslate.Language;
import space.dynomake.libretranslate.Translator;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.*;

/**
 * A plugin that uses external translation service API to translate instant messages between two users.
 *
 * @author Jive Software
 */
public class TranslatorPlugin implements Plugin {
    private MessageFilter translationMessageFilter;
    private TranslationChatRoomListener translationChatRoomListener;

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
        translationChatRoomListener = new TranslationChatRoomListener();
        translationMessageFilter = new TranslationMessageFilter();
        chatManager.addChatRoomListener(translationChatRoomListener);
        chatManager.addMessageFilter(translationMessageFilter);
    }

    /**
     * Called when Spark is shutting down to allow for persistence of information
     * or releasing of resources.
     */
    @Override
    public void shutdown() {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.removeChatRoomListener(translationChatRoomListener);
        chatManager.removeMessageFilter(translationMessageFilter);
        translationChatRoomListener = null;
        translationMessageFilter = null;
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

    private static class TranslationChatRoomListener implements org.jivesoftware.spark.ui.ChatRoomListener {
        @Override
        public void chatRoomOpened(ChatRoom room) {
            TranslatorProperties properties = TranslatorProperties.getInstance();
            if (!properties.getEnabledTranslator()) {
                return;
            }
            // Set server LibreTranslate API
            if (properties.getUseCustomUrl() && !StringUtils.isBlank(properties.getUrl())) {
                Translator.setUrlApi(properties.getUrl());
            } else {
                Translator.setUrlApi(TranslatorUtil.getDefaultUrl());
            }

            // Create a new ChatRoomButton.
            final JComboBox<Language> translatorBox = new JComboBox<>(TranslatorUtil.getLanguage());
            translatorBox.setName("translatorBox");
            translatorBox.setToolTipText(TranslatorResource.getString("translator.translateOutcomingLang") +
                " " +TranslatorResource.getString("translator.externalServiceWarning"));

            translatorBox.addActionListener(e -> {
                // Set the focus back to the message box.
                room.getChatInputEditor().requestFocusInWindow();
            });

            room.addChatRoomComponent(translatorBox);

            final JCheckBox translatorIncoming = new JCheckBox(TranslatorResource.getString("translator.translateIncoming"));
            translatorIncoming.setName("translatorIncoming");
            translatorIncoming.setToolTipText(TranslatorResource.getString("translator.translateIncoming") +
                    " " +TranslatorResource.getString("translator.externalServiceWarning"));
            translatorIncoming.addActionListener(e -> {
                // Set the focus back to the message box.
                room.getChatInputEditor().requestFocusInWindow();
            });
            room.addChatRoomComponent(translatorIncoming);
        }
    }

    private static class TranslationMessageFilter implements MessageFilter {
        @Override
        public void filterOutgoing(ChatRoom room, MessageBuilder messageBuilder) {
            if (!TranslatorProperties.getInstance().getEnabledTranslator()) {
                return;
            }
            @SuppressWarnings("unchecked")
            JComboBox<Language> translatorBox = (JComboBox<Language>) findTranslatorComponent(room, "translatorBox");
            if (translatorBox == null) {
                return;
            }
            Language lang = (Language) translatorBox.getSelectedItem();
            if (lang == null || lang == Language.NONE) {
                return;
            }
            // do the translation for outgoing messages.
            TranscriptWindow transcriptWindow = room.getTranscriptWindow();
            try {
                String currentBody = messageBuilder.getBody();
                currentBody = TranslatorUtil.translate(currentBody, lang);
                transcriptWindow.insertNotificationMessage("-> " + currentBody, Color.gray);
                // Manually remove the existing body to add a new one.
                messageBuilder.removeExtension(Message.Body.ELEMENT, Message.Body.NAMESPACE);
                messageBuilder.addBody(lang.getCode(), currentBody);
            } catch (Exception e) {
                Log.warning(e.getMessage());
                transcriptWindow.insertNotificationMessage(TranslatorResource.getString("translator.error"), ChatManager.ERROR_COLOR);
            }
        }

        private Component findTranslatorComponent(ChatRoom room, String compName) {
            Component[] comps = room.getEditorBar().getComponents();
            for (Component component : comps) {
                if (compName.equals(component.getName())) {
                    return component;
                }
            }
            return null;
        }

        @Override
        public void filterIncoming(ChatRoom room, MessageBuilder messageBuilder) {
            if (!TranslatorProperties.getInstance().getEnabledTranslator()) {
                return;
            }
            JCheckBox translatorIncoming = (JCheckBox) findTranslatorComponent(room, "translatorIncoming");
            if (translatorIncoming == null) {
                return;
            }
            if (!translatorIncoming.isSelected()) {
                return;
            }
            Language myLanguage = Language.fromCode(Locale.getDefault().getLanguage());
            if (myLanguage == null) {
                return;
            }
            TranscriptWindow transcriptWindow = room.getTranscriptWindow();
            try {
                String currentBody = messageBuilder.getBody();
                currentBody = TranslatorUtil.translate(currentBody, myLanguage);
                // Manually remove the existing body to add a new one.
                messageBuilder.removeExtension(Message.Body.ELEMENT, Message.Body.NAMESPACE);
                messageBuilder.addBody(myLanguage.getCode(), currentBody);
            } catch (Exception e) {
                Log.warning(e.getMessage());
                transcriptWindow.insertNotificationMessage(TranslatorResource.getString("translator.error"), ChatManager.ERROR_COLOR);
            }
        }
    }
}
