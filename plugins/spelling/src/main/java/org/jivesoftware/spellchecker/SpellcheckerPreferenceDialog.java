/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2
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
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;

public class SpellcheckerPreferenceDialog extends JPanel {
    private final JCheckBox spellcheckingEnabled = new JCheckBox();
    private final JCheckBox autoSpellcheckingEnabled = new JCheckBox();
    private final JComboBox<String> spellLanguages = new JComboBox<>();
    private final JCheckBox ignoreCase = new JCheckBox();
    private final JCheckBox showLanguages = new JCheckBox();
    private final JPanel spellPanel = new JPanel();
    private final JLabel lLanguage = new JLabel();

    private final Locale[] locales = Locale.getAvailableLocales();
    private final ArrayList<String> languages;

    public SpellcheckerPreferenceDialog(ArrayList<String> languages) {
        this.languages = languages;
        spellPanel.setLayout(new GridBagLayout());

        ignoreCase.addActionListener(e -> setIgnoreUppercase(ignoreCase.isSelected()));
        spellcheckingEnabled.addActionListener(e -> updateUI(spellcheckingEnabled.isSelected()));
        for (String language : languages) {
            for (final Locale locale : locales) {
                if (locale.toString().equals(language)) {
                    String label = locale.getDisplayLanguage(Locale.getDefault());
                    if (!locale.getDisplayCountry(locale).isEmpty()) {
                        label = label + "-" + locale.getDisplayCountry(locale);
                    }
                    spellLanguages.addItem(label);
                }
            }
        }

        Insets insets = new Insets(5, 5, 5, 5);
        spellPanel.add(spellcheckingEnabled, new GridBagConstraints(0, 0, 2, 1, 1, 1, NORTHWEST, NONE, insets, 0, 0));
        spellPanel.add(autoSpellcheckingEnabled, new GridBagConstraints(0, 1, 2, 1, 1, 1, NORTHWEST, NONE, insets, 0, 0));
        spellPanel.add(lLanguage, new GridBagConstraints(0, 2, 1, 1, 1, 1, NORTHWEST, NONE, insets, 0, 0));
        spellPanel.add(spellLanguages, new GridBagConstraints(1, 2, 2, 1, 1, 1, NORTHWEST, NONE, insets, 0, 0));
        spellPanel.add(showLanguages, new GridBagConstraints(0, 3, 2, 1, 1, 1, NORTHWEST, NONE, insets, 0, 0));
        spellPanel.add(ignoreCase, new GridBagConstraints(0, 4, 2, 1, 1, 1, NORTHWEST, NONE, insets, 0, 0));

        ResourceUtils.resButton(spellcheckingEnabled, SpellcheckerRes.getString("preference.spellcheckingEnabled"));
        ResourceUtils.resButton(autoSpellcheckingEnabled, SpellcheckerRes.getString("preference.autoSpellcheckingEnabled"));
        ResourceUtils.resLabel(lLanguage, spellLanguages, SpellcheckerRes.getString("preference.language"));

        ResourceUtils.resButton(ignoreCase, SpellcheckerRes.getString("preference.ignore.uppercasedword"));
        ResourceUtils.resButton(showLanguages, SpellcheckerRes.getString("preference.show.langauage.in.chat.windows"));

        setLayout(new VerticalFlowLayout());
        spellPanel.setBorder(BorderFactory.createTitledBorder(SpellcheckerRes.getString("title.spellchecker")));
        add(spellPanel);
    }

    public void updateUI(boolean enable) {
        autoSpellcheckingEnabled.setEnabled(enable);
        spellLanguages.setEnabled(enable);
        ignoreCase.setEnabled(enable);
        showLanguages.setEnabled(enable);
    }

    public void setSpellCheckingEnabled(boolean enable) {
        spellcheckingEnabled.setSelected(enable);
        updateUI(enable);
    }

    public String getSelectedLanguage() {
        return spellLanguages.getSelectedIndex() > -1 ? languages.get(spellLanguages.getSelectedIndex()) : "";
    }

    public boolean getEnableLanguageSelection() {
        return showLanguages.isSelected();
    }

    public void setEnableLanguageSelection(boolean show) {
        showLanguages.setSelected(show);
    }

    public void setSelectedLanguage(String language) {
        spellLanguages.setSelectedIndex(languages.indexOf(language));
    }

    public void setAutoSpellCheckingEnabled(boolean enable) {
        autoSpellcheckingEnabled.setSelected(enable);
    }

    public boolean isSpellCheckingEnabled() {
        return spellcheckingEnabled.isSelected();
    }

    public boolean isAutoSpellCheckingEnabled() {
        return autoSpellcheckingEnabled.isSelected();
    }

    public boolean getIgnoreUppercase() {
        return ignoreCase.isSelected();
    }

    public void setIgnoreUppercase(boolean ignore) {
        ignoreCase.setSelected(ignore);
    }
}
