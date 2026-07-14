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
package org.jivesoftware.spellchecker;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.dts.spell.SpellChecker;
import org.dts.spell.swing.JTextComponentSpellChecker;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.GraphicUtils;

import static org.jivesoftware.spellchecker.SpellcheckerRes.ICON_SPELLING;

/**
 * This Class adds the SpellCheckButton to the ChatWindow and implements the
 * ActionListener to react on buttonclicks
 */
public class SpellcheckChatRoomDecorator {
    private JTextComponentSpellChecker _sc;
    private RolloverButton _spellingButton;
    private final ChatRoom _room;
    private final JComboBox<String> _languageSelection = new JComboBox<>();
    private Map<String, String> _languages;

    public SpellcheckChatRoomDecorator(ChatRoom room) {
        _room = room;
        final SpellcheckerPreference preference = (SpellcheckerPreference) SparkManager.getPreferenceManager().getPreference(SpellcheckerPreference.NAMESPACE);
        if (!preference.getPreferences().isSpellCheckerEnabled()) {
            return;
        }
        _sc = new JTextComponentSpellChecker(SpellcheckManager.getInstance().getSpellChecker());

        languagesToLocales();

        _languageSelection.addActionListener(e -> {
            String lang = _languages.get((String) _languageSelection.getSelectedItem());
            _sc.stopRealtimeMarkErrors();
            _sc = new JTextComponentSpellChecker(new SpellChecker(SpellcheckManager.getInstance().getDictionary(lang)));
            setIgnoreUppercase(preference.getPreferences().getIgnoreUppercase());
            _sc.startRealtimeMarkErrors(_room.getChatInputEditor());
        });


        _spellingButton = new RolloverButton(ICON_SPELLING);
        _spellingButton.setToolTipText(GraphicUtils.createToolTip(SpellcheckerRes.getString("button.check.spelling")));
        _spellingButton.addActionListener(e -> {
            if (_sc.spellCheck(_room.getChatInputEditor())) {
                JOptionPane.showMessageDialog(_room.getChatInputEditor(), SpellcheckerRes.getString("dialog.no.mistakes"));
                _room.getChatInputEditor().requestFocusInWindow();
            }
        });
        _room.getEditorBar().add(_spellingButton);
        if (preference.getPreferences().getLanguageSelectionInChatRoom()) {
            _room.getEditorBar().add(_languageSelection);
        }
        setIgnoreUppercase(preference.getPreferences().getIgnoreUppercase());

        if (preference.getPreferences().isAutoSpellCheckerEnabled()) {
            _sc.startRealtimeMarkErrors(_room.getChatInputEditor());
        }
    }

    private void setIgnoreUppercase(boolean ignoreUppercase) {
        _sc.getSpellChecker().setIgnoreUpperCaseWords(ignoreUppercase);
        _sc.getSpellChecker().setCaseSensitive(!ignoreUppercase);
    }

    private void languagesToLocales() {
        String spellLanguage = SpellcheckManager.getInstance().getSpellcheckerPreference().getPreferences().getSpellLanguage();
        _languages = new HashMap<>();
        Locale[] locales = Locale.getAvailableLocales();
        List<String> languages = SpellcheckManager.getInstance().getSupportedLanguages();
        for (String language : languages) {
            for (final Locale locale : locales) {
                if (locale.toString().equals(language)) {
                    String label = locale.getDisplayLanguage(Locale.getDefault());
                    if (!locale.getDisplayCountry(locale).isEmpty()) {
                        label = label + "-" + locale.getDisplayCountry(locale);
                    }
                    _languages.put(label, language);
                    _languageSelection.addItem(label);
                    if (language.equals(spellLanguage)) {
                        _languageSelection.setSelectedItem(label);
                    }
                }
            }
        }
    }

}
