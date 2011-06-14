package org.jivesoftware.spark.otrplug.pref;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jivesoftware.spark.otrplug.util.OTRProperties;
import org.jivesoftware.spark.otrplug.util.OTRResources;
import org.jivesoftware.spark.preference.Preference;

/**
 * Implementation of Preference interface provided by Spark
 * 
 * @author Bergunde Holger
 */
public class OTRPreferences implements Preference {

    private OTRPrefPanel pref = new OTRPrefPanel();

    @Override
    public String getTitle() {
        return OTRResources.getString("otr.title");
    }

    @Override
    public Icon getIcon() {
        return OTRResources.getIcon("otr_pref.png");
    }

    @Override
    public String getTooltip() {
        return OTRResources.getString("otr.title");
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
        pref = new OTRPrefPanel();
        return pref.getGUI();
    }

    @Override
    public void load() {
        // TODO Auto-generated method stub

    }

    @Override
    public void commit() {
        OTRProperties.getInstance().setIsOTREnabled(pref.isOTREnabled());
        OTRProperties.getInstance().setOTRCloseOnChatClose(pref.isCloseOnChatClose());
        OTRProperties.getInstance().setOTRCloseOnDisc(pref.isCloseOnDisc());
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
