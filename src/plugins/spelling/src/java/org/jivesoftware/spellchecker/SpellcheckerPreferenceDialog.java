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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

public class SpellcheckerPreferenceDialog extends JPanel implements
	ActionListener {
    private static final long serialVersionUID = -1836601903928057855L;

    private JCheckBox spellcheckingEnabled;
    private JCheckBox autospellcheckingEnabled;
    private JComboBox spellLanguages;
    private JCheckBox ignoreCase;
    private JCheckBox showLanguages; 
    private JPanel spellPanel;

    private Locale[] locales;
    ArrayList<String> languages;

    public SpellcheckerPreferenceDialog(ArrayList<String> languages) {
	this.languages = languages;
	locales = Locale.getAvailableLocales();
	spellPanel = new JPanel();
	spellcheckingEnabled = new JCheckBox();
	autospellcheckingEnabled = new JCheckBox();
	spellLanguages = new JComboBox();
	showLanguages = new JCheckBox();
	spellPanel.setLayout(new GridBagLayout());
	
	ignoreCase = new JCheckBox();
	ignoreCase.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
		setIgnoreUppercase(ignoreCase.isSelected());	
	    }
	});
	
	JLabel lLanguage = new JLabel();
	// spellcheckingEnabled.setText(SpellcheckerResource.getString("preference.spellcheckingEnabled"));

	spellcheckingEnabled.addActionListener(this);

	// autospellcheckingEnabled.setText(SpellcheckerResource.getString("preference.autoSpellcheckingEnabled"));

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
		    spellLanguages.addItem(label);
		}
	    }
	}

	spellPanel.add(spellcheckingEnabled, new GridBagConstraints(0, 0, 2, 1,	1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	spellPanel.add(autospellcheckingEnabled, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	spellPanel.add(lLanguage, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	spellPanel.add(spellLanguages, new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	spellPanel.add(showLanguages, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	spellPanel.add(ignoreCase, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

	// Setup MNEMORICS
	ResourceUtils.resButton(spellcheckingEnabled, SpellcheckerResource
		.getString("preference.spellcheckingEnabled"));
	ResourceUtils.resButton(autospellcheckingEnabled, SpellcheckerResource
		.getString("preference.autoSpellcheckingEnabled"));
	ResourceUtils.resLabel(lLanguage, spellLanguages,
		SpellcheckerResource.getString("preference.language"));
	
	ResourceUtils.resButton(ignoreCase, 
		SpellcheckerResource.getString("preference.ignore.uppercasedword"));
	ResourceUtils.resButton(showLanguages, SpellcheckerResource
                .getString("preference.show.langauage.in.chat.windows"));
	
	setLayout(new VerticalFlowLayout());
	spellPanel.setBorder(BorderFactory
		.createTitledBorder(SpellcheckerResource
			.getString("title.spellchecker")));
	add(spellPanel);
    }

    public void updateUI(boolean enable) {
	if (enable) {
	    autospellcheckingEnabled.setEnabled(true);
	    spellLanguages.setEnabled(true);
	    ignoreCase.setEnabled(true);
	    showLanguages.setEnabled(true);
	} else {
	    autospellcheckingEnabled.setEnabled(false);
	    spellLanguages.setEnabled(false);
	    ignoreCase.setEnabled(false);
	    showLanguages.setEnabled(false);
	}
    }

    public void setSpellCheckingEnabled(boolean enable) {
	spellcheckingEnabled.setSelected(enable);
	updateUI(enable);
    }

    public String getSelectedLanguage() {
	if (spellLanguages.getSelectedIndex() > -1)
	    return languages.get(spellLanguages.getSelectedIndex());
	else
	    return "";
    }
    
    public boolean getEnableLanuageSelection()
    {
        return showLanguages.isSelected();
    }
    
    public void setEnableLanuageSelection(boolean show)
    {
        showLanguages.setSelected(show);
    }
    

    public void setSelectedLanguage(String language) {
	spellLanguages.setSelectedIndex(languages.indexOf(language));
    }

    public void setAutoSpellCheckingEnabled(boolean enable) {
	autospellcheckingEnabled.setSelected(enable);
    }

    public boolean isSpellCheckingEnabled() {
	return spellcheckingEnabled.isSelected();
    }

    public boolean isAutoSpellCheckingEnabled() {
	return autospellcheckingEnabled.isSelected();
    }

    public void actionPerformed(ActionEvent event) {
	updateUI(spellcheckingEnabled.isSelected());
    }
    
    public boolean getIgnoreUppercase() {
	return ignoreCase.isSelected();
    }
    
    public void setIgnoreUppercase(boolean ignore) {
	ignoreCase.setSelected(ignore);
    }
}
