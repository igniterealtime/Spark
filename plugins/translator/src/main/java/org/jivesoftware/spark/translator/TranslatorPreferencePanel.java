package org.jivesoftware.spark.translator;

import org.jivesoftware.spark.component.VerticalFlowLayout;

import javax.swing.*;
import java.awt.*;

public class TranslatorPreferencePanel extends JPanel {

    private final JCheckBox _enabledCheckbox;
    private final JTextField _url;
    private final JCheckBox _useCustomUrl;
    private final Insets INSETS = new Insets(5, 5, 5, 5);

    public TranslatorPreferencePanel() {

        this.setLayout(new BorderLayout());

        _enabledCheckbox = new JCheckBox(TranslatorResource.getString("translator.enabled"));
        _useCustomUrl = new JCheckBox(TranslatorResource.getString("translator.custom.url"));
        _url = new JTextField();
        updateGUI();
        add(makeGeneralSettingsPanel());
    }

    private JComponent makeGeneralSettingsPanel() {

        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createTitledBorder(TranslatorResource.getString("translator.settings")));
        int rowcount = 0;
        generalPanel.add(_enabledCheckbox,
            new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        rowcount++;
        generalPanel.add(_useCustomUrl,
            new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        rowcount++;
        generalPanel.add(new JLabel(TranslatorResource.getString("translator.url")),
            new GridBagConstraints(0, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        generalPanel.add(_url,
            new GridBagConstraints(1, rowcount, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, INSETS, 0, 0));
        rowcount++;
        JLabel placeHolder = new JLabel();
        generalPanel.add(placeHolder,
            new GridBagConstraints(1, rowcount, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, INSETS, 0, 0));

        JPanel panel = new JPanel(new VerticalFlowLayout());
        panel.add(generalPanel);

        return new JScrollPane(panel);
    }

    public void initializeValues() {
        TranslatorProperties props = TranslatorProperties.getInstance();
        _enabledCheckbox.setSelected(props.getEnabledTranslator());
        _useCustomUrl.setSelected(props.getUseCustomUrl());
        _url.setText(props.getUrl());
    }

    public void storeValues(){
        TranslatorProperties props = TranslatorProperties.getInstance();
        props.setEnabledTranslator(_enabledCheckbox.isSelected());
        props.setUseCustomUrl(_useCustomUrl.isSelected());
        props.setUrl(_url.getText());
        props.save();
    }

    private void updateGUI(){

        _enabledCheckbox.addActionListener( e -> {
            if (_enabledCheckbox.isSelected()) {
                _useCustomUrl.setEnabled(true);
                _url.setEnabled(_useCustomUrl.isSelected());
            } else {
                _url.setEnabled(false);
                _useCustomUrl.setEnabled(false);
            }
        });

        _useCustomUrl.addActionListener( e -> {
            if(_useCustomUrl.isSelected()){
                _url.setEnabled(true);
            } else {
                _url.setEnabled(false);
            }
        });

        if (!TranslatorProperties.getInstance().getEnabledTranslator()) {
            _url.setEnabled(false);
            _useCustomUrl.setEnabled(false);
        }

        if(!TranslatorProperties.getInstance().getUseCustomUrl()){
            _url.setEnabled(false);
        }
    }


}
