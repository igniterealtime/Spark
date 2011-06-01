package org.jivesoftware.spark.ui;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.preference.Preference;

public class OTRPreferences implements Preference {

    @Override
    public String getTitle() {
        return "Off-The-Record Plugin";
    }

    @Override
    public Icon getIcon() {
        return OTRManager.getInstance().getIcon("otr_pref.png");
    }

    @Override
    public String getTooltip() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getListName() {
        
        return "OTR";
    }

    @Override
    public String getNamespace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JComponent getGUI() {
        return new OTRPrefPanel();
    }

    @Override
    public void load() {
        // TODO Auto-generated method stub

    }

    @Override
    public void commit() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDataValid() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public String getErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

}
