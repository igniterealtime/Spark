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
package org.jivesoftware.spellchecker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.jivesoftware.spark.util.SwingWorker;
import org.dts.spell.SpellChecker;
import org.dts.spell.swing.JTextComponentSpellChecker;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.util.GraphicUtils;

/**
 * This Class adds the SpellCheckButton to the ChatWindow and implements the
 * ActionListener to react on buttonclicks
 */
public class SpellcheckChatRoomDecorator implements ActionListener,
	ChatRoomClosingListener {
    private JTextComponentSpellChecker _sc;
    private RolloverButton _spellingButton;
    private ChatRoom _room;
    private JComboBox _languageSelection = new JComboBox();
    private Map<String, String> _languages;

    public SpellcheckChatRoomDecorator(ChatRoom room) {
	_room = room; 

	SwingWorker worker = new SwingWorker() {
	    public Object construct() {

		return true;
	    }

	    public void finished() {

		final SpellcheckerPreference preference = (SpellcheckerPreference) SparkManager
			.getPreferenceManager().getPreference(
				SpellcheckerPreference.NAMESPACE);
		if (preference.getPreferences().isSpellCheckerEnabled()) {
		    _sc = new JTextComponentSpellChecker(SpellcheckManager
			    .getInstance().getSpellChecker());

		    languagestoLocales();
		    
		    _languageSelection.addActionListener(new ActionListener() {
                        
		        
		        
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String lang = _languages.get((String)_languageSelection.getSelectedItem());
                            _sc.stopRealtimeMarkErrors();
                            _sc = new JTextComponentSpellChecker(new SpellChecker(SpellcheckManager.getInstance().getDictionary(lang)));
                           
                            if (preference.getPreferences().getIgnoreUppercase()) {
                                _sc.getSpellChecker().setIgnoreUpperCaseWords(true);
                                _sc.getSpellChecker().setCaseSensitive(false);
                            } else {
                                _sc.getSpellChecker().setIgnoreUpperCaseWords(false);
                                _sc.getSpellChecker().setCaseSensitive(true);
                            }
                            _sc.startRealtimeMarkErrors(_room.getChatInputEditor());
                        }
                    });
		    
		    
		    ClassLoader cl = getClass().getClassLoader();

		    ImageIcon spellingIcon = new ImageIcon(
			    cl.getResource("text_ok.png"));
		    _spellingButton = new RolloverButton(spellingIcon);
		    _spellingButton.setToolTipText(GraphicUtils
			    .createToolTip(SpellcheckerResource.getString("button.check.spelling")));
		    _spellingButton
			    .addActionListener(SpellcheckChatRoomDecorator.this);
		    _room.getEditorBar().add(_spellingButton);
                    if (preference.getPreferences().getLanguageSelectionInChatRoom())
                    {
                        _room.getEditorBar().add(_languageSelection);
                    }
		    if (preference.getPreferences().getIgnoreUppercase()) {
			_sc.getSpellChecker().setIgnoreUpperCaseWords(true);
			_sc.getSpellChecker().setCaseSensitive(false);
		    } else {
			_sc.getSpellChecker().setIgnoreUpperCaseWords(false);
			_sc.getSpellChecker().setCaseSensitive(true);
		    }

		    if (preference.getPreferences().isAutoSpellCheckerEnabled()) {
			_sc.startRealtimeMarkErrors(_room.getChatInputEditor());
		    }
		}
	    }

	};

	worker.start();

    }

    @Override
    public void actionPerformed(ActionEvent event) {
	if (_sc.spellCheck(_room.getChatInputEditor())) {
	    JOptionPane.showMessageDialog(_room.getChatInputEditor(),
	            SpellcheckerResource
	                .getString("dialog.no.mistakes"));
	    _room.getChatInputEditor().requestFocusInWindow();
	}

    }

    @Override
    public void closing() {

    }

    private void languagestoLocales()
    {
        String spellLanguage = SpellcheckManager.getInstance().getSpellcheckerPreference().getPreferences().getSpellLanguage();
        _languages = new HashMap<String, String>();
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> languages = SpellcheckManager.getInstance().getSupportedLanguages();
        for (int i = 0; i < languages.size(); i++) {
            for (final Locale locale : locales) {
                if (locale.toString().equals(languages.get(i))) {
                    String label = locale.getDisplayLanguage(Locale
                            .getDefault());
                    if (locale.getDisplayCountry(locale) != null
                            && locale.getDisplayCountry(locale).trim().length() > 0) {
                        label = label + "-"
                                + locale.getDisplayCountry(locale).trim();
                    }
                    _languages.put(label, languages.get(i));
                    _languageSelection.addItem(label);
                    if (languages.get(i).equals(spellLanguage))
                    {
                        _languageSelection.setSelectedItem(label);
                    }
                    
                }
            }
        }
    }

}
