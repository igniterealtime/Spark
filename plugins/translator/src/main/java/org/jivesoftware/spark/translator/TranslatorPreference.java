package org.jivesoftware.spark.translator;

import org.jivesoftware.spark.preference.Preference;

import javax.swing.Icon;
import javax.swing.JComponent;

import static org.jivesoftware.spark.translator.TranslatorResource.ICON_TRANSLATOR;

public class TranslatorPreference implements Preference {
    private static final String NAMESPACE = "translator";
    private TranslatorPreferencePanel _prefPanel;
    private final TranslatorProperties _props = TranslatorProperties.getInstance();

    @Override
    public String getTitle() {
        return TranslatorResource.getString("translator.title");
    }

    @Override
    public Icon getIcon() {
        return ICON_TRANSLATOR;
    }

    @Override
    public String getTooltip() {
        return TranslatorResource.getString("translator.title");
    }

    @Override
    public String getListName() {
        return TranslatorResource.getString("translator.title");
    }

    @Override
    public String getNamespace() {
       return NAMESPACE;
    }

    @Override
    public JComponent getGUI() {
        _prefPanel = new TranslatorPreferencePanel();
        _prefPanel.initializeValues();
        return _prefPanel;
    }

    @Override
    public void load() {
    }

    @Override
    public void commit() {
        _prefPanel.storeValues();
    }

    @Override
    public boolean isDataValid() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public Object getData() {
        return _props;
    }
}
