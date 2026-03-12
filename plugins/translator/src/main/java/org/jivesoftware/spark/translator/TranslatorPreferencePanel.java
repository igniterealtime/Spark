package org.jivesoftware.spark.translator;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import space.dynomake.libretranslate.Language;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;

public class TranslatorPreferencePanel extends JPanel {

    private final JCheckBox _enabledCheckbox;
    private final JComboBox<Language> _myLanguage;
    private final JTextField _url;
    private final JTextField _apiKey;
    private final JCheckBox _useCustomUrl;
    private final Insets INSETS = new Insets(5, 5, 5, 5);

    public TranslatorPreferencePanel() {

        this.setLayout(new BorderLayout());

        _enabledCheckbox = new JCheckBox(TranslatorResource.getString("translator.enabled"));
        _myLanguage = new JComboBox<>(Language.values());
        _useCustomUrl = new JCheckBox(TranslatorResource.getString("translator.custom.url"));
        _url = new JTextField();
        _apiKey = new JTextField();
        updateGUI();
        add(makeGeneralSettingsPanel());
    }

    private JComponent makeGeneralSettingsPanel() {

        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createTitledBorder(TranslatorResource.getString("translator.settings")));
        int rowcount = 0;
        generalPanel.add(_enabledCheckbox,
            new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, WEST, BOTH, INSETS, 0, 0));
        rowcount++;
        generalPanel.add(new JLabel(TranslatorResource.getString("translator.myLanguage")),
            new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, WEST, BOTH, INSETS, 0, 0));
        generalPanel.add(_myLanguage,
            new GridBagConstraints(1, rowcount, 1, 1, 0.0, 0.0, WEST, BOTH, INSETS, 0, 0));
        rowcount++;
        generalPanel.add(_useCustomUrl,
            new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, WEST, BOTH, INSETS, 0, 0));
        rowcount++;
        generalPanel.add(new JLabel(TranslatorResource.getString("translator.url")),
            new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, WEST, BOTH, INSETS, 0, 0));
        generalPanel.add(_url,
            new GridBagConstraints(1, rowcount, 1, 1, 0.0, 0.0, WEST, BOTH, INSETS, 0, 0));
        rowcount++;
        generalPanel.add(new JLabel(TranslatorResource.getString("translator.apiKey")),
            new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, WEST, BOTH, INSETS, 0, 0));
        generalPanel.add(_apiKey,
            new GridBagConstraints(1, rowcount, 1, 1, 0.0, 0.0, WEST, BOTH, INSETS, 0, 0));
        rowcount++;
        JLabel placeHolder = new JLabel();
        generalPanel.add(placeHolder,
            new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, BOTH, INSETS, 0, 0));

        JPanel panel = new JPanel(new VerticalFlowLayout());
        panel.add(generalPanel);

        return new JScrollPane(panel);
    }

    public void initializeValues() {
        TranslatorProperties props = TranslatorProperties.getInstance();
        _enabledCheckbox.setSelected(props.getEnabledTranslator());
        Language myLanguage = Language.fromCode(props.getMyLanguage());
        _myLanguage.setSelectedItem(myLanguage);
        _useCustomUrl.setSelected(props.getUseCustomUrl());
        _url.setText(props.getUrl());
        _apiKey.setText(props.getApiKey());
    }

    public void storeValues(){
        TranslatorProperties props = TranslatorProperties.getInstance();
        props.setEnabledTranslator(_enabledCheckbox.isSelected());
        Language selectedMyLanguage = (Language) _myLanguage.getSelectedItem();
        props.setMyLanguage(selectedMyLanguage != null && selectedMyLanguage != Language.NONE ? selectedMyLanguage.getCode() : "");
        props.setUseCustomUrl(_useCustomUrl.isSelected());
        props.setUrl(_url.getText());
        props.setApiKey(_apiKey.getText());
        props.save();
    }

    private void updateGUI(){
        _enabledCheckbox.addActionListener( e -> {
            if (_enabledCheckbox.isSelected()) {
                _myLanguage.setEnabled(true);
                _useCustomUrl.setEnabled(true);
                _url.setEnabled(_useCustomUrl.isSelected());
                _apiKey.setEnabled(_useCustomUrl.isSelected());
            } else {
                _myLanguage.setEnabled(false);
                _url.setEnabled(false);
                _apiKey.setEnabled(false);
                _useCustomUrl.setEnabled(false);
            }
        });

        _useCustomUrl.addActionListener( e -> {
            if (_useCustomUrl.isSelected()){
                _url.setEnabled(true);
                _apiKey.setEnabled(true);
            } else {
                _url.setEnabled(false);
                _apiKey.setEnabled(false);
            }
        });

        if (!TranslatorProperties.getInstance().getEnabledTranslator()) {
            _myLanguage.setEnabled(false);
            _url.setEnabled(false);
            _apiKey.setEnabled(false);
            _useCustomUrl.setEnabled(false);
        }

        if(!TranslatorProperties.getInstance().getUseCustomUrl()){
            _url.setEnabled(false);
            _apiKey.setEnabled(false);
        }
    }
}
