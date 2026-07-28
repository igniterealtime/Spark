package org.jivesoftware.spark.translator;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.GraphicUtils;
import space.dynomake.libretranslate.ApiProviders;
import space.dynomake.libretranslate.Language;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class TranslatorPreferencePanel extends JPanel {
    private final JCheckBox _enabledCheckbox = new JCheckBox(TranslatorResource.getString("translator.enabled"));
    private final JComboBox<Language> _myLanguage = new JComboBox<>(Language.values());
    private final JComboBox<String> _url = new JComboBox<>();
    private final JTextField _apiKey = new JTextField();
    private final JCheckBox _useCustomUrl = new JCheckBox(TranslatorResource.getString("translator.custom.url"));
    private final JLabel labelMyLangs = new JLabel(TranslatorResource.getString("translator.myLanguage"));
    private final JLabel labelUrl = new JLabel(TranslatorResource.getString("translator.url"));
    private final JLabel labelApiKey = new JLabel(TranslatorResource.getString("translator.apiKey"));

    public TranslatorPreferencePanel() {
        setLayout(new BorderLayout());
        updateGUI();
        add(makeGeneralSettingsPanel());
    }

    private JComponent makeGeneralSettingsPanel() {
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createTitledBorder(TranslatorResource.getString("translator.settings")));
        Insets insets = new Insets(5, 5, 5, 5);
        int row = 0;
        generalPanel.add(_enabledCheckbox, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, BOTH, insets, 0, 0));
        row++;
        generalPanel.add(labelMyLangs, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, BOTH, insets, 0, 0));
        generalPanel.add(_myLanguage, new GridBagConstraints(1, row, 1, 1, 0, 0, WEST, BOTH, insets, 0, 0));
        row++;
        generalPanel.add(_useCustomUrl, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, BOTH, insets, 0, 0));
        row++;
        generalPanel.add(labelUrl, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, BOTH, insets, 0, 0));
        generalPanel.add(_url, new GridBagConstraints(1, row, 1, 1, 0, 0, WEST, BOTH, insets, 0, 0));
        row++;
        generalPanel.add(labelApiKey, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, BOTH, insets, 0, 0));
        generalPanel.add(_apiKey, new GridBagConstraints(1, row, 1, 1, 0, 0, WEST, BOTH, insets, 0, 0));
        row++;
        JLabel placeHolder = new JLabel();
        generalPanel.add(placeHolder, new GridBagConstraints(1, row, 1, 1, 1, 0, NORTHWEST, BOTH, insets, 0, 0));

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
        _url.setEditable(true);
        for (String apiUrl : ApiProviders.API_URLS) {
            GraphicUtils.addItemIfNotExists(_url, apiUrl);
        }
        if (!isBlank(props.getUrl())) {
            GraphicUtils.addItemIfNotExists(_url, props.getUrl());
            _url.setSelectedItem(props.getUrl());
        }
        _apiKey.setText(props.getApiKey());
    }

    public void storeValues() {
        TranslatorProperties props = TranslatorProperties.getInstance();
        props.setEnabledTranslator(_enabledCheckbox.isSelected());
        Language selectedMyLanguage = (Language) _myLanguage.getSelectedItem();
        props.setMyLanguage(selectedMyLanguage != null && selectedMyLanguage != Language.NONE ? selectedMyLanguage.getCode() : "");
        props.setUseCustomUrl(_useCustomUrl.isSelected());
        props.setUrl(_url.getSelectedItem() != null ? _url.getSelectedItem().toString() : null);
        props.setApiKey(_apiKey.getText());
        props.save();
    }

    private void updateGUI() {
        _enabledCheckbox.addActionListener(e -> {
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

        _useCustomUrl.addActionListener(e -> {
            _url.setEnabled(_useCustomUrl.isSelected());
            _apiKey.setEnabled(_useCustomUrl.isSelected());
        });

        if (!TranslatorProperties.getInstance().getEnabledTranslator()) {
            _myLanguage.setEnabled(false);
            _url.setEnabled(false);
            _apiKey.setEnabled(false);
            _useCustomUrl.setEnabled(false);
        }

        if (!TranslatorProperties.getInstance().getUseCustomUrl()) {
            _url.setEnabled(false);
            _apiKey.setEnabled(false);
        }
    }
}
