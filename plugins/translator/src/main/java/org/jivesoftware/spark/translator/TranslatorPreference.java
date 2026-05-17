package org.jivesoftware.spark.translator;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import java.awt.*;

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
        ClassLoader cl = getClass().getClassLoader();
        return new ImageIcon(cl.getResource("translator.png"));
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
