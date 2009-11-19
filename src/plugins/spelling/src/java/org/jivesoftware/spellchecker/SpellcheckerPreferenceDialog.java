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
import javax.swing.JPanel;

import org.jivesoftware.spark.component.VerticalFlowLayout;

public class SpellcheckerPreferenceDialog extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -1836601903928057855L;

	private JCheckBox spellcheckingEnabled = new JCheckBox();
	private JCheckBox autospellcheckingEnabled = new JCheckBox();
	private JComboBox spellLanguages = new JComboBox();
	
	private JPanel spellPanel = new JPanel();

	private Locale[] locales;
	ArrayList<String> languages;
	
	public SpellcheckerPreferenceDialog(ArrayList<String> languages)
	{
		this.languages = languages;
		locales = Locale.getAvailableLocales();
		
		spellPanel.setLayout(new GridBagLayout());
		
		spellcheckingEnabled.setText(SpellcheckerResource.getString("preference.spellcheckingEnabled"));
		
		spellcheckingEnabled.addActionListener(this);
		
		autospellcheckingEnabled.setText(SpellcheckerResource.getString("preference.autoSpellcheckingEnabled"));

		for (int i = 0; i < languages.size(); i++) {
			for (final Locale locale : locales) {
				if (locale.toString().equals(languages.get(i))) {
					String label = locale.getDisplayLanguage(Locale.getDefault());
	                if (locale.getDisplayCountry(locale) != null &&
	                    locale.getDisplayCountry(locale).trim().length() > 0) {
	                    label = label + "-" + locale.getDisplayCountry(locale).trim();
	                }					
					spellLanguages.addItem(label);
				}
			}
		}
		
		spellPanel.add(spellcheckingEnabled, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		spellPanel.add(autospellcheckingEnabled, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		spellPanel.add(spellLanguages, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		setLayout(new VerticalFlowLayout());
		spellPanel.setBorder(BorderFactory.createTitledBorder(SpellcheckerResource.getString("title.spellchecker")));
        add(spellPanel);
	}
	
	public void updateUI(boolean enable)
	{
		if (enable)
		{
			autospellcheckingEnabled.setEnabled(true);
			spellLanguages.setEnabled(true);
		}
		else
		{
			autospellcheckingEnabled.setEnabled(false);
			spellLanguages.setEnabled(false);
		}		
	}
	
	public void setSpellCheckingEnabled(boolean enable)
	{
		spellcheckingEnabled.setSelected(enable);
		updateUI(enable);
	}
	
	public String getSelectedLanguage() {
		return languages.get(spellLanguages.getSelectedIndex());
	}
	
	public void setSelectedLanguage(String language) {
		spellLanguages.setSelectedIndex(languages.indexOf(language));
	}
	
	public void setAutoSpellCheckingEnabled(boolean enable)
	{
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
}
