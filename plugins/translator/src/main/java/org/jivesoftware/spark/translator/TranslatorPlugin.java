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

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * A plugin that uses external translation service API to translate instant messages between two users.
 *
 * @author Jive Software
 */
public class TranslatorPlugin implements Plugin {
    private TranslatorPreference pref;
    private MessageFilter translationMessageFilter;
    private TranslationChatRoomListener translationChatRoomListener;

    @Override
    public void initialize() {
        pref = new TranslatorPreference();
        SparkManager.getPreferenceManager().addPreference(pref);

        // Retrieve ChatManager from the SparkManager
        final ChatManager chatManager = SparkManager.getChatManager();
        // Add to a new ChatRoom when the ChatRoom opens.
        translationChatRoomListener = new TranslationChatRoomListener();
        translationMessageFilter = new TranslationMessageFilter();
        chatManager.addChatRoomListener(translationChatRoomListener);
        chatManager.addMessageFilter(translationMessageFilter);
    }

    @Override
    public void shutdown() {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.removeChatRoomListener(translationChatRoomListener);
        chatManager.removeMessageFilter(translationMessageFilter);
        translationChatRoomListener = null;
        translationMessageFilter = null;
        SparkManager.getPreferenceManager().removePreference(pref);
        pref = null;
    }

    @Override
    public boolean canShutDown() {
        return true;
    }

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
                Translator.setApiKey(properties.getApiKey());
            } else {
                Translator.setUrlApi(TranslatorUtil.getDefaultUrl());
                Translator.setApiKey(null);
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
        private Language localeLanguage;

        public TranslationMessageFilter() {
            localeLanguage = Language.fromCode(Locale.getDefault().toLanguageTag());
            if (localeLanguage == Language.NONE) {
                localeLanguage = Language.fromCode(Locale.getDefault().getLanguage());
            }
        }

        @Override
        public void filterOutgoing(ChatRoom room, MessageBuilder messageBuilder) {
            String currentBody = messageBuilder.getBody();
            if (currentBody == null) {
                return;
            }
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
                String translatedBody = TranslatorUtil.translate(currentBody, lang);
                transcriptWindow.insertNotificationMessage("-> " + translatedBody, Color.gray);
                /*
                 * We'll include the translation along with the original message.
                 * But we need to flip the order of the body elements so that the translation is first.
                 * <pre>
                 * <message>
                 * 	<body lang='ru'>привет</body>
                 * 	<body>hi</body>
                 * </message>
                 * </pre>
                 * This is needed because XMPP clients show a message the in a user's locale or the first body.
                 */
                // Manually remove the existing body to add a new one.
                messageBuilder.removeExtension(Message.Body.ELEMENT, Message.Body.NAMESPACE);
                messageBuilder.addBody(lang.getCode(), translatedBody);
                messageBuilder.addBody(null, currentBody); // keep the original message
            } catch (Exception e) {
                Log.warning(e.getMessage());
                transcriptWindow.insertNotificationMessage(TranslatorResource.getString("translator.error"), ChatManager.ERROR_COLOR);
            }
        }

        @Override
        public void filterIncoming(ChatRoom room, MessageBuilder messageBuilder) {
            String currentBody = messageBuilder.getBody();
            if (currentBody == null) {
                return;
            }
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
            Language myLanguage = getMyLanguage();
            if (myLanguage == null) {
                return;
            }

            TranscriptWindow transcriptWindow = room.getTranscriptWindow();
            try {
                String translatedBody = TranslatorUtil.translate(currentBody, myLanguage);
                transcriptWindow.insertNotificationMessage("-> " + currentBody, Color.gray);
                // Manually remove the existing body to add a new one.
                messageBuilder.removeExtension(Message.Body.ELEMENT, Message.Body.NAMESPACE);
                messageBuilder.addBody(myLanguage.getCode(), translatedBody);
                messageBuilder.addBody(null, translatedBody); // keep the original message
            } catch (Exception e) {
                Log.warning(e.getMessage());
                transcriptWindow.insertNotificationMessage(TranslatorResource.getString("translator.error"), ChatManager.ERROR_COLOR);
            }
        }

        /**
         * Determine own language for translation incoming messages.
         */
        private Language getMyLanguage() {
            // the setting may be changed any time, so we have to always check it
            String myLanguageSetting = TranslatorProperties.getInstance().getMyLanguage();
            return !isEmpty(myLanguageSetting) ? Language.fromCode(myLanguageSetting) : localeLanguage;
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
    }
}
