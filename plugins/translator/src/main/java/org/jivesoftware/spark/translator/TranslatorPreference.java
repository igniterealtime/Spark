package org.jivesoftware.spark.translator;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import java.awt.*;

public class TranslatorPreference implements Preference {

    private TranslatorPreferencePanel _prefPanel;
    private final TranslatorProperties _props;

    public TranslatorPreference() {
        _props = TranslatorProperties.getInstance();

        try {
            if (EventQueue.isDispatchThread()) {
                _prefPanel = new TranslatorPreferencePanel();
            } else {
                EventQueue.invokeAndWait( () -> _prefPanel = new TranslatorPreferencePanel() );
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

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
       return TranslatorResource.getString("translator.title");
    }

    @Override
    public JComponent getGUI() {
        return _prefPanel;
    }

    @Override
    public void load() {
        _prefPanel.initializeValues();
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

    @Override
    public void shutdown() {

    }
}
