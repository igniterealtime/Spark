package org.jivesoftware.spark.ui;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.otrplug.OTRProperties;
import org.jivesoftware.spark.otrplug.OTRResources;
import org.jivesoftware.spark.preference.Preference;

public class OTRPreferences implements Preference {

    private OTRPrefPanel pref = new OTRPrefPanel();
    
    @Override
    public String getTitle() {
        return OTRResources.getString("otr.title");
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
        
        return OTRResources.getString("otr.list.entry");
    }

    @Override
    public String getNamespace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JComponent getGUI() {
        return pref;
    }

    @Override
    public void load() {
        // TODO Auto-generated method stub

    }

    @Override
    public void commit() {
        OTRProperties.getInstance().setIsOTREnabled(pref.isOTREnabled());
        OTRProperties.getInstance().save();

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
